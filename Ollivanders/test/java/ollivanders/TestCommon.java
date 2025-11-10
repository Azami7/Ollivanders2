package ollivanders;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Common functions for tests
 */
public class TestCommon {
    /**
     * Compare the message received by a player to the expected message. This needs a helper because
     * we need to strip chat color codes from the messages sent by the plugin.
     *
     * @param expected the message we expected
     * @param message the message received
     * @return
     */
    public static boolean messageStartsWith(@NotNull String expected, @NotNull String message) {
        String strippedMessage = message.replaceAll("§.", "");

        return strippedMessage.startsWith(expected);
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
    public static String runCommand (PlayerMock player, String command, ServerMock mockServer) {
        assertTrue(player.performCommand(command), "Player cannot run the " + command + " command");
        mockServer.getScheduler().performTicks(10);
        String commandResponse = player.nextMessage();

        return commandResponse;
    }

    /**
     * Does the player's inventory contain an item of the specified type
     * @param player the player to check
     * @param itemType the type to check for
     * @return true if found, false otherwise
     */
    public static boolean isInPlayerInventory(PlayerMock player, Material itemType) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
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
    public static boolean isInPlayerInventory(PlayerMock player, Material itemType, String name) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null)
                continue;

            if (itemStack.getType() == itemType) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta == null)
                    continue;

                if (itemStack.getType() == Material.WRITTEN_BOOK) {
                    if (((BookMeta)itemMeta).getTitle().equals(name))
                        return true;
                }
                else if (itemMeta.getDisplayName().equals(name))
                        return true;
            }
        }

        return false;
    }
}
