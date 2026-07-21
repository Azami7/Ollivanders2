package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.REPARO;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for {@link REPARO}. Extends {@link ReparoBaseTest} for the shared repair and skip-path tests, supplying
 * a diamond tool as the repair target.
 *
 * @author Azami7
 */
public class ReparoTest extends ReparoBaseTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.REPARO;
    }

    @Override
    @NotNull
    Material getRepairableMaterial() {
        return Material.DIAMOND_PICKAXE;
    }
}