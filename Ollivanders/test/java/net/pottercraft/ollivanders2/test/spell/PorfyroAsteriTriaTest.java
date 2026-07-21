package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.PORFYRO_ASTERI_TRIA}. Extends {@link PyrotechniaTest} for
 * the shared fireworks tests.
 */
public class PorfyroAsteriTriaTest extends PyrotechniaTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.PORFYRO_ASTERI_TRIA;
    }
}
