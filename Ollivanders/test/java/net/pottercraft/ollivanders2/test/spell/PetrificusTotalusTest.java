package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.PETRIFICUS_TOTALUS}. Extends {@link ImmobilizePlayerTest}
 * for the shared immobilization tests.
 */
public class PetrificusTotalusTest extends ImmobilizePlayerTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.PETRIFICUS_TOTALUS;
    }
}
