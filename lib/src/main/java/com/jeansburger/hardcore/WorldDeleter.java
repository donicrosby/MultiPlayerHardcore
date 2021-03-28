package com.jeansburger.hardcore;
import com.jeansburger.hardcore.MultiPlayerHardcore;
import com.jeansburger.hardcore.interfaces.observers.*;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.entity.PlayerDeathEvent;
import java.util.logging.Logger;
import java.util.*;
import java.io.*;

public class WorldDeleter extends BukkitRunnable
                          implements PlayerDeathObserver,
                          NewWorldGeneratedObserver {
  private MultiPlayerHardcore parent;
  private Logger logger;
  private BukkitTask deleteWorldTask;

  private List<World> worlds = new ArrayList<World>();
  private List<WorldCreator> newWorlds = new ArrayList<WorldCreator>();
  private long newSeed;
  private boolean deleteWorld = false;

  public WorldDeleter(MultiPlayerHardcore parent) {
    this.parent = parent;
    this.logger = parent.getLogger();
  }

  @Override
  public void onPlayerDeath(PlayerDeathEvent e){
    if (!deleteWorld){
      if (this.deleteWorldTask != null) {
        this.deleteWorldTask.cancel();
      }
      this.deleteWorldTask = this.runTask(this.parent);
    }
    this.deleteWorld = true;
  }

  @Override
  public void onNewWorldCreate(){
    this.deleteWorld = false;
  }

  @Override
  public void run() {
    String worldName = this.getWorldName();
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
    this.parent.notifyNewWorldGenerated();
    this.cancel();

  }

  private void updateWorldsList(String worldName) {
    this.logger.info("Updateing world list for world: " + worldName);
    this.worlds.add(Bukkit.getWorld(worldName));
    this.worlds.add(Bukkit.getWorld(worldName + "_nether"));
    this.worlds.add(Bukkit.getWorld(worldName + "_the_end"));
  }

  private String getWorldFromServerProps(){
    String propsWorldName;

    try {
      BufferedReader is = new BufferedReader(new FileReader("server.properties"));
      Properties props = new Properties();
      props.load(is);
      is.close();
      propsWorldName = props.getProperty("level-name").strip();
    } catch (IOException er) {
      this.logger.warning(er.toString());
      propsWorldName = "";
    }

    return new String(propsWorldName);

  }

  private String getWorldName(){
    return this.getWorldFromServerProps();
  }

  private void unloadWorld(World world) {
    if(!world.equals(null)) {
        Bukkit.getServer().unloadWorld(world, true);
    } else {
      this.logger.warning("World " + world + " could not be unloaded!");
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
