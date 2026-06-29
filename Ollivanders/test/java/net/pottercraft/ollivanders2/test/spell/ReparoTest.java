package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.REPARO;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Tests for the {@link REPARO} spell, the standard Mending Charm that repairs ordinary damageable tools.
 *
 * <p>Inherits the shared repair and skip-path coverage from {@link ReparoBaseTest}, supplying the REPARO
 * spell type and a diamond tool as the repair target.</p>
 *
 * @author Azami7
 * @see REPARO
 */
public class ReparoTest extends ReparoBaseTest {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.REPARO;
    }

    /** {@inheritDoc} */
    @Override
    @NotNull
    Material getRepairableMaterial() {
        return Material.DIAMOND_PICKAXE;
    }
}