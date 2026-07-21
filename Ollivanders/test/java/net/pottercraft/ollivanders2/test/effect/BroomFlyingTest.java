package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.effect.BROOM_FLYING;
import net.pottercraft.ollivanders2.effect.O2Effect;
import org.bukkit.entity.Player;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link BROOM_FLYING}, which enables flight while riding a broom and is always permanent unlike
 * regular {@code FLYING}.
 *
 * @see FlyingTest
 */
public class BroomFlyingTest extends FlyingTest {
    /**
     * {@inheritDoc}
     *
     * <p>The isPermanent parameter is ignored; BROOM_FLYING is always permanent.</p>
     */
    @Override
    BROOM_FLYING createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new BROOM_FLYING(testPlugin, durationInTicks, true, target.getUniqueId());
    }

    /**
     * BROOM_FLYING is always permanent: {@link O2Effect#setPermanent(boolean)} cannot make it non-permanent.
     */
    @Override
    void isPermanentTest() {
        O2Effect effect = createEffect(mockServer.addPlayer(), 10, false);
        assertTrue(effect.isPermanent(), "Effect not permanent when created.");

        effect.setPermanent(false);
        assertTrue(effect.isPermanent(), "Permanent effect not permanent after effect.setPermanent(false)");
    }
}
