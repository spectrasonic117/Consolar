package com.spectrasonic.Consolar.listeners;

import com.spectrasonic.Consolar.game.KothGame;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

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
}
