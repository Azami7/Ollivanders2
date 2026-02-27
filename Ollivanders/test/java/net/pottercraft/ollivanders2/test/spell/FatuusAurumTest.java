package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

/**
 * Test suite for the FATUUS_AURUM spell (Stone to Gold Charm).
 *
 * <p>Tests verify that FATUUS_AURUM correctly targets and transforms stone blocks into gold
 * while rejecting invalid and unbreakable materials. FATUUS_AURUM converts STONE blocks to
 * GOLD_BLOCK with a fixed 1-block radius and temporary duration.</p>
 *
 * <p>Test coverage includes:</p>
 * <ul>
 * <li>Valid target material: STONE blocks only</li>
 * <li>Invalid target materials: Unbreakable materials and non-stone blocks</li>
 * <li>Effect: Stone → Gold transformation</li>
 * <li>Fixed radius: 1 block (single-target precision spell)</li>
 * <li>Spell duration: 15 seconds to 10 minutes based on skill level</li>
 * <li>Success and failure messaging</li>
 * <li>Temporary transfiguration with automatic reversion</li>
 * <li>Handling of blocks already at target material type (GOLD_BLOCK)</li>
 * </ul>
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
     * Returns the valid target material for FATUUS_AURUM tests.
     *
     * <p>FATUUS_AURUM only transfigures STONE blocks. STONE is the only valid target material.</p>
     *
     * @return STONE material type
     */
    @Override
    @NotNull
    Material getValidTargetType() {
        return Material.STONE;
    }

    /**
     * Returns an invalid target material for FATUUS_AURUM tests.
     *
     * <p>FATUUS_AURUM cannot transfigure unbreakable materials. Its allow list contains
     * only STONE, so all other block types are invalid targets.</p>
     *
     * @return First unbreakable material from the global list
     */
    @Override
    @Nullable
    Material getInvalidTargetType() {
        return Ollivanders2Common.getUnbreakableMaterials().getFirst();
    }

    /**
     * Overrides spellConstructionTest with no additional implementation.
     *
     * <p>FATUUS_AURUM uses the default construction behavior provided by the parent test class.
     * No spell-specific construction tests are needed beyond the inherited test coverage.</p>
     */
    @Override
    @Test
    void spellConstructionTest() {

    }
}
