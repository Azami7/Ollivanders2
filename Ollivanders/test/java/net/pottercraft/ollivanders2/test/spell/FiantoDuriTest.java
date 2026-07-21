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
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.FIANTO_DURI;
    }

    /**
     * Verify the spell raises the caster's own shield's duration above its starting value (the skill-based increase
     * far exceeds the few ticks of aging that elapse, so a net increase proves it was extended).
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
     * Verify a shield owned by another player is only aged (not extended) and the caster gets the failure message
     * since no eligible shield was found.
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

    @Override
    @Test
    void revertTest() {
        // FIANTO_DURI has no revert action - the extended duration is permanent
    }
}