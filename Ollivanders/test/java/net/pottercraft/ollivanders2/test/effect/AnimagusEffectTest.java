package net.pottercraft.ollivanders2.test.effect;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.ANIMAGUS_EFFECT;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ANIMAGUS_EFFECT}. Coverage is limited because the actual transformation runs through
 * LibsDisguises, which is not mockable here; only the flag-tracking and event-cancelling behavior is exercised.
 *
 * <p>TODO: mock out LibsDisguises so transformation mechanics can be tested</p>
 *
 * @see PermanentEffectTestSuper
 */
public class AnimagusEffectTest extends PermanentEffectTestSuper {
    /**
     * Also sets the player's animagus form (COW), which the effect requires to function.
     *
     * @param target          the player to add the effect to
     * @param durationInTicks the duration parameter (unused for permanent effects)
     * @param isPermanent     ignored; ANIMAGUS_EFFECT instances are always permanent
     * @return the newly created ANIMAGUS_EFFECT instance
     */
    @Override
    ANIMAGUS_EFFECT createEffect(Player target, int durationInTicks, boolean isPermanent) {
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(target.getUniqueId());

        // we need to make the player an animagus so that the effect can work
        assertNotNull(o2p, "Ollivanders2API.getPlayers().getPlayer(player.getUniqueId()) is null");
        o2p.setAnimagusForm(EntityType.COW);

        return new ANIMAGUS_EFFECT(testPlugin, durationInTicks, true, target.getUniqueId());
    }

    /**
     * Covers the effect's behavior that does not require LibsDisguises: killed when the player has no animagus form,
     * form preserved through application, transformed on the first tick and staying transformed, and killable.
     */
    @Override
    void checkEffectTest() {
        // Test 1: Constructor behavior when animagus form is null
        PlayerMock target1 = mockServer.addPlayer();
        O2Player o2p1 = Ollivanders2API.getPlayers().getPlayer(target1.getUniqueId());
        assertNotNull(o2p1, "O2Player should exist for target1");
        // Don't set animagus form - leave it null
        ANIMAGUS_EFFECT effect1 = new ANIMAGUS_EFFECT(testPlugin, 100, true, target1.getUniqueId());
        assertTrue(effect1.isKilled(), "Effect should be killed when animagus form is null");

        // Test 2: O2Player animagus form is preserved
        PlayerMock target2 = mockServer.addPlayer();
        O2Player o2p2 = Ollivanders2API.getPlayers().getPlayer(target2.getUniqueId());
        assertNotNull(o2p2, "O2Player should exist for target2");
        o2p2.setAnimagusForm(EntityType.COW);
        ANIMAGUS_EFFECT effect2 = (ANIMAGUS_EFFECT) addEffect(target2, 100, true);
        mockServer.getScheduler().performTicks(5);
        // Verify the effect was created and form is still COW after effect application
        assertNotNull(effect2, "Effect2 should be created");
        assertTrue(o2p2.getAnimagusForm() == EntityType.COW, "O2Player animagus form should remain COW");

        // Test 3: Effect transforms on first tick
        PlayerMock target3 = mockServer.addPlayer();
        O2Player o2p3 = Ollivanders2API.getPlayers().getPlayer(target3.getUniqueId());
        assertNotNull(o2p3, "O2Player should exist for target3");
        o2p3.setAnimagusForm(EntityType.CAT);
        ANIMAGUS_EFFECT effect3 = (ANIMAGUS_EFFECT) addEffect(target3, 100, true);
        mockServer.getScheduler().performTicks(5);
        assertTrue(effect3.isTransformed(), "Effect3 should be transformed on first tick");

        // Test 4: Transformed flag persists across multiple tick cycles
        PlayerMock target5 = mockServer.addPlayer();
        O2Player o2p5 = Ollivanders2API.getPlayers().getPlayer(target5.getUniqueId());
        assertNotNull(o2p5, "O2Player should exist for target5");
        o2p5.setAnimagusForm(EntityType.WOLF);
        ANIMAGUS_EFFECT effect5 = (ANIMAGUS_EFFECT) addEffect(target5, 100, true);
        mockServer.getScheduler().performTicks(5);
        assertTrue(effect5.isTransformed(), "Effect5 should be transformed after first tick cycle");
        mockServer.getScheduler().performTicks(10);
        assertTrue(effect5.isTransformed(), "Effect5 should remain transformed after second tick cycle");
        mockServer.getScheduler().performTicks(10);
        assertTrue(effect5.isTransformed(), "Effect5 should remain transformed after third tick cycle");

        // Test 5: Effect removal and cleanup behavior
        PlayerMock target6 = mockServer.addPlayer();
        O2Player o2p6 = Ollivanders2API.getPlayers().getPlayer(target6.getUniqueId());
        assertNotNull(o2p6, "O2Player should exist for target6");
        o2p6.setAnimagusForm(EntityType.CAT);
        ANIMAGUS_EFFECT effect6 = (ANIMAGUS_EFFECT) addEffect(target6, 100, true);
        mockServer.getScheduler().performTicks(5);
        assertTrue(effect6.isTransformed(), "Effect6 should be transformed before kill");
        effect6.kill();
        assertTrue(effect6.isKilled(), "Effect6 should be marked as killed");
    }

    @Override
    void doRemoveTest() {}

    /**
     * Verifies the effect cancels the interactions forbidden while in animal form: block interaction, flight, item
     * pickup, item selection, item consumption, and item dropping.
     */
    @Override
    void eventHandlerTests() {
        doOnPlayerInteractEventTest();
        doOnPlayerToggleFlightEventTest();
        doOnPlayerPickupItemEventTest();
        doOnPlayerItemHeldEventTest();
        doOnPlayerItemConsumeEventTest();
        doOnPlayerDropItemEventTest();
    }

    /**
     * Both left- and right-click block interactions are cancelled while transformed.
     */
    private void doOnPlayerInteractEventTest() {
        PlayerMock target = mockServer.addPlayer();
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(target.getUniqueId());
        assertNotNull(o2p, "O2Player should exist");
        o2p.setAnimagusForm(EntityType.COW);
        ANIMAGUS_EFFECT effect = (ANIMAGUS_EFFECT) addEffect(target, 100, true);
        mockServer.getScheduler().performTicks(5);
        assertTrue(effect.isTransformed(), "Effect should be transformed");

        // Test right-click block interaction
        Block block = target.getLocation().getBlock();
        PlayerInteractEvent rightClickEvent = new PlayerInteractEvent(target, org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK, null, block, BlockFace.DOWN);
        mockServer.getPluginManager().callEvent(rightClickEvent);
        assertTrue(rightClickEvent.isCancelled(), "Right-click block interaction should be cancelled");

        // Test left-click block interaction
        PlayerInteractEvent leftClickEvent = new PlayerInteractEvent(target, Action.LEFT_CLICK_BLOCK, null, block, BlockFace.DOWN);
        mockServer.getPluginManager().callEvent(leftClickEvent);
        assertTrue(leftClickEvent.isCancelled(), "Left-click block interaction should be cancelled");
    }

    /**
     * Flight toggling is cancelled while transformed.
     */
    private void doOnPlayerToggleFlightEventTest() {
        PlayerMock target = mockServer.addPlayer();
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(target.getUniqueId());
        assertNotNull(o2p, "O2Player should exist");
        o2p.setAnimagusForm(EntityType.WOLF);
        ANIMAGUS_EFFECT effect = (ANIMAGUS_EFFECT) addEffect(target, 100, true);
        mockServer.getScheduler().performTicks(5);
        assertTrue(effect.isTransformed(), "Effect should be transformed");

        PlayerToggleFlightEvent flightEvent = new PlayerToggleFlightEvent(target, true);
        mockServer.getPluginManager().callEvent(flightEvent);
        assertTrue(flightEvent.isCancelled(), "Flight toggle should be cancelled");
    }

    /**
     * Item pickup is cancelled while transformed.
     */
    private void doOnPlayerPickupItemEventTest() {
        PlayerMock target = mockServer.addPlayer();
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(target.getUniqueId());
        assertNotNull(o2p, "O2Player should exist");
        o2p.setAnimagusForm(EntityType.CAT);
        ANIMAGUS_EFFECT effect = (ANIMAGUS_EFFECT) addEffect(target, 100, true);
        mockServer.getScheduler().performTicks(5);
        assertTrue(effect.isTransformed(), "Effect should be transformed");

        Item item = target.getWorld().dropItem(target.getLocation(), new ItemStack(org.bukkit.Material.DIAMOND));
        EntityPickupItemEvent pickupEvent = new EntityPickupItemEvent(target, item, 1);
        mockServer.getPluginManager().callEvent(pickupEvent);
        assertTrue(pickupEvent.isCancelled(), "Item pickup should be cancelled");
    }

    /**
     * Hotbar item selection is cancelled while transformed.
     */
    private void doOnPlayerItemHeldEventTest() {
        PlayerMock target = mockServer.addPlayer();
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(target.getUniqueId());
        assertNotNull(o2p, "O2Player should exist");
        o2p.setAnimagusForm(EntityType.RABBIT);
        ANIMAGUS_EFFECT effect = (ANIMAGUS_EFFECT) addEffect(target, 100, true);
        mockServer.getScheduler().performTicks(5);
        assertTrue(effect.isTransformed(), "Effect should be transformed");

        PlayerItemHeldEvent itemHeldEvent = new PlayerItemHeldEvent(target, 0, 1);
        mockServer.getPluginManager().callEvent(itemHeldEvent);
        assertTrue(itemHeldEvent.isCancelled(), "Item selection should be cancelled");
    }

    /**
     * Item consumption is cancelled while transformed.
     */
    private void doOnPlayerItemConsumeEventTest() {
        PlayerMock target = mockServer.addPlayer();
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(target.getUniqueId());
        assertNotNull(o2p, "O2Player should exist");
        o2p.setAnimagusForm(EntityType.HORSE);
        ANIMAGUS_EFFECT effect = (ANIMAGUS_EFFECT) addEffect(target, 100, true);
        mockServer.getScheduler().performTicks(5);
        assertTrue(effect.isTransformed(), "Effect should be transformed");

        PlayerItemConsumeEvent consumeEvent = new PlayerItemConsumeEvent(target, new ItemStack(org.bukkit.Material.APPLE), EquipmentSlot.HAND);
        mockServer.getPluginManager().callEvent(consumeEvent);
        assertTrue(consumeEvent.isCancelled(), "Item consumption should be cancelled");
    }

    /**
     * Item dropping is cancelled while transformed.
     */
    private void doOnPlayerDropItemEventTest() {
        PlayerMock target = mockServer.addPlayer();
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(target.getUniqueId());
        assertNotNull(o2p, "O2Player should exist");
        o2p.setAnimagusForm(EntityType.PANDA);
        ANIMAGUS_EFFECT effect = (ANIMAGUS_EFFECT) addEffect(target, 100, true);
        mockServer.getScheduler().performTicks(5);
        assertTrue(effect.isTransformed(), "Effect should be transformed");

        Item droppedItem = target.getWorld().dropItem(target.getLocation(), new ItemStack(org.bukkit.Material.DIAMOND));
        PlayerDropItemEvent dropEvent = new PlayerDropItemEvent(target, droppedItem);
        mockServer.getPluginManager().callEvent(dropEvent);
        assertTrue(dropEvent.isCancelled(), "Item drop should be cancelled");
    }
}
