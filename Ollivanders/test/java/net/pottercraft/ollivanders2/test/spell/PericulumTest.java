package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for the PERICULUM spell.
 *
 * <p>PERICULUM launches red star-burst fireworks. Tests verify that the spell correctly
 * spawns fireworks with the expected red color and star effect type.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.PERICULUM for the spell implementation
 * @see PyrotechniaTest for inherited test framework
 */
@Isolated
public class PericulumTest extends PyrotechniaTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.PERICULUM
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.PERICULUM;
    }
}
