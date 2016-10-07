/**
 * 
 */
package me.endureblackout.hsm.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author Rayzr
 *
 */
public class Items {

    public static boolean softMatch(ItemStack is1, ItemStack is2) {
        if (is1 == is2) {
            return true;
        }
        if (is1 == null || is2 == null) {
            return false;
        }
        return is1.getType().equals(is2.getType()) && is1.getData().equals(is2.getData()) && is1.getItemMeta().equals(is2.getItemMeta());
    }

    /**
     * @param itemStack
     * @return
     */
    public static boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getType() == Material.AIR;
    }

}
