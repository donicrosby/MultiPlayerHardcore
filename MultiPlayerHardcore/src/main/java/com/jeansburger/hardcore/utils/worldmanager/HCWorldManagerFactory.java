package com.jeansburger.hardcore.utils.worldmanager;

import com.google.inject.Inject;
import com.jeansburger.hardcore.MultiPlayerHardcore;
import com.jeansburger.hardcore.utils.worldmanager.worlds.HCWorldManager;

public class HCWorldManagerFactory {
    private MultiPlayerHardcore plugin;

    @Inject
    public HCWorldManagerFactory(MultiPlayerHardcore plugin){
        this.plugin = plugin;
    }

    public HCWorldManager createHCWorldManager(String worldName){
        HCWorldManager worldManager = new HCWorldManager(plugin, worldName);
        this.plugin.getServer().getPluginManager().registerEvents(worldManager, plugin);
        return worldManager;
    }
}
