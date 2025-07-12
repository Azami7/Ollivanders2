package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.FLAGRANTE_BURNING;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Flagrante Curse causes objects to emit searing heat when touched.
 *
 * <p>Reference: https://harrypotter.fandom.com/wiki/Flagrante_Curse</p>
 */
public class FLAGRANTE extends Enchantment {
    /**
     * The minimum damage the curse will do
     */
    final double minDamage = 0.5;

    /**
     * The maximum damage the curse will do
     */
    final double maxDamage = 8.0;

    /**
     * The amount of damage this instance of the curse will do
     */
    double damage;

    /**
     * Constructor
     *
     * @param plugin   a callback to the plugin
     * @param mag      the magnitude of this enchantment
     * @param args     optional arguments for this enchantment
     * @param itemLore the optional lore for this enchantment
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
     * Handle item pickup events
     *
     * @param event the item pickup event
     */
    @Override
    public void doItemPickup(@NotNull EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player))
            return;

        checkFlagranteStatus((Player) entity);
    }

    /**
     * Handle when a player drops a flagrante item
     *
     * @param event the item drop event
     */
    @Override
    public void doItemDrop(@NotNull PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        checkFlagranteStatus(player);
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
     * Flagrante effect the player as soon as the item enters their inventory so we do not need to check for held
     *
     * @param event the item drop event
     */
    public void doItemHeld(@NotNull PlayerItemHeldEvent event) { }

    /**
     * A flagrante-cursed item was either held or stopped being held - check to see if the player is still holding at least 1 flagrante item
     *
     * @param player the player to check
     */
    void checkFlagranteStatus(Player player) {
        if (isHoldingEnchantedItem(player)) {
            FLAGRANTE_BURNING burning = new FLAGRANTE_BURNING(p, 0, player.getUniqueId());
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
