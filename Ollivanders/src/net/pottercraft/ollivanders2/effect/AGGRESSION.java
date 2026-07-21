package net.pottercraft.ollivanders2.effect;

import java.util.Collection;
import java.util.UUID;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Makes the target periodically damage a nearby entity and provoke nearby creatures to attack them. The chance of
 * acting on each cooldown scales with the aggression level (1-10). Always permanent; use {@link #kill()} to remove.
 * Detectable via Legilimens.
 *
 * @author Azami7
 */
public class AGGRESSION extends O2Effect {
    /**
     * Attack probability threshold, 1-10; each trigger acts if a random value 0-9 is less than this. Mirrored into
     * {@link #duration} for serialization.
     */
    int aggressionLevel = 5;

    /**
     * The minimum aggression level supported.
     */
    static int minAggression = 1;

    /**
     * The maximum aggression level supported.
     */
    static int maxAggression = 10;

    /**
     * Ticks between aggression checks; the counter resets to this after each check.
     */
    static public final int cooldownLimit = 120;

    /**
     * Ticks remaining until the next aggression check; decremented each tick.
     */
    int cooldown;

    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the aggression level (1-10), clamped to valid range
     * @param isPermanent ignored - aggression is always permanent
     * @param pid         the unique ID of the affected player
     */
    public AGGRESSION(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, true, pid);

        effectType = O2EffectType.AGGRESSION;
        checkDurationBounds();

        legilimensText = "feels aggressive";
        affectedPlayerText = "You feel angry.";

        cooldown = cooldownLimit;
    }

    /**
     * Once per cooldown, with probability set by the aggression level, damage a random living entity within 3 blocks
     * and provoke every creature within 6 blocks to target the player.
     */
    @Override
    public void checkEffect() {
        cooldown = cooldown - 1;

        if (cooldown <= 0) {
            int rand = Math.abs(Ollivanders2Common.random.nextInt()) % maxAggression;

            if (rand < aggressionLevel) {
                // damage nearby entity
                Collection<LivingEntity> nearby = EntityCommon.getLivingEntitiesInRadius(target.getLocation(), 3);
                damageRandomEntity(nearby);

                // provoke nearby Creatures to attack
                nearby = EntityCommon.getLivingEntitiesInRadius(target.getLocation(), 6);
                provoke(nearby);
            }

            cooldown = cooldownLimit;
        }
    }

    /**
     * Damage the first non-target entity in the collection for a random 1/2, 1/3, or 1/4 of its current health.
     * No-ops if the collection has no eligible entity, or if WorldGuard denies the hit (PVP flag for players,
     * DAMAGE_ANIMALS flag for non-hostile creatures).
     *
     * @param nearby the nearby living entities to choose from
     */
    private void damageRandomEntity(@NotNull Collection<LivingEntity> nearby) {
        if (!nearby.isEmpty()) {
            LivingEntity toDamage = null;
            for (LivingEntity entity : nearby) {
                // don't let the player hit themselves
                if (!entity.getUniqueId().equals(targetID)) {
                    toDamage = entity;
                    break;
                }
            }

            // no nearby entities that were not the target player
            if (toDamage == null)
                return;

            // don't do damage if worldguard is protecting where the entity is
            if (Ollivanders2.worldGuardEnabled) {
                if (toDamage instanceof Player && !Ollivanders2.worldGuardO2.checkWGFlag(target, toDamage.getEyeLocation(), Flags.PVP))
                    return;
                else if (!(toDamage instanceof Monster) && !Ollivanders2.worldGuardO2.checkWGFlag(target, toDamage.getEyeLocation(), Flags.DAMAGE_ANIMALS))
                    return;
            }

            double curHealth = toDamage.getHealth();
            // damage is entities current health divided by 2, 3, or 4
            int rand = Math.abs(Ollivanders2Common.random.nextInt());
            double damage = curHealth / ((rand % 3) + 2);
            toDamage.damage(damage, target);
        }
    }

    /**
     * Provoke nearby Creatures to target this player.
     *
     * @param nearby collection of nearby living entities
     */
    private void provoke(@NotNull Collection<LivingEntity> nearby) {
        if (!nearby.isEmpty()) {
            for (LivingEntity entity : nearby) {
                if (entity instanceof Creature)
                    ((Creature) entity).setTarget(target);
            }
        }
    }

    /**
     * Set the aggression level, clamped to 1-10. Also mirrors the value into {@link #duration} for serialization.
     *
     * @param level the aggression level, clamped to range 1-10
     */
    public void setAggressionLevel(int level) {
        aggressionLevel = level;

        if (aggressionLevel < minAggression)
            aggressionLevel = minAggression;
        else if (aggressionLevel > maxAggression)
            aggressionLevel = maxAggression;

        duration = aggressionLevel;
    }

    /**
     * No-op: AGGRESSION is always permanent and cannot be toggled. Use {@link #kill()} to remove it.
     *
     * @param perm ignored
     */
    @Override
    public void setPermanent(boolean perm) {
    }

    @Override
    public void doRemove() {
    }
}
