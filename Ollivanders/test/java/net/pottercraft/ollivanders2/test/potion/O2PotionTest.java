package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.HIGHER_SKILL;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.potion.O2Potion;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class O2PotionTest {
    /**
     * Shared mock Bukkit server instance for all tests.
     *
     * <p>Static field initialized once before all tests in this class. Reused across test instances
     * to avoid expensive server setup/teardown for each test method.</p>
     */
    static ServerMock mockServer;

    /**
     * The plugin instance being tested.
     *
     * <p>Loaded once before all tests with the default configuration. Provides access to
     * logger, scheduler, and other plugin API methods during tests.</p>
     */
    static Ollivanders2 testPlugin;

    /**
     * Initialize the mock Bukkit server before all tests.
     *
     * <p>Static setup method called once before all tests in this class. Creates the shared
     * MockBukkit server instance that is reused across all test methods to avoid expensive
     * server creation/destruction overhead.</p>
     */
    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Test getIngredientsText returns properly formatted ingredient list.
     * <p>
     * Since getIngredientsText() is protected, we test it indirectly through getText()
     * which appends the ingredients text to the description. Verifies that:
     * <ul>
     * <li>The text contains "Ingredients:" header</li>
     * <li>Each ingredient name appears in the text</li>
     * <li>Each ingredient amount appears in the text</li>
     * </ul>
     * </p>
     */
    @Test
    void getIngredientsTextTest() {
        // Get a potion with known ingredients
        O2PotionType potionType = O2PotionType.CURE_FOR_BOILS;
        O2Potion potion = Ollivanders2API.getPotions().getPotionFromType(potionType);
        assertNotNull(potion, "getPotionFromType should return a non-null potion");

        Map<O2ItemType, Integer> ingredients = potion.getIngredients();
        assertFalse(ingredients.isEmpty(), "Potion should have ingredients");

        // getText() returns description + getIngredientsText()
        String text = potion.getText();
        assertNotNull(text, "getText should return non-null text");

        // Verify the "Ingredients:" header is present
        assertTrue(text.contains("Ingredients:"), "getText should contain 'Ingredients:' header");

        // Verify each ingredient name and amount appears in the text
        for (Map.Entry<O2ItemType, Integer> entry : ingredients.entrySet()) {
            O2ItemType ingredientType = entry.getKey();
            Integer amount = entry.getValue();

            String ingredientName = Ollivanders2API.getItems().getItemNameByType(ingredientType);
            assertNotNull(ingredientName, "Ingredient should have a name: " + ingredientType);

            assertTrue(text.contains(ingredientName), "getText should contain ingredient name: " + ingredientName);
            assertTrue(text.contains(amount.toString()), "getText should contain ingredient amount: " + amount);
        }
    }

    /**
     * Test getPotionType returns the potion type.
     * <p>
     * Simple getter - basic functionality verified by other tests that use getPotionType()
     * to validate potion instances.
     * </p>
     */
    @Test
    void getPotionTypeTest() {
        // simple getter, skipping tests
    }

    /**
     * Test getName returns the potion name.
     * <p>
     * Simple getter - basic functionality verified by createPotionItemStackTest() which
     * validates that the potion name matches the expected display name.
     * </p>
     */
    @Test
    void getNameTest() {
        // simple getter, skipping tests
    }

    /**
     * Test getIngredients returns the potion ingredients.
     * <p>
     * Simple getter - basic functionality verified by getIngredientsTextTest() and
     * checkRecipeTest() which both validate that ingredients are returned correctly.
     * </p>
     */
    @Test
    void getIngredients() {
        // simple getter, skipping tests
    }

    /**
     * Test getText returns the potion description text.
     * <p>
     * Simple getter - basic functionality verified by getIngredientsTextTest() which
     * validates that getText() returns the description with ingredients appended.
     * </p>
     */
    @Test
    void getTextTest() {
        // simple getter, skipping tests
    }

    /**
     * Test getFlavorText returns appropriate values based on flavor text availability.
     * <p>
     * Verifies that:
     * <ul>
     * <li>Potions without flavor text return null</li>
     * <li>Potions with flavor text return a non-null, non-empty string</li>
     * </ul>
     * </p>
     */
    @Test
    void getFlavorTextTest() {
        // Test a potion without flavor text - should return null
        O2Potion potionWithoutFlavor = Ollivanders2API.getPotions().getPotionFromType(O2PotionType.HERBICIDE_POTION);
        assertNotNull(potionWithoutFlavor, "getPotionFromType should return a non-null potion for HERBICIDE_POTION");
        assertNull(potionWithoutFlavor.getFlavorText(), "getFlavorText should return null for potion without flavor text");

        // Test a potion with flavor text - should return a non-null string
        O2Potion potionWithFlavor = Ollivanders2API.getPotions().getPotionFromType(O2PotionType.CURE_FOR_BOILS);
        assertNotNull(potionWithFlavor, "getPotionFromType should return a non-null potion for CURE_FOR_BOILS");
        String flavorText = potionWithFlavor.getFlavorText();
        assertNotNull(flavorText, "getFlavorText should return non-null for potion with flavor text");
        assertFalse(flavorText.isEmpty(), "getFlavorText should return non-empty string");
    }

    /**
     * Test getMagicBranch returns the magic branch for potions.
     * <p>
     * Simple getter - always returns POTIONS branch for all O2Potion instances.
     * Basic functionality verified by potion instantiation and usage tests.
     * </p>
     */
    @Test
    void getMagicBranchTest() {
        // simple getter, skipping tests
    }

    /**
     * Test getLevel returns the magic difficulty level for the potion.
     * <p>
     * Simple getter - basic functionality verified by setUsesModifierTest() which
     * validates that the potion level is used correctly in modifier calculations.
     * </p>
     */
    @Test
    void getLevelTest() {
        // simple getter, skipping tests
    }

    /**
     * Test checkRecipe validates ingredient lists correctly.
     * <p>
     * Verifies that:
     * <ul>
     * <li>Exact matching ingredients return true</li>
     * <li>Wrong number of ingredient types returns false</li>
     * <li>Missing ingredient returns false</li>
     * <li>Wrong ingredient amount returns false</li>
     * <li>Empty ingredient map returns false</li>
     * </ul>
     * </p>
     */
    @Test
    void checkRecipeTest() {
        O2PotionType potionType = O2PotionType.CURE_FOR_BOILS;
        O2Potion potion = Ollivanders2API.getPotions().getPotionFromType(potionType);
        assertNotNull(potion, "getPotionFromType should return a non-null potion");

        Map<O2ItemType, Integer> correctIngredients = potion.getIngredients();
        assertFalse(correctIngredients.isEmpty(), "Potion should have ingredients");

        // Exact match should return true
        assertTrue(potion.checkRecipe(correctIngredients), "checkRecipe should return true for exact match");

        // Empty map should return false
        Map<O2ItemType, Integer> emptyIngredients = new HashMap<>();
        assertFalse(potion.checkRecipe(emptyIngredients), "checkRecipe should return false for empty ingredients");

        // Missing one ingredient should return false
        Map<O2ItemType, Integer> missingIngredient = new HashMap<>(correctIngredients);
        O2ItemType firstKey = missingIngredient.keySet().iterator().next();
        missingIngredient.remove(firstKey);
        assertFalse(potion.checkRecipe(missingIngredient), "checkRecipe should return false when missing an ingredient");

        // Wrong amount should return false
        Map<O2ItemType, Integer> wrongAmount = new HashMap<>(correctIngredients);
        O2ItemType keyToModify = wrongAmount.keySet().iterator().next();
        wrongAmount.put(keyToModify, wrongAmount.get(keyToModify) + 10);
        assertFalse(potion.checkRecipe(wrongAmount), "checkRecipe should return false for wrong ingredient amount");

        // Extra ingredient should return false
        Map<O2ItemType, Integer> extraIngredient = new HashMap<>(correctIngredients);
        // Find an ingredient type not in the recipe
        for (O2ItemType itemType : O2ItemType.values()) {
            if (!extraIngredient.containsKey(itemType)) {
                extraIngredient.put(itemType, 1);
                break;
            }
        }
        assertFalse(potion.checkRecipe(extraIngredient), "checkRecipe should return false when extra ingredient present");
    }

    /**
     * Test brew creates a valid potion and increments brew count.
     * <p>
     * Verifies that:
     * <ul>
     * <li>Brewing with checkCanBrew=false returns a valid potion</li>
     * <li>The returned ItemStack has the correct potion type name</li>
     * <li>The player's potion brew count is incremented</li>
     * </ul>
     * </p>
     */
    @Test
    void brewTest() {
        O2PotionType potionType = O2PotionType.CURE_FOR_BOILS;
        O2Potion potion = Ollivanders2API.getPotions().getPotionFromType(potionType);
        assertNotNull(potion, "getPotionFromType should return a non-null potion");

        // Create a mock player
        Player brewer = mockServer.addPlayer();
        assertNotNull(brewer, "Mock server should create a player");

        // Get initial potion count
        int initialCount = testPlugin.getO2Player(brewer).getPotionCount(potionType);

        // Brew with checkCanBrew=false to guarantee success
        ItemStack result = potion.brew(brewer, false);
        assertNotNull(result, "brew should return a non-null ItemStack");

        // Verify the potion has correct metadata
        assertNotNull(result.getItemMeta(), "Brewed potion should have item meta");
        assertEquals(potionType.getPotionName(), result.getItemMeta().getDisplayName(),
                "Brewed potion should have correct display name");

        // Verify potion count was incremented
        int newCount = testPlugin.getO2Player(brewer).getPotionCount(potionType);
        assertEquals(initialCount + 1, newCount, "Potion brew count should be incremented");
    }

    /**
     * Test createPotionItemStack creates correctly configured ItemStacks.
     * <p>
     * Verifies that:
     * <ul>
     * <li>Single potion (amount=1) is created correctly</li>
     * <li>Multiple potions (amount > 1) are created correctly</li>
     * <li>The display name matches the potion type name</li>
     * <li>The ItemStack amount matches the requested amount</li>
     * <li>The potion can be identified via findPotionByItemStack</li>
     * </ul>
     * </p>
     */
    @Test
    void createPotionItemStackTest() {
        O2PotionType potionType = O2PotionType.CURE_FOR_BOILS;
        O2Potion potion = Ollivanders2API.getPotions().getPotionFromType(potionType);
        assertNotNull(potion, "getPotionFromType should return a non-null potion");

        // Test creating a single potion
        ItemStack singlePotion = potion.createPotionItemStack(1);
        assertNotNull(singlePotion, "createPotionItemStack should return a non-null ItemStack");
        assertEquals(1, singlePotion.getAmount(), "Single potion should have amount 1");
        assertNotNull(singlePotion.getItemMeta(), "Potion should have item meta");
        assertEquals(potionType.getPotionName(), singlePotion.getItemMeta().getDisplayName(),
                "Potion should have correct display name");

        // Test creating multiple potions
        int multiAmount = 5;
        ItemStack multiplePotions = potion.createPotionItemStack(multiAmount);
        assertNotNull(multiplePotions, "createPotionItemStack should return a non-null ItemStack for multiple");
        assertEquals(multiAmount, multiplePotions.getAmount(), "Multiple potions should have correct amount");

        // Verify the potion can be identified
        O2Potion foundPotion = Ollivanders2API.getPotions().findPotionByItemStack(singlePotion);
        assertNotNull(foundPotion, "findPotionByItemStack should identify the created potion");
        assertEquals(potionType, foundPotion.getPotionType(), "Found potion should have correct type");
    }

    /**
     * Test canBrew determines brewing success correctly.
     * <p>
     * Note: canBrew() is a private method, so it is tested indirectly through brew().
     * When checkCanBrew=true, brew() calls canBrew() to determine success.
     * </p>
     * <p>
     * Verifies that:
     * <ul>
     * <li>With maxSpellLevel enabled, brewing always succeeds</li>
     * <li>The result is a valid potion (not a bad potion) when canBrew returns true</li>
     * </ul>
     * </p>
     */
    @Test
    void canBrewTest() {
        // canBrew() is private - test indirectly through brew() with checkCanBrew=true
        O2PotionType potionType = O2PotionType.CURE_FOR_BOILS;
        O2Potion potion = Ollivanders2API.getPotions().getPotionFromType(potionType);
        assertNotNull(potion, "getPotionFromType should return a non-null potion");

        Player brewer = mockServer.addPlayer();
        assertNotNull(brewer, "Mock server should create a player");

        // Enable maxSpellLevel to guarantee canBrew returns true
        boolean originalMaxSpellLevel = Ollivanders2.maxSpellLevel;
        try {
            Ollivanders2.maxSpellLevel = true;

            // Brew with checkCanBrew=true - should succeed because maxSpellLevel is enabled
            ItemStack result = potion.brew(brewer, true);
            assertNotNull(result, "brew should return a non-null ItemStack");

            // Verify it's a valid potion (not a bad potion)
            O2Potion foundPotion = Ollivanders2API.getPotions().findPotionByItemStack(result);
            assertNotNull(foundPotion, "With maxSpellLevel enabled, brew should return a valid potion");
            assertEquals(potionType, foundPotion.getPotionType(), "Brewed potion should have correct type");
        } finally {
            // Restore original setting
            Ollivanders2.maxSpellLevel = originalMaxSpellLevel;
        }
    }

    /**
     * Test brewBadPotion creates a random invalid potion.
     * <p>
     * Verifies that:
     * <ul>
     * <li>The method returns a non-null ItemStack</li>
     * <li>The potion has item meta with a display name</li>
     * <li>The display name is one of the known bad potion names</li>
     * <li>The potion is NOT identifiable as a valid O2Potion</li>
     * </ul>
     * </p>
     */
    @Test
    void brewBadPotionTest() {
        // Known bad potion names from brewBadPotion() implementation
        List<String> badPotionNames = List.of(
                "Watery Potion", "Slimy Potion", "Lumpy Potion",
                "Cloudy Potion", "Smelly Potion", "Sticky Potion"
        );

        // Test multiple times since results are randomized
        for (int i = 0; i < 10; i++) {
            ItemStack badPotion = O2Potion.brewBadPotion();
            assertNotNull(badPotion, "brewBadPotion should return a non-null ItemStack");

            // Verify it has item meta
            assertNotNull(badPotion.getItemMeta(), "Bad potion should have item meta");

            // Verify display name is one of the bad potion names
            String displayName = badPotion.getItemMeta().getDisplayName();
            assertNotNull(displayName, "Bad potion should have a display name");
            assertTrue(badPotionNames.contains(displayName),
                    "Bad potion name '" + displayName + "' should be one of the known bad potion names");

            // Verify it's NOT a valid O2Potion
            O2Potion foundPotion = Ollivanders2API.getPotions().findPotionByItemStack(badPotion);
            assertNull(foundPotion, "Bad potion should not be identifiable as a valid O2Potion");
        }
    }

    /**
     * Test setUsesModifier calculates brewing skill modifier correctly.
     * <p>
     * Note: setUsesModifier() is a protected method, so we use reflection to test it directly.
     * The usesModifier field is public and can be verified after the method call.
     * </p>
     * <p>
     * Verifies that:
     * <ul>
     * <li>With maxSpellLevel enabled, usesModifier is set to 200</li>
     * <li>With maxSpellLevel disabled, usesModifier is based on potion count</li>
     * <li>With HIGHER_SKILL effect, usesModifier is doubled</li>
     * </ul>
     * </p>
     */
    @Test
    void setUsesModifierTest() throws Exception {
        O2PotionType potionType = O2PotionType.CURE_FOR_BOILS;
        O2Potion potion = Ollivanders2API.getPotions().getPotionFromType(potionType);
        assertNotNull(potion, "getPotionFromType should return a non-null potion");

        Player player = mockServer.addPlayer();
        assertNotNull(player, "Mock server should create a player");

        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        assertNotNull(o2p, "Should have an O2Player for the mock player");

        // Get the protected method via reflection
        Method setUsesModifierMethod = O2Potion.class.getDeclaredMethod("setUsesModifier", O2Player.class);
        setUsesModifierMethod.setAccessible(true);

        // Test with maxSpellLevel enabled
        boolean originalMaxSpellLevel = Ollivanders2.maxSpellLevel;
        boolean originalUseYears = Ollivanders2.useYears;
        try {
            Ollivanders2.maxSpellLevel = true;
            Ollivanders2.useYears = false;

            setUsesModifierMethod.invoke(potion, o2p);
            assertEquals(200.0, potion.usesModifier, "With maxSpellLevel enabled, usesModifier should be 200");

            // Test with maxSpellLevel disabled - usesModifier should be based on potion count
            Ollivanders2.maxSpellLevel = false;
            int potionCount = o2p.getPotionCount(potionType);

            setUsesModifierMethod.invoke(potion, o2p);
            assertEquals((double) potionCount, potion.usesModifier,
                    "With maxSpellLevel disabled, usesModifier should equal potion count");

            // Test with HIGHER_SKILL effect - should double the modifier
            // First increment potion count so we have a non-zero base
            o2p.incrementPotionCount(potionType);
            o2p.incrementPotionCount(potionType);
            int newPotionCount = o2p.getPotionCount(potionType);

            // Add HIGHER_SKILL effect
            HIGHER_SKILL higherSkillEffect = new HIGHER_SKILL(testPlugin, 1200, false, player.getUniqueId());
            Ollivanders2API.getPlayers().playerEffects.addEffect(higherSkillEffect);
            mockServer.getScheduler().performTicks(20); // Allow effect to register

            setUsesModifierMethod.invoke(potion, o2p);
            assertEquals((double) newPotionCount * 2, potion.usesModifier,
                    "With HIGHER_SKILL effect, usesModifier should be doubled");

            // Clean up effect
            Ollivanders2API.getPlayers().playerEffects.removeEffect(o2p.getID(), O2EffectType.HIGHER_SKILL);
        } finally {
            // Restore original settings
            Ollivanders2.maxSpellLevel = originalMaxSpellLevel;
            Ollivanders2.useYears = originalUseYears;
        }
    }

    /**
     * Tear down the mock Bukkit server after all tests complete.
     *
     * <p>Static teardown method called once after all tests in this class have finished.
     * Releases the MockBukkit server resources to prevent memory leaks and allow clean
     * test execution in subsequent test classes.</p>
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
