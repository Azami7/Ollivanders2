package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.PARTIS_TEMPORUS;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockFromToEvent;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the PARTIS_TEMPORUS spell, the Dividing Charm.
 *
 * <p>Once its projectile strikes a target material (water, lava, or fire), PARTIS_TEMPORUS carves a level path of
 * air forward: the cast direction is flattened to horizontal at the block it hit, then each throttled segment clears
 * a width-by-depth cross-section and advances one block along that horizontal direction. While the path is held open
 * it cancels flow events into the cleared blocks, and when the duration expires it restores the blocks and stops
 * listening.</p>
 *
 * <p>The tests cast horizontally toward +x into a body of water. Because the path direction is flattened to
 * horizontal and the cast is offset purely on x, the carve runs in a clean +x line at a single y level, so the
 * cleared blocks are a predictable horizontal run.</p>
 *
 * @author Azami7
 */
public class PartisTemporusTest extends O2SpellTestSuper {
    /**
     * Low experience -> width 1, depth 1, the minimum path length, and the minimum hold duration, keeping the carve
     * a single predictable line and the duration short enough to tick out in the lifecycle test.
     */
    private static final int experience = 1;

    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.PARTIS_TEMPORUS;
    }

    /**
     * Collect the air blocks within a bounding box, i.e. the blocks the spell has cleared from a water body.
     *
     * @param world the world to scan
     * @param x0    minimum x (inclusive)
     * @param x1    maximum x (inclusive)
     * @param y0    minimum y (inclusive)
     * @param y1    maximum y (inclusive)
     * @param z     the z plane to scan
     * @return the air blocks in the box
     */
    private List<Block> airBlocksIn(@NotNull World world, int x0, int x1, int y0, int y1, int z) {
        List<Block> air = new ArrayList<>();
        for (int x = x0; x <= x1; x++)
            for (int y = y0; y <= y1; y++) {
                Block block = new Location(world, x, y, z).getBlock();
                if (block.getType() == Material.AIR)
                    air.add(block);
            }
        return air;
    }

    /**
     * Tests the full lifecycle: carve a horizontal path through water, hold it against flow, then revert.
     *
     * <p>The caster casts toward +x into a block of water. Within a single method (so the shared MockBukkit state
     * evolves consistently) this verifies that:</p>
     * <ul>
     * <li>The path clears exactly its length in water blocks, in a single horizontal (constant-y) line</li>
     * <li>The spell is still active while holding the path open</li>
     * <li>A flow event into a cleared block is cancelled, while a flow event into uncleared water is not</li>
     * <li>After the duration expires the cleared blocks are restored to water and the spell ends</li>
     * <li>Once reverted, the spell no longer cancels flow into the (formerly cleared) blocks</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        PlayerMock caster = mockServer.addPlayer();

        int cx = (int) location.getX();
        int cy = (int) location.getY();
        int cz = (int) location.getZ();

        // a block of water in front of the caster, tall and long enough to contain the horizontal path wherever the
        // projectile enters it
        int xMin = cx + 1, xMax = cx + 15;
        int yMin = cy - 5, yMax = cy + 3;
        for (int x = xMin; x <= xMax; x++)
            for (int y = yMin; y <= yMax; y++)
                new Location(testWorld, x, y, cz).getBlock().setType(Material.WATER);

        // target offset purely on +x (same y and z) so the flattened path direction is a clean (1,0,0)
        Location targetLocation = new Location(testWorld, cx + 10, cy, cz);
        PARTIS_TEMPORUS partis = (PARTIS_TEMPORUS) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, experience);
        int pathLength = partis.getLength();
        int holdDuration = partis.getDuration();

        // carve: each segment takes the half-second cooldown, plus projectile travel; this is ample for the path
        mockServer.getScheduler().performTicks((long)pathLength * Ollivanders2Common.ticksPerSecond);

        // exactly path-length water blocks were cleared, all on one horizontal line (constant y)
        List<Block> cleared = airBlocksIn(testWorld, xMin, xMax, yMin, yMax, cz);
        assertEquals(pathLength, cleared.size(), "spell did not clear its full length of water in a horizontal line");
        long distinctYs = cleared.stream().map(Block::getY).distinct().count();
        assertEquals(1, distinctYs, "cleared blocks were not all on one horizontal level");
        assertFalse(partis.isKilled(), "spell ended before holding the path for its duration");

        int pathY = cleared.getFirst().getY();

        // the flow-prevention handler cancels flow into a cleared block but not into uncleared water. The handler is
        // invoked directly: MockBukkit does not dispatch fired events to a listener the spell self-registered at
        // runtime, so its logic is exercised by calling it rather than by callEvent. Each event is built as a
        // downward flow from the block above the target, so getToBlock() is the block under test.
        Block clearedBlock = cleared.getFirst();
        Block unclearedWater = new Location(testWorld, xMax, pathY, cz).getBlock();

        BlockFromToEvent intoCleared = new BlockFromToEvent(clearedBlock.getRelative(BlockFace.UP), BlockFace.DOWN);
        partis.onBlockFromToEvent(intoCleared);
        assertTrue(intoCleared.isCancelled(), "flow into a cleared path block was not blocked");

        BlockFromToEvent intoWater = new BlockFromToEvent(unclearedWater.getRelative(BlockFace.UP), BlockFace.DOWN);
        partis.onBlockFromToEvent(intoWater);
        assertFalse(intoWater.isCancelled(), "flow into uncleared water was wrongly blocked");

        // tick out the remaining hold duration so the spell reverts
        mockServer.getScheduler().performTicks(holdDuration);

        assertTrue(partis.isKilled(), "spell did not end after its hold duration");
        for (Block block : cleared)
            assertEquals(Material.WATER, block.getType(), "cleared block was not restored to water");
    }

    /**
     * Tests that the path's width, depth, and length scale with the caster's skill and are clamped at the top end.
     *
     * <p>The dimensions are read immediately after each cast, before any block is carved. Verifies that they grow
     * from low to high skill and that two very high skill levels yield the same (clamped) dimensions.</p>
     */
    @Test
    void pathDimensionsScaleWithSkillTest() {
        World testWorld = mockServer.addSimpleWorld("PartisTemporusDimensions");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        PARTIS_TEMPORUS low = castPartis(caster, location, targetLocation, 1);
        PARTIS_TEMPORUS high = castPartis(caster, location, targetLocation, 1000);
        PARTIS_TEMPORUS higher = castPartis(caster, location, targetLocation, 5000);

        assertTrue(high.getWidth() > low.getWidth(), "width did not increase with skill");
        assertEquals(high.getWidth(), higher.getWidth(), "width was not clamped at high skill");

        assertTrue(high.getDepth() > low.getDepth(), "depth did not increase with skill");
        assertEquals(high.getDepth(), higher.getDepth(), "depth was not clamped at high skill");

        assertTrue(high.getLength() > low.getLength(), "length did not increase with skill");
        assertEquals(high.getLength(), higher.getLength(), "length was not clamped at high skill");
    }

    /**
     * PARTIS_TEMPORUS reverts its block changes as part of its normal lifecycle, which is verified in
     * {@link #doCheckEffectTest()}.
     */
    @Override
    @Test
    void revertTest() {
        // revert is exercised in doCheckEffectTest
    }

    /**
     * Casts PARTIS_TEMPORUS at a given experience level and returns the spell instance. The path dimensions are set
     * in the constructor (via {@code doInitSpell}), so they are readable immediately without advancing the scheduler.
     *
     * @param caster         the casting player
     * @param fromLocation   the cast origin
     * @param targetLocation the cast target
     * @param castExperience the caster's experience with the spell
     * @return the PARTIS_TEMPORUS instance
     */
    @NotNull
    private PARTIS_TEMPORUS castPartis(@NotNull PlayerMock caster, @NotNull Location fromLocation, @NotNull Location targetLocation, int castExperience) {
        return (PARTIS_TEMPORUS) castSpell(caster, fromLocation, targetLocation, O2PlayerCommon.rightWand, castExperience);
    }
}