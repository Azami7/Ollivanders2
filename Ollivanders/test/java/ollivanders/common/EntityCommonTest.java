package ollivanders.common;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for EntityCommon class.
 *
 * Tests entity search methods, item search methods, random entity type generation,
 * and hostility detection for various mob types.
 */
public class EntityCommonTest {
    static ServerMock mockServer;
    static Ollivanders2 testPlugin;
    static EntityCommon entityCommon;
    World testWorld;

    /**
     * Global test setup. Initializes MockBukkit server and loads the plugin with test configuration.
     */
    @BeforeAll
    static void globalSetUp () {
        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        entityCommon = new EntityCommon(testPlugin);
    }

    /**
     * Per-test setup. Creates a fresh test world for each test.
     */
    @BeforeEach
    void setUp() {
        testWorld = mockServer.addSimpleWorld("world");
    }

    /**
     * Test that entityTypeFromString() correctly converts a valid entity type string to an EntityType enum.
     */
    @Test
    void entityTypeFromStringTest() {
        EntityType expectedType = EntityType.RABBIT;
        EntityType type = entityCommon.entityTypeFromString("RABBIT");
        assertSame(expectedType, type, "entityCommon.entityTypeFromString() entities not the same, Expected: " + expectedType + " Actual: " + type);
    }

    /**
     * Test that entityTypeFromString() returns null when given an invalid entity type string.
     */
    @Test
    void entityTypeFromStringBadStringTest() {
        assertNull(entityCommon.entityTypeFromString("ROBBIT"), "entityCommon.entityTypeFromString(\"Robbit\") did not return null");
    }

    /**
     * Test that getEntitiesInBounds() correctly finds entities within a bounding box and excludes those outside it.
     */
    @Test
    void getEntitiesInBoundsTest() {
        testWorld.spawn(new Location(testWorld, 0, 4, 0), Rabbit.class);
        testWorld.spawn(new Location(testWorld, 2, 4, 0), Rabbit.class);
        testWorld.spawn(new Location(testWorld, 0, 4, 2), Rabbit.class);

        // get all entities within 10 blocks of 0, 4, 0
        Collection<Entity> entities = EntityCommon.getEntitiesInBounds(new Location(testWorld, 0, 4, 0), 10, 10, 10);
        assertEquals(3, entities.size(), "EntityCommon.getEntitiesInBounds() did not find the expected number of entities");

        testWorld.spawn(new Location(testWorld, 0, 4, 12), Rabbit.class);
        entities = EntityCommon.getEntitiesInBounds(new Location(testWorld, 0, 4, 0), 10, 10, 10);
        assertEquals(3, entities.size(), "EntityCommon.getEntitiesInBounds() did not find the expected number of entities");
    }

    /**
     * Test that getEntitiesInRadius() correctly finds all entities (including non-living entities like minecarts)
     * within a radius and excludes those outside it.
     */
    @Test void getEntitiesInRadiusTest() {
        testWorld.spawn(new Location(testWorld, 100, 4, 100), Rabbit.class);
        testWorld.spawn(new Location(testWorld, 102, 4, 100), Minecart.class);
        testWorld.spawn(new Location(testWorld, 100, 4, 102), Rabbit.class);

        // get all entities within a 10 block radius of 100, 4, 100
        Collection<Entity> entities = EntityCommon.getEntitiesInRadius(new Location(testWorld, 100, 4, 100), 10);
        assertEquals(3, entities.size(), "EntityCommon.getEntitiesInRadius() did not find the expected number of entities");

        testWorld.spawn(new Location(testWorld, 100, 4, 112), Rabbit.class);
        entities = EntityCommon.getEntitiesInRadius(new Location(testWorld, 100, 4, 100), 10);
        assertEquals(3, entities.size(), "EntityCommon.getEntitiesInRadius() did not find the expected number of entities");
    }

    /**
     * Test that getLivingEntitiesInRadius() correctly finds only LivingEntity instances within a radius,
     * excluding non-living entities like minecarts.
     */
    @Test
    void getLivingEntitiesInRadiusTest() {
        testWorld.spawn(new Location(testWorld, 200, 4, 200), Rabbit.class);
        testWorld.spawn(new Location(testWorld, 204, 4, 200), Rabbit.class);
        testWorld.spawn(new Location(testWorld, 200, 4, 204), Rabbit.class);

        List<LivingEntity> livingEntities = EntityCommon.getLivingEntitiesInRadius(new Location(testWorld, 200, 4, 200), 10);
        assertEquals(3, livingEntities.size(), "EntityCommon.getLivingEntitiesInRadius() did not find the expected number of entities");

        testWorld.spawn(new Location(testWorld, 204, 4, 204), Minecart.class);
        testWorld.spawn(new Location(testWorld, 200, 4, 214), Rabbit.class);
        livingEntities = EntityCommon.getLivingEntitiesInRadius(new Location(testWorld, 200, 4, 200), 10);
        assertEquals(3, livingEntities.size(), "EntityCommon.getLivingEntitiesInRadius() did not find the expected number of entities");
    }

    /**
     * Test that getNearbyEntitiesByType() correctly finds only entities of a specific type within a radius,
     * filtering out other entity types.
     */
    @Test
    void getNearbyEntitiesByTypeTest() {
        testWorld.spawn(new Location(testWorld, 300, 4, 300), Rabbit.class);
        testWorld.spawn(new Location(testWorld, 304, 4, 300), Rabbit.class);
        testWorld.spawn(new Location(testWorld, 300, 4, 304), Rabbit.class);

        List<Entity> entities = EntityCommon.getNearbyEntitiesByType(new Location(testWorld, 300, 4, 300), 10, EntityType.RABBIT);
        assertEquals(3, entities.size(), "EntityCommon.getNearbyEntitiesByType() did not return the expected number of entities");

        testWorld.spawn(new Location(testWorld, 304, 4, 304), Minecart.class);
        testWorld.spawn(new Location(testWorld, 300, 4, 314), Rabbit.class);
        entities = EntityCommon.getNearbyEntitiesByType(new Location(testWorld, 300, 4, 300), 10, EntityType.RABBIT);
        assertEquals(3, entities.size(), "EntityCommon.getNearbyEntitiesByType() did not return the expected number of entities");
    }

    /**
     * Test that getItemsInBounds() correctly finds dropped items within a bounding box
     * and excludes both non-item entities and items outside the bounding box.
     */
    @Test
    void getItemsInBoundsTest() {
        testWorld.dropItemNaturally(new Location(testWorld, 400, 4, 400), new ItemStack(Material.APPLE));
        testWorld.dropItemNaturally(new Location(testWorld, 401, 4, 400), new ItemStack(Material.APPLE));
        testWorld.dropItemNaturally(new Location(testWorld, 400, 4, 401), new ItemStack(Material.APPLE));

        List<Item> items = EntityCommon.getItemsInBounds(new Location(testWorld, 400, 4, 400), 10, 10, 10);
        assertEquals(3, items.size(), "EntityCommon.getItemsInBounds() did not return expected number of items");

        testWorld.spawn(new Location(testWorld, 404, 4, 404), Minecart.class);
        testWorld.dropItemNaturally(new Location(testWorld, 400, 4, 415), new ItemStack(Material.APPLE));
        items = EntityCommon.getItemsInBounds(new Location(testWorld, 400, 4, 400), 10, 10, 10);
        assertEquals(3, items.size(), "EntityCommon.getItemsInBounds() did not return expected number of items");
    }

    /**
     * Test that getItemsInRadius() correctly finds dropped items within a radius
     * and excludes items outside the radius.
     */
    @Test
    void getItemsInRadiusTest() {
        testWorld.dropItemNaturally(new Location(testWorld, 500, 4, 500), new ItemStack(Material.APPLE));
        testWorld.dropItemNaturally(new Location(testWorld, 501, 4, 500), new ItemStack(Material.APPLE));
        testWorld.dropItemNaturally(new Location(testWorld, 500, 4, 501), new ItemStack(Material.APPLE));

        List<Item> items = EntityCommon.getItemsInRadius(new Location(testWorld, 500, 4, 500), 10);
        assertEquals(3, items.size(), "EntityCommon.getItemsInRadius() did not return expected number of items");

        testWorld.dropItemNaturally(new Location(testWorld, 500, 4, 515), new ItemStack(Material.APPLE));
        items = EntityCommon.getItemsInRadius(new Location(testWorld, 500, 4, 500), 10);
        assertEquals(3, items.size(), "EntityCommon.getItemsInRadius() did not return expected number of items");
    }

    /**
     * Test that getNearbyItemByMaterial() correctly finds items of a specific material type within a radius,
     * returns null when no items exist, and returns null when only wrong material types are present.
     */
    @Test
    void getNearbyItemByMaterialTest() {
        Item item = EntityCommon.getNearbyItemByMaterial(new Location(testWorld, 600, 4, 600), Material.APPLE, 10);
        assertNull(item, "EntityCommon.getNearbyItemByMaterial() did not return null when no items present");

        testWorld.dropItemNaturally(new Location(testWorld, 604, 4, 600), new ItemStack(Material.ARROW));
        item = EntityCommon.getNearbyItemByMaterial(new Location(testWorld, 600, 4, 600), Material.APPLE, 10);
        assertNull(item, "EntityCommon.getNearbyItemByMaterial() did not return null when no item of the correct type present");

        testWorld.dropItemNaturally(new Location(testWorld, 604, 4, 600), new ItemStack(Material.APPLE));
        item = EntityCommon.getNearbyItemByMaterial(new Location(testWorld, 600, 4, 600), Material.APPLE, 10);
        assertNotNull(item, "EntityCommon.getNearbyItemByMaterial() returned null when item nearby");
        assertSame(Material.APPLE, item.getItemStack().getType());
    }

    /**
     * Test that getNearbyItemByMaterialList() correctly finds items matching any material in a provided list,
     * returns null when no items exist, and returns null when only materials not in the list are present.
     */
    @Test
    void getNearbyItemByMaterialList() {
        ArrayList<Material> materials = new ArrayList<>() {{
            add(Material.APPLE);
            add(Material.ARROW);
        }};

        Item item = EntityCommon.getNearbyItemByMaterialList(new Location(testWorld, 700, 4, 700), materials, 10);
        assertNull(item, "EntityCommon.getNearbyItemByMaterialList() did not return null when no items present");

        testWorld.dropItemNaturally(new Location(testWorld, 700, 4, 700), new ItemStack(Material.BELL));
        item = EntityCommon.getNearbyItemByMaterialList(new Location(testWorld, 700, 4, 700), materials, 10);
        assertNull(item, "EntityCommon.getNearbyItemByMaterialList() did not return null when no items of the correct types present");

        testWorld.dropItemNaturally(new Location(testWorld, 704, 4, 700), new ItemStack(Material.ARROW));
        item = EntityCommon.getNearbyItemByMaterialList(new Location(testWorld, 700, 4, 700), materials, 10);
        assertNotNull(item, "EntityCommon.getNearbyItemByMaterialList() was null when item of correct type nearby");

        testWorld.dropItemNaturally(new Location(testWorld, 702, 4, 700), new ItemStack(Material.APPLE));
        item = EntityCommon.getNearbyItemByMaterialList(new Location(testWorld, 700, 4, 700), materials, 10);
        assertNotNull(item, "EntityCommon.getNearbyItemByMaterialList() was null when item of correct type nearby");
    }

    /**
     * Test that getNearbyO2ItemByType() correctly finds plugin-specific O2 items by type,
     * returns null when no items exist, and returns null when only wrong O2 item types are present.
     */
    @Test
    void getNearbyO2ItemByTypeTest() {
        Item item = EntityCommon.getNearbyO2ItemByType(new Location(testWorld, 800, 4, 800), O2ItemType.FLOO_POWDER, 10);
        assertNull(item, "EntityCommon.getNearbyO2ItemByType() did not return null when no nearby items exist");

        ItemStack itemStack = O2ItemType.GALLEON.getItem(1);
        assertNotNull(itemStack, "O2ItemType.GALLEON.getItem(1) returned null");
        testWorld.dropItemNaturally(new Location(testWorld, 801, 4, 800), itemStack);
        item = EntityCommon.getNearbyO2ItemByType(new Location(testWorld, 800, 4, 800), O2ItemType.FLOO_POWDER, 10);
        assertNull(item, "EntityCommon.getNearbyO2ItemByType() not null when no item of correct type nearby");

        itemStack = O2ItemType.FLOO_POWDER.getItem(1);
        assertNotNull(itemStack, "O2ItemType.FLOO_POWDER.getItem(1) returned null");
        testWorld.dropItemNaturally(new Location(testWorld, 800, 4, 800), itemStack);
        item = EntityCommon.getNearbyO2ItemByType(new Location(testWorld, 800, 4, 800), O2ItemType.FLOO_POWDER, 10);
        assertNotNull(item, "EntityCommon.getNearbyO2ItemByType() null when item of correct type nearby");
    }

    /**
     * Test that getRandomCatType() returns a consistent value for the same seed
     * and produces variety when called multiple times without a seed.
     */
    @Test
    void getRandomCatTypeTest() {
        Set<Cat.Type> catTypes = new HashSet<>();

        assertSame(EntityCommon.getRandomCatType(1), EntityCommon.getRandomCatType(1));

        for (int i = 0; i < 10; i++) {
            Cat.Type type = EntityCommon.getRandomCatType();
            mockServer.getScheduler().performTicks(4);
            catTypes.add(type);
        }

        assertTrue(catTypes.size() > 1, "Expected variety in cat types, but all 10 were the same: " + catTypes);
    }

    /**
     * Test that getRandomRabbitType() returns a consistent value for the same seed
     * and produces variety when called multiple times without a seed.
     */
    @Test
    void getRandomRabbitTypeTest() {
        Set<Rabbit.Type> rabbitTypes = new HashSet<>();

        assertSame(EntityCommon.getRandomRabbitType(1), EntityCommon.getRandomRabbitType(1));

        for (int i = 0; i < 10; i++) {
            Rabbit.Type type = EntityCommon.getRandomRabbitType();
            mockServer.getScheduler().performTicks(4);
            rabbitTypes.add(type);
        }

        assertTrue(rabbitTypes.size() > 1, "Expected variety in rabbit types, but all 10 were the same: " + rabbitTypes);
    }

    /**
     * Test that getRandomHorseStyle() returns a consistent value for the same seed
     * and produces variety when called multiple times without a seed.
     */
    @Test
    void getRandomHorseStyleTest() {
        Set<Horse.Style> horseStyles = new HashSet<>();

        assertSame(EntityCommon.getRandomHorseStyle(1), EntityCommon.getRandomHorseStyle(1));

        for (int i = 0; i < 20; i++) {
            Horse.Style style = EntityCommon.getRandomHorseStyle();
            mockServer.getScheduler().performTicks(3);
            horseStyles.add(style);
        }

        assertTrue(horseStyles.size() > 1, "Expected variety in horse styles, but all were the same: " + horseStyles);
    }

    /**
     * Test that getRandomHorseColor() returns a consistent value for the same seed
     * and produces variety when called multiple times without a seed.
     */
    @Test
    void getRandomHorseColorTest() {
        Set<Horse.Color> horseColors = new HashSet<>();

        assertSame(EntityCommon.getRandomHorseColor(1), EntityCommon.getRandomHorseColor(1));

        for (int i = 0; i < 10; i++) {
            Horse.Color color = EntityCommon.getRandomHorseColor();
            mockServer.getScheduler().performTicks(4);
            horseColors.add(color);
        }

        assertTrue(horseColors.size() > 1, "Expected variety in horse colors, but all 10 were the same: " + horseColors);
    }

    /**
     * Test that getRandomLlamaColor() returns a consistent value for the same seed
     * and produces variety when called multiple times without a seed.
     */
    @Test
    void getRandomLlamaColorTest() {
        Set<Llama.Color> llamaColors = new HashSet<>();

        assertSame(EntityCommon.getRandomLlamaColor(1), EntityCommon.getRandomLlamaColor(1));

        for (int i = 0; i < 10; i++) {
            Llama.Color color = EntityCommon.getRandomLlamaColor();
            mockServer.getScheduler().performTicks(4);
            llamaColors.add(color);
        }

        assertTrue(llamaColors.size() > 1, "Expected variety in llama colors, but all 10 were the same: " + llamaColors);
    }

    /**
     * Test that getRandomParrotVariant() returns a consistent value for the same seed
     * and produces variety when called multiple times without a seed.
     */
    @Test
    void getRandomParrotVariantTest() {
        Set<Parrot.Variant> parrotVariant = new HashSet<>();

        assertSame(EntityCommon.getRandomParrotVariant(1), EntityCommon.getRandomParrotVariant(1));

        for (int i = 0; i < 10; i++) {
            Parrot.Variant variant = EntityCommon.getRandomParrotVariant();
            mockServer.getScheduler().performTicks(4);
            parrotVariant.add(variant);
        }

        assertTrue(parrotVariant.size() > 1, "Expected variety in parrot variants, but all 10 were the same: " + parrotVariant);
    }

    /**
     * Test that getRandomNaturalSheepColor() returns a consistent value for the same seed
     * and produces variety when called multiple times without a seed.
     */
    @Test
    void getRandomNaturalSheepColor() {
        Set<DyeColor> sheepColors = new HashSet<>();

        assertSame(EntityCommon.getRandomNaturalSheepColor(1), EntityCommon.getRandomNaturalSheepColor(1));

        for (int i = 0; i < 10; i++) {
            DyeColor color = EntityCommon.getRandomNaturalSheepColor();
            mockServer.getScheduler().performTicks(4);
            sheepColors.add(color);
        }

        assertTrue(sheepColors.size() > 1, "Expected variety in sheep colors, but all 10 were the same: " + sheepColors);
    }

    /**
     * Test that isHostile() returns true for inherently hostile Enemy mobs like zombies.
     */
    @Test
    void isHostileEnemyTest() {
        Zombie zombie = testWorld.spawn(new Location(testWorld, 900, 4, 900), Zombie.class);
        assertTrue(EntityCommon.isHostile(zombie), "EntityCommon.isHostile() should return true for Enemy mobs like Zombie");
    }

    /**
     * Test that isHostile() returns false for passive mobs like cows.
     */
    @Test
    void isHostilePassiveMobTest() {
        Cow cow = testWorld.spawn(new Location(testWorld, 910, 4, 910), Cow.class);
        assertFalse(EntityCommon.isHostile(cow), "EntityCommon.isHostile() should return false for passive mobs like Cow");
    }

    /**
     * Test that isHostile() returns true for neutral mobs that have a target set.
     */
    @Test
    void isNeutralMobWithTargetTest() {
        Bee bee = testWorld.spawn(new Location(testWorld, 920, 4, 920), Bee.class);
        Zombie target = testWorld.spawn(new Location(testWorld, 921, 4, 920), Zombie.class);
        bee.setTarget(target);
        assertTrue(EntityCommon.isHostile(bee), "EntityCommon.isHostile() should return true for mobs with targets");
    }

    /**
     * Test that isHostile() returns false for neutral mobs that do not have a target set.
     */
    @Test
    void isNeutralMobWithoutTargetTest() {
        Bee bee = testWorld.spawn(new Location(testWorld, 930, 4, 930), Bee.class);
        bee.setTarget(null);
        assertFalse(EntityCommon.isHostile(bee), "EntityCommon.isHostile() should return false for mobs without targets");
    }

    /**
     * Global test teardown. Unmocks the MockBukkit server.
     */
    @AfterAll
    static void globalTearDown () {
        MockBukkit.unmock();
    }
}
