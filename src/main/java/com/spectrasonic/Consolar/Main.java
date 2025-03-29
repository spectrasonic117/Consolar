package com.spectrasonic.Consolar;

import co.aikar.commands.PaperCommandManager;
import com.spectrasonic.Consolar.Utils.MessageUtils;
import com.spectrasonic.Consolar.commands.ConsolarCommand;
import com.spectrasonic.Consolar.game.KothGame;
import com.spectrasonic.Consolar.listeners.PlayerListener;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private KothGame kothGame;
    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        kothGame = new KothGame(this);

        registerCommands();
        registerEvents();

        MessageUtils.sendStartupMessage(this);
    }

    @Override
    public void onDisable() {
        if (kothGame != null && kothGame.isRunning()) {
            kothGame.stop();
        }

        MessageUtils.sendShutdownMessage(this);
    }

    public void registerCommands() {
        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new ConsolarCommand(this, kothGame));
    }

    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerListener(kothGame), this);
    }
}
