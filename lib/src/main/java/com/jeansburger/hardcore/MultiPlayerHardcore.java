package com.jeansburger.hardcore;
import com.jeansburger.hardcore.interfaces.subjects.*;
import com.jeansburger.hardcore.interfaces.observers.*;
import com.jeansburger.hardcore.listeners.*;
import com.jeansburger.hardcore.utils.*;
import com.jeansburger.hardcore.interfaces.runnable.RepeatingRunnable;
import com.jeansburger.hardcore.PlayerKicker;
import com.jeansburger.hardcore.WorldDeleter;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.HandlerList;

import java.util.logging.Logger;

public class MultiPlayerHardcore extends JavaPlugin {
    private static final PlayerDeath playerDeathNotifier = new PlayerDeath();
    private static final NewWorldGenerated newWorldGeneratedNotifier = new NewWorldGenerated();

    private final Logger logger = this.getLogger();
    private MultiverseCore mvcore;
    private MultiverseNetherPortals mvnether;
    private HardCoreWorldManager hcWorldMgr;
    private PlayerKicker kicker;
    private WorldDeleter deleter;
    private RepeatingRunnable repeatingRunnable;
    private BukkitTask removeOldTasks;
    private final String hardcoreWorldName = "multiplayerhardcore";
    private final String holdingWorldName = "playerholding";

    @Override
    public void onEnable(){
      this.logger.info("Initalizing MultiPlayerHardcore....");
      creatHardCoreWorlds();
      setupMultiVerse();
      setupObservers();
      setupRecurringTasks();
      setupListeners();
    }

    @Override
    public void onDisable(){
      HandlerList.unregisterAll(new DeathListener(this));
      this.removePlayerDeathObserver(this.kicker);
      this.removePlayerDeathObserver(this.deleter);
      this.removeOldTasks.cancel();
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

    public void removeOldTasks(){
      this.kicker.removeOldTasks();
    }

    public String getHardcoreWorld(){
      return this.hardcoreWorld;
    }

    public String getHoldingWorld(){
      return this.holdingWorld;
    }

    public void setFirstSpawnWorld(String world){
      if (this.mvcore.getWorld(world) != null){
        this.mvcore.getMVConfig().setFirstSpawnWorld();
      }
    }

    private void setupObservers() {
      this.kicker = new PlayerKicker(this);
      this.deleter = new WorldDeleter(this);
      this.addPlayerDeathObserver(this.kicker);
      this.addWorldGeneratedObserver(this.kicker);
      this.addPlayerDeathObserver(this.deleter);
      this.addWorldGeneratedObserver(this.deleter);
    }

    private void setupRecurringTasks() {
      this.repeatingRunnable = new RepeatingRunnable(this);
      this.removeOldTasks = this.repeatingRunnable.runTaskTimer​(this, 1, 1);
    }

    private void setupListeners() {
      this.getServer().getPluginManager().registerEvents(new DeathListener(this), this);
      this.logger.info("Listening for player deaths....");
    }

    private void setupMultiVerse(){
      this.mvcore = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
      this.mvnether = (MultiverseNetherPortals) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-NetherPortals");
    }

    private void creatHardCoreWorlds(){
      this.hcWorldMgr = new HardCoreWorldManager(this.mvcore.getMVWorldManager(),
                                                 this.hardcoreWorldName,
                                                 this.holdingWorldName);
      this.hcWorldMgr.createHardcoreWorlds();
    }

}
