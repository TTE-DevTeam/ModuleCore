package de.dertoaster.modulecore;

import org.bukkit.configuration.ConfigurationSection;
import java.util.logging.Logger;

public abstract class AbstractModule implements IConfigurationHelper {

    private final String moduleName;
    private ConfigurationSection section = null;
    private Logger logger = null;

    public AbstractModule() {
        this.moduleName = this.getModuleName();
    }

    public ConfigurationSection getModuleConfig() {
        if (this.section == null) {
            throw new IllegalStateException("Configuration is not initialized yet!");
        }
        return this.section;
    }

    public Logger getLogger() {
        if (this.logger == null) {
            throw new IllegalStateException("Logger instance not present!");
        }
        return this.logger;
    }

    final void assignConfig(final ConfigurationSection section) {
        if (this.section == null) {
            this.section = section;
        } else {
            throw new IllegalStateException("Configuration was already assigned!");
        }
    }

    final void assignLogger(final Logger logger) {
        if (this.logger == null) {
            this.logger = logger;
        } else {
            throw new IllegalStateException("Logger was already assigned!");
        }
    }

    @Override
    public final ConfigurationSection getMainSection() {
        return this.getModuleConfig();
    }

    public abstract String getModuleName();

    // Modules get filtered out before onEnable if this returns false
    public boolean shouldEnable() {
        return this.getModuleConfig().getBoolean("Enabled", true);
    }

    // Modules that return false will not be loaded at all
    public abstract boolean shouldLoad();

    // Gets called during onLoad() and before the config is loaded. Use this to register serialization stuff
    public abstract void onInit(AbstractModularPlugin<?> modularPlugin);
    // Gets called after loading the config during onLoad()
    public abstract void onLoad(AbstractModularPlugin<?> modularPlugin);

    public abstract void onEnable(AbstractModularPlugin<?> modularPlugin);
    public abstract void onDisable(AbstractModularPlugin<?> modularPlugin);

}
