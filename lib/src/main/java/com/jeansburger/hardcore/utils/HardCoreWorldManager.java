package com.jeansburger.hardcore;
import com.jeansburger.hardcore.MultiPlayerHardcore;
import com.jeansburger.hardcore.interfaces.observers.*;

import ccom.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;
import org.bukkit.World;
import org.bukkit.WorldType;
import java.util.*;

public class HardCoreWorldManager implements
  PlayerDeathObserver, NewWorldGeneratedObserver
{
  MultiPlayerHardcore parent;
  MVWorldManager mgr;
  String worldName;
  String holdingWorldName;
  Boolean deletingWorld = false;

  public HardCoreWorldManager(MultiPlayerHardcore parent,
                              WorldManager mgr,
                              String worldName,
                              String holdingWorldName)
  {
    this.parent = parent;
    this.mgr = mgr;
    this.worldName = worldName;
    tis.holdingWorldName = holdingWorldName;
  }

  public void createHardcoreWorlds() {
    generateWorlds();
  }

  private void generateWorlds(){
    if (mgr.getWorld(this.worldName) == null) {
      mgr.addWorld(this.worldName, World.Environment.NORMAL, null,
        WorldType.NORMAL, true, null, true
      )
    }

    if (mgr.getWorld(this.holdingWorldName) == null) {
      mgr.addWorld(this.holdingWorldName, World.Environment.FLAT, null,
        WorldType.NORMAL, false, null, false
      )
    }
  }

  @Override
  public void onPlayerDeath(PlayerDeathEvent e){
    if (!deletingWorld){
      this.deleteWorldRunnable = new RecreateWorldRunnable(this, this.getWorldName());
      this.deleteWorldTask = this.deleteWorldRunnable.runTask(this.parent);
    }
    this.deletingWorld = true;
  }

  @Override
  public void onNewWorldCreate(){
    if (this.deleteWorldTask != null) {
      this.deleteWorldTask.cancel();
    }
    this.deletingWorld = false;
  }
