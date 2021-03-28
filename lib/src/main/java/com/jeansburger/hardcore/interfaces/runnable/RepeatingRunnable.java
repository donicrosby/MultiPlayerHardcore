package com.jeansburger.hardcore.interfaces.runnable;
import org.bukkit.scheduler.BukkitRunnable;
import com.jeansburger.hardcore.MultiPlayerHardcore;

// This class is any small tasks that happen every tick to cleanup objects
public class RepeatingRunnable extends BukkitRunnable {
  private MultiPlayerHardcore parent;

  public RepeatingRunnable(MultiPlayerHardcore parent){
    this.parent = parent;
  }

  @Override
  public void run(){
    this.parent.removeOldTasks();
  }
}
