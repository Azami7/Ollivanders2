package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Test suite for the DURO spell (hardening charm).
 *
 * <p>Tests verify that DURO correctly targets and hardens eligible blocks while rejecting
 * invalid and unbreakable materials. DURO transforms most block types into STONE with a radius
 * determined by player skill level.</p>
 *
 * <p>Test coverage includes:</p>
 * <ul>
 * <li>Valid target materials: Most block types except hot blocks (dirt, stone, sand, etc.)</li>
 * <li>Invalid target materials: Unbreakable materials (bedrock, end portal blocks, etc.)</li>
 * <li>Blocked materials: WATER and hot blocks (lava, fire, etc.)</li>
 * <li>Effect radius scaling based on player skill (1-15 blocks, 25% modifier)</li>
 * <li>Spell duration based on skill level (15 seconds to 10 minutes)</li>
 * <li>Success and failure messaging</li>
 * <li>Temporary transfiguration with automatic reversion</li>
 * <li>Handling of blocks already at target material type (STONE)</li>
 * </ul>
 *
 * @author Azami7
 * @see net.pottercraft.ollivanders2.spell.DURO
 */
@Isolated
public class DuroTest extends BlockTransfigurationTest {
    /**
     * Returns the spell type being tested.
     *
     * @return DURO spell type
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.DURO;
    }

    /**
     * Returns the valid target material for DURO tests.
     *
     * <p>DURO can transfigure most block types except hot/flammable materials (lava, fire, etc.).
     * DIRT is used here as a representative valid target material.</p>
     *
     * @return DIRT material type
     */
    @Override
    @NotNull
    Material getValidTargetType() {
        return Material.DIRT;
    }

    /**
     * Returns an invalid target material for DURO tests.
     *
     * <p>DURO cannot transfigure unbreakable materials. These materials are protected from
     * transfiguration spells.</p>
     *
     * @return First unbreakable material from the global list
     */
    @Override
    @NotNull
    Material getInvalidTargetType() {
        return Ollivanders2Common.getUnbreakableMaterials().getFirst();
    }

    /**
     * Overrides spellConstructionTest with no additional implementation.
     *
     * <p>DURO uses the default construction behavior provided by the parent test class.
     * No spell-specific construction tests are needed beyond the inherited test coverage.</p>
     */
    @Override
    @Test
    void spellConstructionTest() {

    }
}
