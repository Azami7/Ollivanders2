package ollivanders.divination;

import net.pottercraft.ollivanders2.divination.CRYSTAL_BALL;
import net.pottercraft.ollivanders2.divination.O2Divination;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CrystalBallTest extends DivinationTestSuper {
    O2Divination createDivination(@NotNull Player prophet, @NotNull Player target, int experience) {
        return new CRYSTAL_BALL(testPlugin, prophet, target, experience);
    }
}
