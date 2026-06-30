package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.ShieldSpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.jetbrains.annotations.NotNull;

/**
 * Lengthens the duration of the caster's shield spells at their location.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Fianto_Duri">Harry Potter Wiki - Fianto Duri</a>
 */
public final class FIANTO_DURI extends O2Spell {
    /**
     * The maximum duration increase this spell can apply (1 day).
     */
    private static final int maxIncrease = Ollivanders2Common.ticksPerHour * 24; // 1 day

    /**
     * Duration increase this spell makes
     */
    private int increase;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public FIANTO_DURI(Ollivanders2 plugin) {
        super(plugin);
        spellType = O2SpellType.FIANTO_DURI;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("\"Protego Maxima. Fianto Duri. Repello Inimicum.\" - Filius Flitwick");
        }};

        text = "Fianto Duri increases the duration of your shield spells at this location.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public FIANTO_DURI(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.FIANTO_DURI;
        branch = O2MagicBranch.CHARMS;

        noProjectile = true;
        failureMessage = "Unable to find any shield spells cast by you in this location.";

        initSpell();
    }

    /**
     * Set the duration increase based on the caster's skill, capped at {@link #maxIncrease}.
     */
    @Override
    void doInitSpell() {
        increase = (int) usesModifier * Ollivanders2Common.ticksPerMinute * 2;

        if (increase > maxIncrease)
            increase = maxIncrease;
    }

    /**
     * Increase the duration of the caster's own shield spells at this location.
     * <p>
     * Finds every {@link ShieldSpell} at the spell's location that was cast by this caster and extends each by
     * {@link #increase} ticks. If no eligible shield is found, the failure message is sent. The spell resolves on the
     * first tick because it is non-projectile.
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        List<O2StationarySpell> shieldSpells = new ArrayList<>();

        for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location)) {
            // only shield spells cast by this caster are extended
            if ((stationarySpell instanceof ShieldSpell) && stationarySpell.getCasterID().equals(caster.getUniqueId()))
                shieldSpells.add(stationarySpell);
        }

        // if we found a target stationary spells, increase their durations
        if (shieldSpells.isEmpty()) {
            sendFailureMessage();
            kill();
            return;
        }

        for (O2StationarySpell spell : shieldSpells) {
            spell.increaseDuration(increase);
            spell.flair(10);
        }

        kill();
    }
}