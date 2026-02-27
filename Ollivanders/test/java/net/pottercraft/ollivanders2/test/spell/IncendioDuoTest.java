package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for the INCENDIO_DUO fire spell.
 *
 * <p>INCENDIO_DUO is a stronger variant of INCENDIO with the following characteristics:
 * <ul>
 * <li><strong>Radius:</strong> 2 blocks (double that of INCENDIO)</li>
 * <li><strong>Strafe:</strong> Yes - affects multiple blocks and entities in radius</li>
 * <li><strong>Duration:</strong> 2x modifier - burn duration is twice that of INCENDIO</li>
 * </ul>
 *
 * <p>This test class runs all inherited tests from {@link IncendioSuperTest} with INCENDIO_DUO
 * as the spell type, validating that the enhanced radius and strafe behavior work correctly.</p>
 *
 * @see IncendioSuperTest
 */
@Isolated
public class IncendioDuoTest extends IncendioSuperTest {
    /**
     * Returns the spell type being tested.
     *
     * @return INCENDIO_DUO spell type
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.INCENDIO_DUO;
    }
}
