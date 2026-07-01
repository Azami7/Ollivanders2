package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.DEFODIO;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the DEFODIO spell, the Gouging Charm.
 *
 * <p>Once its projectile strikes a block, DEFODIO digs forward one block at a time along the cast vector, breaking
 * each block, until it reaches a blocked material (water, lava, fire), a block it cannot break, or has mined its
 * skill-scaled length. The dig length scales deterministically with the caster's skill, so these tests are
 * deterministic from the cast experience alone.</p>
 *
 * <p>Each digging cast aims straight down. DEFODIO advances by adding the cast vector to the block-aligned current
 * block, so the vector must be axis-aligned or the path drifts; the test harness's {@code faceTarget} cannot
 * produce a perfectly horizontal vector (its vertical component is always at least half a block), but a target
 * directly below the caster yields an exact (0, -1, 0) vector, giving a clean vertical dig down a known column.</p>
 *
 * @author Azami7
 */
public class DefodioTest extends O2SpellTestSuper {
    /**
     * Experience giving a usesModifier of 12 -> dig length 3 (12/4), small enough to leave unmined blocks beyond
     * the dig in a short test column.
     */
    private static final int experience = 12;

    /**
     * Generous tick budget: projectile travel plus the per-block cooldown (a quarter second) times the dig length,
     * with headroom. Far exceeds what a length-3 dig needs.
     */
    private static final int digTicks = 80;

    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.DEFODIO;
    }

    /**
     * Tests mining a contiguous column of blocks down to the dig length.
     *
     * <p>A vertical column of stone is placed below the caster, who casts straight down. With a single cast,
     * verifies that exactly the dig-length blocks from the impact point downward are cleared to air and the next
     * block below is left intact, and that the spell ends afterward.</p>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        PlayerMock caster = mockServer.addPlayer();

        int cx = (int) location.getX();
        int cz = (int) location.getZ();
        int topY = (int) location.getY() - 3; // column starts a few blocks below the caster so the projectile travels through air first

        // contiguous stone column below the caster, longer than the dig so a block survives past the dig length
        for (int y = topY; y >= topY - 8; y--)
            new Location(testWorld, cx, y, cz).getBlock().setType(Material.STONE);

        // target directly below the caster gives an exact (0,-1,0) cast vector
        Location targetLocation = new Location(testWorld, cx, location.getY() - 5, cz);
        DEFODIO defodio = (DEFODIO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, experience);
        int digLength = defodio.getRemainingCount();
        assertTrue(digLength >= 1 && digLength < 8, "test setup invalid: dig length must fit within the column");

        mockServer.getScheduler().performTicks(digTicks);

        assertTrue(defodio.isKilled(), "spell did not end after mining its full length");
        for (int i = 0; i < digLength; i++)
            assertEquals(Material.AIR, new Location(testWorld, cx, topY - i, cz).getBlock().getType(), "block within the dig length was not mined");
        assertEquals(Material.STONE, new Location(testWorld, cx, topY - digLength, cz).getBlock().getType(), "block beyond the dig length was mined");
    }

    /**
     * Tests that the dig stops when it reaches a blocked material before exhausting its length.
     *
     * <p>Two stone blocks are placed, then water, then another stone, down the column. With a dig length of three,
     * the spell should mine the two stone blocks, stop at the water (a blocked material it cannot dig through), and
     * never reach the stone beyond it.</p>
     */
    @Test
    void blockedMaterialStopsDigTest() {
        World testWorld = mockServer.addSimpleWorld("DefodioBlockedMaterial");
        Location location = getNextLocation(testWorld);
        PlayerMock caster = mockServer.addPlayer();

        int cx = (int) location.getX();
        int cz = (int) location.getZ();
        int topY = (int) location.getY() - 3;

        new Location(testWorld, cx, topY, cz).getBlock().setType(Material.STONE);
        new Location(testWorld, cx, topY - 1, cz).getBlock().setType(Material.STONE);
        new Location(testWorld, cx, topY - 2, cz).getBlock().setType(Material.WATER);
        new Location(testWorld, cx, topY - 3, cz).getBlock().setType(Material.STONE);

        Location targetLocation = new Location(testWorld, cx, location.getY() - 5, cz);
        DEFODIO defodio = (DEFODIO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, experience);
        assertTrue(defodio.getRemainingCount() >= 3, "test setup invalid: dig length must reach past the water");

        mockServer.getScheduler().performTicks(digTicks);

        assertTrue(defodio.isKilled(), "spell did not end when it hit a blocked material");
        assertEquals(Material.AIR, new Location(testWorld, cx, topY, cz).getBlock().getType(), "first stone was not mined");
        assertEquals(Material.AIR, new Location(testWorld, cx, topY - 1, cz).getBlock().getType(), "second stone was not mined");
        assertEquals(Material.WATER, new Location(testWorld, cx, topY - 2, cz).getBlock().getType(), "water was dug through");
        assertEquals(Material.STONE, new Location(testWorld, cx, topY - 3, cz).getBlock().getType(), "block past the water was mined");
    }

    /**
     * Tests that the dig length scales with the caster's skill and is clamped at both ends.
     *
     * <p>Verifies that very low skill floors the dig length to 1, and very high skill clamps it to the spell's
     * maximum. The length is read immediately after the cast, before any block is mined.</p>
     */
    @Test
    void digLengthScalesWithSkillTest() {
        World testWorld = mockServer.addSimpleWorld("DefodioDigLength");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX(), location.getY() - 5, location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        DEFODIO low = (DEFODIO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 1);
        DEFODIO high = (DEFODIO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 1000);

        assertEquals(1, low.getRemainingCount(), "low-skill dig length was not floored to 1");
        assertEquals(high.getMaxDepth(), high.getRemainingCount(), "high-skill dig length was not clamped to the maximum");
    }

    /**
     * DEFODIO has no revert action - mined blocks are not restored.
     */
    @Override
    @Test
    void revertTest() {
        // nothing to revert
    }
}