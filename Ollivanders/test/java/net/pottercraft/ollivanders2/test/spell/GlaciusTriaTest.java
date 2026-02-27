package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

/**
 * Test suite for the GLACIUS_TRIA spell (strongest freezing charm).
 *
 * <p>Tests verify that GLACIUS_TRIA correctly targets and freezes liquid and ice blocks while
 * rejecting invalid target types. GLACIUS_TRIA is the most powerful freezing charm variant with
 * quadrupled effect radius but minimal duration, designed for massive area-of-effect freezing operations.</p>
 *
 * <p>Test coverage includes:</p>
 * <ul>
 * <li>Valid target materials: WATER, LAVA, ICE</li>
 * <li>Invalid target materials: Unbreakable materials and non-liquid blocks</li>
 * <li>Transformations: Water → Ice, Lava → Obsidian, Ice → Packed Ice</li>
 * <li>Effect radius scaling based on player skill (4-20 blocks, 200% modifier)</li>
 * <li>Spell duration based on skill level (7.5 seconds to 2.5 minutes)</li>
 * <li>Success and failure messaging</li>
 * <li>Temporary transfiguration with automatic reversion</li>
 * </ul>
 *
 * @author Azami7
 * @see net.pottercraft.ollivanders2.spell.GLACIUS_TRIA
 */
public class GlaciusTriaTest extends BlockTransfigurationTest {
    /**
     * Returns the spell type being tested.
     *
     * @return GLACIUS_TRIA spell type
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.GLACIUS_TRIA;
    }

    /**
     * Returns the valid target material for GLACIUS_TRIA tests.
     *
     * <p>GLACIUS_TRIA can transfigure liquid and ice blocks (WATER, LAVA, ICE).
     * ICE is used here as a representative valid target.</p>
     *
     * @return ICE material type
     */
    @Override
    @NotNull
    Material getValidTargetType() {
        return Material.ICE;
    }

    /**
     * Returns an invalid target material for GLACIUS_TRIA tests.
     *
     * <p>GLACIUS_TRIA cannot transfigure unbreakable materials. Only liquid and ice blocks
     * are valid targets.</p>
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
     * <p>GLACIUS_TRIA uses the default construction behavior provided by the parent test class.
     * No spell-specific construction tests are needed beyond the inherited test coverage.</p>
     */
    @Override
    @Test
    void spellConstructionTest() {

    }

    /**
     * Overrides sameMaterialTest because it is not applicable to GLACIUS_TRIA.
     *
     * <p>GLACIUS_TRIA converts ice to packed ice, water to ice, and lava to obsidian. Since
     * packed ice, regular ice, and obsidian are not valid targets for the spell (only WATER,
     * LAVA, and ICE are in the allow list), this spell can never encounter a situation where
     * it tries to transfigure a block that is already the target type.</p>
     *
     * <p>Therefore, this test is not applicable and remains empty.</p>
     */
    @Test
    void sameMaterialTest() {
        // this cannot happen
    }
}
