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
 * Base test class for {@link ImmobilizePlayer} spells, covering target detection, effect application (full vs
 * partial), duration bounds, movement restriction, and prison block creation and reversion.
 *
 * @author Azami7
 * @see ImmobilizePlayer
 * @see O2SpellTestSuper
 */
abstract public class ImmobilizePlayerTest extends O2SpellTestSuper {
    /**
     * Verify the spell applies the immobilization effect (full or partial) to a nearby target player.
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

        assertTrue(immobilize.hasHitBlock(), "did not hit target player");
        if (immobilize.isFullImmobilize())
            assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), O2EffectType.FULL_IMMOBILIZE), "target does not have the full immobilize effect");
        else
            assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), O2EffectType.IMMOBILIZE), "target does not have the immobilize effect");
    }

    /**
     * Verify the immobilization duration stays within its min and max bounds.
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

        assertTrue(immobilize.hasHitBlock());
        assertTrue(immobilize.getEffectDuration() <= immobilize.getMaxEffectDuration(), "effect duration is > max duration");
        assertTrue(immobilize.getEffectDuration() >= immobilize.getMinEffectDuration(), "effect duration is < min duration");
    }

    /**
     * Verify immobilization always blocks a location change, and additionally blocks rotation only when the spell is
     * full immobilize.
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
     * Hook for subclasses to test spell-specific target rejection. The default is a no-op.
     */
    @Test
    void invalidTargetTest() {
    }

    /**
     * Verify an imprisoning spell fills the blocks around the target with its prison material (and the block the
     * target occupies unless it builds only a shell), and that a non-imprisoning spell changes nothing.
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
        assertTrue(immobilizePlayer.hasHitBlock());

        if (immobilizePlayer.doesImprison()) {
            Material prisonMaterial = immobilizePlayer.getImprisonMaterial();

            assertEquals(prisonMaterial, target.getEyeLocation().getBlock().getRelative(BlockFace.EAST).getType(), "block next to eye location was not changed");
            assertTrue(Ollivanders2API.getBlocks().isTemporarilyChangedBlock(target.getEyeLocation().getBlock().getRelative(BlockFace.EAST)), "block not added to tracking");
            if (!immobilizePlayer.isPrisonShell()) {
                assertEquals(prisonMaterial, target.getEyeLocation().getBlock().getType(), "block at eye location not changed to prison material");
            }
        }
        else
            assertEquals(Material.AIR, target.getEyeLocation().getBlock().getRelative(BlockFace.EAST).getType(), "block next to eye location was changed when spell does not do imprison");
    }

    /**
     * Hook for subclasses to verify spell-specific extra effects. The default is a no-op.
     */
    @Test
    void additionalEffectsTest() {
    }

    /**
     * Verify prison blocks are reverted to air and cleared from tracking once the effect duration expires.
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
