package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Variant of the FLYING effect optimized for broom-based flight with permanent duration and no smoke effects.
 *
 * <p>BROOM_FLYING is a specialized flight effect that extends the base {@link FLYING} effect with modifications
 * suitable for magical broom flight. Unlike regular FLYING effects, broom flight is always permanent and does not
 * produce smoke particle effects, resulting in cleaner visual flight for broom-mounted players.</p>
 *
 * <p>Differences from parent FLYING effect:</p>
 * <ul>
 * <li>Always permanent - duration parameter is ignored</li>
 * <li>Smoke effects disabled (doSmokeEffect = false) for cleaner visuals</li>
 * <li>Suitable for persistent broom-based flight mechanics</li>
 * </ul>
 *
 * @author Azami7
 * @see FLYING for the base flight effect mechanism
 */
public class BROOM_FLYING extends FLYING {
    /**
     * Constructor for creating a permanent broom flight effect.
     *
     * <p>Creates a specialized flight effect optimized for broom-based flight. The duration parameter is
     * accepted for API consistency with other effects but is ignored - broom flight is always permanent.
     * Smoke particle effects are disabled to provide cleaner visuals during broom flight.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    ignored - broom flight is always permanent
     * @param isPermanent ignored - broom flight is always permanent
     * @param pid         the unique ID of the player to give broom flight
     */
    public BROOM_FLYING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, true, pid);

        effectType = O2EffectType.FLYING;

        doSmokeEffect = false;
    }
}
