package com.spectrasonic.Consolar.tasks;

import com.spectrasonic.Consolar.game.KothZone;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleTask extends BukkitRunnable {
    private final JavaPlugin plugin;
    private final KothZone zone;
    private final Random random = new Random();

    // Control de animación
    private double baseAngle = 0;
    private final List<ParticleColumn> particleColumns = new ArrayList<>();
    private int tickCounter = 0;

    // Configuración de partículas
    private static final int CIRCLE_PARTICLES = 64; // Número de partículas en el círculo
    private static final int MAX_COLUMNS = 64; // Número máximo de columnas activas
    private static final double COLUMN_HEIGHT = 3.0; // Altura máxima de las columnas

    public ParticleTask(JavaPlugin plugin, KothZone zone) {
        this.plugin = plugin;
        this.zone = zone;

        // Inicializar algunas columnas
        for (int i = 0; i < 16; i++) {
            spawnNewColumn();
        }
    }

    @Override
    public void run() {
        if (zone.getWorld() == null || zone.getCenter() == null)
            return;

        tickCounter++;

        // Dibujar círculos en la base y en la parte superior
        drawBoundaryCircles();

        // Actualizar y dibujar columnas de partículas
        updateParticleColumns();

        // Generar nuevas columnas periódicamente
        if (tickCounter % 15 == 0 && particleColumns.size() < MAX_COLUMNS) {
            spawnNewColumn();
        }

        // Rotar el ángulo base para el siguiente ciclo
        baseAngle += Math.PI / 64;
        if (baseAngle >= 2 * Math.PI) {
            baseAngle = 0;
        }
    }

    /**
     * Dibuja los círculos que delimitan la zona en la base y en la parte superior
     */
    private void drawBoundaryCircles() {
        // Círculo en la base
        drawCircle(0, Particle.HAPPY_VILLAGER, 0.5f);

        // Círculo en la parte superior
        drawCircle(COLUMN_HEIGHT, Particle.HAPPY_VILLAGER, 0.5f);

        // Círculo intermedio (opcional, para mejor visualización)
        drawCircle(COLUMN_HEIGHT / 2, Particle.HAPPY_VILLAGER, 0.3f);
    }

    /**
     * Dibuja un círculo de partículas a una altura específica
     */
    private void drawCircle(double height, Particle particle, float size) {
        double angleStep = 2 * Math.PI / CIRCLE_PARTICLES;

        for (int i = 0; i < CIRCLE_PARTICLES; i++) {
            double angle = baseAngle + (i * angleStep);
            Location particleLoc = zone.getParticleLocation(angle, height);

            zone.getWorld().spawnParticle(
                    particle,
                    particleLoc,
                    1,
                    0, 0, 0,
                    0,
                    null);
        }
    }

    /**
     * Actualiza y dibuja las columnas de partículas animadas
     */
    private void updateParticleColumns() {
        // Usar un iterador para poder eliminar elementos durante la iteración
        particleColumns.removeIf(column -> {
            // Actualizar la posición de la columna
            column.update();

            // Dibujar la columna
            drawParticleColumn(column);

            // Eliminar columnas que han completado su ciclo
            return column.isComplete();
        });
    }

    /**
     * Dibuja una columna de partículas
     */
    private void drawParticleColumn(ParticleColumn column) {
        Location base = zone.getParticleLocation(column.getAngle(), 0);

        // Número de partículas basado en la altura actual
        int particleCount = (int) (column.getCurrentHeight() * 10);

        for (int i = 0; i < particleCount; i++) {
            double height = (i / (double) particleCount) * column.getCurrentHeight();

            Location particleLoc = base.clone().add(0, height, 0);

            // Usar diferentes partículas según la altura para crear un efecto gradiente
            Particle particle;
            if (height < column.getCurrentHeight() * 0.3) {
                particle = Particle.SOUL_FIRE_FLAME;
            } else if (height < column.getCurrentHeight() * 0.7) {
                particle = Particle.FLAME;
            } else {
                particle = Particle.END_ROD;
            }

            zone.getWorld().spawnParticle(
                    particle,
                    particleLoc,
                    1,
                    0.05, 0.05, 0.05,
                    0.01);
        }
    }

    /**
     * Crea una nueva columna de partículas en una posición aleatoria del perímetro
     */
    private void spawnNewColumn() {
        double randomAngle = random.nextDouble() * 2 * Math.PI;
        particleColumns.add(new ParticleColumn(randomAngle));
    }

    /**
     * Clase interna para representar una columna de partículas animada
     */
    private class ParticleColumn {
        private final double angle;
        private double currentHeight = 0;
        private final double speed;
        private boolean ascending = true;

        public ParticleColumn(double angle) {
            this.angle = angle;
            // Velocidad aleatoria para variedad visual
            this.speed = 0.05 + (random.nextDouble() * 0.1);
        }

        public void update() {
            if (ascending) {
                currentHeight += speed;
                if (currentHeight >= COLUMN_HEIGHT) {
                    currentHeight = COLUMN_HEIGHT;
                    ascending = false;
                }
            } else {
                currentHeight -= speed / 2; // Descender más lento que ascender
            }
        }

        public boolean isComplete() {
            return !ascending && currentHeight <= 0;
        }

        public double getAngle() {
            return angle;
        }

        public double getCurrentHeight() {
            return currentHeight;
        }
    }
}
