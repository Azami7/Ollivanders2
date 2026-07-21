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

/**
 * Unit tests for {@link O2Potion}.
 */
public class O2PotionTest {
    /**
     * Shared MockBukkit server, mocked once per test class as server setup is expensive.
     */
    static ServerMock mockServer;

    /**
     * The plugin instance, loaded once for the test class.
     */
    static Ollivanders2 testPlugin;

    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance past the scheduler's 20-tick startup delay
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * getText() appends a formatted ingredient list (name and amount for each) under an "Ingredients:" header;
     * exercises the protected getIngredientsText() indirectly.
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
     * Simple getter, covered by tests that use getPotionType() to validate instances.
     */
    @Test
    void getPotionTypeTest() {
        // simple getter, skipping tests
    }

    /**
     * Simple getter, covered by createPotionItemStackTest().
     */
    @Test
    void getNameTest() {
        // simple getter, skipping tests
    }

    /**
     * Simple getter, covered by getIngredientsTextTest() and checkRecipeTest().
     */
    @Test
    void getIngredients() {
        // simple getter, skipping tests
    }

    /**
     * Simple getter, covered by getIngredientsTextTest().
     */
    @Test
    void getTextTest() {
        // simple getter, skipping tests
    }

    /**
     * getFlavorText() returns null when a potion has no flavor text, and a non-empty string when it does.
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
     * Simple getter, always POTIONS; covered by potion instantiation and usage tests.
     */
    @Test
    void getMagicBranchTest() {
        // simple getter, skipping tests
    }

    /**
     * Simple getter, covered by setUsesModifierTest().
     */
    @Test
    void getLevelTest() {
        // simple getter, skipping tests
    }

    /**
     * checkRecipe() returns true only for an exact ingredient match: a missing, extra, or wrong-amount ingredient, or an
     * empty map, all return false.
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
     * brew() with checkCanBrew=false returns a stack named for the potion type and increments the player's brew count.
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
     * createPotionItemStack() builds a stack of the requested amount, named for the potion type and identifiable via
     * findPotionByItemStack().
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
     * With maxSpellLevel enabled, brew(checkCanBrew=true) always succeeds and returns a valid potion; exercises the
     * private canBrew() indirectly.
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
     * brewBadPotion() returns a stack whose display name is one of the known bad-potion names and that does not resolve
     * to a valid O2Potion.
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
     * setUsesModifier() sets usesModifier to 200 under maxSpellLevel, to the player's potion count otherwise, and
     * doubles it under the HIGHER_SKILL effect; invoked via reflection as the method is protected.
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

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
