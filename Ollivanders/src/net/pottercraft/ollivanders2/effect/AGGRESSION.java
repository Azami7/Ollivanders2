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
 * This effect causes the player to impulsively harm those nearby and causes them to provoke attacks by nearby mobs.
 *
 * <p>This effect does not age but must be removed explicitly.</p>
 */
public class AGGRESSION extends O2Effect {
    /**
     * The level of aggression this player has.
     *
     * <p>Value from 1-10 for how aggressive this player will be with 1 being the lowest level</p>
     */
    int aggressionLevel = 5;

    /**
     * The target of this effect
     */
    Player target;

    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public AGGRESSION(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.AGGRESSION;
        legilimensText = "feels aggressive";
        affectedPlayerText = "You feel angry.";

        permanent = true;
        target = p.getServer().getPlayer(targetID);

        aggressionLevel = duration;
    }

    /**
     * Attack random entity every 15 seconds and provoke nearby Creatures to attack.
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
     * Damage a random nearby entity.
     *
     * @param nearby a collection of nearby entities
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
     * Set the aggression level for this player
     *
     * @param level 1-10 where 1 is the lowest
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
     * Override set permanent so that it cannot be called and alter duration, which we override to also set
     * the aggression level since this is always permanent.
     *
     * @param perm true if this is permanent, false otherwise
     */
    @Override
    public void setPermanent(boolean perm) {
    }

    /**
     * Do any cleanup related to removing this effect from the player
     */
    @Override
    public void doRemove() {
    }
}
