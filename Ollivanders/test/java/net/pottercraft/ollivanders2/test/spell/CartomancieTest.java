package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.CARTOMANCIE}. Extends {@link DivinationBaseTest} for the
 * shared divination tests; this spell requires the caster to be holding (unconsumed) tarot cards.
 *
 * @author Azami7
 */
public class CartomancieTest extends DivinationBaseTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.CARTOMANCIE;
    }
}