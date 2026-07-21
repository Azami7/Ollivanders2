package net.pottercraft.ollivanders2.test.spell;

import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.spell.BRACKIUM_EMENDO;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link BRACKIUM_EMENDO}, the Bone-Healing Spell.
 * <p>
 * Because the player heal uses {@link PotionEffectType#INSTANT_HEALTH} — an instant effect that fires once and is
 * never stored in the active-effects list — it is observed via an {@link EntityPotionEffectEvent} listener rather
 * than {@code hasPotionEffect}.
 * </p>
 *
 * @author Azami7
 */
public class BrackiumEmendoTest extends O2SpellTestSuper {
    @Override
    @NotNull
    O2SpellType getSpellType() {
        return O2SpellType.BRACKIUM_EMENDO;
    }

    /**
     * Spawn a skeleton at a location and return it.
     *
     * @param location the location to spawn at
     * @return the spawned skeleton
     */
    @NotNull
    private Skeleton spawnSkeleton(@NotNull Location location) {
        return (Skeleton) location.getWorld().spawnEntity(location, EntityType.SKELETON);
    }

    /**
     * Verify a default-skill cast damages a skeleton in its path by an amount strictly between the min and max (so
     * the scaling path, not a limit, is exercised) and ends the spell, and that with no entity the spell ends on a
     * block hit.
     */
    @Override
    @Test
    void doCheckEffectTest() {
        World testWorld = mockServer.addSimpleWorld(getSpellType().getSpellName());
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        // skeleton in range: default skill should damage it by a non-limited amount
        Skeleton skeleton = spawnSkeleton(targetLocation);
        double healthBefore = skeleton.getHealth();

        BRACKIUM_EMENDO brackiumEmendo = (BRACKIUM_EMENDO) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(brackiumEmendo.isKilled(), "spell did not end after damaging the skeleton");
        double damageDealt = healthBefore - skeleton.getHealth();
        assertTrue(damageDealt > brackiumEmendo.getMinDamage() && damageDealt < brackiumEmendo.getMaxDamage(),
                "default-skill damage was not strictly between the min and max limits");

        // no entity in range: projectile must end when it hits a block
        Location blockTargetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ() + 20);
        caster.setLocation(location);
        blockTargetLocation.getBlock().setType(Material.STONE);

        BRACKIUM_EMENDO blockCast = (BRACKIUM_EMENDO) castSpell(caster, location, blockTargetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(blockCast.isKilled(), "spell did not end when it hit a block with no target");
    }

    /**
     * Verify skeleton damage floors to the minimum at very low skill and limits to the maximum at very high skill. A
     * fresh skeleton is used per cast so accumulated damage does not skew the second assertion.
     */
    @Test
    void damageLimitTest() {
        World testWorld = mockServer.addSimpleWorld("BrackiumEmendoDamageLimit");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        // low skill floors damage to the minimum
        Skeleton lowSkillSkeleton = spawnSkeleton(targetLocation);
        double lowHealthBefore = lowSkillSkeleton.getHealth();
        BRACKIUM_EMENDO lowSkillCast = (BRACKIUM_EMENDO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 1);
        mockServer.getScheduler().performTicks(20);
        assertEquals(lowSkillCast.getMinDamage(), lowHealthBefore - lowSkillSkeleton.getHealth(), "low-skill damage was not floored to the minimum");
        lowSkillSkeleton.remove();

        // high skill limits damage to the maximum
        caster.setLocation(location);
        Skeleton highSkillSkeleton = spawnSkeleton(targetLocation);
        double highHealthBefore = highSkillSkeleton.getHealth();
        BRACKIUM_EMENDO highSkillCast = (BRACKIUM_EMENDO) castSpell(caster, location, targetLocation, O2PlayerCommon.rightWand, 1000);
        mockServer.getScheduler().performTicks(20);
        assertEquals(highSkillCast.getMaxDamage(), highHealthBefore - highSkillSkeleton.getHealth(), "high-skill damage was not limited to the maximum");
    }

    /**
     * Verify the spell heals a player target (the instant-health effect observed via the listener; see the class doc)
     * and ends after applying it.
     */
    @Test
    void playerHealTest() {
        World testWorld = mockServer.addSimpleWorld("BrackiumEmendoHeal");
        Location location = getNextLocation(testWorld);
        Location targetLocation = new Location(testWorld, location.getX() + 5, location.getY(), location.getZ());
        PlayerMock caster = mockServer.addPlayer();

        PlayerMock target = mockServer.addPlayer();
        target.setLocation(targetLocation);

        AtomicBoolean healApplied = new AtomicBoolean(false);
        mockServer.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onEffect(EntityPotionEffectEvent event) {
                if (event.getEntity().equals(target)
                        && event.getNewEffect() != null
                        && event.getNewEffect().getType().equals(PotionEffectType.INSTANT_HEALTH)) {
                    healApplied.set(true);
                }
            }
        }, testPlugin);

        BRACKIUM_EMENDO brackiumEmendo = (BRACKIUM_EMENDO) castSpell(caster, location, targetLocation);
        mockServer.getScheduler().performTicks(20);

        assertTrue(brackiumEmendo.isKilled(), "spell did not end after healing the player");
        assertTrue(healApplied.get(), "instant health was not applied to the player target");

        target.disconnect();
    }

    /**
     * BRACKIUM_EMENDO has no revert action - healing and damage are not undone.
     */
    @Override
    @Test
    void revertTest() {
        // nothing to revert
    }
}