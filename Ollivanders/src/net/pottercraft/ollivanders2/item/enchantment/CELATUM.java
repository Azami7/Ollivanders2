package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Hides the text on a book or sign.
 * {@link net.pottercraft.ollivanders2.spell.CELATUM}
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Revealing_Charm">https://harrypotter.fandom.com/wiki/Revealing_Charm</a>
 */
public class CELATUM extends Enchantment {
    /**
     * Constructor
     *
     * @param plugin   a callback to the plugin
     * @param mag      the magnitude of this enchantment
     * @param args     optional arguments for this enchantment
     * @param itemLore the optional lore for this enchantment
     */
    public CELATUM(@NotNull Ollivanders2 plugin, int mag, @Nullable String args, @Nullable String itemLore) {
        super(plugin, mag, args, itemLore);
        enchantmentType = ItemEnchantmentType.CELATUM;
    }

    /**
     * Do not allow flagrante items to be despawned
     *
     * @param event the item despawn event
     */
    public void doItemDespawn(@NotNull ItemDespawnEvent event) {
        event.setCancelled(true);
    }

    /**
     * Handle item pickup events
     *
     * @param event the item pickup event
     */
    @Override
    public void doItemPickup(@NotNull EntityPickupItemEvent event) {
    }

    /**
     * Flagrante effect the player as soon as the item enters their inventory so we do not need to check for held
     *
     * @param event the item drop event
     */
    @Override
    public void doItemHeld(@NotNull PlayerItemHeldEvent event) {
    }

    /**
     * A flagrante-cursed item was either held or stopped being held - check to see if the player is still holding at least 1 flagrante item
     *
     * @param event the item drop event
     */
    @Override
    public void doItemDrop(@NotNull PlayerDropItemEvent event) {
    }
}
