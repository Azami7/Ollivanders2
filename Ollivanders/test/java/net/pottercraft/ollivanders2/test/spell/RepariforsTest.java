package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.IMMOBILIZE;
import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.effect.SUSPENSION;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.REPARIFORS;
import net.pottercraft.ollivanders2.test.testcommon.TestCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link REPARIFORS}, the healing spell.
 *
 * <p>Tests cover the spell's three-stage priority healing cascade:
 * <ul>
 * <li><strong>No target:</strong> Spell hits a block with no nearby players and is killed</li>
 * <li><strong>Minor heal:</strong> Player without ailments receives {@link PotionEffectType#INSTANT_HEALTH}.
 *     Because {@code INSTANT_HEALTH} is an instant effect (it fires once and is never stored in
 *     the active effects list), the test observes it via an {@link EntityPotionEffectEvent} listener
 *     rather than {@code hasPotionEffect}.</li>
 * <li><strong>Immobilize aging:</strong> Player with {@link O2EffectType#IMMOBILIZE} has the effect's
 *     duration reduced by an experience-scaled percentage</li>
 * <li><strong>Suspension blocks immobilize healing:</strong> Player with both
 *     {@link O2EffectType#IMMOBILIZE} and {@link O2EffectType#SUSPENSION} is not treated for
 *     immobilize (to prevent falling while suspended)</li>
 * <li><strong>Poison halving:</strong> Player with {@link PotionEffectType#POISON} has the poison
 *     duration halved, preserving the original amplifier</li>
 * </ul>
 *
 * @author Azami7
 */
public class RepariforsTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.REPARIFORS;
    }

    /**
     * Tests the full progression of Reparifors healing scenarios.
     *
     * <p>Sub-tests run in order against the same world and target player. Effect state is
     * cleaned up between sub-tests via explicit effect removal. The target is placed 10 blocks
     * from the caster (rather than the usual 3) to give more ticks of projectile travel,
     * making the natural-aging arithmetic in the duration assertions more predictable.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 10, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        TestCommon.createBlockBase(new Location(targetLocation.getWorld(), targetLocation.getX(), targetLocation.getY() - 1, targetLocation.getZ()), 5);
        REPARIFORS reparifors = (REPARIFORS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(reparifors.isKilled(), "spell was not killed when it hit a block");

        // player without poison or immobilize is healed
        PlayerMock target = mockServer.addPlayer();
        target.setLocation(targetLocation);
        // we cannot check the player health level because MockBukkit does not apply the effects of Effects
        // we also cannot use Player.hasPotionEffect because INSTANT_HEALTH is instant, it doesn't stay on the player to check
        AtomicBoolean effectApplied = new AtomicBoolean(false);
        mockServer.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onEffect(EntityPotionEffectEvent event) {
                if (event.getEntity().equals(target)
                        && event.getNewEffect() != null
                        && event.getNewEffect().getType().equals(PotionEffectType.INSTANT_HEALTH)) {
                    effectApplied.set(true);
                }
            }
        }, testPlugin);
        reparifors = (REPARIFORS) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);
        assertTrue(reparifors.isKilled(), "spell was not killed when it hit an entity");
        assertTrue(effectApplied.get());

        // ages immobilize
        Ollivanders2API.getPlayers().playerEffects.addEffect(new IMMOBILIZE(testPlugin, 1000, false, target.getUniqueId()));
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), O2EffectType.IMMOBILIZE));
        castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 100);
        mockServer.getScheduler().performTicks(11);
        O2Effect immobilize = Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.IMMOBILIZE);
        assertNotNull(immobilize);
        // experience 100 will result in a 55% reduction on the 1000 tick duration, also subtract 12 ticks that will have passed since the effect was added
        assertEquals(443, immobilize.duration, "Immobilize duration not reduced by expected amount");
        Ollivanders2API.getPlayers().playerEffects.removeEffect(target.getUniqueId(), O2EffectType.IMMOBILIZE);

        // does not change immobilize when player is also suspended
        Ollivanders2API.getPlayers().playerEffects.addEffect(new IMMOBILIZE(testPlugin, 1000, false, target.getUniqueId()));
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), O2EffectType.IMMOBILIZE));
        Ollivanders2API.getPlayers().playerEffects.addEffect(new SUSPENSION(testPlugin, 1000, false, target.getUniqueId()));
        assertTrue(Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), O2EffectType.SUSPENSION));
        castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 100);
        mockServer.getScheduler().performTicks(11);
        immobilize = Ollivanders2API.getPlayers().playerEffects.getEffect(target.getUniqueId(), O2EffectType.IMMOBILIZE);
        assertNotNull(immobilize);
        assertEquals(989, immobilize.duration, "immobilize duration reduced when player also has suspension");
        Ollivanders2API.getPlayers().playerEffects.removeEffect(target.getUniqueId(), O2EffectType.IMMOBILIZE);
        Ollivanders2API.getPlayers().playerEffects.removeEffect(target.getUniqueId(), O2EffectType.SUSPENSION);

        // reduce poison duration
        int duration = 1000;
        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 1000, 1));
        castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(11);
        PotionEffect potionEffect = target.getPotionEffect(PotionEffectType.POISON);
        assertNotNull(potionEffect);
        // reduce the duration by half
        assertEquals(493, potionEffect.getDuration(), "poison duration not reduced to expected amount");
    }

    /**
     * No-op revert test — REPARIFORS has no revert actions to undo.
     */
    @Override
    @Test
    void revertTest() {
        // no revert actions
    }
}
