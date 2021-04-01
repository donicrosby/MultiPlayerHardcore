package com.jeansburger.hardcore.utils.worldmanager;

import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.logging.Logger;

public class RecreateWorld extends BukkitRunnable {
  private final HCWorldManager mgr;
  private final String hardcoreWorld;
  private final Logger pluginLogger;

  public RecreateWorld(HCWorldManager mgr, String hardcoreWorld) {
    this.mgr = mgr;
    this.pluginLogger = this.mgr.getPlugin().getLogger();
    this.hardcoreWorld = hardcoreWorld;
  }


  @Override
  public void run() {
    if (mgr.getHardcoreWorld().equals(mgr.getPlugin().getMVWorldManager().getFirstSpawnWorld())) {
      this.pluginLogger.info("Setting First spawn");
      mgr.getPlugin().getMVWorldManager().setFirstSpawnWorld(mgr.getHoldingWorld().getAlias());
    }
    mgr.getPlugin().getMVWorldManager().deleteWorld(hardcoreWorld);
    long seed = new Random().nextLong();
    mgr.getPlugin().getMVWorldManager().addWorld(
            hardcoreWorld, // World Name
            World.Environment.NORMAL, // Normal overworld
            String.valueOf(seed), //Seed
            WorldType.NORMAL,
            true, //Create Structures
            null, //Don't change generator
            true //Use spawn adjust
    );
    World newHardcore = mgr.getPlugin().getMVWorldManager().getMVWorld(hardcoreWorld).getCBWorld();
    if (!newHardcore.isHardcore()){
      newHardcore.setHardcore(true);
    }
    this.mgr.notifyWorldCreate();
    this.cancel();
  }

}
