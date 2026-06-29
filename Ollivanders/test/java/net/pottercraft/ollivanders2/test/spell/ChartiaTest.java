package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.CHARTIA} playing-card cartomancy divination spell.
 *
 * <p>Inherits the shared divination coverage from {@link DivinationBaseTest}; this spell requires the caster to be
 * holding playing cards (not consumed).</p>
 *
 * @author Azami7
 */
public class ChartiaTest extends DivinationBaseTest {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.CHARTIA;
    }
}