package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.ShieldSpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.jetbrains.annotations.NotNull;

/**
 * Shield penetration charm that destroys nearby shield stationary spells.
 * <p>
 * When the projectile reaches a location containing stationary spells, it destroys any {@link ShieldSpell} there whose
 * magic level is at or below this spell's level, then resolves. Stronger shields are left intact, and non-shield
 * stationary spells (floo networks, vanishing cabinets, etc.) cannot be destroyed at all. The number of stationary
 * spells a single cast will target is determined by the caster's skill (see {@link #doInitSpell()}).
 * </p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Shield_penetration_spell">Harry Potter Wiki - Shield penetration spell</a>
 * @see ShieldSpell
 */
public final class SCUTO_CONTERAM extends O2Spell {
    /**
     * The number of shield spells that can be targeted by this spell.
     */
    private int targetsRemaining = 1;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public SCUTO_CONTERAM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.SCUTO_CONTERAM;
        branch = O2MagicBranch.CHARMS;

        text = "Scuto conteram will remove some stationary spells.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public SCUTO_CONTERAM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.SCUTO_CONTERAM;
        branch = O2MagicBranch.CHARMS;
        initSpell();
    }

    /**
     * Set the number of stationary spells this cast will target, based on the caster's skill.
     * <p>
     * The number of targets scales with the caster's {@code usesModifier} and is floored at one, so even an unskilled
     * caster can target a single shield.
     * </p>
     */
    @Override
    void doInitSpell() {
        targetsRemaining = (int) usesModifier / 20;
        if (targetsRemaining < 1)
            targetsRemaining = 1;
    }

    /**
     * Destroy eligible shield spells at the projectile's location, then resolve.
     * <p>
     * Each stationary spell at the current location consumes one of the cast's remaining targets, whether or not it is
     * destroyed: a {@link ShieldSpell} at or below this spell's magic level is destroyed, while stronger shields and
     * non-shield stationary spells (floo networks, vanishing cabinets, etc.) are left intact. The spell resolves at the
     * first location where it contacts a stationary spell, so it does not continue on the next tick once it has acted.
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitBlock())
            kill();

        for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location)) {
            if (stationarySpell instanceof ShieldSpell && (stationarySpell.getSpellType().getLevel().ordinal() <= spellType.getLevel().ordinal()))
                stationarySpell.kill();

            targetsRemaining = targetsRemaining - 1;
            kill(); // so the spell does not repeat next tick

            if (targetsRemaining <= 0)
                break;
        }
    }

    /**
     * Get the number of stationary spells this cast can still target.
     *
     * @return the remaining target count
     */
    public int getTargetsRemaining() {
        return targetsRemaining;
    }
}