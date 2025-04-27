package com.spectrasonic.Consolar.game;

import com.spectrasonic.Consolar.Utils.ItemBuilder;
import com.spectrasonic.Consolar.Utils.MessageUtils;
import com.spectrasonic.Consolar.Utils.PointsManager;
import com.spectrasonic.Consolar.Utils.SoundUtils;
import com.spectrasonic.Consolar.tasks.ParticleTask;
import com.spectrasonic.Consolar.tasks.PointsTask;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;

@Getter
public class KothGame {
    private final JavaPlugin plugin;
    private final KothZone zone;
    private final PointsManager pointsManager;
    private boolean isRunning = false;
    private BukkitTask pointsTask;
    private BukkitTask particleTask;
    private final Map<UUID, ItemStack> specialItems = new HashMap<>();
    private int currentRound = 1;

    public KothGame(JavaPlugin plugin) {
        this.plugin = plugin;
        this.zone = new KothZone(plugin);
        this.pointsManager = new PointsManager(plugin);
    }

    public void start(int round) {
        if (isRunning)
            return;

        isRunning = true;
        this.currentRound = round;

        pointsTask = new PointsTask(plugin, this).runTaskTimer(plugin, 40L, 40L);
        particleTask = new ParticleTask(plugin, zone).runTaskTimer(plugin, 0L, 10L);

        // for (Player player : Bukkit.getOnlinePlayers()) {
        //     giveSpecialItem(player);
        // }

        // MessageUtils.broadcastTitle("<aqua><bold>COMIENZA",
        // "<white>¡El juego ha comenzado!</white>", 1, 1, 1);
        MessageUtils.broadcastActionBar("<yellow>¡Captura la zona para ganar puntos!</yellow>");
        // SoundUtils.broadcastPlayerSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1.0f);
    }

    public void stop() {
        if (!isRunning)
            return;

        isRunning = false;
        this.currentRound = 1;

        if (pointsTask != null) {
            pointsTask.cancel();
            pointsTask = null;
        }

        if (particleTask != null) {
            particleTask.cancel();
            particleTask = null;
        }

        removeAllSpecialItems();

        zone.clearPlayers();

        // MessageUtils.broadcastTitle("<red><bold>GG",
        // "<white>¡El juego ha terminado!</white>", 1, 3, 1);
        // SoundUtils.broadcastPlayerSound(Sound.ENTITY_WITHER_DEATH, 0.5f, 1.0f);
    }

    public int getCurrentRound() {
        return currentRound;
    }
        
    public void giveSpecialItem(Player player) {
        if (player.getGameMode() != org.bukkit.GameMode.ADVENTURE) {
            return;
        }

        ItemStack specialItem = ItemBuilder.setMaterial("PAPER")
                .setName("<light_purple><bold>DILDO")
                .setLore("<gray>¡Usa este item para empujar a tus enemigos!",
                        "<gray>Y mantener el control de la zona.")
                .addEnchantment("knockback", 20)
                .setCustomModelData(1014)
                .setFlag("HIDE_ENCHANTS")
                .build();
                
        player.getInventory().setItemInMainHand(specialItem);
    }
    public void removeSpecialItem(Player player) {
        UUID uuid = player.getUniqueId();

        player.getInventory().forEach((item) -> {
            if (item != null &&
                    item.getType() == Material.PAPER &&
                    item.getItemMeta() != null &&
                    item.getItemMeta().hasCustomModelData() &&
                    item.getItemMeta().getCustomModelData() == 1014) {
                player.getInventory().remove(item);
            }
        });

        specialItems.remove(uuid);
    }

    public void removeAllSpecialItems() {
        Bukkit.getOnlinePlayers().forEach(this::removeSpecialItem);
        specialItems.clear();
    }

    public void reload() {
        boolean wasRunning = isRunning;

        if (wasRunning) {
            stop();
        }

        zone.loadFromConfig();

        if (wasRunning) {
            start(currentRound);
        }
    }

    public void checkPlayerLocation(Player player) {
        boolean inZone = zone.isInZone(player);
        boolean wasInZone = zone.containsPlayer(player);

        if (inZone && !wasInZone) {
            zone.addPlayer(player);
            // SoundUtils.playerSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f,
            // 1.0f);
            // MessageUtils.sendActionBar(player, "<green>¡Has entrado a la zona
            // KOTH!</green>");
        } else if (!inZone && wasInZone) {
            zone.removePlayer(player);
            SoundUtils.playerSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 0.5f, 1.0f);
            // MessageUtils.sendActionBar(player, "<red>¡Has salido de la zona
            // KOTH!</red>");
        }
    }
}
