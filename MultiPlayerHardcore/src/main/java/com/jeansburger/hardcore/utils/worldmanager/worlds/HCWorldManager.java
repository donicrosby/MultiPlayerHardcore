package com.jeansburger.hardcore.utils.worldmanager.worlds;

import com.jeansburger.hardcore.MultiPlayerHardcore;
import com.jeansburger.hardcore.config.ConfigManager;
import com.jeansburger.hardcore.utils.worldmanager.PlayerManager;
import com.jeansburger.hardcore.utils.worldmanager.RecreateWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class HCWorldManager implements Listener {
  private final Logger pluginLogger;
  private final List<RecreateWorld> createWorldTasks = new ArrayList<>();
  private MultiPlayerHardcore plugin;
  private MVWorldManager mvWorldManager;
  private PlayerManager playerMgr;
  private List<String> hardcoreWorlds;
  private List<String> aliases;
  private String hardcoreWorldName;
  private String netherSuffix;
  private String theEndSuffix;
  private String holdingWorldName;
  private boolean worldNeedsToBeRecreated = false;

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent e){
    if (!worldNeedsToBeRecreated) {
        World worldPlayerDied = e.getEntity().getWorld();

        for (MultiverseWorld hardcoreWorld : this.getHardcoreWorlds()){
          if ( hardcoreWorld != null){
            if (worldPlayerDied.equals(hardcoreWorld.getCBWorld())){
              worldNeedsToBeRecreated = true;
              regenWorld(e.getEntity().getPlayerListName());
              return; // We found a hardcore world managed by this
            }
          }
        }
    }
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e){
    Player player = e.getPlayer();
    World joinedWorld = player.getWorld();
    checkPlayerResetList(joinedWorld, player);
  }

  @EventHandler
  public void onPlayerTP(PlayerTeleportEvent e){
    Player player = e.getPlayer();
    World joinedWorld = e.getTo().getWorld();
    checkPlayerResetList(joinedWorld, player);
  }

  private void checkPlayerResetList(World joinedWorld, Player player) {
    if(isHardcoreWorld(joinedWorld)){
      if(!plugin.getConfigManger().isPlayerInSeenList(hardcoreWorldName, player.getUniqueId())){
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0);
        player.getInventory().clear();
        player.updateInventory();
        plugin.getConfigManger().addPlayerToSeenList(hardcoreWorldName, player.getUniqueId());
      }
    }
  }

  public HCWorldManager(MultiPlayerHardcore plugin, String worldName){
    this.plugin = plugin;
    this.pluginLogger = plugin.getLogger();
    this.mvWorldManager = this.plugin.getMVWorldManager();
    this.hardcoreWorldName = worldName;
    this.playerMgr = new PlayerManager(this);
    reloadConfig();
    setHardcoreWorldsHardcore();
  }

  public String getHardcoreWorldName(){
    return this.hardcoreWorldName;
  }

  public List<MultiverseWorld> getHardcoreWorlds(){
    List<MultiverseWorld> mvHardcoreWorlds = new ArrayList<>();
    for (String world: this.hardcoreWorlds){
      mvHardcoreWorlds.add(getMVWorld(world, "Hardcore"));
    }
    return mvHardcoreWorlds;
  }

  public MultiverseWorld getHoldingWorld(){
    return getMVWorld(holdingWorldName, "Holding");
  }

  public void reloadConfig() {
    getConfig();
  }

  private void getConfig(){
    ConfigManager config = this.plugin.getConfigManger();
    this.hardcoreWorlds = config.getHardcoreWorlds(this.hardcoreWorldName);
    this.holdingWorldName = config.getHoldingWorld(this.hardcoreWorldName);
    this.theEndSuffix = config.getEndSuffix(this.hardcoreWorldName);
    this.netherSuffix = config.getNetherSuffix(this.hardcoreWorldName);
    this.aliases = config.getAliases(this.hardcoreWorldName);
    boolean createWorldsOnStart = config.getCreateWorlds(this.hardcoreWorldName);
    if(createWorldsOnStart){
      this.createWorlds(false);
    }
  }

  private MultiverseWorld getMVWorld(String worldName, String worldType){
    MultiverseWorld mvWorld = mvWorldManager.getMVWorld(worldName);
    if(mvWorld == null) {
      this.pluginLogger.warning("Could not find " + worldType + " World " + worldName + "!");
    }
    return mvWorld;
  }

  public MultiPlayerHardcore getPlugin() {
    return this.plugin;
  }

  private void setHardcoreWorldsHardcore() {
    for (MultiverseWorld hardcoreWorld: getHardcoreWorlds()){
      if (hardcoreWorld != null){
        if (!hardcoreWorld.getCBWorld().isHardcore()){
          this.pluginLogger.info("Hardcore world " + hardcoreWorld.getAlias() + " not set to hardcore, setting now....");
          hardcoreWorld.getCBWorld().setHardcore(true);
        }
      }
    }
  }

  public void regenWorld(String playerWhoDied) {
    this.playerMgr.teleportPlayersToHolding(playerWhoDied);
  }

  // Called by Player manager after all players have been tped
  public void recreateWorld() {
    createWorlds(true);
  }

  public void createWorlds(boolean deleteWorlds){
    worldNeedsToBeRecreated = true;
    createNewWorldTasks(deleteWorlds);
    runNewWorldTasks();
  }

  private void createNewWorldTasks(boolean deleteWorld){
    String newSeed = getNewWorldSeed();
    for (String hardcoreWorld: this.hardcoreWorlds){
      if (hardcoreWorld.endsWith(this.netherSuffix)){
        this.createWorldTasks.add(
                new RecreateWorld(
                        this,
                        hardcoreWorld,
                        newSeed,
                        World.Environment.NETHER,
                        this.holdingWorldName,
                        new ArrayList<>(),
                        deleteWorld)
        );
      } else if (hardcoreWorld.endsWith(this.theEndSuffix)){
        this.createWorldTasks.add(
                new RecreateWorld(
                        this,
                        hardcoreWorld,
                        newSeed,
                        World.Environment.THE_END,
                        this.holdingWorldName,
                        new ArrayList<>(),
                        deleteWorld)
        );
      } else {
        this.createWorldTasks.add(
                new RecreateWorld(
                        this,
                        hardcoreWorld,
                        newSeed,
                        World.Environment.NORMAL,
                        this.holdingWorldName,
                        this.aliases,
                        deleteWorld)
        );
      }
    }
  }

  private void runNewWorldTasks(){
    // start world recreate actions
    createWorldTasks.get(0).runTask(this.getPlugin());
  }

  private String getNewWorldSeed() {
    return String.valueOf(new Random().nextLong());
  }

  private void notifyWorldCreate(boolean notifyPlayers) {
    this.worldNeedsToBeRecreated = false;
    plugin.getConfigManger().resetPlayerSeenList(hardcoreWorldName);
    if (notifyPlayers){
      this.playerMgr.notifyPlayersToNewHardcore();
    }
  }

  public void createWorldTaskDone(RecreateWorld task, boolean worldDeleted){
    createWorldTasks.remove(task);
    if (createWorldTasks.isEmpty()){
      notifyWorldCreate(worldDeleted);
    } else {
      runNewWorldTasks();
    }
  }

  private boolean isHardcoreWorld(World world){
    return getHardcoreWorlds().contains(mvWorldManager.getMVWorld(world));
  }

}
