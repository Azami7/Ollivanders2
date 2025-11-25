package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.AWAKE;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerBedEnterEvent;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the AWAKE effect.
 *
 * <p>Tests the insomnia effect that prevents players from sleeping by canceling bed enter events.
 * Verifies that the effect ages properly each tick, is killed when duration expires, and correctly
 * prevents players from entering beds while active.</p>
 */
public class AwakeTest extends EffectTestSuper {
    AWAKE createEffect(int durationInTicks, boolean isPermanent) {
        return new AWAKE(testPlugin, durationInTicks, isPermanent, target.getUniqueId());
    }

    void checkEffectTest() {
        checkEffectTestAgingHelper();
    }

    void eventHandlerTests() {
        doOnPlayerBedEnterEventTest();
    }

    void doOnPlayerBedEnterEventTest() {
        Block bed = testWorld.getBlockAt(new Location(testWorld, 200, 4, 300));
        bed.setType(Material.BLUE_BED);

        target.setLocation(new Location(testWorld, 200, 4, 300));

        AWAKE awake = createEffect(100, false);
        Ollivanders2API.getPlayers().playerEffects.addEffect(awake);

        // call a player bed enter event
        PlayerBedEnterEvent event = new PlayerBedEnterEvent(target, bed, PlayerBedEnterEvent.BedEnterResult.OK);
        mockServer.getPluginManager().callEvent(event);

        mockServer.getScheduler().performTicks(5);
        assertTrue(event.isCancelled(), "PlayerBedEnterEvent was not canceled by AWAKE");
    }

    /**
     * doRemove() for AWAKE does not do anything
     */
    void doRemoveTest() {}
}
