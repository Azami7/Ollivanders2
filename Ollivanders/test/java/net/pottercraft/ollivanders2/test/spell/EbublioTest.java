package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.EBUBLIO}. Extends {@link ImmobilizePlayerTest} for the
 * shared immobilization tests, as the glass-prison variant.
 *
 * @author Azami7
 */
public class EbublioTest extends ImmobilizePlayerTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.EBUBLIO;
    }

    /**
     * Scale-attribute targeting (only players with scale ≤ 1.0) cannot be tested: MockBukkit does not support the
     * SCALE attribute.
     */
    @Override
    @Test
    void invalidTargetTest() {
        // cannot test until MockBukkit supports the SCALE attribute
    }
}
