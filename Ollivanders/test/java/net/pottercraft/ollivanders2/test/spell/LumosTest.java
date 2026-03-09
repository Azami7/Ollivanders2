package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for the LUMOS spell.
 *
 * <p>LUMOS is a Wand-Lighting Charm that grants Night Vision to the caster and all nearby
 * allies within the effect radius. The spell has a minimum duration of 30 seconds and an
 * effect radius that ranges from 5 to 20 blocks depending on caster skill level.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.LUMOS for the spell implementation
 * @see AddPotionEffectTest for inherited test framework
 */
@Isolated
public class LumosTest extends AddPotionEffectTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.LUMOS
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.LUMOS;
    }
}
