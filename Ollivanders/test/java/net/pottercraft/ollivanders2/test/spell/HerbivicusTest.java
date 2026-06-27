package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.HERBIVICUS;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the HERBIVICUS spell, the Plant-Growing Charm.
 *
 * <p>HERBIVICUS is a projectile that, where it lands, advances the age of every {@link Ageable} crop within a
 * radius. Both the radius and the per-crop growth scale with the caster's skill; there is no random element, so
 * these tests are deterministic from the cast experience alone.</p>
 *
 * @author Azami7
 */
public class HerbivicusTest extends O2SpellTestSuper {
    /**
     * Experience giving a usesModifier of 50 -> radius 12 (50/4) and growth 2 (50/25), so the growth clamp at a
     * crop's maximum age can be exercised.
     */
    private static final int experience = 50;

    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.HERBIVICUS;
    }

    /**
     * Tests growing crops around the spell's landing point.
     *
     * <p>A stone block is placed at the target so the projectile stops there, and several blocks are placed around
     * it within and outside the radius. With a single cast, verifies that:</p>
     * <ul>
     * <li>A young crop in range is advanced by exactly the spell's growth amount</li>
     * <li>A near-mature crop is clamped to its maximum age rather than overshooting</li>
     * <li>A fully grown crop is left unchanged</li>
     * <li>A crop outside the radius is left unchanged</li>
     * <li>The spell is killed after the effect resolves</li>
     * </ul>
     *
     * <p>The fire-skip branch is not covered here: MockBukkit does not model fire block data as {@link Ageable}
     * (real Bukkit does), so under test fire never reaches the {@code instanceof Ageable} branch the skip lives
     * in.</p>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(testWorld, targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);
        // a solid block at the target stops the projectile here, so the radius is centered on this point
        targetLocation.getBlock().setType(Material.DIRT);

        // young crop in range: should advance by exactly the growth amount
        Location youngCropLocation = new Location(testWorld, targetLocation.getX(), targetLocation.getY(), targetLocation.getZ() - 1);
        setWheatAge(youngCropLocation, 0);

        // near-mature crop in range: growth would overshoot, so it should clamp to the maximum age
        Location nearMatureLocation = new Location(testWorld, targetLocation.getX(), targetLocation.getY(), targetLocation.getZ() + 1);
        int wheatMaxAge = wheatMaxAge(nearMatureLocation);
        setWheatAge(nearMatureLocation, wheatMaxAge - 1);

        // already fully grown crop in range: should be untouched
        Location matureLocation = new Location(testWorld, targetLocation.getX(), targetLocation.getY() + 1, targetLocation.getZ());
        setWheatAge(matureLocation, wheatMaxAge);

        // crop well outside any possible radius: should be untouched
        int outOfRangeDistance = 25; // exceeds HERBIVICUS's maximum radius
        Location farCropLocation = new Location(testWorld, targetLocation.getX(), targetLocation.getY(), targetLocation.getZ() - outOfRangeDistance);
        setWheatAge(farCropLocation, 0);

        HERBIVICUS herbivicus = (HERBIVICUS) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, experience);
        mockServer.getScheduler().performTicks(20);

        assertTrue(herbivicus.isKilled(), "spell was not killed after growing crops");
        assertTrue(herbivicus.getRadius() < outOfRangeDistance, "test setup invalid: far crop is not outside the spell radius");

        assertEquals(herbivicus.getGrowth(), cropAge(youngCropLocation), "young crop did not advance by the growth amount");
        assertEquals(wheatMaxAge, cropAge(nearMatureLocation), "near-mature crop was not clamped to its maximum age");
        assertEquals(wheatMaxAge, cropAge(matureLocation), "already mature crop was changed");
        assertEquals(0, cropAge(farCropLocation), "crop outside the radius was grown");
    }

    /**
     * Tests that the affected radius scales with the caster's skill and is clamped at both ends.
     *
     * <p>Verifies that:</p>
     * <ul>
     * <li>Zero skill clamps the radius to the minimum of 1</li>
     * <li>The radius increases as skill increases</li>
     * <li>Very high skill is clamped to a fixed maximum (two different over-cap skill levels yield the same
     *     radius)</li>
     * </ul>
     */
    @Test
    void radiusScalesWithSkillTest() {
        World testWorld = mockServer.addSimpleWorld("HerbivicusRadiusScaling");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        int zeroSkillRadius = castHerbivicus(caster, location, targetLocation, 0).getRadius();
        int lowSkillRadius = castHerbivicus(caster, location, targetLocation, 40).getRadius();
        int highSkillRadius = castHerbivicus(caster, location, targetLocation, 1000).getRadius();
        int higherSkillRadius = castHerbivicus(caster, location, targetLocation, 5000).getRadius();

        assertEquals(1, zeroSkillRadius, "radius was not clamped to the minimum of 1 at zero skill");
        assertTrue(lowSkillRadius > zeroSkillRadius, "radius did not increase with skill");
        assertTrue(highSkillRadius > lowSkillRadius, "radius did not continue to increase with skill");
        assertEquals(highSkillRadius, higherSkillRadius, "radius was not clamped to a fixed maximum at very high skill");
    }

    /**
     * Tests that the per-crop growth amount scales with the caster's skill and is clamped at both ends.
     *
     * <p>Verifies that:</p>
     * <ul>
     * <li>Zero skill clamps the growth to the minimum of 1</li>
     * <li>The growth increases as skill increases</li>
     * <li>Very high skill is clamped to a fixed maximum (two different over-cap skill levels yield the same
     *     growth)</li>
     * </ul>
     */
    @Test
    void growthScalesWithSkillTest() {
        World testWorld = mockServer.addSimpleWorld("HerbivicusGrowthScaling");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        int zeroSkillGrowth = castHerbivicus(caster, location, targetLocation, 0).getGrowth();
        int lowSkillGrowth = castHerbivicus(caster, location, targetLocation, 75).getGrowth();
        int highSkillGrowth = castHerbivicus(caster, location, targetLocation, 1000).getGrowth();
        int higherSkillGrowth = castHerbivicus(caster, location, targetLocation, 5000).getGrowth();

        assertEquals(1, zeroSkillGrowth, "growth was not clamped to the minimum of 1 at zero skill");
        assertTrue(lowSkillGrowth > zeroSkillGrowth, "growth did not increase with skill");
        assertTrue(highSkillGrowth > lowSkillGrowth, "growth did not continue to increase with skill");
        assertEquals(highSkillGrowth, higherSkillGrowth, "growth was not clamped to a fixed maximum at very high skill");
    }

    /**
     * HERBIVICUS makes no temporary changes that require reversion.
     */
    @Override
    @Test
    void revertTest() {
        // nothing to revert
    }

    /**
     * Casts HERBIVICUS at a given experience level and returns the spell instance. The radius and growth are set in
     * the constructor (via {@code doInitSpell}), so they are readable immediately without advancing the scheduler.
     *
     * @param caster         the casting player
     * @param fromLocation   the cast origin
     * @param targetLocation the cast target
     * @param castExperience the caster's experience with the spell
     * @return the HERBIVICUS instance
     */
    @NotNull
    private HERBIVICUS castHerbivicus(@NotNull PlayerMock caster, @NotNull Location fromLocation, @NotNull Location targetLocation, int castExperience) {
        return (HERBIVICUS) castSpell(caster, fromLocation, targetLocation, O2PlayerCommon.rightWand, castExperience);
    }

    /**
     * Places a wheat crop at the location and sets its age.
     *
     * @param loc the location to place the wheat
     * @param age the age to set on the crop's {@link Ageable} data
     */
    private void setWheatAge(@NotNull Location loc, int age) {
        Block block = loc.getBlock();
        block.setType(Material.WHEAT);
        Ageable data = (Ageable) block.getBlockData();
        data.setAge(age);
        block.setBlockData(data);
    }

    /**
     * Reads the current age of the {@link Ageable} block at the given location.
     *
     * @param loc the location of the block
     * @return the block's current age
     */
    private int cropAge(@NotNull Location loc) {
        return ((Ageable) loc.getBlock().getBlockData()).getAge();
    }

    /**
     * Returns the maximum age of a wheat crop by briefly placing it and reading its block data.
     *
     * @param loc a scratch location to place the wheat
     * @return the maximum age supported by wheat's {@link Ageable} data
     */
    private int wheatMaxAge(@NotNull Location loc) {
        loc.getBlock().setType(Material.WHEAT);
        return ((Ageable) loc.getBlock().getBlockData()).getMaximumAge();
    }
}