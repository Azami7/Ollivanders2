package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.BOMBARDA}. Extends {@link BombardaBaseTest} for the shared
 * bombardment tests, with Bombarda-specific material thresholds.
 */
public class BombardaTest extends BombardaBaseTest {
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
