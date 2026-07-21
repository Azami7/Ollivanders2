package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.EQUIFORS_MAXIMA;
import net.pottercraft.ollivanders2.effect.PlayerTransformBase;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link EQUIFORS_MAXIMA} (horse transformation).
 *
 * @see PlayerTransformBaseTest
 */
public class EquiforsMaximaTest extends PlayerTransformBaseTest {
    /**
     * {@inheritDoc}
     */
    @Override
    PlayerTransformBase createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new EQUIFORS_MAXIMA(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
