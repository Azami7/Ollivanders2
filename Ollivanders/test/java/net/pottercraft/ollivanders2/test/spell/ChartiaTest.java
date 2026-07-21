package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.CHARTIA}. Extends {@link DivinationBaseTest} for the
 * shared divination tests; this spell requires the caster to be holding (unconsumed) playing cards.
 *
 * @author Azami7
 */
public class ChartiaTest extends DivinationBaseTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.CHARTIA;
    }
}