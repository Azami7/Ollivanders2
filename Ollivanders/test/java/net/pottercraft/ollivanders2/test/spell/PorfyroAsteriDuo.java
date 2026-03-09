package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for the PORFYRO_ASTERI_DUO spell.
 *
 * <p>PORFYRO_ASTERI_DUO launches purple and blue star-burst fireworks with trailing effects.
 * Tests verify that the spell correctly spawns fireworks with the expected dual colors,
 * star effect type, and trailing effects.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.PORFYRO_ASTERI_DUO for the spell implementation
 * @see PyrotechniaTest for inherited test framework
 */
@Isolated
public class PorfyroAsteriDuo extends PyrotechniaTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.PORFYRO_ASTERI_DUO
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.PORFYRO_ASTERI_DUO;
    }
}
