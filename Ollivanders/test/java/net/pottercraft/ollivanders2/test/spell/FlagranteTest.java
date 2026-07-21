package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.FLAGRANTE}. Extends {@link ItemEnchantTest} for the
 * shared item-enchantment tests.
 */
public class FlagranteTest extends ItemEnchantTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.FLAGRANTE;
    }

    /**
     * Get a valid item type for FLAGRANTE testing.
     *
     * @return Material.COMPASS (arbitrary item used for testing)
     */
    @Override
    @NotNull
    Material getValidItemType() {
        return Material.COMPASS;
    }

    /**
     * Get an invalid item type for FLAGRANTE testing.
     *
     * <p>FLAGRANTE has no specific item type restrictions (can enchant any item type),
     * so this returns null to skip invalid type testing.</p>
     *
     * @return null (no invalid types)
     */
    @Override
    @Nullable
    Material getInvalidItemType() {
        return null;
    }

    /**
     * Test enchantment argument generation.
     *
     * <p>FLAGRANTE does not generate enchantment arguments (uses empty string).</p>
     */
    @Override
    @Test
    void createEnchantmentArgsTest() {
        // no enchantment args
    }

    /**
     * Test item alteration.
     *
     * <p>FLAGRANTE does not modify the appearance or properties of the enchanted item.</p>
     */
    @Override
    @Test
    void alterItemTest() {
        // does not alter item
    }
}
