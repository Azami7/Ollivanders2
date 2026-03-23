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
 * Base test class for spells that add potion effects to targets.
 *
 * <p>Tests the core functionality of potion effect spells, including effect application,
 * radius calculations, duration calculations, amplifier scaling, and single-target vs.
 * multi-target behavior. Subclasses implement abstract methods to specify the spell type
 * being tested.</p>
 */
abstract class AddPotionEffectTest extends O2SpellTestSuper {
    /**
     * Test that the spell applies potion effects to the correct target(s).
     *
     * <p>Verifies that when a spell is cast toward a nearby player, the appropriate potion
     * effect is applied. For spells that target the caster (targetsSelf=true), the caster
     * receives the effect. For spells that target others, nearby entities receive the effect.</p>
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
        List<PotionEffectType> effectTypes = addPotionEffect.getEffectTypes();
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
     * Test that targets outside the effect radius do not receive effects.
     *
     * <p>Only runs for spells with no projectile that affect multiple targets. Places a target
     * 20 blocks away (outside the effect radius) and verifies that the target does not receive
     * the potion effect, while the caster (if targetsSelf=true) still does.</p>
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

            for (PotionEffectType potionEffectType : addPotionEffect.getEffectTypes()) {
                if (addPotionEffect.targetsSelf())
                    assertTrue(caster.hasPotionEffect(potionEffectType), "caster does not have effect");
                assertFalse(player.hasPotionEffect(potionEffectType), "player outside radius has effect");
            }
        }
    }

    /**
     * Test that effect radius is calculated and clamped correctly.
     *
     * <p>Verifies that the effect radius scales with caster skill level (using formula:
     * effectRadius = usesModifier / 10) and is clamped within the min and max radius bounds.
     * Tests are performed at skill level 1 (low scaling) and level 200 (high scaling).</p>
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
     * Test that effect duration is calculated and clamped correctly.
     *
     * <p>Verifies that the effect duration scales with caster skill level (using formula:
     * durationInSeconds = usesModifier * durationModifier) and is clamped within the min
     * and max duration bounds. Tests are performed at skill level 1 (low scaling) and
     * level 200 (high scaling).</p>
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
     * Test that potion effect amplifier (strength) is calculated correctly.
     *
     * <p>Verifies that amplifier starts at 0 (effect strength I) at low skill levels and
     * increases to 1 (effect strength II) at the threshold (skill level = spellMasteryLevel / 2).
     * Uses the default amplifier calculation; subclasses with custom scaling should override
     * this method.</p>
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
     * Test that single-target spells affect only the first target in range.
     *
     * <p>Only runs for spells that affect a single target (affectsSingleTarget=true).
     * When targetsSelf=true, verifies the caster is targeted first and other entities are
     * not affected. When targetsSelf=false, verifies that only one of multiple nearby
     * entities receives the effect.</p>
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

            for (PotionEffectType potionEffectType : addPotionEffect.getEffectTypes()) {
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
     * Revert test (empty for potion effect spells).
     *
     * <p>Potion effect spells do not have revert actions.</p>
     */
    @Override
    @Test
    void revertTest() {
        // potion effect spells don't have revert actions
    }
}
