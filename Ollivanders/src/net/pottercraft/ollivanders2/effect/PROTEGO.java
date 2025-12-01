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
 * Spell shield charm that actively blocks projectile attacks through tracking and removal.
 *
 * <p>PROTEGO is an active spell shield charm that protects the affected player from projectile
 * attacks by actively tracking incoming projectiles and removing them when they cross the shield
 * boundary. Unlike passive shields, PROTEGO continuously monitors the positions of tracked
 * projectiles (arrows, snowballs, eggs, splash/lingering potions) within a 120-block range of the
 * player. When a projectile enters the shield radius, it is immediately removed and an impact
 * particle effect (INSTANT_EFFECT) is displayed. The spell inherits the base shield mechanism from
 * SpellShieldEffect (spell blocking, level checking, entity targeting prevention).</p>
 *
 * <p>Shield Configuration:</p>
 * <ul>
 * <li>Active projectile tracking: continuously monitors projectiles in range</li>
 * <li>Maximum tracking distance: 120 blocks (arrow flight distance)</li>
 * <li>Supported projectiles: arrows, snowballs, eggs, splash/lingering potions</li>
 * <li>Shield removal on projectile entry: projectile is instantly removed</li>
 * <li>Pulse effect: disabled (flairPulse = false)</li>
 * <li>Impact flair: enabled (flairOnSpellImpact = true) with INSTANT_EFFECT particles</li>
 * </ul>
 *
 * <p>The Shield Charm - Protego - was a charm that protected the caster with an invisible shield
 * that deflected spells.</p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Shield_Charm">Shield Charm on Harry Potter Wiki</a>
 * @see ShieldSpellEffect for the base spell blocking mechanism and protection behavior
 * @see net.pottercraft.ollivanders2.spell.PROTEGO for the spell that applies this effect
 */
public class PROTEGO extends ShieldSpellEffect {
    /**
     * The projectiles this spell needs to track because there is no entity move or projectile move event to listen to
     */
    ArrayList<Projectile> projectiles = new ArrayList<>();

    /**
     * The maximum distance for an arrow, which is the furthest flying projectile.
     *
     * @see <a href="https://minecraft.fandom.com/wiki/Arrow">https://minecraft.fandom.com/wiki/Arrow</a>
     */
    private final int maxDistance = 120;

    /**
     * Constructor for creating an active projectile-tracking spell shield.
     *
     * <p>Creates a spell shield that actively tracks and blocks projectile attacks. The shield initializes
     * with projectile tracking enabled, impact particle effects configured for INSTANT_EFFECT particles,
     * and displays an initial visual flair at the shield location. Tracked projectiles within 120 blocks
     * are continuously monitored and removed when they cross the shield boundary.</p>
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
     * Track projectile positions and remove those crossing the shield boundary.
     *
     * <p>Called each game tick. This method ages the base shield effect and then iterates through all
     * tracked projectiles to check if any have crossed the shield radius boundary. When a projectile
     * enters the protected area, it is immediately removed and an impact flair particle effect is
     * displayed (if flairOnSpellImpact is enabled). Dead projectiles are cleaned from the tracking list.
     * Projectiles can also be blocked through the base SpellShieldEffect mechanism.</p>
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
     * Start tracking projectiles launched near the shielded player.
     *
     * <p>Monitors all projectile launch events to identify basic projectiles (arrows, snowballs, eggs,
     * splash/lingering potions) within 120 blocks of the shielded player. Only supported projectile types
     * within range are added to the tracking list for continuous boundary checking. This allows the
     * shield to proactively intercept projectiles before they reach the player.</p>
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
     * Cancel projectile hits to the shielded player as a backup protection mechanism.
     *
     * <p>Acts as a safety mechanism to cancel any projectile hit events against the shielded player
     * that may have escaped the active projectile tracking system. This ensures complete protection
     * even if a projectile manages to cross the shield boundary without being detected by the
     * continuous tracking checks.</p>
     *
     * @param event the projectile hit event to cancel
     */
    @Override
    void doOnProjectileHitEvent(@NotNull ProjectileHitEvent event) {
        Entity entity = event.getHitEntity();

        if (entity != null && (entity.getUniqueId().equals(targetID)))
            event.setCancelled(true);
    }
}
