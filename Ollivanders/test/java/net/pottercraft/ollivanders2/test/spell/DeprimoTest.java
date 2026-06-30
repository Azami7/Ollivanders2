package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.DEPRIMO;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the DEPRIMO spell, the Blasting Charm.
 *
 * <p>Where its projectile lands, DEPRIMO turns every solid block within a skill-scaled radius into a falling
 * block and replaces it with air. Pass-through materials (air, water, fire) and protected materials (lava and
 * the unbreakable blocks) within the radius are left untouched. The radius scales deterministically with the
 * caster's skill, so these tests are deterministic from the cast experience alone.</p>
 *
 * @author Azami7
 */
public class DeprimoTest extends O2SpellTestSuper {
    /**
     * Experience giving a usesModifier of 30 -> radius 3 (30/10), large enough to place several blocks both inside
     * and outside the affected area.
     */
    private static final int experience = 30;

    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.DEPRIMO;
    }

    /**
     * Count the falling-block entities present in a world.
     *
     * @param world the world to scan
     * @return the number of falling-block entities in the world
     */
    private long fallingBlockCount(@NotNull World world) {
        return world.getEntities().stream().filter(entity -> entity instanceof FallingBlock).count();
    }

    /**
     * Tests turning solid blocks around the impact point into falling blocks.
     *
     * <p>The projectile is stopped by a solid block at the target. Solid blocks within the radius (the impact
     * block and its four orthogonal neighbours offset in y and z, so they do not block the projectile's path) plus
     * a protected bedrock block and a distant solid block are placed. With a single cast, verifies that:</p>
     * <ul>
     * <li>Each in-range solid block becomes air</li>
     * <li>A protected (bedrock) block in range is left intact</li>
     * <li>A solid block outside the radius is left intact</li>
     * <li>One falling block is spawned per converted block, and none for protected, distant, or air blocks</li>
     * <li>The spell is killed after the effect resolves</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        // solid block at the target stops the projectile here; it is also within the radius and will fall
        Location[] solidInRange = {
                targetLocation,
                new Location(testWorld, targetLocation.getX(), targetLocation.getY() + 1, targetLocation.getZ()),
                new Location(testWorld, targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()),
                new Location(testWorld, targetLocation.getX(), targetLocation.getY(), targetLocation.getZ() + 1),
                new Location(testWorld, targetLocation.getX(), targetLocation.getY(), targetLocation.getZ() - 1),
        };
        for (Location loc : solidInRange)
            loc.getBlock().setType(Material.STONE);

        // protected block in range: should survive (lava and unbreakables are blocked materials)
        Location bedrockLocation = new Location(testWorld, targetLocation.getX(), targetLocation.getY(), targetLocation.getZ() + 2);
        bedrockLocation.getBlock().setType(Material.BEDROCK);

        // solid block well outside the radius: should survive
        int outOfRangeDistance = 8; // exceeds the radius 3 produced by the test experience
        Location farLocation = new Location(testWorld, targetLocation.getX(), targetLocation.getY(), targetLocation.getZ() + outOfRangeDistance);
        farLocation.getBlock().setType(Material.STONE);

        DEPRIMO deprimo = (DEPRIMO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, experience);
        mockServer.getScheduler().performTicks(20);

        assertTrue(deprimo.isKilled(), "spell was not killed after blasting blocks");
        assertTrue(deprimo.getRadius() < outOfRangeDistance, "test setup invalid: far block is not outside the spell radius");

        for (Location loc : solidInRange)
            assertEquals(Material.AIR, loc.getBlock().getType(), "in-range solid block was not turned to air");

        assertEquals(Material.BEDROCK, bedrockLocation.getBlock().getType(), "protected bedrock block was destroyed");
        assertEquals(Material.STONE, farLocation.getBlock().getType(), "solid block outside the radius was destroyed");

        assertEquals(solidInRange.length, fallingBlockCount(testWorld), "unexpected number of falling blocks spawned");
    }

    /**
     * Tests that the affected radius scales with the caster's skill and is clamped at both ends.
     *
     * <p>Verifies that very low skill clamps the radius to the minimum, very high skill clamps it to the maximum,
     * and a mid-range skill falls strictly between the two bounds.</p>
     */
    @Test
    void radiusScalesWithSkillTest() {
        World testWorld = mockServer.addSimpleWorld("DeprimoRadiusScaling");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        DEPRIMO low = castDeprimo(caster, location, targetLocation, 1);
        DEPRIMO mid = castDeprimo(caster, location, targetLocation, experience);
        DEPRIMO high = castDeprimo(caster, location, targetLocation, 1000);

        assertEquals(low.getMinRadius(), low.getRadius(), "low-skill radius was not clamped to the minimum");
        assertEquals(high.getMaxRadius(), high.getRadius(), "high-skill radius was not clamped to the maximum");
        assertTrue(mid.getRadius() > low.getRadius() && mid.getRadius() < high.getRadius(), "mid-skill radius is not between the clamps");
    }

    /**
     * DEPRIMO has no revert action - blasted blocks are not restored.
     */
    @Override
    @Test
    void revertTest() {
        // nothing to revert
    }

    /**
     * Casts DEPRIMO at a given experience level and returns the spell instance. The radius is set in the
     * constructor (via {@code doInitSpell}), so it is readable immediately without advancing the scheduler.
     *
     * @param caster         the casting player
     * @param fromLocation   the cast origin
     * @param targetLocation the cast target
     * @param castExperience the caster's experience with the spell
     * @return the DEPRIMO instance
     */
    @NotNull
    private DEPRIMO castDeprimo(@NotNull PlayerMock caster, @NotNull Location fromLocation, @NotNull Location targetLocation, int castExperience) {
        return (DEPRIMO) castSpell(caster, fromLocation, targetLocation, O2PlayerCommon.rightWand, castExperience);
    }
}