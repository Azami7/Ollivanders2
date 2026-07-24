package net.pottercraft.ollivanders2.test;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the invisibility cloak handling in {@link net.pottercraft.ollivanders2.Ollivanders2Schedule}:
 * wearing the cloak grants invisibility, taking it off removes it, and invisibility from a potion is left alone.
 * The per-tick upkeep calls the scheduler makes are covered by each system's own tests.
 *
 * <p>The cloak check runs once a second, so every scenario advances the scheduler at least a full second of ticks
 * before asserting. Each test uses its own player, so parallel test methods cannot see each other's state.</p>
 */
public class Ollivanders2ScheduleTest {
    static ServerMock mockServer;

    /**
     * One second of game ticks - the interval the cloak check runs on, so advancing this many ticks always
     * includes at least one check.
     */
    static final int cloakCheckTicks = 20;

    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance past the scheduler's startup delay so the per-tick task is running
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Wearing the Cloak of Invisibility grants the infinite-duration invisibility effect, and taking the cloak
     * off removes it. A plain chestplate of the same material must not count as the cloak - only the item with
     * the cloak's NBT does.
     */
    @Test
    void invisibilityCloakTest() {
        PlayerMock player = mockServer.addPlayer();

        // -- a plain chestplate of the cloak's material is not a cloak --
        player.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        mockServer.getScheduler().performTicks(cloakCheckTicks);
        assertFalse(player.hasPotionEffect(PotionEffectType.INVISIBILITY),
                "A plain chestplate should not grant invisibility");

        // -- wearing the cloak makes the player invisible --
        player.getInventory().setChestplate(O2ItemType.INVISIBILITY_CLOAK.getItem(1));
        mockServer.getScheduler().performTicks(cloakCheckTicks);

        PotionEffect invisibility = player.getPotionEffect(PotionEffectType.INVISIBILITY);
        assertNotNull(invisibility, "Wearing the cloak should grant invisibility");
        assertTrue(invisibility.isInfinite(), "Cloak-granted invisibility should be infinite so it never expires while worn");

        // -- taking the cloak off makes the player visible again --
        player.getInventory().setChestplate(null);
        mockServer.getScheduler().performTicks(cloakCheckTicks);
        assertFalse(player.hasPotionEffect(PotionEffectType.INVISIBILITY),
                "Taking the cloak off should remove the cloak-granted invisibility");
    }

    /**
     * Invisibility from a potion is finite, so the cloak check must leave it alone - a player who drank an
     * invisibility potion and is not wearing the cloak stays invisible. Regression test for the cloak check
     * removing potion-granted invisibility because it used the effect type alone as its marker.
     */
    @Test
    void potionInvisibilityPreservedTest() {
        PlayerMock player = mockServer.addPlayer();

        int potionDuration = 8 * 60 * 20; // 8 minutes - the longest vanilla invisibility potion
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, potionDuration, 0));

        mockServer.getScheduler().performTicks(cloakCheckTicks * 2); // two check cycles to prove it survives repeats

        PotionEffect invisibility = player.getPotionEffect(PotionEffectType.INVISIBILITY);
        assertNotNull(invisibility, "The cloak check should not remove potion-granted invisibility");
        assertFalse(invisibility.isInfinite(), "Potion-granted invisibility should still be the finite effect");
    }

    /**
     * A player who has finite potion invisibility and puts the cloak on gets the cloak's infinite effect in its
     * place, and taking the cloak off then removes invisibility entirely. Pins the current behavior that the
     * cloak's grant replaces a potion effect rather than stacking with it.
     */
    @Test
    void cloakOverPotionInvisibilityTest() {
        PlayerMock player = mockServer.addPlayer();

        int potionDuration = 8 * 60 * 20; // 8 minutes - the longest vanilla invisibility potion
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, potionDuration, 0));
        player.getInventory().setChestplate(O2ItemType.INVISIBILITY_CLOAK.getItem(1));

        mockServer.getScheduler().performTicks(cloakCheckTicks);

        PotionEffect invisibility = player.getPotionEffect(PotionEffectType.INVISIBILITY);
        assertNotNull(invisibility, "The player should still be invisible while wearing the cloak");
        assertTrue(invisibility.isInfinite(), "The cloak's infinite effect should replace the finite potion effect");

        // -- taking the cloak off removes the invisibility --
        player.getInventory().setChestplate(null);
        mockServer.getScheduler().performTicks(cloakCheckTicks);
        assertFalse(player.hasPotionEffect(PotionEffectType.INVISIBILITY),
                "Taking the cloak off should remove the cloak-granted invisibility");
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
