package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.GLACIUS_DUO}. Extends {@link BlockTransfigurationTest} for
 * the shared transfiguration tests.
 *
 * @author Azami7
 */
public class GlaciusDuoTest extends BlockTransfigurationTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.GLACIUS_DUO;
    }

    @Override
    @NotNull
    Material getValidTargetType() {
        return Material.ICE;
    }

    @Override
    @NotNull
    Material getInvalidTargetType() {
        return Ollivanders2Common.getUnbreakableMaterials().getFirst();
    }

    /**
     * Not applicable to GLACIUS_DUO: its outputs (packed ice, ice, obsidian) are never valid targets, so it is never
     * asked to transfigure a block that is already the target type.
     */
    @Test
    void sameMaterialTest() {
        // this cannot happen
    }
}
