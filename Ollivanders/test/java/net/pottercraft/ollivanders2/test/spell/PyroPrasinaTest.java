package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for the PYRO_PRASINA spell.
 *
 * <p>PYRO_PRASINA launches green ball-burst fireworks. Tests verify that the spell correctly
 * spawns fireworks with the expected green color and large ball effect type.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.PYRO_PRASINA for the spell implementation
 * @see PyrotechniaTest for inherited test framework
 */
@Isolated
public class PyroPrasinaTest extends PyrotechniaTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.PYRO_PRASINA
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.PYRO_PRASINA;
    }
}
