package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Parent class for all shield effects that block incoming spells within a radius of the protected player.
 * <p>
 * A shield blocks spell projectiles up to one level higher than the shield's own level (an OWL shield blocks up
 * to NEWT), exempts spells cast by the protected player, and cancels mob targeting of that player. Subclasses
 * may enable a periodic pulse particle and/or an impact particle for visual feedback.
 * </p>
 *
 * @author Azami7
 */
public abstract class ShieldSpellEffect extends O2Effect {
    /**
     * The protection radius in blocks from the player's location; also used to position particle effects.
     */
    int radius = 3;

    /**
     * Whether to emit {@link #pulseFlairParticle} every 10 ticks while the shield is active.
     */
    boolean flairPulse = false;

    /**
     * The particle emitted for the periodic pulse when {@link #flairPulse} is true.
     */
    Particle pulseFlairParticle = Particle.CLOUD;

    /**
     * Whether to emit {@link #impactFlairParticle} when a spell is blocked at the shield boundary.
     */
    boolean flairOnSpellImpact = false;

    /**
     * The particle emitted on spell impact when {@link #flairOnSpellImpact} is true.
     */
    Particle impactFlairParticle = Particle.CLOUD;

    /**
     * Constructor. Subclasses set the shield configuration fields (radius, flair flags, particles) after calling.
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the shield in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to protect
     */
    public ShieldSpellEffect(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);
    }

    /**
     * Age the shield and, when pulse flair is enabled, emit the pulse particle every 10 ticks.
     */
    @Override
    public void checkEffect() {
        if (!permanent) {
            age(1);
        }

        if (isKilled())
            return;

        // flair
        if (flairPulse && (duration % 10) == 0) {
            Ollivanders2Common.flair(target.getLocation(), radius, 10, pulseFlairParticle);
        }
    }

    @Override
    public void doRemove() {
    }

    /**
     * Get the radius for this shield spell effect.
     *
     * @return the radius in blocks
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Block a spell projectile that enters the shield radius, unless it is above (shield level + 1) or was cast by
     * the protected player. On a block, emits the impact particle if {@link #flairOnSpellImpact} is set.
     *
     * @param event the spell projectile move event
     */
    void doOnOllivandersSpellProjectileMoveEvent(@NotNull OllivandersSpellProjectileMoveEvent event) {
        // don't stop this player's spells from going across the spell boundary
        if (event.getPlayer().getUniqueId().equals(targetID))
            return;

        Location projectileToLocation = event.getTo();

        if (Ollivanders2Common.isInside(projectileToLocation, target.getLocation(), radius)) {
            boolean canceled = true;

            // this spell can only protect against spells up to one level higher than this spell
            O2Spell spell = event.getSpell();
            if (spell.getSpellType().getLevel().ordinal() > effectType.getLevel().ordinal() + 1)
                canceled = false;

            event.setCancelled(canceled);
            if (flairOnSpellImpact)
                Ollivanders2Common.flair(target.getLocation(), radius, 20, impactFlairParticle);
        }
    }

    /**
     * Kill the shield when the protected player quits the server.
     *
     * @param event the player quit event
     */
    void doOnPlayerQuitEvent(@NotNull PlayerQuitEvent event) {
        if (event.getPlayer().getUniqueId().equals(targetID))
            kill();
    }

    /**
     * Cancel any mob's attempt to target the protected player.
     *
     * @param event the entity target event
     */
    void doOnEntityTargetEvent(@NotNull EntityTargetEvent event) {
        if (event.getTarget() == null)
            return;

        if (event.getTarget().getUniqueId().equals(targetID))
            event.setCancelled(true);
    }
}
