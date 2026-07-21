package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.AddO2Effect;
import net.pottercraft.ollivanders2.spell.O2Spell;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import javax.annotation.Nullable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base test class for {@link AddO2Effect} spells, covering effect application to the correct target, expiry after the
 * duration, and duration bounds. Subclasses declare whether the spell adds a Bukkit potion effect or an O2Effect.
 *
 * @author Azami7
 */
abstract public class AddO2EffectTest extends O2SpellTestSuper {
    /**
     * @return true if the spell adds a Bukkit potion effect, false if it adds an O2Effect
     */
    abstract boolean addsPotionEffect();

    /**
     * @return the potion effect types this spell applies, or null if it adds an O2Effect instead
     */
    @Nullable
    abstract List<PotionEffectType> getPotionEffects();

    /**
     * Verify the spell applies its effect to the target (the caster when self-targeting, otherwise a nearby player)
     * and that the effect clears once its duration expires unless the spell is permanent.
     */
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        PlayerMock player = mockServer.addPlayer();
        player.setLocation(targetLocation);

        AddO2Effect addO2Effect = (AddO2Effect) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel);
        List<O2EffectType> effects = addO2Effect.getEffectsToAdd();
        List<PotionEffectType> potionEffects = getPotionEffects();
        assertNotNull(potionEffects);

        if (addsPotionEffect()) {
            assertNotNull(potionEffects);
            assertFalse(potionEffects.isEmpty());
        }

        assertFalse(effects.isEmpty(), "Spell has no effects to add");

        Player target;
        if (addO2Effect.targetsSelf())
            target = caster;
        else
            target = player;

        if (addsPotionEffect())
            assertFalse(target.hasPotionEffect(potionEffects.getFirst()), "target already has potion effect");
        else
            assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), effects.getFirst()), "target already has o2effect");

        mockServer.getScheduler().performTicks(20);
        assertTrue(addO2Effect.isKilled(), "spell did not hit target");

        if (addsPotionEffect())
            assertTrue(target.hasPotionEffect(potionEffects.getFirst()), "target does not have potion effect");
        else
            assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), effects.getFirst()), "target does not have o2effect");

        mockServer.getScheduler().performTicks((long) addO2Effect.getMaxDurationInSeconds() * Ollivanders2Common.ticksPerSecond);

        if (addO2Effect.isPermanent()) {
            assertFalse(addsPotionEffect(), "spell set to permanent but it adds a potion effect"); // potion effects cannot be permanent
            assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), effects.getFirst()), "target no longer has o2effect when spell is permanent");
        }
        else {
            if (addsPotionEffect())
                assertFalse(target.hasPotionEffect(potionEffects.getFirst()), "target already still has potion effect after duration expired");
            else
                assertFalse(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), effects.getFirst()), "target still has o2effect after duration expired");
        }
    }

    /**
     * Verify the computed effect duration stays within the spell's min and max bounds at both low and above-mastery
     * skill.
     */
    @Test
    void durationTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 3, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        AddO2Effect addO2Effect = (AddO2Effect) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 1);
        addO2Effect.calculateEffectDurationInSeconds();
        assertTrue(addO2Effect.getDurationInSeconds() >= addO2Effect.getMinDurationInSeconds(), "duration < min duration at skill level 1");
        assertTrue(addO2Effect.getDurationInSeconds() <= addO2Effect.getMaxDurationInSeconds(), "duration > max duration at skill level 1");

        addO2Effect = (AddO2Effect) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, O2Spell.spellMasteryLevel * 2);
        addO2Effect.calculateEffectDurationInSeconds();
        assertTrue(addO2Effect.getDurationInSeconds() >= addO2Effect.getMinDurationInSeconds(), "duration < min duration at skill level mastery * 2");
        assertTrue(addO2Effect.getDurationInSeconds() <= addO2Effect.getMaxDurationInSeconds(), "duration > max duration at skill level mastery * 2");
    }

    @Override
    @Test
    void revertTest() {
        // o2effect spells don't have revert actions
    }
}
