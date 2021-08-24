package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FLAGRANTE extends Enchantment
{
    public FLAGRANTE (@NotNull Ollivanders2 plugin, int mag)
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
    public void doItemPickup (@NotNull ItemStack item, @NotNull Player player) { }
}
