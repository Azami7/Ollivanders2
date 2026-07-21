package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.LAUGHING;
import org.bukkit.entity.Player;

/**
 * Unit tests for {@link LAUGHING}, which shares BABBLING's gibberish-chat behavior.
 *
 * @see BabblingTest
 */
public class LaughingTest extends BabblingTest {
    @Override
    LAUGHING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new LAUGHING(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }
}
