package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.effect.AGGRESSION;
import org.bukkit.Location;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Player;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AGGRESSION}.
 *
 * @see PermanentEffectTestSuper
 */
public class AggressionTest extends PermanentEffectTestSuper {
    @Override
    AGGRESSION createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new AGGRESSION(testPlugin, durationInTicks, true, target.getUniqueId());
    }

    /**
     * At maximum aggression level, a nearby neutral entity becomes hostile toward the target and takes damage.
     */
    @Override
    void checkEffectTest() {
        PlayerMock target = mockServer.addPlayer();

        target.setLocation(new Location(testWorld, 500, 4, 0));

        // spawn some neutral entities near the target
        Bee bee1 = testWorld.spawn(new Location(testWorld, 502, 4, 0), Bee.class);
        double health = bee1.getHealth();

        // confirm the bees are not currently hostile
        assertFalse(EntityCommon.isHostile(bee1), "bee1 is already hostile"); //assume if 1 is not they are all not

        // add the aggression effect to the target player with max aggression level so we have 100% chance effects will happen
        AGGRESSION effect = createEffect(target, 10, true);
        effect.setAggressionLevel(10);
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        // run the server forward
        mockServer.getScheduler().performTicks(AGGRESSION.cooldownLimit);

        // target should have damaged a nearby entity and also provoked them to attack the target
        assertTrue(EntityCommon.isHostile(bee1), "bee1 is not targeting target player with aggression");
        assertTrue(bee1.getHealth() < health, "bee1 not damaged by aggression");
    }

    // AGGRESSION has no doRemove() cleanup to verify
    @Override
    void doRemoveTest() {}

    /**
     * Aggression has no event handlers
     */
    @Override
    void eventHandlerTests() {}
}
