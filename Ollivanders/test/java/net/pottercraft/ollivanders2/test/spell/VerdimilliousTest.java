package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.VERDIMILLIOUS}. Extends {@link SparksTest} for the shared
 * sparks tests.
 *
 * @author Azami7
 */
public class VerdimilliousTest extends SparksTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.VERDIMILLIOUS;
    }
}
