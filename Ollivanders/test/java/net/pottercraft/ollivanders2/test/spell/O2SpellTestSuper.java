package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Abstract base class for spell unit tests.
 *
 * <p>Provides shared test infrastructure for testing O2Spell implementations including:
 * <ul>
 * <li>MockBukkit server setup and teardown</li>
 * <li>Plugin instance initialization with default configuration</li>
 * <li>Helper methods for casting spells in tests</li>
 * <li>Abstract test methods that subclasses must implement</li>
 * </ul>
 *
 * <p><strong>Test Structure:</strong> A shared MockBukkit server and plugin instance are created
 * once before all tests and reused across test methods for performance. Each test class extending
 * this base must implement {@link #spellConstructionTest()} and {@link #doCheckEffectTest()}.
 *
 * @author Azami7
 */
@Isolated
public abstract class O2SpellTestSuper {
    /**
     * Default spell experience level for test casting.
     *
     * <p>Used by {@link #castSpell(PlayerMock, Location, Location)} and overloads
     * when no explicit experience is provided.</p>
     */
    static final int defaultExperience = 20;

    /**
     * Shared mock Bukkit server instance for all tests.
     *
     * <p>Static field initialized once before all tests in this class. Reused across test instances
     * to avoid expensive server setup/teardown for each test method.</p>
     */
    static ServerMock mockServer;

    /**
     * The plugin instance being tested.
     *
     * <p>Loaded before all test methods with the default configuration. Provides access to
     * logger, scheduler, and other plugin API methods during tests.</p>
     */
    static Ollivanders2 testPlugin;

    /**
     * Initialize the mock Bukkit server before all tests.
     *
     * <p>Static setup method called once before all tests in this class. Creates the shared
     * MockBukkit server instance that is reused across all test methods to avoid expensive
     * server creation/destruction overhead.</p>
     */
    @BeforeAll
    static void globalSetUp() {
        Ollivanders2.testMode = true;

        mockServer = MockBukkit.mock();
        testPlugin = MockBukkit.loadWithConfig(Ollivanders2.class, new File("Ollivanders/test/resources/default_config.yml"));

        // advance the server by 20 ticks to let the scheduler start (it has an initial delay of 20 ticks)
        mockServer.getScheduler().performTicks(TestCommon.startupTicks);
    }

    /**
     * Get the spell type being tested.
     *
     * @return the spell type
     */
    @NotNull
    abstract O2SpellType getSpellType();

    /**
     * Cast a spell with default wand and experience.
     *
     * @param caster         the player casting the spell
     * @param fromLocation   the location to cast the spell from
     * @param targetLocation the location the spell should target
     * @return the created and added spell
     */
    O2Spell castSpell(@NotNull PlayerMock caster, @NotNull Location fromLocation, @NotNull Location targetLocation) {
        return castSpell(caster, fromLocation, targetLocation, O2PlayerCommon.rightWand, defaultExperience);
    }

    /**
     * Cast a spell with custom wand and default experience.
     *
     * @param caster         the player casting the spell
     * @param fromLocation   the location to cast the spell from
     * @param targetLocation the location the spell should target
     * @param wand           the wand correctness factor (rightWand, wrongWand, elderWand, etc.)
     * @return the created and added spell
     */
    O2Spell castSpell(@NotNull PlayerMock caster, @NotNull Location fromLocation, @NotNull Location targetLocation, double wand) {
        return castSpell(caster, fromLocation, targetLocation, wand, defaultExperience);
    }

    /**
     * Cast a spell with custom wand and experience.
     *
     * <p>Sets up the player's experience with the spell type, positions them to face the target,
     * creates the spell, and adds it to the active spell list.</p>
     *
     * @param caster         the player casting the spell
     * @param fromLocation   the location to cast the spell from
     * @param targetLocation the location the spell should target
     * @param wand           the wand correctness factor (rightWand, wrongWand, elderWand, etc.)
     * @param experience     the spell experience level to set on the player
     * @return the created and added spell
     */
    O2Spell castSpell(@NotNull PlayerMock caster, @NotNull Location fromLocation, @NotNull Location targetLocation, double wand, int experience) {
        return castSpell(caster, fromLocation, targetLocation, wand, experience, getSpellType());
    }

    /**
     * Cast a spell with custom wand and experience.
     *
     * <p>Sets up the player's experience with the spell type, positions them to face the target,
     * creates the spell, and adds it to the active spell list.</p>
     *
     * @param caster         the player casting the spell
     * @param fromLocation   the location to cast the spell from
     * @param targetLocation the location the spell should target
     * @param wand           the wand correctness factor (rightWand, wrongWand, elderWand, etc.)
     * @param experience     the spell experience level to set on the player
     * @param spellType      the type of spell to cast
     * @return the created and added spell
     */
    O2Spell castSpell(@NotNull PlayerMock caster, @NotNull Location fromLocation, @NotNull Location targetLocation, double wand, int experience, O2SpellType spellType) {
        caster.setLocation(fromLocation);

        if (!fromLocation.equals(targetLocation)) {
            O2Player o2p = Ollivanders2API.getPlayers().getPlayer(caster.getUniqueId());
            assertNotNull(o2p, "Unable to get O2Player");
            o2p.setSpellCount(getSpellType(), experience);

            caster.setLocation(TestCommon.faceTarget(caster.getLocation(), targetLocation));
        }

        O2Spell spell = Ollivanders2API.getSpells().createSpell(caster, spellType, wand);
        assertNotNull(spell, "Unable to create spell");

        Ollivanders2API.getSpells().addSpell(caster, spell);

        return spell;
    }

    /**
     * Test spell-specific set up such as anything in doInitSpell, usesModifier, pass through blocks, move effect data,
     * target material allow and block lists, world guard flags, projectile radius, and any spell-specific settings
     */
    @Test
    abstract void spellConstructionTest();

    /**
     * Test the specific spell functionality
     */
    @Test
    abstract void doCheckEffectTest();

    /**
     * Test revert functionality
     */
    @Test
    abstract void revertTest();

    /**
     * Tear down the mock Bukkit server after all tests complete.
     *
     * <p>Static teardown method called once after all tests in this class have finished.
     * Releases the MockBukkit server resources to prevent memory leaks and allow clean
     * test execution in subsequent test classes.</p>
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
