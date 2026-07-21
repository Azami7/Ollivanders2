package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.DIAMAS_REPARO;
import net.pottercraft.ollivanders2.spell.ReparoBase;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link DIAMAS_REPARO}. Extends {@link ReparoBaseTest} for the shared repair and skip-path tests,
 * supplying a netherite tool as the repair target, plus a check that its overridden repair bounds are stronger than
 * base {@link net.pottercraft.ollivanders2.spell.REPARO}.
 *
 * @author Azami7
 */
public class DiamasReparoTest extends ReparoBaseTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.DIAMAS_REPARO;
    }

    @Override
    @NotNull
    Material getRepairableMaterial() {
        return Material.NETHERITE_PICKAXE;
    }

    /**
     * Verify DIAMAS_REPARO's min and max repair bounds are both larger than a base REPARO cast under identical
     * conditions, confirming the constructor's field overrides take effect. Compares live instances rather than
     * hard-coding the production bound values.
     */
    @Test
    void strongerThanReparoTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName() + "Stronger");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        ReparoBase diamas = (ReparoBase) castSpell(caster, location, targetLocation);
        ReparoBase reparo = (ReparoBase) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, defaultExperience, O2SpellType.REPARO);

        assertTrue(diamas.getMinRepair() > reparo.getMinRepair(), "DIAMAS_REPARO minRepair is not stronger than REPARO");
        assertTrue(diamas.getMaxRepair() > reparo.getMaxRepair(), "DIAMAS_REPARO maxRepair is not stronger than REPARO");
    }
}