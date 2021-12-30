package net.pottercraft.ollivanders2.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.BURNING;
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
 * @author Azami7
 * @since 2.6
 */
public class FLAGRANTE extends Enchantment
{
    /**
     * The minimum damage the curse will do
     */
    final double minDamage = 0.5;

    /**
     * The maximum damage the curse will do
     */
    final double maxDamage = 8.0;

    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     * @param mag the magnitude of this enchantment
     * @param args optional arguments for this enchantment
     * @param itemLore the optional lore for this enchantment
     */
    public FLAGRANTE (@NotNull Ollivanders2 plugin, int mag, @Nullable String args, @Nullable String itemLore)
    {
        super(plugin, mag,args, itemLore);
        enchantmentType = ItemEnchantmentType.FLAGRANTE;
    }

    /**
     * Handle item pickup events
     *
     * @param event the item pickup event
     */
    @Override
    public void doItemPickup (@NotNull EntityPickupItemEvent event)
    {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player))
            return;

        double damage = magnitude / 20.0;
        if (damage < minDamage)
            damage = minDamage;
        else if (damage > maxDamage)
            damage = maxDamage;

        BURNING burning = new BURNING(p, 0, entity.getUniqueId());
        burning.addDamage(damage);

        Ollivanders2API.getPlayers().playerEffects.addEffect(burning);

        common.printDebugMessage("Added flagrante curse to " + entity.getName(), null, null, false);
    }

    /**
     * Handle item drop events
     *
     * @param event the item drop event
     */
    @Override
    public void doItemDrop (@NotNull PlayerDropItemEvent event)
    {
        Player player = event.getPlayer();

        Ollivanders2API.getPlayers().playerEffects.removeEffect(player.getUniqueId(), O2EffectType.BURNING);
        common.printDebugMessage("Removed flagrante curse to " + player.getName(), null, null, false);
    }

    /**
     * Handle item despawn events
     *
     * @param event the item despawn event
     */
    public void doItemDespawn (@NotNull ItemDespawnEvent event)
    {
        event.setCancelled(true);
    }

    /**
     * Handle item held events
     *
     * @param event the item drop event
     */
    public void doItemHeld (@NotNull PlayerItemHeldEvent event) { }
}
