package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.FINESTRA;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the FINESTRA spell.
 *
 * <p>Verifies that the spell correctly breaks glass blocks in a radius around the target, ignores
 * non-glass blocks, and respects the maximum radius cap.</p>
 */
public class FinestraTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.FINESTRA;
    }

    /**
     * Verify the spell handles different block types and radius scenarios correctly.
     * <ul>
     *   <li>Non-glass block (STONE): Spell kills but does not break the block</li>
     *   <li>Single glass block with low experience: Glass is broken</li>
     *   <li>Multiple glass types in radius: All glass variants broken, non-glass unaffected, outside-radius glass intact</li>
     *   <li>High experience (200): Radius capped at max, outside-radius glass still intact</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        Block target = testWorld.getBlockAt(targetLocation);
        target.setType(Material.STONE);

        FINESTRA finestra = (FINESTRA) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(finestra.isKilled(), "Finestra spell not killed when target hit");
        assertEquals(Material.STONE, target.getType(), "Target type was changed unexpectedly");

        target.setType(Material.GLASS);
        finestra = (FINESTRA) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 1);
        mockServer.getScheduler().performTicks(20);
        assertTrue(finestra.isKilled(), "Finestra spell not killed when target hit");
        assertEquals(Material.AIR, target.getType(), "Target block not changed to air when hit");
        // MockBukkit does not have Block.breakNaturally fully implemented so we cannot check for the dropped block item

        finestra = (FINESTRA) castSpell(caster, location, targetLocation);
        target.setType(Material.GLASS);
        Block notGlass = target.getRelative(BlockFace.EAST);
        notGlass.setType(Material.OAK_LOG);
        Block glass1 = target.getRelative(BlockFace.WEST);
        glass1.setType(Material.YELLOW_STAINED_GLASS);
        Block glass2 = target.getRelative(BlockFace.SOUTH);
        glass2.setType(Material.RED_STAINED_GLASS);
        Block glass3 = target.getRelative(BlockFace.NORTH);
        glass3.setType(Material.GLASS_PANE);
        Block outsideRadius = testWorld.getBlockAt(new Location(testWorld, targetLocation.getX(), targetLocation.getY(), targetLocation.getZ() + finestra.getMaxRadius() + 1));
        outsideRadius.setType(Material.GLASS);
        mockServer.getScheduler().performTicks(20);
        assertEquals(Material.AIR, target.getType());
        assertEquals(Material.AIR, glass1.getType(), "glass1 not broken");
        assertEquals(Material.AIR, glass2.getType(), "glass2 not broken");
        assertEquals(Material.AIR, glass3.getType(), "glass3 not broken");
        assertEquals(Material.OAK_LOG, notGlass.getType());
        assertEquals(Material.GLASS, outsideRadius.getType(), "block outside max radius broken");

        target.setType(Material.GLASS);
        castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 200);
        mockServer.getScheduler().performTicks(20);
        assertEquals(Material.AIR, target.getType());
        assertEquals(Material.GLASS, outsideRadius.getType(), "block outside max radius broken");
    }

    @Override
    @Test
    void revertTest() {
        // no revert actions
    }
}
