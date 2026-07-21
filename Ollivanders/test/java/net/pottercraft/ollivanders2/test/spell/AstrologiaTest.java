package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.ASTROLOGIA}. Extends {@link DivinationBaseTest} for the
 * shared divination tests; this spell has no facing-block or held-item requirement.
 *
 * @author Azami7
 */
public class AstrologiaTest extends DivinationBaseTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.ASTROLOGIA;
    }
}