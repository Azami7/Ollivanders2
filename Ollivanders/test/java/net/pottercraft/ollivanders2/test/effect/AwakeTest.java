package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.AWAKE;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.effect.SLEEPING;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AWAKE}, the insomnia effect that removes SLEEPING and blocks bed entry.
 *
 * @see EffectTestSuper
 */
public class AwakeTest extends EffectTestSuper {
    /**
     * {@inheritDoc}
     */
    @Override
    AWAKE createEffect(Player target, int durationInTicks, boolean isPermanent) {
        return new AWAKE(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    /**
     * Applying AWAKE removes an active SLEEPING effect, and AWAKE cancels bed-entry attempts. SLEEPING must
     * initialize before AWAKE is applied so the interaction between the two effects is exercised.
     */
    @Override
    void checkEffectTest() {
        Ollivanders2.debug = true;

        // Test 1: AWAKE wakes up a sleeping player and removes SLEEPING effect
        PlayerMock target = mockServer.addPlayer();

        // Apply SLEEPING effect first
        SLEEPING sleeping = new SLEEPING(testPlugin, 100, false, target.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(sleeping);
        // Advance ticks so SLEEPING initializes and puts player to sleep
        mockServer.getScheduler().performTicks(5);

        // Verify SLEEPING effect is active
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), O2EffectType.SLEEPING),
            "SLEEPING effect was not added to target");

        // Verify player is sleeping (should be set by SLEEPING.playerSleep())
        // commented out because MockBukkit has not yet implemented player sleeping
        //assertTrue(target.isSleeping(), "Player was not put to sleep by SLEEPING effect");

        // Apply AWAKE effect
        addEffect(target, 100, false);

        // Advance ticks so AWAKE's checkEffect() runs and wakes the player
        mockServer.getScheduler().performTicks(5);

        // verify AWAKE effect is active
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), O2EffectType.AWAKE),
                "AWAKE effect was not added to target");

        // Verify player was woken up
        // commented out because MockBukkit has not yet implemented player sleeping
        // assertFalse(target.isSleeping(), "AWAKE did not wake up sleeping player");

        // Verify SLEEPING effect was removed
        assertNull(Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.SLEEPING),
            "SLEEPING effect was not removed by AWAKE");

        // Test 2: AWAKE prevents player from entering a bed
        PlayerMock target2 = mockServer.addPlayer();
        Block bed = testWorld.getBlockAt(new Location(testWorld, 200, 4, 300));
        bed.setType(Material.BLUE_BED);
        target2.setLocation(new Location(testWorld, 200, 4, 300));

        // Apply AWAKE effect
        addEffect(target2, 100, false);

        // Try to trigger a bed enter event
        PlayerBedEnterEvent event = new PlayerBedEnterEvent(target2, bed, PlayerBedEnterEvent.BedEnterResult.OK);
        mockServer.getPluginManager().callEvent(event);

        // Verify the event was cancelled
        assertTrue(event.isCancelled(), "AWAKE did not prevent player from entering bed");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void eventHandlerTests() {
        doOnPlayerBedEnterEventTest();
    }

    /**
     * AWAKE cancels a player's bed-entry event, preventing them from sleeping.
     */
    void doOnPlayerBedEnterEventTest() {
        Block bed = testWorld.getBlockAt(new Location(testWorld, 200, 4, 300));
        bed.setType(Material.BLUE_BED);

        PlayerMock target = mockServer.addPlayer();

        target.setLocation(new Location(testWorld, 200, 4, 300));

        AWAKE awake = (AWAKE)addEffect(target, 100, false);

        // call a player bed enter event
        PlayerBedEnterEvent event = new PlayerBedEnterEvent(target, bed, PlayerBedEnterEvent.BedEnterResult.OK);
        mockServer.getPluginManager().callEvent(event);

        mockServer.getScheduler().performTicks(5);
        assertTrue(event.isCancelled(), "PlayerBedEnterEvent was not canceled by AWAKE");
    }

    /**
     * AWAKE has no doRemove() cleanup to test.
     */
    @Override
    void doRemoveTest() {}
}
