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
 * Abstract base test class for {@link net.pottercraft.ollivanders2.spell.AddO2Effect} spell implementations.
 *
 * <p>Provides shared test infrastructure for spells that apply an {@link net.pottercraft.ollivanders2.effect.O2Effect}
 * or Bukkit potion effect to a target player. Tests verify that:
 * <ul>
 * <li>The spell applies its effect to the correct target after being cast</li>
 * <li>The effect expires after the calculated duration</li>
 * <li>Calculated duration stays within the spell's configured min/max bounds at all experience levels</li>
 * </ul>
 *
 * <p>Subclasses must implement {@link #getSpellType()}, {@link #addsPotionEffect()}, and
 * {@link #getPotionEffects()} to configure which spell and effect type are under test.</p>
 *
 * @author Azami7
 */
abstract public class AddO2EffectTest extends O2SpellTestSuper {
    @Override
    void spellConstructionTest() {
    }

    /**
     * Whether this spell applies a Bukkit potion effect rather than a custom O2Effect.
     *
     * <p>When true, {@link #doCheckEffectTest()} verifies the effect via
     * {@link org.bukkit.entity.Player#hasPotionEffect(PotionEffectType)}. When false, it uses
     * {@link net.pottercraft.ollivanders2.effect.O2Effects#hasEffect(java.util.UUID, O2EffectType)}.</p>
     *
     * @return true if the spell adds a Bukkit potion effect, false if it adds an O2Effect
     */
    abstract boolean addsPotionEffect();

    /**
     * The Bukkit potion effect types applied by this spell, or null if the spell adds an O2Effect instead.
     *
     * <p>Only used when {@link #addsPotionEffect()} returns true. The first element is used for
     * presence checks in {@link #doCheckEffectTest()}.</p>
     *
     * @return list of potion effect types, or null if the spell does not add potion effects
     */
    @Nullable
    abstract List<PotionEffectType> getPotionEffects();

    /**
     * Tests that the spell applies its effect to the correct target and that the effect expires after the duration.
     *
     * <p>Casts the spell at a target 3 blocks away and verifies:
     * <ul>
     * <li>The spell kills itself after hitting a target</li>
     * <li>The expected effect is present on the target immediately after the spell hits</li>
     * <li>The effect expires after the maximum configured duration</li>
     * </ul>
     *
     * <p>For self-targeting spells the caster is the target; for non-self-targeting spells the nearby
     * player is the target. Effect presence is checked via
     * {@link org.bukkit.entity.Player#hasPotionEffect(PotionEffectType)} for potion effects and
     * {@link net.pottercraft.ollivanders2.effect.O2Effects#hasEffect(java.util.UUID, O2EffectType)}
     * for O2Effects.</p>
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
            assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), effects.getFirst()), "target does not have  o2effect");

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
     * Tests that the calculated effect duration stays within the spell's configured min/max bounds.
     *
     * <p>Verifies duration bounds at two experience levels:
     * <ul>
     * <li>Experience level 1 (minimum skill) — duration should be at least {@code minDurationInSeconds}</li>
     * <li>Experience level {@code spellMasteryLevel * 2} (beyond mastery) — duration should be at most
     * {@code maxDurationInSeconds}</li>
     * </ul>
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
