package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.CALAMUS}. Extends {@link ItemToItemTransfigurationTest}
 * for the shared transfiguration tests.
 *
 * @author Azami7
 */
public class CalamusTest extends ItemToItemTransfigurationTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.CALAMUS;
    }

    /**
     * @return STICK, the only valid target for CALAMUS
     */
    Material getValidItemType() {
        return Material.STICK;
    }

    /**
     * @return ACACIA_LOG, a material not in the CALAMUS transfiguration map
     */
    Material getInvalidItemType() {
        return Material.ACACIA_LOG;
    }
}
