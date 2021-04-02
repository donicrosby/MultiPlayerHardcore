package com.jeansburger.hardcore.utils.worldmanager;

import com.jeansburger.hardcore.MultiPlayerHardcore;
import com.jeansburger.hardcore.utils.worldmanager.players.TeleportPlayer;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PlayerManager {
    private final Logger pluginLogger;
    private final HCWorldManager mgr;
    private final List<TeleportPlayer> tpList = new ArrayList<>();
    private  List<MultiverseWorld> hardcoreWorlds;
    private  MultiverseWorld holdingWorld;
    private List<Player> playersOnHardcore = new ArrayList<>();

    public PlayerManager(HCWorldManager mgr) {
        this.mgr = mgr;
        this.pluginLogger = mgr.getPlugin().getLogger();
    }

    public void teleportPlayersToHolding(String deadPlayer) {
        getMVWorlds();
        this.pluginLogger.info("Someone died moving players....");
        for (MultiverseWorld hardcoreWorld: hardcoreWorlds){
            if (hardcoreWorld != null){
                this.playersOnHardcore.addAll(hardcoreWorld.getCBWorld().getPlayers());
            }
        }
        for(Player player: this.playersOnHardcore){
            TeleportPlayer tp = new TeleportPlayer(
                    this,
                    player,
                    holdingWorld,
                    deadPlayer
            );
            tpList.add(tp); //Add to list before running task
            tp.runTask(mgr.getPlugin());
        }
    }

    public void notifyPlayersToNewHardcore() {
        getMVWorlds();
        this.pluginLogger.info("Notifying players of new hardcore world...");
        for(Player player: holdingWorld.getCBWorld().getPlayers()){
            if (player.isOnline()){
                player.sendMessage("New Hardcore world has been created!");
            }
        }
        this.playersOnHardcore.clear();
    }

    public void removeTask(TeleportPlayer task) {
        tpList.remove(task);
        if (tpList.isEmpty()){
            this.mgr.recreateWorld();
        }
    }

    public String getTeleportText(String player, String playerWhoDied){
        return mgr.getPlugin().getConfigManger().getTeleportText(player, playerWhoDied);
    }

    public MVDestination getWorldTPLocation(String world){
        return getPlugin().getDestinationFactory().getDestination(world);
    }

    public MultiPlayerHardcore getPlugin(){
        return this.mgr.getPlugin();
    }

    private void getMVWorlds(){
        this.holdingWorld = mgr.getHoldingWorld();
        this.hardcoreWorlds = mgr.getHardcoreWorlds();
    }

}