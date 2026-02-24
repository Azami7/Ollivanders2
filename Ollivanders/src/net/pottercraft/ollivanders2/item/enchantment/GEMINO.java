package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * GEMINO duplication curse for items.
 * <p>
 * GEMINO is an expert-level curse that causes items to multiply exponentially when picked up by a player.
 * When a player picks up a gemino-cursed item, 2^magnitude additional copies are added to their inventory,
 * creating exponential duplication. For example: magnitude 1 creates 2 copies (2^1), magnitude 2 creates
 * 4 copies (2^2), magnitude 3 creates 8 copies (2^3), magnitude 4 creates 16 copies (2^4).</p>
 * <p>
 * The exponential nature of this curse means it can quickly fill a player's inventory, potentially causing
 * inventory overflow and item loss. The cursed item itself is not duplicated—only unenchanted copies are
 * added. Gemino items cannot be picked up by hoppers or other block inventories, preventing automated
 * systems from triggering the curse.</p>
 * <p>
 * Curse behavior:</p>
 * <ul>
 * <li>Player pickup: triggers exponential duplication (2^magnitude copies added)</li>
 * <li>Hopper pickup: blocked, hoppers cannot trigger the curse</li>
 * <li>Drop events: no special behavior, duplication only occurs on pickup</li>
 * <li>Slot switching: no special behavior, duplication only occurs on pickup</li>
 * </ul>
 *
 * @see net.pottercraft.ollivanders2.spell.GEMINO the spell that casts this curse
 * @see <a href="https://harrypotter.fandom.com/wiki/Doubling_Charm">https://harrypotter.fandom.com/wiki/Doubling_Charm</a>
 */
public class GEMINO extends Enchantment {
    /**
     * Constructor for creating a gemino curse enchantment instance.
     *
     * @param plugin   the Ollivanders2 plugin instance
     * @param mag      the magnitude (power level) of this enchantment, affects number of copies created
     * @param args     optional configuration arguments specific to this enchantment instance
     * @param itemLore optional custom lore to display on the cursed item
     */
    public GEMINO(@NotNull Ollivanders2 plugin, int mag, @Nullable String args, @Nullable String itemLore) {
        super(plugin, mag, args, itemLore);
        enchantmentType = ItemEnchantmentType.GEMINO;
    }

    /**
     * Apply the GEMINO curse when a player picks up a cursed item.
     * <p>
     * This method triggers the exponential duplication when a player picks up a gemino-cursed item:</p>
     * <ul>
     * <li>Clones the picked-up item and removes all enchantment NBT data</li>
     * <li>Sets the amount to 2^magnitude (creates the duplicate copies)</li>
     * <li>Adds the duplicates (without the curse) to the player's inventory</li>
     * </ul>
     * <p>
     * The original gemino-cursed item is also added to the inventory via the normal pickup event flow.
     * Combined with the duplicates, this can quickly overwhelm the player's inventory. Non-player entities
     * are ignored since only players can be affected by the curse.</p>
     * <p>
     * Important: The duplicates created by this method have the enchantment NBT removed, so they will not
     * trigger additional duplication if picked up again by another player.</p>
     *
     * @param event the entity item pickup event
     */
    @Override
    public void doEntityPickupItem(@NotNull EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player))
            return;

        Item item = event.getItem();

        ItemStack copy = EnchantedItems.removeEnchantmentNBT(item.getItemStack().clone());
        copy.setAmount((int)Math.pow(2, magnitude));

        ((Player) entity).getInventory().addItem(copy);
    }

    /**
     * Prevent hoppers and other block inventories from picking up gemino items.
     * <p>
     * Gemino-cursed items are too dangerous for automated collection systems and could
     * cause exponential item duplication issues.
     * </p>
     *
     * @param event the inventory pickup event
     */
    @Override
    public void doInventoryPickupItem(@NotNull InventoryPickupItemEvent event) {
        event.setCancelled(true);
    }

    /**
     * No action needed for item drop events.
     * <p>
     * The gemino curse only triggers when items are picked up, not when they are dropped.
     * Dropping a gemino item does not trigger duplication.
     * </p>
     *
     * @param event the item drop event
     */
    @Override
    public void doItemDrop(@NotNull PlayerDropItemEvent event) {
        // no action - gemino only triggers on pickup
    }

    /**
     * No action needed for item held events.
     * <p>
     * The gemino curse only triggers when items are picked up from the world,
     * not when the player switches which item slot is held.
     * </p>
     *
     * @param event the item held event
     */
    @Override
    public void doItemHeld(@NotNull PlayerItemHeldEvent event) {
        // no action - gemino only triggers on pickup
    }
}
