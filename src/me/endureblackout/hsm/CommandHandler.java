
package me.endureblackout.hsm;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.endureblackout.hsm.utils.Items;
import me.endureblackout.hsm.utils.Msg;

public class CommandHandler implements CommandExecutor {

    private HSM             core;

    public static String    missileName = "";
    public static ItemStack missile     = new ItemStack(Material.FIREWORK);
    private static ItemMeta missileMeta = missile.getItemMeta();

    public CommandHandler(HSM instance) {
        this.core = instance;

        missileName = ChatColor.translateAlternateColorCodes('&', core.getConfig().getString("Missile Name"));

        missileMeta.setDisplayName(missileName);
        missile.setItemMeta(missileMeta);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // The following line isn't needed, as this is the only command
        // registered to this class :P
        // if (cmd.getName().equalsIgnoreCase("hsm")) {

        if (!(sender instanceof Player)) {
            // Again, another reverse check to avoid indentation. Also you were
            // repeating your player check multiple times, as well as casting it
            // everytime you wanted to use it. This is what intermediate
            // variables are for :P
            sender.sendMessage(ChatColor.RED + "Only ingame players can use this command");
            return true;
        }

        Player p = (Player) sender;

        if (args.length < 1) {
            // I reversed your check to see if there _aren't_ enough args, I
            // find reverse checks cleaner as they don't cause 50 levels of
            // indentation. Just my personal opinion ;)
            // TODO: @endureblackout Show them a usage or something. I put this
            // one in but you can change it
            p.sendMessage(Msg.get("Usage message"));
            return true;
        }

        // By adding this variable here it shortens the if statements a little.
        // No more need to equalsIgnoreCase, you can just equals since it's
        // definitely in lower case now
        String arg = args[0].toLowerCase();

        // Just a note, I made the second if an elseif, that way you don't do
        // unnescessary checking. If you know the arg equals "get", you don't
        // even need to check if it equals "remove" lol
        if (arg.equals("get")) {
            p.getInventory().addItem(missile);
            // I just added a little utility class here, I think it helps
            p.sendMessage(Msg.get("Missile received"));
        } else if (arg.equals("remove")) {
            // I made this a while loop as the inventory could contain a _lot_
            // of missiles for all we know. Just a precaution ;)

            // Sadly this is the only way I could find to properly remove items,
            // otherwise it doesn't work for item stacks of missiles with more
            // than 1 item (a.k.a a stack of 5 missiles in a slot)
            ItemStack[] items = p.getInventory().getStorageContents();
            for (int i = 0; i < items.length; i++) {
                if (Items.isEmpty(items[i])) {
                    continue;
                }
                if (Items.softMatch(items[i], missile)) {
                    items[i] = null;
                }
            }
            p.getInventory().setStorageContents(items);

            p.sendMessage(Msg.get("Missiles cleared"));

        } else if (arg.equals("reload")) {
            // I figured you wouldn't mind me adding this ^w^
            core.load();
            p.sendMessage(Msg.get("Config reloaded"));
        }

        return true;
    }

}
