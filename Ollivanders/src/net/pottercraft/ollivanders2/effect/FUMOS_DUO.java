package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Stronger smoke shield: a {@link FUMOS} variant that widens the protection radius to 5 blocks.
 *
 * @author Azami7
 * @see FUMOS
 * @see ShieldSpellEffect
 */
public class FUMOS_DUO extends FUMOS {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the shield effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to shield with enhanced smoke protection
     */
    public FUMOS_DUO(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.FUMOS_DUO;
        checkDurationBounds();

        radius = 5;
    }
}
