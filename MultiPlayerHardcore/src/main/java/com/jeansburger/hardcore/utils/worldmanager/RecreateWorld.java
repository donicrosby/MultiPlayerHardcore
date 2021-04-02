package com.jeansburger.hardcore.utils.worldmanager;

import com.jeansburger.hardcore.config.ConfigManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class RecreateWorld extends BukkitRunnable {
  private final HCWorldManager mgr;
  private final String hardcoreWorld;
  private final String seed;
  private final World.Environment env;
  private final String holdingWorld;
  private final Logger pluginLogger;

  public RecreateWorld(HCWorldManager mgr,
                       String hardcoreWorld,
                       String seed,
                       World.Environment env,
                       String holdingWorld) {
    this.mgr = mgr;
    this.pluginLogger = this.mgr.getPlugin().getLogger();
    this.hardcoreWorld = hardcoreWorld;
    this.seed = seed;
    this.env = env;
    this.holdingWorld = holdingWorld;
  }


  @Override
  public void run() {
    // Delete world
    mgr.getPlugin().getMVWorldManager().deleteWorld(hardcoreWorld);

    // Create New World
    createWorld(hardcoreWorld, seed, env);

    // Set Difficulties and Respawn
    MultiverseWorld hardcore = mgr.getPlugin().getMVWorldManager().getMVWorld(hardcoreWorld);
    hardcore.setRespawnToWorld(holdingWorld);
    hardcore.setDifficulty(Difficulty.HARD);

    World newHardcore = hardcore.getCBWorld();
    if (!newHardcore.isHardcore()){
      newHardcore.setHardcore(true);
    }
    this.mgr.createWorldTaskDone(this);
    this.cancel();
  }

  private boolean createWorld(String name, String seed, World.Environment env){
    return mgr.getPlugin().getMVWorldManager().addWorld(
            name, // World Name
            env, // Normal overworld
            seed, //Seed
            WorldType.NORMAL,
            true, //Create Structures
            null, //Don't change generator
            true //Use spawn adjust
    );
  }

}
