package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Base class for effects that hand off to a Minecraft potion effect: it applies the potion on the first tick and
 * immediately kills itself. Subclasses set {@link #potionEffectType} and {@link #strength} before the effect runs.
 *
 * @author Azami7
 */
public abstract class PotionEffect extends O2Effect {
    /**
     * The potion amplifier level; 1 is normal strength, higher values are more potent.
     */
    int strength = 1;

    /**
     * The Minecraft potion effect this effect applies. Subclasses set this to the desired type.
     */
    PotionEffectType potionEffectType = PotionEffectType.GLOWING;

    /**
     * Constructor
     *
     * @param plugin      a reference to the plugin
     * @param duration    the duration in ticks, snapped to min of 2 minutes, max of 5 minutes
     * @param isPermanent ignored - potion effects cannot be permanent
     * @param pid         the unique ID of the target player
     */
    public PotionEffect(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, false, pid);
    }

    /**
     * Applies the configured potion effect to the target for this effect's remaining duration, then kills this effect.
     */
    @Override
    public void checkEffect() {
        target.addPotionEffect(new org.bukkit.potion.PotionEffect(potionEffectType, duration, strength));

        kill();
    }

    /**
     * Set the potion amplifier level.
     *
     * @param s the amplifier level; 1 is normal strength, higher is more potent
     */
    public void setStrength(int s) {
        strength = s;
    }

    /**
     * Get the strength for this potion effect
     *
     * @return the potion strength
     */
    public int getStrength() {
        return strength;
    }

    /**
     * Get the PotionEffectType this PotionEffect effect adds
     *
     * @return the PotionEffectType
     */
    public PotionEffectType getPotionEffectType() {
        return potionEffectType;
    }

    /**
     * Potion effects cannot ever be permanent
     *
     * @param perm ignored - potion effects are always temporary
     */
    @Override
    public void setPermanent(boolean perm) {
    }
}
