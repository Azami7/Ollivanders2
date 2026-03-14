package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Tests for the BOMBARDA_MAXIMA spell.
 *
 * <p>Inherits all test scenarios from BombaraBaseTest with Bombarda Maxima-specific material thresholds.</p>
 */
public class BombardaMaximaTest extends BombaraBaseTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.BOMBARDA_MAXIMA;
    }

    Material getValidMaterial() {
        return Material.OAK_PLANKS;
    }

    Material getInvalidHardnessMaterial() {
        return Material.BARREL;
    }

    Material getInvalidBlastResistanceMaterial() {
        return Material.BRICKS;
    }
}
