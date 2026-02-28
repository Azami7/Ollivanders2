package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Test suite for the TERGEO spell (water siphoning spell).
 *
 * <p>Tests verify that TERGEO correctly targets and transfigures water blocks while rejecting
 * invalid target types. TERGEO transforms WATER blocks to AIR with a radius determined by
 * player skill level.</p>
 *
 * <p>Test coverage includes:</p>
 * <ul>
 * <li>Valid target material: WATER</li>
 * <li>Invalid target material: STONE (and other non-water materials)</li>
 * <li>Effect radius scaling based on player skill</li>
 * <li>Spell duration based on skill level</li>
 * <li>Success and failure messaging</li>
 * <li>Temporary transfiguration with automatic reversion</li>
 * </ul>
 *
 * @author Azami7
 * @see net.pottercraft.ollivanders2.spell.TERGEO
 */
@Isolated
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
     * Returns the valid target material for TERGEO tests.
     *
     * <p>TERGEO only transfigures water blocks, so WATER is the only valid target.</p>
     *
     * @return WATER material type
     */
    @Override
    @NotNull
    Material getValidTargetType() {
        return Material.WATER;
    }

    /**
     * Returns an invalid target material for TERGEO tests.
     *
     * <p>TERGEO's allow list contains only WATER, so all other materials are invalid targets.
     * STONE is used here as a representative non-water material.</p>
     *
     * @return STONE material type (or any non-water material)
     */
    @Override
    @NotNull
    Material getInvalidTargetType() {
        return Material.STONE;
    }

    /**
     * Overrides sameMaterialTest because it is not applicable to TERGEO.
     *
     * <p>TERGEO transfigures WATER to AIR. Since AIR is not in the allow list (only WATER is
     * allowed), this spell can never encounter a situation where it tries to transfigure a block
     * that is already the target material type. The target will always be WATER, which is never
     * equal to the transfigure type (AIR).</p>
     *
     * <p>Therefore, this test is not applicable and remains empty.</p>
     */
    @Test
    void sameMaterialTest() {
        // this cannot happen, the types TERGEO changes blocks into cannot be targeted by the spell
    }
}
