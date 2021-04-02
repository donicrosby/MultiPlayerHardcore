package com.jeansburger.hardcore;
import com.google.inject.Inject;
import com.google.inject.Injector;

import com.jeansburger.hardcore.config.ConfigManager;
import com.jeansburger.hardcore.utils.guice.MultiPlayerHardcoreBinder;
import com.jeansburger.hardcore.utils.worldmanager.HCWorldManager;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.SafeTTeleporter;
import com.onarandombox.MultiverseCore.destination.DestinationFactory;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;


import java.util.logging.Logger;

public class MultiPlayerHardcore extends JavaPlugin {
    @Inject private HCWorldManager worldManager;
    @Inject private ConfigManager config;
    private Logger logger;
    private MultiverseCore mvCore;

    @Override
    public void onEnable(){
        this.mvCore = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        this.logger = this.getLogger();
        MultiPlayerHardcoreBinder binder = new MultiPlayerHardcoreBinder(this);
        Injector injector = binder.createInjector();
        injector.injectMembers(this);

        this.getServer().getPluginManager().registerEvents(this.worldManager, this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this.worldManager);
    }

    public MVWorldManager getMVWorldManager(){
        return this.mvCore.getMVWorldManager();
    }

    public DestinationFactory getDestinationFactory(){
        return this.mvCore.getDestFactory();
    }

    public SafeTTeleporter getSafeTP(){
        return this.mvCore.getSafeTTeleporter();
    }

    public ConfigManager getConfigManger() {
        return this.config;
    }

}
