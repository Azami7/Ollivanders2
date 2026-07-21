package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.IncendioBase;
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
 * Base test class for {@link IncendioBase} spells, covering fire placement on blocks/entities/items, single vs strafe
 * targeting, burn duration bounds, reversion, invalid targets, and permanent burn on soul sand/netherrack.
 *
 * @author Azami7
 */
abstract public class IncendioBaseTest extends O2SpellTestSuper {
    /**
     * Verify the spell places fire above a burnable block without changing the block itself.
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
     * Verify the spell places no fire and is killed when cast at a non-burnable block.
     */
    @Test
    void invalidTargetTest() {
        World testWorld = mockServer.addSimpleWorld("Incendio");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = targetLocation.getBlock();
        target.setType(Material.BEDROCK);

        IncendioBase incendioBase = (IncendioBase) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertNotEquals(Material.FIRE, target.getRelative(BlockFace.UP).getType(), "target block is on fire when it is not a valid target");
        assertTrue(incendioBase.isKilled(), "spell not killed when target was invalid");
    }

    /**
     * Verify fire placed on soul sand is not tracked for reversion and stays lit after the spell ends.
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

        IncendioBase incendioBase = (IncendioBase) castSpell(caster, location, targetLocation);

        mockServer.getScheduler().performTicks(20);
        assertEquals(Material.FIRE, target.getRelative(BlockFace.UP).getType());
        assertFalse(Ollivanders2API.getBlocks().isTemporarilyChangedBlock(target), "target added to temporary block tracking when it is a staysLit block");

        mockServer.getScheduler().performTicks(incendioBase.getBurnDuration() + 1);
        assertTrue(incendioBase.isKilled());
        assertEquals(Material.FIRE, target.getRelative(BlockFace.UP).getType());
    }

    /**
     * Verify the spell sets a dropped item on fire.
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
     * Verify the spell sets a living entity on fire and keeps burning (not killed).
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

        IncendioBase incendioBase = (IncendioBase) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertFalse(incendioBase.isKilled(), "spell failed to target target1");
        assertTrue(target1.getFireTicks() > 0, "target1 is not on fire");
    }

    /**
     * Verify a strafe spell ignites multiple blocks in its radius while a non-strafe spell ignites only one.
     */
    @Test
    void blockStrafeTest() {
        World testWorld = mockServer.addSimpleWorld("Incendio");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        IncendioBase incendioBase = (IncendioBase) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);

        TestCommon.createBlockBase(targetLocation, 3, Material.OAK_PLANKS);
        mockServer.getScheduler().performTicks(20);

        if (incendioBase.isStrafe() && incendioBase.getBlockRadius() > 1) {
            assertTrue(Ollivanders2API.getBlocks().getBlocksChangedBySpell(incendioBase).size() > 1, "strafe did not target multiple blocks");
        }
        else {
            assertEquals(1, Ollivanders2API.getBlocks().getBlocksChangedBySpell(incendioBase).size(), "targeted multiple blocks when strafe not set");
        }
    }

    /**
     * Verify a strafe spell ignites every entity in its radius while a non-strafe spell ignites only one.
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

        IncendioBase incendioBase = (IncendioBase) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        if (incendioBase.isStrafe() && incendioBase.getBlockRadius() > 1) {
            assertTrue(target1.getFireTicks() > 0, "target1 not on fire");
            assertTrue(target2.getFireTicks() > 0, "target2 not on fire");
            assertTrue(target3.getFireTicks() > 0, "target3 not on fire");
        }
        else {
            assertTrue(((target1.getFireTicks() > 0) ^ (target2.getFireTicks() > 0)) ^ (target3.getFireTicks() > 0), "neither or more than 1 of of target1, target2, target3 on fire when spell is not strafe");
        }
    }

    /**
     * Verify a strafe spell ignites every item in its radius while a non-strafe spell ignites only one.
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

        IncendioBase incendioBase = (IncendioBase) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        if (incendioBase.isStrafe() && incendioBase.getBlockRadius() > 1) {
            assertTrue(target1.getFireTicks() > 0, "target1 not on fire");
            assertTrue(target2.getFireTicks() > 0, "target2 not on fire");
            assertTrue(target3.getFireTicks() > 0, "target3 not on fire");
        }
        else {
            assertTrue(((target1.getFireTicks() > 0) ^ (target2.getFireTicks() > 0)) ^ (target3.getFireTicks() > 0), "more than one or none of target1, target2, target3 on fire when spell is not strafe");
        }
    }

    /**
     * Verify the computed burn duration stays within the spell's min and max bounds.
     */
    @Test
    void burnDurationTest() {
        World testWorld = mockServer.addSimpleWorld("Incendio");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(targetLocation, 3);

        IncendioBase incendioBase = (IncendioBase) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        assertTrue(incendioBase.getBurnDuration() <= incendioBase.getMaxBurnDuration(), "burn duration > max burn duration");
        assertTrue(incendioBase.getBurnDuration() >= incendioBase.getMinBurnDuration(), "burn duration < min burn duration");
    }

    /**
     * Verify killing the spell reverts the temporary fire block back to air and clears it from change tracking.
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

        IncendioBase incendioBase = (IncendioBase) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        assertTrue(incendioBase.hasHitBlock());
        assertEquals(Material.FIRE, above.getType());
        assertTrue(Ollivanders2API.getBlocks().isTemporarilyChangedBlock(above), "above block not added to temporarily changed block tracking");
        assertFalse(Ollivanders2API.getBlocks().isTemporarilyChangedBlock(target), "target block was added to temporarily changed block tracking");

        incendioBase.kill();
        mockServer.getScheduler().performTicks(1);
        assertEquals(Material.AIR, above.getType(), "above block not reverted to original type");
        assertFalse(Ollivanders2API.getBlocks().isTemporarilyChangedBlock(above), "above block still in temporarily changed block tracking");
    }
}
