package net.pottercraft.ollivanders2.test.testcommon;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Common utility functions for tests.
 *
 * <p>Provides helper methods for common testing operations including message comparison, command execution,
 * inventory item lookup, and string matching utilities. These utilities handle MockBukkit-specific details
 * and simplify common test assertions.</p>
 *
 * @author Azami7
 */
public class TestCommon {
    /**
     * Number of ticks needed after server start to make sure the scheduler, etc are running
     */
    static public final int startupTicks = 100;

    /**
     * Compare the message received by a player to the expected message. This needs a helper because
     * we need to strip chat color codes from the messages sent by the plugin.
     *
     * @param expected the message we expected
     * @param message the message received
     * @return strippedMessage.startsWith(expected)
     */
    public static boolean messageStartsWith(@NotNull String expected, @NotNull String message) {
        String strippedMessage = message.replaceAll("§.", "");

        return strippedMessage.startsWith(expected);
    }

    /**
     * Remove Minecraft chat color codes from a message string.
     *
     * <p>Strips all color codes (format: section sign followed by a hex digit) from the message
     * to allow direct comparison with expected message text. This is necessary because the plugin
     * adds color codes to messages, but tests need to compare the actual text content.</p>
     *
     * @param message the message received from the plugin
     * @return the message with all chat color codes removed
     */
    public static String cleanChatMessage(@NotNull String message) {
        return message.replaceAll("§.", "");
    }

    /**
     * Make a player run a command and get the server response.
     *
     * @param player the player running the command
     * @param command the command to be run by the player
     * @param mockServer the mocked server for this test
     * @return the server response for the command
     */
    @Nullable
    public static String runCommand (@NotNull PlayerMock player, @NotNull String command, @NotNull ServerMock mockServer) {
        assertTrue(player.performCommand(command), "Player cannot run the " + command + " command");
        mockServer.getScheduler().performTicks(10);

        return player.nextMessage();
    }

    /**
     * Does the player's inventory contain an item of the specified type
     * @param player the player to check
     * @param itemType the type to check for
     * @return true if found, false otherwise
     */
    public static boolean isInPlayerInventory(@NotNull PlayerMock player, @NotNull Material itemType) {
        ItemStack itemStack = getPlayerInventoryItem(player, itemType);

        return itemStack != null;
    }

    /**
     * Does the player's inventory contain an item of the specified type with specified name
     *
     * @param player the player to check
     * @param itemType the item type to check for
     * @param name the name the item needs to have
     * @return true if found, false otherwise
     */
    public static boolean isInPlayerInventory(@NotNull PlayerMock player, @NotNull Material itemType, @NotNull String name) {
        ItemStack itemStack = getPlayerInventoryItem(player, itemType, name);

        return itemStack != null;
    }

    /**
     * Get an item from the player's inventory matching the specified material type.
     *
     * <p>Searches the player's inventory for the first item with a matching material type.
     * Returns null if no matching item is found.</p>
     *
     * @param player the player whose inventory to search
     * @param itemType the material type to match
     * @return the first matching ItemStack, or null if not found
     */
    @Nullable
    public static ItemStack getPlayerInventoryItem(@NotNull PlayerMock player, @NotNull Material itemType) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null)
                continue;

            if (itemStack.getType() == itemType) {
                return itemStack;
            }
        }

        return null;
    }

    /**
     * Get an item from the player's inventory matching the specified material type and display name.
     *
     * <p>Searches the player's inventory for the first item with a matching material type and display name.
     * For written books, matches against the book title. For other items, matches against the display name.
     * Returns null if no matching item is found.</p>
     *
     * @param player the player whose inventory to search
     * @param itemType the material type to match
     * @param name the display name or title to match
     * @return the first matching ItemStack, or null if not found
     */
    @Nullable
    public static ItemStack getPlayerInventoryItem(@NotNull PlayerMock player, @NotNull Material itemType, @NotNull String name) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null)
                continue;

            if (itemStack.getType() == itemType) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta == null)
                    continue;

                if (itemStack.getType() == Material.WRITTEN_BOOK) {
                    String bookTitle = ((BookMeta)itemMeta).getTitle();

                    if (bookTitle != null && bookTitle.equals(name))
                        return itemStack;
                }
                else if (itemMeta.getDisplayName().equals(name))
                    return itemStack;
            }
        }

        return null;
    }

    /**
     * Get the total amount of items in the player's inventory matching the specified type and name.
     *
     * <p>Searches the player's inventory for items matching both the material type and display name,
     * and returns the total stack amount. Returns 0 if no matching items are found.</p>
     *
     * @param player the player whose inventory to search
     * @param itemType the material type to match
     * @param name the display name or title to match
     * @return the amount of matching items, or 0 if none are found
     */
    public static int amountInPlayerInventory(@NotNull PlayerMock player, @NotNull Material itemType, @NotNull String name) {
        if (!isInPlayerInventory(player, itemType, name))
            return 0;

        ItemStack itemStack = getPlayerInventoryItem(player, itemType, name);
        if (itemStack == null)
            return 0;

        return itemStack.getAmount();
    }

    /**
     * Determine if any string in a list contains the specified substring.
     *
     * @param stringList the string list to check
     * @param subString the substring to match
     * @return true if found, false otherwise
     */
    static public boolean containsStringMatch(List<String> stringList, String subString) {
        for (String string : stringList) {
            if (string.contains(subString))
                return true;
        }

        return false;
    }

    /**
     * Determine if a string contains any of the strings in a list.
     *
     * @param stringList the string list of substrings to match
     * @param string the string to check
     */
    static public void stringContainsListMatch(List<String> stringList, String string) {
        boolean found = false;

        for (String subString : stringList) {
            if (string.contains(subString)) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Did not find \"" + string + "\" in list.");
    }

    /**
     * Set a players experience level with a specific potion
     *
     * @param testPlugin the plugin
     * @param player the player
     * @param potionType the potion type
     * @param count the count to set experience to
     */
    static public void setPlayerPotionExperience(@NotNull Ollivanders2 testPlugin, @NotNull Player player, @NotNull O2PotionType potionType, int count) {
        O2Player o2player = testPlugin.getO2Player(player);
        assertNotNull(o2player, "Failed to find O2Player");

        o2player.setPotionCount(potionType, count);
    }

    /**
     * Get a player's experience count for a specific potion type.
     *
     * <p>Retrieves the player's experience level with a specific potion type from their O2Player profile.
     * Used in tests to verify that potion experience has been correctly updated or modified.</p>
     *
     * @param testPlugin the plugin instance
     * @param player the player to query
     * @param potionType the potion type to get experience for
     * @return the player's experience count for the specified potion type
     */
    static public int getPlayerPotionExperience(@NotNull Ollivanders2 testPlugin, @NotNull Player player, @NotNull O2PotionType potionType) {
        O2Player o2player = testPlugin.getO2Player(player);
        assertNotNull(o2player, "Failed to find O2Player");

        return o2player.getPotionCount(potionType);
    }

    /**
     * Set a players experience level with a specific spell
     *
     * @param testPlugin the plugin
     * @param player the player
     * @param spellType the spell type
     * @param count the count to set experience to
     */
    static public void setPlayerSpellExperience(@NotNull Ollivanders2 testPlugin, @NotNull Player player, @NotNull O2SpellType spellType, int count) {
        O2Player o2player = testPlugin.getO2Player(player);
        assertNotNull(o2player, "Failed to find O2Player");

        o2player.setSpellCount(spellType, count);
    }

    /**
     * Get a player's experience count for a specific spell type.
     *
     * <p>Retrieves the player's experience level with a specific spell type from their O2Player profile.
     * Used in tests to verify that spell experience has been correctly updated or modified.</p>
     *
     * @param testPlugin the plugin instance
     * @param player the player to query
     * @param spellType the spell type to get experience for
     * @return the player's experience count for the specified spell type
     */
    static public int getPlayerSpellExperience(@NotNull Ollivanders2 testPlugin, @NotNull Player player, @NotNull O2SpellType spellType) {
        O2Player o2player = testPlugin.getO2Player(player);
        assertNotNull(o2player, "Failed to find O2Player");

        return o2player.getSpellCount(spellType);
    }

    /**
     * Creates a new location with yaw and pitch set so a player at that position faces the target block.
     *
     * <p>Positions the player at the center of their block (X+0.5, Z+0.5) and computes the direction
     * from their approximate eye position (Y rounded up from the 1.62 eye height offset) toward the
     * center of the target block (X+0.5, Y+0.5, Z+0.5). The eye Y is rounded to remove fractional
     * values for cleaner trajectory calculations.</p>
     *
     * @param playerLocation the player's feet position (not null)
     * @param targetLocation the location of the target block (not null)
     * @return a new location at block center with yaw and pitch set to face the target (not null)
     */
    @NotNull
    public static Location faceTarget(@NotNull Location playerLocation, @NotNull Location targetLocation) {
        Location result = playerLocation.clone().add(0.5, 0, 0.5);
        Location eyeLocation = result.clone();
        eyeLocation.setY(Math.round(result.getY() + 1.62));
        Vector targetCenter = targetLocation.toVector().add(new Vector(0.5, 0.5, 0.5));
        result.setDirection(targetCenter.subtract(eyeLocation.toVector()));

        return result;
    }

    /**
     * Get all items in the world.
     *
     * @param world the world
     * @return a list of all item entities found
     */
    @NotNull
    public static List<Item> getAllItems(@NotNull World world) {
        ArrayList<Item> items = new ArrayList<>();

        for (Entity entity : world.getEntities()) {
            if (entity instanceof Item)
                items.add((Item)entity);
        }

        return items;
    }

    /**
     * Get the location relative to a location.
     *
     * @param location the starting location
     * @param relativeTo the relative position to get a location for
     * @return the relative location
     */
    public static Location getRelativeLocation(Location location, BlockFace relativeTo) {
        Block blockAt = location.getBlock();

        return blockAt.getRelative(relativeTo).getLocation();
    }

    /**
     * Creates a base of stone blocks at the specified location that is size blocks out from the location
     *
     * @param location the location to create a base
     * @param size the number of blocks out from the location to create the size
     */
    public static void createBlockBase(Location location, int size) {
        createBlockBase(location, size, Material.STONE);
    }

    /**
     * Creates a base of blocks at the specified location that is size blocks out from the location
     *
     * @param location the location to create a base
     * @param size the number of blocks out from the location to create the size
     * @param material the material to make the base
     */
    public static void createBlockBase(Location location, int size, Material material) {
        for (int x = 0; x <= size; x++) {
            for (int z = 0; z <= size; z++) {
                location.getBlock().getRelative(x, 0, z).setType(material);
                location.getBlock().getRelative(-x, 0, z).setType(material);
                location.getBlock().getRelative(x, 0, -z).setType(material);
                location.getBlock().getRelative(-x, 0, -z).setType(material);
            }
        }
    }

    /**
     * Creates a north-south wall of stone blocks at the specified location that is size blocks out from the location
     *
     * @param location the location to create a wall
     * @param size the number of blocks out from the location to create the size
     */
    public static void createNorthSouthBlockWall(Location location, int size) {
        createNorthSouthBlockWall(location, size, Material.STONE);
    }

    /**
     * Creates a north-south wall of blocks at the specified location that is size blocks out from the location
     *
     * @param location the location to create a wall
     * @param size the number of blocks out from the location to create the size
     * @param material the material to make the wall
     */
    public static void createNorthSouthBlockWall(Location location, int size, Material material) {
        for (int y = 0; y <= size; y++) {
            for (int z = 0; z <= size; z++) {
                location.getBlock().getRelative(0, y, z).setType(material);
                location.getBlock().getRelative(0, -y, z).setType(material);
                location.getBlock().getRelative(0, y, -z).setType(material);
                location.getBlock().getRelative(0, -y, -z).setType(material);
            }
        }
    }

    /**
     * Creates an east-west wall of stone blocks at the specified location that is size blocks out from the location
     *
     * @param location the location to create a wall
     * @param size the number of blocks out from the location to create the size
     */
    public static void createEastWestBlockWall(Location location, int size) {
        createEastWestBlockWall(location, size, Material.STONE);
    }

    /**
     * Creates an east-west wall of blocks at the specified location that is size blocks out from the location
     *
     * @param location the location to create a wall
     * @param size the number of blocks out from the location to create the size
     * @param material the material to make the wall
     */
    public static void createEastWestBlockWall(Location location, int size, Material material) {
        for (int x = 0; x <= size; x++) {
            for (int y = 0; y <= size; y++) {
                location.getBlock().getRelative(x, y, 0).setType(material);
                location.getBlock().getRelative(x, -y, 0).setType(material);
                location.getBlock().getRelative(-x, y, 0).setType(material);
                location.getBlock().getRelative(-x, -y, 0).setType(material);
            }
        }
    }

}
