package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.BlockTransfiguration;
import net.pottercraft.ollivanders2.spell.FATUUS_AURUM;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base test class for {@link BlockTransfiguration} spells, covering target changes, blocked materials, success rate,
 * transfiguration mapping, radius, duration, permanence, reversion, and messaging. Subclasses supply the valid and
 * invalid target materials.
 *
 * @author Azami7
 */
abstract public class BlockTransfigurationTest extends O2SpellTestSuper {
    /**
     * @return a material this spell can transfigure
     */
    @NotNull
    abstract Material getValidTargetType();

    /**
     * @return a material this spell cannot transfigure
     */
    @NotNull
    abstract Material getInvalidTargetType();

    /**
     * Verify the spell hits an invalid target block but leaves it unchanged.
     */
    @Test
    void doCheckEffectTest() {
        // test for invalid target case, other cases in other tests
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = testWorld.getBlockAt(targetLocation);
        TestCommon.createBlockBase(target.getRelative(BlockFace.DOWN).getLocation(), 3);

        Material invalidType = getInvalidTargetType();
        target.setType(invalidType);

        O2Spell spell = castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        assertInstanceOf(BlockTransfiguration.class, spell);
        BlockTransfiguration blockTransfiguration = (BlockTransfiguration) spell;

        mockServer.getScheduler().performTicks(20);

        assertTrue(blockTransfiguration.hasHitBlock(), "hasHitBlock not set when spell hit invalid target " + blockTransfiguration.getLocation().getBlock().getType());
        assertFalse(blockTransfiguration.isTransfigured(target), "invalid block was transfigured");
        assertEquals(invalidType, target.getType(), "material for invalid block was changed");
    }

    /**
     * Verify the spell is killed and nothing is transfigured when the target is on the blocked materials list.
     */
    @Test
    void blockedMaterialsTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = testWorld.getBlockAt(targetLocation);
        target.setType(Ollivanders2Common.getUnbreakableMaterials().getFirst());

        BlockTransfiguration blockTransfiguration = (BlockTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        assertTrue(blockTransfiguration.getBlockedMaterials().contains(target.getType()), "unbreakable material not on the blocked list");

        mockServer.getScheduler().performTicks(20);
        assertTrue(blockTransfiguration.isKilled());
        assertFalse(blockTransfiguration.isTransfigured(target), "target transfigured when it is on the blocked list");
    }

    /**
     * Verify the transfiguration fails at zero skill (success rate under 100%) and succeeds at mastery skill.
     */
    @Test
    void effectSuccessRateTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = testWorld.getBlockAt(targetLocation);
        target.setType(getValidTargetType());

        BlockTransfiguration blockTransfiguration = (BlockTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 0);

        if (blockTransfiguration.getSuccessRate() < 100) {
            mockServer.getScheduler().performTicks(20);
            assertFalse(blockTransfiguration.isTransfigured(target), "transfiguration succeeded when skill is 0 and success rate < 100%");

            blockTransfiguration.kill();
            blockTransfiguration = (BlockTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
            mockServer.getScheduler().performTicks(20);
            assertTrue(blockTransfiguration.isTransfigured(target), "transfiguration failed when skill is mastery and success rate < 100%");
        }
    }

    /**
     * Verify a block is changed to its mapped material, or the default transfigure type when the map is empty.
     */
    @Test
    void transfigurationMapTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        BlockTransfiguration blockTransfiguration = (BlockTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);

        Map<Material, Material> transMap = blockTransfiguration.getTransfigurationMap();
        Block target = testWorld.getBlockAt(targetLocation);
        Material originalMaterial = getValidTargetType();
        Material expectedMaterial;

        if (!transMap.isEmpty()) {
            expectedMaterial = transMap.get(originalMaterial);
        }
        else {
            expectedMaterial = blockTransfiguration.getTransfigureType();
        }

        assertFalse(blockTransfiguration.getProjectilePassThroughMaterials().contains(originalMaterial));

        target.setType(originalMaterial);
        mockServer.getScheduler().performTicks(20);
        assertEquals(expectedMaterial, target.getType(), "block not changed to expected type");
    }

    /**
     * Verify the spell is killed and nothing changes when the target is already the transfiguration type.
     */
    @Test
    void sameMaterialTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = testWorld.getBlockAt(targetLocation);

        BlockTransfiguration blockTransfiguration = (BlockTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);

        Material transType = blockTransfiguration.getTransfigureType();
        target.setType(transType);

        mockServer.getScheduler().performTicks(20);

        assertEquals(transType, target.getType(), "target changed to unexpected material");
        assertTrue(blockTransfiguration.isKilled(), "spell not killed when target is already the transfiguration material type");
        assertFalse(blockTransfiguration.isTransfigured(), "target transfigured when target is already the transfiguration material type");
    }

    /**
     * Verify a temporary transfiguration stays alive until its duration expires, then is killed.
     */
    @Test
    void ageAndKillTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = testWorld.getBlockAt(targetLocation);
        target.setType(getValidTargetType());

        BlockTransfiguration blockTransfiguration = (BlockTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);

        if (!blockTransfiguration.isPermanent()) {
            mockServer.getScheduler().performTicks(20);
            assertTrue(blockTransfiguration.hasHitBlock());
            assertTrue(blockTransfiguration.isTransfigured(), "block was not transfigured");
            assertFalse(blockTransfiguration.isKilled(), "transfiguration killed unexpectedly");

            int ticksRemaining = blockTransfiguration.getEffectDuration() - blockTransfiguration.getAge();
            mockServer.getScheduler().performTicks(ticksRemaining - 1);
            assertFalse(blockTransfiguration.isKilled(), "spell killed before duration expired");

            mockServer.getScheduler().performTicks(2);
            assertTrue(blockTransfiguration.isKilled(), "spell not killed when duration passed");
        }
    }

    /**
     * Verify a permanent transfiguration is killed immediately on success and leaves the block changed.
     */
    @Test
    void isPermanentTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = testWorld.getBlockAt(targetLocation);
        target.setType(getValidTargetType());

        BlockTransfiguration blockTransfiguration = (BlockTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);

        if (blockTransfiguration.isPermanent()) {
            mockServer.getScheduler().performTicks(20);

            assertTrue(blockTransfiguration.isKilled());
            assertTrue(blockTransfiguration.isTransfigured());
            assertNotEquals(getValidTargetType(), target.getType(), "Target changed back to original type when transfiguration is permanent");
        }
    }

    /**
     * Verify a block already under an active transfiguration cannot be re-transfigured by another spell.
     */
    @Test
    void isAlreadyTransfiguredTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = testWorld.getBlockAt(targetLocation);
        target.setType(Material.STONE);

        FATUUS_AURUM fatuusAurum = (FATUUS_AURUM) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel, O2SpellType.FATUUS_AURUM);
        mockServer.getScheduler().performTicks(20);
        assertTrue(fatuusAurum.isTransfigured());
        assertTrue(Ollivanders2API.getBlocks().isTemporarilyChangedBlock(target), "target not added to changed block tracking");

        BlockTransfiguration blockTransfiguration = (BlockTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertFalse(fatuusAurum.isKilled()); // make sure prev spell still active
        assertFalse(blockTransfiguration.isTransfigured(), "transfigured a block that is already under a transfiguration");
        assertTrue(blockTransfiguration.isKilled(), "spell not killed when target is already under a transfiguration");
    }

    /**
     * Verify the effect radius stays within its min/max bounds and, for spells with radius greater than 1, changes
     * multiple blocks rather than just the target.
     */
    @Test
    void effectRadiusTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        testWorld.getBlockAt(targetLocation).setType(getValidTargetType());
        TestCommon.createBlockBase(targetLocation.getBlock().getRelative(BlockFace.DOWN).getLocation(), 5, getValidTargetType());

        BlockTransfiguration blockTransfiguration = (BlockTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);

        assertTrue(blockTransfiguration.getEffectRadius() >= blockTransfiguration.getMinRadius(), "radius not >= minRadius");
        assertTrue(blockTransfiguration.getEffectRadius() <= blockTransfiguration.getMaxRadius(), "radius not <= maxRadius");

        if (blockTransfiguration.getMaxRadius() > 1) {
            assertTrue(blockTransfiguration.hasHitBlock());

            assertNotEquals(getValidTargetType(), testWorld.getBlockAt(targetLocation).getType(), "block at target location not changed");

            if (!blockTransfiguration.isPermanent()) {
                assertTrue(Ollivanders2API.getBlocks().getBlocksChangedBySpell(blockTransfiguration).size() > 1, "spell did not change multiple blocks when maxRadius > 1 and spell level mastery");
            }
        }
    }

    /**
     * Verify a temporary transfiguration's computed duration falls within its min and max bounds.
     */
    @Test
    void effectDurationTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        BlockTransfiguration blockTransfiguration = (BlockTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(1);

        if (!blockTransfiguration.isPermanent()) {
            int duration = blockTransfiguration.getEffectDuration();

            assertTrue(duration >= blockTransfiguration.getMinDuration(), "duration not >= minDuration");
            assertTrue(duration <= blockTransfiguration.getMaxDuration(), "duration not <= maxDuration");
        }
    }

    /**
     * Verify the caster gets the success message when the transfiguration succeeds and the failure message when it
     * hits an invalid target.
     */
    @Test
    void successAndFailureMessageTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = testWorld.getBlockAt(targetLocation);
        target.setType(getValidTargetType());
        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 2, targetLocation.getZ()), 5); // create a base that is just past the target so that we know the projectile will eventually stop

        BlockTransfiguration blockTransfiguration = (BlockTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(blockTransfiguration.isTransfigured());
        String message = caster.nextMessage();
        assertNotNull(message, "caster did not receive success message");
        assertEquals(blockTransfiguration.getSuccessMessage(), TestCommon.cleanChatMessage(message), "caster did not get expected success message");
        blockTransfiguration.kill();

        Material invalidType = getInvalidTargetType();
        target.setType(invalidType);

        blockTransfiguration = (BlockTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(blockTransfiguration.isKilled());
        assertFalse(blockTransfiguration.isTransfigured(), "invalid target block was transfigured");
        message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message");
        assertEquals(blockTransfiguration.getFailureMessage(), TestCommon.cleanChatMessage(message), "caster did not get expected failure message");
    }

    /**
     * Verify killing a temporary transfiguration restores the block and clears it from change tracking, while a
     * permanent one leaves the block changed and untracked.
     */
    @Override
    @Test
    void revertTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        BlockTransfiguration blockTransfiguration = (BlockTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);

        Block target = testWorld.getBlockAt(targetLocation);
        Material originalMaterial = getValidTargetType();
        target.setType(originalMaterial);
        mockServer.getScheduler().performTicks(20);
        assertTrue(blockTransfiguration.isTransfigured());

        if (blockTransfiguration.isPermanent()) {
            assertTrue(Ollivanders2API.getBlocks().getBlocksChangedBySpell(blockTransfiguration).isEmpty(), "blocks added to temporary changed block list when transfiguration is permanent");
        }
        else {
            assertFalse(Ollivanders2API.getBlocks().getBlocksChangedBySpell(blockTransfiguration).isEmpty());
        }

        blockTransfiguration.kill();
        mockServer.getScheduler().performTicks(5);

        if (blockTransfiguration.isPermanent()) {
            assertNotEquals(originalMaterial, target.getType(), "Target changed back to original type when transfiguration is permanent");
        }
        else {
            assertEquals(originalMaterial, target.getType(), "Target not reverted back to original material when transfiguration killed");
            assertTrue(Ollivanders2API.getBlocks().getBlocksChangedBySpell(blockTransfiguration).isEmpty(), "changed blocks not removed from tracking when spell killed");
        }
    }
}
