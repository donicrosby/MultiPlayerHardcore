package com.jeansburger.hardcore.interfaces.runnable;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import java.util.*;

public class KickPlayerTask extends BukkitRunnable {
  private UUID id;
  private String kickReason;
  private boolean finished = false;

  public KickPlayerTask(UUID id, String kickReason){
    super();
    this.id = id;
    this.kickReason = kickReason;
  }

  @Override
  public void run(){
    Player playerToKick = Bukkit.getPlayer(id);
    if (playerToKick != null){
      if (playerToKick.isOnline()){
          playerToKick.kickPlayer(this.kickReason);
      }
    }
    cancel();
  }
 }
