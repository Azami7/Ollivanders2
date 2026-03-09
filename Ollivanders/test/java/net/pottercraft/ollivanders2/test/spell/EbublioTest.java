package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for the EBUBLIO spell.
 *
 * <p>Inherits all immobilization tests from {@link ImmobilizePlayerTest} as the glass-prison variant:</p>
 * <ul>
 * <li>Target detection and effect application</li>
 * <li>Effect duration clamped to min/max bounds</li>
 * <li>Partial immobilization (allows rotation, prevents movement)</li>
 * <li>Shell of WHITE_STAINED_GLASS built around the target</li>
 * <li>Prison blocks reverted after effect duration expires</li>
 * <li>Scale attribute validation (not testable in MockBukkit)</li>
 * </ul>
 *
 * @author Azami7
 * @see net.pottercraft.ollivanders2.spell.EBUBLIO for the spell implementation
 * @see ImmobilizePlayerTest for the inherited test framework
 */
@Isolated
public class EbublioTest extends ImmobilizePlayerTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.EBUBLIO
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.EBUBLIO;
    }

    /**
     * Test that only normal-sized players can be targeted.
     *
     * <p>EBUBLIO only targets players with scale ≤ 1.0. This test cannot be implemented
     * as MockBukkit does not support the SCALE attribute. The test is overridden to do nothing but
     * would verify that oversized players (scale > 1.0) cannot be targeted.</p>
     */
    @Override
    @Test
    void invalidTargetTest() {
        // cannot test until MockBukkit supports the SCALE attribute
    }
}
