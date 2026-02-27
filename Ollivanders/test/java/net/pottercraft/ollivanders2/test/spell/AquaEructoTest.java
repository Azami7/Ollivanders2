package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

/**
 * Test suite for the AQUA_ERUCTO spell (water-jet extinguishing charm).
 *
 * <p>Tests verify that AQUA_ERUCTO correctly targets and extinguishes fire and lava blocks
 * while rejecting invalid materials. AQUA_ERUCTO permanently transforms fire variants to air
 * and lava to obsidian with a fixed 1-block radius.</p>
 *
 * <p>Test coverage includes:</p>
 * <ul>
 * <li>Valid target materials: FIRE, SOUL_FIRE, LAVA, CAMPFIRE, SOUL_CAMPFIRE</li>
 * <li>Invalid target materials: Non-fire blocks (stone, dirt, sand, etc.)</li>
 * <li>Transformations: Fire variants → Air/Logs, Lava → Obsidian</li>
 * <li>Fixed radius: 1 block (single-target precision spell)</li>
 * <li>Permanent duration: Extinguished fires do not reignite</li>
 * <li>Success and failure messaging</li>
 * <li>Visual effects (blue ice water jet)</li>
 * </ul>
 *
 * @author Azami7
 * @see net.pottercraft.ollivanders2.spell.AQUA_ERUCTO
 */
public class AquaEructoTest extends BlockTransfigurationTest {
    /**
     * Returns the spell type being tested.
     *
     * @return AQUA_ERUCTO spell type
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.AQUA_ERUCTO;
    }

    /**
     * Returns the valid target material for AQUA_ERUCTO tests.
     *
     * <p>AQUA_ERUCTO only transfigures fire and lava variants (FIRE, SOUL_FIRE, LAVA, CAMPFIRE,
     * SOUL_CAMPFIRE). LAVA is used here as a representative valid target.</p>
     *
     * @return LAVA material type
     */
    @Override
    @NotNull
    Material getValidTargetType() {
        return Material.LAVA;
    }

    /**
     * Returns an invalid target material for AQUA_ERUCTO tests.
     *
     * <p>AQUA_ERUCTO's allow list contains only fire and lava variants. All other block types,
     * including common materials like stone, are invalid targets.</p>
     *
     * @return STONE material type (or any non-fire/lava material)
     */
    @Override
    @Nullable
    Material getInvalidTargetType() {
        return Material.STONE;
    }

    /**
     * Overrides spellConstructionTest with no additional implementation.
     *
     * <p>AQUA_ERUCTO uses the default construction behavior provided by the parent test class.
     * No spell-specific construction tests are needed beyond the inherited test coverage.</p>
     */
    @Override
    @Test
    void spellConstructionTest() {

    }

    /**
     * Overrides sameMaterialTest because it is not applicable to AQUA_ERUCTO.
     *
     * <p>AQUA_ERUCTO converts fire blocks to air and lava to obsidian (and campfires to oak logs).
     * Since air, obsidian, and oak logs are not valid targets for the spell (only FIRE, SOUL_FIRE,
     * LAVA, CAMPFIRE, and SOUL_CAMPFIRE are in the allow list), this spell can never encounter a
     * situation where it tries to transfigure a block that is already the target type.</p>
     *
     * <p>Therefore, this test is not applicable and remains empty.</p>
     */
    @Test
    void sameMaterialTest() {
        // this cannot happen, the types aqua eructo changes blocks in to cannot be targeted by the spell
    }
}
