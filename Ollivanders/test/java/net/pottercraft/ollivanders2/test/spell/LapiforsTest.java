package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.LAPIFORS;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Tests for {@link LAPIFORS}.
 *
 * <p>Lapifors accepts any dropped item so no invalid material type is defined and
 * {@code getInvalidMaterialType()} is not overridden (returns null).</p>
 *
 * @see LAPIFORS
 */
public class LapiforsTest extends ItemToEntityTransfigurationTest {
    /**
     * Returns the spell type being tested.
     *
     * @return LAPIFORS spell type
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.LAPIFORS;
    }

    /**
     * @return BOWL, an arbitrary item since Lapifors can transfigure any dropped item
     */
    @NotNull
    Material getValidMaterialType() {
        return Material.BOWL;
    }
}
