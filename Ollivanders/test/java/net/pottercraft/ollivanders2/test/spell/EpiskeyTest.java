package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.EPISKEY}. Extends {@link AddPotionEffectTest} for the
 * shared potion-effect tests.
 */
public class EpiskeyTest extends AddPotionEffectTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.EPISKEY;
    }
}
