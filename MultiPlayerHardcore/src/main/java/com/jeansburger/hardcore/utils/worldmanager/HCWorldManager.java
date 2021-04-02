package com.jeansburger.hardcore.utils.worldmanager;

import com.google.inject.Inject;
import com.jeansburger.hardcore.MultiPlayerHardcore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class HCWorldManager implements Listener {
  private final Logger pluginLogger;
  private MultiPlayerHardcore plugin;
  private MVWorldManager mvWorldManager;
  private PlayerManager playerMgr;
  private RecreateWorld createWorld;
  private List<String> hardcoreWorlds;
  private String holdingWorldName;
  private boolean worldNeedsToBeRecreated = false;
  private boolean canRecreateWorld = false;

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent e){
    if (!worldNeedsToBeRecreated) {
        World worldPlayerDied = e.getEntity().getWorld();

        for (MultiverseWorld hardcoreWorld : this.getHardcoreWorlds()){
          if ( hardcoreWorld != null){
            if (worldPlayerDied.equals(hardcoreWorld.getCBWorld())){
              worldNeedsToBeRecreated = true;
              regenWorld(e.getEntity().getPlayerListName());
              return;
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
    this.hardcoreWorlds = this.plugin.getConfigManger().getHardcoreWorlds("multiplayerhardcore");
    this.holdingWorldName = this.plugin.getConfigManger().getHoldingWorld();
    this.playerMgr = new PlayerManager(this);
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

  public void regenWorld(String playerWhoDied){
    this.playerMgr.teleportPlayersToHolding(playerWhoDied);
  }

  // Called by Player manager after all players have been tped
  public void recreateWorld(){
    createWorld = new RecreateWorld(this, this.hardcoreWorlds, this.holdingWorldName);
    createWorld.runTask(this.getPlugin());
  }

  public void notifyWorldCreate(){
    this.worldNeedsToBeRecreated = false;
    this.canRecreateWorld = false;
    this.playerMgr.notifyPlayersToNewHardcore();
  }

  private boolean isHardcoreWorld(String world){
    return hardcoreWorlds.contains(world);
  }
}
