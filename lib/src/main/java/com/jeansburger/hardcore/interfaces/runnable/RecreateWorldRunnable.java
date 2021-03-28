package com.jeansburger.hardcore.interfaces.runnable;

import com.jeansburger.hardcore.WorldDeleter;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.logging.Logger;
import java.util.*;
import java.io.*;

public class RecreateWorldRunnable extends BukkitRunnable {

  private List<World> worlds = new ArrayList<World>();
  private List<WorldCreator> newWorlds = new ArrayList<WorldCreator>();
  private long newSeed;
  private String worldName;
  private WorldDeleter parent;
  private Logger logger;

  public RecreateWorldRunnable(WorldDeleter parent, String worldName){
    this.parent = parent;
    this.logger = parent.getLogger();
    this.worldName = worldName;
  }

  @Override
  public void run() {
    updateWorldsList(worldName);
    for (World oldWorld: this.worlds){
      WorldCreator wc = new WorldCreator(oldWorld.getName());
      wc.copy(oldWorld);
      this.newWorlds.add(wc);
      unloadWorld(oldWorld);
      deleteWorld(oldWorld.getWorldFolder());
    }
    this.newSeed = new Random().nextLong();
    for (WorldCreator wc: this.newWorlds){
      wc.seed(this.newSeed);
      wc.createWorld();
    }
    Bukkit.reloadData();
    this.worlds.clear();
    this.newWorlds.clear();
    this.parent.notifyNewWorldGenerated();
    this.cancel();
  }

  private void updateWorldsList(String worldName) {
    this.logger.info("Updateing world list for world: " + worldName);
    this.worlds.add(Bukkit.getWorld(worldName + "_nether"));
    this.worlds.add(Bukkit.getWorld(worldName + "_the_end"));
    this.worlds.add(Bukkit.getWorld(worldName));
  }

  private void unloadWorld(World world) {
    boolean unloadSucceeded;
    if(!world.equals(null)) {
         unloadSucceeded = Bukkit.getServer().unloadWorld(world, true);
    } else {
      this.logger.warning("World " + world.getName() + " could not be unloaded!");
      unloadSucceeded = false;
    }
    if (!unloadSucceeded){
      this.logger.warning("Could not unload " + world.getName());
    }
  }

  private boolean deleteWorld(File path) {
    if(path.exists()) {
      File files[] = path.listFiles();
      for(int i=0; i<files.length; i++) {
        if(files[i].isDirectory()) {
          deleteWorld(files[i]);
        } else {
          files[i].delete();
        }
      }
    }
    return(path.delete());
 }
}
