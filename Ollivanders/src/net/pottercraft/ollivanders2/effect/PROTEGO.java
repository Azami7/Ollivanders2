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
 * The Shield Charm - Protego - was a charm that protected the caster with an invisible shield that deflected spells.
 * <p>
 * {@link net.pottercraft.ollivanders2.spell.PROTEGO}
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Shield_Charm">https://harrypotter.fandom.com/wiki/Shield_Charm</a>
 */
public class PROTEGO extends SpellShieldEffect {
    /**
     * The projectiles this spell needs to track because there is no entity move or projectile move event to listen to
     */
    ArrayList<Projectile> projectiles = new ArrayList<>();

    /**
     * The maximum distance for an arrow, which is the furthest flying projectile.
     *
     * @see <a href = "https://minecraft.fandom.com/wiki/Arrow">https://minecraft.fandom.com/wiki/Arrow</a>
     */
    private final int maxDistance = 120;

    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public PROTEGO(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.PROTEGO;
        flairPulse = false;
        flairOnSpellImpact = true;
        impactFlairParticle = Particle.INSTANT_EFFECT;

        Ollivanders2Common.flair(player.getLocation(), radius, 10, Particle.INSTANT_EFFECT);
    }

    /**
     * Check the locations projectiles we are tracking
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
                if (Ollivanders2Common.isInside(player.getLocation(), projectile.getLocation(), radius)) {
                    common.printDebugMessage("projectile in shield area", null, null, false);
                    projectile.remove();
                    if (flairOnSpellImpact)
                        Ollivanders2Common.flair(player.getLocation(), radius, 10, impactFlairParticle);
                }
                else
                    continue;
            }

            // dead projectile
            projectiles.remove(projectile);
        }
    }

    /**
     * Start tracking any projectiles that are launched near the player.
     *
     * @param event the event
     */
    @Override
    void doOnProjectileLaunchEvent(@NotNull ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        EntityType type = event.getEntityType();

        // this spell only stops basic projectiles like arrows, snowballs, eggs, and thrown potions
        if (type == EntityType.ARROW || type == EntityType.SNOWBALL || type == EntityType.EGG || type == EntityType.SPLASH_POTION || type == EntityType.LINGERING_POTION) {
            // is this projectile within maxDistance of this spell center?
            if (Ollivanders2Common.isInside(player.getLocation(), projectile.getLocation(), maxDistance)) {
                common.printDebugMessage("adding projectile " + type, null, null, false);
                projectiles.add(projectile);
            }
        }
    }

    /**
     * In case we miss killing projectiles that cross the shield, cancel any projectile hit events to the player
     *
     * @param event the event
     */
    @Override
    void doOnProjectileHitEvent(@NotNull ProjectileHitEvent event) {
        Entity entity = event.getHitEntity();

        if (entity != null && (entity.getUniqueId().equals(player.getUniqueId())))
            event.setCancelled(true);
    }
}
