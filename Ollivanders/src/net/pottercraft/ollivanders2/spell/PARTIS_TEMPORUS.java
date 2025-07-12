package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Temporarily disables a stationary spell's effects if it is your spell.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Partis_Temporus">https://harrypotter.fandom.com/wiki/Partis_Temporus</a>
 */
public final class PARTIS_TEMPORUS extends O2Spell {
    // todo rework to actually match the books - part fire or water - https://harrypotter.fandom.com/wiki/Partis_Temporus
    /**
     * The duration of the spell effect
     */
    private int duration;

    /**
     * The minimum time this will disable the stationary spell
     */
    private final static int minDurationInSeconds = 15;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PARTIS_TEMPORUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.PARTIS_TEMPORUS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Parting Charm");
        }};

        text = "Partis temporus, if cast at a stationary spell that you have cast, will cause that stationary spell's effects to stop for a short time.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PARTIS_TEMPORUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PARTIS_TEMPORUS;
        branch = O2MagicBranch.CHARMS;

        initSpell();
    }

    /**
     * Set the duration based on the caster's skill
     */
    @Override
    void doInitSpell() {
        int durationInSeconds = (int) usesModifier;
        if (durationInSeconds < minDurationInSeconds)
            durationInSeconds = minDurationInSeconds;

        duration = durationInSeconds * Ollivanders2Common.ticksPerSecond;
    }

    /**
     * Find any nearby stationary spells and, if they were cast by the caster, disable them
     */
    @Override
    protected void doCheckEffect() {
        for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location)) {
            if (stationarySpell.isKilled())
                continue;

            if (stationarySpell.getCasterID() == player.getUniqueId()) {
                stopProjectile();

                stationarySpell.setActive(false);
                stationarySpell.flair(10);

                if (duration > stationarySpell.getDurationRemaining())
                    duration = stationarySpell.getDurationRemaining();
            }
        }

        if (hasHitTarget()) {
            duration = duration - 1;

            if (duration <= 0)
                kill();
        }
    }

    /**
     * Re-activate the spells
     */
    @Override
    protected void revert() {
        for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location)) {
            if (stationarySpell.getCasterID() == player.getUniqueId()) {
                if (!stationarySpell.isKilled()) {
                    stationarySpell.setActive(true);
                    stationarySpell.flair(10);
                }
            }
        }
    }
}