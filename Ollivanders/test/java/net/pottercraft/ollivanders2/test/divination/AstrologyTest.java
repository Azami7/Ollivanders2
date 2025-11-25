package net.pottercraft.ollivanders2.test.divination;

import net.pottercraft.ollivanders2.divination.ASTROLOGY;
import net.pottercraft.ollivanders2.divination.O2Divination;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AstrologyTest extends DivinationTestSuper {
    O2Divination createDivination(@NotNull Player prophet, @NotNull Player target, int experience) {
        return new ASTROLOGY(testPlugin, prophet, target, experience);
    }
}
