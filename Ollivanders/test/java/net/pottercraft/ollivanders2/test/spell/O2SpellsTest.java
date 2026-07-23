package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.FAST_LEARNING;
import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.item.wand.O2WandCoreType;
import net.pottercraft.ollivanders2.item.wand.O2WandWoodType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.Divination;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.O2Spells;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.FileConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link O2Spells} manager: spell loading and lookup, active-spell management, cast-count
 * tracking, creation, upkeep, and zone permissions. Everything runs in one method sharing a static MockBukkit server
 * because the tests would interfere with each other's server state if parallelized.
 *
 * @author Azami7
 */
@Isolated
public class O2SpellsTest {
    /**
     * Shared MockBukkit server, created once for all tests in the class.
     */
    static ServerMock mockServer;

    /**
     * The plugin under test, loaded once with the default config.
     */
    static Ollivanders2 testPlugin;

    /**
     * Start the shared MockBukkit server and load the plugin before any tests run.
     */
    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Run all O2Spells manager checks in sequence (see the class comment for why they share one method).
     */
    @Test
    void o2SpellsTest() {
        testGetAllSpellTypes();
        testGetSpellTypeByName();
        testIsLoaded();
        testParseSpell();
        testSpeakIncantationWandCheck();
        testSpeakIncantationUntrackedWand();
        testSpeakIncantationBookLearning();
        testSpeakIncantationDivine();
        testCastSpell();
        testRotateNonVerbalSpell();
        testCreateSpell();
        testAddSpellAndGetActiveSpells();
        testAddSpellIncrementsCastCount();
        testAddSpellWithFastLearning();
        testUpkeepRemovesKilledSpells();
        testOnDisable();
        testIsSpellTypeAllowedDefault();
        testIsSpellTypeAllowedGlobalDisallow();
        testIsSpellTypeAllowedGlobalAllow();
    }

    /**
     * Test that getAllSpellTypes returns a populated list of loaded spell types.
     */
    private void testGetAllSpellTypes() {
        List<O2SpellType> allSpells = O2Spells.getAllSpellTypes();

        assertNotNull(allSpells, "getAllSpellTypes() should not return null");
        assertFalse(allSpells.isEmpty(), "getAllSpellTypes() should not be empty");
        assertTrue(allSpells.contains(O2SpellType.LUMOS), "getAllSpellTypes() should include LUMOS");
        assertTrue(allSpells.contains(O2SpellType.ACCIO), "getAllSpellTypes() should include ACCIO");
    }

    /**
     * Test spell type lookup by display name, including case insensitivity and unknown names.
     */
    private void testGetSpellTypeByName() {
        // lookup by exact display name
        String lumosName = O2SpellType.LUMOS.getSpellName();
        O2SpellType lumos = Ollivanders2API.getSpells().getSpellTypeByName(lumosName);
        assertEquals(O2SpellType.LUMOS, lumos, "should find LUMOS by its display name");

        // lookup is case insensitive
        O2SpellType lumosUpper = Ollivanders2API.getSpells().getSpellTypeByName(lumosName.toUpperCase());
        assertEquals(O2SpellType.LUMOS, lumosUpper, "lookup should be case insensitive");

        // unknown name returns null
        O2SpellType notFound = Ollivanders2API.getSpells().getSpellTypeByName("not_a_real_spell");
        assertNull(notFound, "should return null for unknown spell name");
    }

    /**
     * Test that isLoaded returns true for standard spell types.
     */
    private void testIsLoaded() {
        assertTrue(Ollivanders2API.getSpells().isLoaded(O2SpellType.LUMOS), "LUMOS should be loaded");
        assertTrue(Ollivanders2API.getSpells().isLoaded(O2SpellType.ACCIO), "ACCIO should be loaded");
    }

    /**
     * Test that createSpell produces a valid spell instance via reflection.
     */
    private void testCreateSpell() {
        World testWorld = mockServer.addSimpleWorld("createSpellWorld");
        Location location = new Location(testWorld, 100, 40, 100);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(location);

        O2Spell spell = Ollivanders2API.getSpells().createSpell(caster, O2SpellType.LUMOS, O2PlayerCommon.rightWand);
        assertNotNull(spell, "createSpell should return a non-null spell");
        assertEquals(O2SpellType.LUMOS, spell.getSpellType(), "created spell should have correct type");
    }

    /**
     * Test that addSpell adds to the active spells list and getActiveSpells returns them.
     */
    private void testAddSpellAndGetActiveSpells() {
        World testWorld = mockServer.addSimpleWorld("addSpellWorld");
        Location location = new Location(testWorld, 100, 40, 100);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(location);

        cleanupActiveSpells();
        assertTrue(Ollivanders2API.getSpells().getActiveSpells().isEmpty(), "active spells should be empty after cleanup");

        O2Spell spell = Ollivanders2API.getSpells().createSpell(caster, O2SpellType.LUMOS, O2PlayerCommon.rightWand);
        assertNotNull(spell);
        Ollivanders2API.getSpells().addSpell(caster, spell);

        List<O2Spell> activeSpells = Ollivanders2API.getSpells().getActiveSpells();
        assertEquals(1, activeSpells.size(), "should have 1 active spell after addSpell");
        assertEquals(O2SpellType.LUMOS, activeSpells.getFirst().getSpellType(), "active spell should be the one we added");

        cleanupActiveSpells();
    }

    /**
     * Test that addSpell increments the caster's spell count by 1.
     */
    private void testAddSpellIncrementsCastCount() {
        World testWorld = mockServer.addSimpleWorld("castCountWorld");
        Location location = new Location(testWorld, 100, 40, 100);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(location);

        TestCommon.setPlayerSpellExperience(testPlugin, caster, O2SpellType.LUMOS, 0);

        O2Spell spell = Ollivanders2API.getSpells().createSpell(caster, O2SpellType.LUMOS, O2PlayerCommon.rightWand);
        assertNotNull(spell);
        Ollivanders2API.getSpells().addSpell(caster, spell);

        int count = TestCommon.getPlayerSpellExperience(testPlugin, caster, O2SpellType.LUMOS);
        assertEquals(1, count, "spell count should be 1 after casting once");

        cleanupActiveSpells();
    }

    /**
     * Test that addSpell increments the caster's spell count by 2 when FAST_LEARNING is active.
     */
    private void testAddSpellWithFastLearning() {
        World testWorld = mockServer.addSimpleWorld("fastLearningWorld");
        Location location = new Location(testWorld, 100, 40, 100);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(location);

        TestCommon.setPlayerSpellExperience(testPlugin, caster, O2SpellType.ACCIO, 0);

        // add FAST_LEARNING effect and let it activate
        O2Effect fastLearning = new FAST_LEARNING(testPlugin, 1000, false, caster.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(fastLearning);
        mockServer.getScheduler().performTicks(20);

        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(caster.getUniqueId(), O2EffectType.FAST_LEARNING),
                "player should have FAST_LEARNING effect");

        O2Spell spell = Ollivanders2API.getSpells().createSpell(caster, O2SpellType.ACCIO, O2PlayerCommon.rightWand);
        assertNotNull(spell);
        Ollivanders2API.getSpells().addSpell(caster, spell);

        int count = TestCommon.getPlayerSpellExperience(testPlugin, caster, O2SpellType.ACCIO);
        assertEquals(2, count, "spell count should be 2 with FAST_LEARNING (double increment)");

        // cleanup
        Ollivanders2API.getPlayers().playerEffects.removeAllEffects();
        mockServer.getScheduler().performTicks(5);
        cleanupActiveSpells();
    }

    /**
     * Test that upkeep removes killed spells from the active spells list.
     */
    private void testUpkeepRemovesKilledSpells() {
        World testWorld = mockServer.addSimpleWorld("upkeepWorld");
        Location location = new Location(testWorld, 100, 40, 100);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(location);

        cleanupActiveSpells();

        O2Spell spell = Ollivanders2API.getSpells().createSpell(caster, O2SpellType.LUMOS, O2PlayerCommon.rightWand);
        assertNotNull(spell);
        Ollivanders2API.getSpells().addSpell(caster, spell);
        assertEquals(1, Ollivanders2API.getSpells().getActiveSpells().size(), "should have 1 active spell");

        spell.kill();
        Ollivanders2API.getSpells().upkeep();

        assertTrue(Ollivanders2API.getSpells().getActiveSpells().isEmpty(),
                "killed spell should be removed by upkeep");
    }

    /**
     * Test that onDisable kills all active spells.
     */
    private void testOnDisable() {
        World testWorld = mockServer.addSimpleWorld("disableWorld");
        Location location = new Location(testWorld, 100, 40, 100);
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(location);

        cleanupActiveSpells();

        O2Spell spell1 = Ollivanders2API.getSpells().createSpell(caster, O2SpellType.LUMOS, O2PlayerCommon.rightWand);
        O2Spell spell2 = Ollivanders2API.getSpells().createSpell(caster, O2SpellType.ACCIO, O2PlayerCommon.rightWand);
        assertNotNull(spell1);
        assertNotNull(spell2);
        Ollivanders2API.getSpells().addSpell(caster, spell1);
        Ollivanders2API.getSpells().addSpell(caster, spell2);

        assertFalse(spell1.isKilled(), "spell1 should not be killed before onDisable");
        assertFalse(spell2.isKilled(), "spell2 should not be killed before onDisable");

        Ollivanders2API.getSpells().onDisable();

        assertTrue(spell1.isKilled(), "spell1 should be killed after onDisable");
        assertTrue(spell2.isKilled(), "spell2 should be killed after onDisable");

        // clean up killed spells from the active list
        Ollivanders2API.getSpells().upkeep();
    }

    /**
     * Test that all spells are allowed with the default config (no zone restrictions).
     */
    private void testIsSpellTypeAllowedDefault() {
        World testWorld = mockServer.addSimpleWorld("allowedDefaultWorld");
        Location location = new Location(testWorld, 100, 40, 100);

        assertTrue(Ollivanders2API.getSpells().isSpellTypeAllowed(location, O2SpellType.LUMOS),
                "LUMOS should be allowed with default config");
        assertTrue(Ollivanders2API.getSpells().isSpellTypeAllowed(location, O2SpellType.ACCIO),
                "ACCIO should be allowed with default config");
        assertTrue(Ollivanders2API.getSpells().isSpellTypeAllowed(location, O2SpellType.AVADA_KEDAVRA),
                "AVADA_KEDAVRA should be allowed with default config (no restrictions)");
    }

    /**
     * Test that a globally disallowed spell is blocked while other spells remain allowed.
     */
    private void testIsSpellTypeAllowedGlobalDisallow() {
        World testWorld = mockServer.addSimpleWorld("globalDisallowWorld");
        Location location = new Location(testWorld, 100, 40, 100);

        FileConfiguration config = testPlugin.getConfig();
        config.set("zones.global.disallowed-spells", List.of("AVADA_KEDAVRA"));
        config.set("zones.global.allowed-spells", List.of());
        Ollivanders2API.getSpells().loadZoneConfig();

        assertFalse(Ollivanders2API.getSpells().isSpellTypeAllowed(location, O2SpellType.AVADA_KEDAVRA),
                "AVADA_KEDAVRA should be disallowed by global disallow list");
        assertTrue(Ollivanders2API.getSpells().isSpellTypeAllowed(location, O2SpellType.LUMOS),
                "LUMOS should still be allowed when only AVADA_KEDAVRA is globally disallowed");
    }

    /**
     * Test that a global allow list restricts casting to only the listed spells.
     */
    private void testIsSpellTypeAllowedGlobalAllow() {
        World testWorld = mockServer.addSimpleWorld("globalAllowWorld");
        Location location = new Location(testWorld, 100, 40, 100);

        FileConfiguration config = testPlugin.getConfig();
        config.set("zones.global.allowed-spells", List.of("LUMOS", "NOX"));
        config.set("zones.global.disallowed-spells", List.of());
        Ollivanders2API.getSpells().loadZoneConfig();

        assertTrue(Ollivanders2API.getSpells().isSpellTypeAllowed(location, O2SpellType.LUMOS),
                "LUMOS should be allowed by global allow list");
        assertTrue(Ollivanders2API.getSpells().isSpellTypeAllowed(location, O2SpellType.NOX),
                "NOX should be allowed by global allow list");
        assertFalse(Ollivanders2API.getSpells().isSpellTypeAllowed(location, O2SpellType.ACCIO),
                "ACCIO should be disallowed when not in global allow list");

        // reset zone config to default (empty lists) so it doesn't affect other tests
        config.set("zones.global.allowed-spells", List.of());
        config.set("zones.global.disallowed-spells", List.of());
        Ollivanders2API.getSpells().loadZoneConfig();
    }

    /**
     * Test that parseSpell matches a spell name at the start of a message and ignores trailing words.
     */
    private void testParseSpell() {
        O2Spells spells = Ollivanders2API.getSpells();
        String lumos = O2SpellType.LUMOS.getSpellName();
        String accio = O2SpellType.ACCIO.getSpellName();
        // AVADA_KEDAVRA has a two-word name, exercising the multi-word prefix build
        String avadaKedavra = O2SpellType.AVADA_KEDAVRA.getSpellName();

        // whole message is exactly a spell name
        assertEquals(O2SpellType.LUMOS, spells.parseSpell(lumos), "parseSpell should match a bare spell name");
        assertEquals(O2SpellType.AVADA_KEDAVRA, spells.parseSpell(avadaKedavra),
                "parseSpell should match a multi-word spell name");

        // a spell name at the start with trailing words matches, trailing words ignored
        assertEquals(O2SpellType.AVADA_KEDAVRA, spells.parseSpell(avadaKedavra + " now"),
                "parseSpell should match a spell name followed by extra words");

        // when the message leads with one spell name and names another later, the leading one wins
        assertEquals(O2SpellType.LUMOS, spells.parseSpell(lumos + " " + accio),
                "parseSpell should match the leading spell name, not a later one");

        // no spell name anywhere in the message
        assertNull(spells.parseSpell("hello there friend"), "parseSpell should return null when no spell name matches");
    }

    /**
     * Test the wand check in speakIncantation: a non-immediate spell is queued on the player only when they hold a
     * valid wand. Uses the elder wand for a deterministic pass and no wand for a deterministic fail.
     */
    private void testSpeakIncantationWandCheck() {
        World testWorld = mockServer.addSimpleWorld("speakWandCheckWorld");
        Location location = new Location(testWorld, 100, 40, 100);

        // holding the elder wand passes the wand check, so LUMOS is queued as the player's wand spell
        PlayerMock withWand = mockServer.addPlayer();
        withWand.setLocation(location);
        O2Player withWandO2p = testPlugin.getO2Player(withWand);
        withWand.getInventory().setItemInMainHand(O2ItemType.ELDER_WAND.getItem(1));

        Ollivanders2API.getSpells().speakIncantation(withWand, O2SpellType.LUMOS, new String[]{lumosWords()});
        assertEquals(O2SpellType.LUMOS, withWandO2p.getWandSpell(),
                "speakIncantation should queue the spell when the player holds a valid wand");

        // holding no wand fails the wand check, so nothing is queued
        PlayerMock noWand = mockServer.addPlayer();
        noWand.setLocation(location);
        O2Player noWandO2p = testPlugin.getO2Player(noWand);

        Ollivanders2API.getSpells().speakIncantation(noWand, O2SpellType.LUMOS, new String[]{lumosWords()});
        assertNull(noWandO2p.getWandSpell(),
                "speakIncantation should not queue the spell when the player holds no wand");
    }

    /**
     * Test the wand check for a wand that is neither the player's destined wand nor the elder wand: with no
     * experience in the spell the check always fails, so nothing is queued.
     */
    private void testSpeakIncantationUntrackedWand() {
        World testWorld = mockServer.addSimpleWorld("speakUntrackedWandWorld");
        Location location = new Location(testWorld, 100, 40, 100);

        PlayerMock player = mockServer.addPlayer();
        player.setLocation(location);
        O2Player o2p = testPlugin.getO2Player(player);

        // build a valid wand whose wood differs from the player's destined wand, so it is neither destined nor elder
        String destinedWood = o2p.getDestinedWandWood();
        String otherWood = null;
        for (String wood : O2WandWoodType.getAllWandWoodsByName()) {
            if (!wood.equals(destinedWood)) {
                otherWood = wood;
                break;
            }
        }
        assertNotNull(otherWood, "test needs at least two wand woods to build a non-destined wand");

        String core = O2WandCoreType.getAllWandCoreNames().getFirst();
        ItemStack wand = Ollivanders2API.getItems().getWands().makeWand(otherWood, core, 1);
        assertNotNull(wand, "should be able to build a non-destined wand");
        assertFalse(Ollivanders2API.getItems().getWands().isDestinedWand(player, wand),
                "the test wand must not be the player's destined wand");
        player.getInventory().setItemInMainHand(wand);

        // with no experience in the spell, a non-destined, non-elder wand always fails the wand check
        TestCommon.setPlayerSpellExperience(testPlugin, player, O2SpellType.LUMOS, 0);
        Ollivanders2API.getSpells().speakIncantation(player, O2SpellType.LUMOS, new String[]{lumosWords()});
        assertNull(o2p.getWandSpell(),
                "an untracked wand with no spell experience should fail the wand check and queue nothing");

        // a cast at spell mastery passes the experience roll (deterministic under testMode) and queues the spell
        TestCommon.setPlayerSpellExperience(testPlugin, player, O2SpellType.LUMOS, O2Spell.spellMasteryLevel);
        Ollivanders2API.getSpells().speakIncantation(player, O2SpellType.LUMOS, new String[]{lumosWords()});
        assertEquals(O2SpellType.LUMOS, o2p.getWandSpell(),
                "an untracked wand at spell mastery should pass the experience roll and queue the spell");
    }

    /**
     * Test that speakIncantation refuses a spell the player has not learned when bookLearning is enabled.
     */
    private void testSpeakIncantationBookLearning() {
        World testWorld = mockServer.addSimpleWorld("speakBookLearningWorld");
        Location location = new Location(testWorld, 100, 40, 100);

        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(location);
        O2Player o2p = testPlugin.getO2Player(caster);
        caster.getInventory().setItemInMainHand(O2ItemType.ELDER_WAND.getItem(1));
        TestCommon.setPlayerSpellExperience(testPlugin, caster, O2SpellType.LUMOS, 0);
        TestCommon.clearMessageQueue(caster);

        boolean originalBookLearning = Ollivanders2.bookLearning;
        Ollivanders2.bookLearning = true;
        try {
            Ollivanders2API.getSpells().speakIncantation(caster, O2SpellType.LUMOS, new String[]{lumosWords()});
        }
        finally {
            Ollivanders2.bookLearning = originalBookLearning;
        }

        assertNull(o2p.getWandSpell(), "speakIncantation should not queue an unlearned spell when bookLearning is on");
        String message = TestCommon.getWholeMessage(caster);
        assertNotNull(message, "speakIncantation should message the player when refusing an unlearned spell");
        assertTrue(TestCommon.messageStartsWith("You do not know that spell", message),
                "speakIncantation should tell the player they have not learned the spell, got: " + message);
    }

    /**
     * Test the divination branch of speakIncantation: a divination spell is wandless and casts immediately against the
     * named target, and reports failure when the target name is missing or not online.
     */
    private void testSpeakIncantationDivine() {
        World testWorld = mockServer.addSimpleWorld("speakDivineWorld");
        Location location = new Location(testWorld, 100, 40, 100);

        O2SpellType divinationType = Divination.divinationSpells.getFirst();
        assertTrue(Ollivanders2API.getSpells().isLoaded(divinationType), "divination spell should be loaded");

        // caster holds no wand, proving divinations bypass the wand check
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(location);
        testPlugin.getO2Player(caster);
        PlayerMock target = mockServer.addPlayer();

        cleanupActiveSpells();

        // happy path: last word names an online player, so the divination is cast and becomes active
        Ollivanders2API.getSpells().speakIncantation(caster, divinationType, new String[]{"x", target.getName()});
        List<O2Spell> active = Ollivanders2API.getSpells().getActiveSpells();
        assertEquals(1, active.size(), "casting a divination should add one active spell");
        assertEquals(divinationType, active.getFirst().getSpellType(), "the active spell should be the divination");
        cleanupActiveSpells();

        // no target name given
        TestCommon.clearMessageQueue(caster);
        Ollivanders2API.getSpells().speakIncantation(caster, divinationType, new String[]{"x"});
        assertTrue(Ollivanders2API.getSpells().getActiveSpells().isEmpty(),
                "a divination with no target name should not be cast");
        String noNameMessage = TestCommon.getWholeMessage(caster);
        assertNotNull(noNameMessage, "speakIncantation should message the caster when no target name is given");
        assertTrue(TestCommon.messageStartsWith("You must say the name", noNameMessage),
                "speakIncantation should ask for the target's name, got: " + noNameMessage);

        // named target is not online
        TestCommon.clearMessageQueue(caster);
        Ollivanders2API.getSpells().speakIncantation(caster, divinationType, new String[]{"x", "NotAnOnlinePlayer"});
        assertTrue(Ollivanders2API.getSpells().getActiveSpells().isEmpty(),
                "a divination targeting an offline player should not be cast");
        String noTargetMessage = TestCommon.getWholeMessage(caster);
        assertNotNull(noTargetMessage, "speakIncantation should message the caster when the target is not found");
        assertTrue(TestCommon.messageStartsWith("Unable to find player named", noTargetMessage),
                "speakIncantation should report the target could not be found, got: " + noTargetMessage);
    }

    /**
     * The incantation word for LUMOS, used as the single-word chat input for a non-argument spell cast.
     *
     * @return the LUMOS spell name
     */
    private String lumosWords() {
        return O2SpellType.LUMOS.getSpellName();
    }

    /**
     * Test castSpell: the wand flick that fires the spell a player queued (or their master spell when non-verbal
     * casting is on). A queued spell is cast and cleared only when the player holds a wand; otherwise it stays queued.
     */
    private void testCastSpell() {
        World testWorld = mockServer.addSimpleWorld("castSpellWorld");
        Location location = new Location(testWorld, 100, 40, 100);
        O2Spells spells = Ollivanders2API.getSpells();

        // nothing queued and non-verbal casting off: castSpell does nothing
        PlayerMock idle = mockServer.addPlayer();
        idle.setLocation(location);
        O2Player idleO2p = testPlugin.getO2Player(idle);
        idle.getInventory().setItemInMainHand(O2ItemType.ELDER_WAND.getItem(1));
        cleanupActiveSpells();

        spells.castSpell(idle);
        assertTrue(spells.getActiveSpells().isEmpty(), "castSpell should do nothing when no spell is queued");
        assertNull(idleO2p.getWandSpell(), "castSpell should leave the queued spell null when none was queued");

        // spell queued but no wand held: the spell is not cast and stays queued for a later flick
        PlayerMock noWand = mockServer.addPlayer();
        noWand.setLocation(location);
        O2Player noWandO2p = testPlugin.getO2Player(noWand);
        noWandO2p.setWandSpell(O2SpellType.LUMOS);
        cleanupActiveSpells();

        spells.castSpell(noWand);
        assertTrue(spells.getActiveSpells().isEmpty(), "castSpell should not cast a queued spell without a wand");
        assertEquals(O2SpellType.LUMOS, noWandO2p.getWandSpell(),
                "castSpell should leave the spell queued when the player holds no wand");

        // spell queued and wand held: the spell is cast and the queue is cleared
        PlayerMock caster = mockServer.addPlayer();
        caster.setLocation(location);
        O2Player casterO2p = testPlugin.getO2Player(caster);
        caster.getInventory().setItemInMainHand(O2ItemType.ELDER_WAND.getItem(1));
        casterO2p.setWandSpell(O2SpellType.LUMOS);
        cleanupActiveSpells();

        spells.castSpell(caster);
        List<O2Spell> active = spells.getActiveSpells();
        assertEquals(1, active.size(), "castSpell should add one active spell for a queued spell with a wand");
        assertEquals(O2SpellType.LUMOS, active.getFirst().getSpellType(), "castSpell should cast the queued spell");
        assertNull(casterO2p.getWandSpell(), "castSpell should clear the queued spell after casting");
        cleanupActiveSpells();

        // nothing queued but a master spell is set and non-verbal casting is on: the master spell is cast
        PlayerMock nonverbal = mockServer.addPlayer();
        nonverbal.setLocation(location);
        O2Player nonverbalO2p = testPlugin.getO2Player(nonverbal);
        nonverbal.getInventory().setItemInMainHand(O2ItemType.ELDER_WAND.getItem(1));
        nonverbalO2p.setMasterSpell(O2SpellType.ACCIO);
        cleanupActiveSpells();

        boolean originalNonVerbal = Ollivanders2.enableNonVerbalSpellCasting;
        Ollivanders2.enableNonVerbalSpellCasting = true;
        try {
            spells.castSpell(nonverbal);
        }
        finally {
            Ollivanders2.enableNonVerbalSpellCasting = originalNonVerbal;
        }

        List<O2Spell> nonverbalActive = spells.getActiveSpells();
        assertEquals(1, nonverbalActive.size(), "castSpell should cast the master spell when non-verbal casting is on");
        assertEquals(O2SpellType.ACCIO, nonverbalActive.getFirst().getSpellType(),
                "castSpell should cast the player's master spell non-verbally");
        cleanupActiveSpells();
    }

    /**
     * Test rotateNonVerbalSpell: an off-hand wand click cycles the player's master spell, forward on a left click and
     * backward on a right click, and no-ops when non-verbal casting is off or no off-hand wand is held.
     */
    private void testRotateNonVerbalSpell() {
        World testWorld = mockServer.addSimpleWorld("rotateNonVerbalWorld");
        Location location = new Location(testWorld, 100, 40, 100);
        O2Spells spells = Ollivanders2API.getSpells();

        PlayerMock player = mockServer.addPlayer();
        player.setLocation(location);
        O2Player o2p = testPlugin.getO2Player(player);

        // mastering a spell (count >= 100) adds it to the mastered list; the first mastered spell becomes the master
        TestCommon.setPlayerSpellExperience(testPlugin, player, O2SpellType.LUMOS, 100);
        TestCommon.setPlayerSpellExperience(testPlugin, player, O2SpellType.ACCIO, 100);
        assertEquals(O2SpellType.LUMOS, o2p.getMasterSpell(), "the first mastered spell should be the master spell");

        boolean originalNonVerbal = Ollivanders2.enableNonVerbalSpellCasting;
        try {
            // non-verbal casting disabled: no rotation even with an off-hand wand
            Ollivanders2.enableNonVerbalSpellCasting = false;
            player.getInventory().setItemInOffHand(O2ItemType.ELDER_WAND.getItem(1));
            spells.rotateNonVerbalSpell(player, Action.LEFT_CLICK_AIR);
            assertEquals(O2SpellType.LUMOS, o2p.getMasterSpell(),
                    "rotate should do nothing when non-verbal casting is disabled");

            Ollivanders2.enableNonVerbalSpellCasting = true;

            // no wand in the off-hand: no rotation
            player.getInventory().setItemInOffHand(null);
            spells.rotateNonVerbalSpell(player, Action.LEFT_CLICK_AIR);
            assertEquals(O2SpellType.LUMOS, o2p.getMasterSpell(),
                    "rotate should do nothing without an off-hand wand");

            // left click advances to the next mastered spell and messages the player
            player.getInventory().setItemInOffHand(O2ItemType.ELDER_WAND.getItem(1));
            TestCommon.clearMessageQueue(player);
            spells.rotateNonVerbalSpell(player, Action.LEFT_CLICK_AIR);
            assertEquals(O2SpellType.ACCIO, o2p.getMasterSpell(), "left click should advance the mastered spell");
            String message = TestCommon.getWholeMessage(player);
            assertNotNull(message, "rotate should message the player their new master spell");
            assertTrue(TestCommon.messageStartsWith("Wand master spell set to", message),
                    "rotate should report the new master spell, got: " + message);

            // right click rotates back to the previous mastered spell
            spells.rotateNonVerbalSpell(player, Action.RIGHT_CLICK_AIR);
            assertEquals(O2SpellType.LUMOS, o2p.getMasterSpell(),
                    "right click should rotate back to the previous mastered spell");
        }
        finally {
            Ollivanders2.enableNonVerbalSpellCasting = originalNonVerbal;
        }
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