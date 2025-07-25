package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Causes the player to be surrounded in smoke and not targetable by projectile spells
 *
 * @author Azami7
 * @since 2.21
 */
public class FUMOS extends SpellShieldEffect {
    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public FUMOS(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.FUMOS;
        flairPulse = true;
        pulseFlairParticle = Particle.CAMPFIRE_COSY_SMOKE;
        flairOnSpellImpact = false;
    }
}
