package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Spell shield that surrounds the player with continuously pulsing campfire smoke and blocks incoming projectile
 * spells without an impact flair.
 *
 * @author Azami7
 * @see ShieldSpellEffect for the spell blocking mechanism and protection behavior
 */
public class FUMOS extends ShieldSpellEffect {
    /**
     * Constructor
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
