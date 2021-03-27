package com.jeansburger.hardcore;
import com.jeansburger.hardcore.interfaces.subjects.*;
import com.jeansburger.hardcore.interfaces.observers.*;
import com.jeansburger.hardcore.listeners.*;
import com.jeansburger.hardcore.PlayerKicker;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.util.logging.Logger;

public class MultiPlayerHardcore extends JavaPlugin implements BukkitScheduler {
    private static final PlayerDeath playerDeathNotifier = new PlayerDeath();
    private static final NewWorldGenerated newWorldGeneratedNotifier = new NewWorldGenerated();

    private final Logger logger = this.getLogger();
    private PlayerKicker kicker;

    @Override
    public void onEnable(){
      this.logger.info("Initalizing MultiPlayerHardcore....");
      this.getServer().getPluginManager().registerEvents(new DeathListener(this), this);
      this.logger.info("Listening for player deaths....");
      this.kicker = new PlayerKicker(this);
      this.addPlayerDeathObserver(this.kicker);
    }

    @Override
    public void onDisable(){
      HandlerList.unregisterAll(new DeathListener(this));
      this.removePlayerDeathObserver(this.kicker);
    }

    public void addPlayerDeathObserver(PlayerDeathObserver obs){
      this.playerDeathNotifier.registerObserver(obs);
    }

    public void removePlayerDeathObserver(PlayerDeathObserver obs){
      this.playerDeathNotifier.removeObserver(obs);
    }

    public void notifyPlayerDeath(PlayerDeathEvent e) {
      this.playerDeathNotifier.notifyPlayerDeath(e);
    }

    public void addWorldGeneratedObserver(NewWorldGeneratedObserver obs){
      this.newWorldGeneratedNotifier.registerObserver(obs);
    }

    public void removeWorldGeneratedObserver(NewWorldGeneratedObserver obs){
      this.newWorldGeneratedNotifier.removeObserver(obs);
    }

    public void notifyNewWorldGenerated() {
      this.newWorldGeneratedNotifier.notifyNewWorld();
    }

    public void
}
