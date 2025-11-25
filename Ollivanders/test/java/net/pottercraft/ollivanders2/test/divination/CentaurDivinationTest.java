package net.pottercraft.ollivanders2.test.divination;

import net.pottercraft.ollivanders2.divination.CENTAUR_DIVINATION;
import net.pottercraft.ollivanders2.divination.O2Divination;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CentaurDivinationTest extends DivinationTestSuper {
    O2Divination createDivination(@NotNull Player prophet, @NotNull Player target, int experience) {
        return new CENTAUR_DIVINATION(testPlugin, prophet, target, experience);
    }
}
