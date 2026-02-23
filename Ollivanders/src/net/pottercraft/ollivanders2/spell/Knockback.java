package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Abstract base class for spells with knockback effects (push/pull, vertical/horizontal).
 *
 * <p>Provides common functionality for calculating and applying velocity to entities. Subclasses must implement
 * {@link #canTarget(Entity)} to determine valid targets. The spell finds the nearest valid entity at the projectile
 * location, calculates the appropriate velocity based on distance and drag factors, and applies it to the target.</p>
 *
 * @author Azami7
 */
public abstract class Knockback extends O2Spell {
    /**
     * This reduces how strong the knockback is. 1 is max strength, any number higher than one will reduce the strength.
     */
    protected int strengthReducer = 20;

    /**
     * The minimum distance we can move things
     */
    protected int minDistance = 1;

    /**
     * The maximum distance we can move things
     */
    protected int maxDistance = 5;

    /**
     * The velocity this knockback will add to the target
     */
    protected Vector velocity = new Vector(0, 0, 0);

    /**
     * Does this spell pull or push?
     */
    boolean pull = false;

    /**
     * Is the knockback vertical or horizontal
     */
    boolean isVertical = false;

    /**
     * Does this spell target the caster?
     */
    boolean targetsSelf = false;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public Knockback(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public Knockback(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
    }

    /**
     * Finds an entity at the projectile location and applies velocity to it.
     *
     * <p>Searches for a valid target entity near the projectile, calculates the appropriate velocity based on
     * the caster's skill level, and applies the velocity to the target. For vertical spells targeting entities
     * underwater, the distance is capped to prevent launching them out of water at unrealistic speeds.</p>
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitTarget()) // projectile is stopped so we'll check this location then exit the spell
            kill();

        //
        // get target entity
        //
        Entity target = null;

        if (targetsSelf) {
            target = player;
        }
        else { // look for a valid target at the location
            List<Entity> entities = getCloseEntities(defaultRadius);
            for (Entity entity : entities) {
                if (!entityTargetCheck(entity)) // we cannot target this entity
                    continue;

                target = entity;
                break;
            }
        }

        if (target == null)
            return;
        else
            kill(); // stop the spell from continuing to search for targets

        //
        // calculate velocity
        //

        // determine distance to send the target
        double distance = calculateDistance(target);
        if (distance <= 0) // spell will have no effect so exit
            return;

        // determine the magnitude of the velocity
        double magnitude = calculateVelocityMagnitude(target, distance);
        if (magnitude == 0) // spell will have no effect so exit
            return;

        if (pull) // make velocity negative to move towards rather than away from the caster location
            magnitude = magnitude * -1;

        if (isVertical) // velocity is in the Y direction (up or down)
            velocity = new Vector(0.0, magnitude, 0.0);
        else // velocity is towards or away from the direction the caster is facing
            velocity = player.getLocation().getDirection().normalize().multiply(magnitude);

        target.setVelocity(velocity);
    }

    /**
     * Determine if this entity can be targeted
     *
     * @param entity the entity to check
     * @return true if it can be targeted, false otherwise
     */
    boolean entityTargetCheck(Entity entity) {
        // check entity allow list
        if (!canTarget(entity))
            return false;

        // this may harm the entity so check that this is allowed
        return entityHarmWGCheck(entity);
    }

    /**
     * Can this spell target this entity?
     *
     * @param entity the entity to check
     * @return true if it can target the entity, false otherwise
     */
    abstract boolean canTarget(Entity entity);

    /**
     * Calculate the distance to move the target based on the caster's skill.
     *
     * @param target the target to move
     * @return the distance to move the target
     */
    double calculateDistance(Entity target) {
        double distance = usesModifier / strengthReducer;
        if (distance <= minDistance)
            distance = minDistance;
        else if (distance > maxDistance)
            distance = maxDistance;

        // if this is a vertical push spell and the target is underwater, we don't want to send them flying out of the water at underwater drag velocity
        if (isVertical && !pull && !Ollivanders2.testMode && target.isUnderWater()) { // isUnderWater not currently implemented in MockBukkit
            int distanceToSurface = EntityCommon.distanceToSurface(target);
            if (distance > (distanceToSurface + 1))
                distance = distanceToSurface + 1;
        }

        common.printDebugMessage("Knockback.calculateDistance: distance = " + distance, null, null, false);
        return distance;
    }

    /**
     * Calculate the velocity magnitude to apply to the target.
     *
     * <p>Uses drag factors based on whether the target is underwater or airborne (vertical/horizontal) to
     * determine the velocity needed to move the target the specified distance.</p>
     *
     * @param target   the target to affect
     * @param distance the distance to move the target
     * @return the velocity magnitude for the target
     */
    double calculateVelocityMagnitude(Entity target, double distance) {
        double drag;

        if (!Ollivanders2.testMode && target.isUnderWater()) { // isUnderWater not currently implemented in MockBukkit
            drag = Ollivanders2Common.underWaterDragFactor;
        }
        else {
            if (isVertical)
                drag = Ollivanders2Common.airVerticalDragFactor;
            else
                drag = Ollivanders2Common.airHorizontalDragFactor;
        }

        double magnitude = Ollivanders2Common.velocityForDistance(distance, drag);

        common.printDebugMessage("Knockback.calculateVelocityMagnitude: magnitude = " + magnitude, null, null, false);
        return magnitude;
    }

    /**
     * Get the velocity for this spell.
     *
     * @return the velocity this spell will add to the target entity
     */
    public Vector getVelocity() {
        return velocity.clone();
    }

    /**
     * Check if this spell pulls toward the caster.
     *
     * @return true if the spell pulls, false if it pushes
     */
    public boolean isPull() {
        return pull;
    }

    /**
     * Check if this spell applies vertical knockback.
     *
     * @return true if the knockback is vertical (Y-axis), false if horizontal
     */
    public boolean isVertical() {
        return isVertical;
    }

    /**
     * Check if this spell targets the caster.
     *
     * @return true if the spell affects the caster, false if it targets other entities
     */
    public boolean isTargetsSelf() {
        return targetsSelf;
    }

    /**
     * Get the strength reducer for this spell.
     *
     * @return the strength reducer value (higher values reduce knockback strength)
     */
    public int getStrengthReducer() {
        return strengthReducer;
    }

    /**
     * Get the minimum distance for knockback.
     *
     * @return the minimum distance
     */
    public int getMinDistance() {
        return minDistance;
    }

    /**
     * Get the maximum distance for knockback.
     *
     * @return the maximum distance
     */
    public int getMaxDistance() {
        return maxDistance;
    }
}
