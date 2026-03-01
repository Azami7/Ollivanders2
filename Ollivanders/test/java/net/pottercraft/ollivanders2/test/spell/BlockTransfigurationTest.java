package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
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
 * Abstract base class for testing BlockTransfiguration spell implementations.
 *
 * <p>Provides comprehensive test coverage for block transfiguration spells including:
 * <ul>
 * <li>Effect validation and target block changes</li>
 * <li>Invalid target handling and blocked materials</li>
 * <li>Success rate and skill-based behavior</li>
 * <li>Transfiguration mapping and material type handling</li>
 * <li>Edge cases: same material targets, permanent vs. temporary effects</li>
 * <li>Duration and radius mechanics</li>
 * <li>Temporary block tracking and reversion</li>
 * </ul>
 *
 * <p>Subclasses must implement {@link #getSpellType()}, {@link #getValidTargetType()},
 * and {@link #getInvalidTargetType()} to define the specific spell being tested.</p>
 *
 * @author Azami7
 */
abstract public class BlockTransfigurationTest extends O2SpellTestSuper {
    /**
     * Get the valid target block material for this spell.
     *
     * @return a material type that can be transfigured by this spell
     */
    @NotNull
    abstract Material getValidTargetType();

    /**
     * Get an invalid target block material that should block transfiguration.
     *
     * @return a material type that cannot be transfigured, or null if no invalid type exists
     */
    @NotNull
    abstract Material getInvalidTargetType();

    /**
     * Should not be needed for most block transfiguration spells, specific spells should override as needed
     */
    @Override
    @Test
    void spellConstructionTest() {
    }

    /**
     * Tests spell behavior when targeting invalid (blocked) materials.
     *
     * <p>Verifies that the spell hits the target but fails to transfigure blocks
     * that are on the blocked materials list (e.g., unbreakable materials).</p>
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

        assertTrue(blockTransfiguration.hasHitTarget(), "hitTarget not set when spell hit invalid target " + blockTransfiguration.location.getBlock().getType());
        assertFalse(blockTransfiguration.isBlockTransfigured(target), "invalid block was transfigured");
        assertEquals(invalidType, target.getType(), "material for invalid block was changed");
    }

    /**
     * Tests that blocked materials cannot be transfigured.
     * Tests that blocked materials cannot be transfigured.
     *
     * <p>Verifies the spell is killed when the target block is on the blocked list
     * and no transfiguration occurs.</p>
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
        assertFalse(blockTransfiguration.isBlockTransfigured(target), "target transfigured when it is on the blocked list");
    }

    /**
     * Tests success rate mechanics based on player spell skill level.
     *
     * <p>Verifies that spells fail with low skill (when success rate < 100%) and succeed
     * at spell mastery level (skill = 100).</p>
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
            assertFalse(blockTransfiguration.isBlockTransfigured(target), "transfiguration succeeded when skill is 0 and success rate < 100%");

            blockTransfiguration.kill();
            blockTransfiguration = (BlockTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
            mockServer.getScheduler().performTicks(20);
            assertTrue(blockTransfiguration.isBlockTransfigured(target), "transfiguration failed when skill is mastery and success rate < 100%");
        }
    }

    /**
     * Tests custom transfiguration mapping for spells with source-to-target material mappings.
     *
     * <p>Verifies that blocks are transformed according to the spell's transfiguration map,
     * or to the default transfigure type if no mapping exists.</p>
     */
    @Test
    void transfigurationMapTest() {
        Ollivanders2.debug = true;

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
     * Tests spell behavior when the target block is already the transfiguration type.
     *
     * <p>Verifies that the spell is killed and no transfiguration occurs when the
     * target block material is already the spell's transfiguration target type.</p>
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
     * Tests spell duration and automatic termination for temporary transfigurations.
     *
     * <p>Verifies that non-permanent spells run for their specified duration and are
     * killed when the duration expires.</p>
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
            assertTrue(blockTransfiguration.hasHitTarget());
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
     * Tests permanent transfiguration spells that are not reverted after duration expires.
     *
     * <p>Verifies that permanent spells are immediately killed after transfiguration
     * and transfigured blocks remain in their changed state.</p>
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
     * Tests that blocks already affected by another transfiguration spell cannot be re-transfigured.
     *
     * <p>Verifies that the spell fails when attempting to transfigure a block that is
     * already being affected by another active transfiguration.</p>
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
     * Tests the effect radius of the spell and transfiguration of multiple blocks.
     *
     * <p>For spells with radius > 1, verifies that multiple blocks within the radius
     * are transfigured, not just the exact target block.</p>
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
            assertTrue(blockTransfiguration.hasHitTarget());

            assertNotEquals(getValidTargetType(), testWorld.getBlockAt(targetLocation).getType(), "block at target location not changed");

            if (!blockTransfiguration.isPermanent()) {
                assertTrue(Ollivanders2API.getBlocks().getBlocksChangedBySpell(blockTransfiguration).size() > 1, "spell did not change multiple blocks when maxRadius > 1 and spell level mastery");
            }
        }
    }

    /**
     * Tests that spell duration is within configured min/max bounds for temporary spells.
     *
     * <p>Verifies that the duration of temporary transfigurations is properly calculated
     * and falls within the valid range.</p>
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
     * Tests that appropriate success and failure messages are sent to the caster.
     *
     * <p>Verifies that the caster receives the success message when transfiguration succeeds
     * and the failure message when transfiguration fails.</p>
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
        assertFalse(blockTransfiguration.isTransfigured(), "block trans"); // should fail because the block is already transfigured
        message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message");
        assertEquals(blockTransfiguration.getFailureMessage(), TestCommon.cleanChatMessage(message), "caster did not get expected failure message");
    }

    /**
     * Tests that temporary transfigurations are properly reverted when the spell ends.
     *
     * <p>Verifies that transfigured blocks are restored to their original material type
     * when the spell duration expires or is explicitly killed. For permanent spells,
     * verifies that blocks remain in their transfigured state.</p>
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
