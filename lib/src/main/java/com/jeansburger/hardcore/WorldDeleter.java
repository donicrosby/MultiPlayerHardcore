package com.jeansburger.hardcore;
import com.jeansburger.hardcore.MultiPlayerHardcore;
import com.jeansburger.hardcore.interfaces.observers.*;
import com.jeansburger.hardcore.interfaces.runnable.RecreateWorldRunnable;

import org.bukkit.scheduler.BukkitTask;
import org.bukkit.event.entity.PlayerDeathEvent;
import java.util.logging.Logger;
import java.util.*;
import java.io.*;

public class WorldDeleter implements
  PlayerDeathObserver, NewWorldGeneratedObserver
{
  private MultiPlayerHardcore parent;
  private Logger logger;
  private BukkitTask deleteWorldTask;
  private RecreateWorldRunnable deleteWorldRunnable;

  private boolean deleteWorld = false;

  public WorldDeleter(MultiPlayerHardcore parent) {
    this.parent = parent;
    this.logger = parent.getLogger();
  }

  @Override
  public void onPlayerDeath(PlayerDeathEvent e){
    if (!deleteWorld){
      this.deleteWorldRunnable = new RecreateWorldRunnable(this, this.getWorldName());
      this.deleteWorldTask = this.deleteWorldRunnable.runTask(this.parent);
    }
    this.deleteWorld = true;
  }

  @Override
  public void onNewWorldCreate(){
    if (this.deleteWorldTask != null) {
      this.deleteWorldTask.cancel();
    }
    this.deleteWorld = false;
  }

  public Logger getLogger() {
    return parent.getLogger();
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

  public void notifyNewWorldGenerated(){
    this.parent.notifyNewWorldGenerated();
  }

}
