
package me.endureblackout.hsm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.endureblackout.hsm.utils.Items;
import me.endureblackout.hsm.utils.Msg;
import me.endureblackout.hsm.utils.ParticleEffect;

public class MissileHandler implements Listener {

    HSM        core;

    List<UUID> reload = new ArrayList<UUID>();

    public MissileHandler(HSM instance) {
        this.core = instance;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {

        final Player p = e.getPlayer();

        if (!e.getAction().toString().startsWith("RIGHT_CLICK")) {
            // They aren't right clicking
            return;
        }

        ItemStack firework = p.getInventory().getItemInMainHand();

        if (!Items.softMatch(firework, CommandHandler.missile)) {
            // It isn't the missile
            return;
        }

        if (reload.contains(p.getUniqueId())) {
            p.sendMessage(Msg.get("Reloading message"));
            e.setCancelled(true);
            return;
        }

        double closest = -1;
        Player closestP = null;

        List<Entity> nearby = p.getNearbyEntities(32, 32, 32);

        for (Entity ent : nearby) {
            if (!(ent instanceof Player)) {
                continue;
            }
            Player p1 = (Player) ent;
            if (!(p1 == p)) {
                double dist = p1.getLocation().distance(p.getLocation());
                if (closest == -1 || dist < closest) {
                    closest = dist;
                    closestP = p1;
                }
            }
        }

        if (closestP == null) {
            p.sendMessage(Msg.get("No target message"));
            e.setCancelled(true);
            return;
        }

        Vector loc = p.getTargetBlock((Set<Material>) null, 1).getType() == Material.AIR ? p.getLocation().getDirection().multiply(2.0) : p.getLocation().getDirection();

        final Arrow missile = p.getWorld().spawnArrow(p.getEyeLocation().add(loc), p.getEyeLocation().getDirection().multiply(2.0).normalize(), (float) 3, (float) 0);
        final Player finalTarget = closestP;
        missile.setGravity(false);

        new BukkitRunnable() {
            public void run() {
                if (!missile.isDead()) {
                    if (missile.isOnGround()) {
                        missile.setTicksLived(Integer.MAX_VALUE);
                        return;
                    }
                    Vector vec = (finalTarget.getEyeLocation().toVector()).subtract(missile.getLocation().toVector());
                    missile.setVelocity(vec.normalize());
                    ParticleEffect.CLOUD.display(0, 0, 0, 0.1f, 80, missile.getLocation(), 64.0f);
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(core, 7, 5);

        reload.add(p.getUniqueId());

        new BukkitRunnable() {
            public void run() {
                reload.remove(p.getUniqueId());
                p.sendMessage(Msg.get("Reload message"));
            }
        }.runTaskLater(core, core.getConfig().getInt("Reload time") * 20);

    }

}
