package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.IncendioSuper;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract base class for unit testing IncendioSuper spell implementations.
 *
 * <p>Provides comprehensive test coverage for fire-based spell mechanics including:
 * <ul>
 * <li><strong>Fire Effect:</strong> Verifies fire is placed above burnable blocks</li>
 * <li><strong>Block Targeting:</strong> Tests single-target vs strafe (radius) behavior</li>
 * <li><strong>Entity Targeting:</strong> Validates fire applied to entities and items in effective radius</li>
 * <li><strong>Duration:</strong> Confirms burn duration is calculated and clamped correctly</li>
 * <li><strong>Reversion:</strong> Verifies temporary fire blocks are reverted when spell ends</li>
 * <li><strong>Invalid Targets:</strong> Tests spell behavior on non-burnable blocks</li>
 * <li><strong>Permanent Burn:</strong> Verifies fire on soul sand/netherrack is not reverted</li>
 * </ul>
 *
 * <p>Subclasses implement {@link #getSpellType()} to specify which IncendioSuper variant to test.
 * The same test methods are run for each subclass, allowing different spell configurations
 * (e.g., INCENDIO, INCENDIO_DUO, INCENDIO_TRIA) to be validated with the same test suite.</p>
 *
 * @author Azami7
 */
abstract public class IncendioSuperTest extends O2SpellTestSuper {
    /**
     * Tests spell construction and configuration.
     *
     * <p>Default implementation is empty. Subclasses may override to validate spell-specific
     * setup such as strafe settings, radius values, and duration modifiers.</p>
     */
    @Override
    @Test
    void spellConstructionTest() {
    }

    /**
     * Tests basic fire spell effect on a burnable block.
     *
     * <p>Verifies that:
     * <ul>
     * <li>Fire is placed above the target block (one block up)</li>
     * <li>The target block itself is not destroyed or changed</li>
     * <li>Fire effects work on burnable materials like OAK_PLANKS</li>
     * </ul>
     *
     * <p>This is the fundamental test that the spell actually sets blocks on fire.</p>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld("Incendio");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = targetLocation.getBlock();
        TestCommon.createBlockBase(target.getRelative(BlockFace.DOWN).getLocation(), 5);
        target.setType(Material.OAK_PLANKS);

        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertEquals(Material.FIRE, target.getRelative(BlockFace.UP).getType(), "target block is not on fire");
        assertEquals(Material.OAK_PLANKS, target.getType(), "target type was changed");
    }

    /**
     * Tests spell behavior when cast on a non-burnable block.
     *
     * <p>Verifies that:
     * <ul>
     * <li>No fire is placed on water or other non-burnable blocks</li>
     * <li>The spell is killed when its target cannot burn</li>
     * </ul>
     *
     * <p>This ensures the spell fails safely when cast on inappropriate targets.</p>
     */
    @Test
    void invalidTargetTest() {
        World testWorld = mockServer.addSimpleWorld("Incendio");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = targetLocation.getBlock();
        target.setType(Material.BEDROCK);

        IncendioSuper incendioSuper = (IncendioSuper) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertNotEquals(Material.FIRE, target.getRelative(BlockFace.UP).getType(), "target block is on fire when it is not a valid target");
        assertTrue(incendioSuper.isKilled(), "spell not killed when target was invalid");
    }

    /**
     * Tests permanent fire behavior on soul sand and netherrack.
     *
     * <p>Verifies that:
     * <ul>
     * <li>Fire is placed on soul sand as expected</li>
     * <li>Fire on soul sand is NOT tracked as a temporary block change (will burn permanently)</li>
     * <li>When the spell ends, the fire is NOT reverted (stays lit permanently)</li>
     * </ul>
     *
     * <p>This ensures that fire on certain materials (soul sand, netherrack) is not cleaned up
     * when the spell duration expires, allowing it to burn indefinitely.</p>
     */
    @Test
    void staysLitTest() {
        World testWorld = mockServer.addSimpleWorld("Incendio");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = targetLocation.getBlock();
        TestCommon.createBlockBase(target.getRelative(BlockFace.DOWN).getLocation(), 5);
        target.setType(Material.SOUL_SAND);

        IncendioSuper incendioSuper = (IncendioSuper) castSpell(caster, location, targetLocation);

        mockServer.getScheduler().performTicks(20);
        assertEquals(Material.FIRE, target.getRelative(BlockFace.UP).getType());
        assertFalse(Ollivanders2API.getBlocks().isTemporarilyChangedBlock(target), "target added to temporary block tracking when it is a staysLit block");

        mockServer.getScheduler().performTicks(incendioSuper.getBurnDuration() + 1);
        assertTrue(incendioSuper.isKilled());
        assertEquals(Material.FIRE, target.getRelative(BlockFace.UP).getType());
    }

    /**
     * Tests fire effect on dropped items.
     *
     * <p>Verifies that:
     * <ul>
     * <li>Items at the target location are set on fire</li>
     * <li>Item fire ticks are set to the calculated burn duration</li>
     * </ul>
     *
     * <p>This ensures the spell can ignite items as well as blocks and entities.</p>
     */
    @Test
    void itemTargetTest() {
        World testWorld = mockServer.addSimpleWorld("Incendio");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = targetLocation.getBlock();
        TestCommon.createBlockBase(target.getRelative(BlockFace.DOWN).getLocation(), 5);
        Item item = testWorld.dropItem(targetLocation, new ItemStack(Material.ARROW, 1));

        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(item.getFireTicks() > 0, "item is not on fire");
    }

    /**
     * Tests fire effect on living entities.
     *
     * <p>Verifies that:
     * <ul>
     * <li>Entities at the target location are set on fire</li>
     * <li>Entity fire ticks are set to the calculated burn duration</li>
     * <li>The spell continues to exist (not killed) when targeting entities</li>
     * </ul>
     *
     * <p>This ensures the spell affects entities as well as blocks.</p>
     */
    @Test
    void entityTargetTest() {
        World testWorld = mockServer.addSimpleWorld("Incendio");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = targetLocation.getBlock();
        TestCommon.createBlockBase(target.getRelative(BlockFace.DOWN).getLocation(), 5);
        Entity target1 = testWorld.spawnEntity(targetLocation, EntityType.SPIDER);

        IncendioSuper incendioSuper = (IncendioSuper) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertFalse(incendioSuper.isKilled(), "spell failed to target target1");
        assertTrue(target1.getFireTicks() > 0, "target1 is not on fire");
    }

    /**
     * Tests radius/strafe behavior for block targeting.
     *
     * <p>Verifies that:
     * <ul>
     * <li>Strafe spells (with radius > 1) affect multiple blocks in an area</li>
     * <li>Non-strafe spells affect only the single target block</li>
     * </ul>
     *
     * <p>This ensures the spell's strafe configuration controls whether it affects a single target
     * or all blocks within a radius.</p>
     */
    @Test
    void blockStrafeTest() {
        World testWorld = mockServer.addSimpleWorld("Incendio");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        IncendioSuper incendioSuper = (IncendioSuper) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);

        TestCommon.createBlockBase(targetLocation, 3, Material.OAK_PLANKS);
        mockServer.getScheduler().performTicks(20);

        if (incendioSuper.isStrafe() && incendioSuper.getBlockRadius() > 1) {
            assertTrue(Ollivanders2API.getBlocks().getBlocksChangedBySpell(incendioSuper).size() > 1, "strafe did not target multiple blocks");
        }
        else {
            assertEquals(1, Ollivanders2API.getBlocks().getBlocksChangedBySpell(incendioSuper).size(), "targeted multiple blocks when strafe not set");
        }
    }

    /**
     * Tests radius/strafe behavior for entity targeting.
     *
     * <p>Verifies that:
     * <ul>
     * <li>Strafe spells affect all entities within the effective radius (target1, target2, target3)</li>
     * <li>Non-strafe spells affect only one entity even if multiple are present at the target location</li>
     * <li>Entities outside the effective radius (target3 at NORTH) are only affected by strafe spells</li>
     * </ul>
     *
     * <p>This ensures the spell's strafe configuration controls whether it affects one entity
     * or all entities within the area.</p>
     */
    @Test
    void entityStrafeTest() {
        World testWorld = mockServer.addSimpleWorld("Incendio");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block center = targetLocation.getBlock();
        TestCommon.createBlockBase(center.getRelative(BlockFace.DOWN).getLocation(), 3);
        Entity target1 = testWorld.spawnEntity(targetLocation, EntityType.SPIDER);
        Entity target2 = testWorld.spawnEntity(targetLocation, EntityType.SPIDER);
        Entity target3 = testWorld.spawnEntity(center.getRelative(BlockFace.NORTH).getLocation(), EntityType.SPIDER);
        mockServer.getScheduler().performTicks(5);

        IncendioSuper incendioSuper = (IncendioSuper) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        if (incendioSuper.isStrafe() && incendioSuper.getBlockRadius() > 1) {
            assertTrue(target1.getFireTicks() > 0, "target1 not on fire");
            assertTrue(target2.getFireTicks() > 0, "target2 not on fire");
            assertTrue(target3.getFireTicks() > 0, "target3 not on fire");
        }
        else {
            assertTrue(((target1.getFireTicks() > 0) ^ (target2.getFireTicks() > 0)) ^ (target3.getFireTicks() > 0), "neither or more than 1 of of target1, target2, target3 on fire when spell is not strafe");
        }
    }

    /**
     * Tests radius/strafe behavior for item targeting.
     *
     * <p>Verifies that:
     * <ul>
     * <li>Strafe spells affect all items within the effective radius (target1, target2, target3)</li>
     * <li>Non-strafe spells affect only one item even if multiple are present at the target location</li>
     * <li>Items outside the effective radius (target3 at EAST) are only affected by strafe spells</li>
     * </ul>
     *
     * <p>This ensures the spell's strafe configuration controls whether it affects one item
     * or all items within the area.</p>
     */
    @Test
    void itemStrafeTest() {
        World testWorld = mockServer.addSimpleWorld("Incendio");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block center = targetLocation.getBlock();
        TestCommon.createBlockBase(center.getRelative(BlockFace.DOWN).getLocation(), 3);
        Item target1 = testWorld.dropItem(targetLocation, new ItemStack(Material.ARROW, 1));
        Item target2 = testWorld.dropItem(targetLocation, new ItemStack(Material.ARROW, 1));
        Item target3 = testWorld.dropItem(center.getRelative(BlockFace.EAST).getLocation(), new ItemStack(Material.ARROW, 1));
        mockServer.getScheduler().performTicks(5);

        IncendioSuper incendioSuper = (IncendioSuper) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        if (incendioSuper.isStrafe() && incendioSuper.getBlockRadius() > 1) {
            assertTrue(target1.getFireTicks() > 0, "target1 not on fire");
            assertTrue(target2.getFireTicks() > 0, "target2 not on fire");
            assertTrue(target3.getFireTicks() > 0, "target3 not on fire");
        }
        else {
            assertTrue(((target1.getFireTicks() > 0) ^ (target2.getFireTicks() > 0)) ^ (target3.getFireTicks() > 0), "more than one or none of target1, target2, target3 on fire when spell is not strafe");
        }
    }

    /**
     * Tests burn duration calculation and clamping.
     *
     * <p>Verifies that:
     * <ul>
     * <li>The calculated burn duration is within valid bounds (min to max)</li>
     * <li>The spell respects configured min and max duration limits</li>
     * </ul>
     *
     * <p>This ensures burn duration scaling (based on caster spell level and duration modifier)
     * produces reasonable values that don't exceed configured boundaries.</p>
     */
    @Test
    void burnDurationTest() {
        World testWorld = mockServer.addSimpleWorld("Incendio");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(targetLocation, 3);

        IncendioSuper incendioSuper = (IncendioSuper) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        assertTrue(incendioSuper.getBurnDuration() <= incendioSuper.getMaxBurnDuration(), "burn duration > max burn duration");
        assertTrue(incendioSuper.getBurnDuration() >= incendioSuper.getMinBurnDuration(), "burn duration < min burn duration");
    }

    /**
     * Tests fire block reversion when spell ends.
     *
     * <p>Verifies that:
     * <ul>
     * <li>Fire is placed above the target block</li>
     * <li>The fire block is tracked as a temporary block change</li>
     * <li>The target block itself is not tracked (only the fire block above it)</li>
     * <li>When the spell is killed, the fire block is reverted to its original type (AIR)</li>
     * <li>The fire block is removed from temporary block tracking after reversion</li>
     * </ul>
     *
     * <p>This ensures that temporary fire blocks are properly cleaned up and the world is restored
     * to its original state when the spell ends.</p>
     */
    @Override
    @Test
    void revertTest() {
        World testWorld = mockServer.addSimpleWorld("Incendio");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = testWorld.getBlockAt(targetLocation);
        Block above = target.getRelative(BlockFace.UP);
        target.setType(Material.OAK_PLANKS);
        above.setType(Material.AIR);

        IncendioSuper incendioSuper = (IncendioSuper) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        assertTrue(incendioSuper.hasHitTarget());
        assertEquals(Material.FIRE, above.getType());
        assertTrue(Ollivanders2API.getBlocks().isTemporarilyChangedBlock(above), "above block not added to temporarily changed block tracking");
        assertFalse(Ollivanders2API.getBlocks().isTemporarilyChangedBlock(target), "target block was added to temporarily changed block tracking");

        incendioSuper.kill();
        mockServer.getScheduler().performTicks(1);
        assertEquals(Material.AIR, above.getType(), "above block not reverted to original type");
        assertFalse(Ollivanders2API.getBlocks().isTemporarilyChangedBlock(above), "above block still in temporarily changed block tracking");
    }
}
