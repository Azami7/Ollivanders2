package net.pottercraft.ollivanders2.test.player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.player.O2Players;
import net.pottercraft.ollivanders2.player.Year;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link O2Players}.
 */
public class O2PlayersTest {
    static Ollivanders2 testPlugin;
    static ServerMock mockServer;

    /**
     * Dedicated to the save/load round-trip test.
     */
    static PlayerMock player1;

    /**
     * Dedicated to the command tests.
     */
    static PlayerMock player2;

    /**
     * Dedicated to the animagus command test.
     */
    static PlayerMock player3;

    /**
     * The subject of the summary command test.
     */
    static PlayerMock player4;

    /**
     * The second player in the summary command test, used as both an admin sender and a summary target.
     */
    static PlayerMock player5;

    /**
     * Dedicated to the year command test.
     */
    static PlayerMock player6;

    @BeforeAll
    static void globalSetUp() {
        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        mockServer.addSimpleWorld("world");

        player1 = mockServer.addPlayer();
        player2 = mockServer.addPlayer();
        player3 = mockServer.addPlayer();
        player4 = mockServer.addPlayer();
        player5 = mockServer.addPlayer();
        player6 = mockServer.addPlayer();
    }

    /**
     * Players can be added and looked up by UUID and by case-insensitive name; unknown players return null.
     */
    @Test
    void testAddAndGetPlayer() {
        // a private O2Players instance so this test does not disturb the plugin's shared player map
        O2Players players = new O2Players(testPlugin);
        UUID pid = UUID.randomUUID();

        assertNull(players.getPlayer(pid), "Unknown UUID should return null");
        assertNull(players.getPlayer("Nobody"), "Unknown name should return null");

        O2Player o2p = players.addPlayer(pid, "TestWizard");
        assertNotNull(o2p, "addPlayer should return the new player");

        assertSame(o2p, players.getPlayer(pid), "Player should be found by UUID");
        assertSame(o2p, players.getPlayer("TestWizard"), "Player should be found by name");
        assertSame(o2p, players.getPlayer("testwizard"), "Player lookup by name should be case-insensitive");

        assertTrue(players.getPlayerIDs().contains(pid), "Player IDs should include the added player");

        // updatePlayer replaces the record for a UUID
        O2Player replacement = new O2Player(pid, "RenamedWizard", testPlugin);
        players.updatePlayer(pid, replacement);
        assertSame(replacement, players.getPlayer(pid), "updatePlayer should replace the record");
    }

    /**
     * Player data survives a save/load round trip: identity, wand, souls, muggle status, year, animagus form,
     * spell and potion experience, and the wand master spell.
     */
    @Test
    void testSaveLoadRoundTrip() {
        final int masteryCount = 100; // spells are mastered at 100 casts
        O2Players savedPlayers = new O2Players(testPlugin);

        O2Player original = savedPlayers.addPlayer(player1.getUniqueId(), player1.getName());
        original.setSouls(3);
        original.setMuggle(false);
        original.setYear(Year.YEAR_3);
        original.setAnimagusForm(EntityType.COW);
        original.setSpellCount(O2SpellType.NOX, 42);
        // one mastered spell so the master spell restore cannot depend on save-file ordering
        original.setSpellCount(O2SpellType.LUMOS, masteryCount);
        original.setPotionCount(O2PotionType.CURE_FOR_BOILS, 7);
        original.setSpellRecentCastTime(O2SpellType.NOX);

        String destinedWood = original.getDestinedWandWood();
        String destinedCore = original.getDestinedWandCore();

        savedPlayers.saveO2Players();

        // load into a fresh O2Players instance, as happens at plugin enable
        O2Players loadedPlayers = new O2Players(testPlugin);
        loadedPlayers.loadO2Players();

        O2Player loaded = loadedPlayers.getPlayer(player1.getUniqueId());
        assertNotNull(loaded, "Saved player should be restored on load");

        assertEquals(player1.getName(), loaded.getPlayerName(), "Player name should round trip");
        assertEquals(destinedWood, loaded.getDestinedWandWood(), "Destined wand wood should round trip");
        assertEquals(destinedCore, loaded.getDestinedWandCore(), "Destined wand core should round trip");
        assertEquals(3, loaded.getSouls(), "Souls should round trip");
        assertFalse(loaded.isMuggle(), "Muggle status should round trip");
        assertSame(Year.YEAR_3, loaded.getYear(), "Year should round trip");
        assertSame(EntityType.COW, loaded.getAnimagusForm(), "Animagus form should round trip");
        assertEquals(42, loaded.getSpellCount(O2SpellType.NOX), "Spell count should round trip");
        assertEquals(masteryCount, loaded.getSpellCount(O2SpellType.LUMOS), "Mastered spell count should round trip");
        assertSame(O2SpellType.LUMOS, loaded.getMasterSpell(), "Master spell should round trip");
        assertEquals(7, loaded.getPotionCount(O2PotionType.CURE_FOR_BOILS), "Potion count should round trip");
        assertSame(O2SpellType.NOX, loaded.getLastSpell(), "Last spell should round trip");
    }

    /**
     * The summary command tells a muggle they are a muggle and lists a wizard's known spells.
     */
    @Test
    void testCommands() {
        O2Players players = new O2Players(testPlugin);
        players.addPlayer(player2.getUniqueId(), player2.getName());

        // summary for a muggle
        TestCommon.clearMessageQueue(player2);
        players.playerSummary(player2, player2);
        String summary = TestCommon.getWholeMessage(player2);
        assertTrue(summary.contains("Muggle"), "Summary for a muggle should say they are a muggle");

        // summary for a wizard lists known spells
        O2Player o2p = players.getPlayer(player2.getUniqueId());
        assertNotNull(o2p, "O2Player should exist for player2");
        o2p.setMuggle(false);
        o2p.setSpellCount(O2SpellType.LUMOS, 4);

        TestCommon.clearMessageQueue(player2);
        players.playerSummary(player2, player2);
        summary = TestCommon.getWholeMessage(player2);
        assertNotNull(summary);
        assertTrue(summary.contains("Wizard"), "Summary for a wizard should say they are a wizard");
        assertTrue(summary.contains(O2SpellType.LUMOS.getSpellName()), "Summary should list known spells");
    }

    /**
     * The year command requires admin permission, reports when years are disabled, and otherwise tells, sets,
     * promotes, and demotes a player's year, rejecting unknown players, invalid years, and out of range promotions
     * and demotions.
     * <p>
     * Kept in one method because it toggles the global {@link Ollivanders2#useYears} setting, which every other
     * year-aware test reads.
     */
    @Test
    void runYearTest() {
        // a private O2Players instance so this test does not disturb the plugin's shared player map
        O2Players players = new O2Players(testPlugin);
        players.addPlayer(player6.getUniqueId(), player6.getName());
        O2Player o2p = players.getPlayer(player6.getUniqueId());
        assertNotNull(o2p, "O2Player should exist for player6");
        String name = player6.getName();

        boolean originalUseYears = Ollivanders2.useYears;

        try {
            // non-admins cannot run the year command
            player6.setOp(false);
            assertFalse(players.runYear(player6, new String[]{"year", name}),
                    "Year command should not be handled for a non-admin");

            player6.setOp(true); // year commands require admin permission

            // years disabled
            Ollivanders2.useYears = false;
            TestCommon.clearMessageQueue(player6);
            assertTrue(players.runYear(player6, new String[]{"year", name}), "Year command should be handled");
            String wholeMessage = TestCommon.getWholeMessage(player6);
            assertNotNull(wholeMessage);
            assertTrue(wholeMessage.contains("not currently enabled"),
                    "Year command should report that years are disabled");

            // tell the year of a player
            Ollivanders2.useYears = true;
            o2p.setYear(Year.YEAR_3);
            TestCommon.clearMessageQueue(player6);
            assertTrue(players.runYear(player6, new String[]{"year", name}), "Year command should be handled");
            wholeMessage = TestCommon.getWholeMessage(player6);
            assertNotNull(wholeMessage);
            assertTrue(wholeMessage.contains(Year.YEAR_3.getDisplayText()),
                    "Year command should report the player's year");

            // unknown player, empty player name, and no player name all fail gracefully
            Ollivanders2.useYears = true;
            TestCommon.clearMessageQueue(player6);
            assertTrue(players.runYear(player6, new String[]{"year", "Nobody"}), "Year command should be handled");
            wholeMessage = TestCommon.getWholeMessage(player6);
            assertNotNull(wholeMessage);
            assertTrue(wholeMessage.contains("Unable to find player"),
                    "Year command for an unknown player should report the player could not be found");

            TestCommon.clearMessageQueue(player6);
            assertTrue(players.runYear(player6, new String[]{"year", ""}), "Year command should be handled");
            wholeMessage = TestCommon.getWholeMessage(player6);
            assertNotNull(wholeMessage);
            assertTrue(wholeMessage.contains("Year commands"),
                    "Year command with an empty player name should print usage");

            TestCommon.clearMessageQueue(player6);
            assertTrue(players.runYear(player6, new String[]{"year"}), "Year command should be handled");
            wholeMessage = TestCommon.getWholeMessage(player6);
            assertNotNull(wholeMessage);
            assertTrue(wholeMessage.contains("Year commands"),
                    "Year command with no arguments should print usage");

            // an unrecognized subcommand prints usage
            TestCommon.clearMessageQueue(player6);
            assertTrue(players.runYear(player6, new String[]{"year", "expel", name}), "Year command should be handled");
            wholeMessage = TestCommon.getWholeMessage(player6);
            assertNotNull(wholeMessage);
            assertTrue(wholeMessage.contains("Year commands"),
                    "Year command with an unknown subcommand should print usage");

            // set
            Ollivanders2.useYears = true;
            TestCommon.clearMessageQueue(player6);
            assertTrue(players.runYear(player6, new String[]{"year", "set", name}), "Year command should be handled");
            wholeMessage = TestCommon.getWholeMessage(player6);
            assertNotNull(wholeMessage);
            assertTrue(wholeMessage.contains("Year commands"),
                    "Set with no year should print usage");

            TestCommon.clearMessageQueue(player6);
            assertTrue(players.runYear(player6, new String[]{"year", "set", name, "5"}), "Year command should be handled");
            wholeMessage = TestCommon.getWholeMessage(player6);
            assertNotNull(wholeMessage);
            assertTrue(wholeMessage.contains(Year.YEAR_5.getDisplayText()),
                    "Set should report the new year");
            assertSame(Year.YEAR_5, o2p.getYear(), "Set should change the player's year");

            TestCommon.clearMessageQueue(player6);
            assertTrue(players.runYear(player6, new String[]{"year", "set", "Nobody", "5"}), "Year command should be handled");
            wholeMessage = TestCommon.getWholeMessage(player6);
            assertNotNull(wholeMessage);
            assertTrue(wholeMessage.contains("Unable to find a player named Nobody"),
                    "Set for an unknown player should report the player could not be found");

            TestCommon.clearMessageQueue(player6);
            assertTrue(players.runYear(player6, new String[]{"year", "set", name, ""}), "Year command should be handled");
            wholeMessage = TestCommon.getWholeMessage(player6);
            assertNotNull(wholeMessage);
            assertTrue(wholeMessage.contains("Year commands"),
                    "Set with an empty year should print usage");

            // years are 1-7, so 8 and a non-number are both invalid
            TestCommon.clearMessageQueue(player6);
            assertTrue(players.runYear(player6, new String[]{"year", "set", name, "8"}), "Year command should be handled");
            wholeMessage = TestCommon.getWholeMessage(player6);
            assertNotNull(wholeMessage);
            assertTrue(wholeMessage.contains("is not a valid year"),
                    "Set to a year above the maximum should report an invalid year");

            TestCommon.clearMessageQueue(player6);
            assertTrue(players.runYear(player6, new String[]{"year", "set", name, "third"}), "Year command should be handled");
            wholeMessage = TestCommon.getWholeMessage(player6);
            assertNotNull(wholeMessage);
            assertTrue(wholeMessage.contains("is not a valid year"),
                    "Set to a non-numeric year should report an invalid year");
            assertSame(Year.YEAR_5, o2p.getYear(), "An invalid year should not change the player's year");

            // promote and demote
            Ollivanders2.useYears = true;
            TestCommon.clearMessageQueue(player6);
            assertTrue(players.runYear(player6, new String[]{"year", "promote", name}), "Year command should be handled");
            wholeMessage = TestCommon.getWholeMessage(player6);
            assertNotNull(wholeMessage);
            assertTrue(wholeMessage.contains(Year.YEAR_6.getDisplayText()),
                    "Promote should report the new year");
            assertSame(Year.YEAR_6, o2p.getYear(), "Promote should raise the player's year by one");

            TestCommon.clearMessageQueue(player6);
            assertTrue(players.runYear(player6, new String[]{"year", "demote", name}), "Year command should be handled");
            wholeMessage = TestCommon.getWholeMessage(player6);
            assertNotNull(wholeMessage);
            assertTrue(wholeMessage.contains(Year.YEAR_5.getDisplayText()),
                    "Demote should report the new year");
            assertSame(Year.YEAR_5, o2p.getYear(), "Demote should lower the player's year by one");

            TestCommon.clearMessageQueue(player6);
            assertTrue(players.runYear(player6, new String[]{"year", "promote", "Nobody"}), "Year command should be handled");
            wholeMessage = TestCommon.getWholeMessage(player6);
            assertNotNull(wholeMessage);
            assertTrue(wholeMessage.contains("Unable to find player"),
                    "Promote for an unknown player should report the player could not be found");

            TestCommon.clearMessageQueue(player6);
            assertTrue(players.runYear(player6, new String[]{"year", "demote", "Nobody"}), "Year command should be handled");
            wholeMessage = TestCommon.getWholeMessage(player6);
            assertNotNull(wholeMessage);
            assertTrue(wholeMessage.contains("Unable to find player"),
                    "Demote for an unknown player should report the player could not be found");

            // promoting past the last year and demoting below the first are both rejected
            Ollivanders2.useYears = true;
            o2p.setYear(Year.YEAR_7);
            TestCommon.clearMessageQueue(player6);
            assertTrue(players.runYear(player6, new String[]{"year", "promote", name}), "Year command should be handled");
            wholeMessage = TestCommon.getWholeMessage(player6);
            assertNotNull(wholeMessage);
            assertTrue(wholeMessage.contains("is not a valid year"),
                    "Promoting past the last year should report an invalid year");
            assertSame(Year.YEAR_7, o2p.getYear(), "Promoting past the last year should not change the player's year");

            o2p.setYear(Year.YEAR_1);
            TestCommon.clearMessageQueue(player6);
            assertTrue(players.runYear(player6, new String[]{"year", "demote", name}), "Year command should be handled");
            wholeMessage = TestCommon.getWholeMessage(player6);
            assertNotNull(wholeMessage);
            assertTrue(wholeMessage.contains("is not a valid year"),
                    "Demoting below the first year should report an invalid year");
            assertSame(Year.YEAR_1, o2p.getYear(), "Demoting below the first year should not change the player's year");
        }
        finally {
            Ollivanders2.useYears = originalUseYears;
            player6.setOp(false);
        }
    }

    /**
     * The animagus command requires admin permission, reports usage for bad arguments or an unknown player,
     * makes a valid target an animagus, and is a no-op if they already are one.
     */
    @Test
    void runAnimagusTest() {
        // a private O2Players instance so this test does not disturb the plugin's shared player map
        O2Players players = new O2Players(testPlugin);
        players.addPlayer(player3.getUniqueId(), player3.getName());
        O2Player o2p = players.getPlayer(player3.getUniqueId());
        assertNotNull(o2p, "O2Player should exist for player3");

        // non-admins cannot run the animagus command
        player3.setOp(false);
        TestCommon.clearMessageQueue(player3);
        assertFalse(players.runAnimagus(player3, new String[]{"animagus", player3.getName()}),
                "Animagus command should not be handled for a non-admin");
        assertFalse(o2p.isAnimagus(), "A non-admin command should not have made the player an animagus");

        player3.setOp(true); // animagus commands require admin permission

        // wrong number of arguments prints usage
        TestCommon.clearMessageQueue(player3);
        assertTrue(players.runAnimagus(player3, new String[]{"animagus"}), "Animagus command should be handled");
        String wholeMessage = TestCommon.getWholeMessage(player3);
        assertNotNull(wholeMessage);
        assertTrue(wholeMessage.contains("Usage"),
                "Animagus command with no player name should print usage");

        // an unknown player prints usage
        TestCommon.clearMessageQueue(player3);
        assertTrue(players.runAnimagus(player3, new String[]{"animagus", "Nobody"}), "Animagus command should be handled");
        wholeMessage = TestCommon.getWholeMessage(player3);
        assertNotNull(wholeMessage);
        assertTrue(wholeMessage.contains("Usage"),
                "Animagus command for an unknown player should print usage");

        // a valid target becomes an animagus
        TestCommon.clearMessageQueue(player3);
        assertTrue(players.runAnimagus(player3, new String[]{"animagus", player3.getName()}), "Animagus command should be handled");
        wholeMessage = TestCommon.getWholeMessage(player3);
        assertNotNull(wholeMessage);
        assertTrue(wholeMessage.contains("is now an animagus"),
                "Animagus command should report the player is now an animagus");
        assertTrue(o2p.isAnimagus(), "Animagus command should have made the player an animagus");
        assertNotNull(o2p.getAnimagusForm(), "An animagus should have been given a form");

        // running it again is a no-op
        EntityType form = o2p.getAnimagusForm();
        TestCommon.clearMessageQueue(player3);
        assertTrue(players.runAnimagus(player3, new String[]{"animagus", player3.getName()}), "Animagus command should be handled");
        wholeMessage = TestCommon.getWholeMessage(player3);
        assertNotNull(wholeMessage);
        assertTrue(wholeMessage.contains("is already an animagus"),
                "Animagus command should report the player is already an animagus");
        assertSame(form, o2p.getAnimagusForm(), "A repeated animagus command should not change the animagus form");

        player3.setOp(false);
    }

    /**
     * The summary command summarizes the sender, lets an admin summarize another player, and prints usage for
     * an unknown or unpermitted target. The summary itself covers unknown players, wand and master spell, animagus
     * form, known spells and potions, the "nothing learned" wording for both self and other, and the admin-only
     * effects section.
     */
    @Test
    void runSummaryTest() {
        // a private O2Players instance so this test does not disturb the plugin's shared player map
        O2Players players = new O2Players(testPlugin);
        players.addPlayer(player4.getUniqueId(), player4.getName());
        players.addPlayer(player5.getUniqueId(), player5.getName());

        O2Player subject = players.getPlayer(player4.getUniqueId());
        O2Player other = players.getPlayer(player5.getUniqueId());
        assertNotNull(subject, "O2Player should exist for player4");
        assertNotNull(other, "O2Player should exist for player5");

        player4.setOp(false);
        player5.setOp(false);

        // a player this O2Players instance does not know cannot be summarized
        TestCommon.clearMessageQueue(player4);
        players.playerSummary(player4, player1);
        String wholeMessage = TestCommon.getWholeMessage(player4);
        assertNotNull(wholeMessage);
        assertTrue(wholeMessage.contains("Unable to find player"),
                "Summary for an unknown player should report the player could not be found");

        // a wizard who knows nothing is told so, in the first person for themselves...
        other.setMuggle(false);
        TestCommon.clearMessageQueue(player5);
        assertTrue(players.runSummary(player5, new String[]{"summary"}), "Summary command should be handled");
        String otherSummary = TestCommon.getWholeMessage(player5);
        assertNotNull(otherSummary);
        assertTrue(otherSummary.contains("You have not learned any spells"),
                "Self summary should use first person for having learned no spells");
        assertTrue(otherSummary.contains("You have not learned any potions"),
                "Self summary should use first person for having learned no potions");

        // ...and in the third person for someone else
        TestCommon.clearMessageQueue(player4);
        players.playerSummary(player4, player5);
        otherSummary = TestCommon.getWholeMessage(player4);
        assertNotNull(otherSummary);
        assertTrue(otherSummary.contains(player5.getName() + " has not learned any spells"),
                "Summary of another player should name them when they have learned no spells");
        assertTrue(otherSummary.contains(player5.getName() + " has not learned any potions"),
                "Summary of another player should name them when they have learned no potions");

        // give the subject everything the summary can report
        subject.setFoundWand(true); // also makes them a wizard
        subject.setMasterSpell(O2SpellType.LUMOS);
        subject.setSpellCount(O2SpellType.LUMOS, 4);
        subject.setAnimagusForm(EntityType.COW);
        subject.setPotionCount(O2PotionType.CURE_FOR_BOILS, 2);

        TestCommon.clearMessageQueue(player4);
        assertTrue(players.runSummary(player4, new String[]{"summary"}), "Summary command should be handled");
        String summary = TestCommon.getWholeMessage(player4);
        assertNotNull(summary);
        assertTrue(summary.contains("Wizard"), "Summary for a wizard should say they are a wizard");
        assertTrue(summary.contains("Wand Type"), "Summary should report the wand type once a wand is found");
        assertTrue(summary.contains(O2SpellType.LUMOS.getSpellName()), "Summary should report the master spell and known spells");
        assertTrue(summary.contains(Ollivanders2Common.enumRecode(EntityType.COW.toString())),
                "Summary should report the animagus form");
        if (Ollivanders2API.getPotions().isLoaded(O2PotionType.CURE_FOR_BOILS))
            assertTrue(summary.contains(O2PotionType.CURE_FOR_BOILS.getPotionName()), "Summary should list known potions");
        assertFalse(summary.contains("Affected by"), "A non-admin summary should not include the effects section");

        // an admin can summarize another player and sees the effects section
        player5.setOp(true); // summarizing another player requires admin permission
        TestCommon.clearMessageQueue(player5);
        assertTrue(players.runSummary(player5, new String[]{"summary", player4.getName()}), "Summary command should be handled");
        summary = TestCommon.getWholeMessage(player5);
        assertNotNull(summary);
        assertTrue(summary.contains("Wand Type"), "Admin summary of another player should report that player's info");
        assertTrue(summary.contains("Affected by"), "An admin summary should include the effects section");
        assertTrue(summary.contains("Nothing"), "An admin summary should report no effects for an unaffected player");

        // an unknown target prints usage
        TestCommon.clearMessageQueue(player5);
        assertTrue(players.runSummary(player5, new String[]{"summary", "Nobody"}), "Summary command should be handled");
        wholeMessage = TestCommon.getWholeMessage(player5);
        assertNotNull(wholeMessage);
        assertTrue(wholeMessage.contains("Usage"),
                "Summary command for an unknown player should print usage");

        // a non-admin cannot summarize another player
        player5.setOp(false);
        TestCommon.clearMessageQueue(player5);
        assertTrue(players.runSummary(player5, new String[]{"summary", player4.getName()}), "Summary command should be handled");
        wholeMessage = TestCommon.getWholeMessage(player5);
        assertNotNull(wholeMessage);
        assertTrue(wholeMessage.contains("Usage"),
                "Summary command for another player should print usage for a non-admin");
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
