package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.FLAGRANTE_BURNING;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Flagrante Curse causes objects to emit searing heat when touched.
 * <p>
 * When a player holds a flagrante-cursed item, they suffer continuous {@link FLAGRANTE_BURNING} damage.
 * The curse is applied whenever the player picks up or holds the cursed item, and removed when they no
 * longer hold it. Flagrante items cannot be picked up by hoppers or other block inventories, and they
 * will not despawn from the world.
 * </p>
 * <p>
 * Damage is calculated from magnitude: {@code damage = magnitude / 20}, clamped between 0.5 and 8.0.
 * </p>
 *
 * @see FLAGRANTE_BURNING the burning effect applied to players holding flagrante items
 * @see net.pottercraft.ollivanders2.spell.FLAGRANTE the spell that casts this curse
 * @see <a href="https://harrypotter.fandom.com/wiki/Flagrante_Curse">https://harrypotter.fandom.com/wiki/Flagrante_Curse</a>
 */
public class FLAGRANTE extends Enchantment {
    /**
     * The minimum damage per application of the flagrante curse.
     * <p>
     * Ensures even low-magnitude curses deal at least this much damage when applied.
     * </p>
     */
    private static final double minDamage = 0.5;

    /**
     * The maximum damage per application of the flagrante curse.
     * <p>
     * Caps damage to prevent extremely high-magnitude curses from being overpowered.
     * </p>
     */
    private static final double maxDamage = 8.0;

    /**
     * The calculated damage per application for this enchantment instance.
     * <p>
     * Computed from magnitude and clamped between {@link #minDamage} and {@link #maxDamage}.
     * Applied to the player via {@link FLAGRANTE_BURNING} when they hold a cursed item.
     * </p>
     */
    private double damage;

    /**
     * Constructor for creating a flagrante curse enchantment instance.
     * <p>
     * Initializes the curse with a calculated damage value based on magnitude. The damage is computed as
     * {@code magnitude / 20}, then clamped between {@link #minDamage} and {@link #maxDamage} to ensure
     * balanced curse strength across all magnitude levels.
     * </p>
     *
     * @param plugin   the Ollivanders2 plugin instance
     * @param mag      the magnitude (power level) of this enchantment instance
     * @param args     optional configuration arguments specific to this enchantment instance
     * @param itemLore optional custom lore to display on the cursed item
     */
    public FLAGRANTE(@NotNull Ollivanders2 plugin, int mag, @Nullable String args, @Nullable String itemLore) {
        super(plugin, mag, args, itemLore);
        enchantmentType = ItemEnchantmentType.FLAGRANTE;

        damage = magnitude / 20.0;
        if (damage < minDamage)
            damage = minDamage;
        else if (damage > maxDamage)
            damage = maxDamage;
    }

    /**
     * Apply the flagrante curse when a player picks up a cursed item.
     * <p>
     * Triggers {@link #checkFlagranteStatus(Player)} for the player picking up the item. Non-player
     * entities are ignored since only players can be affected by the curse.
     * </p>
     *
     * @param event the entity item pickup event
     */
    @Override
    public void doEntityPickupItem(@NotNull EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player))
            return;

        checkFlagranteStatus((Player) entity);
    }

    /**
     * Prevent hoppers and other block inventories from picking up flagrante items.
     * <p>
     * Flagrante-cursed items are too dangerous for automated collection systems and will not be
     * picked up by hoppers, droppers, or other block-based inventories.
     * </p>
     *
     * @param event the inventory pickup event
     */
    @Override
    public void doInventoryPickupItem(@NotNull InventoryPickupItemEvent event) {
        event.setCancelled(true);
    }

    /**
     * Update curse status when a player drops a cursed item.
     * <p>
     * When a player drops a flagrante item, re-evaluate their curse status in case they are no longer
     * holding another cursed item. If they are, the curse continues; otherwise it is removed.
     * </p>
     *
     * @param event the item drop event
     */
    @Override
    public void doItemDrop(@NotNull PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        checkFlagranteStatus(player);
    }

    /**
     * No action needed for item held events.
     * <p>
     * The flagrante curse is applied immediately when items enter the player's inventory (via
     * {@link #doEntityPickupItem(EntityPickupItemEvent)}), so no additional handling is required when
     * the player changes which item slot is held.
     * </p>
     *
     * @param event the item held event
     */
    @Override
    public void doItemHeld(@NotNull PlayerItemHeldEvent event) {
        checkFlagranteStatus(event.getPlayer());
    }

    /**
     * Check if a player is holding a flagrante-cursed item and apply or remove the curse accordingly.
     * <p>
     * This method implements the core curse logic: if the player holds at least one flagrante-cursed
     * item, a {@link FLAGRANTE_BURNING} effect is applied with the calculated damage. If they no longer
     * hold any cursed items, any existing flagrante burning effect is removed.
     * </p>
     * <p>
     * Called whenever a flagrante item is picked up, dropped, or despawned to keep the curse status
     * synchronized with the player's actual inventory.
     * </p>
     *
     * @param player the player to check and update
     */
    private void checkFlagranteStatus(@NotNull Player player) {
        if (isHoldingEnchantedItem(player)) {
            FLAGRANTE_BURNING burning = new FLAGRANTE_BURNING(p, 5, true, player.getUniqueId());
            burning.addDamage(damage);

            Ollivanders2API.getPlayers().playerEffects.addEffect(burning);
            common.printDebugMessage("Added flagrante curse to " + player.getName(), null, null, false);
        }
        else {
            Ollivanders2API.getPlayers().playerEffects.removeEffect(player.getUniqueId(), O2EffectType.FLAGRANTE_BURNING);
            common.printDebugMessage("Removed flagrante curse from " + player.getName(), null, null, false);
        }
    }
}
