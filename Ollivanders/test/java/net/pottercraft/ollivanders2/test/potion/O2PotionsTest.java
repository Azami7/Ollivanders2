package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.potion.O2Potion;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.potion.O2Potions;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.potion.O2Potions}, the potion manager.
 */
public class O2PotionsTest {
    /**
     * Shared MockBukkit server, mocked once per test class as server setup is expensive.
     */
    static ServerMock mockServer;

    /**
     * The plugin instance, loaded once for the test class.
     */
    static Ollivanders2 testPlugin;

    /**
     * The test world used for all potion tests.
     */
    static World testWorld;

    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        testWorld = mockServer.addSimpleWorld("world");

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * getAllPotionTypes() is a simple getter, verified by the potion instantiation tests.
     */
    @Test
    void getAllPotionTypesTest() {
        // simple getter, verified by potion instantiation tests
    }

    /**
     * getAllPotions() returns one valid, non-duplicate O2Potion instance per loaded potion type.
     */
    @Test
    void getAllPotionsTest() {
        Collection<O2Potion> allPotions = Ollivanders2API.getPotions().getAllPotions();
        assertNotNull(allPotions, "getAllPotions should return a non-null collection");
        assertFalse(allPotions.isEmpty(), "getAllPotions should return a non-empty collection of loaded potions");

        // Verify all elements in the collection are valid O2Potion instances
        for (O2Potion potion : allPotions) {
            assertNotNull(potion, "getAllPotions should not contain null elements");
            assertNotNull(potion.getPotionType(), "Each potion should have a valid potion type");
        }

        // Verify the count matches the expected number of loaded potions
        // In test mode, all potions are loaded (LibsDisguises is considered enabled in test mode)
        assertEquals(O2PotionType.values().length, allPotions.size(), "getAllPotions should return all loaded potion types");

        // Verify no duplicate potions (check by potion type)
        Set<O2PotionType> potionTypes = new HashSet<>();
        for (O2Potion potion : allPotions) {
            O2PotionType potionType = potion.getPotionType();
            assertTrue(potionTypes.add(potionType), "getAllPotions should not contain duplicate potion types: " + potionType);
        }
    }

    /**
     * getAllPotionNames() returns one non-empty display name per potion type.
     */
    @Test
    void getAllPotionNamesTest() {
        List<String> allPotionNames = Ollivanders2API.getPotions().getAllPotionNames();
        assertNotNull(allPotionNames, "getAllPotionNames should return a non-null list");
        assertFalse(allPotionNames.isEmpty(), "getAllPotionNames should return a non-empty list of potion names");

        // Verify the count matches the expected number of potion types
        assertEquals(O2PotionType.values().length, allPotionNames.size(), "getAllPotionNames should return all potion type display names");

        // Verify no null or empty names
        for (String potionName : allPotionNames) {
            assertNotNull(potionName, "getAllPotionNames should not contain null names");
            assertFalse(potionName.isEmpty(), "getAllPotionNames should not contain empty names");
        }

        // Verify each potion type name is present in the list
        for (O2PotionType potionType : O2PotionType.values()) {
            assertTrue(allPotionNames.contains(potionType.getPotionName()), "getAllPotionNames should contain display name for " + potionType);
        }
    }

    /**
     * brewPotion() creates the expected potion from matching cauldron ingredients (happy path).
     */
    @Test
    void brewPotionTest() {
        Block cauldron = createCauldronAt(200);
        Location location = cauldron.getLocation();
        O2PotionType expectedPotionType = O2PotionType.COMMON_ANTIDOTE_POTION;

        // put ingredients at cauldron
        Map<O2ItemType, Integer> ingredients = getIngredientsForPotion(expectedPotionType);
        for (O2ItemType ingredientType : ingredients.keySet()) {
            Integer amount = ingredients.get(ingredientType);
            assertNotNull(amount, "Ingredient amount should not be null");
            dropIngredientAt(location, ingredientType, amount);
        }

        PlayerMock player = mockServer.addPlayer();
        TestCommon.setPlayerPotionExperience(testPlugin, player, expectedPotionType, 100);

        ItemStack brewedPotion = Ollivanders2API.getPotions().brewPotion(cauldron, player);
        assertNotNull(brewedPotion, "brewPotion should return a non-null potion for valid recipe");
        O2Potion potion = Ollivanders2API.getPotions().findPotionByItemStack(brewedPotion);
        assertNotNull(potion, "brewPotion result should be findable by itemstack");
        assertEquals(expectedPotionType, potion.getPotionType(), "brewPotion should create the expected potion type");
    }

    /**
     * brewPotion() returns null when the block is not a water cauldron.
     */
    @Test
    void brewPotionNotCauldronTest() {
        Location location = new Location(testWorld, 210, 4, 0);
        Block notCauldron = testWorld.getBlockAt(location);
        notCauldron.setType(Material.DIRT);

        PlayerMock player = mockServer.addPlayer();

        ItemStack result = Ollivanders2API.getPotions().brewPotion(notCauldron, player);
        assertNull(result, "brewPotion should return null for non-cauldron blocks");
    }

    /**
     * brewPotion() returns null for a cauldron with no ingredients.
     */
    @Test
    void brewPotionEmptyCauldronTest() {
        Block cauldron = createCauldronAt(220);
        PlayerMock player = mockServer.addPlayer();

        ItemStack result = Ollivanders2API.getPotions().brewPotion(cauldron, player);
        assertNull(result, "brewPotion should return null for empty cauldrons");
    }

    /**
     * brewPotion() returns a bad potion when the ingredients match no known recipe.
     */
    @Test
    void brewPotionUnknownRecipeTest() {
        Block cauldron = createCauldronAt(230);
        Location location = cauldron.getLocation();

        // Add random O2Items that don't form a valid recipe
        dropIngredientAt(location, O2ItemType.ACONITE, 1);
        dropIngredientAt(location, O2ItemType.BEZOAR, 1);

        PlayerMock player = mockServer.addPlayer();

        ItemStack result = Ollivanders2API.getPotions().brewPotion(cauldron, player);
        assertBadPotion(result, "unknown recipes");
    }

    /**
     * brewPotion() returns a bad potion when the right ingredients are present in the wrong amounts.
     */
    @Test
    void brewPotionWrongAmountsTest() {
        Block cauldron = createCauldronAt(240);
        Location location = cauldron.getLocation();
        O2PotionType potionType = O2PotionType.CURE_FOR_BOILS;

        // Get correct ingredients but use wrong amounts (always 1 instead of correct amount)
        Map<O2ItemType, Integer> correctIngredients = getIngredientsForPotion(potionType);
        for (O2ItemType ingredientType : correctIngredients.keySet()) {
            dropIngredientAt(location, ingredientType, 1);
        }

        PlayerMock player = mockServer.addPlayer();

        ItemStack result = Ollivanders2API.getPotions().brewPotion(cauldron, player);
        assertBadPotion(result, "wrong ingredient amounts");
    }

    /**
     * brewPotion() returns a bad potion when only some of the required ingredients are present.
     */
    @Test
    void brewPotionIncompleteIngredientsTest() {
        Block cauldron = createCauldronAt(250);
        Location location = cauldron.getLocation();
        O2PotionType potionType = O2PotionType.SLEEPING_DRAUGHT;

        // Get correct ingredients but only add the first one
        Map<O2ItemType, Integer> correctIngredients = getIngredientsForPotion(potionType);
        O2ItemType firstIngredient = (O2ItemType) correctIngredients.keySet().toArray()[0];
        Integer amount = correctIngredients.get(firstIngredient);
        assertNotNull(amount, "Ingredient amount should not be null");

        dropIngredientAt(location, firstIngredient, amount);

        PlayerMock player = mockServer.addPlayer();

        ItemStack result = Ollivanders2API.getPotions().brewPotion(cauldron, player);
        assertBadPotion(result, "incomplete ingredients");
    }

    /**
     * Get the ingredients required for brewing a specific potion type.
     *
     * @param potionType the potion type to get ingredients for
     * @return a map of ingredient types to required amounts
     */
    Map<O2ItemType, Integer> getIngredientsForPotion(@NotNull O2PotionType potionType) {
        O2Potion potion = Ollivanders2API.getPotions().getPotionFromType(potionType);
        assertNotNull(potion);

        Map<O2ItemType, Integer> ingredients = potion.getIngredients();
        assertFalse(ingredients.isEmpty());

        return ingredients;
    }

    /**
     * Create a water cauldron at (x, 4, 0) in the test world.
     *
     * @param x the x-coordinate for the cauldron location
     * @return the block set as a water cauldron
     */
    Block createCauldronAt(int x) {
        Location location = new Location(testWorld, x, 4, 0);
        Block cauldron = testWorld.getBlockAt(location);
        cauldron.setType(Material.WATER_CAULDRON);
        return cauldron;
    }

    /**
     * Drop an ingredient item at the given location.
     *
     * @param location       the location to drop the ingredient
     * @param ingredientType the type of ingredient to drop
     * @param amount         the amount of the ingredient
     */
    void dropIngredientAt(@NotNull Location location, @NotNull O2ItemType ingredientType, int amount) {
        ItemStack ingredient = ingredientType.getItem(amount);
        assertNotNull(ingredient, "Failed to create ingredient: " + ingredientType);
        testWorld.dropItem(location, ingredient);
    }

    /**
     * Assert the brewed result is a bad potion: non-null but not resolvable to a valid O2Potion.
     *
     * @param result   the ItemStack result from brewPotion; a null result fails the assertion
     * @param scenario description of the test scenario, used in assertion messages
     */
    void assertBadPotion(@Nullable ItemStack result, @NotNull String scenario) {
        assertNotNull(result, "brewPotion should return a bad potion for " + scenario);
        O2Potion potion = Ollivanders2API.getPotions().findPotionByItemStack(result);
        assertNull(potion, "brewPotion should return a bad potion (not a valid O2Potion) for " + scenario);
    }

    /**
     * findPotionByIngredients() matches only exact ingredient-and-amount sets, returning null for wrong amounts,
     * missing ingredients, or an empty map.
     */
    @Test
    void findPotionByIngredientsTest() {
        O2PotionType expectedPotionType = O2PotionType.BABBLING_BEVERAGE;

        // right ingredients and amounts
        Map<O2ItemType, Integer> ingredients = getIngredientsForPotion(expectedPotionType);
        O2Potion potion = Ollivanders2API.getPotions().findPotionByIngredients(ingredients);
        assertNotNull(potion, "findPotionByIngredients should find potion with correct ingredients and amounts");
        assertEquals(expectedPotionType, potion.getPotionType(), "findPotionByIngredients should return correct potion type");

        // right ingredients, wrong amounts
        ingredients.replaceAll((ingredient, amount) -> 1);
        potion = Ollivanders2API.getPotions().findPotionByIngredients(ingredients);
        assertNull(potion, "findPotionByIngredients should return null for wrong ingredient amounts");

        // incorrect ingredients
        O2ItemType ingredient = (O2ItemType)ingredients.keySet().toArray()[0];
        ingredients.remove(ingredient);
        potion = Ollivanders2API.getPotions().findPotionByIngredients(ingredients);
        assertNull(potion, "findPotionByIngredients should return null for missing ingredients");

        // empty ingredients
        ingredients.clear();
        potion = Ollivanders2API.getPotions().findPotionByIngredients(ingredients);
        assertNull(potion, "findPotionByIngredients should return null for empty ingredient map");
    }

    /**
     * getIngredientsInCauldron() detects only O2Items dropped in the cauldron, ignoring non-O2Items and aggregating
     * distinct ingredient types.
     */
    @Test
    void getIngredientsInCauldronTest() {
        Block cauldron = createCauldronAt(100);
        Location location = cauldron.getLocation();

        // empty cauldron
        Map<O2ItemType, Integer> ingredients = Ollivanders2API.getPotions().getIngredientsInCauldron(cauldron);
        assertTrue(ingredients.isEmpty(), "getIngredientsInCauldron should return empty map for empty cauldron");

        // items that are not O2Items
        testWorld.dropItem(location, new ItemStack(Material.VINE, 1));
        ingredients = Ollivanders2API.getPotions().getIngredientsInCauldron(cauldron);
        assertTrue(ingredients.isEmpty(), "getIngredientsInCauldron should ignore non-O2Items");

        // items that are O2Items
        dropIngredientAt(location, O2ItemType.VALERIAN_SPRIGS, 1);
        ingredients = Ollivanders2API.getPotions().getIngredientsInCauldron(cauldron);
        assertFalse(ingredients.isEmpty(), "getIngredientsInCauldron should find O2Items");
        assertEquals(1, ingredients.size(), "getIngredientsInCauldron should find exactly one ingredient type");

        dropIngredientAt(location, O2ItemType.DITTANY, 1);
        ingredients = Ollivanders2API.getPotions().getIngredientsInCauldron(cauldron);
        assertEquals(2, ingredients.size(), "getIngredientsInCauldron should find two different ingredient types");
    }

    /**
     * findPotionByItemMeta() resolves the O2Potion from a tagged ItemStack's meta, and returns null for empty meta or
     * standard Minecraft potion meta.
     */
    @Test
    void findPotionByItemMetaTest() {
        // happy path use cases covered by brewPotionTest()
        ItemStack itemStack = new ItemStack(Material.POTION, 1);

        // empty meta
        ItemMeta itemMeta = itemStack.getItemMeta();
        assertNotNull(itemMeta, "ItemStack should have ItemMeta");
        assertNull(Ollivanders2API.getPotions().findPotionByItemMeta(itemMeta), "Empty ItemMeta should return null");

        // standard MC potion meta
        PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
        potionMeta.setBasePotionType(PotionType.HEALING);
        assertNull(Ollivanders2API.getPotions().findPotionByItemMeta(potionMeta), "Standard MC potion meta should return null");

        // O2Potion
        O2PotionType expectedPotionType = O2PotionType.HERBICIDE_POTION;
        ItemStack potionItemStack = Ollivanders2API.getPotions().getPotionItemStackByType(expectedPotionType, 2);
        assertNotNull(potionItemStack, "getPotionItemStackByType should return a non-null ItemStack");
        itemMeta = potionItemStack.getItemMeta();
        assertNotNull(itemMeta, "O2Potion ItemStack should have ItemMeta");
        O2Potion potion = Ollivanders2API.getPotions().findPotionByItemMeta(itemMeta);
        assertNotNull(potion, "findPotionByItemMeta should find O2Potion from ItemMeta");
        assertEquals(expectedPotionType, potion.getPotionType(), "findPotionByItemMeta should return correct potion type");
    }

    /**
     * getPotionFromType() instantiates each potion type via reflection, returning an instance with the matching type.
     */
    @Test
    void getPotionFromTypeTest() {
        // Test a specific potion type
        O2PotionType expectedType = O2PotionType.SLEEPING_DRAUGHT;
        O2Potion potion = Ollivanders2API.getPotions().getPotionFromType(expectedType);
        assertNotNull(potion, "getPotionFromType should return a non-null potion for valid type");
        assertEquals(expectedType, potion.getPotionType(), "getPotionFromType should return potion with correct type");

        // Verify all potion types can be instantiated via reflection
        for (O2PotionType potionType : O2PotionType.values()) {
            O2Potion instantiatedPotion = Ollivanders2API.getPotions().getPotionFromType(potionType);
            assertNotNull(instantiatedPotion, "getPotionFromType should instantiate " + potionType);
            assertEquals(potionType, instantiatedPotion.getPotionType(), "getPotionFromType returned wrong type for " + potionType);
        }
    }

    /**
     * getAllIngredientNames() returns one non-empty, unique display name per ingredient.
     */
    @Test
    void getAllIngredientNamesTest() {
        List<String> allIngredientNames = O2Potions.getAllIngredientNames();
        assertNotNull(allIngredientNames, "getAllIngredientNames should return a non-null list");
        assertFalse(allIngredientNames.isEmpty(), "getAllIngredientNames should return a non-empty list");

        // Verify the count matches the ingredients list
        assertEquals(O2Potions.ingredients.size(), allIngredientNames.size(), "getAllIngredientNames should return names for all ingredients");

        // Verify no null or empty names
        for (String ingredientName : allIngredientNames) {
            assertNotNull(ingredientName, "getAllIngredientNames should not contain null names");
            assertFalse(ingredientName.isEmpty(), "getAllIngredientNames should not contain empty names");
        }

        // Verify all names are unique (no duplicates)
        Set<String> uniqueNames = new HashSet<>(allIngredientNames);
        assertEquals(allIngredientNames.size(), uniqueNames.size(), "getAllIngredientNames should not contain duplicate names");
    }

    /**
     * isLoaded() reports true for a potion type in the cache. In test mode all potions are loaded, so this exercises
     * only the cache lookup.
     */
    @Test
    void isLoadedTest() {
        // In test mode, all potions are loaded, so we verify that a potion is found in the cache
        assertTrue(Ollivanders2API.getPotions().isLoaded(O2PotionType.HERBICIDE_POTION), "isLoaded should return true for potions in the cache");
    }

    /**
     * getPotionEffectMagicLevel() is a simple getter, verified by the FINITE_INCANTATEM spell tests.
     */
    @Test
    void getPotionEffectMagicLevelTest() {
        // simple getter, verified by FINITE_INCANTATEM spell tests
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
