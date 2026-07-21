package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.ENGORGIO}, the Engorgement Charm. Extends
 * {@link ChangeEntitySizeTest} for the shared size-change tests, as the growing variant.
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