package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.ANIMAGUS_INCANTATION;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.potion.ANIMAGUS_POTION;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ANIMAGUS_POTION}.
 */
public class AnimagusPotionTest {
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
     * Drinking grants ANIMAGUS_EFFECT only with the ANIMAGUS_INCANTATION effect and thundering weather; otherwise the
     * drinker gets a failure message, and an existing animagus gets the already-animagus message.
     */
    @Test
    void drinkTest() {
        PlayerMock player = mockServer.addPlayer();
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);
        ANIMAGUS_POTION potion = new ANIMAGUS_POTION(testPlugin);

        player.setLocation(location);

        // player does not have animagus incantation effect and wrong weather, nothing happens
        potion.drink(player);
        String potionMessage = player.nextMessage();
        assertNotNull(potionMessage);
        assertEquals(potion.getPotionFailureMessage(), TestCommon.cleanChatMessage(potionMessage), "Player did not get expected potion failure message");
        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.ANIMAGUS_EFFECT), "Animagus effect set on player when they did not have animagus incantation");

        // player has animagus incantation but it is not thundering
        ANIMAGUS_INCANTATION animagusIncantation = new ANIMAGUS_INCANTATION(testPlugin, 1000, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(animagusIncantation);
        mockServer.getScheduler().performTicks(20);
        potion.drink(player);
        potionMessage = player.nextMessage();
        assertNotNull(potionMessage);
        assertEquals(potion.getPotionFailureMessage(), TestCommon.cleanChatMessage(potionMessage), "Player did not get expected potion failure message");
        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.ANIMAGUS_EFFECT), "Animagus effect set when it was not thundering");

        // player had animagus incantation and it is thundering
        testWorld.setThundering(true);
        potion.drink(player);
        mockServer.getScheduler().performTicks(20);
        potionMessage = player.nextMessage();
        assertNotNull(potionMessage);
        assertEquals(potion.getPotionSuccessMessage(), TestCommon.cleanChatMessage(potionMessage), "Player did not get potion success message");
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.ANIMAGUS_EFFECT), "Animagus effect not set");
        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.ANIMAGUS_INCANTATION), "Animagus incantation effect not removed");

        PlayerMock player2 = mockServer.addPlayer();
        O2Player o2player2 = Ollivanders2API.getPlayers().getPlayer(player2.getUniqueId());
        assertNotNull(o2player2);
        o2player2.setIsAnimagus();
        potion.drink(player2);
        potionMessage = player2.nextMessage();
        assertNotNull(potionMessage);
        assertEquals(potion.getAlreadyAnimagusMessage(), TestCommon.cleanChatMessage(potionMessage), "Player2 did not get already animagus message");
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
