package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for the COMETES spell.
 *
 * <p>COMETES launches white star-burst fireworks. Tests verify that the spell correctly
 * spawns fireworks with the expected white color and star effect type.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.COMETES for the spell implementation
 * @see PyrotechniaTest for inherited test framework
 */
@Isolated
public class CometesTest extends PyrotechniaTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.COMETES
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.COMETES;
    }
}
