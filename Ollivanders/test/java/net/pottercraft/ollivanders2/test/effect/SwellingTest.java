package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.SWELLING;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link SWELLING}.
 *
 * @author Azami7
 * @see PlayerChangeSizeTestSuper
 */
public class SwellingTest extends PlayerChangeSizeTestSuper {
    @Override
    SWELLING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new SWELLING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
