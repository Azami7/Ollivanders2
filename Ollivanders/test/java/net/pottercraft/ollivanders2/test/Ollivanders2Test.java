package net.pottercraft.ollivanders2.test;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.potion.O2Potions;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Ollivanders2}: the /ollivanders2 command routing and its subcommands, the spell
 * casting gate {@link Ollivanders2#canCast}, and the O2Player and spell count accessors. Subsystem behavior
 * behind the subcommands (books, houses, players) is covered by each subsystem's own tests, and the teleport
 * queue is covered by {@link Ollivanders2TeleportActionsTest} - the plugin's live queue is not tested here
 * because parallel tests advancing the scheduler would drain it mid-assertion.
 *
 * <p>Each test uses its own player, so parallel test methods cannot see each other's inventory or player
 * record state.</p>
 */
public class Ollivanders2Test {
    static Ollivanders2 testPlugin;
    static ServerMock mockServer;

    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        mockServer.addSimpleWorld("world");
    }

    /**
     * Count the total items in a player's main inventory, summing stack amounts so the count does not depend
     * on how the items stacked.
     *
     * @param player the player to check
     * @return the total number of items across all inventory slots
     */
    private static int countInventoryItems(@NotNull PlayerMock player) {
        int count = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null)
                count = count + itemStack.getAmount();
        }

        return count;
    }

    /**
     * A player without admin permission gets the player usage summary and no admin action happens - the
     * wands subcommand must not give them any items.
     */
    @Test
    void nonAdminCommandTest() {
        PlayerMock player = mockServer.addPlayer();
        player.setOp(false);

        String message = TestCommon.runCommand(player, "Ollivanders2 wands", mockServer);
        assertNotNull(message, "A non-admin player should get the player usage summary");
        assertEquals(0, countInventoryItems(player), "A non-admin player should not be given any wands");
    }

    /**
     * The olli alias from plugin.yml routes to the same command, and the prophecy subcommand reports when
     * there are no unfulfilled prophecies.
     */
    @Test
    void commandAliasTest() {
        PlayerMock player = mockServer.addPlayer();
        player.setOp(true);

        String message = TestCommon.runCommand(player, "olli prophecy", mockServer);
        assertNotNull(message, "The olli alias should route to the Ollivanders2 command");
        assertEquals("There are no unfulfilled prophecies.", TestCommon.cleanChatMessage(message),
                "The prophecy subcommand should report there are no prophecies");
    }

    /**
     * The wands subcommand gives the sender one of every wand, gives a named player a random wand, and shows
     * the usage message for an unknown player name.
     */
    @Test
    void wandsCommandTest() {
        PlayerMock admin = mockServer.addPlayer();
        admin.setOp(true);
        PlayerMock target = mockServer.addPlayer();
        target.setOp(false);

        // -- wands with no args gives the sender every wand --
        assertTrue(admin.performCommand("Ollivanders2 wands"), "The wands subcommand should succeed");
        int allWandsCount = Ollivanders2API.getItems().getWands().getAllWands().size();
        assertEquals(allWandsCount, countInventoryItems(admin), "The sender should be given one of every wand");

        // -- wands with a player name gives that player a random wand --
        assertTrue(admin.performCommand("Ollivanders2 wands " + target.getName()),
                "The wands subcommand with a target should succeed");
        assertEquals(1, countInventoryItems(target), "The target should be given a random wand");

        // -- wands with an unknown player name shows the usage message --
        TestCommon.clearMessageQueue(admin);
        assertTrue(admin.performCommand("Ollivanders2 wands NoSuchPlayer"),
                "The wands subcommand with an unknown target should still succeed");
        assertNotNull(admin.nextMessage(), "An unknown target should get the wand usage message");
    }

    /**
     * The items subcommand lists items, gives the requested amount of a named item, clamps the amount to a
     * single stack, rejects an unparseable amount, and reports an unknown item name.
     */
    @Test
    void itemsCommandTest() {
        PlayerMock admin = mockServer.addPlayer();
        admin.setOp(true);

        // -- items list --
        String message = TestCommon.runCommand(admin, "Ollivanders2 items list", mockServer);
        assertNotNull(message, "The items list subcommand should send the list");
        assertTrue(message.contains("Item list:"), "The items list should be sent to the player");

        // -- items give with a valid amount --
        assertTrue(admin.performCommand("Ollivanders2 items give 2 Floo Powder"), "The items give subcommand should succeed");
        assertEquals(2, TestCommon.amountInPlayerInventory(admin, Ollivanders2.flooPowderMaterial, "Floo Powder"),
                "The sender should be given the requested amount of the item");

        // -- an amount over a single stack is clamped to a stack --
        PlayerMock admin2 = mockServer.addPlayer();
        admin2.setOp(true);
        assertTrue(admin2.performCommand("Ollivanders2 items give 70 Floo Powder"), "The items give subcommand should succeed");
        assertEquals(64, TestCommon.amountInPlayerInventory(admin2, Ollivanders2.flooPowderMaterial, "Floo Powder"),
                "An amount over a stack should be clamped to a full stack"); // 64 is the max Minecraft stack size

        // -- an unparseable amount fails with a message and gives nothing --
        TestCommon.clearMessageQueue(admin);
        assertFalse(admin.performCommand("Ollivanders2 items give abc Floo Powder"),
                "An unparseable amount should fail the command");
        String parseFailMessage = admin.nextMessage();
        assertNotNull(parseFailMessage, "An unparseable amount should message the player");
        assertTrue(parseFailMessage.contains("Unable to parse amount"), "The message should say the amount could not be parsed");

        // -- an unknown item name reports it was not found --
        TestCommon.clearMessageQueue(admin);
        assertTrue(admin.performCommand("Ollivanders2 items give 1 No Such Item"),
                "An unknown item name should not fail the command");
        String notFoundMessage = admin.nextMessage();
        assertNotNull(notFoundMessage, "An unknown item name should message the player");
        assertTrue(notFoundMessage.contains("Unable to find an item"), "The message should say the item was not found");
    }

    /**
     * The floo subcommand gives floo powder to the sender or a named player, and reports an unknown player
     * name.
     */
    @Test
    void flooCommandTest() {
        PlayerMock admin = mockServer.addPlayer();
        admin.setOp(true);
        PlayerMock target = mockServer.addPlayer();
        target.setOp(false);

        // -- floo with no args gives the sender floo powder --
        assertTrue(admin.performCommand("Ollivanders2 floo"), "The floo subcommand should succeed");
        assertEquals(8, TestCommon.amountInPlayerInventory(admin, Ollivanders2.flooPowderMaterial, "Floo Powder"),
                "The sender should be given floo powder"); // the floo subcommand gives 8 floo powder

        // -- floo with a player name gives that player floo powder --
        assertTrue(admin.performCommand("Ollivanders2 floo " + target.getName()),
                "The floo subcommand with a target should succeed");
        assertEquals(8, TestCommon.amountInPlayerInventory(target, Ollivanders2.flooPowderMaterial, "Floo Powder"),
                "The target should be given floo powder");

        // -- floo with an unknown player name reports it --
        TestCommon.clearMessageQueue(admin);
        assertTrue(admin.performCommand("Ollivanders2 floo NoSuchPlayer"),
                "The floo subcommand with an unknown target should still succeed");
        String message = admin.nextMessage();
        assertNotNull(message, "An unknown target should message the sender");
        assertTrue(message.contains("Unable to find player"), "The message should say the player was not found");
    }

    /**
     * The debug subcommand toggles the plugin debug flag on and off.
     */
    @Test
    void debugCommandTest() {
        PlayerMock admin = mockServer.addPlayer();
        admin.setOp(true);

        boolean originalDebug = Ollivanders2.debug;
        try {
            assertTrue(admin.performCommand("Ollivanders2 debug"), "The debug subcommand should succeed");
            assertEquals(!originalDebug, Ollivanders2.debug, "The debug subcommand should toggle the debug flag");

            assertTrue(admin.performCommand("Ollivanders2 debug"), "The debug subcommand should succeed");
            assertEquals(originalDebug, Ollivanders2.debug, "A second toggle should restore the debug flag");
        }
        finally {
            Ollivanders2.debug = originalDebug; // never leak a debug change to parallel tests
        }
    }

    /**
     * The house subcommand shows its usage message when called with no option and lists the houses for the
     * list option. Sorting and points behavior is covered by the O2Houses tests.
     */
    @Test
    void houseCommandTest() {
        PlayerMock admin = mockServer.addPlayer();
        admin.setOp(true);

        String usageMessage = TestCommon.runCommand(admin, "Ollivanders2 house", mockServer);
        assertNotNull(usageMessage, "The house subcommand with no option should show usage");
        assertTrue(usageMessage.contains("house"), "The usage message should describe the house subcommand");

        TestCommon.clearMessageQueue(admin);
        String listMessage = TestCommon.runCommand(admin, "Ollivanders2 house list", mockServer);
        assertNotNull(listMessage, "The house list subcommand should send the house list");
        assertTrue(listMessage.contains("Hufflepuff"), "The house list should contain the house names");
    }

    /**
     * The potions subcommand lists potions and ingredients, gives the sender a potion by name prefix, gives
     * a named player a potion, gives the sender a named ingredient, gives the sender one of every potion,
     * and reports an unknown potion name.
     */
    @Test
    void potionsCommandTest() {
        PlayerMock admin = mockServer.addPlayer();
        admin.setOp(true);
        PlayerMock target = mockServer.addPlayer();
        target.setOp(false);

        // -- potions list --
        String listMessage = TestCommon.runCommand(admin, "Ollivanders2 potions list", mockServer);
        assertNotNull(listMessage, "The potions list subcommand should send the list");
        assertTrue(listMessage.contains("Potions:"), "The potions list should be sent to the player");

        // -- potions ingredient list --
        TestCommon.clearMessageQueue(admin);
        String ingredientMessage = TestCommon.runCommand(admin, "Ollivanders2 potions ingredient list", mockServer);
        assertNotNull(ingredientMessage, "The ingredient list subcommand should send the list");
        assertTrue(ingredientMessage.contains("Ingredients:"), "The ingredient list should be sent to the player");

        // take the material and display name from production so the test does not hard-code them - Wiggenweld
        // is a splash potion, so its material is not the drinkable POTION
        ItemStack expectedPotion = Ollivanders2API.getPotions().getPotionItemStackByType(O2PotionType.WIGGENWELD_POTION, 1);
        assertNotNull(expectedPotion, "The Wiggenweld potion item should be creatable");
        Material potionMaterial = expectedPotion.getType();
        String potionName = O2PotionType.WIGGENWELD_POTION.getPotionName();

        // -- potions with a potion name prefix gives the sender that potion --
        assertTrue(admin.performCommand("Ollivanders2 potions Wiggenweld"), "The potions subcommand should succeed");
        assertNotNull(TestCommon.getPlayerInventoryItem(admin, potionMaterial, potionName),
                "The sender should be given the named potion");

        // -- potions give gives a named player the potion --
        assertTrue(admin.performCommand("Ollivanders2 potions give " + target.getName() + " Wiggenweld"),
                "The potions give subcommand should succeed");
        assertNotNull(TestCommon.getPlayerInventoryItem(target, potionMaterial, potionName),
                "The target should be given the named potion");

        // -- potions ingredient with an ingredient name gives the sender that ingredient --
        PlayerMock admin2 = mockServer.addPlayer();
        admin2.setOp(true);
        String ingredientName = O2Potions.getAllIngredientNames().getFirst(); // any real ingredient name works
        assertTrue(admin2.performCommand("Ollivanders2 potions ingredient " + ingredientName),
                "The potions ingredient subcommand should succeed");
        assertEquals(1, countInventoryItems(admin2), "The sender should be given the named ingredient");

        // -- potions all gives the sender one of every potion --
        PlayerMock admin3 = mockServer.addPlayer();
        admin3.setOp(true);
        assertTrue(admin3.performCommand("Ollivanders2 potions all"), "The potions all subcommand should succeed");
        assertEquals(Ollivanders2API.getPotions().getAllPotions().size(), countInventoryItems(admin3),
                "The sender should be given one of every potion");

        // -- an unknown potion name reports it was not found --
        TestCommon.clearMessageQueue(admin);
        assertTrue(admin.performCommand("Ollivanders2 potions No Such Potion"),
                "An unknown potion name should not fail the command");
        String notFoundMessage = admin.nextMessage();
        assertNotNull(notFoundMessage, "An unknown potion name should message the player");
        assertTrue(notFoundMessage.contains("Unable to find potion"), "The message should say the potion was not found");
    }

    /**
     * The apparateLoc subcommand tells the sender the feature is off when apparate locations are not enabled
     * in config.
     */
    @Test
    void apparateLocCommandTest() {
        PlayerMock admin = mockServer.addPlayer();
        admin.setOp(true);

        String message = TestCommon.runCommand(admin, "Ollivanders2 apparate", mockServer);
        assertNotNull(message, "The apparateLoc subcommand should message the sender");
        assertTrue(message.contains("not currently enabled"),
                "The message should say apparate locations are not enabled");
    }

    /**
     * The reload subcommand reloads the config and confirms to the sender.
     */
    @Test
    void reloadCommandTest() {
        PlayerMock admin = mockServer.addPlayer();
        admin.setOp(true);

        String message = TestCommon.runCommand(admin, "Ollivanders2 reload", mockServer);
        assertNotNull(message, "The reload subcommand should message the sender");
        assertEquals("Config reloaded", TestCommon.cleanChatMessage(message),
                "The reload subcommand should confirm the reload");
    }

    /**
     * canCast allows a spell with no restrictions, blocks a spell on cooldown, and honors an explicitly set
     * spell permission - denied blocks the cast, granted allows it. The animagus form restriction is covered
     * by the animagus effect tests.
     */
    @Test
    void canCastTest() {
        PlayerMock player = mockServer.addPlayer();
        player.setOp(false);

        // ensure the player has an O2Player record - canCast fails without one
        O2Player o2p = testPlugin.getO2Player(player);
        assertNotNull(o2p, "getO2Player should create the player record");

        // -- a spell with no restrictions can be cast --
        assertTrue(testPlugin.canCast(player, O2SpellType.LUMOS, false),
                "A spell with no restrictions should be castable");

        // -- a spell on cooldown cannot be cast --
        o2p.setSpellRecentCastTime(O2SpellType.LUMOS);
        assertFalse(testPlugin.canCast(player, O2SpellType.LUMOS, false),
                "A spell on cooldown should not be castable");
        assertTrue(testPlugin.canCast(player, O2SpellType.NOX, false),
                "A different spell should not be affected by the cooldown");

        // -- an explicitly denied spell permission blocks the cast --
        player.addAttachment(testPlugin, "Ollivanders2." + O2SpellType.ALOHOMORA, false);
        assertFalse(testPlugin.canCast(player, O2SpellType.ALOHOMORA, false),
                "A denied spell permission should block the cast");

        // -- an explicitly granted spell permission allows the cast --
        player.addAttachment(testPlugin, "Ollivanders2." + O2SpellType.ALOHOMORA, true);
        assertTrue(testPlugin.canCast(player, O2SpellType.ALOHOMORA, false),
                "A granted spell permission should allow the cast");
    }

    /**
     * The spell count accessors round-trip: a fresh player has count 0, setSpellCount stores the count, and
     * incrementSpellCount adds one and returns the new count.
     */
    @Test
    void spellCountTest() {
        PlayerMock player = mockServer.addPlayer();

        assertEquals(0, testPlugin.getSpellCount(player, O2SpellType.LUMOS),
                "A fresh player should have a spell count of 0");

        testPlugin.setSpellCount(player, O2SpellType.LUMOS, 5);
        assertEquals(5, testPlugin.getSpellCount(player, O2SpellType.LUMOS),
                "setSpellCount should store the count");

        assertEquals(6, testPlugin.incrementSpellCount(player, O2SpellType.LUMOS),
                "incrementSpellCount should return the incremented count");
        assertEquals(6, testPlugin.getSpellCount(player, O2SpellType.LUMOS),
                "incrementSpellCount should store the incremented count");
    }

    /**
     * The O2Player accessors: getO2Player creates a record for a new player, the player's UUID appears in the
     * ID list, and setO2Player replaces the record.
     */
    @Test
    void o2PlayerAccessorsTest() {
        PlayerMock player = mockServer.addPlayer();

        O2Player o2p = testPlugin.getO2Player(player);
        assertNotNull(o2p, "getO2Player should create a record for a new player");
        assertSame(o2p, testPlugin.getO2Player(player), "getO2Player should return the same record on lookup");

        assertTrue(testPlugin.getO2PlayerIDs().contains(player.getUniqueId()),
                "The player IDs should include the new player");

        O2Player replacement = new O2Player(player.getUniqueId(), player.getName(), testPlugin);
        testPlugin.setO2Player(player, replacement);
        assertSame(replacement, testPlugin.getO2Player(player), "setO2Player should replace the record");
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
