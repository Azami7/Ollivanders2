package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for the PERICULUM_DUO spell.
 *
 * <p>PERICULUM_DUO launches red and orange star-burst fireworks with trailing effects.
 * Tests verify that the spell correctly spawns fireworks with the expected dual colors,
 * star effect type, and trailing effects.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.PERICULUM_DUO for the spell implementation
 * @see PyrotechniaTest for inherited test framework
 */
@Isolated
public class PericulumDuoTest extends PyrotechniaTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.PERICULUM_DUO
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.PERICULUM_DUO;
    }
}
