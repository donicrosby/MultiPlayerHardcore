package com.jeansburger.hardcore.utils.worldmanager.players;

import com.jeansburger.hardcore.utils.worldmanager.PlayerManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;


public class TeleportPlayer extends BukkitRunnable {
    private final Player player;
    private final MultiverseWorld world;
    private final String playerWhoDied;
    private final PlayerManager mgr;
    private BukkitTask task;

    public TeleportPlayer(@NotNull PlayerManager mgr, @NotNull  Player player, @NotNull  MultiverseWorld world, @Nullable String playerWhoDied) {
        this.mgr = mgr;
        this.player = player;
        this.world = world;
        this.playerWhoDied = playerWhoDied;
    }

    public void setTask(BukkitTask task){
        this.task = task;
    }

    @Override
    public void run() {
        if (player.isOnline()) {
            Location holding = world.getSpawnLocation();
            player.teleport(holding, PlayerTeleportEvent.TeleportCause.PLUGIN);
            if(playerWhoDied != null) {
                StringBuilder sb = new StringBuilder();
                if (player.getDisplayName().equals(playerWhoDied)){
                    sb.append("You have");
                } else {
                    sb.append(Bukkit.getPlayer(playerWhoDied).getDisplayName());
                    sb.append("has");
                }
                sb.append(" died please make a note of it. Enjoy the new world!");
                player.sendMessage(sb.toString());
            } else {
                player.sendMessage("Here is the new hardcore world, try not to die!");
            }
        }
        this.cancel();
        this.mgr.removeTask(this);
    }
}
