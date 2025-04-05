package com.spectrasonic.Consolar.tasks;

import com.spectrasonic.Consolar.Utils.MessageUtils;
import com.spectrasonic.Consolar.Utils.SoundUtils;
import com.spectrasonic.Consolar.game.KothGame;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
                
                // Mostrar actionbar brevemente
                MessageUtils.sendActionBar(player, "<green>+1 punto</green>");
                
                // Reproducir sonido de puntuación
                SoundUtils.playerSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
                
                // Programar la eliminación del actionbar después de un breve tiempo
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    MessageUtils.sendActionBar(player, "");
                }, 10L); // 10 ticks = 0.5 segundos
            }
        }
    }
}
