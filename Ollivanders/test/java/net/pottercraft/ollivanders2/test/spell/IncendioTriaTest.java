package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for the INCENDIO_TRIA fire spell.
 *
 * <p>INCENDIO_TRIA is the strongest fire spell with the following characteristics:
 * <ul>
 * <li><strong>Block Radius:</strong> 4 blocks (quadruple that of INCENDIO)</li>
 * <li><strong>Entity Radius:</strong> 2 blocks (double that of INCENDIO)</li>
 * <li><strong>Strafe:</strong> Yes - affects all blocks and entities in radius</li>
 * <li><strong>Duration:</strong> 4x modifier - burn duration is four times that of INCENDIO</li>
 * </ul>
 *
 * <p>This test class runs all inherited tests from {@link IncendioSuperTest} with INCENDIO_TRIA
 * as the spell type, validating that the large radius and strafe behavior work correctly.</p>
 *
 * @see IncendioSuperTest
 */
@Isolated
public class IncendioTriaTest extends IncendioSuperTest {
    /**
     * Returns the spell type being tested.
     *
     * @return INCENDIO_TRIA spell type
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.INCENDIO_TRIA;
    }
}
