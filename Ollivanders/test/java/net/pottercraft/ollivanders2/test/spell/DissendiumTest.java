package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.DISSENDIUM;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the {@link DISSENDIUM} spell.
 * <p>
 * Verifies DISSENDIUM's behavior across all three {@link Openable} target families (doors,
 * trapdoors, fence gates), plus failure paths (invalid target, already-open target, blocked by
 * COLLOPORTUS), the skill-based open duration, and revert semantics when the spell is killed
 * or the target block is replaced mid-window.
 * </p>
 *
 * @author Azami7
 */
public class DissendiumTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.DISSENDIUM;
    }

    /**
     * No-op: DISSENDIUM has no spell-specific construction state worth asserting beyond what
     * the casting constructor sets, which is covered by
     * {@link #openTimeTest()}.
     */
    @Override
    @Test
    void spellConstructionTest() {
        // no special spell construction code
    }

    /**
     * Cover the target-validation paths: a non-Openable target (chest) is rejected; each Openable family (door, fence
     * gate, trapdoor) opens; and an already-open target kills the spell without reopening.
     * <p>
     * The {@code performTicks(1)} after each {@link DISSENDIUM#kill()} works around a MockBukkit timing issue where
     * killed spells linger one tick before pruning; without the gap the next sub-test's casts can race against stale
     * state. Do not remove.
     * </p>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        // non-door cannot be targeted
        Block target = testWorld.getBlockAt(targetLocation);
        target.setType(Material.CHEST);
        DISSENDIUM dissendium = (DISSENDIUM) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(dissendium.isKilled(), "spell not killed when hitting invalid target");
        assertFalse(dissendium.isDoorOpened(), "spell opened an invalid target block");

        // door can be targeted
        target.setType(Material.OAK_DOOR);
        assertTrue(Ollivanders2Common.getDoors().contains(target.getType()));
        dissendium = (DISSENDIUM) castSpell(caster, location, targetLocation);
        assertTrue(dissendium.getAllowedMaterials().contains(target.getType()));
        mockServer.getScheduler().performTicks(20);
        assertTrue(dissendium.isDoorOpened(), "failed to open door");
        BlockData blockData = target.getBlockData();
        assertInstanceOf(Openable.class, blockData);
        assertTrue(((Openable)blockData).isOpen(), "door is not open");
        dissendium.kill();
        mockServer.getScheduler().performTicks(1);

        // gate can be targeted
        target.setType(Material.DARK_OAK_FENCE_GATE);
        assertTrue(Ollivanders2Common.getDoors().contains(target.getType()));
        dissendium = (DISSENDIUM) castSpell(caster, location, targetLocation);
        assertTrue(dissendium.getAllowedMaterials().contains(target.getType()));
        mockServer.getScheduler().performTicks(20);
        assertTrue(dissendium.isDoorOpened(), "failed to open gate");
        blockData = target.getBlockData();
        assertInstanceOf(Openable.class, blockData);
        assertTrue(((Openable)blockData).isOpen(), "gate is not open");
        dissendium.kill();
        mockServer.getScheduler().performTicks(1);

        // trapdoor can be targeted
        target.setType(Material.ACACIA_TRAPDOOR);
        assertTrue(Ollivanders2Common.getDoors().contains(target.getType()));
        dissendium = (DISSENDIUM) castSpell(caster, location, targetLocation);
        assertTrue(dissendium.getAllowedMaterials().contains(target.getType()));
        mockServer.getScheduler().performTicks(20);
        assertTrue(dissendium.isDoorOpened(), "failed to open trapdoor");
        blockData = target.getBlockData();
        assertInstanceOf(Openable.class, blockData);
        assertTrue(((Openable)blockData).isOpen(), "trapdoor is not open");
        dissendium.kill();
        mockServer.getScheduler().performTicks(1);

        // already opened door has no change
        blockData = target.getBlockData();
        ((Openable)blockData).setOpen(true);
        target.setBlockData(blockData);
        dissendium = (DISSENDIUM) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(dissendium.isKilled());
        assertFalse(dissendium.isDoorOpened(), "dissendium.doorOpened set to true when door already open");
        blockData = target.getBlockData();
        assertTrue(((Openable)blockData).isOpen(), "trapdoor that was open is now not open");
    }

    /**
     * Verify the open time limits to {@link DISSENDIUM#minOpenTime} at zero experience and to
     * {@link DISSENDIUM#maxOpenTime} at twice mastery.
     */
    @Test
    void openTimeTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 20, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        DISSENDIUM dissendium = (DISSENDIUM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 0);
        assertEquals(DISSENDIUM.minOpenTime, dissendium.getOpenTime(), "Open time not set to minOpenTime when spell experience is 0");

        dissendium = (DISSENDIUM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        assertEquals(DISSENDIUM.maxOpenTime, dissendium.getOpenTime(), "Open time not set to maxOpenTime when spell experience is 2x mastery");
    }

    /**
     * Verify DISSENDIUM is killed without opening a door that is locked by an active COLLOPORTUS stationary spell.
     */
    @Test
    void colloportusTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 20, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = testWorld.getBlockAt(targetLocation);
        target.setType(Material.OAK_DOOR);
        castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel, O2SpellType.COLLOPORTUS);
        mockServer.getScheduler().performTicks(20);
        assertTrue(Ollivanders2API.getStationarySpells().checkLocationForStationarySpell(targetLocation, O2StationarySpellType.COLLOPORTUS));

        DISSENDIUM dissendium = (DISSENDIUM) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(dissendium.isKilled(), "dissendium not killed when colloportus present");
        assertFalse(dissendium.isDoorOpened(), "dissendium opened door in colloportus");
    }

    /**
     * Verify revert closes the trapdoor on manual kill and on duration expiry, and that swapping the opened block to
     * a non-Openable (chest) mid-window makes revert bail out via its defensive {@code instanceof Openable} check
     * rather than throw.
     */
    @Override
    @Test
    void revertTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = testWorld.getBlockAt(targetLocation);
        target.setType(Material.DARK_OAK_TRAPDOOR);
        assertTrue(Ollivanders2Common.getDoors().contains(target.getType()));
        BlockData blockData = target.getBlockData();
        assertInstanceOf(Openable.class, blockData);

        DISSENDIUM dissendium = (DISSENDIUM) castSpell(caster, location, targetLocation);
        assertTrue(dissendium.getAllowedMaterials().contains(target.getType()));
        mockServer.getScheduler().performTicks(20);
        assertTrue(dissendium.isDoorOpened());
        assertTrue(((Openable)blockData).isOpen());
        dissendium.kill();
        mockServer.getScheduler().performTicks(1);
        assertTrue(dissendium.isKilled());
        blockData = target.getBlockData();
        assertFalse(((Openable) blockData).isOpen(), "gate is still open after dissendium is killed");

        dissendium = (DISSENDIUM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 0);
        mockServer.getScheduler().performTicks(20);
        assertTrue(((Openable)blockData).isOpen());
        mockServer.getScheduler().performTicks(DISSENDIUM.minOpenTime - 21);
        assertFalse(dissendium.isKilled(), "dissendium killed before minOpenTime elapsed");
        blockData = target.getBlockData();
        assertTrue(((Openable) blockData).isOpen(), "door not open when minOpenTime has not elapsed");
        mockServer.getScheduler().performTicks(25);
        assertTrue(dissendium.isKilled(), "dissendium not killed after open time elapsed");

        dissendium = (DISSENDIUM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 0);
        mockServer.getScheduler().performTicks(20);
        target.setType(Material.CHEST);
        dissendium.kill(); // make sure no runtime issue happens here because the target block is no longer a door
        assertTrue(dissendium.isKilled());
    }
}
