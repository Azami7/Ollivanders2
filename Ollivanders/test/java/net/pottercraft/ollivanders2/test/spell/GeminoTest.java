package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

/**
 * Unit tests for the GEMINO spell.
 *
 * <p>GEMINO is a dark arts duplication curse that enchants items to duplicate exponentially
 * (2^magnitude copies) when picked up. The spell has no magnitude variation in the duplication
 * formula itself, but magnitude still affects how many copies are created. GEMINO enchantments
 * do not modify the item appearance and generate no enchantment arguments.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.GEMINO for the spell implementation
 * @see ItemEnchantTest for inherited test framework
 */
@Isolated
public class GeminoTest extends ItemEnchantTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.GEMINO
     */
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
