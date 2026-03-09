package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for the LUMOS_MAXIMA spell.
 *
 * <p>LUMOS_MAXIMA is a Wand-Lighting Charm that produces a blinding flash of bright white light.
 * The spell applies Blindness to nearby entities within 5-10 blocks of the caster (the caster
 * is not affected). The effect lasts between 5 and 30 seconds depending on caster skill level
 * and is accompanied by a flash particle effect.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.LUMOS_MAXIMA for the spell implementation
 * @see AddPotionEffectTest for inherited test framework
 */
@Isolated
public class LumosMaximaTest extends AddPotionEffectTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.LUMOS_MAXIMA
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.LUMOS_MAXIMA;
    }
}
