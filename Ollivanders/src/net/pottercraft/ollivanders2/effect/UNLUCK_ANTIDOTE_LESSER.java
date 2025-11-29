package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Reduced-potency antidote to the UNLUCK potion effect.
 *
 * <p>UNLUCK_ANTIDOTE_LESSER is a partial antidote that counteracts the UNLUCK potion effect with
 * reduced potency. As an antidote with strength 0.25, it reduces the duration of any active UNLUCK
 * effects on the player by 25%. This allows for incomplete recovery from unluck-based curses,
 * leaving some residual bad fortune that requires additional healing or stronger antidotes to
 * fully remove.</p>
 *
 * @author Azami7
 * @see PotionEffectAntidoteSuper for the antidote strength mechanism
 * @see UNLUCK for the unluck potion effect this antidote counteracts
 */
public class UNLUCK_ANTIDOTE_LESSER extends PotionEffectAntidoteSuper {
    /**
     * Constructor for creating a reduced-potency unluck antidote.
     *
     * <p>Creates an antidote effect that reduces the duration of active UNLUCK potion effects by
     * 25% (strength 0.25). The duration parameter is accepted for API consistency with other effects
     * but is not used by antidote effects.</p>
     *
     * @param plugin      a reference to the plugin for logging
     * @param duration    ignored - antidotes apply immediately and are resolved
     * @param isPermanent ignored - antidotes are immediately applied and resolved
     * @param pid         the unique ID of the player to treat with the antidote
     */
    public UNLUCK_ANTIDOTE_LESSER(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.UNLUCK_ANTIDOTE_LESSER;
        checkDurationBounds();

        potionEffectType = PotionEffectType.UNLUCK;
        strength = 0.25;
    }

    /**
     * Perform cleanup when the antidote effect is removed.
     *
     * <p>The default implementation does nothing, as antidote effects have no persistent state to
     * clean up. When removed, the player's remaining UNLUCK effect duration remains at its reduced
     * value.</p>
     */
    @Override
    public void doRemove() {
    }
}
