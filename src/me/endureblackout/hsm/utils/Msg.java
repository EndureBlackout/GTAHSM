/**
 * 
 */
package me.endureblackout.hsm.utils;

import org.bukkit.ChatColor;

import me.endureblackout.hsm.HSM;

/**
 * @author Rayzr
 *
 */
public class Msg {

    private static HSM core;

    public static void load(HSM core) {
        // Store a reference to the plugin
        Msg.core = core;
    }

    public static void unload() {
        if (core == null) {
            System.out.println("Attempted to unload Msg before it was loaded! Ignoring...");
        }
        // Make sure to deallocate that ram
        core = null;
    }

    /**
     * Returns a message with the given key
     * 
     * @param key
     * @return
     */
    public static String get(String key) {
        if (core == null) {
            throw new IllegalStateException("The Msg utility class hasn't been initialized yet!");
        }
        String msg = core.getConfig().getString(key);
        if (msg == null) {
            return key;
        }
        return color(core.getConfig().getString("Message prefix") + msg);
    }

    /**
     * A general utility method for coloring text. Shorthand for
     * {@link ChatColor#translateAlternateColorCodes(char, String)}<br>
     * <br>
     * To be honest I should've put this in a class with a more relevant name,
     * but I didn't want to do that for just <i>one</i> method.
     * 
     * @param text the text to color
     * @return The text, formatted using '&' color codes
     */
    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
