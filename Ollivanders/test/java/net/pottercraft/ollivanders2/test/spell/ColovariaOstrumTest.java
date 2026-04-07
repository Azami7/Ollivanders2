package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.COLOVARIA_OSTRUM} spell (purple colour change).
 */
public class ColovariaOstrumTest extends ChangeColorableTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.COLOVARIA_OSTRUM;
    }
}
