package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.LUMOS_CAERULEUM;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the LUMOS_CAERULEUM spell.
 *
 * <p>LUMOS_CAERULEUM transforms a glass bottle held in the caster's off-hand into a soul lantern. These tests
 * verify the happy path, the multi-bottle stack case (only one bottle is consumed, the rest are preserved), and
 * the no-op behavior when the off-hand does not hold a glass bottle.</p>
 *
 * @see LUMOS_CAERULEUM
 */
public class LumosCaeruleumTest extends O2SpellTestSuper {
    /**
     * Returns the spell type being tested.
     *
     * @return LUMOS_CAERULEUM spell type
     */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.LUMOS_CAERULEUM;
    }

    /**
     * Tests transfiguring a single glass bottle.
     *
     * <p>Verifies that:</p>
     * <ul>
     * <li>A single glass bottle in the caster's off-hand is converted to a soul lantern</li>
     * <li>The lantern is given the "Jar of Bluebell Flames" display name</li>
     * <li>The spell is killed after the effect resolves</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld("LumosCaeruleum");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        caster.getInventory().setItemInOffHand(new ItemStack(Material.GLASS_BOTTLE, 1));
        LUMOS_CAERULEUM lumosCaeruleum = (LUMOS_CAERULEUM) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(1);

        ItemStack heldItem = caster.getInventory().getItemInOffHand();
        assertNotNull(heldItem, "lantern not in player's off hand");
        assertEquals(Material.SOUL_LANTERN, heldItem.getType(), "off hand item not expected type");
        assertEquals(1, heldItem.getAmount(), "expected a single lantern");
        assertNotNull(heldItem.getItemMeta());
        assertTrue(heldItem.getItemMeta().hasDisplayName(), "lantern was not given a display name");
        // production sets the name to "§9Jar of Bluebell Flames"; strip the color code before comparing
        assertEquals("Jar of Bluebell Flames", TestCommon.cleanChatMessage(heldItem.getItemMeta().getDisplayName()),
                "lantern display name not as expected");
        assertTrue(lumosCaeruleum.isKilled(), "spell not killed after capturing flames");
    }

    /**
     * Tests transfiguring one bottle from a stack of bottles.
     *
     * <p>This covers the stack-handling case: when the caster holds more than one glass bottle, exactly one is
     * consumed and converted to a lantern, and the remaining bottles must be preserved rather than destroyed.</p>
     *
     * <p>Verifies that:</p>
     * <ul>
     * <li>The off-hand holds a single soul lantern after the cast</li>
     * <li>The remaining bottles (original count minus one) are dropped into the world, not lost</li>
     * <li>The spell is killed after the effect resolves</li>
     * </ul>
     */
    @Test
    void multipleBottlesTest() {
        World testWorld = mockServer.addSimpleWorld("LumosCaeruleumStack");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        int originalBottleCount = 5;
        caster.getInventory().setItemInOffHand(new ItemStack(Material.GLASS_BOTTLE, originalBottleCount));
        LUMOS_CAERULEUM lumosCaeruleum = (LUMOS_CAERULEUM) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(1);

        // one bottle was consumed and became the lantern in the off-hand
        ItemStack heldItem = caster.getInventory().getItemInOffHand();
        assertNotNull(heldItem, "lantern not in player's off hand");
        assertEquals(Material.SOUL_LANTERN, heldItem.getType(), "off hand item not expected type");
        assertEquals(1, heldItem.getAmount(), "expected a single lantern");

        // the remaining bottles must have been dropped into the world, not destroyed
        ItemStack droppedBottles = null;
        for (Item item : TestCommon.getAllItems(testWorld)) {
            if (item.getItemStack().getType() == Material.GLASS_BOTTLE) {
                droppedBottles = item.getItemStack();
                break;
            }
        }
        assertNotNull(droppedBottles, "leftover glass bottles were not dropped into the world");
        assertEquals(originalBottleCount - 1, droppedBottles.getAmount(), "wrong number of leftover bottles preserved");

        assertTrue(lumosCaeruleum.isKilled(), "spell not killed after capturing flames");
    }

    /**
     * Tests spell behavior when the off-hand does not hold a glass bottle.
     *
     * <p>Verifies that:</p>
     * <ul>
     * <li>The unsupported item is left unchanged in the off-hand</li>
     * <li>No leftover items are dropped into the world</li>
     * <li>The spell is still killed after finding no valid target</li>
     * </ul>
     */
    @Test
    void invalidTargetTest() {
        World testWorld = mockServer.addSimpleWorld("LumosCaeruleumInvalid");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        caster.getInventory().setItemInOffHand(new ItemStack(Material.HONEY_BOTTLE, 1));
        LUMOS_CAERULEUM lumosCaeruleum = (LUMOS_CAERULEUM) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(1);

        ItemStack heldItem = caster.getInventory().getItemInOffHand();
        assertNotNull(heldItem, "caster no longer holding item in off hand");
        assertEquals(Material.HONEY_BOTTLE, heldItem.getType(), "off hand item was altered for an invalid target");
        assertTrue(TestCommon.getAllItems(testWorld).isEmpty(), "items were dropped for an invalid target");
        assertTrue(lumosCaeruleum.isKilled(), "spell not killed when invalid target detected");
    }

    /**
     * Tests reversion. LUMOS_CAERULEUM makes no temporary changes that require reversion.
     */
    @Override
    @Test
    void revertTest() {
        // nothing to revert
    }
}
