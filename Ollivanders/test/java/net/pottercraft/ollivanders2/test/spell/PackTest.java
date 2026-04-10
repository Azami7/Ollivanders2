package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.PACK;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link PACK}, the Packing Charm.
 *
 * <p>Provides test coverage for the spell's container-packing mechanics including:
 * <ul>
 * <li><strong>Target validation:</strong> Verifies the spell only fires on chest-family blocks</li>
 * <li><strong>Item collection:</strong> Tests that nearby item entities are scooped into the target container</li>
 * <li><strong>Container routing:</strong> Validates correct destination for chests, ender chests, and shulker boxes</li>
 * <li><strong>Overflow handling:</strong> Confirms items that exceed container capacity remain in the world with the leftover stack</li>
 * <li><strong>Non-item entities:</strong> Verifies non-item entities within radius are not affected</li>
 * </ul>
 *
 * @author Azami7
 */
public class PackTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.PACK;
    }

    /**
     * Tests packing behavior against a regular chest.
     *
     * <p>Verifies that:
     * <ul>
     * <li>The spell is killed when it strikes a non-chest block (oak door)</li>
     * <li>The spell is killed when it strikes a chest with no nearby items, and the chest stays empty</li>
     * <li>Nearby blocks of other types are not modified by the spell</li>
     * <li>Non-item entities (boat) within radius are not affected</li>
     * <li>A single nearby item is moved into the chest and removed from the world</li>
     * <li>Multiple nearby items are all moved into the chest in a single cast</li>
     * <li>Adding items does not remove items already in the chest</li>
     * <li>Items that exceed the chest's remaining capacity remain in the world with the leftover stack amount</li>
     * </ul>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);
        targetLocation.getBlock().setType(Material.OAK_DOOR);

        PACK pack = (PACK) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(pack.isKilled(), "spell not killed when hit non pass through block");

        Block chestBlock = targetLocation.getBlock();
        chestBlock.setType(Material.CHEST);

        // spell only targets blocks that are chest materials
        Chest chest = (Chest) chestBlock.getState();
        assertTrue(chest.getInventory().isEmpty());
        pack = (PACK) castSpell(caster, location, targetLocation);
        chest = (Chest) chestBlock.getState(); // get the state fresh every time since mockbukkit likes to cache it
        mockServer.getScheduler().performTicks(20);
        assertTrue(pack.isKilled(), "spell not killed when it hit a chest block");
        assertTrue(chest.getInventory().isEmpty());

        // nearby blocks are not affected
        Block nearbyBlock = chestBlock.getRelative(BlockFace.EAST);
        nearbyBlock.setType(Material.OAK_PLANKS);
        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertEquals(Material.OAK_PLANKS, nearbyBlock.getType(), "nearby block type changed unexpectedly");
        chest = (Chest) chestBlock.getState();
        assertTrue(chest.getInventory().isEmpty());

        // entities that are not items are not affected
        Entity nearbyEntity = testWorld.spawnEntity(chestBlock.getRelative(BlockFace.WEST).getLocation(), EntityType.OAK_BOAT);
        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertFalse(nearbyEntity.isDead(), "nearby entity removed unexpectedly");
        chest = (Chest) chestBlock.getState();
        assertTrue(chest.getBlockInventory().isEmpty());

        // add 1 item
        Item nearbyItem = testWorld.dropItem(chestBlock.getRelative(BlockFace.SOUTH).getLocation(), new ItemStack(Material.COMPASS, 1));
        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        chest = (Chest) chestBlock.getState();
        assertFalse(chest.getBlockInventory().isEmpty(), "Chest is empty when pack should have added 1 item");
        assertEquals(1, getChestItemCount(chest), "unexpected count of items in chest");
        assertTrue(nearbyItem.isDead(), "nearby item not removed when added to chest");
        chest.getInventory().clear();

        // add multiple items
        nearbyItem = testWorld.dropItem(chestBlock.getRelative(BlockFace.SOUTH).getLocation(), new ItemStack(Material.COMPASS, 1));
        Item nearbyItem2 = testWorld.dropItem(chestBlock.getRelative(BlockFace.NORTH).getLocation(), new ItemStack(Material.BOWL, 1));
        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        chest = (Chest) chestBlock.getState();
        assertFalse(chest.getBlockInventory().isEmpty());
        assertEquals(2, getChestItemCount(chest), "unexpected count of items in chest");
        assertTrue(nearbyItem.isDead(), "nearby item not removed when added to chest");
        assertTrue(nearbyItem2.isDead(), "nearby item not removed when added to chest");

        // adding an item does not remove items already in the chest
        nearbyItem = testWorld.dropItem(chestBlock.getRelative(BlockFace.SOUTH).getLocation(), new ItemStack(Material.BOOK, 1));
        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        chest = (Chest) chestBlock.getState();
        assertFalse(chest.getBlockInventory().isEmpty());
        assertEquals(3, getChestItemCount(chest), "unexpected count of items in chest");
        assertTrue(nearbyItem.isDead(), "nearby item not removed when added to chest");

        // adding more items that can fit results in overflow items outside the chest
        int maxChestCapacity = 1728; // 27 slots x 64 item stack depth - https://minecraft.fandom.com/wiki/Chest
        nearbyItem = testWorld.dropItem(chestBlock.getRelative(BlockFace.SOUTH).getLocation(), new ItemStack(Material.OXEYE_DAISY, maxChestCapacity));
        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        chest = (Chest) chestBlock.getState();
        assertFalse(chest.getBlockInventory().isEmpty());
        assertFalse(nearbyItem.isDead(), "item removed when there should be overflow inventory");
        // remainder will be 1728 - 3 stacks of 64 because there are 3 item slots in use in the chest already = 192
        assertEquals(192, nearbyItem.getItemStack().getAmount(), "unexpected overflow amount");
    }

    /**
     * Tests packing behavior against an ender chest.
     *
     * <p>Verifies that items go into the caster's personal ender chest inventory rather than into
     * the targeted block. Covers the same three phases as {@link #doCheckEffectTest()}:
     * <ul>
     * <li>Adding a single item</li>
     * <li>Adding multiple items in one cast</li>
     * <li>Overflow handling when items exceed the ender chest's capacity</li>
     * </ul>
     */
    @Test
    void enderChestTest() {
        runContainerPackTest(
                "PackEnderChest",
                Material.ENDER_CHEST,
                (block, caster) -> caster.getEnderChest(),
                "ender chest"
        );
    }

    /**
     * Tests packing behavior against a shulker box.
     *
     * <p>Verifies that items are added to the shulker box's block inventory. Covers the same three
     * phases as {@link #doCheckEffectTest()}:
     * <ul>
     * <li>Adding a single item</li>
     * <li>Adding multiple items in one cast</li>
     * <li>Overflow handling when items exceed the shulker box's capacity</li>
     * </ul>
     */
    @Test
    void shulkerBoxTest() {
        runContainerPackTest(
                "PackShulkerBox",
                Material.SHULKER_BOX,
                (block, caster) -> ((ShulkerBox) block.getState()).getInventory(),
                "shulker box"
        );
    }

    /**
     * Shared three-phase pack test against an arbitrary container type.
     *
     * <p>Phases run in order against the same container, caster, and world:
     * <ul>
     * <li>Add a single item — verifies it lands in the container and the entity is removed.</li>
     * <li>Add multiple items in one cast — verifies all items land and all entities are removed.</li>
     * <li>Overflow — drops more items than the container can hold and verifies the leftover stack
     *     remains in the world with the correct overflow amount.</li>
     * </ul>
     *
     * <p>The container inventory is cleared between phases.
     *
     * @param worldName         name passed to {@link org.mockbukkit.mockbukkit.ServerMock#addSimpleWorld(String)}
     * @param containerMaterial the container block type to spawn at the target location
     * @param inventoryAccessor function returning the container's inventory given the block and caster;
     *                          called fresh on every access to avoid stale MockBukkit state
     * @param containerLabel    human-readable container name used in assertion failure messages
     */
    private void runContainerPackTest(String worldName, Material containerMaterial, BiFunction<Block, PlayerMock, Inventory> inventoryAccessor, String containerLabel) {
        World testWorld = mockServer.addSimpleWorld(worldName);
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);
        Block containerBlock = targetLocation.getBlock();
        containerBlock.setType(containerMaterial);

        // sanity check: container is empty to start
        assertTrue(inventoryAccessor.apply(containerBlock, caster).isEmpty());

        // add 1 item
        Item nearbyItem = testWorld.dropItem(containerBlock.getRelative(BlockFace.SOUTH).getLocation(), new ItemStack(Material.COMPASS, 1));
        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertFalse(inventoryAccessor.apply(containerBlock, caster).isEmpty(), containerLabel + " is empty when pack should have added 1 item");
        assertEquals(1, getInventoryItemCount(inventoryAccessor.apply(containerBlock, caster)), "unexpected count of items in " + containerLabel);
        assertTrue(nearbyItem.isDead(), "nearby item not removed when added to " + containerLabel);
        inventoryAccessor.apply(containerBlock, caster).clear();

        // add multiple items
        nearbyItem = testWorld.dropItem(containerBlock.getRelative(BlockFace.SOUTH).getLocation(), new ItemStack(Material.COMPASS, 1));
        Item nearbyItem2 = testWorld.dropItem(containerBlock.getRelative(BlockFace.NORTH).getLocation(), new ItemStack(Material.BOWL, 1));
        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertFalse(inventoryAccessor.apply(containerBlock, caster).isEmpty());
        assertEquals(2, getInventoryItemCount(inventoryAccessor.apply(containerBlock, caster)), "unexpected count of items in " + containerLabel);
        assertTrue(nearbyItem.isDead(), "nearby item not removed when added to " + containerLabel);
        assertTrue(nearbyItem2.isDead(), "nearby item not removed when added to " + containerLabel);
        inventoryAccessor.apply(containerBlock, caster).clear();

        // adding more items than can fit results in overflow items outside the container
        int maxCapacity = 1728; // 27 slots x 64 item stack depth - https://minecraft.fandom.com/wiki/Chest
        nearbyItem = testWorld.dropItem(containerBlock.getRelative(BlockFace.SOUTH).getLocation(), new ItemStack(Material.OXEYE_DAISY, maxCapacity + 3));
        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertFalse(inventoryAccessor.apply(containerBlock, caster).isEmpty());
        assertFalse(nearbyItem.isDead(), "item removed when there should be overflow inventory");
        // remainder will be (1728 + 3) - 1728 = 3
        assertEquals(3, nearbyItem.getItemStack().getAmount(), "unexpected overflow amount");
    }

    /**
     * Counts the number of non-empty item slots in a chest's block inventory.
     *
     * @param chest the chest whose contents to count
     * @return the number of slots containing items
     */
    private int getChestItemCount(Chest chest) {
        return getInventoryItemCount(chest.getBlockInventory());
    }

    /**
     * Counts the number of non-empty item slots in an inventory.
     *
     * @param inventory the inventory whose contents to count
     * @return the number of slots containing items
     */
    private int getInventoryItemCount(Inventory inventory) {
        int count = 0;

        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null && itemStack.getAmount() > 0)
                count = count + 1;
        }

        return count;
    }

    /**
     * No-op revert test — PACK has no revert actions to undo.
     */
    @Override
    @Test
    void revertTest() {
        // no revert actions
    }
}
