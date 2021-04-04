package com.jeansburger.hardcore.config;

import com.google.inject.Inject;
import com.jeansburger.hardcore.MultiPlayerHardcore;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class ConfigManager {
    static final String WORLDS_CONFIG_KEY = "words";
    static final String WORLD_PLAYER_CONFIG_KEY = "players";
    static final String NETHER_DEFAULT_SUFFIX = "_nether";
    static final String THE_END_DEFAULT_SUFFIX = "_the_end";
    static final String DEFAULT_DEATH_MESSAGE = "{<player> has:You have} died please make a note of it.";
    static final String DEFAULT_WORLD_CREATE_MESSAGE = "New hardcore world has been created!";
    static final boolean CREATE_WORLD_ON_START_DEFAULT = true;

    private MultiPlayerHardcore plugin;
    private FileConfiguration config;
    private Logger pluginLogger;

    @Inject
    public ConfigManager(MultiPlayerHardcore plugin) {
        this.plugin = plugin;
        this.pluginLogger = plugin.getLogger();
        this.config = this.plugin.getConfig();
        initConfig();
    }

    public List<String> getHardcoreWorlds(String world) {
        List<String> worlds =  new ArrayList<String>();
        worlds.add(world);
        worlds.add(world + getNetherSuffix(world));
        worlds.add(world + getEndSuffix(world));
        return worlds;
    }

    public String getNetherSuffix(String world) {
        return getWorldConfig(world).getString("netherSuffix");
    }

    public String getEndSuffix(String world){
        return getWorldConfig(world).getString("theEndSuffix");
    }

    public String getHoldingWorld(String world){
        return getWorldConfig(world).getString("holdingWorld");
    }

    public List<String> getHardcoreWorlds(){
        return this.config.getStringList("worlds");
    }

    public boolean getCreateWorlds(String world){
        return getWorldConfig(world).getBoolean("createOnStart");
    }

    public List<String> getAliases(String world){
        return getWorldConfig(world).getStringList("aliases");
    }

    public String getTeleportText(String world, String player, String playerWhoDied) {
        String deathMessage = getWorldConfig(world).getString("onDeathMessage");
        return interpolateMessage(deathMessage, playerWhoDied, !player.equals(playerWhoDied));
    }

    public String getNewWorldText(String world, String player) {
        String newWorldText = getWorldConfig(world).getString("onNewWorldCreate");
        return interpolateMessage(newWorldText, player, true);
    }

    private String interpolateMessage(String message, String player, boolean useOption){
        message = message.replace("<player>", player);
        String[] stringToInterpolate = StringUtils.substringsBetween(message,"{", "}" );
        if (stringToInterpolate != null){
            String[] stringReplaceWith = new String[stringToInterpolate.length];
            for (int i=0; i < stringToInterpolate.length; i++){
                String[] options = stringToInterpolate[i].split(":");
                if(useOption){
                    stringReplaceWith[i] = options[0];
                } else {
                    stringReplaceWith[i] = options[1];
                }
            }
            message = StringUtils.replaceEach(message, stringToInterpolate, stringReplaceWith);
            message = message.replaceAll("\\{", "").replaceAll("}", "");
        }
        return message;
    }

    private String getDefaultWorld() {
        return plugin.getMVWorldManager().getSpawnWorld().getAlias();
    }

    private void setDefaults() {
        List<String> defaultWorlds = createDefaultWorlds();
        for (String world : defaultWorlds){
            createDefaultWorld(world);
        }
    }

    private List<String> createDefaultWorlds(){
        List<String> defaultWorlds = new ArrayList<>();
        defaultWorlds.add("multiplayerhardcore");
        config.addDefault(WORLDS_CONFIG_KEY, defaultWorlds);
        return defaultWorlds;
    }

    private void createDefaultWorld(String world){
        ConfigurationSection defaultWorld = getWorldConfig(world);
        defaultWorld.addDefault("players", new ArrayList<String>());
        defaultWorld.addDefault("holdingWorld", getDefaultWorld());
        defaultWorld.addDefault("createOnStart", CREATE_WORLD_ON_START_DEFAULT);
        defaultWorld.addDefault("aliases", new ArrayList<String>());
        defaultWorld.addDefault("onDeathMessage", DEFAULT_DEATH_MESSAGE);
        defaultWorld.addDefault("onNewWorldCreate", DEFAULT_WORLD_CREATE_MESSAGE);
        defaultWorld.addDefault("netherSuffix", NETHER_DEFAULT_SUFFIX);
        defaultWorld.addDefault("theEndSuffix", THE_END_DEFAULT_SUFFIX);
    }

    private void createConfig(){
        File configFile = new File(this.plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            setDefaults();
            config.options().copyDefaults(true);
            this.plugin.saveConfig();
            return;
        }

        boolean changed = false;
        Configuration defaults = config.getDefaults();
        for (String defaultKey : defaults.getKeys(true)){
            if (defaultKey.equals(WORLDS_CONFIG_KEY)){
                if(!config.contains(WORLDS_CONFIG_KEY)){
                    createDefaultWorlds();
                    changed = true;
                }
            } else {
                if(!config.contains(defaultKey)){
                    config.set(defaultKey, defaults.get(defaultKey));
                    changed = true;
                }
            }
        }

        if(changed){plugin.saveConfig();}
    }

    @SuppressWarnings("unchecked")
    private List<OfflinePlayer> getOfflinePlayerList(String world){
        List<?> configPlayerList = getWorldConfig(world).getList(WORLD_PLAYER_CONFIG_KEY);
        List<OfflinePlayer> playerList = new ArrayList<OfflinePlayer>((List<OfflinePlayer>) configPlayerList);
        if (playerList != null){
            return playerList;
        }
        return null;
    }

    public boolean isPlayerInSeenList(String world, UUID playerId){
        List<OfflinePlayer> playerList = getOfflinePlayerList(world);
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerId);
        Predicate<OfflinePlayer> uuidExists = listPlayer -> listPlayer.getUniqueId().equals(player.getUniqueId());
        if (playerList != null){
            return playerList.stream().anyMatch(uuidExists);
        }
        return false;
    }

    public void addPlayerToSeenList(String world, UUID playerId){
        ConfigurationSection worldConfig = getWorldConfig(world);
        List<OfflinePlayer> players = getOfflinePlayerList(world);
        if(players != null){
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerId);
            pluginLogger.info("Adding player: " + player.getName());
            players.add(player);
            worldConfig.set(WORLD_PLAYER_CONFIG_KEY, players);
            plugin.saveConfig();
        } else {
            pluginLogger.warning("Could not find player list in config");
        }
    }

    public void resetPlayerSeenList(String world){
        ConfigurationSection worldConfig = getWorldConfig(world);
        List<OfflinePlayer> playerList = new ArrayList<>();
        worldConfig.set(WORLD_PLAYER_CONFIG_KEY, playerList);
        plugin.saveConfig();
    }

    private void initConfig(){
        createConfig();
    }

    private ConfigurationSection getWorldConfig(String world){
        return config.getConfigurationSection(world);
    }

}
