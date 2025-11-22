package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Enhanced spell shield that increases the protection radius of smoke barrier.
 *
 * <p>FUMOS_DUO is a stronger variant of the {@link FUMOS} smoke shield spell that
 * expands the magical protection radius. While it inherits all smoke shield mechanics
 * from the parent FUMOS class (blocking projectile spells, displaying campfire smoke
 * particles), this version increases the effective radius from the default value to 5
 * blocks, providing broader protection coverage for the affected player.</p>
 *
 * <p>Shield Mechanism (inherited from FUMOS/SpellShieldEffect):</p>
 * <ul>
 * <li>Blocks incoming projectile spells from hitting the player</li>
 * <li>Only blocks spells up to 1 level higher than the shield's magic level</li>
 * <li>Prevents entity targeting of the shielded player</li>
 * <li>Pulses campfire smoke particles continuously around the player</li>
 * <li>Increased protection radius of 5 blocks (FUMOS_DUO variant)</li>
 * <li>No impact flair displayed when spells are blocked</li>
 * </ul>
 *
 * @author Azami7
 * @see FUMOS for the base smoke shield effect
 * @see SpellShieldEffect for the spell blocking mechanism and protection behavior
 */
public class FUMOS_DUO extends FUMOS {
    /**
     * Constructor for creating an enhanced smoke shield with increased protection radius.
     *
     * <p>Creates a protective spell shield effect with an expanded protection radius.
     * This variant increases the shield's effective radius to 5 blocks, providing broader
     * magical protection compared to the standard FUMOS effect. All smoke shield particle
     * effects and spell blocking mechanics are inherited from the parent FUMOS class.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the shield effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to shield with enhanced smoke protection
     */
    public FUMOS_DUO(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.FUMOS_DUO;
        radius = 5;
    }
}
