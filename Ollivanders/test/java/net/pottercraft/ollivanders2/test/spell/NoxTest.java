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
 * Unit tests for {@link NOX}. Extends {@link RemovePotionEffectTest} for the shared potion-removal tests.
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
     * Verify NOX's effect radius stays within LUMOS's bounds across low, mid, and mastery experience levels.
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
