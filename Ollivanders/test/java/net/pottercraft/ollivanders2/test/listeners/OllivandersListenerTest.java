package net.pottercraft.ollivanders2.test.listeners;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.listeners.OllivandersListener;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Item;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Zombie;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link OllivandersListener}, the plugin's primary event listener. Each test fires the real Bukkit
 * event through the plugin manager and asserts the effect the handler is responsible for, rather than calling the
 * handler directly, so that event registration is covered too. The exception is {@code rightClickWand}, which is
 * called directly because the event path to it needs an unimplemented MockBukkit method — see that test.
 *
 * <p>Everything runs in one test method sharing a static MockBukkit server: the handlers mutate global server and
 * plugin state (players, worlds, active spells, config flags), so parallel methods would interfere with each other.</p>
 *
 * @author Azami7
 */
@Isolated
public class OllivandersListenerTest {
    /**
     * Shared MockBukkit server, created once for all tests in the class.
     */
    static ServerMock mockServer;

    /**
     * The plugin under test, loaded once with the default config.
     */
    static Ollivanders2 testPlugin;

    /**
     * Number of ticks to advance to run a deferred handler and let its effects settle.
     */
    static int deferredTicks;

    /**
     * Start the shared MockBukkit server and load the plugin before any tests run.
     */
    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        mockServer.getScheduler().performTicks(TestCommon.startupTicks);

        // the listener defers most handlers by threadDelay ticks; 20 more (1 second) lets the resulting action settle
        deferredTicks = OllivandersListener.getThreadDelay() + 20;
    }

    /**
     * Run all listener checks in sequence (see the class comment for why they share one method).
     */
    @Test
    void ollivandersListenerTest() {
        onPlayerChatRecipientsTest();
        onPlayerChatCastingTest();
        onPlayerInteractPrimaryHandTest();
        rightClickWandTest();
        onPlayerInteractSecondaryHandTest();
        onPlayerJoinTest();
        onPlayerQuitTest();
        onPlayerDeathTest();
        onEntityDamageTest();
        onCloakPlayerTest();
        onWitchWandDropTest();
        onSpellJournalHoldTest();
    }

    /**
     * onPlayerChat limits who hears an incantation: a non-spoken spell (apparating) is heard by no one, and any other
     * incantation is heard only by players within the configured chat dropoff distance.
     */
    private void onPlayerChatRecipientsTest() {
        World testWorld = mockServer.addSimpleWorld("chatRecipientsWorld");
        Location location = new Location(testWorld, 100, 40, 100);

        PlayerMock speaker = mockServer.addPlayer();
        speaker.setLocation(location);

        PlayerMock nearby = mockServer.addPlayer();
        nearby.setLocation(new Location(testWorld, 101, 40, 100));

        PlayerMock faraway = mockServer.addPlayer();
        faraway.setLocation(new Location(testWorld, 100 + (Ollivanders2.chatDropoff * 10), 40, 100));

        // a non-spoken spell is heard by no one
        Set<Player> apparateRecipients = new HashSet<>(List.of(speaker, nearby, faraway));
        mockServer.getPluginManager().callEvent(new AsyncPlayerChatEvent(false, speaker,
                O2SpellType.APPARATE.getSpellName(), apparateRecipients));

        assertTrue(apparateRecipients.isEmpty(), "onPlayerChat should clear all recipients for a non-spoken spell");

        // a spoken spell is heard only by players inside the chat dropoff radius
        Set<Player> lumosRecipients = new HashSet<>(List.of(speaker, nearby, faraway));
        mockServer.getPluginManager().callEvent(new AsyncPlayerChatEvent(false, speaker,
                O2SpellType.LUMOS.getSpellName(), lumosRecipients));

        assertTrue(lumosRecipients.contains(nearby), "onPlayerChat should keep recipients within the chat dropoff");
        assertFalse(lumosRecipients.contains(faraway), "onPlayerChat should drop recipients beyond the chat dropoff");

        mockServer.getScheduler().performTicks(deferredTicks);
        cleanupActiveSpells();
    }

    /**
     * onPlayerChat casts the spoken incantation on a delay, so a player holding a valid wand has the spell queued once
     * the delay has elapsed. A message that is not an incantation queues nothing.
     */
    private void onPlayerChatCastingTest() {
        World testWorld = mockServer.addSimpleWorld("chatCastingWorld");
        Location location = new Location(testWorld, 100, 40, 100);

        PlayerMock speaker = mockServer.addPlayer();
        speaker.setLocation(location);
        O2Player o2p = testPlugin.getO2Player(speaker);
        // the elder wand always passes the wand check, making the queueing deterministic
        speaker.getInventory().setItemInMainHand(O2ItemType.ELDER_WAND.getItem(1));
        o2p.setWandSpell(null);

        // the incantation is not handled during dispatch, only after the delay
        mockServer.getPluginManager().callEvent(new AsyncPlayerChatEvent(false, speaker,
                O2SpellType.LUMOS.getSpellName(), new HashSet<>()));
        assertNull(o2p.getWandSpell(), "onPlayerChat should defer the incantation rather than handle it inline");

        mockServer.getScheduler().performTicks(deferredTicks);
        assertEquals(O2SpellType.LUMOS, o2p.getWandSpell(), "onPlayerChat should queue the spoken spell after the delay");

        // ordinary chat is not an incantation and queues nothing
        o2p.setWandSpell(null);
        mockServer.getPluginManager().callEvent(new AsyncPlayerChatEvent(false, speaker, "hello there", new HashSet<>()));
        mockServer.getScheduler().performTicks(deferredTicks);

        assertNull(o2p.getWandSpell(), "onPlayerChat should queue nothing for a message that is not an incantation");
    }

    /**
     * onPlayerInteract casts the player's queued spell on a left click of the primary hand while holding a wand.
     *
     * @implNote The right-click branch cannot be tested: it looks for a cauldron with {@code playerFacingBlockType},
     * which calls the MockBukkit-unimplemented {@code Player.getLineOfSight}.
     */
    private void onPlayerInteractPrimaryHandTest() {
        World testWorld = mockServer.addSimpleWorld("interactPrimaryWorld");
        Location location = new Location(testWorld, 100, 40, 100);

        PlayerMock player = mockServer.addPlayer();
        player.setLocation(location);
        O2Player o2p = testPlugin.getO2Player(player);
        ItemStack wand = O2ItemType.ELDER_WAND.getItem(1);
        player.getInventory().setItemInMainHand(wand);

        cleanupActiveSpells();
        TestCommon.setPlayerSpellExperience(testPlugin, player, O2SpellType.LUMOS, 0);
        o2p.setWandSpell(O2SpellType.LUMOS);

        mockServer.getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, wand,
                null, BlockFace.SELF, EquipmentSlot.HAND));
        mockServer.getScheduler().performTicks(deferredTicks);

        // the cast is counted when the spell is created; the spell itself may already have run its course and been
        // reaped by upkeep by the time the deferred handler's delay has elapsed, so the count is what we can assert on
        assertEquals(1, TestCommon.getPlayerSpellExperience(testPlugin, player, O2SpellType.LUMOS),
                "a primary hand left click with a wand should cast the queued spell");
        assertNull(o2p.getWandSpell(), "casting should clear the player's queued spell");

        // the same click without a wand casts nothing
        cleanupActiveSpells();
        PlayerMock wandless = mockServer.addPlayer();
        wandless.setLocation(location);
        O2Player wandlessO2p = testPlugin.getO2Player(wandless);
        TestCommon.setPlayerSpellExperience(testPlugin, wandless, O2SpellType.LUMOS, 0);
        wandlessO2p.setWandSpell(O2SpellType.LUMOS);

        mockServer.getPluginManager().callEvent(new PlayerInteractEvent(wandless, Action.LEFT_CLICK_AIR, null,
                null, BlockFace.SELF, EquipmentSlot.HAND));
        mockServer.getScheduler().performTicks(deferredTicks);

        assertEquals(0, TestCommon.getPlayerSpellExperience(testPlugin, wandless, O2SpellType.LUMOS),
                "a primary hand left click without a wand should cast nothing");

        cleanupActiveSpells();
    }

    /**
     * rightClickWand brews when the player faces a cauldron with an empty bottle in their off hand, and otherwise
     * waves the wand to check whether it is the player's destined wand.
     *
     * <p>This is the body of the right-click branch of {@code primaryHandInteractEvents}, which cannot be reached
     * through a {@link PlayerInteractEvent} under test: finding the cauldron goes through
     * {@code Ollivanders2Common.playerFacingBlockType}, which calls the MockBukkit-unimplemented
     * {@code Player.getLineOfSight}. Passing the cauldron in leaves only that one lookup uncovered.</p>
     */
    private void rightClickWandTest() {
        World testWorld = mockServer.addSimpleWorld("rightClickWandWorld");
        // a listener built just for this test; it handles no events, as registration only happens in onEnable
        OllivandersListener listener = new OllivandersListener(testPlugin);

        PlayerMock player = mockServer.addPlayer();
        player.setLocation(new Location(testWorld, 100, 40, 100));

        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        assertNotNull(o2p, "the test player should be tracked by the plugin");
        // hold the player's own destined wand, so the wand wave branch is the found-wand one
        ItemStack destinedWand = Ollivanders2API.getItems().getWands().makeWand(o2p.getDestinedWandWood(), o2p.getDestinedWandCore(), 1);
        assertNotNull(destinedWand, "should be able to build the player's destined wand");
        player.getInventory().setItemInMainHand(destinedWand);

        // facing a cauldron with an empty bottle in the off hand brews; the empty cauldron brews nothing and says so
        Block cauldron = createHotCauldron(new Location(testWorld, 110, 4, 0));
        player.getInventory().setItemInOffHand(new ItemStack(Material.GLASS_BOTTLE, 1));
        TestCommon.clearMessageQueue(player);

        listener.rightClickWand(player, cauldron);

        String brewMessage = TestCommon.getWholeMessage(player);
        assertNotNull(brewMessage, "right clicking a cauldron with a bottle should attempt to brew and message the player");
        assertTrue(TestCommon.messageStartsWith("The cauldron appears unchanged", brewMessage),
                "brewing an empty cauldron should tell the player nothing was brewed, got: " + brewMessage);
        assertFalse(o2p.foundWand(), "right clicking to brew should not also wave the wand");

        // facing a cauldron without a bottle in the off hand waves the wand instead
        player.getInventory().setItemInOffHand(null);
        TestCommon.clearMessageQueue(player);

        listener.rightClickWand(player, cauldron);

        assertTrue(o2p.foundWand(), "right clicking a cauldron without a bottle should wave the wand");
        assertNull(TestCommon.getWholeMessage(player), "waving the wand should not send a brewing message");

        // not facing a cauldron at all waves the wand even with a bottle in the off hand
        o2p.setFoundWand(false);
        assertFalse(o2p.foundWand(), "the found wand flag should be clear before the next wand wave");
        player.getInventory().setItemInOffHand(new ItemStack(Material.GLASS_BOTTLE, 1));
        TestCommon.clearMessageQueue(player);

        listener.rightClickWand(player, null);

        assertTrue(o2p.foundWand(), "right clicking away from a cauldron should wave the wand");
        assertNull(TestCommon.getWholeMessage(player), "waving the wand should not send a brewing message");
    }

    /**
     * Create a water cauldron over a heat source, as brewing requires.
     *
     * @param location where to put the cauldron
     * @return the cauldron block
     */
    private Block createHotCauldron(@NotNull Location location) {
        Block cauldron = location.getBlock();
        cauldron.setType(Material.WATER_CAULDRON);

        // take the heat source from the plugin's own list so this test follows any change to it
        List<Material> hotBlocks = Ollivanders2Common.getHotBlocks();
        assertFalse(hotBlocks.isEmpty(), "Ollivanders2Common should define at least one hot block");
        cauldron.getRelative(BlockFace.DOWN).setType(hotBlocks.getFirst());

        return cauldron;
    }

    /**
     * onPlayerInteract rotates the player's non-verbal master spell on an off-hand click while holding a wand there.
     */
    private void onPlayerInteractSecondaryHandTest() {
        World testWorld = mockServer.addSimpleWorld("interactSecondaryWorld");
        Location location = new Location(testWorld, 100, 40, 100);

        PlayerMock player = mockServer.addPlayer();
        player.setLocation(location);
        O2Player o2p = testPlugin.getO2Player(player);

        // mastering a spell adds it to the mastered list; the first mastered spell becomes the master spell
        TestCommon.setPlayerSpellExperience(testPlugin, player, O2SpellType.LUMOS, O2Spell.spellMasteryLevel);
        TestCommon.setPlayerSpellExperience(testPlugin, player, O2SpellType.ACCIO, O2Spell.spellMasteryLevel);
        assertEquals(O2SpellType.LUMOS, o2p.getMasterSpell(), "the first mastered spell should be the master spell");

        ItemStack wand = O2ItemType.ELDER_WAND.getItem(1);
        player.getInventory().setItemInOffHand(wand);

        boolean originalNonVerbal = Ollivanders2.enableNonVerbalSpellCasting;
        Ollivanders2.enableNonVerbalSpellCasting = true;
        try {
            mockServer.getPluginManager().callEvent(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, wand,
                null, BlockFace.SELF, EquipmentSlot.OFF_HAND));
            mockServer.getScheduler().performTicks(deferredTicks);

            assertEquals(O2SpellType.ACCIO, o2p.getMasterSpell(),
                    "an off-hand click with a wand should rotate the master spell");
        }
        finally {
            Ollivanders2.enableNonVerbalSpellCasting = originalNonVerbal;
        }
    }

    /**
     * onPlayerJoin tracks a joining player and refreshes a returning player's display name if it changed while they
     * were offline.
     */
    private void onPlayerJoinTest() {
        World testWorld = mockServer.addSimpleWorld("joinWorld");
        PlayerMock player = mockServer.addPlayer();
        player.setLocation(new Location(testWorld, 100, 40, 100));

        mockServer.getPluginManager().callEvent(new PlayerJoinEvent(player, "joined"));
        mockServer.getScheduler().performTicks(deferredTicks);

        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        assertNotNull(o2p, "onPlayerJoin should leave the joining player tracked by the plugin");

        // a returning player whose name changed while offline has the stored name refreshed on join
        String staleName = "staleName";
        o2p.setPlayerName(staleName);

        mockServer.getPluginManager().callEvent(new PlayerJoinEvent(player, "joined"));
        mockServer.getScheduler().performTicks(deferredTicks);

        O2Player rejoined = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        assertNotNull(rejoined, "the returning player should still be tracked");
        assertEquals(player.getName(), rejoined.getPlayerName(),
                "onPlayerJoin should update a returning player's stored name to their current name");
    }

    /**
     * onPlayerQuit runs the player's quit actions and leaves them tracked so their data is still saved.
     *
     * @implNote {@link O2Player#onQuit} currently does nothing observable, so this only verifies the handler runs
     * without error and does not drop the player from the plugin.
     */
    private void onPlayerQuitTest() {
        World testWorld = mockServer.addSimpleWorld("quitWorld");
        PlayerMock player = mockServer.addPlayer();
        player.setLocation(new Location(testWorld, 100, 40, 100));
        testPlugin.getO2Player(player);

        mockServer.getPluginManager().callEvent(new PlayerQuitEvent(player, "quit"));
        mockServer.getScheduler().performTicks(deferredTicks);

        assertNotNull(Ollivanders2API.getPlayers().getPlayer(player.getUniqueId()),
                "onPlayerQuit should not stop tracking the player");
    }

    /**
     * onPlayerDeath runs the player's death actions on a delay: their collected souls are reset and any queued spell
     * is discarded.
     */
    private void onPlayerDeathTest() {
        World testWorld = mockServer.addSimpleWorld("deathWorld");
        PlayerMock player = mockServer.addPlayer();
        player.setLocation(new Location(testWorld, 100, 40, 100));

        O2Player o2p = testPlugin.getO2Player(player);
        o2p.setSouls(5);
        o2p.setWandSpell(O2SpellType.LUMOS);

        DamageSource damageSource = DamageSource.builder(DamageType.IN_FIRE)
                .withDamageLocation(player.getLocation())
                .build();
        mockServer.getPluginManager().callEvent(new PlayerDeathEvent(player, damageSource, new ArrayList<>(), 0, "died"));
        mockServer.getScheduler().performTicks(deferredTicks);

        assertEquals(0, o2p.getSouls(), "onPlayerDeath should reset the player's souls");
        assertNull(o2p.getWandSpell(), "onPlayerDeath should discard the player's queued spell");
    }

    /**
     * onEntityDamage awards the attacker a soul only when their blow is what kills the other player.
     */
    private void onEntityDamageTest() {
        World testWorld = mockServer.addSimpleWorld("entityDamageWorld");
        Location location = new Location(testWorld, 100, 40, 100);

        PlayerMock attacker = mockServer.addPlayer();
        attacker.setLocation(location);
        O2Player attackerO2p = testPlugin.getO2Player(attacker);
        attackerO2p.setSouls(0);

        PlayerMock damaged = mockServer.addPlayer();
        damaged.setLocation(location);
        testPlugin.getO2Player(damaged);

        DamageSource damageSource = DamageSource.builder(DamageType.PLAYER_ATTACK)
                .withDamageLocation(location)
                .build();

        // a blow the other player survives awards nothing
        double survivableHealth = 20.0;
        double smallDamage = 1.0;
        damaged.setHealth(survivableHealth);
        mockServer.getPluginManager().callEvent(new EntityDamageByEntityEvent(attacker, damaged,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK, damageSource, smallDamage));
        mockServer.getScheduler().performTicks(deferredTicks);

        assertEquals(0, attackerO2p.getSouls(), "a non-fatal blow should not award the attacker a soul");

        // a blow that takes the other player to zero health awards a soul
        double lethalDamage = 5.0;
        damaged.setHealth(lethalDamage - 1);
        mockServer.getPluginManager().callEvent(new EntityDamageByEntityEvent(attacker, damaged,
                EntityDamageEvent.DamageCause.ENTITY_ATTACK, damageSource, lethalDamage));
        mockServer.getScheduler().performTicks(deferredTicks);

        assertEquals(1, attackerO2p.getSouls(), "a fatal blow should award the attacker a soul");
    }

    /**
     * onCloakPlayer cancels a mob targeting an invisible player, from any source of invisibility, and leaves targeting
     * of a visible player alone.
     */
    private void onCloakPlayerTest() {
        World testWorld = mockServer.addSimpleWorld("cloakWorld");
        Location location = new Location(testWorld, 100, 40, 100);

        Zombie zombie = (Zombie) testWorld.spawnEntity(location, EntityType.ZOMBIE);

        // a visible player can be targeted
        PlayerMock visible = mockServer.addPlayer();
        visible.setLocation(location);

        EntityTargetEvent visibleEvent = new EntityTargetEvent(zombie, visible, EntityTargetEvent.TargetReason.CLOSEST_PLAYER);
        mockServer.getPluginManager().callEvent(visibleEvent);
        assertFalse(visibleEvent.isCancelled(), "targeting a visible player should not be cancelled");

        // an invisible player cannot
        PlayerMock invisible = mockServer.addPlayer();
        invisible.setLocation(location);
        int duration = 20 * 60; // ticks, long enough to outlast the assertion
        invisible.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 1));

        EntityTargetEvent invisibleEvent = new EntityTargetEvent(zombie, invisible, EntityTargetEvent.TargetReason.CLOSEST_PLAYER);
        mockServer.getPluginManager().callEvent(invisibleEvent);
        assertTrue(invisibleEvent.isCancelled(), "targeting an invisible player should be cancelled");
    }

    /**
     * onWitchWandDrop drops a wand where a witch died, but only when the witch drop config option is enabled.
     */
    private void onWitchWandDropTest() {
        World testWorld = mockServer.addSimpleWorld("witchDropWorld");

        boolean originalWitchDrop = Ollivanders2.enableWitchDrop;
        try {
            // disabled: a dying witch drops no wand
            Location disabledLocation = new Location(testWorld, 100, 40, 100);
            Ollivanders2.enableWitchDrop = false;
            killWitchAt(disabledLocation);

            assertTrue(getWandsNear(disabledLocation).isEmpty(),
                    "no wand should drop when the witch drop option is disabled");

            // enabled: a dying witch drops a wand
            Location enabledLocation = new Location(testWorld, 200, 40, 100);
            Ollivanders2.enableWitchDrop = true;
            killWitchAt(enabledLocation);

            assertFalse(getWandsNear(enabledLocation).isEmpty(),
                    "a wand should drop where a witch died when the witch drop option is enabled");
        }
        finally {
            Ollivanders2.enableWitchDrop = originalWitchDrop;
        }
    }

    /**
     * Spawn a witch at a location and fire its death event, letting the deferred drop handler run.
     *
     * @param location where the witch dies
     */
    private void killWitchAt(@NotNull Location location) {
        Witch witch = (Witch) location.getWorld().spawnEntity(location, EntityType.WITCH);

        DamageSource damageSource = DamageSource.builder(DamageType.PLAYER_ATTACK)
                .withDamageLocation(location)
                .build();
        mockServer.getPluginManager().callEvent(new EntityDeathEvent(witch, damageSource, new ArrayList<>()));
        mockServer.getScheduler().performTicks(deferredTicks);
    }

    /**
     * Find all dropped wands close to a location.
     *
     * @param location the location to search around
     * @return the dropped items at that location that are wands, empty if there are none
     */
    private List<Item> getWandsNear(@NotNull Location location) {
        // small radius: the drop lands within a block of where the witch died
        double dropRadius = 2.0;

        List<Item> wands = new ArrayList<>();
        for (Item item : EntityCommon.getItemsInRadius(location, dropRadius)) {
            if (Ollivanders2API.getItems().getWands().isWand(item.getItemStack()))
                wands.add(item);
        }

        return wands;
    }

    /**
     * onSpellJournalHold swaps a held spell journal for a freshly generated one, and leaves other written books alone.
     */
    private void onSpellJournalHoldTest() {
        World testWorld = mockServer.addSimpleWorld("spellJournalWorld");
        PlayerMock player = mockServer.addPlayer();
        player.setLocation(new Location(testWorld, 100, 40, 100));
        testPlugin.getO2Player(player);
        TestCommon.setPlayerSpellExperience(testPlugin, player, O2SpellType.LUMOS, 10);

        boolean originalSpellJournal = Ollivanders2.useSpellJournal;
        Ollivanders2.useSpellJournal = true;
        try {
            // a book titled "Spell Journal" is replaced with the player's generated journal, which is authored by them
            int journalSlot = 0;
            player.getInventory().setItem(journalSlot, makeWrittenBook("Spell Journal"));

            mockServer.getPluginManager().callEvent(new PlayerItemHeldEvent(player, 1, journalSlot));

            ItemStack heldJournal = player.getInventory().getItem(journalSlot);
            assertNotNull(heldJournal, "the spell journal slot should not be empty after the swap");
            BookMeta journalMeta = (BookMeta) heldJournal.getItemMeta();
            assertNotNull(journalMeta, "the swapped-in spell journal should have book meta");
            assertEquals(player.getName(), journalMeta.getAuthor(),
                    "onSpellJournalHold should swap in the player's own generated spell journal");

            // any other written book is left untouched
            int otherSlot = 1;
            String otherTitle = "Quidditch Through the Ages";
            player.getInventory().setItem(otherSlot, makeWrittenBook(otherTitle));

            mockServer.getPluginManager().callEvent(new PlayerItemHeldEvent(player, journalSlot, otherSlot));

            ItemStack heldOther = player.getInventory().getItem(otherSlot);
            assertNotNull(heldOther, "the other book slot should not be empty");
            BookMeta otherMeta = (BookMeta) heldOther.getItemMeta();
            assertNotNull(otherMeta, "the other book should still have book meta");
            assertEquals(otherTitle, otherMeta.getTitle(), "onSpellJournalHold should not touch other written books");

            // with the spell journal disabled, even a spell journal is left untouched
            Ollivanders2.useSpellJournal = false;
            int disabledSlot = 2;
            player.getInventory().setItem(disabledSlot, makeWrittenBook("Spell Journal"));

            mockServer.getPluginManager().callEvent(new PlayerItemHeldEvent(player, otherSlot, disabledSlot));

            ItemStack heldDisabled = player.getInventory().getItem(disabledSlot);
            assertNotNull(heldDisabled, "the disabled journal slot should not be empty");
            BookMeta disabledMeta = (BookMeta) heldDisabled.getItemMeta();
            assertNotNull(disabledMeta, "the untouched book should still have book meta");
            assertNull(disabledMeta.getAuthor(),
                    "onSpellJournalHold should do nothing when the spell journal option is disabled");
        }
        finally {
            Ollivanders2.useSpellJournal = originalSpellJournal;
        }
    }

    /**
     * Make an unauthored written book with a title, so that a swap for a generated journal is detectable by author.
     *
     * @param title the title to give the book
     * @return the book item
     */
    private ItemStack makeWrittenBook(@NotNull String title) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);

        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        assertNotNull(bookMeta, "should be able to build book meta for a written book");
        bookMeta.setTitle(title);
        book.setItemMeta(bookMeta);

        return book;
    }

    /**
     * Kill all active spells and run upkeep to remove them from the active list.
     */
    private void cleanupActiveSpells() {
        for (O2Spell spell : Ollivanders2API.getSpells().getActiveSpells()) {
            spell.kill();
        }
        Ollivanders2API.getSpells().upkeep();
    }

    /**
     * Stop the MockBukkit server after all tests in the class complete.
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}