package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for the MEGA_PYRO_PRASINA spell.
 *
 * <p>MEGA_PYRO_PRASINA launches large green and lime ball-burst fireworks with trailing effects.
 * Tests verify that the spell correctly spawns fireworks with the expected dual colors,
 * large ball effect type, and trailing effects.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.MEGA_PYRO_PRASINA for the spell implementation
 * @see PyrotechniaTest for inherited test framework
 */
@Isolated
public class MegaPyroPrasinaTest extends PyrotechniaTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.MEGA_PYRO_PRASINA
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.MEGA_PYRO_PRASINA;
    }
}
