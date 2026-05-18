package de.dertoaster.modulecore;

import org.bukkit.event.Listener;

public class ModuleListener<T extends AbstractModule> implements Listener {

    private final T module;

    public ModuleListener(final T module) {
        this.module = module;
    }

    protected T getModule() {
        return this.module;
    }

}
