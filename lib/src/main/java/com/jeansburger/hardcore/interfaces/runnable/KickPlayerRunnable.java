package com.jeansburger.hardcore.interfaces.runnable;
import org.bukkit.entity.Player;

public class KickPlayerRunnable implements Runnable {
  private Player player;
  private String kickReason;

  public KickPlayerRunnable(Player Player, String kickReason){
    this.player = player;
    this.kickReason = kickReason;
  }

  public void run(){
    this.player.kickPlayer(this.kickReason);
  }
 }
