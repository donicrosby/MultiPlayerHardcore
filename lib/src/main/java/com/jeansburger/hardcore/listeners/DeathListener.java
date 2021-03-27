package com.jeansburger.hardcore.listeners;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;
import com.jeansburger.hardcore.MultiPlayerHardcore;

public class DeathListener implements Listener {

    private MultiPlayerHardcore parent;

    public DeathListener(MultiPlayerHardcore parent){
      this.parent = parent;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        this.parent.notifyPlayerDeath(e);
    }
}
