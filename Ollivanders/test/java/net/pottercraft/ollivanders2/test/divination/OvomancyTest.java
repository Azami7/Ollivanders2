package net.pottercraft.ollivanders2.test.divination;

import net.pottercraft.ollivanders2.divination.O2Divination;
import net.pottercraft.ollivanders2.divination.OVOMANCY;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for {@link OVOMANCY}.
 *
 * @see DivinationTestSuper
 */
public class OvomancyTest extends DivinationTestSuper {
    @Override
    O2Divination createDivination(@NotNull Player prophet, @NotNull Player target, int experience) {
        return new OVOMANCY(testPlugin, prophet, target, experience);
    }
}
