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
 * Duplication curse — picking up a cursed item adds {@code 2^magnitude} unenchanted copies to the player's inventory,
 * which can quickly overflow it. The cursed item itself is never duplicated, so the copies cannot re-trigger the curse.
 *
 * @see net.pottercraft.ollivanders2.spell.GEMINO
 * @see <a href="https://harrypotter.fandom.com/wiki/Doubling_Charm">Harry Potter Wiki - Doubling Charm</a>
 */
public class GEMINO extends Enchantment {
    /**
     * Constructor
     *
     * @param plugin   the Ollivanders2 plugin instance
     * @param mag      the magnitude (power level) of this enchantment; sets the exponent for the copy count
     * @param args     optional configuration arguments for this enchantment
     * @param itemLore optional custom lore for the enchanted item
     */
    public GEMINO(@NotNull Ollivanders2 plugin, int mag, @Nullable String args, @Nullable String itemLore) {
        super(plugin, mag, args, itemLore);
        enchantmentType = ItemEnchantmentType.GEMINO;
    }

    /**
     * On a player pickup, add {@code 2^magnitude} unenchanted copies of the item to their inventory. Non-player pickups
     * are ignored. The copies have the enchantment NBT stripped so they do not re-trigger the curse.
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
     * Prevent block inventories (e.g. hoppers) from collecting the gemino-cursed item, so automated systems cannot
     * trigger the duplication.
     *
     * @param event the inventory pickup item event
     */
    @Override
    public void doInventoryPickupItem(@NotNull InventoryPickupItemEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void doItemDrop(@NotNull PlayerDropItemEvent event) {
    }

    @Override
    public void doItemHeld(@NotNull PlayerItemHeldEvent event) {
    }
}
