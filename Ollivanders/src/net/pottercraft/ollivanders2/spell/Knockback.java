package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
    protected double minDistance = 1;

    /**
     * The maximum distance we can move things
     */
    protected double maxDistance = 5;

    /**
     * The maximum number of targets
     */
    protected int maxTargets = 1;

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
    boolean vertical = false;

    /**
     * Does this spell target the caster?
     */
    boolean targetsSelf = false;

    /**
     * Can this spell target multiple entities? This will be ignored for spells that target self
     */
    boolean targetsMultiple = false;

    /**
     * The maximum radius this spell will search for targets in.
     */
    double maxRadius = defaultRadius;

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
        if (hasHitTarget() || targetsSelf) // projectile is stopped so we'll check this location then exit the spell
            kill();

        //
        // get target entity
        //
        List<Entity> targets = new ArrayList<>();

        if (targetsSelf) {
            targets.add(player);
        }
        else { // look for a valid target at the location
            int numberOfTargets = calculateNumberOfTargets();
            double radius = calculateRadius();

            List<Entity> entities = getNearbyEntities(radius);
            for (Entity entity : entities) {
                if (!entityTargetCheck(entity)) // we cannot target this entity
                    continue;

                targets.add(entity);

                if (targets.size() == numberOfTargets)
                    break;
            }
        }

        for (Entity target : targets) {
            // determine distance to send the target
            double distance = calculateDistance(target);
            if (distance <= 0) // spell will have no effect so exit
                return;

            if (isVertical())
                velocity = Ollivanders2Common.calculateVerticalVelocity(distance, calculateDragFactor(target), !isPull());
            else
                velocity = Ollivanders2Common.calculateVelocityForDistance(location, distance, calculateDragFactor(target), isPull());

            target.setVelocity(velocity);
        }
    }

    /**
     * Determine if this entity can be targeted
     *
     * @param entity the entity to check
     * @return true if it can be targeted, false otherwise
     */
    boolean entityTargetCheck(Entity entity) {
        if (entity.getUniqueId().equals(player.getUniqueId()) && !targetsSelf)
            return false;
        else if (entity.getUniqueId().equals(player.getUniqueId()) && targetsSelf)
            return true;

        // check canTarget
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
     * Calculate the number of targets based on the caster's skill
     *
     * @return the number of targets, bounded by 1 <= targets <= maxTargets
     */
    int calculateNumberOfTargets() {
        if (!targetsMultiple)
            return 1;

        // to calculate number of targets, divide usesModifier by mastery to get % of mastery
        // and then multiply by maxTargets. Example: 5 maxTarget, usesModifier of 20 is (20 / 100) * 5 = 1
        int numTargets = (int)((usesModifier / O2Spell.spellMasteryLevel) * maxTargets);

        if (numTargets < 1)
            numTargets = 1;
        else if (numTargets > maxTargets) // in the case of usesModifier > mastery
            numTargets = maxTargets;

        return numTargets;
    }

    /**
     * Calculate the effect radius for this spell based on the caster's skill
     *
     * @return the radius, bounded by defaultRadius <= radius <= maxRadius
     */
    double calculateRadius() {
        // to calculate the radius, divide usesModifier by mastery to get % of mastery
        // and then multiply by maxRadius. Example: 5 maxTarget, usesModifier of 40 is (40 / 100) * 5 = 2
        double radius = (usesModifier / O2Spell.spellMasteryLevel) * maxRadius;

        if (radius < defaultRadius)
            radius = defaultRadius;
        else if (radius > maxRadius)
            radius = maxRadius;

        return radius;
    }

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
        if (vertical && !pull && !Ollivanders2.testMode && target.isUnderWater()) { // isUnderWater not currently implemented in MockBukkit
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
     * @return the velocity magnitude for the target
     */
    double calculateDragFactor(Entity target) {
        double drag;

        if (!Ollivanders2.testMode && target.isUnderWater()) { // isUnderWater not currently implemented in MockBukkit
            drag = Ollivanders2Common.underWaterDragFactor;
        }
        else {
            if (vertical)
                drag = Ollivanders2Common.airVerticalDragFactor;
            else
                drag = Ollivanders2Common.airHorizontalDragFactor;
        }

        return drag;
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
        return vertical;
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
     * Check if this spell can target multiple entities.
     *
     * @return true if it can target multiple, false otherwise
     */
    public boolean isTargetsMultiple() {
        return targetsMultiple;
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
    public double getMinDistance() {
        return minDistance;
    }

    /**
     * Get the maximum distance for knockback.
     *
     * @return the maximum distance
     */
    public double getMaxDistance() {
        return maxDistance;
    }

    /**
     * Get the maximum radius
     *
     * @return the max radius
     */
    public double getMaxRadius() {
        return maxRadius;
    }

    /**
     * Get the maximum number of targets
     *
     * @return the max targets
     */
    public int getMaxTargets () {
        return maxTargets;
    }
}
