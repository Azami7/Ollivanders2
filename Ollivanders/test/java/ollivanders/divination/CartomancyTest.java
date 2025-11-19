package ollivanders.divination;

import net.pottercraft.ollivanders2.divination.CARTOMANCY;
import net.pottercraft.ollivanders2.divination.O2Divination;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CartomancyTest extends DivinationTestSuper {
    O2Divination createDivination(@NotNull Player prophet, @NotNull Player target, int experience) {
        return new CARTOMANCY(testPlugin, prophet, target, experience);
    }
}
