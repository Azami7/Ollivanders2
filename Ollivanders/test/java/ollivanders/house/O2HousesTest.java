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
        assertTrue(O2Houses.useHouses);
        assertEquals(4, testHouses.getAllHouseNames().size());
        assertTrue(testHouses.getAllHouseNames().contains("Gryffindor"));

        O2HouseType gryffindor = testHouses.getHouseType("Gryffindor");
        assertNotNull(gryffindor);
        assertEquals(0, gryffindor.getScore());
        assertEquals(0, testHouses.getHouseMembers(gryffindor).size());
    }

    /**
     * Test sorting and house membership
     */
    @Test
    void testSorting() {
        // player1 is sorted in to ravenclaw
        assertTrue(testHouses.sort(player1, O2HouseType.RAVENCLAW));
        assertTrue(testHouses.getHouseMembers(O2HouseType.RAVENCLAW).contains(player1Name));

        // player does not appear in any other house
        assertFalse(testHouses.getHouseMembers(O2HouseType.HUFFLEPUFF).contains(player1Name));
        assertFalse(testHouses.getHouseMembers(O2HouseType.GRYFFINDOR).contains(player1Name));
        assertFalse(testHouses.getHouseMembers(O2HouseType.SLYTHERIN).contains(player1Name));

        // a sort event is fired for player being sorted
        assertTrue(testHouses.sort(player2, O2HouseType.HUFFLEPUFF));
        assertTrue(mockServer.getPluginManager()
                .getFiredEvents()
                .filter(e -> e instanceof OllivandersPlayerSortedEvent)
                .map(e -> (OllivandersPlayerSortedEvent) e)
                .anyMatch(ev -> ev.getPlayer().getName().equals(player2Name))
        );

        // a player cannot be sorted again in to their own house
        assertFalse(testHouses.sort(player1, O2HouseType.RAVENCLAW));
        assertFalse(testHouses.sort(player2, O2HouseType.HUFFLEPUFF));

        // a player cannot be sorted in to another house
        assertFalse(testHouses.sort(player1, O2HouseType.HUFFLEPUFF));
        assertFalse(testHouses.sort(player2, O2HouseType.RAVENCLAW));

        // players can be unsorted
        assertTrue(testHouses.getHouseMembers(O2HouseType.RAVENCLAW).contains(player1Name));
        testHouses.unsort(player1);
        assertFalse(testHouses.getHouseMembers(O2HouseType.RAVENCLAW).contains(player1Name));

        assertTrue(testHouses.getHouseMembers(O2HouseType.HUFFLEPUFF).contains(player2Name));
        testHouses.unsort(player2);
        assertFalse(testHouses.getHouseMembers(O2HouseType.HUFFLEPUFF).contains(player2Name));

        // a player can be sorted in to a new house
        assertTrue(testHouses.sort(player1, O2HouseType.HUFFLEPUFF));

        // a player can ge resorted in to their previous house
        assertTrue(testHouses.sort(player2, O2HouseType.HUFFLEPUFF));
    }

    /**
     * Test isSorted() and getHouse() by name and UUID
     */
    @Test
    void testIsSortedAndGetHouse() {
        assertTrue(testHouses.sort(player1, O2HouseType.RAVENCLAW));
        assertTrue(testHouses.sort(player2, O2HouseType.HUFFLEPUFF));

        // isSorted check works by player
        assertTrue(testHouses.isSorted(player1));
        // isSorted check works by UUID
        assertTrue(testHouses.isSorted(player2.getUniqueId()));
        assertFalse(testHouses.isSorted(player3));

        // getHouse works by player
        assertSame(O2HouseType.RAVENCLAW, testHouses.getHouse(player1));
        // getHouse works by UUID
        assertSame(O2HouseType.HUFFLEPUFF, testHouses.getHouse(player2.getUniqueId()));
        // getHouse returns null when the player is not sorted
        assertNull(testHouses.getHouse(player3));
    }

    /**
     * Test force sorting
     */
    @Test
    void testForceSorting() {
        assertTrue(testHouses.sort(player1, O2HouseType.RAVENCLAW));

        // a player who is already sorted will have their house changed
        testHouses.forceSetHouse(player1, O2HouseType.RAVENCLAW);
        assertTrue(testHouses.getHouseMembers(O2HouseType.RAVENCLAW).contains(player1Name));

        // a player who is not sorted will get sorted
        testHouses.forceSetHouse(player3, O2HouseType.RAVENCLAW);
        assertTrue(testHouses.getHouseMembers(O2HouseType.RAVENCLAW).contains(player3Name));
    }

    /**
     * Test house points
     */
    @Test
    void testHousePoints() {
        // set house points to an explicit value
        assertTrue(testHouses.setHousePoints(O2HouseType.RAVENCLAW, 5));
        assertEquals(5, O2HouseType.RAVENCLAW.getScore());
        assertTrue(testHouses.setHousePoints(O2HouseType.HUFFLEPUFF, 10));
        assertEquals(10, O2HouseType.HUFFLEPUFF.getScore());

        // add points
        assertTrue(testHouses.addHousePoints(O2HouseType.RAVENCLAW, 10));
        assertEquals(15, O2HouseType.RAVENCLAW.getScore());

        // subtract points
        assertTrue(testHouses.subtractHousePoints(O2HouseType.HUFFLEPUFF, 5));
        assertEquals(5, O2HouseType.HUFFLEPUFF.getScore());
    }

    /**
     * Test that the scoreboard works
     */
    @Test
    void testScoreboard() {
        // scoreboard has expected objective
        ObjectiveMock mockObjective = mockServer.getScoreboardManager().getMainScoreboard().getObjective("o2_hpoints");
        assertNotNull(mockObjective);

        // scoreboard has expected teams
        ScoreboardMock mockScoreboard = mockObjective.getScoreboard();
        assertNotNull(mockScoreboard);

        assertNotNull(mockScoreboard.getTeam(O2HouseType.GRYFFINDOR.getName()));
        assertNotNull(mockScoreboard.getTeam(O2HouseType.HUFFLEPUFF.getName()));
        assertNotNull(mockScoreboard.getTeam(O2HouseType.RAVENCLAW.getName()));
        assertNotNull(mockScoreboard.getTeam(O2HouseType.SLYTHERIN.getName()));

        // scores start at 0
        assertEquals(0, mockObjective.getScore(O2HouseType.GRYFFINDOR.getName()).getScore());
        assertEquals(0, mockObjective.getScore(O2HouseType.SLYTHERIN.getName()).getScore());

        // scores change when points added/removed/set
        testHouses.addHousePoints(O2HouseType.GRYFFINDOR, 10);
        assertEquals(10, mockObjective.getScore(O2HouseType.GRYFFINDOR.getName()).getScore());
        assertEquals(0, mockObjective.getScore(O2HouseType.SLYTHERIN.getName()).getScore());
        testHouses.subtractHousePoints(O2HouseType.GRYFFINDOR, 5);
        assertEquals(5, mockObjective.getScore(O2HouseType.GRYFFINDOR.getName()).getScore());
        assertEquals(0, mockObjective.getScore(O2HouseType.SLYTHERIN.getName()).getScore());

        // resetting scores sets them all back to 0
        testHouses.resetHousePoints();
        assertEquals(0, mockObjective.getScore(O2HouseType.GRYFFINDOR.getName()).getScore());
        assertEquals(0, mockObjective.getScore(O2HouseType.SLYTHERIN.getName()).getScore());
    }

    /**
     * Test house place text works.
     */
    @Test
    void testHousePlaceText() {
        assertTrue(testHouses.setHousePoints(O2HouseType.RAVENCLAW, 15));
        assertTrue(testHouses.setHousePoints(O2HouseType.HUFFLEPUFF, 10));
        assertTrue(testHouses.setHousePoints(O2HouseType.SLYTHERIN, 5));
        assertTrue(testHouses.setHousePoints(O2HouseType.GRYFFINDOR, 5));

        assertTrue(O2HouseType.getHousePlaceText(O2HouseType.RAVENCLAW).equals("in 1st place"));
        assertTrue(O2HouseType.getHousePlaceText(O2HouseType.SLYTHERIN).equals("tied for 3rd place"));
    }

    @AfterEach
    void tearDown () {
        MockBukkit.unmock();
    }
}
