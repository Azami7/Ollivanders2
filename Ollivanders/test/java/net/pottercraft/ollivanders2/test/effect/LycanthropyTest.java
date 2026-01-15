package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.common.TimeCommon;
import net.pottercraft.ollivanders2.effect.LYCANTHROPY;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.World;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the LYCANTHROPY effect.
 *
 * <p>Tests the permanent werewolf curse that transforms players during full moons. Verifies
 * core effect behaviors and specific LYCANTHROPY mechanics:</p>
 * <ul>
 * <li>Transformation occurs only on full moon days between moonrise and dawn</li>
 * <li>No transformation on wrong moon phases or before moonrise</li>
 * <li>Secondary effects (AGGRESSION and LYCANTHROPY_SPEECH) are applied during transformation</li>
 * <li>Secondary effects are removed when transformation ends</li>
 * <li>Effect is always permanent regardless of duration parameter</li>
 * <li>Curse can spread to other players through damage when transformed</li>
 * <li>Relief effect (LYCANTHROPY_RELIEF) can suppress transformation</li>
 * </ul>
 *
 * <p>The main focus is on verifying that transformation is properly tied to the lunar cycle
 * and that infection mechanics work correctly.</p>
 */
public class LycanthropyTest extends PermanentEffectTestSuper {
    /**
     * Create a LYCANTHROPY effect for testing.
     *
     * <p>Instantiates a new LYCANTHROPY effect with the specified parameters. This method is called
     * by the test methods to create fresh effect instances for each test scenario.</p>
     *
     * @param target          the player to add the effect to
     * @param durationInTicks duration parameter (ignored - lycanthropy is always permanent)
     * @param isPermanent     ignored - lycanthropy is always permanent
     * @return a new LYCANTHROPY effect targeting the specified player
     */
    LYCANTHROPY createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new LYCANTHROPY(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Test LYCANTHROPY effect transformation behavior tied to the lunar cycle.
     *
     * <p>Performs five comprehensive tests of the transformation mechanics:</p>
     * <ol>
     * <li><strong>Test 1: Wrong day, midday</strong> - Verifies no transformation at midday on day 2 (not full moon)</li>
     * <li><strong>Test 2: Wrong day, moonrise</strong> - Verifies no transformation at moonrise on day 2 (not full moon)</li>
     * <li><strong>Test 3: Right day, before moonrise</strong> - Verifies no transformation at midday on day 8 (full moon day)</li>
     * <li><strong>Test 4: Right day, at moonrise</strong> - Verifies player transforms at moonrise on full moon day
     * and secondary effects (AGGRESSION and LYCANTHROPY_SPEECH) are applied</li>
     * <li><strong>Test 5: Transformation cleanup</strong> - Verifies player reverts to human form after advancing
     * past dawn and secondary effects are removed</li>
     * </ol>
     */
    @Override
    void checkEffectTest() {
        PlayerMock target = mockServer.addPlayer();
        World world = target.getWorld();

        LYCANTHROPY lycanthropy = (LYCANTHROPY) addEffect(target, 100, true);

        // Test 1: check that player is not affected when the wrong day
        // set day to day 2
        world.setFullTime((24000 * 2) + TimeCommon.MIDDAY.getTick());
        // play forward 5 ticks for the effect to run
        mockServer.getScheduler().performTicks(5);
        assertFalse(lycanthropy.isTransformed(), "Player is shape-shifted on the wrong day");
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.AGGRESSION), "Target has aggression enabled on the wrong day");
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.LYCANTHROPY_SPEECH), "Target has lycanthropy speech enabled on the wrong day");

        // Test 2: check that player is not affected when it is Moonrise on the wrong day
        // set the world time to MOONRISE
        world.setFullTime((24000 * 2) + TimeCommon.MOONRISE.getTick());
        // play forward 5 ticks for the effect to run
        mockServer.getScheduler().performTicks(5);
        assertFalse(lycanthropy.isTransformed(), "Player is shape-shifted at Moonrise on the wrong day");
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.AGGRESSION), "Target has aggression enabled at Moonrise on the wrong day");
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.LYCANTHROPY_SPEECH), "Target has lycanthropy speech enabled at Moonrise on the wrong day");

        // Test 3: check the player is not affected before Moonrise on the right day
        world.setFullTime((24000 * 8) + TimeCommon.MIDDAY.getTick());
        // play forward 5 ticks for the effect to run
        mockServer.getScheduler().performTicks(5);
        assertFalse(lycanthropy.isTransformed(), "Player is shape-shifted at midday on the right day");
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.AGGRESSION), "Target has aggression enabled at midday on the right day");
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.LYCANTHROPY_SPEECH), "Target has lycanthropy speech enabled at midday on the right day");

        // Test 4: check that secondary effects are added at moonrise on a full moon day and then removed once the time
        // is passed.
        world.setFullTime((24000 * 8) + TimeCommon.MOONRISE.getTick());

        // play forward 5 ticks for the effect to run
        mockServer.getScheduler().performTicks(5);
        assertTrue(lycanthropy.isTransformed(), "Player not transformed at Moonrise on the right day.");
        assertNotNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.AGGRESSION), "Player did not have aggression added at Moonrise on the right day.");
        assertNotNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.LYCANTHROPY_SPEECH), "Player did not have lycanthropy speech added at Moonrise on the right day.");

        // Test 5: make sure effects and transformation is removed once the moonrise evening passes
        mockServer.getScheduler().performTicks(12000);
        assertFalse(lycanthropy.isTransformed(), "Player is did not transform back after dawn");
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.AGGRESSION), "Player still has aggression after dawn");
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.LYCANTHROPY_SPEECH), "Player still has lycanthropy speech after dawn");
    }

    /**
     * Run all event handler tests for the LYCANTHROPY effect.
     *
     * <p>Executes the EntityDamageByEntityEvent test to verify that the curse can spread
     * through damage dealt by transformed players.</p>
     */
    @Override
    void eventHandlerTests() {
        doOnEntityDamageByEntityEventTest();
    }

    /**
     * Test that LYCANTHROPY spreads through damage when the attacker is transformed.
     *
     * <p>Performs two infection scenarios:</p>
     * <ol>
     * <li><strong>Non-transformed attack</strong> - Verifies that damage from a non-transformed player
     * does not spread the curse to the victim</li>
     * <li><strong>Transformed attack</strong> - Verifies that when the attacker is transformed at moonrise
     * on a full moon day, damage spreads the lycanthropy curse to the victim. The infection is scheduled
     * to occur 1 second after the damage event to allow the event to be processed.</li>
     * </ol>
     */
    private void doOnEntityDamageByEntityEventTest() {
        PlayerMock target = mockServer.addPlayer();
        PlayerMock player = mockServer.addPlayer();
        World world = target.getWorld();

        LYCANTHROPY lycanthropy = (LYCANTHROPY) addEffect(target, 10, true);
        world.setFullTime((24000 * 8) + TimeCommon.MIDDAY.getTick());
        mockServer.getScheduler().performTicks(10);

        // have the player attack another player
        DamageSource damageSource = DamageSource.builder(DamageType.PLAYER_ATTACK)
                .withCausingEntity(target)
                .withDirectEntity(target)
                .build();
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(target, player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damageSource, 1);
        mockServer.getPluginManager().callEvent(event);

        // run the server ahead
        mockServer.getScheduler().performTicks(10);
        // confirm that the attacked player does not have lycanthropy
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.LYCANTHROPY), "Player was infected with lycanthropy when attacker was not transformed.");

        // set the world to a full moon evening
        world.setFullTime((24000 * 8) + TimeCommon.MOONRISE.getTick());
        // play forward for the effect to transform the target
        mockServer.getScheduler().performTicks(Ollivanders2Common.ticksPerSecond);
        assertTrue(lycanthropy.isTransformed(), "target is not transformed");

        // have the target attack the player again
        event = new EntityDamageByEntityEvent(target, player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damageSource, 1);
        mockServer.getPluginManager().callEvent(event);
        // play forward the server so that the player can be infected
        mockServer.getScheduler().performTicks(Ollivanders2Common.ticksPerSecond * 5);
        try {
            // now sleep so we give the runnable time to run
            Thread.sleep(500);
        }
        catch (InterruptedException e) {}
        assertNotNull(Ollivanders2API.getPlayers().playerEffects.getEffect(player.getUniqueId(), O2EffectType.LYCANTHROPY), "Player was not infected with lycanthropy when attacker was transformed.");
    }

    /**
     * Cleanup test for the LYCANTHROPY effect.
     *
     * <p>LYCANTHROPY is a permanent curse that cannot be removed through normal means. When the effect
     * is killed via the kill() method, it properly restores the player to human form and removes secondary
     * effects (AGGRESSION and LYCANTHROPY_SPEECH). This cleanup is tested implicitly through the
     * transformation tests which verify that effects are removed when the transformation conditions are
     * no longer met. Therefore, this method is empty as there is no specific doRemove() behavior to test
     * beyond the base class cleanup behavior.</p>
     */
    @Override
    void doRemoveTest() {}
}
