package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for the PORFYRO_ASTERI spell.
 *
 * <p>PORFYRO_ASTERI launches purple star-burst fireworks. Tests verify that the spell correctly
 * spawns fireworks with the expected purple color and star effect type.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.PORFYRO_ASTERI for the spell implementation
 * @see PyrotechniaTest for inherited test framework
 */
public class PorfyroAsteriTest extends PyrotechniaTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.PORFYRO_ASTERI
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.PORFYRO_ASTERI;
    }
}
