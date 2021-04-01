package com.jeansburger.hardcore.utils.guice;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jeansburger.hardcore.MultiPlayerHardcore;
import com.jeansburger.hardcore.utils.worldmanager.HCWorldManager;

import java.util.logging.Logger;

public class MultiPlayerHardcoreBinder extends AbstractModule {


    private final MultiPlayerHardcore plugin;

    // This is also dependency injection, but without any libraries/frameworks since we can't use those here yet.
    public MultiPlayerHardcoreBinder(MultiPlayerHardcore plugin) {
        this.plugin = plugin;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        // Here we tell Guice to use our plugin instance everytime we need it
        this.bind(MultiPlayerHardcore.class).toInstance(this.plugin);
    }
}
