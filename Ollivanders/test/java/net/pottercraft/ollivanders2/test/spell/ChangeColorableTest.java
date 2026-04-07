package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.ChangeColorable;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract tests for {@link ChangeColorable} colour change spells.
 *
 * <p>Verifies the shared behaviour across all colour change variants: non-colorable blocks produce a failure
 * message, colorable blocks are recolored, non-sheep entities produce a failure message, and sheep are
 * recolored to the spell's target color.</p>
 */
abstract public class ChangeColorableTest extends O2SpellTestSuper {
    /**
     * Verify the spell handles different target types correctly.
     * <ul>
     *   <li>Non-colorable block (CHEST): Spell kills with failure message</li>
     *   <li>Colorable block (wool): Block recolored to spell's color, no failure message</li>
     *   <li>Non-colorable entity (COW): Spell kills with failure message</li>
     *   <li>Sheep: Sheep dye color changed to spell's color</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        Block target = testWorld.getBlockAt(targetLocation);
        target.setType(Material.CHEST);

        ChangeColorable changeColorable = (ChangeColorable) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(changeColorable.isKilled(), "Spell not killed when target hit");
        String message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message when block is not colorable.");
        TestCommon.clearMessageQueue(caster);

        changeColorable = (ChangeColorable) castSpell(caster, location, targetLocation);

        Material targetType = changeColorable.getColor().getColoredMaterial("WOOL");
        assertNotNull(targetType);
        if (targetType == Material.WHITE_WOOL)
            target.setType(Material.BLACK_WOOL);
        else
            target.setType(Material.WHITE_WOOL);

        mockServer.getScheduler().performTicks(20);
        assertTrue(changeColorable.isKilled(), "Spell not killed when target hit");
        message = caster.nextMessage();
        assertNull(message, "caster received a message when success does not send a message");
        assertEquals(targetType, target.getType(), "Target block not changed to expected type");

        target.setType(Material.AIR);
        Entity targetEntity = testWorld.spawnEntity(targetLocation, EntityType.COW);
        changeColorable = (ChangeColorable) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(changeColorable.isKilled(), "spell did not stop when hitting a living entity");
        message = caster.nextMessage();
        assertNotNull(message, "caster did not receive failure message when entity is not colorable.");
        TestCommon.clearMessageQueue(caster);

        targetEntity.remove();
        targetEntity = testWorld.spawnEntity(targetLocation, EntityType.SHEEP);
        changeColorable = (ChangeColorable) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(changeColorable.isKilled());
        assertEquals(changeColorable.getColor().getDyeColor(), ((Sheep)targetEntity).getColor(), "Target sheep is not expected color");
        message = caster.nextMessage();
        assertNull(message, "caster received a message when success does not send a message");
    }

    @Override
    @Test
    void revertTest() {
        // no revert actions
    }
}
