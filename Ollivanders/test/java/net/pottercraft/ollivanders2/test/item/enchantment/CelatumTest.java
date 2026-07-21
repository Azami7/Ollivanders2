package net.pottercraft.ollivanders2.test.item.enchantment;

import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.item.enchantment.CELATUM}. Despawn and inventory-pickup blocking
 * are covered by {@link EnchantmentTestSuper}; the entity-pickup, drop, and held handlers are no-ops.
 */
public class CelatumTest extends EnchantmentTestSuper {
    @Override @BeforeEach
    void setUp() {
        enchantmentType = ItemEnchantmentType.CELATUM;
        itemType = Material.WOODEN_SWORD;

        defaultArgs = "Some hidden text";
    }

    /**
     * CELATUM takes no action on entity pickup.
     */
    @Override @Test
    void doEntityPickupItemTest() {}

    /**
     * CELATUM takes no action on item drop.
     */
    @Override @Test
    void doItemDropTest() {}

    /**
     * CELATUM takes no action on held-slot change.
     */
    @Override @Test
    void doItemHeldTest() {}
}
