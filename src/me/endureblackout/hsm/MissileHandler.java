
package me.endureblackout.hsm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.endureblackout.hsm.utils.ParticleEffect;

public class MissileHandler implements Listener {

	HSM			core;

	List<UUID>	reload	= new ArrayList<UUID>();

	public MissileHandler(HSM instance) {
		this.core = instance;
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		final Player p = e.getPlayer();

		if (e.getAction().toString().startsWith("RIGHT_CLICK") && p.getInventory().getItemInMainHand().equals(CommandHandler.missile)) {
			ItemStack firework = new ItemStack(p.getInventory().getItemInMainHand());
			ItemMeta fireworkMeta = firework.getItemMeta();

			if (fireworkMeta.getDisplayName().equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', core.getConfig().getString("Missile Name")))) {

				double closest = Double.MAX_VALUE;
				Player closestP = null;

				for (Player p1 : Bukkit.getOnlinePlayers()) {
					if (!(p1 == p)) {
						double dist = p1.getLocation().distance(p.getLocation());

						if (closest == Double.MAX_VALUE || dist < closest) {
							closest = dist;
							closestP = p1;
						}
					}
				}

				Vector vec = closestP.getLocation().subtract(p.getLocation()).toVector();
				Vector loc = p.getTargetBlock((Set<Material>) null, 1).getType() == Material.AIR ? p.getLocation().getDirection().multiply(2.0) : p.getLocation().getDirection();

				Arrow missile = p.getWorld().spawnArrow(p.getEyeLocation().add(loc), p.getLocation().getDirection().multiply(2.0).normalize(), (float) 3, (float) 0);
				Location arrowLoc = missile.getLocation();

				if (!reload.contains(p.getUniqueId())) {
					missile.setVelocity(vec.normalize());
					reload.add(p.getUniqueId());
					missile.setGravity(false);

					ParticleEffect.CLOUD.display(0, 0, 0, 2, 80, arrowLoc, 2.0);

					new BukkitRunnable() {

						public void run() {
							reload.remove(p.getUniqueId());
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', core.getConfig().getString("Reload message")));
						}
					}.runTaskLater(core, core.getConfig().getInt("Reload time") * 20);
				}
			}
		}
	}
}
