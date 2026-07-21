package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.INCENDIO}. Extends {@link IncendioBaseTest} for the shared
 * fire-spell tests.
 */
public class IncendioTest extends IncendioBaseTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.INCENDIO;
    }
}
