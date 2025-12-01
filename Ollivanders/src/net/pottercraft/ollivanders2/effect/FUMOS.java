package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Spell shield effect that surrounds the player with smoke, blocking incoming projectile spells.
 *
 * <p>FUMOS is a specialized spell shield that protects the affected player from projectile spell attacks by
 * creating a smoke barrier. Unlike regular shields, FUMOS displays continuous smoke particle effects (campfire smoke)
 * that pulse around the player, creating a visual representation of the magical protection. When projectile spells
 * are blocked by this shield, they are intercepted without triggering impact effects.</p>
 *
 * <p>Shield Mechanism (inherited from SpellShieldEffect):</p>
 * <ul>
 * <li>Blocks incoming projectile spells from hitting the player</li>
 * <li>Only blocks spells up to 1 level higher than the shield's magic level</li>
 * <li>Prevents entity targeting of the shielded player</li>
 * <li>Pulses campfire smoke particles continuously around the player</li>
 * <li>No impact flair displayed when spells are blocked</li>
 * </ul>
 *
 * @author Azami7
 * @see ShieldSpellEffect for the spell blocking mechanism and protection behavior
 */
public class FUMOS extends ShieldSpellEffect {
    /**
     * Constructor for creating a smoke shield spell barrier effect.
     *
     * <p>Creates a protective spell shield effect that surrounds the player with smoke. The shield blocks
     * incoming projectile spells while displaying continuous campfire smoke particle effects around the player.
     * Configuration:</p>
     * <ul>
     * <li>flairPulse = true: Particles continuously pulse around the protected player</li>
     * <li>pulseFlairParticle = CAMPFIRE_COSY_SMOKE: Cozy campfire smoke particles for the visual effect</li>
     * <li>flairOnSpellImpact = false: No additional impact particles when spells are blocked</li>
     * </ul>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the shield effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to shield with smoke protection
     */
    public FUMOS(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.FUMOS;
        checkDurationBounds();

        flairPulse = true;
        pulseFlairParticle = Particle.CAMPFIRE_COSY_SMOKE;
        flairOnSpellImpact = false;
    }
}
