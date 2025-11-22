package ollivanders.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.effect.AGGRESSION;
import org.bukkit.Location;
import org.bukkit.entity.Bee;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AggressionTest extends PermanentEffectTestSuper {
    @Override
    AGGRESSION createEffect(int durationInTicks, boolean isPermanent, @NotNull UUID targetID) {
        return new AGGRESSION(testPlugin, durationInTicks, true, targetID);
    }

    @Override
    void checkEffectTest() {
        Ollivanders2.debug = true;
        target.setLocation(new Location(testWorld, 500, 4, 0));

        // spawn some neutral entities near the target
        Bee bee1 = testWorld.spawn(new Location(testWorld, 502, 4, 0), Bee.class);
        double health = bee1.getHealth();

        // confirm the bees are not currently hostile
        assertFalse(EntityCommon.isHostile(bee1), "bee1 is already hostile"); //assume if 1 is not they are all not

        // add the aggression effect to the target player with max aggression level so we have 100% chance effects will happen
        AGGRESSION effect = createEffect(10, true, target.getUniqueId());
        effect.setAggressionLevel(10);
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        // run the server forward
        mockServer.getScheduler().performTicks(AGGRESSION.cooldownLimit);

        // target should have damaged a nearby entity and also provoked them to attack the target
        assertTrue(EntityCommon.isHostile(bee1), "bee1 is not targeting target player with aggression");
        assertTrue(bee1.getHealth() < health, "bee1 not damaged by aggression");
    }

    /**
     * doRemove() in AGGRESSION doesn't do anything
     */
    @Override
    void doRemoveTest() {}
}
