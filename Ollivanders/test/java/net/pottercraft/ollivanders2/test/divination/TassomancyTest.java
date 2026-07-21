package net.pottercraft.ollivanders2.test.divination;

import net.pottercraft.ollivanders2.divination.O2Divination;
import net.pottercraft.ollivanders2.divination.TASSOMANCY;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for {@link TASSOMANCY}.
 *
 * @see DivinationTestSuper
 */
public class TassomancyTest extends DivinationTestSuper {
    @Override
    O2Divination createDivination(@NotNull Player prophet, @NotNull Player target, int experience) {
        return new TASSOMANCY(testPlugin, prophet, target, experience);
    }
}
