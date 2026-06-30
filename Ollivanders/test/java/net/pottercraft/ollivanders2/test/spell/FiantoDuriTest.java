package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.spell.FIANTO_DURI;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.stationaryspell.MUFFLIATO;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@link FIANTO_DURI} spell, which lengthens the duration of the caster's own shield spells at their
 * location.
 *
 * @author Azami7
 * @see FIANTO_DURI
 */
public class FiantoDuriTest extends O2SpellTestSuper {
    /** {@inheritDoc} */
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.FIANTO_DURI;
    }

    /**
     * Verifies that the spell increases the duration of the caster's own shield spell at their location.
     *
     * <p>Places a shield cast by the caster, casts the spell (with skill so the increase is non-zero), and confirms the
     * shield's duration ends up higher than its starting value - the skill-based increase far exceeds the few ticks of
     * aging that elapse, so a net increase proves the spell extended it.</p>
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        // a separate target location so castSpell sets the caster's experience (and thus a non-zero increase)
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        MUFFLIATO shield = new MUFFLIATO(testPlugin, caster.getUniqueId(), location, MUFFLIATO.minRadiusConfig, MUFFLIATO.minDurationConfig);
        Ollivanders2API.getStationarySpells().addStationarySpell(shield);
        int durationBefore = shield.getDuration();

        FIANTO_DURI fianto = (FIANTO_DURI) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(5);

        assertTrue(fianto.isKilled(), "spell did not resolve");
        assertTrue(shield.getDuration() > durationBefore, "caster's shield duration was not increased");
    }

    /**
     * Verifies that the spell does not extend a shield cast by a different player and reports failure.
     *
     * <p>A shield owned by another player at the caster's location must only age (not be extended), and the caster must
     * receive the spell's failure message because no eligible shield was found.</p>
     */
    @Test
    void notOwnShieldTest() {
        World testWorld = mockServer.addSimpleWorld("FiantoDuriNotOwner");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();
        PlayerMock otherPlayer = mockServer.addPlayer();

        // shield owned by a different player, so this caster's FIANTO_DURI should not extend it
        MUFFLIATO shield = new MUFFLIATO(testPlugin, otherPlayer.getUniqueId(), location, MUFFLIATO.minRadiusConfig, MUFFLIATO.minDurationConfig);
        Ollivanders2API.getStationarySpells().addStationarySpell(shield);
        int durationBefore = shield.getDuration();

        FIANTO_DURI fianto = (FIANTO_DURI) castSpell(caster, location, targetLocation);
        String failureMessage = fianto.getFailureMessage();
        TestCommon.clearMessageQueue(caster); // isolate the failure message sent during the ticks below
        mockServer.getScheduler().performTicks(5);

        assertTrue(fianto.isKilled(), "spell did not resolve");
        // the other player's shield only aged - it was not extended
        assertTrue(shield.getDuration() < durationBefore, "another player's shield should not be extended");

        String messages = TestCommon.getWholeMessage(caster);
        assertNotNull(messages, "caster received no message");
        assertTrue(TestCommon.cleanChatMessage(messages).contains(failureMessage), "caster did not receive the failure message");
    }

    /** {@inheritDoc} */
    @Override
    @Test
    void revertTest() {
        // FIANTO_DURI has no revert action - the extended duration is permanent
    }
}