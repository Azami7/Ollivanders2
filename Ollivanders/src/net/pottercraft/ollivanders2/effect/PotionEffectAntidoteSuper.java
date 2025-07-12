package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Antidotes to standard MC potion effects.
 *
 * @author Azami7
 */
public abstract class PotionEffectAntidoteSuper extends O2Effect {
    /**
     * the potion effect type this effect is the antidote for
     */
    PotionEffectType potionEffectType = PotionEffectType.GLOWING;

    /**
     * strength of this antidote - strength of 1 will cancel the potion effect entirely, 0.5 reduced by half-time, 0.25 reduced by quarter time, etc.
     */
    double strength = 0.25;

    /**
     * Constructor
     *
     * @param plugin a reference to the plugin for logging
     * @param duration the duration of the effect
     * @param pid    the player to target
     */
    public PotionEffectAntidoteSuper(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        // hard-code duration because these are instant effects
        super(plugin, duration, pid);
    }

    /**
     * Do the effect immediately on the target and kill.
     */
    @Override
    public void checkEffect() {
        Player target = p.getServer().getPlayer(targetID);

        if (target != null) {
            PotionEffect targetEffect = target.getPotionEffect(potionEffectType);

            if (targetEffect != null) {
                target.removePotionEffect(potionEffectType);

                if (strength < 1) {
                    int newDuration = (int) (targetEffect.getDuration() * strength);

                    target.addPotionEffect(new PotionEffect(potionEffectType, newDuration, targetEffect.getAmplifier()));
                }
            }
        }

        kill();
    }
}
