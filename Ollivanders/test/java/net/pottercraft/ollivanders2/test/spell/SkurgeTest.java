package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.SKURGE;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the SKURGE spell, the Scouring Charm.
 *
 * <p>SKURGE is a projectile that, where it lands, turns every slime block within a radius to air. The radius
 * scales with the caster's experience and is limited between 1 and the spell's maximum; there is no random
 * element, so these tests are deterministic from the cast experience alone.</p>
 *
 * @author Azami7
 */
public class SkurgeTest extends O2SpellTestSuper {
    /**
     * Experience giving a usesModifier of 50 -> radius 5 (50/10), large enough to place slime both inside and
     * outside the cleared area.
     */
    private static final int experience = 50;

    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.SKURGE;
    }

    /**
     * Verify a cast clears slime blocks at and within the radius of the impact to air, leaves a slime block outside
     * the radius and a non-slime block in range untouched (only slime is scoured), and ends the spell.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        // a solid block at the target stops the projectile here, so the radius is centered on this point
        targetLocation.getBlock().setType(Material.DIRT);

        // slime at the impact point: should be cleared
        Location impactSlimeLocation = new Location(testWorld, targetLocation.getX(), targetLocation.getY() + 1, targetLocation.getZ());
        impactSlimeLocation.getBlock().setType(Material.SLIME_BLOCK);

        // slime one block away (distance 1 < radius 5): should be cleared
        Location inRangeSlimeLocation = new Location(testWorld, targetLocation.getX(), targetLocation.getY(), targetLocation.getZ() - 1);
        inRangeSlimeLocation.getBlock().setType(Material.SLIME_BLOCK);

        // non-slime block in range: should be left alone since only slime is scoured
        Location inRangeStoneLocation = new Location(testWorld, targetLocation.getX(), targetLocation.getY(), targetLocation.getZ() + 1);
        inRangeStoneLocation.getBlock().setType(Material.STONE);

        // slime well outside the radius: should be untouched
        int outOfRangeDistance = 10; // exceeds the radius 5 produced by the test experience
        Location farSlimeLocation = new Location(testWorld, targetLocation.getX(), targetLocation.getY(), targetLocation.getZ() - outOfRangeDistance);
        farSlimeLocation.getBlock().setType(Material.SLIME_BLOCK);

        SKURGE skurge = (SKURGE) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, experience);
        mockServer.getScheduler().performTicks(20);

        assertTrue(skurge.isKilled(), "spell was not killed after clearing slime");
        assertTrue(skurge.getRadius() < outOfRangeDistance, "test setup invalid: far slime is not outside the spell radius");

        assertEquals(Material.AIR, impactSlimeLocation.getBlock().getType(), "slime at the impact point was not cleared");
        assertEquals(Material.AIR, inRangeSlimeLocation.getBlock().getType(), "slime in range was not cleared");
        assertEquals(Material.STONE, inRangeStoneLocation.getBlock().getType(), "non-slime block in range was changed");
        assertEquals(Material.SLIME_BLOCK, farSlimeLocation.getBlock().getType(), "slime outside the radius was cleared");
    }

    /**
     * Verify the radius limits to 1 at zero experience, increases with experience, and limits to the spell's maximum
     * at very high experience (two different over-cap levels yield the same max radius).
     */
    @Test
    void radiusScalesWithSkillTest() {
        World testWorld = mockServer.addSimpleWorld("SkurgeRadiusScaling");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        int zeroSkillRadius = castSkurge(caster, location, targetLocation, 0).getRadius();
        int lowSkillRadius = castSkurge(caster, location, targetLocation, 50).getRadius();
        SKURGE highSkillSpell = castSkurge(caster, location, targetLocation, 1000);
        int highSkillRadius = highSkillSpell.getRadius();
        int higherSkillRadius = castSkurge(caster, location, targetLocation, 5000).getRadius();

        assertEquals(1, zeroSkillRadius, "radius was not limited to the minimum of 1 at zero skill");
        assertTrue(lowSkillRadius > zeroSkillRadius, "radius did not increase with skill");
        assertTrue(highSkillRadius > lowSkillRadius, "radius did not continue to increase with skill");
        assertEquals(highSkillSpell.getMaxRadius(), highSkillRadius, "radius was not limited to the spell's maximum at very high skill");
        assertEquals(highSkillRadius, higherSkillRadius, "radius was not limited to a fixed maximum at very high skill");
    }

    /**
     * SKURGE makes no temporary changes that require reversion.
     */
    @Override
    @Test
    void revertTest() {
        // nothing to revert
    }

    /**
     * Casts SKURGE at a given experience level and returns the spell instance. The radius is set in the
     * constructor (via {@code doInitSpell}), so it is readable immediately without advancing the scheduler.
     *
     * @param caster         the casting player
     * @param fromLocation   the cast origin
     * @param targetLocation the cast target
     * @param castExperience the caster's experience with the spell
     * @return the SKURGE instance
     */
    @NotNull
    private SKURGE castSkurge(@NotNull PlayerMock caster, @NotNull Location fromLocation, @NotNull Location targetLocation, int castExperience) {
        return (SKURGE) castSpell(caster, fromLocation, targetLocation, O2PlayerCommon.rightWand, castExperience);
    }
}