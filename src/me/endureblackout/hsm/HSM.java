
package me.endureblackout.hsm;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.endureblackout.hsm.utils.Msg;

public class HSM extends JavaPlugin {

    @Override
    public void onEnable() {

        // Comment all the things!

        // Initialize Msg utility class
        Msg.load(this);

        // File file = new File(getDataFolder(), "config.yml");
        // if (!(file.exists())) {
        // try {
        // saveConfig();
        // setupConfig(getConfig());
        // getConfig().options().copyDefaults(true);
        // saveConfig();
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }

        // This should work better... sorry for messing with everything :3
        load();

        Bukkit.getServer().getPluginManager().registerEvents(new MissileHandler(this), this);
        getCommand("hsm").setExecutor(new CommandHandler(this));

    }

    @Override
    public void onDisable() {
        // Avoid problems with static references
        Msg.unload();
    }

    public void load() {
        reloadConfig();
        try {
            setupConfig();
        } catch (IOException e) {
            // Well, that's problematic. Time to leave.
            System.err.println("Failed to set up config file! Exiting...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
    }

    private void setupConfig() throws IOException {
        if (!new File(getDataFolder(), "RESET.FILE").exists()) {
            new File(getDataFolder(), "RESET.FILE").createNewFile();

            // Start by saving it
            saveConfig();

            // Set up all the defaults
            YamlConfiguration defaults = new YamlConfiguration();
            defaults.set("Reload time", 15);
            defaults.set("Jam chance", 0.2);
            defaults.set("Missile Name", "&4Missile");
            defaults.set("Message prefix", "&8\u00bb &6");
            defaults.set("Reload message", "Missile reloaded! Hold the missile to fire!");
            defaults.set("Reloading message", "You can't fire until you reload!");
            defaults.set("No target message", "There's nobody to shoot at!");
            defaults.set("Missile received", "&7Missile received");
            defaults.set("Missiles cleared", "&7All missiles removed");
            defaults.set("Usage message", "Usage: &c/hsm [get|remove|reload]");
            defaults.set("Config reloaded", "The config has been reloaded");

            // Set it to use those defaults
            getConfig().setDefaults(defaults);

            // Make it copy them
            getConfig().options().copyDefaults(true);

            // Save it again
            saveConfig();
        }
    }
}
