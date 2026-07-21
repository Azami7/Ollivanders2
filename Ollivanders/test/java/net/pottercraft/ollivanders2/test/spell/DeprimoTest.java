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
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.DEPRIMO}. The affected radius scales deterministically
 * with the caster's skill, so these tests are deterministic from the cast experience alone.
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
     * Verify each in-range solid block becomes air and spawns one falling block, while a protected (bedrock) block, a
     * block outside the radius, and air are left untouched, then the spell ends. In-range neighbours are offset in y
     * and z so they do not block the projectile's path to the impact point.
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
     * Verify the radius limits to the minimum at very low skill and the maximum at very high skill, with a mid-range
     * skill falling strictly between.
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

        assertEquals(low.getMinRadius(), low.getRadius(), "low-skill radius was not limited to the minimum");
        assertEquals(high.getMaxRadius(), high.getRadius(), "high-skill radius was not limited to the maximum");
        assertTrue(mid.getRadius() > low.getRadius() && mid.getRadius() < high.getRadius(), "mid-skill radius is not between the limits");
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