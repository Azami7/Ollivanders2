package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for the EPISKEY spell.
 *
 * <p>EPISKEY is a healing charm that applies Regeneration effect to targets, allowing them to
 * heal minor injuries for 15-120 seconds depending on caster skill level. The healing effect
 * strength is consistent and does not scale with skill level.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.EPISKEY for the spell implementation
 * @see AddPotionEffectTest for inherited test framework
 */
@Isolated
public class EpiskeyTest extends AddPotionEffectTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.EPISKEY
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.EPISKEY;
    }
}
