package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The concealment charm - used to hide secret messages in books.
 * <p>
 * Enchantment behavior:</p>
 * <ul>
 * <li>Player pickup: no special behavior, players can pick up celatum items normally</li>
 * <li>Hopper pickup: blocked, hoppers cannot collect celatum-enchanted items</li>
 * <li>Drop events: no special behavior, items can be dropped normally</li>
 * <li>Slot switching: no special behavior, celatum status is independent of player actions</li>
 * </ul>
 *
 * @see net.pottercraft.ollivanders2.spell.CELATUM the spell that casts this enchantment
 * @see <a href="https://harrypotter.fandom.com/wiki/Revealing_Charm">https://harrypotter.fandom.com/wiki/Revealing_Charm</a>
 */
public class CELATUM extends Enchantment {
    /**
     * Constructor for creating a CELATUM enchantment instance.
     *
     * @param plugin   the Ollivanders2 plugin instance
     * @param mag      the magnitude (power level) of this enchantment
     * @param args     optional configuration arguments specific to this enchantment instance
     * @param itemLore optional custom lore to display on the enchanted item
     */
    public CELATUM(@NotNull Ollivanders2 plugin, int mag, @Nullable String args, @Nullable String itemLore) {
        super(plugin, mag, args, itemLore);
        enchantmentType = ItemEnchantmentType.CELATUM;

        if (args == null)
            common.printDebugMessage("CELATUM enchantment created with null arguments", null, null, true);
    }

    /**
     * No special handling for player item pickup events.
     * <p>
     * CELATUM enchantments do not respond to players picking up items. Players can freely pick up
     * celatum-enchanted items without any restrictions or special effects. The enchantment only
     * affects whether hoppers and other block inventories can collect the items.
     * </p>
     *
     * @param event the entity item pickup event (not used)
     */
    @Override
    public void doEntityPickupItem(@NotNull EntityPickupItemEvent event) {
    }

    /**
     * Prevent hoppers and block inventories from picking up celatum-enchanted items.
     * <p>
     * Celatum-enchanted items cannot be picked up by hoppers or other automated inventory systems.
     * This prevents the items from being automatically collected, allowing them to persist in
     * specific world locations without being moved into storage systems.
     * </p>
     *
     * @param event the inventory pickup item event
     */
    @Override
    public void doInventoryPickupItem(@NotNull InventoryPickupItemEvent event) {
        event.setCancelled(true);
    }

    /**
     * No special handling for item held events.
     * <p>
     * CELATUM enchantments do not respond to players switching between hotbar slots. The enchantment's
     * concealment behavior is independent of what the player is currently holding and applies to
     * the item at all times.
     * </p>
     *
     * @param event the player item held event (not used)
     */
    @Override
    public void doItemHeld(@NotNull PlayerItemHeldEvent event) {
    }

    /**
     * No special handling for item drop events.
     * <p>
     * CELATUM enchantments do not respond to players dropping items. The enchantment's concealment
     * behavior applies to the item whether it's in a player's inventory or dropped in the world.
     * Items can be dropped freely without any restrictions.
     * </p>
     *
     * @param event the player drop item event (not used)
     */
    @Override
    public void doItemDrop(@NotNull PlayerDropItemEvent event) {
    }
}
