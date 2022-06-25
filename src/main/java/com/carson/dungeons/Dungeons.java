package com.carson.dungeons;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class Dungeons extends JavaPlugin {

    // HashMap with the purpose of storing the player's uuid as well as creation progress status
    public static HashMap<UUID, Integer> playersDungeonCreationProcess = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("\nDungeons has been enabled.\n");

        // create config and create default values
        getConfig().options().copyDefaults();

        // register the events class
        Bukkit.getPluginManager().registerEvents(new Events(), this);

        // commands:
        getCommand("dungeons").setExecutor(new DungeonsCommand());
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("\nDungeons has been disabled.\n");
    }
}
