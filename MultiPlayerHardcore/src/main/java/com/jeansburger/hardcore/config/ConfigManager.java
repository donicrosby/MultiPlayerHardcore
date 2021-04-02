package com.jeansburger.hardcore.config;

import com.google.inject.Inject;
import com.jeansburger.hardcore.MultiPlayerHardcore;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ConfigManager {
    private MultiPlayerHardcore plugin;
    private Logger pluginLogger;
    private String holdingWorld;

    @Inject
    public ConfigManager(MultiPlayerHardcore plugin) {
        this.plugin = plugin;
        this.pluginLogger = plugin.getLogger();
        this.holdingWorld = getDefaultWorld();
    }

    public List<String> getHardcoreWorlds(String world) {
        List<String> worlds =  new ArrayList<String>();
        worlds.add(world);
        worlds.add(world + getNetherSuffix());
        worlds.add(world + getEndSuffix());
        return worlds;
    }

    public String getNetherSuffix(){
        return "_nether";
    }

    public String getEndSuffix(){
        return "_the_end";
    }

    public String getHoldingWorld(){
        return holdingWorld;
    }

    public String getTeleportText(String player, String playerWhoDied){
        StringBuilder sb = new StringBuilder();
        if (player.equals(playerWhoDied)){
            sb.append("You have");
        } else {
            sb.append(playerWhoDied);
            sb.append("has");
        }
        sb.append(" died please make a note of it.");
        return sb.toString();
    }

    private String getDefaultWorld() {
        return plugin.getMVWorldManager().getSpawnWorld().getAlias();
    }

}
