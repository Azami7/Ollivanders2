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
 * Searing-heat curse — a player holding a cursed item suffers continuous {@link FLAGRANTE_BURNING} damage until they
 * no longer hold one. Flagrante items cannot be collected by block inventories (e.g. hoppers) and do not despawn.
 *
 * @see net.pottercraft.ollivanders2.spell.FLAGRANTE
 * @see <a href="https://harrypotter.fandom.com/wiki/Flagrante_Curse">Harry Potter Wiki - Flagrante Curse</a>
 */
public class FLAGRANTE extends Enchantment {
    /**
     * Lower clamp on {@link #damage} so even magnitude-1 curses deal meaningful damage.
     */
    private static final double minDamage = 0.5;

    /**
     * Upper clamp on {@link #damage} so high-magnitude curses cannot become overpowered.
     */
    private static final double maxDamage = 8.0;

    /**
     * Per-tick burning damage, scaled from magnitude and clamped to [{@link #minDamage}, {@link #maxDamage}]. Applied
     * via {@link FLAGRANTE_BURNING} while the player holds a cursed item.
     */
    private double damage;

    /**
     * Constructor
     *
     * @param plugin   the Ollivanders2 plugin instance
     * @param mag      the magnitude (power level) of this enchantment; scales {@link #damage}
     * @param args     optional configuration arguments for this enchantment
     * @param itemLore optional custom lore for the enchanted item
     */
    public FLAGRANTE(@NotNull Ollivanders2 plugin, int mag, @Nullable String args, @Nullable String itemLore) {
        super(plugin, mag, args, itemLore);
        enchantmentType = ItemEnchantmentType.FLAGRANTE;

        damage = magnitude / 1.25 ;
        if (damage < minDamage)
            damage = minDamage;
        else if (damage > maxDamage)
            damage = maxDamage;
    }

    /**
     * On a player pickup, re-evaluate the player's curse status. Non-player pickups are ignored.
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
     * Prevent block inventories (e.g. hoppers) from collecting the flagrante-cursed item.
     *
     * @param event the inventory pickup item event
     */
    @Override
    public void doInventoryPickupItem(@NotNull InventoryPickupItemEvent event) {
        event.setCancelled(true);
    }

    /**
     * On a drop, re-evaluate the player's curse status; the burning is removed if they no longer hold a cursed item.
     *
     * @param event the item drop event
     */
    @Override
    public void doItemDrop(@NotNull PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        checkFlagranteStatus(player);
    }

    /**
     * On a slot change, re-evaluate the player's curse status.
     *
     * @param event the item held event
     */
    @Override
    public void doItemHeld(@NotNull PlayerItemHeldEvent event) {
        checkFlagranteStatus(event.getPlayer());
    }

    /**
     * Apply or remove the burning curse to match the player's inventory: adds a {@link FLAGRANTE_BURNING} effect (with
     * this enchantment's {@link #damage}) if they hold a cursed item, otherwise removes any existing one.
     *
     * @param player the player whose curse status to synchronize
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
