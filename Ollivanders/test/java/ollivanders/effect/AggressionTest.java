package ollivanders.effect;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.effect.AGGRESSION;
import net.pottercraft.ollivanders2.effect.O2Effect;
import org.bukkit.Location;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class AggressionTest extends EffectTestSuper {
    O2Effect createEffect(int durationInTicks, boolean isPermanent, @NotNull UUID targetID) {
        return new AGGRESSION(testPlugin, durationInTicks, true, targetID);
    }

    @Test
    void checkEffectTest() {
        target.setLocation(new Location(testWorld, 0, 4, 0));

        // spawn some neutral entities near the target
        Entity bee1 = testWorld.spawn(new Location(testWorld, 2, 4, 0), Bee.class);
        Entity bee2 = testWorld.spawn(new Location(testWorld, 2, 4, 2), Bee.class);
        Entity bee3 = testWorld.spawn(new Location(testWorld, 0, 4, 1), Bee.class);
        Entity bee4 = testWorld.spawn(new Location(testWorld, 2, 4, 1), Bee.class);

        // confirm the bees are not currently hostile
        assertFalse(EntityCommon.isHostile((Bee) bee1), "bee1 is already hostile");
    }
}
