package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.VOLATUS;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the VOLATUS spell.
 *
 * <p>VOLATUS is a charms spell that enchants broomsticks to enable flight. The spell has strict
 * item type restrictions: only BASIC_BROOM (O2ItemType) can be enchanted. VOLATUS does not
 * generate enchantment arguments and does not modify the item appearance.</p>
 *
 * @see net.pottercraft.ollivanders2.spell.VOLATUS for the spell implementation
 * @see ItemEnchantTest for inherited test framework
 */
public class VolatusTest extends ItemEnchantTest {
    /**
     * Get the spell type being tested.
     *
     * @return O2SpellType.VOLATUS
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.VOLATUS;
    }

    /**
     * Get a valid material type for testing (not used for VOLATUS since it uses O2ItemType).
     *
     * @return Material.WOODEN_SWORD (fallback; actual valid items are O2ItemType.BASIC_BROOM)
     */
    @Override
    @NotNull
    Material getValidItemType() {
        return Material.WOODEN_SWORD;
    }

    /**
     * Get an invalid item type for VOLATUS testing.
     *
     * <p>VOLATUS only enchants BASIC_BROOM via O2ItemTypeAllowList, so all other materials
     * including generic broomstick-like items are invalid. This returns null to use base
     * class invalid testing (BARRIER fallback).</p>
     *
     * @return null (coverage via createInvalidItem)
     */
    @Override
    @Nullable
    Material getInvalidItemType() {
        return null;
    }

    /**
     * Test O2ItemType filtering for VOLATUS.
     *
     * <p>Verifies that only BASIC_BROOM (O2ItemType) can be enchanted by VOLATUS.
     * Other O2ItemTypes like BROOMSTICK are rejected.</p>
     */
    @Test
    void o2ItemTypeFilterTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);
        VOLATUS volatus = (VOLATUS) castSpell(caster, location, targetLocation);

        // test valid O2ItemType (BASIC_BROOM)
        ItemStack basicBroom = O2ItemType.BASIC_BROOM.getItem(1);
        assertNotNull(basicBroom, "BASIC_BROOM item could not be created");
        assertTrue(volatus.canBeEnchanted(basicBroom), "BASIC_BROOM cannot be enchanted by VOLATUS");

        // test invalid O2ItemType (BROOMSTICK is different from BASIC_BROOM)
        ItemStack broomstick = O2ItemType.BROOMSTICK.getItem(1);
        assertNotNull(broomstick, "BROOMSTICK item could not be created");
        assertFalse(volatus.canBeEnchanted(broomstick), "BROOMSTICK can be enchanted when it should not be");
    }

    /**
     * Test enchantment argument generation.
     *
     * <p>VOLATUS does not generate enchantment arguments (uses empty string).</p>
     */
    @Override
    @Test
    void createEnchantmentArgsTest() {
        // no enchantment args
    }

    /**
     * Test item alteration.
     *
     * <p>VOLATUS does not modify the appearance or properties of the enchanted broomstick.</p>
     */
    @Override
    @Test
    void alterItemTest() {
        // does not alter item
    }
}
