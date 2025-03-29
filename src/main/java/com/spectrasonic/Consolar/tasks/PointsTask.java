package com.spectrasonic.Consolar.tasks;

import com.spectrasonic.Consolar.Utils.MessageUtils;
import com.spectrasonic.Consolar.game.KothGame;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PointsTask extends BukkitRunnable {
    private final JavaPlugin plugin;
    private final KothGame game;

    public PointsTask(JavaPlugin plugin, KothGame game) {
        this.plugin = plugin;
        this.game = game;
    }

    @Override
    public void run() {
        if (!game.isRunning()) {
            this.cancel();
            return;
        }

        // Otorgar puntos a jugadores en la zona
        for (UUID uuid : game.getZone().getPlayersInZone()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                game.getPointsManager().addPoints(player, 1);
                MessageUtils.sendActionBar(player, "<green>+1 punto</green>");
            }
        }
    }
}
