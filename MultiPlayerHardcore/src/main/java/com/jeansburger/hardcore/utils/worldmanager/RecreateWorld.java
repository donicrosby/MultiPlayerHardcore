package com.jeansburger.hardcore.utils.worldmanager;

import com.jeansburger.hardcore.utils.worldmanager.worlds.HCWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.logging.Logger;

public class RecreateWorld extends BukkitRunnable {
  private final HCWorldManager mgr;
  private final String hardcoreWorld;
  private final String seed;
  private final World.Environment env;
  private final String holdingWorld;
  private final Logger pluginLogger;
  private final boolean deleteWorld;
  private List<String> aliases;

  public RecreateWorld(HCWorldManager mgr,
                       String hardcoreWorld,
                       String seed,
                       World.Environment env,
                       String holdingWorld,
                       List<String> aliases,
                       boolean deleteWorld) {
    this.mgr = mgr;
    this.pluginLogger = this.mgr.getPlugin().getLogger();
    this.hardcoreWorld = hardcoreWorld;
    this.seed = seed;
    this.env = env;
    this.holdingWorld = holdingWorld;
    this.deleteWorld = deleteWorld;
    this.aliases = aliases;
  }


  @Override
  public void run() {

    if(deleteWorld){
      // Delete world
      mgr.getPlugin().getMVWorldManager().deleteWorld(hardcoreWorld);
      createHCWorld();
    } else {
      MultiverseWorld hcWorld = mgr.getPlugin().getMVWorldManager().getMVWorld(hardcoreWorld);
      if (hcWorld == null){
        pluginLogger.info("Creating Hardcore world " + hardcoreWorld + " from config!");
        createHCWorld();
      }
    }
    finalizeTasks();
  }

  private void finalizeTasks(){
    this.mgr.createWorldTaskDone(this, deleteWorld);
    this.cancel();
  }

  private void createHCWorld(){
    // Create New World
    createMVWorld(hardcoreWorld, seed, env);

    // Set Difficulties and Respawn
    MultiverseWorld hardcore = mgr.getPlugin().getMVWorldManager().getMVWorld(hardcoreWorld);
    for (String alias : aliases){
      hardcore.setAlias(alias);
    }
    hardcore.setRespawnToWorld(holdingWorld);
    hardcore.setDifficulty(Difficulty.HARD);

    World newHardcore = hardcore.getCBWorld();
    if (!newHardcore.isHardcore()){
      newHardcore.setHardcore(true);
    }

  }

  private boolean createMVWorld(String name, String seed, World.Environment env){
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
