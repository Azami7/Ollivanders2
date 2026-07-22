package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.listeners.OllivandersListener;
import net.pottercraft.ollivanders2.potion.O2Potion;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.potion.O2Potions;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
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
import java.util.HashMap;
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
     * checkRecipeAndBrew() creates the expected potion from matching cauldron ingredients (happy path).
     */
    @Test
    void checkRecipeAndBrewTest() {
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

        ItemStack brewedPotion = Ollivanders2API.getPotions().checkRecipeAndBrew(cauldron, player);
        assertNotNull(brewedPotion, "checkRecipeAndBrew should return a non-null potion for valid recipe");
        O2Potion potion = Ollivanders2API.getPotions().findPotionByItemStack(brewedPotion);
        assertNotNull(potion, "checkRecipeAndBrew result should be findable by itemstack");
        assertEquals(expectedPotionType, potion.getPotionType(), "checkRecipeAndBrew should create the expected potion type");
    }

    /**
     * checkRecipeAndBrew() returns null when the block is not a water cauldron.
     */
    @Test
    void checkRecipeAndBrewNotCauldronTest() {
        Location location = new Location(testWorld, 210, 4, 0);
        Block notCauldron = testWorld.getBlockAt(location);
        notCauldron.setType(Material.DIRT);

        PlayerMock player = mockServer.addPlayer();

        ItemStack result = Ollivanders2API.getPotions().checkRecipeAndBrew(notCauldron, player);
        assertNull(result, "checkRecipeAndBrew should return null for non-cauldron blocks");
    }

    /**
     * checkRecipeAndBrew() returns null for a cauldron with no ingredients.
     */
    @Test
    void checkRecipeAndBrewEmptyCauldronTest() {
        Block cauldron = createCauldronAt(220);
        PlayerMock player = mockServer.addPlayer();

        ItemStack result = Ollivanders2API.getPotions().checkRecipeAndBrew(cauldron, player);
        assertNull(result, "checkRecipeAndBrew should return null for empty cauldrons");
    }

    /**
     * checkRecipeAndBrew() returns a bad potion when the ingredients match no known recipe.
     */
    @Test
    void checkRecipeAndBrewUnknownRecipeTest() {
        Block cauldron = createCauldronAt(230);
        Location location = cauldron.getLocation();

        // Add random O2Items that don't form a valid recipe
        dropIngredientAt(location, O2ItemType.ACONITE, 1);
        dropIngredientAt(location, O2ItemType.BEZOAR, 1);

        PlayerMock player = mockServer.addPlayer();

        ItemStack result = Ollivanders2API.getPotions().checkRecipeAndBrew(cauldron, player);
        assertBadPotion(result, "unknown recipes");
    }

    /**
     * checkRecipeAndBrew() returns a bad potion when the right ingredients are present in the wrong amounts.
     */
    @Test
    void checkRecipeAndBrewWrongAmountsTest() {
        Block cauldron = createCauldronAt(240);
        Location location = cauldron.getLocation();
        O2PotionType potionType = O2PotionType.CURE_FOR_BOILS;

        // Get correct ingredients but use wrong amounts (always 1 instead of correct amount)
        Map<O2ItemType, Integer> correctIngredients = getIngredientsForPotion(potionType);
        for (O2ItemType ingredientType : correctIngredients.keySet()) {
            dropIngredientAt(location, ingredientType, 1);
        }

        PlayerMock player = mockServer.addPlayer();

        ItemStack result = Ollivanders2API.getPotions().checkRecipeAndBrew(cauldron, player);
        assertBadPotion(result, "wrong ingredient amounts");
    }

    /**
     * checkRecipeAndBrew() returns a bad potion when only some of the required ingredients are present.
     */
    @Test
    void checkRecipeAndBrewIncompleteIngredientsTest() {
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

        ItemStack result = Ollivanders2API.getPotions().checkRecipeAndBrew(cauldron, player);
        assertBadPotion(result, "incomplete ingredients");
    }

    /**
     * brewPotion() puts the brewed potion in the player's off-hand, consumes the glass bottle, and empties the
     * cauldron.
     */
    @Test
    void brewPotionTest() {
        Block cauldron = createHotCauldronAt(260);
        O2PotionType expectedPotionType = O2PotionType.COMMON_ANTIDOTE_POTION;
        addIngredientsFor(cauldron, expectedPotionType);

        PlayerMock player = mockServer.addPlayer();
        TestCommon.setPlayerPotionExperience(testPlugin, player, expectedPotionType, 100);
        player.getInventory().setItemInOffHand(new ItemStack(Material.GLASS_BOTTLE, 1));

        Ollivanders2API.getPotions().brewPotion(player, cauldron);

        ItemStack offHand = player.getInventory().getItemInOffHand();
        O2Potion potion = Ollivanders2API.getPotions().findPotionByItemStack(offHand);
        assertNotNull(potion, "brewPotion should put a valid potion in the player's off-hand");
        assertEquals(expectedPotionType, potion.getPotionType(), "brewPotion should brew the expected potion type");

        assertTrue(Ollivanders2API.getPotions().getIngredientsInCauldron(cauldron).isEmpty(),
                "brewPotion should remove the ingredients from the cauldron");
        assertNull(TestCommon.getPlayerInventoryItem(player, Material.GLASS_BOTTLE),
                "brewPotion should consume the player's only glass bottle");
    }

    /**
     * brewPotion() consumes exactly one glass bottle, returning the rest of the stack to the player's inventory.
     */
    @Test
    void brewPotionExtraBottlesTest() {
        int bottleCount = 3;
        Block cauldron = createHotCauldronAt(270);
        O2PotionType expectedPotionType = O2PotionType.COMMON_ANTIDOTE_POTION;
        addIngredientsFor(cauldron, expectedPotionType);

        PlayerMock player = mockServer.addPlayer();
        TestCommon.setPlayerPotionExperience(testPlugin, player, expectedPotionType, 100);
        player.getInventory().setItemInOffHand(new ItemStack(Material.GLASS_BOTTLE, bottleCount));

        Ollivanders2API.getPotions().brewPotion(player, cauldron);

        assertNotNull(Ollivanders2API.getPotions().findPotionByItemStack(player.getInventory().getItemInOffHand()),
                "brewPotion should put a valid potion in the player's off-hand");

        ItemStack leftoverBottles = TestCommon.getPlayerInventoryItem(player, Material.GLASS_BOTTLE);
        assertNotNull(leftoverBottles, "brewPotion should return the unused glass bottles to the player");
        assertEquals(bottleCount - 1, leftoverBottles.getAmount(), "brewPotion should consume exactly one glass bottle");
    }

    /**
     * brewPotion() leaves the player and cauldron untouched when the player has no glass bottle to brew into.
     */
    @Test
    void brewPotionNoGlassBottleTest() {
        Block cauldron = createHotCauldronAt(280);
        O2PotionType potionType = O2PotionType.COMMON_ANTIDOTE_POTION;
        addIngredientsFor(cauldron, potionType);

        PlayerMock player = mockServer.addPlayer();
        TestCommon.setPlayerPotionExperience(testPlugin, player, potionType, 100);
        player.getInventory().setItemInOffHand(new ItemStack(Material.STICK, 1));

        Ollivanders2API.getPotions().brewPotion(player, cauldron);

        assertEquals(Material.STICK, player.getInventory().getItemInOffHand().getType(),
                "brewPotion should not replace an off-hand item that is not a glass bottle");
        assertFalse(Ollivanders2API.getPotions().getIngredientsInCauldron(cauldron).isEmpty(),
                "brewPotion should leave the ingredients in the cauldron when the player has no glass bottle");
    }

    /**
     * brewPotion() leaves the player and cauldron untouched when the cauldron has no heat source under it.
     */
    @Test
    void brewPotionColdCauldronTest() {
        Block cauldron = createCauldronAt(290);
        // air is not a heat source, so this cauldron can never brew
        cauldron.getRelative(BlockFace.DOWN).setType(Material.AIR);

        O2PotionType potionType = O2PotionType.COMMON_ANTIDOTE_POTION;
        addIngredientsFor(cauldron, potionType);

        PlayerMock player = mockServer.addPlayer();
        TestCommon.setPlayerPotionExperience(testPlugin, player, potionType, 100);
        player.getInventory().setItemInOffHand(new ItemStack(Material.GLASS_BOTTLE, 1));

        Ollivanders2API.getPotions().brewPotion(player, cauldron);

        assertEquals(Material.GLASS_BOTTLE, player.getInventory().getItemInOffHand().getType(),
                "brewPotion should not consume the glass bottle when the cauldron is cold");
        assertFalse(Ollivanders2API.getPotions().getIngredientsInCauldron(cauldron).isEmpty(),
                "brewPotion should leave the ingredients in the cauldron when it is cold");
    }

    /**
     * brewPotion() tells the player nothing happened and keeps their glass bottle when the cauldron is empty.
     */
    @Test
    void brewPotionEmptyCauldronTest() {
        Block cauldron = createHotCauldronAt(300);

        PlayerMock player = mockServer.addPlayer();
        player.getInventory().setItemInOffHand(new ItemStack(Material.GLASS_BOTTLE, 1));
        TestCommon.clearMessageQueue(player);

        Ollivanders2API.getPotions().brewPotion(player, cauldron);

        assertEquals(Material.GLASS_BOTTLE, player.getInventory().getItemInOffHand().getType(),
                "brewPotion should not consume the glass bottle when there is nothing to brew");

        String message = TestCommon.getWholeMessage(player);
        assertNotNull(message, "brewPotion should tell the player the cauldron is unchanged");
        assertTrue(TestCommon.messageStartsWith("The cauldron appears unchanged", message),
                "brewPotion should tell the player the cauldron is unchanged, got: " + message);
    }

    /**
     * brewPotion() still consumes the ingredients and the glass bottle when the ingredients match no known recipe,
     * handing the player a bad potion.
     */
    @Test
    void brewPotionUnknownRecipeTest() {
        Block cauldron = createHotCauldronAt(310);
        Location location = cauldron.getLocation();

        // ingredients that form no valid recipe
        dropIngredientAt(location, O2ItemType.ACONITE, 1);
        dropIngredientAt(location, O2ItemType.BEZOAR, 1);

        PlayerMock player = mockServer.addPlayer();
        player.getInventory().setItemInOffHand(new ItemStack(Material.GLASS_BOTTLE, 1));

        Ollivanders2API.getPotions().brewPotion(player, cauldron);

        assertBadPotion(player.getInventory().getItemInOffHand(), "unknown recipes");
        assertTrue(Ollivanders2API.getPotions().getIngredientsInCauldron(cauldron).isEmpty(),
                "brewPotion should remove the ingredients from the cauldron even for an unknown recipe");
    }

    /**
     * Drop every ingredient a potion recipe requires, in the required amounts, at a cauldron.
     *
     * @param cauldron   the cauldron to drop the ingredients at
     * @param potionType the potion whose recipe should be satisfied
     */
    void addIngredientsFor(@NotNull Block cauldron, @NotNull O2PotionType potionType) {
        Location location = cauldron.getLocation();
        Map<O2ItemType, Integer> ingredients = getIngredientsForPotion(potionType);

        for (O2ItemType ingredientType : ingredients.keySet()) {
            Integer amount = ingredients.get(ingredientType);
            assertNotNull(amount, "Ingredient amount should not be null");
            dropIngredientAt(location, ingredientType, amount);
        }
    }

    /**
     * Create a water cauldron at (x, 4, 0) standing on a heat source, as brewing requires.
     *
     * @param x the x-coordinate for the cauldron location
     * @return the block set as a water cauldron
     */
    Block createHotCauldronAt(int x) {
        Block cauldron = createCauldronAt(x);

        // take the heat source from the plugin's own list so this test follows any change to it
        List<Material> hotBlocks = Ollivanders2Common.getHotBlocks();
        assertFalse(hotBlocks.isEmpty(), "Ollivanders2Common should define at least one hot block");
        cauldron.getRelative(BlockFace.DOWN).setType(hotBlocks.get(0));

        return cauldron;
    }

    /**
     * onPlayerDrink() applies an O2 potion's effect when the player drinks it, and ignores a plain vanilla potion.
     */
    @Test
    void onPlayerDrinkTest() {
        World testWorld = mockServer.addSimpleWorld("onPlayerDrinkWorld");

        // drinking a tagged O2 potion applies its effect (BABBLING_BEVERAGE grants the BABBLING effect)
        PlayerMock drinker = mockServer.addPlayer();
        drinker.setLocation(new Location(testWorld, 400, 4, 0));
        testPlugin.getO2Player(drinker);

        ItemStack potion = Ollivanders2API.getPotions().getPotionItemStackByType(O2PotionType.BABBLING_BEVERAGE, 1);
        assertNotNull(potion, "should be able to build a BABBLING_BEVERAGE item");

        mockServer.getPluginManager().callEvent(new PlayerItemConsumeEvent(drinker, potion, EquipmentSlot.HAND));
        // the effect is applied on a delayed task; advance past the delay plus effect activation
        mockServer.getScheduler().performTicks(OllivandersListener.getThreadDelay() + 20);

        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(drinker.getUniqueId(), O2EffectType.BABBLING),
                "onPlayerDrink should apply the potion's effect to the drinker");

        // drinking a plain vanilla potion does nothing and does not error
        PlayerMock muggle = mockServer.addPlayer();
        muggle.setLocation(new Location(testWorld, 410, 4, 0));
        testPlugin.getO2Player(muggle);

        ItemStack plainPotion = new ItemStack(Material.POTION, 1);
        PlayerItemConsumeEvent plainEvent = new PlayerItemConsumeEvent(muggle, plainPotion, EquipmentSlot.HAND);
        mockServer.getPluginManager().callEvent(plainEvent);
        mockServer.getScheduler().performTicks(OllivandersListener.getThreadDelay() + 20);

        assertTrue(Ollivanders2API.getPlayers().playerEffects.getEffects(muggle.getUniqueId()).isEmpty(),
                "onPlayerDrink should do nothing for a plain vanilla potion");
    }

    /**
     * onSplashPotion() dispatches an O2SplashPotion's impact effect synchronously, so an effect that adjusts the
     * splash (HERBICIDE reduces per-entity intensity) still takes hold. Guards against re-deferring the handler, which
     * would make {@link PotionSplashEvent#setIntensity} a no-op.
     */
    @Test
    void onSplashPotionTest() {
        World testWorld = mockServer.addSimpleWorld("onSplashPotionWorld");
        Location location = new Location(testWorld, 420, 4, 0);

        ItemStack herbicide = Ollivanders2API.getPotions().getPotionItemStackByType(O2PotionType.HERBICIDE_POTION, 1);
        assertNotNull(herbicide, "should be able to build a HERBICIDE_POTION item");

        ThrownPotion thrown = testWorld.spawn(location, ThrownPotion.class);
        thrown.setItem(herbicide);

        // a non-creeper entity in the splash should have its intensity reduced by the impact effect
        PlayerMock affected = mockServer.addPlayer();
        affected.setLocation(location);
        double fullIntensity = 1.0;
        HashMap<LivingEntity, Double> affectedEntities = new HashMap<>();
        affectedEntities.put(affected, fullIntensity);

        PotionSplashEvent event = new PotionSplashEvent(thrown, affectedEntities);
        mockServer.getPluginManager().callEvent(event);

        // no performTicks: the reduction must happen during event dispatch, not on a later tick
        assertTrue(event.getIntensity(affected) < fullIntensity,
                "onSplashPotion must reduce HERBICIDE intensity synchronously during the event");
    }

    /**
     * onPotionBrewing() does nothing when the player is not sneaking.
     *
     * @implNote The sneaking-and-facing-a-cauldron path cannot be tested: it goes through
     * {@code playerFacingBlockType}, which calls the MockBukkit-unimplemented {@code Player.getLineOfSight}.
     */
    @Test
    void onPotionBrewingTest() {
        World testWorld = mockServer.addSimpleWorld("onPotionBrewingWorld");
        PlayerMock player = mockServer.addPlayer();
        player.setLocation(new Location(testWorld, 430, 4, 0));
        player.getInventory().setItemInOffHand(new ItemStack(Material.GLASS_BOTTLE, 1));

        // not sneaking: the handler returns before touching the off-hand item
        mockServer.getPluginManager().callEvent(new PlayerToggleSneakEvent(player, false));

        assertEquals(Material.GLASS_BOTTLE, player.getInventory().getItemInOffHand().getType(),
                "onPotionBrewing should not consume the off-hand item when the player is not sneaking");
    }

    /**
     * addIngredientToCauldron() drops the player's off-hand item into the cauldron, clears the off-hand, and messages
     * the player; it no-ops on an empty off-hand and falls back to the material name for an un-renamed item.
     */
    @Test
    void addIngredientToCauldronTest() {
        O2Potions potions = Ollivanders2API.getPotions();
        Block cauldron = createCauldronAt(330);

        PlayerMock player = mockServer.addPlayer();
        player.setLocation(new Location(testWorld, 330, 4, 5));

        // empty off-hand: nothing happens
        TestCommon.clearMessageQueue(player);
        potions.addIngredientToCauldron(player, cauldron);
        assertNull(TestCommon.getWholeMessage(player), "adding with an empty off-hand should message nothing");
        assertTrue(potions.getIngredientsInCauldron(cauldron).isEmpty(),
                "an empty off-hand should add nothing to the cauldron");

        // an O2 ingredient is dropped into the cauldron, the off-hand cleared, and the player messaged
        player.getInventory().setItemInOffHand(O2ItemType.ACONITE.getItem(1));
        TestCommon.clearMessageQueue(player);
        potions.addIngredientToCauldron(player, cauldron);

        assertEquals(Material.AIR, player.getInventory().getItemInOffHand().getType(),
                "off-hand should be cleared after adding the ingredient");
        String message = TestCommon.getWholeMessage(player);
        assertNotNull(message, "player should be told the ingredient was added");
        assertTrue(TestCommon.messageStartsWith("Added", message), "message should confirm the ingredient was added");
        assertTrue(potions.getIngredientsInCauldron(cauldron).containsKey(O2ItemType.ACONITE),
                "the ingredient should be in the cauldron after adding");

        // an un-renamed item falls back to its material name in the message
        Block cauldron2 = createCauldronAt(340);
        PlayerMock plainPlayer = mockServer.addPlayer();
        plainPlayer.setLocation(new Location(testWorld, 340, 4, 5));
        plainPlayer.getInventory().setItemInOffHand(new ItemStack(Material.STICK, 1));
        TestCommon.clearMessageQueue(plainPlayer);
        potions.addIngredientToCauldron(plainPlayer, cauldron2);

        assertEquals(Material.AIR, plainPlayer.getInventory().getItemInOffHand().getType(),
                "off-hand should be cleared after adding a plain item");
        String plainMessage = TestCommon.getWholeMessage(plainPlayer);
        assertNotNull(plainMessage, "player should be messaged for a plain item");
        assertTrue(plainMessage.contains(Material.STICK.name()),
                "message should fall back to the material name for an un-renamed item");
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
     * @param result   the ItemStack result from checkRecipeAndBrew; a null result fails the assertion
     * @param scenario description of the test scenario, used in assertion messages
     */
    void assertBadPotion(@Nullable ItemStack result, @NotNull String scenario) {
        assertNotNull(result, "checkRecipeAndBrew should return a bad potion for " + scenario);
        O2Potion potion = Ollivanders2API.getPotions().findPotionByItemStack(result);
        assertNull(potion, "checkRecipeAndBrew should return a bad potion (not a valid O2Potion) for " + scenario);
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
        // happy path use cases covered by checkRecipeAndBrewTest()
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
