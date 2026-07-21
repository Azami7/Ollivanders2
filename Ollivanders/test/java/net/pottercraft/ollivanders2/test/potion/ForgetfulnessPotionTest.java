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
 * Unit tests for {@link FORGETFULNESS_POTION}.
 */
public class ForgetfulnessPotionTest {
    /**
     * Shared MockBukkit server, mocked once per test class as server setup is expensive.
     */
    static ServerMock mockServer;

    /**
     * The plugin instance, loaded once for the test class.
     */
    static Ollivanders2 testPlugin;

    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance past the scheduler's 20-tick startup delay
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Drinking the potion decreases the drinker's experience in at least one of spells or potions.
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

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
