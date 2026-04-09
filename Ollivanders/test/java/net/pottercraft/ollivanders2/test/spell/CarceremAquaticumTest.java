package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.CARCEREM_AQUATICUM;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
 * <li>Spell targeting and effect application (inherited from ImmobilizePlayerTest)</li>
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
     * Test that the WATER_BREATHING effect is applied to the target.
     *
     * <p>Verifies that the spell applies the WATER_BREATHING effect to the target player to prevent
     * drowning inside the water orb. Water block creation is tested via the inherited
     * {@link ImmobilizePlayerTest#imprisonEffectTest()}.</p>
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

        assertTrue(carceremAquaticum.hasHitBlock());
        assertTrue(target.hasPotionEffect(PotionEffectType.WATER_BREATHING), "target does not have water breathing");
    }
}
