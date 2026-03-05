package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for REDUCIO, the Shrinking Charm.
 *
 * <p>Inherits all size-change tests from {@link ChangeEntitySizeTest} as the shrinking variant:</p>
 * <ul>
 * <li>Adult peaceful entities are made babies</li>
 * <li>Slimes shrink smaller</li>
 * <li>Hostile entities require skill level &ge; 100</li>
 * <li>Multiple entities can be targeted per cast</li>
 * </ul>
 *
 * @author Azami7
 */
@Isolated
public class ReducioTest extends ChangeEntitySizeTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.REDUCIO;
    }
}