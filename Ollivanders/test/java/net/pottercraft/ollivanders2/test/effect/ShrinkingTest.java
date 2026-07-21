package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.SHRINKING;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link SHRINKING}.
 *
 * @author Azami7
 * @see PlayerChangeSizeTestSuper
 */
public class ShrinkingTest extends PlayerChangeSizeTestSuper {
    @Override
    SHRINKING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new SHRINKING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
