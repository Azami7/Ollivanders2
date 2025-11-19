package ollivanders.divination;

import net.pottercraft.ollivanders2.divination.CARTOMANCY_TAROT;
import net.pottercraft.ollivanders2.divination.O2Divination;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CartomancyTarotTest extends DivinationTestSuper {
    O2Divination createDivination(@NotNull Player prophet, @NotNull Player target, int experience) {
        return new CARTOMANCY_TAROT(testPlugin, prophet, target, experience);
    }
}
