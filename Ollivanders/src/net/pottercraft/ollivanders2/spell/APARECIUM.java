package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.jetbrains.annotations.NotNull;

/**
 * The Revealing Charm - https://harrypotter.fandom.com/wiki/Revealing_Charm - causes any stationary spell objects to
 * flair with an intensity equal to your level.
 */
public final class APARECIUM extends O2Spell {
    //todo rework to match books - https://harrypotter.fandom.com/wiki/Revealing_Charm

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public APARECIUM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.APARECIUM;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Revealing Charm will reveal invisible ink and messages hidden by magical means. Simply tap a book or parchment with your wand and any hidden message will be revealed. This spell is more than sufficient to overcome the basic concealing charms and so is a favourite of parents and teachers alike.");
            add("The Revealing Charm");
        }};

        text = "Causes any area spells to reveal their borders.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public APARECIUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.APARECIUM;
        branch = O2MagicBranch.CHARMS;

        initSpell();
    }

    /**
     * If any stationary spells are at the location of the spell projectile, make them flair.
     */
    @Override
    protected void doCheckEffect() {
        List<O2StationarySpell> stationarySpells = Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location);

        if (!stationarySpells.isEmpty()) {
            common.printDebugMessage("Found " + stationarySpells.size() + " stationary spells", null, null, false);
            stationarySpells.getFirst().flair(10);

            kill();
            return;
        }

        // if the spell has hit a solid block, the projectile is stopped and won't go further so kill the spell
        if (hasHitTarget())
            kill();
    }
}