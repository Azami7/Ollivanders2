package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.DURO}. Extends {@link BlockTransfigurationTest} for the
 * shared transfiguration tests.
 *
 * @author Azami7
 */
public class DuroTest extends BlockTransfigurationTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.DURO;
    }

    /**
     * @return DIRT, a representative block DURO can harden
     */
    @Override
    @NotNull
    Material getValidTargetType() {
        return Material.DIRT;
    }

    @Override
    @NotNull
    Material getInvalidTargetType() {
        return Ollivanders2Common.getUnbreakableMaterials().getFirst();
    }
}
