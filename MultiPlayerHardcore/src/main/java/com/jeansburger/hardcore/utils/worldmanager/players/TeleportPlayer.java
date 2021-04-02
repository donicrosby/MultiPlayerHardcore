package com.jeansburger.hardcore.utils.worldmanager.players;

import com.jeansburger.hardcore.utils.worldmanager.PlayerManager;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.api.SafeTTeleporter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;


public class TeleportPlayer extends BukkitRunnable {
    private final Player player;
    private final MultiverseWorld world;
    private final String playerWhoDied;
    private final PlayerManager mgr;

    public TeleportPlayer(@NotNull PlayerManager mgr,
                          @NotNull  Player player,
                          @NotNull MultiverseWorld world,
                          @Nullable String playerWhoDied)
    {
        this.mgr = mgr;
        this.player = player;
        this.world = world;
        this.playerWhoDied = playerWhoDied;
    }

    @Override
    public void run() {
        if (player.isOnline()) {
            if (player.isDead()){
                player.spigot().respawn();
            } else {
                SafeTTeleporter tp = mgr.getPlugin().getSafeTP();
                MVDestination worldDest = mgr.getPlugin().getDestinationFactory().getDestination("w:"+world.getAlias());
                tp.safelyTeleport(Bukkit.getConsoleSender(), player, worldDest);
            }

            player.sendMessage(
                    mgr.getTeleportText(
                            player.getDisplayName(),
                            Bukkit.getPlayer(playerWhoDied).getDisplayName()
                    )
            );
        }
        this.cancel();
        this.mgr.removeTask(this);
    }
}
