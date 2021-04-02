package com.jeansburger.hardcore.utils.worldmanager;

import com.google.inject.Inject;
import com.jeansburger.hardcore.MultiPlayerHardcore;
import com.jeansburger.hardcore.config.ConfigManager;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class HCWorldManager implements Listener {
  private final Logger pluginLogger;
  private final List<RecreateWorld> createWorldTasks = new ArrayList<>();
  private MultiPlayerHardcore plugin;
  private MVWorldManager mvWorldManager;
  private PlayerManager playerMgr;
  private List<String> hardcoreWorlds;
  private String netherSuffix;
  private String theEndSuffix;
  private String holdingWorldName;
  private boolean worldNeedsToBeRecreated = false;

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent e){
    if (!worldNeedsToBeRecreated) {
        World worldPlayerDied = e.getEntity().getWorld();

        for (MultiverseWorld hardcoreWorld : this.getHardcoreWorlds()){
          if ( hardcoreWorld != null){
            if (worldPlayerDied.equals(hardcoreWorld.getCBWorld())){
              worldNeedsToBeRecreated = true;
              regenWorld(e.getEntity().getPlayerListName());
              return; // We found a hardcore world managed by this
            }
          }
        }
    }
  }

  @Inject
  public HCWorldManager(MultiPlayerHardcore plugin){
    this.plugin = plugin;
    this.pluginLogger = plugin.getLogger();
    this.mvWorldManager = this.plugin.getMVWorldManager();
    this.playerMgr = new PlayerManager(this);
    reloadConfig();
    setHardcoreWorldsHardcore();
  }

  public List<MultiverseWorld> getHardcoreWorlds(){
    List<MultiverseWorld> mvHardcoreWorlds = new ArrayList<>();
    for (String world: this.hardcoreWorlds){
      mvHardcoreWorlds.add(getMVWorld(world, "Hardcore"));
    }
    return mvHardcoreWorlds;
  }

  public MultiverseWorld getHoldingWorld(){
    return getMVWorld(holdingWorldName, "Holding");
  }

  public void reloadConfig() {
    getConfig();
  }

  private void getConfig(){
    ConfigManager config = this.plugin.getConfigManger();
    this.hardcoreWorlds = config.getHardcoreWorlds("multiplayerhardcore");
    this.holdingWorldName = config.getHoldingWorld();
    this.theEndSuffix = config.getEndSuffix();
    this.netherSuffix = config.getNetherSuffix();
  }

  private MultiverseWorld getMVWorld(String worldName, String worldType){
    MultiverseWorld mvWorld = mvWorldManager.getMVWorld(worldName);
    if(mvWorld == null) {
      this.pluginLogger.warning("Could not find " + worldType + " World " + worldName + "!");
    }
    return mvWorld;
  }

  public MultiPlayerHardcore getPlugin() {
    return this.plugin;
  }

  private void setHardcoreWorldsHardcore() {
    for (MultiverseWorld hardcoreWorld: getHardcoreWorlds()){
      if (hardcoreWorld != null){
        if (!hardcoreWorld.getCBWorld().isHardcore()){
          this.pluginLogger.info("Hardcore world " + hardcoreWorld.getAlias() + " not set to hardcore, setting now....");
          hardcoreWorld.getCBWorld().setHardcore(true);
        }
      }
    }
  }

  public void regenWorld(String playerWhoDied) {
    this.playerMgr.teleportPlayersToHolding(playerWhoDied);
  }

  // Called by Player manager after all players have been tped
  public void recreateWorld() {
    createNewWorldTasks();
    runNewWorldTasks();
  }

  private void createNewWorldTasks(){
    String newSeed = getNewWorldSeed();
    for (String hardcoreWorld: this.hardcoreWorlds){
      if (hardcoreWorld.endsWith(this.netherSuffix)){
        this.createWorldTasks.add(
                new RecreateWorld(
                        this,
                        hardcoreWorld,
                        newSeed,
                        World.Environment.NETHER,
                        this.holdingWorldName)
        );
      } else if (hardcoreWorld.endsWith(this.theEndSuffix)){
        this.createWorldTasks.add(
                new RecreateWorld(
                        this,
                        hardcoreWorld,
                        newSeed,
                        World.Environment.THE_END,
                        this.holdingWorldName)
        );
      } else {
        this.createWorldTasks.add(
                new RecreateWorld(
                        this,
                        hardcoreWorld,
                        newSeed,
                        World.Environment.NORMAL,
                        this.holdingWorldName)
        );
      }
    }
  }

  private void runNewWorldTasks(){
    // start world recreate actions
    createWorldTasks.get(0).runTask(this.getPlugin());
  }

  private String getNewWorldSeed() {
    return String.valueOf(new Random().nextLong());
  }

  private void notifyWorldCreate() {
    this.worldNeedsToBeRecreated = false;
    this.playerMgr.notifyPlayersToNewHardcore();
  }

  public void createWorldTaskDone(RecreateWorld task){
    createWorldTasks.remove(task);
    if (createWorldTasks.isEmpty()){
      notifyWorldCreate();
    } else {
      runNewWorldTasks();
    }
  }

}
