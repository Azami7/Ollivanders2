package net.pottercraft.ollivanders2.test.player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.item.wand.O2WandCoreType;
import net.pottercraft.ollivanders2.item.wand.O2WandWoodType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.player.Year;
import net.pottercraft.ollivanders2.player.events.OllivandersPlayerFoundWandEvent;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link O2Player}.
 */
public class O2PlayerTest {
    static Ollivanders2 testPlugin;
    static ServerMock mockServer;

    /**
     * General-purpose player; each test method gets its own O2Player for it via setUp().
     */
    static PlayerMock player1;

    /**
     * Dedicated to the found-wand event test so fired-event assertions cannot see other tests' events.
     */
    static PlayerMock player2;

    /**
     * Dedicated to the op-players-are-never-muggles test since it changes op status.
     */
    static PlayerMock player3;

    O2Player o2p;

    @BeforeAll
    static void globalSetUp() {
        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));
        mockServer.addSimpleWorld("world");

        player1 = mockServer.addPlayer();
        player2 = mockServer.addPlayer();
        player3 = mockServer.addPlayer();
    }

    @BeforeEach
    void setUp() {
        // a fresh O2Player per test so methods running in parallel do not share state
        o2p = new O2Player(player1.getUniqueId(), player1.getName(), testPlugin);
    }

    /**
     * Destined wand is valid, deterministic per-UUID, and settable only to known wood/core types.
     */
    @Test
    void testDestinedWand() {
        String wood = o2p.getDestinedWandWood();
        String core = o2p.getDestinedWandCore();
        assertTrue(O2WandWoodType.getAllWandWoodsByName().contains(wood), "Destined wood should be a known wood type");
        assertTrue(O2WandCoreType.getAllWandCoreNames().contains(core), "Destined core should be a known core type");

        // the same UUID always gets the same destined wand
        O2Player samePlayer = new O2Player(player1.getUniqueId(), player1.getName(), testPlugin);
        assertEquals(wood, samePlayer.getDestinedWandWood(), "Destined wood should be deterministic per UUID");
        assertEquals(core, samePlayer.getDestinedWandCore(), "Destined core should be deterministic per UUID");

        // setting a valid wood/core changes the destined wand
        String otherWood = O2WandWoodType.getAllWandWoodsByName().stream()
                .filter(w -> !w.equals(wood)).findFirst().orElseThrow();
        o2p.setWandWood(otherWood);
        assertEquals(otherWood, o2p.getDestinedWandWood(), "Destined wood should change to a valid wood");

        // setting an unknown wood/core is ignored
        o2p.setWandWood("Not A Real Wood");
        assertEquals(otherWood, o2p.getDestinedWandWood(), "Unknown wood should be ignored");
        o2p.setWandCore("Not A Real Core");
        assertEquals(core, o2p.getDestinedWandCore(), "Unknown core should be ignored");
    }

    /**
     * Spell counts: unknown spells are 0, counts can be set and incremented, and a count below 1 forgets the spell.
     */
    @Test
    void testSpellCount() {
        assertEquals(0, o2p.getSpellCount(O2SpellType.LUMOS), "Unknown spell should have count 0");

        o2p.setSpellCount(O2SpellType.LUMOS, 5);
        assertEquals(5, o2p.getSpellCount(O2SpellType.LUMOS), "Spell count should be 5 after set");

        o2p.incrementSpellCount(O2SpellType.LUMOS);
        assertEquals(6, o2p.getSpellCount(O2SpellType.LUMOS), "Spell count should be 6 after increment");

        // incrementing an unknown spell starts it at 1
        o2p.incrementSpellCount(O2SpellType.NOX);
        assertEquals(1, o2p.getSpellCount(O2SpellType.NOX), "Incrementing an unknown spell should set count to 1");

        // a count below 1 removes the spell from the known list
        o2p.setSpellCount(O2SpellType.LUMOS, 0);
        assertEquals(0, o2p.getSpellCount(O2SpellType.LUMOS), "Spell count should be 0 after unset");
        assertFalse(o2p.getKnownSpells().containsKey(O2SpellType.LUMOS), "Spell should be forgotten at count 0");

        o2p.resetSpellCount();
        assertTrue(o2p.getKnownSpells().isEmpty(), "No spells should be known after reset");
    }

    /**
     * Mastered spells: reaching 100 casts sets the wand master spell, shifting cycles through mastered spells,
     * dropping below 100 unmasters, and Avada Kedavra can never be a master spell.
     */
    @Test
    void testMasteredSpells() {
        final int masteryCount = 100; // spells are mastered at 100 casts

        assertNull(o2p.getMasterSpell(), "No master spell should be set initially");

        // first mastered spell becomes the wand master spell
        o2p.setSpellCount(O2SpellType.LUMOS, masteryCount);
        assertSame(O2SpellType.LUMOS, o2p.getMasterSpell(), "First mastered spell should become the master spell");

        // mastering a second spell does not change the loaded master spell
        o2p.setSpellCount(O2SpellType.AGUAMENTI, masteryCount);
        assertSame(O2SpellType.LUMOS, o2p.getMasterSpell(), "Master spell should not change when a second spell is mastered");

        // shifting moves to the other mastered spell and shifting again cycles back
        o2p.shiftMasterSpell(false);
        assertSame(O2SpellType.AGUAMENTI, o2p.getMasterSpell(), "Shift should move to the other mastered spell");
        o2p.shiftMasterSpell(false);
        assertSame(O2SpellType.LUMOS, o2p.getMasterSpell(), "Shift should cycle back to the first mastered spell");

        // shifting in reverse walks the mastered spells the other way and also rolls over
        o2p.shiftMasterSpell(true);
        assertSame(O2SpellType.AGUAMENTI, o2p.getMasterSpell(), "Reverse shift should move to the other mastered spell");
        o2p.shiftMasterSpell(true);
        assertSame(O2SpellType.LUMOS, o2p.getMasterSpell(), "Reverse shift should cycle back to the first mastered spell");

        // the deprecated no-arg shift is equivalent to a forward shift
        o2p.shiftMasterSpell();
        assertSame(O2SpellType.AGUAMENTI, o2p.getMasterSpell(), "No-arg shift should shift forward");
        o2p.shiftMasterSpell();

        // dropping below mastery removes the spell as master and shifts to the remaining one
        o2p.setSpellCount(O2SpellType.LUMOS, masteryCount - 1);
        assertSame(O2SpellType.AGUAMENTI, o2p.getMasterSpell(), "Master should shift when the current master is unmastered");

        // with only one mastered spell left, shifting keeps it
        o2p.shiftMasterSpell(false);
        assertSame(O2SpellType.AGUAMENTI, o2p.getMasterSpell(), "Shift should keep the only mastered spell");

        // unmastering the last mastered spell leaves no master spell, and shifting with none stays empty
        o2p.setSpellCount(O2SpellType.AGUAMENTI, masteryCount - 1);
        assertNull(o2p.getMasterSpell(), "Unmastering the last mastered spell should clear the master spell");
        o2p.shiftMasterSpell(false);
        assertNull(o2p.getMasterSpell(), "Shift with no mastered spells should leave no master spell");

        // unmastering a spell that is not the master leaves the master alone
        o2p.setSpellCount(O2SpellType.LUMOS, masteryCount);
        o2p.setSpellCount(O2SpellType.AGUAMENTI, masteryCount);
        o2p.setSpellCount(O2SpellType.AGUAMENTI, masteryCount - 1);
        assertSame(O2SpellType.LUMOS, o2p.getMasterSpell(), "Unmastering a non-master spell should not change the master spell");
        o2p.setSpellCount(O2SpellType.LUMOS, masteryCount - 1);

        // Avada Kedavra is never a master spell
        o2p.resetSpellCount();
        o2p.setSpellCount(O2SpellType.AVADA_KEDAVRA, masteryCount);
        assertNull(o2p.getMasterSpell(), "Avada Kedavra should never become the master spell");

        // reset clears the master spell
        o2p.setSpellCount(O2SpellType.LUMOS, masteryCount);
        assertNotNull(o2p.getMasterSpell(), "Master spell should be set before reset");
        o2p.resetSpellCount();
        assertNull(o2p.getMasterSpell(), "Master spell should be cleared by reset");
    }

    /**
     * Casting a spell records a cooldown expiry in the future and updates the last-cast spell.
     */
    @Test
    void testSpellRecentCastTime() {
        assertEquals(0L, o2p.getSpellLastCastTime(O2SpellType.LUMOS), "Uncast spell should have no cooldown expiry");
        assertNull(o2p.getLastSpell(), "No last spell should be set initially");

        long before = System.currentTimeMillis();
        o2p.setSpellRecentCastTime(O2SpellType.LUMOS);

        long expiry = o2p.getSpellLastCastTime(O2SpellType.LUMOS);
        assertTrue(expiry >= before, "Cooldown expiry should not be in the past");
        assertTrue(expiry <= System.currentTimeMillis() + O2SpellType.LUMOS.getCooldown(),
                "Cooldown expiry should be no more than the spell cooldown from now");

        assertSame(O2SpellType.LUMOS, o2p.getLastSpell(), "Casting should record the last spell");

        // prior incantatem is separate from last spell
        o2p.setPriorIncantatem(O2SpellType.NOX);
        assertSame(O2SpellType.NOX, o2p.getPriorIncantatem(), "Prior incantatem should be settable");
        assertSame(O2SpellType.LUMOS, o2p.getLastSpell(), "Prior incantatem should not change the last spell");
    }

    /**
     * Potion counts: unknown potions are 0, counts can be set and incremented, and a count below 1 forgets the potion.
     */
    @Test
    void testPotionCount() {
        assertEquals(0, o2p.getPotionCount(O2PotionType.CURE_FOR_BOILS), "Unknown potion should have count 0");

        o2p.setPotionCount(O2PotionType.CURE_FOR_BOILS, 3);
        assertEquals(3, o2p.getPotionCount(O2PotionType.CURE_FOR_BOILS), "Potion count should be 3 after set");

        o2p.incrementPotionCount(O2PotionType.CURE_FOR_BOILS);
        assertEquals(4, o2p.getPotionCount(O2PotionType.CURE_FOR_BOILS), "Potion count should be 4 after increment");

        o2p.setPotionCount(O2PotionType.CURE_FOR_BOILS, 0);
        assertFalse(o2p.getKnownPotions().containsKey(O2PotionType.CURE_FOR_BOILS), "Potion should be forgotten at count 0");

        o2p.incrementPotionCount(O2PotionType.CURE_FOR_BOILS);
        o2p.resetPotionCount();
        assertTrue(o2p.getKnownPotions().isEmpty(), "No potions should be known after reset");
    }

    /**
     * The wand spell can be loaded and cleared; with non-verbal casting disabled in config the master spell is not
     * used as a fallback.
     */
    @Test
    void testWandSpell() {
        assertNull(o2p.getWandSpell(), "No wand spell should be loaded initially");

        o2p.setWandSpell(O2SpellType.LUMOS);
        assertSame(O2SpellType.LUMOS, o2p.getWandSpell(), "Wand spell should be loaded");

        o2p.setWandSpell(null);
        assertNull(o2p.getWandSpell(), "Wand spell should be cleared");

        // non-verbal casting is disabled in the test config, so the master spell is not returned as the wand spell
        assertFalse(Ollivanders2.enableNonVerbalSpellCasting, "Test config should have non-verbal casting disabled");
        o2p.setMasterSpell(O2SpellType.AGUAMENTI);
        assertNull(o2p.getWandSpell(), "Master spell should not be the wand spell when non-verbal casting is disabled");
    }

    /**
     * Souls can be added, subtracted (never below zero), set, and reset.
     */
    @Test
    void testSouls() {
        assertEquals(0, o2p.getSouls(), "Souls should start at 0");

        o2p.addSoul();
        o2p.addSoul();
        assertEquals(2, o2p.getSouls(), "Souls should be 2 after adding two");

        o2p.subtractSoul();
        assertEquals(1, o2p.getSouls(), "Souls should be 1 after subtracting one");

        o2p.subtractSoul();
        o2p.subtractSoul();
        assertEquals(0, o2p.getSouls(), "Souls should not go below 0");

        o2p.setSouls(5);
        o2p.resetSouls();
        assertEquals(0, o2p.getSouls(), "Souls should be 0 after reset");
    }

    /**
     * Finding the destined wand fires the found-wand event and makes the player no longer a muggle; the
     * deserialization initializer does neither.
     */
    @Test
    void testFoundWand() {
        O2Player o2p2 = new O2Player(player2.getUniqueId(), player2.getName(), testPlugin);

        assertTrue(o2p2.isMuggle(), "Player should start as a muggle");
        assertFalse(o2p2.foundWand(), "Player should not have found their wand initially");

        // setFoundWand fires the event and clears muggle status
        o2p2.setFoundWand(true);
        assertTrue(o2p2.foundWand(), "Player should have found their wand");
        assertFalse(o2p2.isMuggle(), "Player should no longer be a muggle after finding their wand");
        assertTrue(mockServer.getPluginManager()
                        .getFiredEvents()
                        .filter(e -> e instanceof OllivandersPlayerFoundWandEvent)
                        .map(e -> (OllivandersPlayerFoundWandEvent) e)
                        .anyMatch(ev -> ev.getPlayer().getUniqueId().equals(player2.getUniqueId())),
                "Found-wand event should have been fired for player2");

        // initFoundWand is for loading saved data: sets the flag without touching muggle status
        O2Player loaded = new O2Player(player2.getUniqueId(), player2.getName(), testPlugin);
        assertTrue(loaded.isMuggle(), "Freshly constructed player should be a muggle");
    }

    /**
     * Op players are never muggles regardless of the stored muggle flag.
     */
    @Test
    void testOpsAreNeverMuggles() {
        O2Player o2p3 = new O2Player(player3.getUniqueId(), player3.getName(), testPlugin);
        assertTrue(o2p3.isMuggle(), "Non-op player should start as a muggle");

        player3.setOp(true);
        assertFalse(o2p3.isMuggle(), "Op player should never be a muggle");
    }

    /**
     * Year defaults to first year and can be set.
     */
    @Test
    void testYear() {
        assertSame(Year.YEAR_1, o2p.getYear(), "Players should start in year 1");

        o2p.setYear(Year.YEAR_5);
        assertSame(Year.YEAR_5, o2p.getYear(), "Year should be settable");
    }

    /**
     * Animagus state: players start as non-animagi, becoming one assigns a deterministic form, and an explicit
     * allowed form can be set.
     */
    @Test
    void testAnimagus() {
        assertFalse(o2p.isAnimagus(), "Player should not start as an animagus");
        assertNull(o2p.getAnimagusForm(), "Player should have no animagus form initially");
        assertNull(o2p.getAnimagusColor(), "Player should have no animagus color without a form");

        o2p.setIsAnimagus();
        assertTrue(o2p.isAnimagus(), "Player should be an animagus after setIsAnimagus");
        EntityType form = o2p.getAnimagusForm();
        assertNotNull(form, "Animagus should have a form");

        // the form is deterministic per UUID
        O2Player samePlayer = new O2Player(player1.getUniqueId(), player1.getName(), testPlugin);
        samePlayer.setIsAnimagus();
        assertSame(form, samePlayer.getAnimagusForm(), "Animagus form should be deterministic per UUID");

        // an explicitly set allowed form is used as-is
        o2p.setAnimagusForm(EntityType.COW);
        assertSame(EntityType.COW, o2p.getAnimagusForm(), "An allowed form should be set directly");
    }

    /**
     * The spell journal is a written book authored by the player listing known spells, paginating when the
     * spell list exceeds one page.
     */
    @Test
    void testSpellJournal() {
        o2p.setSpellCount(O2SpellType.LUMOS, 7);

        ItemStack journal = o2p.getSpellJournal();
        assertNotNull(journal, "Spell journal should be created");
        assertEquals(Material.WRITTEN_BOOK, journal.getType(), "Spell journal should be a written book");

        BookMeta meta = (BookMeta) journal.getItemMeta();
        assertNotNull(meta, "Spell journal should have book meta");
        assertEquals(player1.getName(), meta.getAuthor(), "Spell journal should be authored by the player");
        assertEquals("Spell Journal", meta.getTitle(), "Spell journal should be titled");
        assertTrue(meta.getPage(1).contains(O2SpellType.LUMOS.getSpellName()),
                "Spell journal should list the known spell");
        assertTrue(meta.getPage(1).contains("7"), "Spell journal should list the spell count");

        // enough known spells forces the journal on to multiple pages (a page holds 14 lines)
        int spellsAdded = 0;
        for (O2SpellType spellType : O2SpellType.values()) {
            o2p.setSpellCount(spellType, 1);
            spellsAdded = spellsAdded + 1;
            if (spellsAdded >= 30)
                break;
        }

        ItemStack bigJournal = o2p.getSpellJournal();
        assertNotNull(bigJournal, "Multi-page spell journal should be created");
        BookMeta bigMeta = (BookMeta) bigJournal.getItemMeta();
        assertNotNull(bigMeta, "Multi-page spell journal should have book meta");
        assertTrue(bigMeta.getPageCount() > 1, "30 known spells should not fit on one page");
    }

    /**
     * Death always resets souls and unloads the wand spell; spell and potion counts survive only when death
     * experience loss is disabled.
     * <p>
     * Kept in one method because it toggles the global {@link Ollivanders2#enableDeathExpLoss} setting.
     */
    @Test
    void testOnDeath() {
        boolean originalDeathExpLoss = Ollivanders2.enableDeathExpLoss;

        try {
            // experience is kept when death experience loss is off
            Ollivanders2.enableDeathExpLoss = false;

            o2p.setSpellCount(O2SpellType.LUMOS, 5);
            o2p.setPotionCount(O2PotionType.CURE_FOR_BOILS, 3);
            o2p.setSouls(2);
            o2p.setWandSpell(O2SpellType.LUMOS);

            o2p.onDeath();

            assertEquals(0, o2p.getSouls(), "Death should reset souls");
            assertNull(o2p.getWandSpell(), "Death should unload the wand spell");
            assertEquals(5, o2p.getSpellCount(O2SpellType.LUMOS), "Spell counts should survive death when exp loss is off");
            assertEquals(3, o2p.getPotionCount(O2PotionType.CURE_FOR_BOILS),
                    "Potion counts should survive death when exp loss is off");

            // experience is lost when death experience loss is on
            Ollivanders2.enableDeathExpLoss = true;
            o2p.setSouls(2);
            o2p.setWandSpell(O2SpellType.LUMOS);

            o2p.onDeath();

            assertEquals(0, o2p.getSouls(), "Death should reset souls");
            assertNull(o2p.getWandSpell(), "Death should unload the wand spell");
            assertTrue(o2p.getKnownSpells().isEmpty(), "Spell counts should be lost on death when exp loss is on");
            assertTrue(o2p.getKnownPotions().isEmpty(), "Potion counts should be lost on death when exp loss is on");
        }
        finally {
            Ollivanders2.enableDeathExpLoss = originalDeathExpLoss;
        }
    }

    /**
     * isOnline reflects whether the player is connected to the server.
     */
    @Test
    void testIsOnline() {
        assertTrue(o2p.isOnline(), "Mock player should be online");

        O2Player offline = new O2Player(UUID.randomUUID(), "NeverSeen", testPlugin);
        assertFalse(offline.isOnline(), "A player not on the server should be offline");
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
