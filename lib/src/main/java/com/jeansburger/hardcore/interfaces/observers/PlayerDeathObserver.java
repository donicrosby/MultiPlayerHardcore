package com.jeansburger.hardcore.interfaces.observers;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public interface PlayerDeathObserver {
  public void onPlayerDeath(PlayerDeathEvent e);
}
