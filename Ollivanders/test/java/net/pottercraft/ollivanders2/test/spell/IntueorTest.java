package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.INTUEOR} crystal-gazing divination spell.
 *
 * <p>Inherits the shared divination coverage from {@link DivinationBaseTest}; this spell requires the caster to be
 * facing a crystal ball (a glass block).</p>
 *
 * @author Azami7
 */
public class IntueorTest extends DivinationBaseTest {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.INTUEOR;
    }
}