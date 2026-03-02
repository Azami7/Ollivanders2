package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.item.wand.O2WandCoreType;
import net.pottercraft.ollivanders2.item.wand.O2WandWoodType;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.ItemEnchant;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Abstract base test class for ItemEnchant spell implementations.
 *
 * <p>Provides common test coverage for all item enchantment spells, including projectile-based
 * enchantment (targeting dropped items) and held-item enchantment (targeting off-hand items).
 * Concrete subclasses must implement {@link #getValidItemType()}, {@link #getInvalidItemType()},
 * {@link #createEnchantmentArgsTest()}, and {@link #alterItemTest()}.</p>
 *
 * <p>Test Coverage:</p>
 * <ul>
 * <li>Projectile-based item enchantment and item stack splitting</li>
 * <li>Held-item enchantment from player off-hand</li>
 * <li>Item stack splitting when enchanting stacks with multiple items</li>
 * <li>Magnitude calculation based on caster skill and spell strength</li>
 * <li>Item type validation and filtering</li>
 * <li>Spell-specific enchantment argument generation and item alteration</li>
 * </ul>
 *
 * @see net.pottercraft.ollivanders2.spell.ItemEnchant for the spell base class
 * @see O2SpellTestSuper for inherited spell testing framework
 */
abstract class ItemEnchantTest extends O2SpellTestSuper {
    /**
     * Get the primary material type that this enchantment spell can target.
     *
     * @return a Material type that is valid for this enchantment
     */
    @NotNull
    abstract Material getValidItemType();

    /**
     * Get an invalid material type that this enchantment spell cannot target.
     *
     * <p>Return null to skip testing invalid material types. This is typically used when
     * the spell accepts all items (no material restrictions) or when the spell uses
     * {@link net.pottercraft.ollivanders2.item.O2ItemType} filtering instead of Material filtering.</p>
     *
     * @return an invalid Material, or null to skip invalid type testing
     */
    @Nullable
    abstract Material getInvalidItemType();

    @Override
    void spellConstructionTest() {
        // no special set up code
    }

    /**
     * Create a valid item stack that this enchantment spell can target.
     *
     * <p>Prioritizes creating items from the spell's O2ItemTypeAllowList if available,
     * otherwise creates a stack of the valid material type. This allows testing spells
     * that have specific O2ItemType restrictions.</p>
     *
     * @param itemEnchant the spell being tested
     * @param amount      the number of items in the stack
     * @return a valid ItemStack that can be enchanted
     */
    @NotNull
    ItemStack createValidItem(@NotNull ItemEnchant itemEnchant, int amount) {
        ItemStack itemStack = null;

        // check if this spell supports O2Items first
        if (!itemEnchant.getO2ItemTypeAllowList().isEmpty())
            itemStack = itemEnchant.getO2ItemTypeAllowList().getFirst().getItem(amount);

        if (itemStack == null)
            itemStack = new ItemStack(getValidItemType(), amount);

        return itemStack;
    }

    /**
     * Create an invalid item stack that this enchantment spell cannot target.
     *
     * <p>If the spell has no invalid material types (accepts all items), returns a BARRIER item.
     * Otherwise, returns a stack of the invalid material type specified by {@link #getInvalidItemType()}.</p>
     *
     * @return an invalid ItemStack that cannot be enchanted
     */
    @NotNull
    ItemStack createInvalidItem() {
        ItemStack itemStack = null;

        Material invalidMaterial = getInvalidItemType();
        if (invalidMaterial == null)
            itemStack = new ItemStack(Material.BARRIER, 1);
        else
            itemStack = new ItemStack(invalidMaterial, 1);

        return itemStack;
    }

    /**
     * Test projectile-based item enchantment.
     *
     * <p>For spells that use projectiles (noProjectile == false), verifies that:</p>
     * <ul>
     * <li>The spell targets items at its projectile endpoint</li>
     * <li>The original item is removed from the world</li>
     * <li>An enchanted item is dropped at the spell location</li>
     * <li>The enchanted item has the correct enchantment type applied</li>
     * </ul>
     */
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 3);

        ItemEnchant itemEnchant = (ItemEnchant) castSpell(caster, location, targetLocation);

        if (!itemEnchant.isNoProjectile()) {
            Item item = testWorld.dropItem(targetLocation, createValidItem(itemEnchant, 1));
            assertTrue(itemEnchant.canBeEnchanted(item));
            mockServer.getScheduler().performTicks(20);

            assertTrue(itemEnchant.isKilled(), "spell was not killed when target hit");
            assertTrue(item.isDead(), "Original item not removed");
            Item enchantedItem = EntityCommon.getItemAtLocation(itemEnchant.getLocation());
            assertNotNull(enchantedItem, "Enchanted item not dropped");
            assertTrue(Ollivanders2API.getItems().enchantedItems.isEnchanted(enchantedItem), "valid item type was not enchanted");
            assertEquals(itemEnchant.getEnchantmentType(), Ollivanders2API.getItems().enchantedItems.getEnchantmentType(enchantedItem.getItemStack()), "enchantment type not expected");
        }
    }

    /**
     * Test held-item enchantment from player off-hand.
     *
     * <p>For spells that use held-item mode (noProjectile == true), verifies that:</p>
     * <ul>
     * <li>Spell completes successfully when caster holds no item in off-hand</li>
     * <li>Spell enchants a valid item held in the caster's off-hand</li>
     * <li>The enchanted item is dropped at the spell location</li>
     * <li>The enchanted item has the correct enchantment type applied</li>
     * </ul>
     */
    @Test
    void affectsHeldItemTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        ItemEnchant itemEnchant = (ItemEnchant) castSpell(caster, location, targetLocation);

        if (itemEnchant.isNoProjectile()) {
            // test player holding nothing
            mockServer.getScheduler().performTicks(2);
            assertTrue(itemEnchant.isKilled(), "spell not killed immediately");
            Item enchantedItem = EntityCommon.getItemAtLocation(itemEnchant.getLocation());
            assertNull(enchantedItem, "item found at spell location when caster did not have item");

            // test player holding valid item
            caster.getInventory().setItemInOffHand(createValidItem(itemEnchant, 1));
            itemEnchant = (ItemEnchant) castSpell(caster, location, targetLocation);
            mockServer.getScheduler().performTicks(2);
            assertTrue(itemEnchant.isKilled());
            enchantedItem = EntityCommon.getItemAtLocation(itemEnchant.getLocation());
            assertNotNull(enchantedItem, "Enchanted item not dropped");
            assertTrue(Ollivanders2API.getItems().enchantedItems.isEnchanted(enchantedItem), "valid item type was not enchanted");
            assertEquals(itemEnchant.getEnchantmentType(), Ollivanders2API.getItems().enchantedItems.getEnchantmentType(enchantedItem.getItemStack()), "enchantment type not expected");
        }
    }

    /**
     * Test item stack splitting when enchanting stacks with multiple items.
     *
     * <p>Verifies that when enchanting a stack containing multiple items:</p>
     * <ul>
     * <li>Exactly one item is enchanted and dropped separately</li>
     * <li>The remaining items are dropped as a separate stack</li>
     * <li>The remainder stack has the correct reduced amount (original - 1)</li>
     * </ul>
     */
    @Test
    void enchantStackWithMultipleItemsTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        ItemEnchant itemEnchant = (ItemEnchant) castSpell(caster, location, targetLocation);

        ItemStack itemStack = createValidItem(itemEnchant, 2);
        if (itemEnchant.isNoProjectile()) {
            caster.getInventory().setItemInOffHand(itemStack);
        }
        else {
            testWorld.dropItem(targetLocation, itemStack);
        }

        mockServer.getScheduler().performTicks(20);
        assertTrue(itemEnchant.isKilled());

        ItemStack orig = null;
        ItemStack enchanted = null;

        for (Item item : itemEnchant.getNearbyItems(O2Spell.defaultRadius)) {
            if (Ollivanders2API.getItems().enchantedItems.isEnchanted(item)) {
                enchanted = item.getItemStack();
            }
            else
                orig = item.getItemStack();
        }
        assertNotNull(enchanted, "Enchanted item not dropped");
        assertNotNull(orig, "Remainder of original not dropped");
        assertEquals(1, orig.getAmount(), "Unexpected remainder amount");
    }

    /**
     * Test magnitude calculation based on caster skill and spell strength.
     *
     * <p>Verifies the magnitude formula: magnitude = (int)((usesModifier / 10) * strength),
     * clamped to the range [minMagnitude, maxMagnitude]. Tests with various experience levels:</p>
     * <ul>
     * <li>Low experience (4): magnitude within valid range</li>
     * <li>Medium experience (80): magnitude calculation matches formula</li>
     * <li>High experience (200): magnitude capped at maxMagnitude</li>
     * </ul>
     */
    @Test
    void magnitudeTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        double usesModifier = 4;
        ItemEnchant itemEnchant = (ItemEnchant) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, usesModifier);
        assertTrue(itemEnchant.getMagnitude() >= itemEnchant.getMinMagnitude(), "magnitude < minMagnitude");
        assertTrue(itemEnchant.getMagnitude() <= itemEnchant.getMaxMagnitude(), "magnitude > maxMagnitude");

        usesModifier = 80;
        itemEnchant = (ItemEnchant) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, usesModifier);
        assertEquals((int) ((usesModifier / 10) * itemEnchant.getStrength()), itemEnchant.getMagnitude(), "unexpected magnitude");

        usesModifier = 200;
        itemEnchant = (ItemEnchant) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, usesModifier);
        assertTrue(itemEnchant.getMagnitude() >= itemEnchant.getMinMagnitude(), "magnitude < minMagnitude");
        assertTrue(itemEnchant.getMagnitude() <= itemEnchant.getMaxMagnitude(), "magnitude > maxMagnitude");
    }

    /**
     * Test item type validation and filtering.
     *
     * <p>Verifies the spell's item enchantment validation:</p>
     * <ul>
     * <li>Valid item types are enchantable</li>
     * <li>Wands cannot be enchanted (cannot stack enchantments)</li>
     * <li>Invalid item types (based on allow lists) are rejected</li>
     * </ul>
     */
    @Test
    void canBeEnchantedTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        ItemEnchant itemEnchant = (ItemEnchant) castSpell(caster, location, targetLocation);

        ItemStack itemStack = createValidItem(itemEnchant, 1);
        assertTrue(itemEnchant.canBeEnchanted(itemStack), "valid item stack cannot be enchanted");

        itemStack = Ollivanders2API.getItems().getWands().makeWand(O2WandWoodType.getRandomWood(), O2WandCoreType.getRandomCore(), 1);
        assertNotNull(itemStack);
        assertFalse(itemEnchant.canBeEnchanted(itemStack), "wand can be enchanted when it shouldn't be");

        itemStack = createInvalidItem();
        assertFalse(itemEnchant.canBeEnchanted(itemStack), "invalid item stack can be enchanted");
    }

    /**
     * Test spell-specific enchantment argument generation.
     *
     * <p>Concrete subclasses must implement this test to verify that enchantment arguments
     * are correctly generated and stored. Enchantment arguments are spell-specific data
     * (e.g., book content for CELATUM, destination coordinates for PORTUS) that are
     * associated with the enchanted item.</p>
     */
    @Test
    abstract void createEnchantmentArgsTest();

    /**
     * Test spell-specific item alteration effects.
     *
     * <p>Concrete subclasses must implement this test to verify that the spell applies
     * any custom visual or functional modifications to the enchanted item
     * (e.g., clearing pages for CELATUM). Most spells do not alter items and can
     * leave this method empty.</p>
     */
    @Test
    abstract void alterItemTest();

    /**
     * Test revert functionality (not applicable for item enchantments).
     *
     * <p>Item enchantments do not have revert actions. Once an item is enchanted,
     * the enchantment persists indefinitely unless explicitly removed.</p>
     */
    @Override
    @Test
    void revertTest() {
        // item enchantments don't have revert actions
    }
}
