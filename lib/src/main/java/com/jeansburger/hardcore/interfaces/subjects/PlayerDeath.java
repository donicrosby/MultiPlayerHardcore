package com.jeansburger.hardcore.interfaces.subjects;
import com.jeansburger.hardcore.interfaces.observers.PlayerDeathObserver;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.*;

public class PlayerDeath {
  private List<PlayerDeathObserver> registeredObservers = new ArrayList<PlayerDeathObserver>();

  public void registerObserver(PlayerDeathObserver observer){
    this.registeredObservers.add(observer);
  }

  public void removeObserver(PlayerDeathObserver observer){
    this.registeredObservers.remove(observer);
  }

  public void notifyPlayerDeath(PlayerDeathEvent e){
    for(PlayerDeathObserver observer: this.registeredObservers){
      observer.onPlayerDeath(e);
    }
  }

}
