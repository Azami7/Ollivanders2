package net.pottercraft.ollivanders2.test.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.effect.SLEEPING;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link net.pottercraft.ollivanders2.potion.WIGGENWELD_POTION}.
 */
public class WiggenweldPotionTest {
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
     * Splashing the potion removes the SLEEPING effect from affected players.
     */
    @Test
    void doOnPotionSplashEventTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 4, 100);

        ItemStack potionItemStack = Ollivanders2API.getPotions().getPotionItemStackByType(O2PotionType.WIGGENWELD_POTION, 1);
        assertNotNull(potionItemStack);

        // Spawn the ThrownPotion entity
        ThrownPotion thrownPotion = testWorld.spawn(location, ThrownPotion.class);
        thrownPotion.setItem(potionItemStack);

        PlayerMock player = mockServer.addPlayer();
        PlayerMock player2 = mockServer.addPlayer();
        SLEEPING sleeping = new SLEEPING(testPlugin, 1000, false, player2.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(sleeping);
        mockServer.getScheduler().performTicks(20);

        HashMap<LivingEntity, Double> affectedEntities = new HashMap<>(){{
            put(player2, 1.0);
        }};
        player.setLocation(location);
        PotionSplashEvent event = new PotionSplashEvent(thrownPotion, player, null, BlockFace.DOWN, affectedEntities);
        mockServer.getPluginManager().callEvent(event);
        mockServer.getScheduler().performTicks(20);

        assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(player2.getUniqueId(), O2EffectType.SLEEPING));
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
