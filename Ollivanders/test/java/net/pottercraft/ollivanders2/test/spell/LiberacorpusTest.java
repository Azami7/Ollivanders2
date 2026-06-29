package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.LIBERACORPUS} spell, the counter-spell to the Levicorpus
 * Jinx.
 *
 * <p>LIBERACORPUS removes the {@link net.pottercraft.ollivanders2.effect.O2EffectType#SUSPENSION} effect from a target.
 * The inherited {@link RemoveO2EffectTest} coverage verifies that the effect is removed from a target the projectile
 * reaches.</p>
 *
 * @author Azami7
 */
public class LiberacorpusTest extends RemoveO2EffectTest {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.LIBERACORPUS;
    }
}