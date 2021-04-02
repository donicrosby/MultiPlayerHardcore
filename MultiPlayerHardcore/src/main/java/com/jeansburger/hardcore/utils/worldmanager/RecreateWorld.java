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
  private final List<String> hardcoreWorlds;
  private final String holdingWorld;
  private final String netherSuffix;
  private final String theEndSuffix;
  private final Logger pluginLogger;

  public RecreateWorld(HCWorldManager mgr, List<String> hardcoreWorlds, String holdingWorld) {
    this.mgr = mgr;
    this.pluginLogger = this.mgr.getPlugin().getLogger();
    ConfigManager config = this.mgr.getPlugin().getConfigManger();
    this.netherSuffix = config.getNetherSuffix();
    this.theEndSuffix = config.getEndSuffix();
    this.hardcoreWorlds = hardcoreWorlds;
    this.holdingWorld = holdingWorld;
  }


  @Override
  public void run() {
    String seed = String.valueOf(new Random().nextLong());
    for (String hardcoreWorld: hardcoreWorlds){
      mgr.getPlugin().getMVWorldManager().deleteWorld(hardcoreWorld);
      if(hardcoreWorld.endsWith(netherSuffix)){
        createWorld(hardcoreWorld, seed, World.Environment.NETHER);
      } else if (hardcoreWorld.endsWith(theEndSuffix)) {
        createWorld(hardcoreWorld, seed, World.Environment.THE_END);
      } else {
        createWorld(hardcoreWorld, seed, World.Environment.NORMAL);
      }
      MultiverseWorld hardcore = mgr.getPlugin().getMVWorldManager().getMVWorld(hardcoreWorld);
      hardcore.setRespawnToWorld(holdingWorld);
      hardcore.setDifficulty(Difficulty.HARD);
      World newHardcore = hardcore.getCBWorld();
      if (!newHardcore.isHardcore()){
        newHardcore.setHardcore(true);
      }
    }
    this.mgr.notifyWorldCreate();
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
