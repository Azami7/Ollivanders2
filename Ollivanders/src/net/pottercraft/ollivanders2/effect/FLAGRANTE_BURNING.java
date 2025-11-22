package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Permanent burning effect inflicted by a flagrante-cursed item.
 *
 * <p>FLAGRANTE_BURNING is a specialized variant of the {@link BURNING} effect that is applied by cursed
 * items bearing the Flagrante curse. Unlike regular burning effects which are temporary, flagrante burning
 * is always permanent and cannot be modified via setPermanent(). The effect applies periodic fire damage
 * at 3-second intervals with the same mechanisms as the parent BURNING class: damage clamping to 0.5-10,
 * death prevention, and visual fire effects (smoke and hurt sound).</p>
 *
 * <p>Differences from parent BURNING effect:</p>
 * <ul>
 * <li>Always permanent - duration parameter is ignored</li>
 * <li>Applied by flagrante-cursed items (e.g., cursed gloves, cursed book)</li>
 * <li>Cannot be modified to temporary status via setPermanent()</li>
 * <li>Inherits all other burning mechanics from parent BURNING class</li>
 * </ul>
 *
 * @author Azami7
 * @see BURNING for the base burning effect mechanism
 */
public class FLAGRANTE_BURNING extends BURNING {
    /**
     * Constructor for creating a permanent flagrante burning effect.
     *
     * <p>Creates a permanent burning effect applied by a flagrante-cursed item. The duration parameter is
     * accepted for API consistency with other effects but is ignored - flagrante burning is always permanent.
     * All burning mechanics (damage calculation, visual effects, etc.) are inherited from the parent BURNING
     * class and applied with permanent status.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    ignored - flagrante burning is always permanent
     * @param isPermanent ignored - flagrante burning is always permanent
     * @param pid         the unique ID of the player affected by the cursed item
     */
    public FLAGRANTE_BURNING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, true, pid);
    }
}
