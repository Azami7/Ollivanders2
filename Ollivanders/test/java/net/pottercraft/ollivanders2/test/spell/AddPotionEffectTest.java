package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.AddPotionEffect;
import net.pottercraft.ollivanders2.spell.O2Spell;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base test class for {@link AddPotionEffect} spells, covering effect application, radius, duration, amplifier
 * scaling, and single- vs multi-target behavior.
 */
abstract class AddPotionEffectTest extends O2SpellTestSuper {
    /**
     * Verify the spell applies its potion effect to the right target: the caster when self-targeting, otherwise a
     * nearby player.
     */
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        PlayerMock player = mockServer.addPlayer();
        player.setLocation(targetLocation);

        AddPotionEffect addPotionEffect = (AddPotionEffect) castSpell(caster, location, targetLocation);
        List<PotionEffectType> effectTypes = addPotionEffect.getPotionEffectTypes();
        assertFalse(effectTypes.isEmpty(), "effect types list empty");

        mockServer.getScheduler().performTicks(20);
        assertTrue(addPotionEffect.isKilled(), "spell did not hit a target");
        for (PotionEffectType potionEffectType : effectTypes) {
            if (addPotionEffect.targetsSelf())
                assertTrue(caster.hasPotionEffect(potionEffectType), "caster does not have effect");
            else
                assertTrue(player.hasPotionEffect(potionEffectType), "player does not have effect");
        }
    }

    /**
     * Verify a target outside the effect radius is not affected (for no-projectile, multi-target spells).
     */
    @Test
    void targetNotInRadiusTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 20, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        AddPotionEffect addPotionEffect = (AddPotionEffect) castSpell(caster, location, targetLocation);

        if (!addPotionEffect.affectsSingleTarget() && addPotionEffect.isNoProjectile()) {
            PlayerMock player = mockServer.addPlayer();
            player.setLocation(targetLocation);
            mockServer.getScheduler().performTicks(2);
            assertTrue(addPotionEffect.isKilled());

            for (PotionEffectType potionEffectType : addPotionEffect.getPotionEffectTypes()) {
                if (addPotionEffect.targetsSelf())
                    assertTrue(caster.hasPotionEffect(potionEffectType), "caster does not have effect");
                assertFalse(player.hasPotionEffect(potionEffectType), "player outside radius has effect");
            }
        }
    }

    /**
     * Verify the effect radius stays within its min and max bounds at both low and high skill.
     */
    @Test
    void radiusTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        double level = 1;
        AddPotionEffect addPotionEffect = (AddPotionEffect) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, level);
        mockServer.getScheduler().performTicks(1);
        assertTrue(addPotionEffect.getEffectRadius() >= addPotionEffect.getMinEffectRadius(), "effectRadius < minEffectRadius");
        assertTrue(addPotionEffect.getEffectRadius() <= addPotionEffect.getMaxEffectRadius(), "effectRadius > maxEffectRadius");

        level = 200;
        addPotionEffect = (AddPotionEffect) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, level);
        mockServer.getScheduler().performTicks(1);
        assertTrue(addPotionEffect.getEffectRadius() >= addPotionEffect.getMinEffectRadius(), "effectRadius < minEffectRadius");
        assertTrue(addPotionEffect.getEffectRadius() <= addPotionEffect.getMaxEffectRadius(), "effectRadius > maxEffectRadius");
    }

    /**
     * Verify the effect duration stays within its min and max bounds at both low and high skill.
     */
    @Test
    void durationTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        PlayerMock player = mockServer.addPlayer();
        player.setLocation(targetLocation);

        double level = 1;
        AddPotionEffect addPotionEffect = (AddPotionEffect) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, level);
        mockServer.getScheduler().performTicks(5);
        assertTrue(addPotionEffect.getDurationInSeconds() >= addPotionEffect.getMinDurationInSeconds(), "durationInSeconds < minDurationInSeconds");
        assertTrue(addPotionEffect.getDurationInSeconds() <= addPotionEffect.getMaxDurationInSeconds(), "durationInSeconds > maxDurationInSeconds");

        level = 200;
        addPotionEffect = (AddPotionEffect) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, level);
        mockServer.getScheduler().performTicks(5);
        assertTrue(addPotionEffect.getDurationInSeconds() >= addPotionEffect.getMinDurationInSeconds(), "durationInSeconds < minDurationInSeconds");
        assertTrue(addPotionEffect.getDurationInSeconds() <= addPotionEffect.getMaxDurationInSeconds(), "durationInSeconds > maxDurationInSeconds");
    }

    /**
     * Verify the amplifier is 0 at low skill and rises to 1 past half spell mastery. Subclasses with custom amplifier
     * scaling should override this.
     */
    @Test
    void amplifierTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        PlayerMock player = mockServer.addPlayer();
        player.setLocation(targetLocation);

        double level = 1;
        AddPotionEffect addPotionEffect = (AddPotionEffect) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, level);
        mockServer.getScheduler().performTicks(5);
        assertEquals(0, addPotionEffect.getAmplifier(), "unexpected amplifier for skill level 1");

        level = (double) (O2Spell.spellMasteryLevel / 2) + 1;
        addPotionEffect = (AddPotionEffect) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, level);
        mockServer.getScheduler().performTicks(5);
        assertEquals(1, addPotionEffect.getAmplifier(), "unexpected amplifier for skill level O2Spell.spellMasteryLevel / 2) + 1");
    }

    /**
     * Verify a single-target spell affects only one target: the caster when self-targeting, otherwise exactly one of
     * several nearby players.
     */
    @Test
    void singleTargetTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        AddPotionEffect addPotionEffect = (AddPotionEffect) castSpell(caster, location, targetLocation);

        if (addPotionEffect.affectsSingleTarget()) {
            PlayerMock player = mockServer.addPlayer();
            player.setLocation(targetLocation);
            PlayerMock player2 = mockServer.addPlayer();
            player2.setLocation(targetLocation);

            mockServer.getScheduler().performTicks(5);

            for (PotionEffectType potionEffectType : addPotionEffect.getPotionEffectTypes()) {
                if (addPotionEffect.targetsSelf()) {
                    assertTrue(caster.hasPotionEffect(potionEffectType), "caster does not have effect");
                    assertFalse(player.hasPotionEffect(potionEffectType), "player has effect when caster already targeted");
                    assertFalse(player2.hasPotionEffect(potionEffectType), "player2 has effect when caster already targeted");
                }
                else {
                    assertTrue(player.hasPotionEffect(potionEffectType) ^ player2.hasPotionEffect(potionEffectType), "both players were affected (or neither were)");
                }
            }
        }
    }

    /**
     * No-op: potion effect spells have no revert action.
     */
    @Override
    @Test
    void revertTest() {
        // potion effect spells don't have revert actions
    }
}
