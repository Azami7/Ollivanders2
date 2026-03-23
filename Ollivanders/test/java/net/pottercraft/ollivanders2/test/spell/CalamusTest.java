package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.CALAMUS} spell (match to needle).
 *
 * <p>CALAMUS transfigures sticks into arrows. Only sticks are valid targets.</p>
 *
 * @author Azami7
 */
public class CalamusTest extends ItemToItemTransfigurationTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.CALAMUS
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.CALAMUS;
    }

    /**
     * Sticks are valid targets for CALAMUS.
     *
     * @return Material.STICK
     */
    Material getValidItemType() {
        return Material.STICK;
    }

    /**
     * Acacia logs are not in the CALAMUS transfiguration map.
     *
     * @return Material.ACACIA_LOG
     */
    Material getInvalidItemType() {
        return Material.ACACIA_LOG;
    }
}
