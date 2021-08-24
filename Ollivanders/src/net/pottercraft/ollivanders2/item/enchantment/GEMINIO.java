package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GEMINIO extends Enchantment
{
    public GEMINIO (@NotNull Ollivanders2 plugin, int mag)
    {
        super(plugin, mag);
        enchantmentType = ItemEnchantmentType.GEMINIO;
    }

    /**
     * Handle item pickup events
     *
     * @param item the item picked up
     * @param player the player who picked up the item
     */
    @Override
    public void doItemPickup (@NotNull ItemStack item, @NotNull Player player)
    {
        // double the item for each magnitude
        double copies = Math.pow(2, magnitude);

        for (int i = 0; i < copies; i++)
        {
            ItemStack copy = item.clone();
            player.getInventory().addItem(copy);
        }
    }
}
