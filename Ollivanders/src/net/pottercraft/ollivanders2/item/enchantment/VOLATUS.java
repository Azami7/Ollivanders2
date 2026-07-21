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
 * Flight enchantment for broomsticks: grants {@link O2EffectType#BROOM_FLYING} to a player while they hold a
 * volatus-enchanted broom in either hand, and removes it once they no longer do. Only active when broomsticks are
 * enabled in the plugin configuration.
 *
 * @see net.pottercraft.ollivanders2.spell.VOLATUS
 * @see <a href="https://harrypotter.fandom.com/wiki/Broomstick">Harry Potter Wiki - Broomstick</a>
 */
public class VOLATUS extends Enchantment {
    /**
     * Constructor
     *
     * @param plugin   the Ollivanders2 plugin instance
     * @param mag      the magnitude (power level) of this enchantment
     * @param args     optional configuration arguments for this enchantment
     * @param itemLore optional custom lore for the enchanted broomstick
     */
    public VOLATUS(@NotNull Ollivanders2 plugin, int mag, @Nullable String args, @Nullable String itemLore) {
        super(plugin, mag, args, itemLore);
        enchantmentType = ItemEnchantmentType.VOLATUS;
    }

    /**
     * Re-evaluate the player's flight after they pick up an item, applying {@link O2EffectType#BROOM_FLYING} if they
     * now hold a volatus broom. Deferred a tick so the inventory update settles first. No-op if brooms are disabled
     * or the entity is not a player.
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
     * Prevent block inventories (e.g. hoppers) from collecting the volatus-enchanted broom.
     *
     * @param event the inventory pickup item event
     */
    @Override
    public void doInventoryPickupItem(@NotNull InventoryPickupItemEvent event) {
        event.setCancelled(true);
    }

    /**
     * Re-evaluate the player's flight after they drop an item, removing {@link O2EffectType#BROOM_FLYING} if they no
     * longer hold a volatus broom. Deferred a tick so the inventory update settles first. No-op if brooms are
     * disabled.
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
     * Re-evaluate the player's flight when they switch hotbar slots, applying or removing
     * {@link O2EffectType#BROOM_FLYING} to match whether they now hold a volatus broom. Deferred a tick so the slot
     * change settles first. No-op if brooms are disabled.
     *
     * @param event the player item held event
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
     * Apply {@link O2EffectType#BROOM_FLYING} if the player is holding a volatus broom in either hand, otherwise
     * remove it, keeping flight in sync with the player's current inventory. No-op if brooms are disabled.
     *
     * @param player the player whose flight status to update
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
