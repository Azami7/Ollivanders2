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
 * Effect that causes a player to impulsively harm nearby entities and provoke mob aggression.
 *
 * <p>The AGGRESSION effect causes a player to periodically (every 10 seconds / 200 ticks) attack nearby entities
 * and provoke creatures to target them. The effect has an aggressionLevel (1-10) that acts as a probability threshold:
 * on each trigger, a random value (0-9) is compared against aggressionLevel. If random &lt; aggressionLevel, the effect
 * activates.
 * </p>
 *
 * <p>Attack Mechanism:
 * When activated, the effect:
 * </p>
 * <ul>
 * <li>Selects a random living entity within 3 blocks</li>
 * <li>Applies damage equal to: currentHealth / randomDivisor where randomDivisor is 2, 3, or 4</li>
 * <li>Respects WorldGuard PVP and damage animal flags</li>
 * <li>Provokes all creatures within 6 blocks to target the aggressed player</li>
 * </ul>
 *
 * <p>This effect is permanent and cannot be modified via setPermanent(). Use kill() to remove it.</p>
 *
 * @author Azami7
 */
public class AGGRESSION extends O2Effect {
    /**
     * The aggression level of this player, representing attack probability.
     *
     * <p>Range: 1-10, where higher values increase the probability of attacks. On each trigger (every 10 seconds),
     * a random value (0-9) is generated. If random < aggressionLevel, the effect activates. For example:
     * <ul>
     * <li>aggressionLevel = 1: ~10% chance to attack per trigger</li>
     * <li>aggressionLevel = 5: ~50% chance to attack per trigger</li>
     * <li>aggressionLevel = 10: 100% chance to attack per trigger</li>
     * </ul>
     * This value is also stored in the duration field for serialization purposes.</p>
     */
    int aggressionLevel = 5;

    /**
     * The player affected by this aggression effect.
     *
     * <p>Reference to the player who is forced to attack nearby entities and provoke creatures.
     * Cached at construction time for quick access during effect processing.</p>
     */
    Player target;

    /**
     * Constructor for creating an aggression effect.
     *
     * <p>Creates a permanent aggression effect that causes the player to periodically attack nearby entities
     * and provoke mobs. The aggressionLevel is set from the duration parameter and controls the probability
     * of attacks (1-10 range).</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the aggression level (1-10), which will be clamped to valid range
     * @param isPermanent ignored - aggression is always permanent
     * @param pid         the unique ID of the affected player
     */
    public AGGRESSION(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, true, pid);

        effectType = O2EffectType.AGGRESSION;
        legilimensText = "feels aggressive";
        affectedPlayerText = "You feel angry.";

        permanent = true;
        target = p.getServer().getPlayer(targetID);

        aggressionLevel = duration;
    }

    /**
     * Check the effect and periodically attack nearby entities and provoke creatures.
     *
     * <p>This method executes once per tick and performs the following:</p>
     * <ol>
     * <li>Ages the effect by 1 tick (note: effect is permanent, so this doesn't cause expiration)</li>
     * <li>Every 200 ticks (10 seconds), performs an action check:
     *     <ul>
     *     <li>Generates random value 0-9</li>
     *     <li>If random &lt; aggressionLevel: damages a random nearby entity (within 3 blocks) and provokes
     *         all creatures within 6 blocks to target the player</li>
     *     </ul>
     * </li>
     * </ol>
     */
    @Override
    public void checkEffect() {
        age(1);

        // only take action once per 10 seconds, which is every 120 ticks
        if ((duration % 120) == 0) {
            int rand = Math.abs(Ollivanders2Common.random.nextInt()) % 10;

            if (rand < aggressionLevel) {
                // damage nearby entity
                Collection<LivingEntity> nearby = EntityCommon.getLivingEntitiesInRadius(target.getLocation(), 3);
                damageRandomEntity(nearby);

                // provoke nearby Creatures to attack
                nearby = EntityCommon.getLivingEntitiesInRadius(target.getLocation(), 6);
                provoke(nearby);
            }
        }
    }

    /**
     * Damage a random nearby entity with probability-based damage.
     *
     * <p>Selects a random entity from the nearby collection and applies damage equal to:
     * currentHealth / randomDivisor, where randomDivisor is randomly chosen as 2, 3, or 4.
     * This ensures damage is 25-50% of the target's current health.
     * </p>
     *
     * <p>Respects WorldGuard protection flags:
     * <ul>
     * <li>For players: requires PVP flag to be enabled</li>
     * <li>For non-hostile creatures: requires DAMAGE_ANIMALS flag to be enabled</li>
     * </ul>
     * </p>
     *
     * @param nearby a collection of nearby living entities to choose from
     */
    private void damageRandomEntity(@NotNull Collection<LivingEntity> nearby) {
        Player target = p.getServer().getPlayer(targetID);
        if (target == null)
            return;

        if (!nearby.isEmpty()) {
            int rand = Math.abs(Ollivanders2Common.random.nextInt());
            LivingEntity[] nArray = nearby.toArray(new LivingEntity[nearby.size()]);

            LivingEntity toDamage = nArray[rand % nearby.size()];

            // don't let the player hit themselves
            if (toDamage.getUniqueId() != targetID)
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
            rand = Math.abs(Ollivanders2Common.random.nextInt());
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
        Player target = p.getServer().getPlayer(targetID);

        if (!nearby.isEmpty()) {
            for (LivingEntity entity : nearby) {
                if (entity instanceof Creature)
                    ((Creature) entity).setTarget(target);
            }
        }
    }

    /**
     * Set the aggression level and update the effect's probability.
     *
     * <p>Sets the aggressionLevel (1-10) which controls the probability of attacks on each trigger.
     * The duration field is automatically updated to match, maintaining synchronization for serialization.</p>
     *
     * @param level the aggression level, will be clamped to range 1-10
     */
    void setAggressionLevel(int level) {
        aggressionLevel = level;

        if (aggressionLevel < 1)
            aggressionLevel = 1;
        else if (aggressionLevel > 10)
            aggressionLevel = 10;

        duration = aggressionLevel;
    }

    /**
     * Override to prevent external modification of permanent status.
     *
     * <p>This effect is always permanent and cannot be modified via this method. The override is necessary
     * because AGGRESSION must always remain permanent during the player's session. Use kill() to remove the effect
     * instead.</p>
     *
     * @param perm ignored - effect is always permanent
     */
    @Override
    public void setPermanent(boolean perm) {
    }

    /**
     * Perform cleanup when this aggression effect is removed.
     *
     * <p>The default implementation does nothing, as aggression effects do not require special cleanup.
     * The effect is removed from the player's effect list when kill() is called.</p>
     */
    @Override
    public void doRemove() {
    }
}
