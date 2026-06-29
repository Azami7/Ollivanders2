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
 * Tests for the {@link DIAMAS_REPARO} spell, the stronger Mending Charm for diamond and netherite items.
 *
 * <p>Inherits the shared repair and skip-path coverage from {@link ReparoBaseTest}, supplying the
 * DIAMAS_REPARO spell type and a netherite tool as the repair target, and adds a check that the spell's
 * overridden repair bounds are stronger than the base {@link net.pottercraft.ollivanders2.spell.REPARO}.</p>
 *
 * @author Azami7
 * @see DIAMAS_REPARO
 */
public class DiamasReparoTest extends ReparoBaseTest {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.DIAMAS_REPARO;
    }

    /** {@inheritDoc} */
    @Override
    @NotNull
    Material getRepairableMaterial() {
        return Material.NETHERITE_PICKAXE;
    }

    /**
     * Verifies that DIAMAS_REPARO repairs more than the base Mending Charm.
     *
     * <p>Confirms the field overrides in the casting constructor actually take effect by comparing this
     * spell's repair bounds against a base REPARO cast under identical conditions - both the minimum and
     * maximum repair amounts must be larger for the stronger variant. Comparing live instances avoids
     * hard-coding the production bound values.</p>
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