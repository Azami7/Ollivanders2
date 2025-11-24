package ollivanders.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.effect.AGGRESSION;
import org.bukkit.Location;
import org.bukkit.entity.Bee;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the AGGRESSION spell effect.
 *
 * <p>AggressionTest verifies that the AGGRESSION effect correctly implements spell mechanics for
 * causing nearby neutral entities to become hostile and attack the targeted player. The test creates
 * AGGRESSION effects as permanent effects and validates that aggression level properly controls the
 * effectiveness of the spell.</p>
 *
 * @see AGGRESSION for the effect being tested
 * @see PermanentEffectTestSuper for the testing framework
 */
public class AggressionTest extends PermanentEffectTestSuper {
    /**
     * Create an AGGRESSION effect for testing.
     *
     * <p>Creates a permanent AGGRESSION effect. The isPermanent parameter is ignored as
     * AGGRESSION effects are always permanent by design.</p>
     *
     * @param durationInTicks the duration parameter (unused for permanent effects)
     * @param isPermanent     ignored; AGGRESSION effects are always permanent
     * @return the newly created AGGRESSION effect instance
     */
    @Override
    AGGRESSION createEffect(int durationInTicks, boolean isPermanent) {
        return new AGGRESSION(testPlugin, durationInTicks, true, target.getUniqueId());
    }

    /**
     * Test AGGRESSION effect behavior on nearby neutral entities.
     *
     * <p>Verifies that the AGGRESSION effect correctly:
     * - Spawns nearby neutral entities (bees) in a known non-hostile state
     * - Applies the AGGRESSION effect to the target player with maximum aggression level
     * - Runs the server scheduler for one aggression cooldown cycle
     * - Confirms that nearby entities become hostile and target the player
     * - Confirms that nearby entities take damage from the aggression effect</p>
     */
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
        AGGRESSION effect = createEffect(10, true);
        effect.setAggressionLevel(10);
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        // run the server forward
        mockServer.getScheduler().performTicks(AGGRESSION.cooldownLimit);

        // target should have damaged a nearby entity and also provoked them to attack the target
        assertTrue(EntityCommon.isHostile(bee1), "bee1 is not targeting target player with aggression");
        assertTrue(bee1.getHealth() < health, "bee1 not damaged by aggression");
    }

    /**
     * Verify doRemove() cleanup for AGGRESSION effect.
     *
     * <p>The AGGRESSION effect does not perform any special cleanup when removed, so this test
     * is empty. All state changes (hostile entities, damage) are already persistent in the world
     * and do not need to be reverted.</p>
     */
    @Override
    void doRemoveTest() {}

    /**
     * Aggression has no event handlers
     */
    void eventHandlerTests() {}
}
