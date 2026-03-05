package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.spell.ImmobilizePlayer;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.player.PlayerMoveEvent;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract base class for testing ImmobilizePlayerSuper spell implementations.
 *
 * <p>ImmobilizePlayerSuperTest provides a common testing framework for all spell subclasses that extend
 * ImmobilizePlayerSuper, which are spells that target and immobilize a nearby player. This test class
 * validates that the spell correctly targets players, applies appropriate immobilization effects, and
 * respects duration and immobilization type (full vs. partial).</p>
 *
 * <p>Test Coverage:</p>
 * <ul>
 * <li>Target detection - verifies the spell correctly identifies and targets nearby players</li>
 * <li>Effect application - verifies IMMOBILIZE or FULL_IMMOBILIZE effect is applied based on spell type</li>
 * <li>Duration calculation - verifies effect duration is within configured min/max bounds</li>
 * <li>Movement restriction - verifies location changes are prevented while rotation may be allowed</li>
 * <li>Full immobilization - verifies FULL_IMMOBILIZE prevents all movement including rotation</li>
 * <li>Partial immobilization - verifies IMMOBILIZE allows rotation but prevents location changes</li>
 * </ul>
 *
 * @author Azami7
 * @see ImmobilizePlayer for the spell superclass being tested
 * @see O2SpellTestSuper for the base spell testing framework
 */
abstract public class ImmobilizePlayerTest extends O2SpellTestSuper {
    /**
     * Test that the immobilize spell correctly targets and applies effects to nearby players.
     *
     * <p>Verifies that the spell detects a nearby player target and applies the appropriate immobilization
     * effect (IMMOBILIZE or FULL_IMMOBILIZE depending on the spell type). The test creates a caster and
     * a target player, casts the spell, and validates that the target has the correct effect applied.</p>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld("Immobilize");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        PlayerMock target = mockServer.addPlayer();
        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        target.setLocation(targetLocation);

        ImmobilizePlayer immobilize = (ImmobilizePlayer) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(immobilize.hasHitTarget(), "did not hit target player");
        if (immobilize.isFullImmobilize())
            assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), O2EffectType.FULL_IMMOBILIZE), "target does not have the full immobilize effect");
        else
            assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), O2EffectType.IMMOBILIZE), "target does not have the immobilize effect");
    }

    /**
     * Test that the immobilization effect duration is within configured bounds.
     *
     * <p>Verifies that the spell calculates effect duration correctly and that the duration is clamped
     * between the minimum and maximum duration limits. The test casts the spell and validates that the
     * resulting immobilization effect has a duration within the expected range.</p>
     */
    @Test
    void durationTest() {
        World testWorld = mockServer.addSimpleWorld("Immobilize");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        PlayerMock target = mockServer.addPlayer();
        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        target.setLocation(targetLocation);

        ImmobilizePlayer immobilize = (ImmobilizePlayer) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(immobilize.hasHitTarget());
        assertTrue(immobilize.getEffectDuration() <= immobilize.getMaxEffectDuration(), "effect duration is > max duration");
        assertTrue(immobilize.getEffectDuration() >= immobilize.getMinEffectDuration(), "effect duration is < min duration");
    }

    /**
     * Test that immobilization prevents movement and optionally prevents rotation.
     *
     * <p>Verifies the movement restriction behavior of the immobilization effect. The test validates that
     * location-changing movement is always prevented. For full immobilization (FULL_IMMOBILIZE), rotation
     * changes (pitch/yaw only) are also prevented. For partial immobilization (IMMOBILIZE), rotation changes
     * are allowed while location changes remain blocked.</p>
     */
    @Test
    void fullImmobilizeTest() {
        World testWorld = mockServer.addSimpleWorld("Immobilize");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        PlayerMock target = mockServer.addPlayer();
        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        target.setLocation(targetLocation);

        ImmobilizePlayer immobilize = (ImmobilizePlayer) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        PlayerMoveEvent event = new PlayerMoveEvent(target, targetLocation, location);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(5);
        assertTrue(event.isCancelled(), "PlayerMoveEvent(target, targetLocation, location) not canceled");

        Location rotationLoc = targetLocation.clone();
        rotationLoc.setPitch(targetLocation.getPitch() + 5);

        event = new PlayerMoveEvent(target, targetLocation, rotationLoc);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(5);

        if (immobilize.isFullImmobilize()) {
            assertTrue(event.isCancelled(), "PlayerMoveEvent(target, targetLocation, rotationLoc) not canceled");
        }
        else {
            assertFalse(event.isCancelled(), "PlayerMoveEvent(target, targetLocation, rotationLoc) was canceled");
        }
    }

    /**
     * Test that the spell correctly rejects invalid targets.
     *
     * <p>Abstract method that concrete subclasses must implement to test spell-specific target validation
     * logic. Examples of invalid targets may include players with certain attributes, effects, or conditions
     * that prevent the spell from affecting them.</p>
     */
    @Test
    abstract void invalidTargetTest();

    /**
     * Test that the spell applies any spell-specific additional effects to the target.
     *
     * <p>Abstract method that concrete subclasses must implement to test additional effects beyond the
     * base immobilization effect. Different immobilize spells may apply supplementary effects such as
     * potion effects or environmental changes as part of their mechanics.</p>
     */
    @Test
    abstract void additionalEffectsTest();

    /**
     * Test that the spell effects revert after expiration.
     *
     * <p>This test is left empty for immobilize spells as the revert behavior is handled by the standard
     * effect expiration mechanism and is not spell-specific.</p>
     */
    @Override
    @Test
    void revertTest() {
    }
}
