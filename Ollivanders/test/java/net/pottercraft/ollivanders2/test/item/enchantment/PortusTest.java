package net.pottercraft.ollivanders2.test.item.enchantment;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.enchantment.Enchantment;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import net.pottercraft.ollivanders2.item.enchantment.PORTUS;
import net.pottercraft.ollivanders2.stationaryspell.NULLUM_APPAREBIT;
import net.pottercraft.ollivanders2.stationaryspell.NULLUM_EVANESCUNT;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the {@link ItemEnchantmentType#PORTUS} portkey enchantment.
 * <p>
 * Verifies that the PORTUS enchantment correctly creates and activates portkeys that teleport
 * players to predetermined destinations. Portkeys are magical transportation devices that can
 * transport multiple players within a configurable radius.
 * </p>
 * <p>
 * All test scenarios are consolidated into a single test method to ensure sequential execution
 * and avoid race conditions from parallel test execution with the shared MockBukkit server.
 * </p>
 * <p>
 * Test coverage includes:
 * <ul>
 * <li>Non-player pickup blocking: verifies only players can activate portkeys</li>
 * <li>Player teleportation: verifies players are teleported to the destination</li>
 * <li>Round-trip behavior: verifies the destination is updated to the pickup location after teleportation</li>
 * <li>Stationary spell blocking: verifies NULLUM_EVANESCUNT and NULLUM_APPAREBIT prevent teleportation</li>
 * <li>Proximity check: verifies portkeys don't activate if player is within 3 blocks of destination</li>
 * <li>Radius teleportation: verifies nearby players are also teleported</li>
 * </ul>
 * </p>
 *
 * @see ItemEnchantmentType#PORTUS the portkey enchantment being tested
 */
public class PortusTest extends EnchantmentTestSuper {
    /**
     * Configure this test instance for PORTUS enchantment testing.
     * <p>
     * Sets the enchantment type to PORTUS, uses FLOWER_POT material for creating test items,
     * and sets default args to teleport to coordinates (100, 4, 100) in the test world.
     * </p>
     */
    @Override @BeforeEach
    void setUp() {
        enchantmentType = ItemEnchantmentType.PORTUS;
        itemType = Material.FLOWER_POT;
        defaultArgs = testWorld.getName() + " " + 100 + " " + 4 + " " + 100;
    }

    /**
     * Comprehensive test for all PORTUS portkey enchantment behaviors.
     * <p>
     * This method consolidates all portkey test scenarios into a single sequential test to avoid
     * race conditions from parallel test execution. Each scenario uses different coordinates to
     * prevent interference between tests.
     * </p>
     * <p>
     * Scenarios tested:
     * <ol>
     * <li>Non-player pickup blocking: skeleton cannot pick up portkey</li>
     * <li>Player teleportation: player is teleported to destination when picking up portkey</li>
     * <li>Round-trip behavior: portkey destination updates to pickup location after teleportation</li>
     * <li>NULLUM_EVANESCUNT blocking: stationary spell prevents teleportation FROM location</li>
     * <li>NULLUM_APPAREBIT blocking: stationary spell prevents teleportation TO destination</li>
     * <li>Proximity check: portkey doesn't activate within 3 blocks of destination</li>
     * <li>Radius teleportation: nearby players are also teleported</li>
     * </ol>
     * </p>
     */
    @Override @Test
    void doEntityPickupItemTest() {
        // ========================================
        // Scenario 1: Non-player entities cannot activate portkeys
        // ========================================
        Location skeletonOrigin = new Location(testWorld, 0, 4, 0);
        ItemStack skeletonPortkey = makeEnchantedItem(1, defaultArgs);

        Skeleton skeleton = (Skeleton) testWorld.spawnEntity(skeletonOrigin, EntityType.SKELETON);
        skeleton.setCanPickupItems(true);
        Item droppedSkeletonPortkey = skeleton.getWorld().dropItem(skeleton.getLocation(), skeletonPortkey);
        skeleton.getEquipment().setItemInMainHand(skeletonPortkey);
        EntityPickupItemEvent skeletonEvent = new EntityPickupItemEvent(skeleton, droppedSkeletonPortkey, 0);
        mockServer.getPluginManager().callEvent(skeletonEvent);
        mockServer.getScheduler().performTicks(20);
        assertTrue(skeletonEvent.isCancelled(), "PORTUS did not prevent non-Player from picking up portkey item");

        // ========================================
        // Scenario 2 & 3: Player teleportation and round-trip behavior
        // ========================================
        Location teleportOrigin = new Location(testWorld, 1000, 4, 1000);
        Location teleportDestination = new Location(testWorld, 1020, 4, 1000);
        PlayerMock teleportPlayer = mockServer.addPlayer();
        teleportPlayer.setLocation(teleportOrigin);

        ItemStack teleportPortkey = makeEnchantedItem(1, testWorld.getName() + " 1020 4 1000");
        Enchantment portkeyEnchantment = Ollivanders2API.getItems().enchantedItems.getEnchantment(teleportPortkey);
        assertNotNull(portkeyEnchantment, "Failed to get enchantment from portkey");

        Item droppedTeleportPortkey = teleportPlayer.getWorld().dropItem(teleportPlayer.getLocation(), teleportPortkey);
        teleportPlayer.getInventory().addItem(teleportPortkey);
        EntityPickupItemEvent teleportEvent = new EntityPickupItemEvent(teleportPlayer, droppedTeleportPortkey, 0);
        mockServer.getPluginManager().callEvent(teleportEvent);
        mockServer.getScheduler().performTicks(200);
        assertEquals(teleportDestination.getX(), teleportPlayer.getLocation().getX(), "Player was not teleported to the destination when portkey picked up");

        // Verify round-trip: after teleporting, the portkey's destination is now the origin
        Location portkeyDestination = ((PORTUS) portkeyEnchantment).getDestination();
        assertNotNull(portkeyDestination);
        assertEquals(teleportOrigin.getX(), portkeyDestination.getX(), "Portkey destination not reset to origin after teleporting player");

        // ========================================
        // Scenario 4: NULLUM_EVANESCUNT blocks teleportation FROM location
        // ========================================
        Location evanescuntOrigin = new Location(testWorld, 2000, 4, 2000);
        PlayerMock evanescuntPlayer = mockServer.addPlayer();

        NULLUM_EVANESCUNT nullumEvanescunt = new NULLUM_EVANESCUNT(testPlugin, evanescuntPlayer.getUniqueId(), evanescuntOrigin, 10, 1000);
        Ollivanders2API.getStationarySpells().addStationarySpell(nullumEvanescunt);
        ItemStack evanescuntPortkey = makeEnchantedItem(1, testWorld.getName() + " 2000 4 2050");

        evanescuntPlayer.setLocation(evanescuntOrigin);
        mockServer.getScheduler().performTicks(20);
        Item droppedEvanescuntPortkey = evanescuntPlayer.getWorld().dropItem(evanescuntPlayer.getLocation(), evanescuntPortkey);
        evanescuntPlayer.getInventory().addItem(evanescuntPortkey);

        EntityPickupItemEvent evanescuntEvent = new EntityPickupItemEvent(evanescuntPlayer, droppedEvanescuntPortkey, 0);
        mockServer.getPluginManager().callEvent(evanescuntEvent);
        mockServer.getScheduler().performTicks(200);
        assertEquals(evanescuntOrigin.getZ(), evanescuntPlayer.getLocation().getZ(), "Player was teleported by portkey when NULLUM_EVANESCUNT present at origin");

        // Remove stationary spell for cleanup
        Ollivanders2API.getStationarySpells().removeStationarySpell(nullumEvanescunt);

        // ========================================
        // Scenario 5: NULLUM_APPAREBIT blocks teleportation TO destination
        // ========================================
        Location apparebitOrigin = new Location(testWorld, 3000, 4, 3000);
        Location apparebitDestination = new Location(testWorld, 3000, 4, 3050);
        PlayerMock apparebitPlayer = mockServer.addPlayer();

        NULLUM_APPAREBIT nullumApparebit = new NULLUM_APPAREBIT(testPlugin, apparebitPlayer.getUniqueId(), apparebitDestination, 10, 1000);
        Ollivanders2API.getStationarySpells().addStationarySpell(nullumApparebit);
        ItemStack apparebitPortkey = makeEnchantedItem(1, testWorld.getName() + " 3000 4 3050");

        apparebitPlayer.setLocation(apparebitOrigin);
        mockServer.getScheduler().performTicks(20);
        Item droppedApparebitPortkey = apparebitPlayer.getWorld().dropItem(apparebitPlayer.getLocation(), apparebitPortkey);
        apparebitPlayer.getInventory().addItem(apparebitPortkey);

        EntityPickupItemEvent apparebitEvent = new EntityPickupItemEvent(apparebitPlayer, droppedApparebitPortkey, 0);
        mockServer.getPluginManager().callEvent(apparebitEvent);
        mockServer.getScheduler().performTicks(200);
        assertEquals(apparebitOrigin.getZ(), apparebitPlayer.getLocation().getZ(), "Player was teleported by portkey when NULLUM_APPAREBIT present at destination");

        // Remove stationary spell for cleanup
        Ollivanders2API.getStationarySpells().removeStationarySpell(nullumApparebit);

        // ========================================
        // Scenario 6: Proximity check blocks teleportation within 3 blocks
        // ========================================
        Location proximityOrigin = new Location(testWorld, 4000, 4, 4000);
        PlayerMock proximityPlayer = mockServer.addPlayer();

        // Destination is only 2 blocks away (within 3 block threshold)
        ItemStack proximityPortkey = makeEnchantedItem(1, testWorld.getName() + " 4000 4 4002");
        proximityPlayer.setLocation(proximityOrigin);
        mockServer.getScheduler().performTicks(20);

        Item droppedProximityPortkey = proximityPlayer.getWorld().dropItem(proximityPlayer.getLocation(), proximityPortkey);
        proximityPlayer.getInventory().addItem(proximityPortkey);

        EntityPickupItemEvent proximityEvent = new EntityPickupItemEvent(proximityPlayer, droppedProximityPortkey, 0);
        mockServer.getPluginManager().callEvent(proximityEvent);
        mockServer.getScheduler().performTicks(200);
        assertEquals(proximityOrigin.getZ(), proximityPlayer.getLocation().getZ(), "Player was teleported by portkey when destination within 3 blocks of player location");

        // ========================================
        // Scenario 7: Nearby players within radius are also teleported
        // ========================================
        Location radiusOrigin = new Location(testWorld, 5000, 4, 5000);
        Location radiusDestination = new Location(testWorld, 5050, 4, 5000);
        PlayerMock radiusPlayer1 = mockServer.addPlayer();
        PlayerMock radiusPlayer2 = mockServer.addPlayer();

        ItemStack radiusPortkey = makeEnchantedItem(1, testWorld.getName() + " 5050 4 5000");
        radiusPlayer1.setLocation(radiusOrigin);
        radiusPlayer2.setLocation(radiusOrigin);
        mockServer.getScheduler().performTicks(20);

        Item droppedRadiusPortkey = radiusPlayer1.getWorld().dropItem(radiusPlayer1.getLocation(), radiusPortkey);
        radiusPlayer1.getInventory().addItem(radiusPortkey);
        EntityPickupItemEvent radiusEvent = new EntityPickupItemEvent(radiusPlayer1, droppedRadiusPortkey, 0);
        mockServer.getPluginManager().callEvent(radiusEvent);
        mockServer.getScheduler().performTicks(200);

        assertEquals(radiusDestination.getX(), radiusPlayer1.getLocation().getX(), "Player was not teleported to the destination when portkey picked up");
        assertEquals(radiusDestination.getX(), radiusPlayer2.getLocation().getX(), "Player2 was not teleported to the destination when portkey picked up");
    }

    /**
     * Portus has no action for PlayerDropItemEvent.
     * <p>
     * PORTUS portkeys only activate when picked up by a player, not when dropped.
     * </p>
     */
    @Override @Test
    void doItemDropTest() {}

    /**
     * Portus has no action for PlayerItemHeldEvent.
     * <p>
     * PORTUS portkeys only activate when picked up from the world, not when held or switched to.
     * </p>
     */
    @Override @Test
    void doItemHeldTest() {}
}