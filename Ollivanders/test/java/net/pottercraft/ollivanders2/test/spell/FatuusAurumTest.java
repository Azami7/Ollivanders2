package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Test suite for the {@link net.pottercraft.ollivanders2.spell.FATUUS_AURUM} spell (Stone to Gold Charm), which
 * temporarily transfigures STONE blocks into GOLD_BLOCK within a fixed 1-block radius. Inherits the shared block
 * transfiguration tests from {@link BlockTransfigurationTest}, supplying STONE as the valid target and an
 * unbreakable material as the invalid target.
 *
 * @author Azami7
 * @see net.pottercraft.ollivanders2.spell.FATUUS_AURUM
 */
public class FatuusAurumTest extends BlockTransfigurationTest {
    /**
     * Returns the spell type being tested.
     *
     * @return FATUUS_AURUM spell type
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.FATUUS_AURUM;
    }

    /**
     * Returns the valid target material for FATUUS_AURUM tests: STONE, the only block type it transfigures.
     *
     * @return STONE material type
     */
    @Override
    @NotNull
    Material getValidTargetType() {
        return Material.STONE;
    }

    /**
     * Returns an invalid target material for FATUUS_AURUM tests: an unbreakable material, which is not in the
     * spell's STONE-only allow list.
     *
     * @return the first unbreakable material from the global list
     */
    @Override
    @NotNull
    Material getInvalidTargetType() {
        return Ollivanders2Common.getUnbreakableMaterials().getFirst();
    }
}
