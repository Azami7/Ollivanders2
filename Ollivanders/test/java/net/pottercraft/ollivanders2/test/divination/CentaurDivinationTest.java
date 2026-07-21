package net.pottercraft.ollivanders2.test.divination;

import net.pottercraft.ollivanders2.divination.CENTAUR_DIVINATION;
import net.pottercraft.ollivanders2.divination.O2Divination;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for {@link CENTAUR_DIVINATION}.
 *
 * @see DivinationTestSuper
 */
public class CentaurDivinationTest extends DivinationTestSuper {
    @Override
    O2Divination createDivination(@NotNull Player prophet, @NotNull Player target, int experience) {
        return new CENTAUR_DIVINATION(testPlugin, prophet, target, experience);
    }
}
