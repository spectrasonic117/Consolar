package com.spectrasonic.Consolar.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.spectrasonic.Consolar.Utils.MessageUtils;
import com.spectrasonic.Consolar.game.KothGame;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

@CommandAlias("consolar")
public class ConsolarCommand extends BaseCommand {
    private final JavaPlugin plugin;
    private final KothGame game;

    public ConsolarCommand(JavaPlugin plugin, KothGame game) {
        this.plugin = plugin;
        this.game = game;
    }

    @Subcommand("game")
    @CommandPermission("consolar.admin")
    @Description("Controla el juego KOTH")
    public class GameCommands extends BaseCommand {

        @Subcommand("start")
        @Description("Inicia el juego KOTH")
        public void onStart(CommandSender sender) {
            if (game.isRunning()) {
                MessageUtils.sendMessage(sender, "<red>El juego ya está en curso.</red>");
                return;
            }
            Player player = (Player) sender;
            player.performCommand("id false");
            player.performCommand("pvp true");
            player.performCommand("gamemode @a survival");
            game.start();
            MessageUtils.sendMessage(sender, "<green>¡Juego KOTH iniciado!</green>");
        }

        @Subcommand("stop")
        @Description("Detiene el juego KOTH")
        public void onStop(CommandSender sender) {
            if (!game.isRunning()) {
                MessageUtils.sendMessage(sender, "<red>El juego no está en curso.</red>");
                return;
            }
            Player player = (Player) sender;
            player.performCommand("id true");
            player.performCommand("pvp false");
            player.performCommand("gamemode @a adventure");

            // Clear all online players' inventories
            Bukkit.getOnlinePlayers().forEach(p -> p.getInventory().clear());

            game.stop();
            MessageUtils.sendMessage(sender, "<green>¡Juego KOTH detenido!</green>");
        }

        @Default
        @HelpCommand
        public void onHelp(CommandSender sender) {
            MessageUtils.sendMessage(sender, "<yellow>Comandos disponibles:</yellow>");
            MessageUtils.sendMessage(sender,
                    "<gray>- /consolar game start</gray> <white>- Inicia el juego KOTH</white>");
            MessageUtils.sendMessage(sender,
                    "<gray>- /consolar game stop</gray> <white>- Detiene el juego KOTH</white>");
        }
    }

    @Subcommand("reload")
    @CommandPermission("consolar.admin")
    @Description("Recarga la configuración del plugin")
    public void onReload(CommandSender sender) {
        plugin.reloadConfig();
        game.reload();
        MessageUtils.sendMessage(sender, "<green>¡Configuración recargada!</green>");
    }

    @Default
    @HelpCommand
    public void onHelp(CommandSender sender) {
        MessageUtils.sendMessage(sender, "<yellow>Comandos disponibles:</yellow>");
        MessageUtils.sendMessage(sender, "<gray>- /consolar game</gray> <white>- Controla el juego KOTH</white>");
        MessageUtils.sendMessage(sender, "<gray>- /consolar reload</gray> <white>- Recarga la configuración</white>");
    }
}
