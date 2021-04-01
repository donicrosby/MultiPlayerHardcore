package com.jeansburger.hardcore.utils.worldmanager;

import com.google.inject.Inject;
import com.jeansburger.hardcore.MultiPlayerHardcore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.logging.Logger;

public class HCWorldManager implements Listener {
  private final Logger pluginLogger;
  private MultiPlayerHardcore plugin;
  private MVWorldManager mvWorldManager;
  private PlayerManager playerMgr;
  private RecreateWorld createWorld;
  private String hardcoreWorldName = "multiplayerhardcore";
  private String holdingWorldName = "playerholding";
  private boolean worldNeedsToBeRecreated = false;

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent e){
    if (!worldNeedsToBeRecreated) {
        World worldPlayerDied = e.getEntity().getWorld();

        MultiverseWorld hardcoreWorld = this.getHardcoreWorld();
        if ( hardcoreWorld != null){
          if (worldPlayerDied.equals(hardcoreWorld.getCBWorld())){
            worldNeedsToBeRecreated = true;
            recreateWorld(e.getEntity().getPlayerListName());
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
    createHoldingWorld();
    if(getHardcoreWorld() != null){
      setHarcoreWorldHardcore();
    }
  }

  private void createHoldingWorld(){
    if(mvWorldManager.getMVWorld(holdingWorldName) == null){
      mvWorldManager.addWorld(
              holdingWorldName, //World Name
              World.Environment.NORMAL, // Overworld Env
              null, //Don't care about seed
              WorldType.FLAT,
              false, // Don't Gen structures
              null // Don't care about the generator
      );
    }
  }

  public MultiverseWorld getHoldingWorld(){
    createHoldingWorld();
    return mvWorldManager.getMVWorld(holdingWorldName);
  }

  public MultiverseWorld getHardcoreWorld(){
    MultiverseWorld hardcoreWorld = mvWorldManager.getMVWorld(hardcoreWorldName);
    if(hardcoreWorld == null){
      this.pluginLogger.warning("Could not find Hardcore World " + hardcoreWorldName + "!");
      return null;
    } else {
      return hardcoreWorld;
    }
  }

  public MultiPlayerHardcore getPlugin() {
    return this.plugin;
  }

  private void setHarcoreWorldHardcore() {
    World hardcoreWorld = getHardcoreWorld().getCBWorld();
    if (!hardcoreWorld.isHardcore()){
      this.pluginLogger.info("Hardcore world not set to hardcore, setting now....");
      hardcoreWorld.setHardcore(true);
    }
  }

  public void recreateWorld(String playerWhoDied){
    this.playerMgr.teleportPlayersToHolding(playerWhoDied);
    createWorld = new RecreateWorld(this, this.hardcoreWorldName);
    createWorld.runTask(this.getPlugin());
  }

  public void notifyWorldCreate(){
    this.worldNeedsToBeRecreated = false;
    this.playerMgr.teleportPlayersToHardcore();
  }
}
