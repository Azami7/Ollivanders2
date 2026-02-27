package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for the INCENDIO fire spell.
 *
 * <p>INCENDIO is the basic fire-making charm with the following characteristics:
 * <ul>
 * <li><strong>Radius:</strong> 1 block (single target only)</li>
 * <li><strong>Strafe:</strong> No - affects only the single targeted block and entity</li>
 * <li><strong>Duration:</strong> 1x modifier - baseline burn duration</li>
 * <li><strong>Entity Radius:</strong> 1 block - only entities directly at target location are affected</li>
 * </ul>
 *
 * <p>This test class runs all inherited tests from {@link IncendioSuperTest} with INCENDIO
 * as the spell type, validating that the basic single-target fire spell behavior works correctly.</p>
 *
 * @see IncendioSuperTest
 */
@Isolated
public class IncendioTest extends IncendioSuperTest {
    /**
     * Returns the spell type being tested.
     *
     * @return INCENDIO spell type
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.INCENDIO;
    }
}
