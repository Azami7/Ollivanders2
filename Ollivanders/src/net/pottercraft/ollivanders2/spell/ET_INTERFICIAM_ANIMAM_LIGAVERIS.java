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
 * Creates a horcrux stationary spell object where it collides with a block.
 * Also damages the player and increases their souls count.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Horcrux-making_spell">https://harrypotter.fandom.com/wiki/Horcrux-making_spell</a>
 */
public final class ET_INTERFICIAM_ANIMAM_LIGAVERIS extends O2Spell {
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
     * Look for an item to make in to a horcrux in the spell projectile's path.
     */
    @Override
    protected void doCheckEffect() {
        // if we hit a target but didn't find an item, then end this spell
        if (hasHitTarget()) {
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
            AttributeInstance healthAttribute = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
            if (healthAttribute == null) {
                common.printDebugMessage("ET_INTERFICIAM_ANIMAM_LIGAVERIS.checkEffect: player health attribute is null", null, null, true);
                kill();
                return;
            }

            p.getO2Player(player).subtractSoul();
            healthAttribute.setBaseValue(futureHealth);

            // create a horcrux from this item
            HORCRUX horcrux = new HORCRUX(p, player.getUniqueId(), item.getLocation(), item);
            horcrux.flair(10);
            Ollivanders2API.getStationarySpells().addStationarySpell(horcrux);

            kill();
            return;
        }
    }

    /**
     * Can this player create a horcrux?
     */
    boolean canCreateHorcrux() {
        // does the caster have souls that can be used
        if (p.getO2Player(player).getSouls() < 1) {
            player.sendMessage(Ollivanders2.chatColor + "Your soul is not yet so damaged to allow this.");
            return false;
        }

        // do they already have a horcrux?
        for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells().getActiveStationarySpells()) {
            if (stationarySpell.getSpellType() == O2StationarySpellType.HORCRUX && stationarySpell.getCasterID().equals(player.getUniqueId())) {
                player.sendMessage(Ollivanders2.chatColor + "You can have already divided your soul.");
                return false;
            }
        }

        // does the player have enough health to create a new horcrux?
        AttributeInstance healthAttribute = player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
        if (healthAttribute == null) {
            common.printDebugMessage("ET_INTERFICIAM_ANIMAM_LIGAVERIS.checkEffect: player health attribute is null", null, null, true);
            return false;
        }
        futureHealth = healthAttribute.getBaseValue() / 2;
        if (futureHealth < 2) {
            player.sendMessage(Ollivanders2.chatColor + "You are not strong enough to split your soul.");
            return false;
        }

        return true;
    }
}