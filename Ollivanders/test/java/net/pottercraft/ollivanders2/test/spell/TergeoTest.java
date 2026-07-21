package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

/**
 * Test suite for the {@link net.pottercraft.ollivanders2.spell.TERGEO} spell (water siphoning spell), which
 * temporarily transfigures WATER blocks to AIR within a skill-scaled radius. Inherits the shared block
 * transfiguration tests from {@link BlockTransfigurationTest}, supplying WATER as the valid target and STONE as a
 * representative invalid target.
 *
 * @author Azami7
 * @see net.pottercraft.ollivanders2.spell.TERGEO
 */
public class TergeoTest extends BlockTransfigurationTest {
    /**
     * Returns the spell type being tested.
     *
     * @return TERGEO spell type
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.TERGEO;
    }

    /**
     * Returns the valid target material for TERGEO tests: WATER, the only block it transfigures.
     *
     * @return WATER material type
     */
    @Override
    @NotNull
    Material getValidTargetType() {
        return Material.WATER;
    }

    /**
     * Returns an invalid target material for TERGEO tests: STONE, a representative material outside TERGEO's
     * WATER-only allow list.
     *
     * @return STONE material type
     */
    @Override
    @NotNull
    Material getInvalidTargetType() {
        return Material.STONE;
    }

    /**
     * No-op override: TERGEO transfigures WATER to AIR, and AIR is not in its allow list, so it can never target a
     * block that is already its output type. The inherited same-material case therefore does not apply.
     */
    @Test
    void sameMaterialTest() {
        // this cannot happen, the types TERGEO changes blocks into cannot be targeted by the spell
    }
}
