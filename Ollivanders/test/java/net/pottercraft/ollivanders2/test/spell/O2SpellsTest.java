package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.FAST_LEARNING;
import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.O2Spells;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
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
 * Tests for the {@link net.pottercraft.ollivanders2.spell.O2Spells} spell management system.
 *
 * <p>Tests the core spell lifecycle and management functionality including spell loading and lookup,
 * active spell management, cast count tracking, spell creation, upkeep processing, and zone-based
 * spell permission checks.</p>
 *
 * <p><strong>IMPORTANT: Single MockServer Architecture</strong></p>
 *
 * <p>This test class uses a single static MockBukkit.mock() server instance that is shared across
 * all tests. All tests are consolidated into a single @Test mega-test method that calls helper
 * methods sequentially to prevent test interference with shared server state.</p>
 *
 * <p>Test Categories:</p>
 * <ul>
 * <li><strong>Spell Loading:</strong> getAllSpellTypes, getSpellTypeByName, isLoaded</li>
 * <li><strong>Spell Creation:</strong> createSpell via reflection</li>
 * <li><strong>Active Spell Management:</strong> addSpell, getActiveSpells, upkeep, onDisable</li>
 * <li><strong>Cast Count Tracking:</strong> Normal increment and FAST_LEARNING double increment</li>
 * <li><strong>Zone Permissions:</strong> Default (no restrictions), global disallow, global allow</li>
 * </ul>
 *
 * @author Azami7
 */
@Isolated
public class O2SpellsTest {
    /**
     * Shared mock Bukkit server instance for all tests.
     */
    static ServerMock mockServer;

    /**
     * Plugin instance loaded once for all tests.
     */
    static Ollivanders2 testPlugin;

    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Comprehensive O2Spells system test.
     *
     * <p>Runs all spell manager tests in sequence within a single @Test method to ensure
     * shared game state remains consistent throughout all validations.</p>
     */
    @Test
    void o2SpellsTest() {
        testGetAllSpellTypes();
        testGetSpellTypeByName();
        testIsLoaded();
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
     * Kill all active spells and run upkeep to remove them from the active list.
     */
    private void cleanupActiveSpells() {
        for (O2Spell spell : Ollivanders2API.getSpells().getActiveSpells()) {
            spell.kill();
        }
        Ollivanders2API.getSpells().upkeep();
    }

    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}