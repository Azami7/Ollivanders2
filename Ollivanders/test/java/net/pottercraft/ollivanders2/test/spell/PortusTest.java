package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.PORTUS;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the PORTUS spell.
 *
 * <p>PORTUS is a charms spell that creates portkeys—items that teleport whoever picks them up
 * to a pre-arranged destination. The spell uses held-item mode, enchanting the off-hand item
 * and storing the caster's current location as the portkey destination. PORTUS does not
 * modify the item appearance and has no item type restrictions.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.PORTUS for the spell implementation
 * @see ItemEnchantTest for inherited test framework
 */
public class PortusTest extends ItemEnchantTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.PORTUS
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.PORTUS;
    }

    /**
     * Get a valid item type for PORTUS testing.
     *
     * @return Material.BUCKET (arbitrary item used for testing)
     */
    @Override
    @NotNull
    Material getValidItemType() {
        return Material.BUCKET;
    }

    /**
     * Get an invalid item type for PORTUS testing.
     *
     * <p>PORTUS has no specific item type restrictions (can enchant any item type),
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
     * Test that portkey destination coordinates are stored as enchantment arguments.
     *
     * <p>Verifies that PORTUS correctly captures the caster's location at enchantment time
     * and stores it in the format "world_name x y z". This destination is used when the
     * portkey is activated to teleport the holder.</p>
     */
    @Override
    @Test
    void createEnchantmentArgsTest() {
        World testWorld = mockServer.addSimpleWorld("Portus");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        PORTUS portus = (PORTUS) castSpell(caster, location, targetLocation);
        ItemStack itemStack = createValidItem(portus, 1);
        caster.getInventory().setItemInOffHand(itemStack);

        assertTrue(portus.canBeEnchanted(itemStack));
        mockServer.getScheduler().performTicks(2);

        String enchantmentArgs = portus.getEnchantmentArgs();
        String expectedArgs = caster.getLocation().getWorld().getName() + " " + caster.getLocation().getX() + " " + caster.getLocation().getY() + " " + caster.getLocation().getZ();
        assertFalse(enchantmentArgs.isEmpty(), "Enchantment args not set");
        assertEquals(expectedArgs, enchantmentArgs, "unexpected enchantment args");
    }

    /**
     * Test item alteration.
     *
     * <p>PORTUS does not modify the appearance or properties of the enchanted item.</p>
     */
    @Override
    @Test
    void alterItemTest() {
        // does not alter item
    }
}
