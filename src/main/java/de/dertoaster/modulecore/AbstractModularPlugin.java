package de.dertoaster.modulecore;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractModularPlugin<T extends AbstractModule> extends JavaPlugin implements IConfigurationHelper {

    protected final Map<String, T> modules = new ConcurrentHashMap<String, T>();
    protected Level defaultLogLevel;

    {
        this.defaultLogLevel = Level.INFO;
    }

    final void loadModules() {
        if (this.modules.isEmpty()) {
            for (T module : ServiceLoader.load(this.getModuleBaseClass(), getClass().getClassLoader())) {
                Logger moduleLogger = buildModuleLogger(module.getModuleName());
                module.assignLogger(moduleLogger);
                if (module.shouldLoad())
                    modules.put(module.getModuleName(), module);
            }
        }
    }

    protected Logger buildModuleLogger(String moduleName) {
        Logger moduleLogger = Logger.getLogger(this.getLogger().getName() + "." + moduleName);
        moduleLogger.setParent(this.getLogger());
        moduleLogger.setUseParentHandlers(true);

        return moduleLogger;
    }

    protected final Collection<T> modules() {
        if (this.modules.isEmpty()) {
            loadModules();
        }
        return this.modules.values();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        final AbstractModularPlugin<T> modularPlugin = this;
        this.modules().forEach(module -> module.onInit(modularPlugin));

        saveDefaultConfig();
        getConfig();

        applyLogLevel(this.defaultLogLevel);

        if (this.getConfig() != null) {
            for (Map.Entry<String, T> entry : this.modules.entrySet()) {
                T module = entry.getValue();
                ConfigurationSection moduleSection = this.getSection("Modules/" + entry.getKey());
                module.assignConfig(moduleSection);
                applyLogLevel(moduleSection, module.getLogger(), module.getDefaultLogLevel());
            }
        }
        this.modules().forEach(module -> module.onLoad(modularPlugin));
    }

    @Override
    public void onEnable() {
        super.onEnable();

        Set<T> toRemove = new HashSet<>();
        for (Map.Entry<String, T> entry : this.modules.entrySet()) {
            if (!entry.getValue().shouldEnable()) {
                toRemove.add(entry.getValue());
            }
        }
        toRemove.forEach(this.modules::remove);

        final AbstractModularPlugin<T> modularPlugin = this;
        this.modules().forEach(module -> module.onEnable(modularPlugin));
    }

    @Override
    public void onDisable() {
        super.onDisable();
        final AbstractModularPlugin<T> modularPlugin = this;
        this.modules().forEach(module -> module.onDisable(modularPlugin));
    }

    @Override
    public ConfigurationSection getMainSection() {
        return getConfig();
    }

    private void applyLogLevel(Level defaultLogLevel) {
        applyLogLevel(getMainSection(), this.getLogger(), defaultLogLevel);
    }

    private void applyLogLevel(final ConfigurationSection config, final Logger logger, final Level defaultLogLevel) {
        Level logLevel = defaultLogLevel;
        if (config.contains("LogLevel")) {
            String configLL = config.getString("LogLevel", defaultLogLevel.getName());
            try {
                logLevel = Level.parse(configLL);
            } catch(Exception ex) {
                logger.warning("Invalid loglevel <" + configLL + "> entered!");
                logLevel = defaultLogLevel;
            }
        }
        logger.setLevel(logLevel);
    }

    protected abstract Class<T> getModuleBaseClass();

}
