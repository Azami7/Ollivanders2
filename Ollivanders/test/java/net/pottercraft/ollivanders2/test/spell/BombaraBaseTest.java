package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.BombardaBase;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract base test for Bombarda spell variants.
 *
 * <p>Tests block-breaking behavior including validation of blast resistance and hardness thresholds,
 * door-breaking capability, unbreakable materials, and explosion radius.</p>
 */
abstract public class BombaraBaseTest extends O2SpellTestSuper {
    /**
     * Must meet both the blast resistance and hardness requirements, cannot be unbreakable, should not be a door since
     * that is explicitly tested.
     *
     * @return a valid material
     */
    abstract Material getValidMaterial();

    /**
     * This should not be an unbreakable or a door/trapdoor/fence gate - those are explicitly tested. It
     * must also meet the blast resistance requirement for the spell.
     *
     * @return a material this bombarda spell will fail on due to hardness
     */
    abstract Material getInvalidHardnessMaterial();

    /**
     * This should not be an unbreakable or a door/trapdoor/fence gate - those are explicitly tested
     *
     * @return a material this bombarda spell will fail on due to blast resistance
     */
    abstract Material getInvalidBlastResistanceMaterial();

    /**
     * Comprehensive test covering all block-breaking validation rules.
     *
     * <p>Tests include:</p>
     *
     * <ul>
     * <li>Valid blocks are broken</li>
     * <li>Unbreakable materials are not broken</li>
     * <li>Door behavior matches the spell's {@code breaksDoors} setting</li>
     * <li>Blocks exceeding max blast resistance are not broken</li>
     * <li>Blocks exceeding max hardness are not broken</li>
     * <li>Explosion radius correctly breaks nearby valid blocks</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        // create a base layer to stop the spell and catch broken block items
        TestCommon.createBlockBase(new Location(testWorld, targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5, Material.BEDROCK);

        // test valid material
        Block block = testWorld.getBlockAt(targetLocation);
        block.setType(getValidMaterial());
        BombardaBase bombarda = (BombardaBase) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(bombarda.hasHitTarget(), "spell did not hit target");
        assertEquals(Material.AIR, block.getType(), "block was not broken");

        // test unbreakable
        Material unbreakable = Ollivanders2Common.getUnbreakableMaterials().getFirst();
        block.setType(unbreakable);
        bombarda = (BombardaBase) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(bombarda.hasHitTarget());
        assertEquals(unbreakable, block.getType(), "unbreakable material broken");

        // test doors
        Material door = Material.OAK_DOOR;
        block.setType(door);
        bombarda = (BombardaBase) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(bombarda.hasHitTarget());
        if (bombarda.doesBreakDoors()) {
            assertEquals(Material.AIR, block.getType(), "spell did not break door");
        }
        else {
            assertEquals(door, block.getType(), "spell broken a door when breakDoors = false");
        }

        // test blast resistance
        Material blastResistant = getInvalidBlastResistanceMaterial();
        block.setType(blastResistant);
        bombarda = (BombardaBase) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(bombarda.hasHitTarget());
        assertEquals(blastResistant, block.getType(), "block with higher blast resistance broken");

        // test hardness
        Material hard = getInvalidHardnessMaterial();
        block.setType(hard);
        bombarda = (BombardaBase) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(bombarda.hasHitTarget());
        assertEquals(hard, block.getType(), "block with higher hardness was broken");

        // test radius
        block.setType(getValidMaterial());
        Block east = block.getRelative(BlockFace.EAST);
        east.setType(getValidMaterial());
        Block west = block.getRelative(BlockFace.WEST);
        west.setType(getValidMaterial());
        Block outside = location.getBlock().getRelative(BlockFace.DOWN);
        outside.setType(getValidMaterial());
        bombarda = (BombardaBase) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        mockServer.getScheduler().performTicks(20);
        assertTrue(bombarda.hasHitTarget());
        assertEquals(Material.AIR, block.getType(), "block not broken");
        assertEquals(Material.AIR, east.getType(), "east block not broken");
        assertEquals(Material.AIR, west.getType(), "west block not broken");
        assertEquals(getValidMaterial(), outside.getType(), "block outside radius broken");
    }

    /**
     * {@inheritDoc}
     *
     * <p>No state changes to revert for Bombarda spells.</p>
     */
    @Override
    @Test
    void revertTest() {
    }
}
