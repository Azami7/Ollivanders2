package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for the BOTHYNUS_TRIA spell.
 *
 * <p>BOTHYNUS_TRIA launches premium yellow and orange star-burst fireworks with trailing
 * and fade effects (fading to silver). Tests verify that the spell correctly spawns fireworks
 * with the expected dual colors, star effect type, trailing effects, and silver fade.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.BOTHYNUS_TRIA for the spell implementation
 * @see PyrotechniaTest for inherited test framework
 */
@Isolated
public class BothynusTriaTest extends PyrotechniaTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.BOTHYNUS_TRIA
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.BOTHYNUS_TRIA;
    }
}
