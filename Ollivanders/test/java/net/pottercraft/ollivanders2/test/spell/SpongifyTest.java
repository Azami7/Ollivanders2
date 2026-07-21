package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Test suite for the {@link net.pottercraft.ollivanders2.spell.SPONGIFY} spell (softening charm), which temporarily
 * transfigures most breakable, non-water, non-hot blocks into SPONGE within a skill-scaled radius. Inherits the
 * shared block transfiguration tests from {@link BlockTransfigurationTest}, supplying STONE as a representative
 * valid target and an unbreakable material as the invalid target.
 *
 * @author Azami7
 * @see net.pottercraft.ollivanders2.spell.SPONGIFY
 */
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
     * Returns the valid target material for SPONGIFY tests: STONE, a representative block SPONGIFY can transfigure.
     *
     * @return STONE material type
     */
    @Override
    @NotNull
    Material getValidTargetType() {
        return Material.STONE;
    }

    /**
     * Returns an invalid target material for SPONGIFY tests: an unbreakable material, which is on SPONGIFY's
     * blocked list.
     *
     * @return the first unbreakable material from the global list
     */
    @Override
    @NotNull
    Material getInvalidTargetType() {
        return Ollivanders2Common.getUnbreakableMaterials().getFirst();
    }
}
