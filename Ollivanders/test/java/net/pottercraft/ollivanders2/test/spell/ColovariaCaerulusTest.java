package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.COLOVARIA_CAERULUS} spell (blue colour change).
 */
public class ColovariaCaerulusTest extends ChangeColorableTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.COLOVARIA_CAERULUS;
    }
}
