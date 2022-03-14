package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.BROOM_FLYING;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Add broom flight to an item
 *
 * @link https://harrypotter.fandom.com/wiki/Broomstick
 */
public class VOLATUS extends Enchantment
{
    /**
     * Constructor
     *
     * @param plugin   a callback to the plugin
     * @param mag      the magnitude of this enchantment
     * @param args     optional arguments for this enchantment
     * @param itemLore the optional lore for this enchantment
     */
    public VOLATUS(@NotNull Ollivanders2 plugin, int mag, @Nullable String args, @Nullable String itemLore)
    {
        super(plugin, mag, args, itemLore);
        enchantmentType = ItemEnchantmentType.VOLATUS;
    }

    /**
     * Handle item pickup events
     *
     * @param event the item pickup event
     */
    @Override
    public void doItemPickup(@NotNull EntityPickupItemEvent event)
    {
    }

    /**
     * Handle item drop events
     *
     * @param event the item drop event
     */
    @Override
    public void doItemDrop(@NotNull PlayerDropItemEvent event)
    {
    }

    /**
     * Handle item despawn events
     *
     * @param event the item despawn event
     */
    @Override
    public void doItemDespawn(@NotNull ItemDespawnEvent event)
    {
    }

    /**
     * Handle item held events
     *
     * @param event the item drop event
     */
    public void doItemHeld(@NotNull PlayerItemHeldEvent event)
    {
        if (!EnchantedItems.enableBrooms)
            return;

        Player player = event.getPlayer();

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                checkBroomStatus(player);
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * A broom was either held or stopped being held - check to see if the player is still holding a least 1 broom
     *
     * @param player the player to check
     */
    void checkBroomStatus(Player player)
    {
        if (!EnchantedItems.enableBrooms)
            return;

        // do they have a broom in either hand?
        if (isHoldingVolatusEnchantedItem(player))
        {
            common.printDebugMessage(player.getDisplayName() + " is holding a broom", null, null, false);
            BROOM_FLYING effect = new BROOM_FLYING(p, 5, player.getUniqueId());
            Ollivanders2API.getPlayers().playerEffects.addEffect(effect);
        }
        else
        {
            common.printDebugMessage(player.getDisplayName() + " is not holding a broom", null, null, false);
            Ollivanders2API.getPlayers().playerEffects.removeEffect(player.getUniqueId(), O2EffectType.FLYING);
        }
    }

    /**
     * Is the player holding an item enchanted with Volatus?
     *
     * @param player the player to check
     * @return true if they are holding a broom, false otherwise
     */
    private boolean isHoldingVolatusEnchantedItem(Player player)
    {
        ItemStack primaryItem = player.getInventory().getItemInMainHand();

        // first check NBT tag of item in primary hand
        String eTypeStr = Ollivanders2API.getItems().enchantedItems.getEnchantmentTypeKey(primaryItem);
        if (eTypeStr != null)
            return eTypeStr.equalsIgnoreCase(enchantmentType.toString());

        // check NBT tag in item in player's off hand
        ItemStack secondaryItem = player.getInventory().getItemInOffHand();
        eTypeStr = Ollivanders2API.getItems().enchantedItems.getEnchantmentTypeKey(secondaryItem);

        if (eTypeStr != null)
            return eTypeStr.equalsIgnoreCase(enchantmentType.toString());

        // no NBT tag, this is not a broom, return false
        return false;
    }
}
