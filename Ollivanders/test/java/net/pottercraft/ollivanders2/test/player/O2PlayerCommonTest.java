package net.pottercraft.ollivanders2.test.player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.house.O2HouseType;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.item.wand.O2WandWoodType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.EntityType;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link O2PlayerCommon}.
 */
public class O2PlayerCommonTest {
    static Ollivanders2 testPlugin;
    static ServerMock mockServer;
    static O2PlayerCommon playerCommon;

    /**
     * Dedicated to the wand check test, which changes held items.
     */
    static PlayerMock player1;

    /**
     * Dedicated to the potion effect tests, which add potion effects.
     */
    static PlayerMock player2;

    /**
     * Dedicated to the restore-health test, which changes health and effects.
     */
    static PlayerMock player3;

    /**
     * Dedicated to the kit test, which adds inventory items.
     */
    static PlayerMock player4;

    /**
     * Dedicated to the sorted-players test, which sorts the player into a house.
     */
    static PlayerMock player5;

    @BeforeAll
    static void globalSetUp() {
        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        mockServer.addSimpleWorld("world");

        playerCommon = new O2PlayerCommon(testPlugin);

        player1 = mockServer.addPlayer();
        player2 = mockServer.addPlayer();
        player3 = mockServer.addPlayer();
        player4 = mockServer.addPlayer();
        player5 = mockServer.addPlayer();
    }

    /**
     * Animagus form assignment is deterministic per UUID, and form allow-listing respects the hostile-mob config.
     */
    @Test
    void testAnimagusForms() {
        // the form for a UUID is deterministic
        UUID pid = UUID.randomUUID();
        EntityType form = playerCommon.getAnimagusForm(pid);
        assertNotNull(form, "An animagus form should always be assigned");
        assertEquals(form, playerCommon.getAnimagusForm(pid), "Animagus form should be deterministic per UUID");

        // common and rare forms are allowed
        assertTrue(playerCommon.isAllowedAnimagusForm(EntityType.COW), "Common form should be allowed");
        assertTrue(playerCommon.isAllowedAnimagusForm(EntityType.OCELOT), "Rare form should be allowed");

        // hostile mob animagi are disabled in the test config
        assertFalse(Ollivanders2.useHostileMobAnimagi, "Test config should have hostile mob animagi disabled");
        assertFalse(playerCommon.isAllowedAnimagusForm(EntityType.CREEPER),
                "Hostile form should not be allowed when hostile mob animagi are disabled");

        // non-animagus entity types are never allowed
        assertFalse(playerCommon.isAllowedAnimagusForm(EntityType.ENDER_DRAGON),
                "Ender dragon should never be an allowed form");
    }

    /**
     * Wand check returns the destined-wand multiplier for the player's own wand, the wrong-wand multiplier for
     * another wand, the elder-wand multiplier for the Elder Wand, and -1 when not holding a wand.
     */
    @Test
    void testWandCheck() {
        // the wand check needs an O2Player record; the plugin may have already created one on player join
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player1.getUniqueId());
        if (o2p == null)
            o2p = Ollivanders2API.getPlayers().addPlayer(player1.getUniqueId(), player1.getName());
        assertNotNull(o2p, "O2Player should exist for player1");

        // the player's destined wand
        ItemStack destinedWand = Ollivanders2API.getItems().getWands()
                .makeWand(o2p.getDestinedWandWood(), o2p.getDestinedWandCore(), 1);
        player1.getInventory().setItemInMainHand(destinedWand);
        assertEquals(O2PlayerCommon.rightWand, playerCommon.wandCheck(player1),
                "Destined wand should return the right-wand multiplier");

        // a wand that is not the player's destined wand: change the wood so it cannot match
        String destinedWood = o2p.getDestinedWandWood();
        String otherWood = O2WandWoodType.getAllWandWoodsByName().stream()
                .filter(w -> !w.equals(destinedWood)).findFirst().orElseThrow();
        ItemStack otherWand = Ollivanders2API.getItems().getWands().makeWand(otherWood, o2p.getDestinedWandCore(), 1);
        player1.getInventory().setItemInMainHand(otherWand);
        assertEquals(O2PlayerCommon.wrongWand, playerCommon.wandCheck(player1),
                "Another player's wand should return the wrong-wand multiplier");

        // the Elder Wand
        player1.getInventory().setItemInMainHand(O2ItemType.ELDER_WAND.getItem(1));
        assertEquals(O2PlayerCommon.elderWand, playerCommon.wandCheck(player1),
                "The Elder Wand should return the elder-wand multiplier");
    }

    /**
     * Potion effect checks: hasPotionEffect sees an active effect and isInvisible is true with an invisibility
     * effect.
     */
    @Test
    void testPotionEffectChecks() {
        assertFalse(O2PlayerCommon.hasPotionEffect(player2, PotionEffectType.SPEED),
                "Player should not have speed initially");
        assertFalse(O2PlayerCommon.isInvisible(player2), "Player should not be invisible initially");

        int durationTicks = 20 * 60; // 1 minute — long enough to still be active when asserted
        player2.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, durationTicks, 1));
        assertTrue(O2PlayerCommon.hasPotionEffect(player2, PotionEffectType.SPEED),
                "Player should have the speed effect");
        assertFalse(O2PlayerCommon.hasPotionEffect(player2, PotionEffectType.JUMP_BOOST),
                "Player should not have an effect that was not applied");

        player2.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, durationTicks, 1));
        assertTrue(O2PlayerCommon.isInvisible(player2), "Player with an invisibility effect should be invisible");
    }

    /**
     * Restoring full health sets health to max and removes active potion effects.
     */
    @Test
    void testRestoreFullHealth() {
        AttributeInstance maxHealth = player3.getAttribute(Attribute.MAX_HEALTH);
        assertNotNull(maxHealth, "Player should have a max health attribute");

        player3.setHealth(maxHealth.getBaseValue() / 2);
        int durationTicks = 20 * 60; // 1 minute
        player3.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, durationTicks, 1));

        O2PlayerCommon.restoreFullHealth(player3);

        assertEquals(maxHealth.getBaseValue(), player3.getHealth(), "Player should be restored to full health");
        assertTrue(player3.getActivePotionEffects().isEmpty(), "Potion effects should be removed on restore");
    }

    /**
     * Giving a kit puts the items in the player's inventory.
     */
    @Test
    void testGivePlayerKit() {
        ItemStack apples = new ItemStack(Material.APPLE, 3);
        ItemStack bread = new ItemStack(Material.BREAD, 2);

        O2PlayerCommon.givePlayerKit(player4, List.of(apples, bread));

        assertTrue(player4.getInventory().contains(Material.APPLE, 3), "Kit apples should be in inventory");
        assertTrue(player4.getInventory().contains(Material.BREAD, 2), "Kit bread should be in inventory");
    }

    /**
     * Only sorted players appear in the sorted online players list.
     */
    @Test
    void testGetAllOnlineSortedPlayers() {
        assertFalse(playerCommon.getAllOnlineSortedPlayers().contains(player5),
                "Unsorted player should not be in the sorted players list");

        assertTrue(Ollivanders2API.getHouses().sort(player5, O2HouseType.HUFFLEPUFF), "Player5 should sort");
        assertTrue(playerCommon.getAllOnlineSortedPlayers().contains(player5),
                "Sorted player should be in the sorted players list");
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
