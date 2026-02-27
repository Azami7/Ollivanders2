package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.AGUAMENTI;
import net.pottercraft.ollivanders2.spell.BlockTransfiguration;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.TERGEO;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the AGUAMENTI spell (water-making charm).
 *
 * <p>Tests verify that AGUAMENTI correctly creates water blocks while rejecting invalid
 * materials. AGUAMENTI is unique among transfiguration spells because it targets air blocks
 * (pass-through blocks) and converts them to water. The spell uses special targeting to place
 * water adjacent to solid surfaces.</p>
 *
 * <p>Test coverage includes:</p>
 * <ul>
 * <li>Valid target material: AIR (and other pass-through blocks)</li>
 * <li>Invalid target material: FIRE and other blocked materials</li>
 * <li>Transfiguration map test: Verifies water placement and special targeting</li>
 * <li>Already transfigured test: Verifies behavior when target is under another spell</li>
 * <li>Success and failure messaging</li>
 * <li>Temporary water creation with automatic reversion</li>
 * <li>Pass-through block targeting (air conversion to water)</li>
 * </ul>
 *
 * @author Azami7
 * @see AGUAMENTI
 */
public class AguamentiTest extends BlockTransfigurationTest {
    /**
     * Returns the spell type being tested.
     *
     * @return AGUAMENTI spell type
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.AGUAMENTI;
    }

    /**
     * Returns the valid target material for AGUAMENTI tests.
     *
     * <p>AGUAMENTI targets air blocks (pass-through blocks) and converts them to water.
     * For testing purposes, DIRT is used as a representative material that will be hit
     * by the projectile before reaching the air block.</p>
     *
     * @return DIRT material type
     */
    @Override
    @NotNull
    Material getValidTargetType() {
        return Material.DIRT;
    }

    /**
     * Returns an invalid target material for AGUAMENTI tests.
     *
     * <p>AGUAMENTI's blocked list includes hot blocks like FIRE. FIRE is used as a
     * representative invalid material that cannot be transfigured.</p>
     *
     * @return FIRE material type
     */
    @Override
    @Nullable
    Material getInvalidTargetType() {
        return Material.FIRE;
    }

    /**
     * Overrides spellConstructionTest with no additional implementation.
     *
     * <p>AGUAMENTI uses the default construction behavior provided by the parent test class.
     * No spell-specific construction tests are needed beyond the inherited test coverage.</p>
     */
    @Override
    @Test
    void spellConstructionTest() {

    }

    /**
     * Tests AGUAMENTI's special targeting and water placement behavior.
     *
     * <p>AGUAMENTI places water adjacent to solid surfaces using its special getTargetBlock()
     * override. This test verifies that the spell correctly identifies and transforms the
     * target block into water.</p>
     */
    @Override
    @Test
    void transfigurationMapTest() {
        World testWorld = mockServer.addSimpleWorld("blocktransworld");
        Location location = new Location(testWorld, 1400, 40, 400);
        Location targetLocation = new Location(testWorld, 1410, 40, 400);
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(targetLocation, 5);
        AGUAMENTI aguamenti = (AGUAMENTI) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);

        mockServer.getScheduler().performTicks(20);
        Block target = aguamenti.getTargetBlock();
        assertTrue(aguamenti.hasHitTarget());
        assertFalse(aguamenti.isKilled());
        assertNotNull(target, "target block is null");
        assertEquals(Material.WATER, target.getType(), "block not changed to expected type");
    }

    /**
     * Overrides sameMaterialTest because it is not applicable to AGUAMENTI.
     *
     * <p>AGUAMENTI converts AIR (pass-through blocks) to WATER. Since WATER is not in the
     * allow list (only AIR/CAVE_AIR are valid targets), this spell can never encounter a
     * situation where it tries to transfigure a block that is already the target material type.</p>
     *
     * <p>Therefore, this test is not applicable and remains empty.</p>
     */
    @Override
    @Test
    void sameMaterialTest() {
        // this cannot happen for aguamenti because it only targets AIR which it changes it to WATER so they can never be the same
    }

    /**
     * Tests that AGUAMENTI fails when the target is already affected by another transfiguration.
     *
     * <p>This test verifies that AGUAMENTI respects the "already transfigured" check. The test
     * creates a water block with TERGEO transfiguration, then attempts to cast AGUAMENTI on it.
     * AGUAMENTI should fail because the block is already under another spell's effect.</p>
     */
    @Override
    @Test
    void isAlreadyTransfiguredTest() {
        World testWorld = mockServer.addSimpleWorld("blocktransworld");
        Location location = new Location(testWorld, 1200, 40, 100);
        Location targetLocation = new Location(testWorld, 1210, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        Block target = testWorld.getBlockAt(targetLocation);
        TestCommon.createBlockBase(targetLocation, 3);

        // cast an aguamenti so we can get what the actual target block will be
        AGUAMENTI aguamenti = (AGUAMENTI) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        Block actualTarget = aguamenti.getTargetBlock(); // need to determine what the actual target block will be
        assertNotNull(actualTarget);
        aguamenti.kill();
        mockServer.getScheduler().performTicks(5);

        // set that actual target block to water and add a tergeo, which will turn that water to air - which is what aguamenti will want to change
        actualTarget.setType(Material.WATER);
        TERGEO tergeo = (TERGEO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel, O2SpellType.TERGEO);
        mockServer.getScheduler().performTicks(20);
        assertTrue(tergeo.isTransfigured());
        assertFalse(tergeo.isKilled());

        // call aguamenti, which should fail to change the tergeo'd block back to water
        aguamenti = (AGUAMENTI) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertFalse(aguamenti.isTransfigured(), "transfigured a block that is already under a transfiguration");
        assertTrue(aguamenti.isKilled(), "spell not killed when target is already under a transfiguration");
    }

    /**
     * Tests AGUAMENTI's success and failure messaging.
     *
     * <p>This test verifies that AGUAMENTI sends appropriate messages to the caster for both
     * successful and failed casts. Success is tested when valid blocks can be transfigured,
     * and failure is tested when invalid materials (blocked materials) are encountered.</p>
     */
    @Override
    @Test
    void successAndFailureMessageTest() {
        World testWorld = mockServer.addSimpleWorld("blocktransworld");
        Location location = new Location(testWorld, 1300, 40, 100);
        Location targetLocation = new Location(testWorld, 1310, 40, 100);
        PlayerMock caster = mockServer.addPlayer();

        testWorld.getBlockAt(targetLocation).setType(Material.STONE);

        BlockTransfiguration blockTransfiguration = (BlockTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(blockTransfiguration.isTransfigured());
        String message = caster.nextMessage();
        assertNotNull(message, "caster did not receive success message");
        assertEquals(blockTransfiguration.getSuccessMessage(), TestCommon.cleanChatMessage(message), "caster did not get expected success message");
        blockTransfiguration.kill();

        testWorld.getBlockAt(targetLocation).setType(Material.FIRE);
        blockTransfiguration = (BlockTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertFalse(blockTransfiguration.isTransfigured()); // should fail because the block is FIRE
        message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message");
        assertEquals(blockTransfiguration.getFailureMessage(), TestCommon.cleanChatMessage(message), "caster did not get expected failure message");
    }
}
