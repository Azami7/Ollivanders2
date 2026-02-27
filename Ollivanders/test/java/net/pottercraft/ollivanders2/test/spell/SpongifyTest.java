package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Test suite for the SPONGIFY spell (softening charm).
 *
 * <p>Tests verify that SPONGIFY correctly targets and transfigures eligible blocks while rejecting
 * unbreakable and protected materials. SPONGIFY transforms most block types into SPONGE with
 * a radius determined by player skill level.</p>
 *
 * <p>Test coverage includes:</p>
 * <ul>
 * <li>Valid target material: STONE (and other transfigurable block types)</li>
 * <li>Invalid target material: Unbreakable materials (bedrock, end portal blocks, etc.)</li>
 * <li>Blocked materials: WATER and hot blocks (lava, fire, etc.)</li>
 * <li>Effect radius scaling based on player skill</li>
 * <li>Spell duration based on skill level</li>
 * <li>Success and failure messaging</li>
 * <li>Temporary transfiguration with automatic reversion</li>
 * </ul>
 *
 * @author Azami7
 * @see net.pottercraft.ollivanders2.spell.SPONGIFY
 */
@Isolated
public class SpongifyTest extends BlockTransfigurationTest {
    /**
     * Returns the spell type being tested.
     *
     * @return SPONGIFY spell type
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.SPONGIFY;
    }

    /**
     * Returns the valid target material for SPONGIFY tests.
     *
     * <p>SPONGIFY can transfigure most block types except water and hot blocks. STONE is used
     * as a representative valid target material for testing.</p>
     *
     * @return STONE material type
     */
    @Override
    @NotNull
    Material getValidTargetType() {
        return Material.STONE;
    }

    /**
     * Returns an invalid target material for SPONGIFY tests.
     *
     * <p>SPONGIFY cannot transfigure unbreakable materials such as bedrock and protected blocks.
     * Unbreakable materials are on SPONGIFY's material blocked list.</p>
     *
     * @return First unbreakable material from the global list
     */
    @Override
    @Nullable
    Material getInvalidTargetType() {
        return Ollivanders2Common.getUnbreakableMaterials().getFirst();
    }
}
