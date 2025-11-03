package ollivanders.house;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.house.O2HouseType;
import net.pottercraft.ollivanders2.house.O2Houses;
import net.pottercraft.ollivanders2.house.events.OllivandersPlayerSortedEvent;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.scoreboard.ObjectiveMock;
import org.mockbukkit.mockbukkit.scoreboard.ScoreboardMock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class O2HousesTest {
    // Constants
    private static final String SCOREBOARD_OBJECTIVE = "o2_hpoints";
    private static final int INITIAL_POINTS = 0;

    static World testWorld;
    static Ollivanders2 testPlugin;
    static ServerMock mockServer;
    static O2Houses testHouses;

    static PlayerMock player1;
    static final String player1Name = "Bob";
    static PlayerMock player2;
    static final String player2Name = "Fred";
    static PlayerMock player3;
    static final String player3Name = "Joe";

    // Helper methods
    private void assertPlayerInHouse(PlayerMock player, O2HouseType house, String message) {
        assertTrue(testHouses.getHouseMembers(house).contains(player.getName()), message);
    }

    private void assertPlayerNotInHouse(PlayerMock player, O2HouseType house, String message) {
        assertFalse(testHouses.getHouseMembers(house).contains(player.getName()), message);
    }

    private void assertHouseScore(O2HouseType house, int expectedScore, String message) {
        assertEquals(expectedScore, house.getScore(), message);
    }

    private void assertScoreboardScore(ObjectiveMock objective, O2HouseType house, int expectedScore, String message) {
        assertEquals(expectedScore, objective.getScore(house.getName()).getScore(), message);
    }

    @BeforeEach
    void setUp () {
        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/houses_config.yml"));

        // set up world
        testWorld = mockServer.addSimpleWorld("world");

        // get O2Houses
        testHouses = Ollivanders2API.getHouses();
        testHouses.reset();

        // create some players we will need
        player1 = mockServer.addPlayer(player1Name);
        player2 = mockServer.addPlayer(player2Name);
        player3 = mockServer.addPlayer(player3Name);
    }

    /**
     * Test that the initial state of the house is what we expect.
     */
    @Test
    void testHouseSettings() {
        assertTrue(O2Houses.useHouses, "Houses should be enabled");
        assertEquals(4, testHouses.getAllHouseNames().size(), "Should have 4 houses");
        assertTrue(testHouses.getAllHouseNames().contains("Gryffindor"), "Should contain Gryffindor house");

        O2HouseType gryffindor = testHouses.getHouseType("Gryffindor");
        assertNotNull(gryffindor, "Gryffindor house should exist");
        assertHouseScore(gryffindor, INITIAL_POINTS, "Gryffindor should start with 0 points");
        assertEquals(0, testHouses.getHouseMembers(gryffindor).size(), "Gryffindor should start with 0 members");
    }

    /**
     * Test sorting and house membership
     */
    @Test
    void testSorting() {
        // player1 is sorted in to ravenclaw
        assertTrue(testHouses.sort(player1, O2HouseType.RAVENCLAW), "Player1 should be sorted into Ravenclaw");
        assertPlayerInHouse(player1, O2HouseType.RAVENCLAW, "Ravenclaw should contain player1");

        // player does not appear in any other house
        assertPlayerNotInHouse(player1, O2HouseType.HUFFLEPUFF, "Hufflepuff should not contain player1");
        assertPlayerNotInHouse(player1, O2HouseType.GRYFFINDOR, "Gryffindor should not contain player1");
        assertPlayerNotInHouse(player1, O2HouseType.SLYTHERIN, "Slytherin should not contain player1");

        // a sort event is fired for player being sorted
        assertTrue(testHouses.sort(player2, O2HouseType.HUFFLEPUFF), "Player2 should be sorted into Hufflepuff");
        assertTrue(mockServer.getPluginManager()
                .getFiredEvents()
                .filter(e -> e instanceof OllivandersPlayerSortedEvent)
                .map(e -> (OllivandersPlayerSortedEvent) e)
                .anyMatch(ev -> ev.getPlayer().getName().equals(player2Name)),
                "Sort event should have been fired for player2"
        );

        // a player cannot be sorted again in to their own house
        assertFalse(testHouses.sort(player1, O2HouseType.RAVENCLAW),
                "Player1 should not be able to be sorted into Ravenclaw again");
        assertFalse(testHouses.sort(player2, O2HouseType.HUFFLEPUFF),
                "Player2 should not be able to be sorted into Hufflepuff again");

        // a player cannot be sorted in to another house
        assertFalse(testHouses.sort(player1, O2HouseType.HUFFLEPUFF),
                "Player1 should not be able to be sorted into a different house");
        assertFalse(testHouses.sort(player2, O2HouseType.RAVENCLAW),
                "Player2 should not be able to be sorted into a different house");

        // players can be unsorted
        assertPlayerInHouse(player1, O2HouseType.RAVENCLAW, "Ravenclaw should contain player1 before unsorting");
        testHouses.unsort(player1);
        assertPlayerNotInHouse(player1, O2HouseType.RAVENCLAW, "Ravenclaw should not contain player1 after unsorting");

        assertPlayerInHouse(player2, O2HouseType.HUFFLEPUFF, "Hufflepuff should contain player2 before unsorting");
        testHouses.unsort(player2);
        assertPlayerNotInHouse(player2, O2HouseType.HUFFLEPUFF, "Hufflepuff should not contain player2 after unsorting");

        // a player can be sorted in to a new house
        assertTrue(testHouses.sort(player1, O2HouseType.HUFFLEPUFF),
                "Player1 should be able to be sorted into a new house after being unsorted");

        // a player can ge resorted in to their previous house
        assertTrue(testHouses.sort(player2, O2HouseType.HUFFLEPUFF),
                "Player2 should be able to be resorted into their previous house");
    }

    /**
     * Test isSorted() and getHouse() by name and UUID
     */
    @Test
    void testIsSortedAndGetHouse() {
        assertTrue(testHouses.sort(player1, O2HouseType.RAVENCLAW), "Player1 should sort into Ravenclaw");
        assertTrue(testHouses.sort(player2, O2HouseType.HUFFLEPUFF), "Player2 should sort into Hufflepuff");

        // isSorted check works by player
        assertTrue(testHouses.isSorted(player1), "Player1 should be sorted");
        // isSorted check works by UUID
        assertTrue(testHouses.isSorted(player2.getUniqueId()), "Player2 should be sorted (checked by UUID)");
        assertFalse(testHouses.isSorted(player3), "Player3 should not be sorted");

        // getHouse works by player
        assertSame(O2HouseType.RAVENCLAW, testHouses.getHouse(player1), "Player1 should be in Ravenclaw");
        // getHouse works by UUID
        assertSame(O2HouseType.HUFFLEPUFF, testHouses.getHouse(player2.getUniqueId()),
                "Player2 should be in Hufflepuff (checked by UUID)");
        // getHouse returns null when the player is not sorted
        assertNull(testHouses.getHouse(player3), "Player3 should not have a house");
    }

    /**
     * Test force sorting
     */
    @Test
    void testForceSorting() {
        assertTrue(testHouses.sort(player1, O2HouseType.RAVENCLAW), "Player1 should sort into Ravenclaw");

        // a player who is already sorted will have their house changed
        testHouses.forceSetHouse(player1, O2HouseType.RAVENCLAW);
        assertPlayerInHouse(player1, O2HouseType.RAVENCLAW, "Player1 should still be in Ravenclaw after force sorting");

        // a player who is not sorted will get sorted
        testHouses.forceSetHouse(player3, O2HouseType.RAVENCLAW);
        assertPlayerInHouse(player3, O2HouseType.RAVENCLAW, "Player3 should be in Ravenclaw after force sorting");
    }

    /**
     * Test house points
     */
    @Test
    void testHousePoints() {
        // set house points to an explicit value
        assertTrue(testHouses.setHousePoints(O2HouseType.RAVENCLAW, 5), "Should be able to set Ravenclaw points to 5");
        assertHouseScore(O2HouseType.RAVENCLAW, 5, "Ravenclaw should have 5 points");
        assertTrue(testHouses.setHousePoints(O2HouseType.HUFFLEPUFF, 10), "Should be able to set Hufflepuff points to 10");
        assertHouseScore(O2HouseType.HUFFLEPUFF, 10, "Hufflepuff should have 10 points");

        // add points
        assertTrue(testHouses.addHousePoints(O2HouseType.RAVENCLAW, 10), "Should be able to add 10 points to Ravenclaw");
        assertHouseScore(O2HouseType.RAVENCLAW, 15, "Ravenclaw should have 15 points after adding 10");

        // subtract points
        assertTrue(testHouses.subtractHousePoints(O2HouseType.HUFFLEPUFF, 5), "Should be able to subtract 5 points from Hufflepuff");
        assertHouseScore(O2HouseType.HUFFLEPUFF, 5, "Hufflepuff should have 5 points after subtracting 5");
    }

    /**
     * Test that the scoreboard works
     */
    @Test
    void testScoreboard() {
        // scoreboard has expected objective
        ObjectiveMock mockObjective = mockServer.getScoreboardManager().getMainScoreboard().getObjective(SCOREBOARD_OBJECTIVE);
        assertNotNull(mockObjective, "House points objective should exist");

        // scoreboard has expected teams
        ScoreboardMock mockScoreboard = mockObjective.getScoreboard();
        assertNotNull(mockScoreboard, "Scoreboard should exist");

        assertNotNull(mockScoreboard.getTeam(O2HouseType.GRYFFINDOR.getName()), "Gryffindor team should exist");
        assertNotNull(mockScoreboard.getTeam(O2HouseType.HUFFLEPUFF.getName()), "Hufflepuff team should exist");
        assertNotNull(mockScoreboard.getTeam(O2HouseType.RAVENCLAW.getName()), "Ravenclaw team should exist");
        assertNotNull(mockScoreboard.getTeam(O2HouseType.SLYTHERIN.getName()), "Slytherin team should exist");

        // scores start at 0
        assertScoreboardScore(mockObjective, O2HouseType.GRYFFINDOR, INITIAL_POINTS,
                "Gryffindor should start with 0 points on scoreboard");
        assertScoreboardScore(mockObjective, O2HouseType.SLYTHERIN, INITIAL_POINTS,
                "Slytherin should start with 0 points on scoreboard");

        // scores change when points added/removed/set
        testHouses.addHousePoints(O2HouseType.GRYFFINDOR, 10);
        assertScoreboardScore(mockObjective, O2HouseType.GRYFFINDOR, 10,
                "Gryffindor should have 10 points on scoreboard after adding 10");
        assertScoreboardScore(mockObjective, O2HouseType.SLYTHERIN, INITIAL_POINTS,
                "Slytherin should still have 0 points on scoreboard");
        testHouses.subtractHousePoints(O2HouseType.GRYFFINDOR, 5);
        assertScoreboardScore(mockObjective, O2HouseType.GRYFFINDOR, 5,
                "Gryffindor should have 5 points on scoreboard after subtracting 5");
        assertScoreboardScore(mockObjective, O2HouseType.SLYTHERIN, INITIAL_POINTS,
                "Slytherin should still have 0 points on scoreboard");

        // resetting scores sets them all back to 0
        testHouses.resetHousePoints();
        assertScoreboardScore(mockObjective, O2HouseType.GRYFFINDOR, INITIAL_POINTS,
                "Gryffindor should have 0 points on scoreboard after reset");
        assertScoreboardScore(mockObjective, O2HouseType.SLYTHERIN, INITIAL_POINTS,
                "Slytherin should have 0 points on scoreboard after reset");
    }

    /**
     * Test house place text works.
     */
    @Test
    void testHousePlaceText() {
        assertTrue(testHouses.setHousePoints(O2HouseType.RAVENCLAW, 15), "Should set Ravenclaw points");
        assertTrue(testHouses.setHousePoints(O2HouseType.HUFFLEPUFF, 10), "Should set Hufflepuff points");
        assertTrue(testHouses.setHousePoints(O2HouseType.SLYTHERIN, 5), "Should set Slytherin points");
        assertTrue(testHouses.setHousePoints(O2HouseType.GRYFFINDOR, 5), "Should set Gryffindor points");

        assertEquals("in 1st place", O2HouseType.getHousePlaceText(O2HouseType.RAVENCLAW),
                "Ravenclaw with 15 points should be in 1st place");
        assertEquals("tied for 3rd place", O2HouseType.getHousePlaceText(O2HouseType.SLYTHERIN),
                "Slytherin with 5 points should be tied for 3rd place");
    }

    @AfterEach
    void tearDown () {
        MockBukkit.unmock();
    }
}
