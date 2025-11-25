package net.pottercraft.ollivanders2.test.divination;

import net.pottercraft.ollivanders2.divination.O2Divination;
import net.pottercraft.ollivanders2.divination.TASSEOMANCY;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TasseomancyTest extends DivinationTestSuper {
    O2Divination createDivination(@NotNull Player prophet, @NotNull Player target, int experience) {
        return new TASSEOMANCY(testPlugin, prophet, target, experience);
    }
}
