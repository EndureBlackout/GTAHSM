
package me.endureblackout.hsm;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class HSM extends JavaPlugin {

	public void onEnable() {

		File file = new File(getDataFolder(), "config.yml");
		if (!(file.exists())) {
			try {
				saveConfig();
				setupConfig(getConfig());
				getConfig().options().copyDefaults(true);
				saveConfig();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Bukkit.getServer().getPluginManager().registerEvents(new MissileHandler(this), this);
		getCommand("hsm").setExecutor(new CommandHandler(this));

	}

	private void setupConfig(FileConfiguration config) throws IOException {
		if (!new File(getDataFolder(), "RESET.FILE").exists()) {
			new File(getDataFolder(), "RESET.FILE").createNewFile();
			getConfig().set("Reload time", 15);
			getConfig().set("Missile Name", "&4Missile");
			getConfig().set("Reload message", "&6Missile reloaded! Hold the missile to fire!");
			getConfig().set("Missile received", "&7Missile received");
			getConfig().set("Jam chance", 0.2);
		}
	}
}
