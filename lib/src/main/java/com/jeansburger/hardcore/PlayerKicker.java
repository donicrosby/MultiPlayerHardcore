package com.jeansburger.hardcore;
import com.jeansburger.hardcore.MultiPlayerHardcore;
import com.jeansburger.hardcore.interfaces.observers.*;
import com.jeansburger.hardcore.interfaces.runnable.KickPlayerRunnable;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.logging.Logger;
import java.util.*;

class PlayerKicker implements
  PlayerDeathObserver,
  Listener
{

    private Logger logger;
    private boolean kickPlayers = false;

    public PlayerKicker(MultiPlayerHardcore parent){
      this.logger = parent.getLogger();
    }

    @Override
    public void onPlayerDeath(PlayerDeathEvent e){
      this.kickPlayers = true;
      this.handlePlayerDeath(e);
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
        Runnable kickPlayerRunnable = new Runnable()
        player.kickPlayer(kickReason.toString());
      }
    }
}
