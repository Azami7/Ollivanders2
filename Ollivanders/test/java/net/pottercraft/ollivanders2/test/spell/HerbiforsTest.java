package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Tests for {@link net.pottercraft.ollivanders2.spell.HERBIFORS}. Inherits all test logic from
 * {@link GaleatiTest}; only provides the spell type.
 *
 * @author Azami7
 */
public class HerbiforsTest extends GaleatiTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.HERBIFORS;
    }
}
