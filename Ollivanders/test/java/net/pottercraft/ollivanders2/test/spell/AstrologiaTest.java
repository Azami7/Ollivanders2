package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.ASTROLOGIA} divination spell.
 *
 * <p>Inherits the shared divination coverage from {@link DivinationBaseTest}; this spell has no facing-block or
 * held-item requirement.</p>
 *
 * @author Azami7
 */
public class AstrologiaTest extends DivinationBaseTest {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.ASTROLOGIA;
    }
}