package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.ANIMAGUS_EFFECT;
import net.pottercraft.ollivanders2.effect.AWAKE;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.house.O2HouseType;
import net.pottercraft.ollivanders2.house.O2Houses;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.spell.LEGILIMENS;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for LEGILIMENS spell functionality.
 *
 * <p>Tests the mind-reading spell including:</p>
 * <ul>
 * <li>Failure when caster has insufficient skill to detect targets</li>
 * <li>Information revealed progressively by caster's Legilimens level (muggle status, wand, spells, effects, animagus form)</li>
 * <li>Detection of animagus forms by level 10+ casters only</li>
 * <li>Spell failure when target is in animagus form and caster skill is below mastery</li>
 * <li>Spell failure when no targets are in range</li>
 * <li>Zone-based spell blocking via isAllowed check</li>
 * </ul>
 *
 * <p>All tests are combined into a single method because global state like maxSpellLevel and blocked spell
 * zones interfere when tests run in parallel. The @Isolated annotation should be used to prevent parallel
 * execution of this test class.</p>
 *
 * @author Azami7
 */
public class LegilimensTest extends O2SpellTestSuper {
    @Override @Test
    void spellConstructionTest() {
        // legilimens has no spell-specific settings
    }

    /**
     * Test LEGILIMENS spell behavior across all scenarios.
     *
     * <p>Validates spell functionality including failure detection, progressive information revelation,
     * animagus detection, and spell blocking. Tests are ordered to progressively enable features and
     * modify player state (house sorting, wand acquisition, effects, animagus transformation, skill level changes)
     * to verify spell behavior at each stage. Tests confirm that readMind() revealscorrect information and that
     * checkEffect() properly handles animagus forms and skill thresholds.</p>
     */
    @Override @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld("world");
        Location location = new Location(testWorld, 100, 40, 100);
        PlayerMock caster = mockServer.addPlayer("Caster");
        PlayerMock player = mockServer.addPlayer("Player1");

        caster.setLocation(location);
        player.setLocation(TestCommon.getRelativeLocation(caster.getLocation(), BlockFace.EAST));

        O2Player casterO2P = Ollivanders2API.getPlayers().getPlayer(caster.getUniqueId());
        assertNotNull(casterO2P);
        O2Player playerO2P = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        assertNotNull(playerO2P);

        // when spell fail to read the player's mind, a failure message is sent
        LEGILIMENS legilimens = (LEGILIMENS) castSpell(caster, location, O2SpellType.LEGILIMENS);
        mockServer.getScheduler().performTicks(5);
        assertTrue(legilimens.isKilled(), "Legilimens not immediately killed");
        String message = getWholeMessage(caster);
        assertNotNull(message, "caster did not receive a failure message when mind read failed on target");
        assertTrue(message.contains(LEGILIMENS.mindReadFailureMessage), "caster did not receive expected failure message when mind read failed on target");

        // when the player is a muggle, we should get the muggle message
        Ollivanders2.maxSpellLevel = true;
        caster.setLocation(location); // handle caster drift
        castSpell(caster, location, O2SpellType.LEGILIMENS);
        mockServer.getScheduler().performTicks(5);
        message = getWholeMessage(caster);
        assertNotNull(message);
        assertTrue(message.contains(" is a muggle."), "not expected message when target is a muggle");

        // when the player is not a muggle, we should get a wizard message
        playerO2P.setMuggle(false);
        O2Houses.useHouses = true;
        caster.setLocation(location); // handle caster drift
        castSpell(caster, location, O2SpellType.LEGILIMENS);
        mockServer.getScheduler().performTicks(5);
        message = getWholeMessage(caster);
        assertNotNull(message);
        assertTrue(message.contains(" is a witch/wizard."), "not expected message when target is a wizard");

        // when the player has not been sorted yet
        assertTrue(message.contains(" has not started school yet."), "not expected message when target has not been sorted");

        // when the player has not found their destined wand
        assertTrue(message.contains(" has not gotten a wand."), "not expected message when target has not found wand yet");

        // when player has been sorted
        Ollivanders2API.getHouses().sort(player, O2HouseType.HUFFLEPUFF);
        playerO2P.setFoundWand(true);
        Ollivanders2.useYears = true;
        caster.setLocation(location); // handle caster drift
        castSpell(caster, location, O2SpellType.LEGILIMENS);
        mockServer.getScheduler().performTicks(5);
        message = getWholeMessage(caster);
        assertNotNull(message);

        assertTrue(message.contains(O2HouseType.HUFFLEPUFF.getName()), "not expected message when target has been sorted");

        // when years enabled and player has been sorted
        assertTrue(message.contains(playerO2P.getYear().getDisplayText() + " year"), "not expected message when years enabled");

        // when player has found destined wand
        assertTrue(message.contains(" uses a " + playerO2P.getDestinedWandWood() + " and " + playerO2P.getDestinedWandCore() + " wand."));

        Ollivanders2.useYears = false;
        playerO2P.setSpellRecentCastTime(O2SpellType.ACCIO);
        playerO2P.setMasterSpell(O2SpellType.LUMOS);
        Ollivanders2.enableNonVerbalSpellCasting = true;
        caster.setLocation(location); // handle caster drift
        castSpell(caster, location, O2SpellType.LEGILIMENS);
        mockServer.getScheduler().performTicks(5);
        message = getWholeMessage(caster);
        assertNotNull(message);

        // the last spell a player cast
        assertTrue(message.contains(" last cast "), "not expected message when player has cast a spell");

        // when target has a mastered spell
        assertTrue(message.contains(" can non-verbally cast the spell "), "not expected message when player has mastered spell");

        // when the player has an effect
        AWAKE awake = new AWAKE(testPlugin, 200, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(awake);
        mockServer.getScheduler().performTicks(20);
        caster.setLocation(location); // handle caster drift
        castSpell(caster, location, O2SpellType.LEGILIMENS);
        mockServer.getScheduler().performTicks(5);
        message = getWholeMessage(caster);
        assertNotNull(message);

        assertNotNull(awake.getLegilimensText());
        assertTrue(message.contains(awake.getLegilimensText()), "not expected message when target has an effect");
        awake.kill();
        mockServer.getScheduler().performTicks(5);

        // when player is an animagus
        playerO2P.setIsAnimagus();
        playerO2P.setAnimagusForm(EntityType.RABBIT);
        caster.setLocation(location); // handle caster drift
        castSpell(caster, location, O2SpellType.LEGILIMENS);
        mockServer.getScheduler().performTicks(5);
        message = getWholeMessage(caster);
        assertNotNull(message);
        assertTrue(message.contains(" has the animagus form of a "), "not expected message when target is an animagus");

        // when player is in animagus form and spell level not expert
        Ollivanders2API.getPlayers().playerEffects.addEffect(new ANIMAGUS_EFFECT(testPlugin, 100, true, player.getUniqueId()));
        mockServer.getScheduler().performTicks(5);
        Ollivanders2.maxSpellLevel = false;
        casterO2P.setSpellCount(O2SpellType.LEGILIMENS, O2Spell.spellMasteryLevel - 10);
        caster.setLocation(location); // handle caster drift
        castSpell(caster, location, O2SpellType.LEGILIMENS);
        mockServer.getScheduler().performTicks(5);
        assertNull(caster.nextMessage(), "caster unexpectedly got a failure message when target in animagus form and skill level below mastery");

        // when player is in animagus form when spell level is expert
        Ollivanders2.maxSpellLevel = true;

        caster.setLocation(location); // handle caster drift
        castSpell(caster, location, O2SpellType.LEGILIMENS);
        mockServer.getScheduler().performTicks(5);
        message = getWholeMessage(caster);
        assertNotNull(message, "caster did not get a result message when target in animagus form and skill level above mastery"); // they'll either get a failure message or the legilmens data
        Ollivanders2API.getPlayers().playerEffects.removeEffect(player.getUniqueId(), O2EffectType.ANIMAGUS_EFFECT);

        // spell cannot target a player if none are in range
        player.setLocation(new Location(location.getWorld(), location.getX() + LEGILIMENS.radius + 100, location.getY(), location.getZ()));
        caster.setLocation(location); // handle caster drift
        castSpell(caster, location, O2SpellType.LEGILIMENS);
        mockServer.getScheduler().performTicks(5);
        assertNull(caster.nextMessage(), "caster unexpectedly got a failure message when no players in range");

        // make sure the overridden checkEffect is isAllowed check is working
        testPlugin.getConfig().set("zones.test-world.type", "World");
        testPlugin.getConfig().set("zones.test-world.world", testWorld.getName());
        testPlugin.getConfig().set("zones.test-world.disallowed-spells", List.of("LEGILIMENS"));
        Ollivanders2API.getSpells().loadZoneConfig();

        caster.setLocation(location); // handle caster drift
        castSpell(caster, location, O2SpellType.LEGILIMENS);
        mockServer.getScheduler().performTicks(5);
        message = caster.nextMessage();
        assertNotNull(message, "caster did not get isAllowed failure message");
        assertEquals(O2Spell.isAllowedFailureMessage, TestCommon.cleanChatMessage(message), "Player did not get expected failure message");
    }

    /**
     * Collects all pending messages from a player's message queue into a single string.
     *
     * <p>Repeatedly calls nextMessage() to drain the queue, concatenating all messages with space
     * separators. Used to gather multi-line readMind() output into a single string for assertion checking.</p>
     *
     * @param player the player to collect messages from
     * @return concatenated message string with spaces between lines, or null if no messages are pending
     */
    @Nullable
    String getWholeMessage(PlayerMock player) {
        String message = player.nextMessage();
        if (message == null)
            return null;

        StringBuilder wholeMessage = new StringBuilder();
        while (message != null) {
            wholeMessage.append(message).append(" ");
            message = player.nextMessage();
        }

        return wholeMessage.toString();
    }
}
