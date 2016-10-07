
package me.endureblackout.hsm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
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

        // They have a missile in their hand, so let's stop 'em from using it
        e.setCancelled(true);

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
            return;
        }

        // All checks passed, it's time to fire
        if (p.getGameMode() != GameMode.CREATIVE) {
            // If they're in a non-creative gamemode, actually consume the
            // firework
            consume(firework);
            p.getInventory().setItemInMainHand(firework);
        }

        // This ugly one-liner is to prevent both hitting yourself and shooting
        // through walls
        Vector loc = p.getTargetBlock((Set<Material>) null, 1).getType() == Material.AIR ? p.getLocation().getDirection().multiply(2.0) : p.getLocation().getDirection();

        final Arrow missile = p.getWorld().spawnArrow(p.getEyeLocation().add(loc), p.getEyeLocation().getDirection().multiply(2.0).normalize(), (float) 3, (float) 0);
        final Player finalTarget = closestP;

        missile.setGravity(false);

        new BukkitRunnable() {
            public void run() {
                if (missile.isOnGround()) {
                    missile.remove();
                }

                if (missile.isDead()) {
                    ParticleEffect.SMOKE_LARGE.display(0, 0, 0, 0.1f, 30, missile.getLocation(), 64.0f);
                    missile.getWorld().playSound(missile.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 1.0f);
                    cancel();
                    return;
                }

                Vector vec = (finalTarget.getEyeLocation().toVector()).subtract(missile.getLocation().toVector());
                missile.setVelocity(vec.normalize());
                ParticleEffect.CLOUD.display(0, 0, 0, 0.1f, 50, missile.getLocation(), 64.0f);
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

    private void consume(ItemStack is) {
        if (is == null) {
            return;
        }
        is.setAmount(is.getAmount() - 1);
        if (is.getAmount() < 1) {
            is.setType(Material.AIR);
        }
    }

}
