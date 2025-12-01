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
 * Parent class for all shield effects that block incoming spells.
 *
 * <p>ShieldSpellEffect provides a protective barrier that blocks spell projectiles from hitting the protected
 * player. The shield operates within a specified radius around the player and prevents spells based on a
 * spell level comparison: the shield can only block spells up to one level higher than the shield spell itself.
 * For example, a shield at level OWL can block spells up to level NEWT (one level higher), but cannot block
 * level EXPERT spells.
 * </p>
 *
 * <p>Protection Mechanics:</p>
 * <ul>
 * <li>Radius: The shield protects in a sphere of radius around the player</li>
 * <li>Spell Level Check: Incoming spell level must be ≤ (shield level + 1) to be blocked</li>
 * <li>Entity Targeting: Prevents enemies from targeting the protected player via entity targeting events</li>
 * <li>Self Exemption: Does not block spells cast by the protected player themselves</li>
 * </ul>
 *
 * <p>Visual Effects:</p>
 * Subclasses can configure two types of visual particle effects:
 * <ul>
 * <li>Pulse Flair: A periodic visual effect displayed every 10 ticks (flairPulse, pulseFlairParticle)</li>
 * <li>Impact Flair: A visual effect displayed when a spell hits the shield boundary (flairOnSpellImpact, impactFlairParticle)</li>
 * </ul>
 *
 * @author Azami7
 */
public abstract class ShieldSpellEffect extends O2Effect {
    /**
     * The protection radius of this shield, measured in blocks from the player's location.
     *
     * <p>Spell projectiles that enter this radius around the player will be blocked (subject to spell level
     * restrictions). The radius is also used for particle effect positions.</p>
     */
    int radius = 3;

    /**
     * Whether this shield displays a periodic pulse visual effect.
     *
     * <p>If true, a particle effect (pulseFlairParticle) will be displayed every 10 ticks (0.5 seconds) at
     * the player's location. This provides continuous visual feedback that the shield is active.</p>
     */
    boolean flairPulse = false;

    /**
     * The particle type used for the periodic pulse visual effect.
     *
     * <p>This particle is displayed every 10 ticks at the player's location when flairPulse is true.
     * Common choices: Particle.CLOUD, Particle.ENCHANTMENT_TABLE, Particle.SPELL, etc.</p>
     */
    Particle pulseFlairParticle = Particle.CLOUD;

    /**
     * Whether this shield displays a visual effect when spell projectiles hit its boundary.
     *
     * <p>If true, a particle effect (impactFlairParticle) will be displayed when a spell projectile
     * collides with the shield boundary, providing immediate visual feedback of spell blocking.</p>
     */
    boolean flairOnSpellImpact = false;

    /**
     * The particle type used for the spell impact visual effect.
     *
     * <p>This particle is displayed at the player's location when a spell hits the shield boundary
     * and flairOnSpellImpact is true. Common choices: Particle.BLOCK_DUST, Particle.CRIT, etc.</p>
     */
    Particle impactFlairParticle = Particle.CLOUD;

    /**
     * Constructor for creating a spell shield effect.
     *
     * <p>Creates a protective shield around the target player. This constructor attempts to look up the
     * player entity and kills the effect if the player is not found online. Subclasses should set shield
     * configuration fields (radius, flairPulse, flairOnSpellImpact, particles) during initialization.</p>
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
     * Check the effect each game tick and apply visual effects.
     *
     * <p>This method executes once per tick and performs the following:</p>
     * <ol>
     * <li>Ages the effect by 1 tick (for non-permanent effects)</li>
     * <li>Returns early if the effect has been killed</li>
     * <li>If flairPulse is enabled and the duration is divisible by 10 (every 10 ticks / 0.5 seconds),
     *     displays the pulse flair particle effect</li>
     * </ol>
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

    /**
     * Perform cleanup when this shield effect is removed.
     *
     * <p>This method is called when the shield expires or is explicitly removed. The default implementation
     * does nothing, as shields do not require special cleanup (particles and targeting restrictions are
     * automatically handled). Subclasses can override to perform additional cleanup if needed.</p>
     */
    @Override
    public void doRemove() {
    }

    /**
     * Block spell projectiles that hit the shield boundary.
     *
     * <p>This method is called when a spell projectile moves and checks if it has entered the shield radius.
     * If the projectile is within the shield radius:
     * <ul>
     * <li>Performs a spell level check: the spell's level must be ≤ (shield's level + 1) to be blocked</li>
     * <li>If the spell qualifies, cancels the projectile movement to block it</li>
     * <li>If flairOnSpellImpact is enabled, displays the impact flair particle effect</li>
     * </ul>
     * Spells cast by the protected player are always exempted from blocking.
     *
     * @param event the spell projectile move event
     */
    void doOnOllivandersSpellProjectileMoveEvent(@NotNull OllivandersSpellProjectileMoveEvent event) {
        // don't stop this player's spells from going across the spell boundary
        if (event.getPlayer().getUniqueId().equals(targetID))
            return;

        Location projectileLocation = event.getTo();

        if (Ollivanders2Common.isInside(projectileLocation, target.getLocation(), radius)) {
            boolean canceled = true;

            // this spell can only protect against spells up to one level higher than this spell
            O2Spell spell = event.getSpell();
            if (spell.spellType.getLevel().ordinal() > effectType.getLevel().ordinal() + 1)
                canceled = false;

            event.setCancelled(canceled);
            if (flairOnSpellImpact)
                Ollivanders2Common.flair(target.getLocation(), radius, 20, impactFlairParticle);
        }
    }

    /**
     * Kill the shield when the protected player quits the server.
     *
     * <p>If the protected player logs out, the shield is automatically removed since it no longer has
     * a target player to protect. This prevents orphaned shield effects from remaining active.</p>
     *
     * @param event the player quit event
     */
    void doOnPlayerQuitEvent(@NotNull PlayerQuitEvent event) {
        if (event.getPlayer().getUniqueId().equals(targetID))
            kill();
    }

    /**
     * Prevent entity targeting of the protected player.
     *
     * <p>When an entity (mob) attempts to target the protected player, this method cancels the
     * EntityTargetEvent, preventing the entity from acquiring the player as a target. This provides
     * protection against mob aggression in addition to spell blocking.</p>
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
