package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for the BOTHYNUS_DUO spell.
 *
 * <p>BOTHYNUS_DUO launches yellow and orange star-burst fireworks with trailing effects.
 * Tests verify that the spell correctly spawns fireworks with the expected dual colors,
 * star effect type, and trailing effects.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.BOTHYNUS_DUO for the spell implementation
 * @see PyrotechniaTest for inherited test framework
 */
public class BothynusDuoTest extends PyrotechniaTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.BOTHYNUS_DUO
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.BOTHYNUS_DUO;
    }
}
