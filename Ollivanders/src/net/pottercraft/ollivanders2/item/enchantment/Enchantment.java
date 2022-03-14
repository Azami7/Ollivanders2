package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Item enchantment
 */
public abstract class Enchantment
{
    /**
     * The type of enchantment
     */
    ItemEnchantmentType enchantmentType = ItemEnchantmentType.GEMINIO;

    /**
     * A callback to the plugin
     */
    Ollivanders2 p;

    /**
     * The magnitude of this enchantment
     */
    int magnitude;

    /**
     * Optional lore to add to the item for this enchantment, such as for a broom
     */
    String lore;

    /**
     * Optional arguments for this enchantment
     */
    String args;

    /**
     * Common functions
     */
    Ollivanders2Common common;

    /**
     * Constructor
     *
     * @param plugin   a callback to the plugin
     * @param mag      the magnitude of this enchantment
     * @param args     optional arguments for this enchantment
     * @param itemLore the optional lore for this enchantment
     */
    public Enchantment(@NotNull Ollivanders2 plugin, int mag, @Nullable String args, @Nullable String itemLore)
    {
        p = plugin;
        magnitude = mag;
        lore = itemLore;
        this.args = args;

        common = new Ollivanders2Common(p);
    }

    /**
     * Handle item despawn events
     *
     * @param event the item despawn event
     */
    abstract public void doItemDespawn(@NotNull ItemDespawnEvent event);

    /**
     * Handle item pickup events
     *
     * @param event the item pick up event
     */
    abstract public void doItemPickup(@NotNull EntityPickupItemEvent event);

    /**
     * Handle item drop events
     *
     * @param event the item drop event
     */
    abstract public void doItemDrop(@NotNull PlayerDropItemEvent event);

    /**
     * Handle item held events
     *
     * @param event the item drop event
     */
    abstract public void doItemHeld(@NotNull PlayerItemHeldEvent event);

    /**
     * Get the name of this enchantment.
     *
     * @return the enchantment name
     */
    @NotNull
    public String getName()
    {
        return enchantmentType.getName();
    }

    /**
     * Get the magnitude for this enchantment
     *
     * @return the magnitude
     */
    public int getMagnitude()
    {
        return magnitude;
    }

    /**
     * Get the type of enchantment
     *
     * @return the enchantment type
     */
    @NotNull
    public ItemEnchantmentType getType()
    {
        return enchantmentType;
    }

    /**
     * Get the optional arguments for this enchantment
     *
     * @return the args string
     */
    @Nullable
    public String getArgs()
    {
        return args;
    }
}
