package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.LUMOS;
import net.pottercraft.ollivanders2.spell.NOX;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@link NOX} spell (Wand-Extinguishing Charm).
 *
 * <p>Verifies NOX-specific behavior including Night Vision removal and effect radius
 * clamping to {@link LUMOS} bounds.</p>
 */
public class NoxTest extends RemovePotionEffectTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.NOX;
    }

    @Override
    PotionEffectType getValidEffectType() {
        return PotionEffectType.NIGHT_VISION;
    }

    /**
     * Test that NOX effect radius is clamped to LUMOS radius bounds.
     *
     * <p>Verifies the radius stays within [{@link LUMOS#minEffectRadiusConfig},
     * {@link LUMOS#maxEffectRadiusConfig}] at low, mid, and mastery experience levels.</p>
     */
    @Test
    void effectRadiusTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        NOX nox = (NOX) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 0);
        assertTrue(nox.getEffectRadius() >= LUMOS.minEffectRadiusConfig, "nox radius < min radius");

        nox = (NOX) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 50);
        assertEquals(5, nox.getEffectRadius(), "unexpected radius");

        nox = (NOX) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 4);
        assertTrue(nox.getEffectRadius() <= LUMOS.maxEffectRadiusConfig, "nox radius > max radius");
    }
}
