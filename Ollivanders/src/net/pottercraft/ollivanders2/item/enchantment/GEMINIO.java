package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Geminio Curse is used to curse an object into multiplying repeatedly when touched.
 *
 * @author Azami7
 * @since 2.6
 */
public class GEMINIO extends Enchantment
{
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     * @param mag the magnitude of this enchantment
     * @param args optional arguments for this enchantment
     * @param itemLore the optional lore for this enchantment
     */
    public GEMINIO (@NotNull Ollivanders2 plugin, int mag, @Nullable String args, @Nullable String itemLore)
    {
        super(plugin, mag, args, itemLore);
        enchantmentType = ItemEnchantmentType.GEMINIO;
    }

    /**
     * Handle item pickup events
     *
     * @param event the item pickup event
     */
    @Override
    public void doItemPickup (@NotNull EntityPickupItemEvent event)
    {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player))
            return;

        Item item = event.getItem();

        // double the item for each magnitude
        double copies = Math.pow(2, magnitude);

        for (int i = 0; i < copies; i++)
        {
            ItemStack copy = item.getItemStack().clone();
            ((Player)entity).getInventory().addItem(copy);
        }
    }

    /**
     * Handle item drop events
     *
     * @param event the item drop event
     */
    @Override
    public void doItemDrop (@NotNull PlayerDropItemEvent event) { }

    /**
     * Handle item despawn events
     *
     * @param event the item despawn event
     */
    @Override
    public void doItemDespawn (@NotNull ItemDespawnEvent event)
    {
        event.setCancelled(true);
    }

    /**
     * Handle item held events
     *
     * @param event the item drop event
     */
    public void doItemHeld (@NotNull PlayerItemHeldEvent event) { }
}
