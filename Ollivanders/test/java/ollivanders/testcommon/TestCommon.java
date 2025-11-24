package ollivanders.testcommon;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Common functions for tests
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
     * Compare the message received by a player to the expected message. This needs a helper because
     * we need to strip chat color codes from the messages sent by the plugin.
     *
     * @param expected the message we expected
     * @param message the message received
     * @return strippedMessage.equals(expected)
     */
    public static boolean messageEquals(@NotNull String expected, @NotNull String message) {
        String strippedMessage = message.replaceAll("§.", "");

        return strippedMessage.equals(expected);
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
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null)
                return false;

            if (itemStack.getType() == itemType) {
                return true;
            }
        }

        return false;
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
                        return true;
                }
                else if (itemMeta.getDisplayName().equals(name))
                        return true;
            }
        }

        return false;
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
}
