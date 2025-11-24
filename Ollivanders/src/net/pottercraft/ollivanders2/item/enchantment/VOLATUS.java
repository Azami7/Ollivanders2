package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.BROOM_FLYING;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Add broom flight to an item
 * <p>
 * {@link net.pottercraft.ollivanders2.spell.VOLATUS}
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Broomstick">https://harrypotter.fandom.com/wiki/Broomstick</a>
 */
public class VOLATUS extends Enchantment {
    /**
     * Constructor
     *
     * @param plugin   a callback to the plugin
     * @param mag      the magnitude of this enchantment
     * @param args     optional arguments for this enchantment
     * @param itemLore the optional lore for this enchantment
     */
    public VOLATUS(@NotNull Ollivanders2 plugin, int mag, @Nullable String args, @Nullable String itemLore) {
        super(plugin, mag, args, itemLore);
        enchantmentType = ItemEnchantmentType.VOLATUS;
    }

    /**
     * Handle item pickup events
     *
     * @param event the item pickup event
     */
    @Override
    public void doEntityPickupItem(@NotNull EntityPickupItemEvent event) {
        if (!EnchantedItems.enableBrooms)
            return;

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
     * Handle item pickup events
     *
     * @param event the item pickup event
     */
    public void doInventoryPickupItem(@NotNull InventoryPickupItemEvent event) {
    }

    /**
     * Handle item drop events
     *
     * @param event the item drop event
     */
    @Override
    public void doItemDrop(@NotNull PlayerDropItemEvent event) {
        if (!EnchantedItems.enableBrooms)
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                checkBroomStatus(event.getPlayer());
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Handle item despawn events
     *
     * @param event the item despawn event
     */
    @Override
    public void doItemDespawn(@NotNull ItemDespawnEvent event) {
        if (!EnchantedItems.enableBrooms)
            return;

        Item despawnedItem = event.getEntity();

        // is this a broom?
        if (!O2ItemType.BASIC_BROOM.isItemThisType(despawnedItem))
            return;

        // is it enchanted with Volatus?
        Enchantment enchantment = Ollivanders2API.getItems().enchantedItems.getEnchantment(despawnedItem.getItemStack());
        if (enchantment == null || enchantment.getType() != ItemEnchantmentType.VOLATUS)
            return;

        // someone is holding this - we don't know who so we need to iterate through all players to check broom flying
        for (Player player : p.getServer().getOnlinePlayers()) {
            checkBroomStatus(player);
        }
    }

    /**
     * Handle item held events
     *
     * @param event the item drop event
     */
    public void doItemHeld(@NotNull PlayerItemHeldEvent event) {
        if (!EnchantedItems.enableBrooms)
            return;

        Player player = event.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                checkBroomStatus(player);
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * A broom was either held or stopped being held - check to see if the player is still holding at least 1 broom
     *
     * @param player the player to check
     */
    void checkBroomStatus(Player player) {
        if (!EnchantedItems.enableBrooms)
            return;

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
