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
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AGUAMENTI}. Extends {@link BlockTransfigurationTest} for the shared transfiguration tests.
 *
 * @author Azami7
 */
public class AguamentiTest extends BlockTransfigurationTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.AGUAMENTI;
    }

    /**
     * @return DIRT, a solid block the projectile strikes before the air block it converts to water
     */
    @Override
    @NotNull
    Material getValidTargetType() {
        return Material.DIRT;
    }

    @Override
    @NotNull
    Material getInvalidTargetType() {
        return Material.FIRE;
    }

    /**
     * Verify the special targeting places water in the block just before the air the projectile hit.
     */
    @Override
    @Test
    void transfigurationMapTest() {
        World testWorld = mockServer.addSimpleWorld("aguamenti");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(targetLocation, 5);
        AGUAMENTI aguamenti = (AGUAMENTI) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);

        mockServer.getScheduler().performTicks(20);
        Block target = aguamenti.getTargetBlock();
        assertTrue(aguamenti.hasHitBlock());
        assertFalse(aguamenti.isKilled());
        assertNotNull(target, "target block is null");
        assertEquals(Material.WATER, target.getType(), "block not changed to expected type");
    }

    /**
     * Not applicable to AGUAMENTI: it only targets air and converts it to water, so the target can never already be
     * the output material.
     */
    @Override
    @Test
    void sameMaterialTest() {
        // this cannot happen for aguamenti because it only targets AIR which it changes it to WATER so they can never be the same
    }

    /**
     * Verify AGUAMENTI fails on a block already under another transfiguration.
     */
    @Override
    @Test
    void isAlreadyTransfiguredTest() {
        World testWorld = mockServer.addSimpleWorld("aguamenti");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

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
     * Verify AGUAMENTI sends the success message on a valid cast and the failure message on a blocked material.
     */
    @Override
    @Test
    void successAndFailureMessageTest() {
        World testWorld = mockServer.addSimpleWorld("aguamenti");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        testWorld.getBlockAt(targetLocation).setType(Material.STONE);

        BlockTransfiguration blockTransfiguration = (BlockTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(blockTransfiguration.isTransfigured());
        String message = caster.nextMessage();
        assertNotNull(message, "caster did not receive success message");
        assertEquals(blockTransfiguration.getSuccessMessage(), TestCommon.cleanChatMessage(message), "caster did not get expected success message");
        blockTransfiguration.kill();

        testWorld.getBlockAt(targetLocation).setType(Material.SOUL_CAMPFIRE);
        blockTransfiguration = (BlockTransfiguration) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertFalse(blockTransfiguration.isTransfigured()); // should fail because the block is FIRE
        message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message");
        assertEquals(blockTransfiguration.getFailureMessage(), TestCommon.cleanChatMessage(message), "caster did not get expected failure message");
    }
}
