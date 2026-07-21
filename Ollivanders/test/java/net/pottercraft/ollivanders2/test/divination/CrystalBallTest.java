package net.pottercraft.ollivanders2.test.divination;

import net.pottercraft.ollivanders2.divination.CRYSTAL_BALL;
import net.pottercraft.ollivanders2.divination.O2Divination;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for {@link CRYSTAL_BALL}.
 *
 * @see DivinationTestSuper
 */
public class CrystalBallTest extends DivinationTestSuper {
    @Override
    O2Divination createDivination(@NotNull Player prophet, @NotNull Player target, int experience) {
        return new CRYSTAL_BALL(testPlugin, prophet, target, experience);
    }
}
