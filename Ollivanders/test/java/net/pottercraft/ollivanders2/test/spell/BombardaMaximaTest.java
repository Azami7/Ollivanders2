package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.BOMBARDA_MAXIMA}. Extends {@link BombardaBaseTest} for the
 * shared bombardment tests, with Bombarda Maxima-specific material thresholds.
 */
public class BombardaMaximaTest extends BombardaBaseTest {
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
