package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Shield charm effect that protects the target from basic projectiles (arrows, snowballs, eggs, splash/lingering
 * potions). Tracks projectiles launched within 120 blocks and removes them, with an impact particle flair, when they
 * cross the shield radius. Extends {@link ShieldSpellEffect} for the underlying spell-blocking behavior.
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Shield_Charm">Shield Charm on Harry Potter Wiki</a>
 * @see ShieldSpellEffect
 * @see net.pottercraft.ollivanders2.spell.PROTEGO for the spell that applies this effect
 */
public class PROTEGO extends ShieldSpellEffect {
    /**
     * The projectiles this spell needs to track because there is no entity move or projectile move event to listen to
     */
    ArrayList<Projectile> projectiles = new ArrayList<>();

    /**
     * The tracking range in blocks, matching the maximum flight distance of an arrow.
     *
     * @see <a href="https://minecraft.fandom.com/wiki/Arrow">Minecraft Wiki - Arrow</a>
     */
    private final int maxDistance = 120;

    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the shield effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to shield with projectile protection
     */
    public PROTEGO(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.PROTEGO;
        checkDurationBounds();

        flairPulse = false;
        flairOnSpellImpact = true;
        impactFlairParticle = Particle.INSTANT_EFFECT;

        if (!isKilled()) // case where target == null from constructor
            Ollivanders2Common.flair(target.getLocation(), radius, 10, Particle.INSTANT_EFFECT);
    }

    /**
     * Remove any tracked projectile that has crossed the shield radius (with an impact flair) and drop dead
     * projectiles from the tracking list, in addition to the base shield behavior.
     */
    @Override
    public void checkEffect() {
        super.checkEffect();

        if (isKilled())
            return;

        // check to see if any of the projectiles we're tracking have crossed the shield boundary
        ArrayList<Projectile> projectileIterator = new ArrayList<>(projectiles);
        for (Projectile projectile : projectileIterator) {
            if (!projectile.isDead()) {
                if (Ollivanders2Common.isInside(target.getLocation(), projectile.getLocation(), radius)) {
                    common.printDebugMessage("projectile in shield area", null, null, false);
                    projectile.remove();
                    if (flairOnSpellImpact)
                        Ollivanders2Common.flair(target.getLocation(), radius, 10, impactFlairParticle);
                }
                else
                    continue;
            }

            // dead projectile
            projectiles.remove(projectile);
        }
    }

    /**
     * Start tracking a launched projectile if it is a supported type (arrow, snowball, egg, splash/lingering potion)
     * within {@link #maxDistance} of the shielded player.
     *
     * @param event the projectile launch event
     */
    @Override
    void doOnProjectileLaunchEvent(@NotNull ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        EntityType type = event.getEntityType();

        // this spell only stops basic projectiles like arrows, snowballs, eggs, and thrown potions
        if (type == EntityType.ARROW || type == EntityType.SNOWBALL || type == EntityType.EGG || type == EntityType.SPLASH_POTION || type == EntityType.LINGERING_POTION) {
            // is this projectile within maxDistance of this spell center?
            if (Ollivanders2Common.isInside(target.getLocation(), projectile.getLocation(), maxDistance)) {
                common.printDebugMessage("adding projectile " + type, null, null, false);
                projectiles.add(projectile);
            }
        }
    }

    /**
     * Cancel a projectile hit against the shielded player, as a backstop for any projectile the active tracking
     * missed.
     *
     * @param event the projectile hit event
     */
    @Override
    void doOnProjectileHitEvent(@NotNull ProjectileHitEvent event) {
        Entity entity = event.getHitEntity();

        if (entity != null && (entity.getUniqueId().equals(targetID)))
            event.setCancelled(true);
    }

    /**
     * Check whether a projectile is currently being tracked by this shield.
     *
     * @param projectile the projectile to check
     * @return true if the projectile is in the tracking list
     */
    public boolean isProjectileTracked(@NotNull Projectile projectile) {
        return projectiles.contains(projectile);
    }
}
