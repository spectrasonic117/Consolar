package com.spectrasonic.Consolar.listeners;

import com.spectrasonic.Consolar.game.KothGame;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final KothGame game;

    public PlayerListener(KothGame game) {
        this.game = game;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!game.isRunning())
            return;

        // Solo procesar si el jugador cambió de bloque
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        game.checkPlayerLocation(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (game.isRunning()) {
            Player player = event.getPlayer();
            game.giveSpecialItem(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Remover del registro de zona si estaba dentro
        if (game.getZone().containsPlayer(player)) {
            game.getZone().removePlayer(player);
        }

        // Remover item especial
        if (game.isRunning()) {
            game.removeSpecialItem(player);
        }
    }
}
