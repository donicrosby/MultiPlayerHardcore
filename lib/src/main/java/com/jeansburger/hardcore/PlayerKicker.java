package com.jeansburger.hardcore;
import com.jeansburger.hardcore.MultiPlayerHardcore;
import com.jeansburger.hardcore.interfaces.observers.*;
import com.jeansburger.hardcore.interfaces.runnable.KickPlayerTask;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;
import java.util.*;

class PlayerKicker implements
  PlayerDeathObserver,
  NewWorldGeneratedObserver,
  Listener
{
    private MultiPlayerHardcore parent;
    private Logger logger;
    private boolean kickPlayers = false;
    private final List<KickPlayerTask> playerKickTasks = new ArrayList<KickPlayerTask>();

    public PlayerKicker(MultiPlayerHardcore parent){
      this.parent = parent;
      this.logger = this.parent.getLogger();

    }

    @Override
    public void onPlayerDeath(PlayerDeathEvent e){
      this.kickPlayers = true;
      this.handlePlayerDeath(e);
    }

    @Override
    public void onNewWorldCreate(){
      this.kickPlayers = false;
    }

    public void removeOldTasks() {
      for (KickPlayerTask task: playerKickTasks ){
        if(task.isCancelled()){
          playerKickTasks.remove(task);
        }
      }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
      if (kickPlayers) {
        e.getPlayer().kickPlayer("Someone died, creating a new world!");
      }
    }

    private void handlePlayerDeath(PlayerDeathEvent e){
      List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());
      String playerWhoDied = e.getEntity().getPlayerListName();
      for(Player player: playerList ){
        StringBuilder kickReason = new StringBuilder();
        if (player.getPlayerListName() == playerWhoDied){
          kickReason.append("You");
        } else {
          kickReason.append(playerWhoDied);
        }
        kickReason.append(" died, creating a new world...");
        KickPlayerTask kickTask = new KickPlayerTask(player.getUniqueId(), kickReason.toString());
        kickTask.runTask(this.parent);
      }
    }
}
