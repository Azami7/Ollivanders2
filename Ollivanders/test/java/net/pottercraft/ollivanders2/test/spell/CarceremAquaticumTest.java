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
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.CARCEREM_AQUATICUM}. Extends {@link ImmobilizePlayerTest}
 * for the shared immobilization tests; adds coverage for the WATER_BREATHING effect that keeps the trapped player
 * from drowning.
 */
public class CarceremAquaticumTest extends ImmobilizePlayerTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.CARCEREM_AQUATICUM;
    }

    /**
     * Overridden to a no-op: the scale ≤ 1.0 targeting restriction cannot be tested because MockBukkit does not
     * support the SCALE attribute.
     */
    @Override
    @Test
    void invalidTargetTest() {
        // cannot test until MockBukkit supports the SCALE attribute
    }

    /**
     * Verify the spell applies WATER_BREATHING to the target so it does not drown in the orb. Water-block creation is
     * covered by the inherited {@link ImmobilizePlayerTest#imprisonEffectTest()}.
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
