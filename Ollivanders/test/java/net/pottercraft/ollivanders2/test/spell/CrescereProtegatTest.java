package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for the {@link net.pottercraft.ollivanders2.spell.CRESCERE_PROTEGAT} spell, which grows a
 * stationary shield spell's radius.
 *
 * <p>{@code CRESCERE_PROTEGAT} extends {@code HORREAT_PROTEGAT} and only flips the change direction, so this
 * class reuses {@link HorreatProtegatTest}'s tests, overriding only the spell type and the expected change
 * direction. {@link HorreatProtegatTest#doCheckEffectTest()} therefore verifies the radius grows here.</p>
 *
 * @author Azami7
 */
public class CrescereProtegatTest extends HorreatProtegatTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.CRESCERE_PROTEGAT;
    }

    @Override
    boolean expectShrink() {
        return false;
    }
}
