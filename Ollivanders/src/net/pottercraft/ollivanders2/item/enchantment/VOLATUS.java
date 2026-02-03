package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.BROOM_FLYING;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * VOLATUS flight enchantment for broomsticks.
 * <p>
 * VOLATUS is an expert-level beneficial enchantment that grants the {@link O2EffectType#BROOM_FLYING}
 * effect to players holding volatus-enchanted broomsticks. The enchantment actively monitors whether
 * the player is holding a volatus item and applies or removes the flying effect accordingly. Flight
 * is only enabled when the player has a volatus-enchanted broomstick in their main hand or off-hand.
 * </p>
 * <p>
 * Enchantment behavior:
 * <ul>
 * <li>When a volatus item is picked up, the flying effect is applied if the player is now holding one</li>
 * <li>When a volatus item is dropped, the flying effect is removed if the player is no longer holding one</li>
 * <li>When the player switches hotbar slots, flying status is re-evaluated based on current item</li>
 * <li>Hoppers cannot pick up volatus items (broomsticks persist in the world)</li>
 * <li>Requires broomsticks to be enabled in the plugin configuration</li>
 * </ul>
 * </p>
 *
 * @see net.pottercraft.ollivanders2.spell.VOLATUS the spell that casts this enchantment
 * @see <a href="https://harrypotter.fandom.com/wiki/Broomstick">https://harrypotter.fandom.com/wiki/Broomstick</a>
 */
public class VOLATUS extends Enchantment {
    /**
     * Constructor for creating a VOLATUS flight enchantment instance.
     *
     * @param plugin   the Ollivanders2 plugin instance
     * @param mag      the magnitude (power level) of this enchantment
     * @param args     optional configuration arguments specific to this enchantment instance
     * @param itemLore optional custom lore to display on the enchanted broomstick
     */
    public VOLATUS(@NotNull Ollivanders2 plugin, int mag, @Nullable String args, @Nullable String itemLore) {
        super(plugin, mag, args, itemLore);
        enchantmentType = ItemEnchantmentType.VOLATUS;
    }

    /**
     * Handle player pickup of a volatus-enchanted item (broomstick).
     * <p>
     * When a player picks up an item, this method schedules a deferred check to see if they're now
     * holding a volatus-enchanted broomstick. If so, the {@link O2EffectType#BROOM_FLYING} effect
     * is applied via {@link #checkBroomStatus(Player)}.
     * </p>
     * <p>
     * The check is scheduled with a 1-second (20-tick) delay to allow the inventory update to complete
     * before evaluating the player's current items. Does nothing if the entity picking up the item is
     * not a player, or if broomsticks are disabled in the configuration.
     * </p>
     *
     * @param event the entity item pickup event
     */
    @Override
    public void doEntityPickupItem(@NotNull EntityPickupItemEvent event) {
        if (!EnchantedItems.areBroomsEnabled()) {
            common.printDebugMessage("VOLATUS.doEntityPickupItem: brooms not enabled", null, null, false);
            return;
        }

        Entity entity = event.getEntity();
        if (!(entity instanceof Player))
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                checkBroomStatus((Player) entity);
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Prevent hoppers and block inventories from picking up volatus-enchanted items.
     * <p>
     * Volatus items (broomsticks) cannot be picked up by hoppers or other automated inventory systems.
     * This prevents broomsticks from being moved into storage systems and ensures they remain available
     * for players to find and use as intended.
     * </p>
     *
     * @param event the inventory pickup item event (not used, but required by interface)
     */
    @Override
    public void doInventoryPickupItem(@NotNull InventoryPickupItemEvent event) {
        event.setCancelled(true);
    }

    /**
     * Re-evaluate flying status when a player drops an item.
     * <p>
     * When a player drops an item, this method schedules a deferred check to see if they're still
     * holding a volatus-enchanted broomstick. If they no longer have a volatus item in either hand,
     * the {@link O2EffectType#BROOM_FLYING} effect is removed.
     * </p>
     * <p>
     * The check is scheduled with a 1-second (20-tick) delay to allow the inventory update to complete
     * before evaluating the player's current items. Does nothing if broomsticks are disabled in the
     * configuration.
     * </p>
     *
     * @param event the player drop item event
     */
    @Override
    public void doItemDrop(@NotNull PlayerDropItemEvent event) {
        if (!EnchantedItems.areBroomsEnabled()) {
            common.printDebugMessage("VOLATUS.doItemDrop: brooms not enabled", null, null, false);
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                checkBroomStatus(event.getPlayer());
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Re-evaluate flying status when a player changes which item slot is held.
     * <p>
     * When a player switches between hotbar slots, this method schedules a deferred check to see
     * if they're now holding a volatus-enchanted broomstick. If they are, the
     * {@link O2EffectType#BROOM_FLYING} effect is applied; otherwise it is removed.
     * </p>
     * <p>
     * The check is scheduled with a 1-second (20-tick) delay to allow the slot change to complete
     * before evaluating what item is now in the player's hand. Does nothing if broomsticks are
     * disabled in the configuration.
     * </p>
     *
     * @param event the player item held event (slot switching event)
     */
    @Override
    public void doItemHeld(@NotNull PlayerItemHeldEvent event) {
        if (!EnchantedItems.areBroomsEnabled()) {
            common.printDebugMessage("VOLATUS.doItemHeld: brooms not enabled", null, null, false);
            return;
        }

        Player player = event.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                checkBroomStatus(player);
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Re-evaluate the player's flying status based on whether they're holding a volatus broomstick.
     * <p>
     * This method checks if the player is holding a volatus-enchanted item in either their main hand
     * or off-hand. If they are, the {@link O2EffectType#BROOM_FLYING} flying effect is applied.
     * If they are not, any existing flying effect is removed.
     * </p>
     * <p>
     * The flying effect is permanent (lasts until explicitly removed). This method is called
     * whenever item-related events occur (pickup, drop, slot switch) to keep flying status in sync
     * with the player's current inventory.
     * </p>
     *
     * @param player the player whose flying status should be checked and updated
     */
    void checkBroomStatus(Player player) {
        if (!EnchantedItems.areBroomsEnabled()) {
            common.printDebugMessage("VOLATUS.doItemHeld: checkBroomStatus", null, null, false);
            return;
        }

        // do they have a broom in either hand?
        if (isHoldingEnchantedItem(player)) {
            common.printDebugMessage(player.getDisplayName() + " is holding a broom", null, null, false);
            BROOM_FLYING effect = new BROOM_FLYING(p, 5, true, player.getUniqueId());
            Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        }
        else {
            common.printDebugMessage(player.getDisplayName() + " is not holding a broom", null, null, false);
            Ollivanders2API.getPlayers().playerEffects.removeEffect(player.getUniqueId(), O2EffectType.BROOM_FLYING);
        }
    }
}
