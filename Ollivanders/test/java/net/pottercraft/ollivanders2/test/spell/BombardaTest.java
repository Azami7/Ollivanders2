package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Tests for the BOMBARDA spell.
 *
 * <p>Inherits all test scenarios from BombaraBaseTest with Bombarda-specific material thresholds.</p>
 */
public class BombardaTest extends BombaraBaseTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.BOMBARDA;
    }

    Material getValidMaterial() {
        return Material.SANDSTONE;
    }

    Material getInvalidHardnessMaterial() {
        return Material.BOOKSHELF;
    }

    Material getInvalidBlastResistanceMaterial() {
        return Material.BLUE_CONCRETE;
    }
}
