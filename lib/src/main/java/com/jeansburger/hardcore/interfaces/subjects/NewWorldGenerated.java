package com.jeansburger.hardcore.interfaces.subjects;
import com.jeansburger.hardcore.interfaces.observers.NewWorldGeneratedObserver;
import java.util.*;


public class NewWorldGenerated {
  private List<NewWorldGeneratedObserver> registeredObservers = new ArrayList<NewWorldGeneratedObserver>();

  public void registerObserver(NewWorldGeneratedObserver observer){
    this.registeredObservers.add(observer);
  }

  public void removeObserver(NewWorldGeneratedObserver observer){
    this.registeredObservers.remove(observer);
  }

  public void notifyNewWorld(){
    for(NewWorldGeneratedObserver observer: this.registeredObservers){
      observer.onNewWorldCreate();
    }
  }

}
