package com.jeansburger.hardcore.utils.worldmanager;

import com.google.inject.Inject;
import com.jeansburger.hardcore.MultiPlayerHardcore;
import com.jeansburger.hardcore.utils.worldmanager.players.TeleportPlayer;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class PlayerManager {
    private final Logger pluginLogger;
    private final HCWorldManager mgr;
    private final MultiverseWorld hardcoreWorld;
    private final MultiverseWorld holdingWorld;
    private final List<TeleportPlayer> tpList = new ArrayList<>();

    public PlayerManager(HCWorldManager mgr) {
        this.mgr = mgr;
        this.pluginLogger = mgr.getPlugin().getLogger();
        this.holdingWorld = mgr.getHoldingWorld();
        this.hardcoreWorld = mgr.getHardcoreWorld();
    }

    public void teleportPlayersToHolding(String deadPlayer) {
        this.pluginLogger.info("Someone died moving players....");
        hardcoreWorld.setRespawnToWorld(holdingWorld.getAlias());
        for(Player player: hardcoreWorld.getCBWorld().getPlayers()){
            TeleportPlayer tp = new TeleportPlayer(this, player, this.holdingWorld, deadPlayer);
            BukkitTask task = tp.runTask(mgr.getPlugin());
            tp.setTask(task);
            tpList.add(tp);
        }
    }

    public void teleportPlayersToHardcore() {
        this.pluginLogger.info("Moving players to new hardcore world...");
        holdingWorld.setRespawnToWorld(hardcoreWorld.getAlias());
        for(Player player: holdingWorld.getCBWorld().getPlayers()){
            TeleportPlayer tp = new TeleportPlayer(this, player, this.hardcoreWorld, null); //No player
            BukkitTask task = tp.runTask(mgr.getPlugin());
            tp.setTask(task);
            tpList.add(tp);
        }
    }

    public void removeTask(TeleportPlayer task) {
        tpList.remove(task);
    }

}