package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.spell.GEMINO}. Extends {@link ItemEnchantTest} for the shared
 * item-enchantment tests.
 */
public class GeminoTest extends ItemEnchantTest {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.GEMINO;
    }

    /**
     * Get a valid item type for GEMINO testing.
     *
     * @return Material.WOODEN_SWORD (arbitrary item used for testing)
     */
    @Override
    @NotNull
    Material getValidItemType() {
        return Material.WOODEN_SWORD;
    }

    /**
     * Get an invalid item type for GEMINO testing.
     *
     * <p>GEMINO has no specific item type restrictions (can enchant any item type),
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
     * <p>GEMINO does not generate enchantment arguments (uses empty string).</p>
     */
    @Override
    @Test
    void createEnchantmentArgsTest() {
        // no enchantment args
    }

    /**
     * Test item alteration.
     *
     * <p>GEMINO does not modify the appearance or properties of the enchanted item.</p>
     */
    @Override
    @Test
    void alterItemTest() {
        // does not alter item
    }
}
