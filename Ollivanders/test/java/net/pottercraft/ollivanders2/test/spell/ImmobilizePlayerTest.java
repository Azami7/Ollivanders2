package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.spell.ImmobilizePlayer;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerMoveEvent;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract base class for testing ImmobilizePlayer spell implementations.
 *
 * <p>ImmobilizePlayerTest provides a common testing framework for all spell subclasses that extend
 * ImmobilizePlayer, which are spells that target and immobilize a nearby player. This test class
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
 * <li>Prison block creation and reversion (for spells with imprison = true)</li>
 * </ul>
 *
 * @author Azami7
 * @see ImmobilizePlayer for the spell superclass being tested
 * @see O2SpellTestSuper for the base spell testing framework
 */
abstract public class ImmobilizePlayerTest extends O2SpellTestSuper {
    /**
     * Overridden to do nothing. Immobilize spells have no construction requirements beyond
     * those covered by inherited base class tests.
     */
    @Override
    @Test
    void spellConstructionTest() {
    }

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
     * <p>No-op by default. Subclasses may override to test spell-specific target validation logic,
     * such as verifying that players with certain attributes or conditions cannot be targeted.</p>
     */
    @Test
    void invalidTargetTest() {
    }

    /**
     * Test that prison blocks are created correctly when the spell imprisons the target.
     *
     * <p>For spells with imprison = true, verifies that the prison material is placed adjacent to the
     * target's eye location and that the block is tracked as a temporarily changed block. For spells
     * where prisonIsShell = false, also verifies that the block at the target's eye location itself
     * is changed. For non-imprisoning spells, verifies that no blocks are changed.</p>
     */
    @Test
    void imprisonEffectTest() {
        World testWorld = mockServer.addSimpleWorld("Immobilize");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        PlayerMock target = mockServer.addPlayer();
        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), target.getX(), target.getY() - 1, target.getZ()), 3);
        target.setLocation(targetLocation);
        assertEquals(Material.AIR, target.getEyeLocation().getBlock().getType());

        ImmobilizePlayer immobilizePlayer = (ImmobilizePlayer) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(immobilizePlayer.hasHitTarget());

        if (immobilizePlayer.doesImprison()) {
            Material prisonMaterial = immobilizePlayer.getImprisonMaterial();

            assertEquals(prisonMaterial, target.getEyeLocation().getBlock().getRelative(BlockFace.EAST).getType(), "block next to eye location was changed");
            assertTrue(Ollivanders2API.getBlocks().isTemporarilyChangedBlock(target.getEyeLocation().getBlock().getRelative(BlockFace.EAST)), "block not added to tracking");
            if (!immobilizePlayer.isPrisonShell()) {
                assertEquals(prisonMaterial, target.getEyeLocation().getBlock().getType(), "block at eye location not changed to Water");
            }
        }
        else
            assertEquals(Material.AIR, target.getEyeLocation().getBlock().getRelative(BlockFace.EAST).getType(), "block next to eye location was changed");
    }

    /**
     * Test that the spell applies any spell-specific additional effects to the target.
     *
     * <p>No-op by default. Subclasses may override to verify supplementary effects such as
     * potion effects or environmental changes applied beyond the base immobilization.</p>
     */
    @Test
    void additionalEffectsTest() {
    }

    /**
     * Test that prison blocks are reverted after the effect duration expires.
     *
     * <p>Verifies that when the immobilization effect duration expires, all prison blocks
     * are automatically reverted to their original state (AIR) and are no longer tracked
     * as temporarily changed blocks.</p>
     */
    @Override
    @Test
    void revertTest() {
        World testWorld = mockServer.addSimpleWorld("Immobilize");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        ImmobilizePlayer immobilizePlayer = (ImmobilizePlayer) castSpell(caster, location, targetLocation);

        if (immobilizePlayer.doesImprison()) {
            PlayerMock target = mockServer.addPlayer();
            TestCommon.createBlockBase(new Location(targetLocation.getWorld(), target.getX(), target.getY() - 1, target.getZ()), 3);
            target.setLocation(targetLocation);

            mockServer.getScheduler().performTicks(20);

            assertTrue(Ollivanders2API.getBlocks().isTemporarilyChangedBlock(targetLocation.getBlock().getRelative(BlockFace.EAST)));
            mockServer.getScheduler().performTicks(immobilizePlayer.getEffectDuration());

            assertFalse(Ollivanders2API.getBlocks().isTemporarilyChangedBlock(targetLocation.getBlock().getRelative(BlockFace.EAST)), "prison block still being tracked");
            assertEquals(Material.AIR, targetLocation.getBlock().getType(), "prison block not reverted");
        }
    }
}
