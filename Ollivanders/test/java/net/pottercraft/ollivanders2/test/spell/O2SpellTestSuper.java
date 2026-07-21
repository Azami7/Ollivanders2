package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Base class for {@link O2Spell} unit tests. Sets up a shared MockBukkit server and plugin once for all tests and
 * provides helpers to cast spells. Subclasses implement {@link #getSpellType()}, {@link #doCheckEffectTest()}, and
 * {@link #revertTest()}.
 *
 * @author Azami7
 */
public abstract class O2SpellTestSuper {
    /**
     * Default spell experience used when casting in tests without an explicit value.
     */
    static final int defaultExperience = 20;

    /**
     * Shared MockBukkit server, created once for all tests in the class.
     */
    static ServerMock mockServer;

    /**
     * The plugin under test, loaded once with the default config.
     */
    static Ollivanders2 testPlugin;

    /**
     * Next X coordinate handed out by {@link #getNextLocation(World)}.
     */
    int nextX = 0;

    /**
     * Start the shared MockBukkit server and load the plugin before any tests run.
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
     * Get a fresh test location, advancing the X coordinate so successive calls do not overlap.
     *
     * @param world the world to create the location in
     * @return the new location
     */
    synchronized Location getNextLocation(World world) {
        Location location = new Location(world, nextX, 40, 100);
        nextX = nextX + 100;

        return location;
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
     * @param caster         the player casting the spell
     * @param fromLocation   the location to cast the spell from
     * @param targetLocation the location the spell should target
     * @param wand           the wand correctness factor (rightWand, wrongWand, elderWand, etc.)
     * @param experience     the spell experience level to set on the player
     * @return the created and added spell
     */
    O2Spell castSpell(@NotNull PlayerMock caster, @NotNull Location fromLocation, @NotNull Location targetLocation, double wand, double experience) {
        return castSpell(caster, fromLocation, targetLocation, wand, experience, getSpellType());
    }

    /**
     * Set the caster's experience with the spell, face them at the target, then create the spell and register it as
     * active. When {@code fromLocation} equals {@code targetLocation} the experience and facing steps are skipped.
     *
     * @param caster         the player casting the spell
     * @param fromLocation   the location to cast the spell from
     * @param targetLocation the location the spell should target
     * @param wand           the wand correctness factor (rightWand, wrongWand, elderWand, etc.)
     * @param experience     the spell experience level to set on the player
     * @param spellType      the type of spell to cast
     * @return the created and added spell
     */
    O2Spell castSpell(@NotNull PlayerMock caster, @NotNull Location fromLocation, @NotNull Location targetLocation, double wand, double experience, O2SpellType spellType) {
        caster.setLocation(fromLocation);

        if (!fromLocation.equals(targetLocation)) {
            TestCommon.setPlayerSpellExperience(testPlugin, caster, spellType, (int)Math.floor(experience));

            caster.setLocation(TestCommon.faceTarget(caster.getLocation(), targetLocation));
        }

        O2Spell spell = Ollivanders2API.getSpells().createSpell(caster, spellType, wand);
        assertNotNull(spell, "Unable to create spell");

        Ollivanders2API.getSpells().addSpell(caster, spell);

        return spell;
    }

    /**
     * Verify spell-specific construction: doInitSpell values, usesModifier, pass-through blocks, move effect data,
     * material allow/block lists, WorldGuard flags, projectile radius, and any other per-spell settings. Empty here so
     * subclasses that have nothing extra to check need not override it.
     */
    @Test
    void spellConstructionTest() {}

    /**
     * Verify the spell's effect behavior.
     */
    @Test
    abstract void doCheckEffectTest();

    /**
     * Verify the spell's revert behavior.
     */
    @Test
    abstract void revertTest();

    /**
     * Stop the MockBukkit server after all tests in the class complete.
     */
    @AfterAll
    static void globalTearDown() {
        MockBukkit.unmock();
    }
}
