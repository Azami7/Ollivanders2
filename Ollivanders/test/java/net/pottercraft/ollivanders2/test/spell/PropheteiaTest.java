package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.divination.O2Prophecy;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@link net.pottercraft.ollivanders2.spell.PROPHETEIA} spell, which reveals an unfulfilled prophecy
 * about a nearby target player.
 *
 * <p>The spell's success roll is non-deterministic in production but always succeeds under {@code Ollivanders2.testMode},
 * so these tests exercise the reveal and the no-prophecy outcomes deterministically.</p>
 *
 * @author Azami7
 * @see net.pottercraft.ollivanders2.spell.PROPHETEIA
 */
public class PropheteiaTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.PROPHETEIA;
    }

    /**
     * Register a pending prophecy about the target player.
     *
     * @param target  the player the prophecy is about
     * @param prophet the player credited with making the prophecy
     * @param message the prophecy text that should be revealed
     */
    private void addProphecyAbout(@NotNull PlayerMock target, @NotNull PlayerMock prophet, @NotNull String message) {
        // a far-future delay (1000 ticks) keeps the prophecy pending so the spell can read it during the test
        O2Prophecy prophecy = new O2Prophecy(testPlugin, O2EffectType.SUSPENSION, message, target.getUniqueId(), prophet.getUniqueId(), 1000L, 100, 50);
        Ollivanders2API.getProphecies().addProphecy(prophecy);
    }

    /**
     * Verify the spell broadcasts a target's pending prophecy to nearby players (including the caster) and resolves.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(targetLocation);

        String prophecyMessage = "PropheteiaTest prophecy revealed";
        addProphecyAbout(target, caster, prophecyMessage);

        O2Spell spell = castSpell(caster, location, targetLocation);
        TestCommon.clearMessageQueue(caster); // isolate the broadcast that happens during the ticks below
        mockServer.getScheduler().performTicks(20);

        assertTrue(spell.isKilled(), "spell did not resolve on the target");

        String messages = TestCommon.getWholeMessage(caster);
        assertNotNull(messages, "caster received no message");
        assertTrue(TestCommon.cleanChatMessage(messages).contains(prophecyMessage), "prophecy was not revealed to the caster");
    }

    /**
     * Verify that when the target has no pending prophecy the caster is told that nothing was discovered.
     */
    @Test
    void noProphecyTest() {
        World testWorld = mockServer.addSimpleWorld("PropheteiaNoProphecy");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(targetLocation);

        O2Spell spell = castSpell(caster, location, targetLocation);
        TestCommon.clearMessageQueue(caster);
        mockServer.getScheduler().performTicks(20);

        assertTrue(spell.isKilled(), "spell did not resolve on the target");

        String messages = TestCommon.getWholeMessage(caster);
        assertNotNull(messages, "caster received no message");
        assertTrue(TestCommon.cleanChatMessage(messages).contains("do not discover"), "caster was not told that nothing was discovered");
    }

    @Override
    @Test
    void revertTest() {
        // PROPHETEIA has no revert action - it only reveals an existing prophecy
    }
}