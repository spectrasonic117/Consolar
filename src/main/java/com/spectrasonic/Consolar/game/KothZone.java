package com.spectrasonic.Consolar.game;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;

@Getter
public class KothZone {
    private final JavaPlugin plugin;
    private World world;
    private Location center;
    private double radius;
    private final Set<UUID> playersInZone = new HashSet<>();

    public KothZone(JavaPlugin plugin) {
        this.plugin = plugin;
        loadFromConfig();
    }

    public void loadFromConfig() {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("koth_zone");
        if (config == null) {
            plugin.getLogger().warning("No se encontró la configuración de la zona KOTH");
            return;
        }

        String worldName = config.getString("world", "world");
        world = plugin.getServer().getWorld(worldName);

        if (world == null) {
            plugin.getLogger().warning("Mundo no encontrado: " + worldName);
            return;
        }

        ConfigurationSection centerSection = config.getConfigurationSection("center");
        if (centerSection != null) {
            double x = centerSection.getDouble("x");
            double y = centerSection.getDouble("y");
            double z = centerSection.getDouble("z");
            center = new Location(world, x, y, z);
        } else {
            center = world.getSpawnLocation();
            plugin.getLogger().warning("Centro de KOTH no configurado, usando spawn del mundo");
        }

        radius = config.getDouble("radius", 10);
    }

    public boolean isInZone(Player player) {
        if (player.getWorld() != world)
            return false;

        Location playerLoc = player.getLocation();
        double distanceXZ = Math.sqrt(
                Math.pow(center.getX() - playerLoc.getX(), 2) +
                        Math.pow(center.getZ() - playerLoc.getZ(), 2));

        // Verificar si está dentro del radio en XZ y hasta 3 bloques arriba en Y
        return distanceXZ <= radius &&
                playerLoc.getY() >= center.getY() &&
                playerLoc.getY() <= center.getY() + 3;
    }

    public void addPlayer(Player player) {
        playersInZone.add(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        playersInZone.remove(player.getUniqueId());
    }

    public boolean containsPlayer(Player player) {
        return playersInZone.contains(player.getUniqueId());
    }

    public void clearPlayers() {
        playersInZone.clear();
    }

    public Location getParticleLocation(double angle, double height) {
        double x = center.getX() + radius * Math.cos(angle);
        double y = center.getY() + height;
        double z = center.getZ() + radius * Math.sin(angle);
        return new Location(world, x, y, z);
    }

    public Vector getSpiralParticleLocation(double angle, double height, double radiusFactor) {
        double adjustedRadius = radius * radiusFactor;
        double x = center.getX() + adjustedRadius * Math.cos(angle);
        double y = center.getY() + height;
        double z = center.getZ() + adjustedRadius * Math.sin(angle);
        return new Vector(x, y, z);
    }
}
