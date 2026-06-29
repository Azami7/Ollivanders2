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
 * Tests for the {@link SCUTO_CONTERAM} shield-penetration spell.
 *
 * <p>Verifies that the spell destroys shield stationary spells at or below its magic level (NEWT), leaves stronger
 * shields and non-shield stationary spells intact, resolves on first contact, and that the number of targets scales
 * with caster skill down to a floor of one.</p>
 *
 * @author Azami7
 * @see SCUTO_CONTERAM
 */
public class ScutoConteramTest extends O2SpellTestSuper {
    /** {@inheritDoc} */
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
     * Verifies that the spell destroys a shield at or below its magic level that lies in its path.
     *
     * <p>MUFFLIATO is a NEWT-level shield in the {@code O2StationarySpellType} scale that SCUTO_CONTERAM's level is
     * compared against, so it is eligible to be destroyed. After the projectile reaches the shield the shield must be
     * killed and the spell resolved.</p>
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
     * Verifies that a shield stronger than the spell's magic level is left intact.
     *
     * <p>PROTEGO_HORRIBILIS is an EXPERT-level {@code ShieldSpell}, above SCUTO_CONTERAM's NEWT level, so it must
     * survive even though the spell still resolves (consuming its attempt) on contact.</p>
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
     * Verifies that a non-shield stationary spell is not destroyed.
     *
     * <p>COLLOPORTUS is not a {@code ShieldSpell}, so it must be left intact; the spell still resolves on contact (its
     * attempt is spent, but nothing is destroyed).</p>
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
     * Verifies that a high-skill cast destroys multiple eligible shields stacked at the same location.
     *
     * <p>With enough skill that more than one target is available, two overlapping eligible shields are both
     * destroyed in the single contact tick.</p>
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
     * Verifies that the target count scales with caster skill and is floored at one.
     *
     * <p>The count is read from the spell immediately after creation (before it resolves). A very low experience must
     * floor to a single target, and a high experience must yield more than one.</p>
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

    /** {@inheritDoc} */
    @Override
    @Test
    void revertTest() {
        // SCUTO_CONTERAM has no revert action - destroyed shields are not restored
    }
}