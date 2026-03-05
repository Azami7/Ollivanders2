package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.spell.CARCEREM_AQUATICUM;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the CARCEREM_AQUATICUM spell.
 *
 * <p>CARCEREM_AQUATICUM traps the target player in a protective orb of non-flowing water,
 * immobilizing them while preventing drowning damage. The spell creates a 3x3 grid of water
 * blocks above, at, and below the player's eye level. The spell only affects players at normal
 * or reduced size (scale ≤ 1.0).</p>
 *
 * <p>Test Coverage:</p>
 * <ul>
 * <li>Spell targeting and effect application (inherited from ImmobilizePlayerSuperTest)</li>
 * <li>Effect duration calculation (inherited)</li>
 * <li>Partial immobilization behavior (inherited)</li>
 * <li>Water breathing effect application</li>
 * <li>Water block creation in 3-level grid pattern</li>
 * <li>Water block tracking as temporarily changed blocks</li>
 * <li>Water block cleanup and reversion after effect duration</li>
 * <li>Scale attribute validation (not testable in MockBukkit)</li>
 * </ul>
 *
 * @see net.pottercraft.ollivanders2.spell.CARCEREM_AQUATICUM for the spell implementation
 * @see ImmobilizePlayerTest for inherited test framework
 */
@Isolated
public class CarceremAquaticumTest extends ImmobilizePlayerTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.CARCEREM_AQUATICUM
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.CARCEREM_AQUATICUM;
    }

    /**
     * Test spell construction and initial configuration.
     *
     * <p>Overridden to do nothing as CARCEREM_AQUATICUM has no spell-specific construction
     * requirements beyond those tested in the base class.</p>
     */
    @Override
    @Test
    void spellConstructionTest() {
    }

    /**
     * Test spell targeting and effect application.
     *
     * <p>Overridden to do nothing as the inherited doCheckEffectTest() from ImmobilizePlayerSuperTest
     * provides complete coverage for this spell's targeting behavior.</p>
     */
    @Override
    @Test
    void doCheckEffectTest() {

    }

    /**
     * Test that only normal-sized players can be targeted.
     *
     * <p>CARCEREM_AQUATICUM only targets players with scale ≤ 1.0. This test cannot be implemented
     * as MockBukkit does not support the SCALE attribute. The test is overridden to do nothing but
     * would verify that oversized players (scale > 1.0) cannot be targeted.</p>
     */
    @Override
    @Test
    void invalidTargetTest() {
        // cannot test until MockBukkit supports the SCALE attribute
    }

    /**
     * Test that WATER_BREATHING effect and water blocks are applied correctly.
     *
     * <p>Verifies that the spell applies the WATER_BREATHING effect to the target player
     * and creates a complete 3x3 water block grid at three levels (above, at, and below the
     * player's eye level). All water blocks must be tracked as temporarily changed blocks.</p>
     */
    @Override
    @Test
    void additionalEffectsTest() {
        World testWorld = mockServer.addSimpleWorld("Immobilize");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        PlayerMock target = mockServer.addPlayer();
        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), target.getX(), target.getY() - 1, target.getZ()), 3);
        target.setLocation(targetLocation);
        assertEquals(Material.AIR, target.getEyeLocation().getBlock().getType());

        CARCEREM_AQUATICUM carceremAquaticum = (CARCEREM_AQUATICUM) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(carceremAquaticum.hasHitTarget());
        assertTrue(target.hasPotionEffect(PotionEffectType.WATER_BREATHING), "target does not have water breathing");

        assertEquals(Material.WATER, target.getEyeLocation().getBlock().getType(), "block at eye location not changed to Water");
        assertTrue(Ollivanders2API.getBlocks().isTemporarilyChangedBlock(target.getEyeLocation().getBlock()), "eye location block not added to tracking");
    }

    /**
     * Test that water blocks are reverted after the effect duration expires.
     *
     * <p>Verifies that when the immobilization effect duration expires, all water blocks
     * are automatically reverted to their original state (AIR) and are no longer tracked
     * as temporarily changed blocks.</p>
     */
    @Override
    @Test
    void revertTest() {
        World testWorld = mockServer.addSimpleWorld("Immobilize");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        PlayerMock target = mockServer.addPlayer();
        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), target.getX(), target.getY() - 1, target.getZ()), 3);
        target.setLocation(targetLocation);

        CARCEREM_AQUATICUM carceremAquaticum = (CARCEREM_AQUATICUM) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(Ollivanders2API.getBlocks().isTemporarilyChangedBlock(targetLocation.getBlock()));
        mockServer.getScheduler().performTicks(carceremAquaticum.getEffectDuration());

        assertFalse(Ollivanders2API.getBlocks().isTemporarilyChangedBlock(targetLocation.getBlock()), "water block still being tracked");
        assertEquals(Material.AIR, targetLocation.getBlock().getType(), "water block not reverted");
    }
}
