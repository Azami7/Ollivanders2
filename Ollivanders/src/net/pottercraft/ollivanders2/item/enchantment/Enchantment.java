package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class Enchantment
{
    ItemEnchantmentType enchantmentType = ItemEnchantmentType.GEMINIO;
    Ollivanders2 p;
    int magnitude = 1;
    Ollivanders2Common common;

    public Enchantment (@NotNull Ollivanders2 plugin, int mag)
    {
        p = plugin;
        magnitude = mag;

        common = new Ollivanders2Common(p);
    }

    /**
     * Handle item pickup events
     *
     * @param item the item picked up
     * @param player the player who picked up the item
     */
    abstract public void doItemPickup (@NotNull ItemStack item, @NotNull Player player);
}
