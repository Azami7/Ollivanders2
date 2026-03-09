package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for the OBSCURO spell.
 *
 * <p>OBSCURO is a charm that applies Blindness effect to targets, preventing them from seeing
 * for 30-120 seconds depending on caster skill level. The blindness effect strength is
 * consistent and does not scale with skill level.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.OBSCURO for the spell implementation
 * @see AddPotionEffectTest for inherited test framework
 */
@Isolated
public class ObscuroTest extends AddPotionEffectTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.OBSCURO
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.OBSCURO;
    }
}
