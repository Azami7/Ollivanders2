package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.stationaryspell.HORCRUX;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Splits the caster's soul into a horcrux, letting them later resurrect with their magical
 * experience intact at a permanent cost.
 * <p>
 * When the projectile reaches a usable item, the caster spends one collected soul and has their
 * maximum health permanently halved, and a {@link HORCRUX} stationary spell is anchored to that
 * item. A caster may own only one horcrux at a time, must have at least one collected soul, and
 * must have enough health left to survive the halving. Horcruxes can only be destroyed with
 * Fiendfyre.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Horcrux-making_spell">Harry Potter Wiki - Horcrux-making Spell</a>
 */
public final class ET_INTERFICIAM_ANIMAM_LIGAVERIS extends O2Spell {
    /**
     * The caster's maximum health after the horcrux is created (current maximum halved). Computed in
     * {@link #canCreateHorcrux()} and applied in {@link #doCheckEffect()}.
     */
    private double futureHealth = 0.0;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ET_INTERFICIAM_ANIMAM_LIGAVERIS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.ET_INTERFICIAM_ANIMAM_LIGAVERIS;
        branch = O2MagicBranch.DARK_ARTS;

        flavorText = new ArrayList<>() {{
            add("Tamper with the deepest mysteries — the source of life, the essence of self — only if prepared for consequences of the most extreme and dangerous kind.");
        }};

        text = "The most horrifying and destructive act man can do is the creation of a Horcrux. Through splitting one's soul through the murder of another player, one is able to resurrect with all of their magical experience intact. "
                + "However, this action has a terrible cost, for as long as the soul is split, the player's maximum health is halved for each Horcrux they have made. "
                + "The only known way of destroying a Horcrux is with Fiendfyre.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ET_INTERFICIAM_ANIMAM_LIGAVERIS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.ET_INTERFICIAM_ANIMAM_LIGAVERIS;
        branch = O2MagicBranch.DARK_ARTS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.MOB_SPAWNING); // needed because Fiendfyre requires it, otherwise horcruxes could get made in locations players couldn't kill them

        initSpell();
    }

    /**
     * Look for a usable item in the projectile's path and turn it into a horcrux.
     * <p>
     * Skips wands and enchanted items. On the first usable item, and only if
     * {@link #canCreateHorcrux()} passes, the caster spends a soul, has their maximum health halved,
     * and a {@link HORCRUX} stationary spell is anchored to the item. The spell ends after handling
     * one item, or when the projectile hits a block without finding one.
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        // if we hit a target but didn't find an item, then end this spell
        if (hasHitBlock()) {
            kill();
            return;
        }

        // check for an item
        List<Item> items = getNearbyItems(defaultRadius);
        for (Item item : items) {
            // if this is a wand or enchanted item, we cannot use it
            if (Ollivanders2API.getItems().getWands().isWand(item.getItemStack()) || (Ollivanders2API.getItems().enchantedItems.isEnchanted(item)))
                continue;

            // check to see if they can create a horcrux
            if (!canCreateHorcrux()) {
                common.printDebugMessage("ET_INTERFICIAM_ANIMAM_LIGAVERIS.checkEffect: player not able to create a horcrux", null, null, false);
                kill();
                return;
            }

            // reduce their health and decrement a soul
            AttributeInstance healthAttribute = caster.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
            if (healthAttribute == null) {
                common.printDebugMessage("ET_INTERFICIAM_ANIMAM_LIGAVERIS.checkEffect: player health attribute is null", null, null, true);
                kill();
                return;
            }

            p.getO2Player(caster).subtractSoul();
            healthAttribute.setBaseValue(futureHealth);

            // create a horcrux from this item
            HORCRUX horcrux = new HORCRUX(p, caster.getUniqueId(), item.getLocation(), item);
            horcrux.flair(10);
            Ollivanders2API.getStationarySpells().addStationarySpell(horcrux);

            kill();
            return;
        }
    }

    /**
     * Check whether the caster currently meets every requirement to create a horcrux.
     * <p>
     * Requires at least one collected soul, no horcrux already owned by the caster, and enough
     * maximum health to survive halving it. On failure the caster is messaged the reason; on success
     * the post-halving health is recorded in {@link #futureHealth} for {@link #doCheckEffect()} to
     * apply.
     * </p>
     *
     * @return true if the caster can create a horcrux, false otherwise
     */
    boolean canCreateHorcrux() {
        // does the caster have souls that can be used
        if (p.getO2Player(caster).getSouls() < 1) {
            caster.sendMessage(Ollivanders2.chatColor + "Your soul is not yet so damaged to allow this.");
            return false;
        }

        // do they already have a horcrux?
        for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells().getActiveStationarySpells()) {
            if (stationarySpell.getSpellType() == O2StationarySpellType.HORCRUX && stationarySpell.getCasterID().equals(caster.getUniqueId())) {
                caster.sendMessage(Ollivanders2.chatColor + "You can have already divided your soul.");
                return false;
            }
        }

        // does the player have enough health to create a new horcrux?
        AttributeInstance healthAttribute = caster.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
        if (healthAttribute == null) {
            common.printDebugMessage("ET_INTERFICIAM_ANIMAM_LIGAVERIS.checkEffect: player health attribute is null", null, null, true);
            return false;
        }
        futureHealth = healthAttribute.getBaseValue() / 2;
        if (futureHealth < 2) {
            caster.sendMessage(Ollivanders2.chatColor + "You are not strong enough to split your soul.");
            return false;
        }

        return true;
    }
}