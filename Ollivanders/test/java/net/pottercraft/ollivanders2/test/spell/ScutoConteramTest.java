package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.SCUTO_CONTERAM;
import net.pottercraft.ollivanders2.stationaryspell.COLLOPORTUS;
import net.pottercraft.ollivanders2.stationaryspell.MUFFLIATO;
import net.pottercraft.ollivanders2.stationaryspell.PROTEGO_HORRIBILIS;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link SCUTO_CONTERAM}, the shield-penetration spell.
 *
 * @author Azami7
 */
public class ScutoConteramTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.SCUTO_CONTERAM;
    }

    /**
     * Build a location a few blocks ahead of the caster, in the projectile's path.
     */
    @NotNull
    private Location targetLocation(@NotNull Location casterLocation) {
        return new Location(casterLocation.getWorld(), casterLocation.getX() + 5, casterLocation.getY(), casterLocation.getZ());
    }

    /**
     * Verify the spell destroys a shield at or below its magic level in its path (MUFFLIATO, a NEWT-level shield) and
     * resolves.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location casterLocation = getNextLocation(testWorld);
        Location shieldLocation = targetLocation(casterLocation);
        PlayerMock caster = mockServer.addPlayer();

        MUFFLIATO shield = new MUFFLIATO(testPlugin, caster.getUniqueId(), shieldLocation, MUFFLIATO.minRadiusConfig, MUFFLIATO.minDurationConfig);
        Ollivanders2API.getStationarySpells().addStationarySpell(shield);

        O2Spell scuto = castSpell(caster, casterLocation, shieldLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(shield.isKilled(), "eligible shield was not destroyed");
        assertTrue(scuto.isKilled(), "spell did not resolve after contacting the shield");
    }

    /**
     * Verify a shield above the spell's level (PROTEGO_HORRIBILIS, EXPERT) survives, while the spell still resolves,
     * consuming its attempt.
     */
    @Test
    void higherLevelShieldNotDestroyedTest() {
        World testWorld = mockServer.addSimpleWorld("ScutoConteramHorribilis");
        Location casterLocation = getNextLocation(testWorld);
        Location shieldLocation = targetLocation(casterLocation);
        PlayerMock caster = mockServer.addPlayer();

        PROTEGO_HORRIBILIS shield = new PROTEGO_HORRIBILIS(testPlugin, caster.getUniqueId(), shieldLocation, PROTEGO_HORRIBILIS.minRadiusConfig, PROTEGO_HORRIBILIS.minDurationConfig);
        Ollivanders2API.getStationarySpells().addStationarySpell(shield);

        O2Spell scuto = castSpell(caster, casterLocation, shieldLocation);
        mockServer.getScheduler().performTicks(20);

        assertFalse(shield.isKilled(), "higher-level shield should not be destroyed");
        assertTrue(scuto.isKilled(), "spell did not resolve after contacting the shield");
    }

    /**
     * Verify a non-ShieldSpell stationary spell (COLLOPORTUS) is left intact while the spell still resolves, spending
     * its attempt without destroying anything.
     */
    @Test
    void nonShieldNotDestroyedTest() {
        World testWorld = mockServer.addSimpleWorld("ScutoConteramNonShield");
        Location casterLocation = getNextLocation(testWorld);
        Location lockLocation = targetLocation(casterLocation);
        PlayerMock caster = mockServer.addPlayer();

        COLLOPORTUS lock = new COLLOPORTUS(testPlugin, caster.getUniqueId(), lockLocation);
        Ollivanders2API.getStationarySpells().addStationarySpell(lock);

        O2Spell scuto = castSpell(caster, casterLocation, lockLocation);
        mockServer.getScheduler().performTicks(20);

        assertFalse(lock.isKilled(), "non-shield stationary spell should not be destroyed");
        assertTrue(scuto.isKilled(), "spell did not resolve after contacting the stationary spell");
    }

    /**
     * Verify a high-skill cast (more than one target available) destroys two overlapping eligible shields in a single
     * contact tick.
     */
    @Test
    void multipleShieldsTest() {
        World testWorld = mockServer.addSimpleWorld("ScutoConteramMultiple");
        Location casterLocation = getNextLocation(testWorld);
        Location shieldLocation = targetLocation(casterLocation);
        PlayerMock caster = mockServer.addPlayer();

        MUFFLIATO shield1 = new MUFFLIATO(testPlugin, caster.getUniqueId(), shieldLocation, MUFFLIATO.minRadiusConfig, MUFFLIATO.minDurationConfig);
        MUFFLIATO shield2 = new MUFFLIATO(testPlugin, caster.getUniqueId(), shieldLocation, MUFFLIATO.minRadiusConfig, MUFFLIATO.minDurationConfig);
        Ollivanders2API.getStationarySpells().addStationarySpell(shield1);
        Ollivanders2API.getStationarySpells().addStationarySpell(shield2);

        // high experience yields more than one target (usesModifier / 20)
        O2Spell scuto = castSpell(caster, casterLocation, shieldLocation, O2PlayerCommon.rightWand, 200);
        mockServer.getScheduler().performTicks(20);

        assertTrue(shield1.isKilled(), "first stacked shield was not destroyed");
        assertTrue(shield2.isKilled(), "second stacked shield was not destroyed");
        assertTrue(scuto.isKilled(), "spell did not resolve");
    }

    /**
     * Verify the target count (read immediately after creation) floors to one at very low skill and exceeds one at
     * high skill.
     */
    @Test
    void targetsRemainingTest() {
        World testWorld = mockServer.addSimpleWorld("ScutoConteramTargets");
        Location casterLocation = getNextLocation(testWorld);
        Location target = targetLocation(casterLocation);
        PlayerMock caster = mockServer.addPlayer();

        // low experience floors at a single target
        SCUTO_CONTERAM low = (SCUTO_CONTERAM) castSpell(caster, casterLocation, target, O2PlayerCommon.rightWand, 4);
        assertEquals(1, low.getTargetsRemaining(), "low-skill cast should floor to one target");

        // high experience scales above the floor
        SCUTO_CONTERAM high = (SCUTO_CONTERAM) castSpell(caster, casterLocation, target, O2PlayerCommon.rightWand, 200);
        assertTrue(high.getTargetsRemaining() > 1, "high-skill cast should target more than one shield");
    }

    @Override
    @Test
    void revertTest() {
        // SCUTO_CONTERAM has no revert action - destroyed shields are not restored
    }
}