package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for ENGORGIO, the Engorgement Charm.
 *
 * <p>Inherits all size-change tests from {@link ChangeEntitySizeTest} as the growing variant:</p>
 * <ul>
 * <li>Baby peaceful entities are made adults</li>
 * <li>Slimes grow larger</li>
 * <li>Hostile entities require skill level &ge; 100</li>
 * <li>Multiple entities can be targeted per cast</li>
 * </ul>
 *
 * @author Azami7
 */
public class EngorgioTest extends ChangeEntitySizeTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.ENGORGIO;
    }
}