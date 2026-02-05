package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.potion.FORGETFULNESS_POTION;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for the Forgetfulness Potion effect.
 *
 * <p>Verifies that the Forgetfulness Potion correctly decreases the player's experience in
 * either spells or potions when consumed. Tests that at least one skill decreases to ensure
 * the potion's intended effect of causing memory loss in magical studies.</p>
 */
public class ForgetfulnessPotionTest {
    /**
     * Shared mock Bukkit server instance for all tests.
     *
     * <p>Static field initialized once before all tests in this class. Reused across test instances
     * to avoid expensive server setup/teardown for each test method.</p>
     */
    static ServerMock mockServer;

    /**
     * The plugin instance being tested.
     *
     * <p>Loaded once before all tests with the default configuration. Provides access to
     * logger, scheduler, and other plugin API methods during tests.</p>
     */
    static Ollivanders2 testPlugin;

    /**
     * Initialize the mock Bukkit server before all tests.
     *
     * <p>Static setup method called once before all tests in this class. Creates the shared
     * MockBukkit server instance that is reused across all test methods to avoid expensive
     * server creation/destruction overhead.</p>
     */
    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Test the Forgetfulness Potion's effect on skill experience.
     *
     * <p>This test verifies that when a player with known spell and potion skills drinks the
     * Forgetfulness Potion, at least one of their skill levels decreases as a result of the
     * potion's memory loss effect. The test sets up both spell and potion experience at level 100,
     * then verifies that at least one skill level has been reduced after drinking the potion.</p>
     */
    @Test
    void drinkTest() {
        FORGETFULNESS_POTION potion = new FORGETFULNESS_POTION(testPlugin);
        PlayerMock player = mockServer.addPlayer();

        O2PotionType knownPotion = O2PotionType.HUNGER_POTION;
        O2SpellType knownSpell = O2SpellType.ACCIO;
        int skillLevel = 100;
        TestCommon.setPlayerPotionExperience(testPlugin, player, knownPotion, skillLevel);
        TestCommon.setPlayerSpellExperience(testPlugin, player, knownSpell, skillLevel);

        potion.drink(player);

        int potionSkillLevel = TestCommon.getPlayerPotionExperience(testPlugin, player, knownPotion);
        int spellSkillLevel = TestCommon.getPlayerSpellExperience(testPlugin, player, knownSpell);

        // either the spell skill decreased or the potion skill did
        assertTrue((potionSkillLevel < skillLevel) || (spellSkillLevel < skillLevel), "Spell or potion skill did not decrease");
    }

    /**
     * Tear down the mock Bukkit server after all tests complete.
     *
     * <p>Static teardown method called once after all tests in this class have finished.
     * Releases the MockBukkit server resources to prevent memory leaks and allow clean
     * test execution in subsequent test classes.</p>
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
