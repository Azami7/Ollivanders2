package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The concealment charm — hides secret messages in books. Its only item-event behavior is blocking block inventories
 * (e.g. hoppers) from collecting the enchanted item.
 *
 * @see net.pottercraft.ollivanders2.spell.CELATUM
 * @see <a href="https://harrypotter.fandom.com/wiki/Revealing_Charm">Harry Potter Wiki - Revealing Charm</a>
 */
public class CELATUM extends Enchantment {
    /**
     * Constructor
     *
     * @param plugin   the Ollivanders2 plugin instance
     * @param mag      the magnitude (power level) of this enchantment
     * @param args     optional configuration arguments for this enchantment
     * @param itemLore optional custom lore for the enchanted item
     */
    public CELATUM(@NotNull Ollivanders2 plugin, int mag, @Nullable String args, @Nullable String itemLore) {
        super(plugin, mag, args, itemLore);
        enchantmentType = ItemEnchantmentType.CELATUM;

        if (args == null)
            common.printDebugMessage("CELATUM enchantment created with null arguments", null, null, true);
    }

    @Override
    public void doEntityPickupItem(@NotNull EntityPickupItemEvent event) {
    }

    /**
     * Prevent block inventories (e.g. hoppers) from collecting the celatum-enchanted item.
     *
     * @param event the inventory pickup item event
     */
    @Override
    public void doInventoryPickupItem(@NotNull InventoryPickupItemEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void doItemHeld(@NotNull PlayerItemHeldEvent event) {
    }

    @Override
    public void doItemDrop(@NotNull PlayerDropItemEvent event) {
    }
}
