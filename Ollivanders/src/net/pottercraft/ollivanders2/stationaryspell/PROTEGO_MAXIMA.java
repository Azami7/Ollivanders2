package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.MagicLevel;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import org.bukkit.Location;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Protego Maxima: a stationary shield that layers several protections over its area:
 * <ul>
 *   <li>Kills tracked projectiles that enter the area</li>
 *   <li>Stops outside entities from targeting players or passive mobs inside</li>
 *   <li>Blocks OWL-level-or-lower spell projectiles from crossing in, while letting spells cast from inside exit</li>
 *   <li>Prevents projectiles from hitting players or passive mobs inside, but still lets them hit hostile mobs</li>
 * </ul>
 * <p>
 * In canon this charm also repels hostile entities such as Dementors, but Spigot has no entity-move event to hook, so
 * that is not implemented.
 *
 * @author Azami7
 * @version Ollivanders2
 * @see <a href="https://harrypotter.fandom.com/wiki/Protego_Maxima">Harry Potter Wiki - Protego Maxima</a>
 */
public class PROTEGO_MAXIMA extends ShieldSpell {
    /**
     * Minimum spell radius, in blocks.
     */
    public static final int minRadiusConfig = 5;

    /**
     * Maximum spell radius, in blocks.
     */
    public static final int maxRadiusConfig = 30;

    /**
     * Minimum spell duration: 5 minutes.
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * Maximum spell duration: 30 minutes.
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    /**
     * Projectiles this spell watches so it can destroy any that enter the area. Spigot has no projectile-move event,
     * so they are tracked here and checked each {@link #upkeep()}.
     */
    ArrayList<Projectile> projectiles = new ArrayList<>();

    /**
     * Tracking range around the spell center, in blocks: the maximum flight distance of an arrow, the longest-flying
     * projectile.
     *
     * @see <a href="https://minecraft.fandom.com/wiki/Arrow">Minecraft Wiki - Arrow</a>
     */
    private final int maxDistance = 120;

    /**
     * Constructor for loading a saved spell from disk; do not use to cast a new spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public PROTEGO_MAXIMA(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.PROTEGO_MAXIMA;
    }

    /**
     * Constructor for casting a new Protego Maxima spell.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the UUID of the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the radius for this spell, clamped to the min/max bounds
     * @param duration the duration in ticks, clamped to the min/max bounds
     */
    public PROTEGO_MAXIMA(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.PROTEGO_MAXIMA;

        setRadius(radius);
        setDuration(duration, false);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * Get the projectiles this spell is currently tracking.
     *
     * @return a copy of the tracked projectiles; empty if none
     */
    @NotNull
    public List<Projectile> getTrackedProjectiles() {
        return new ArrayList<>(projectiles);
    }

    /**
     * Age the spell and destroy any tracked projectile that has entered the area, with a non-damaging explosion. Dead
     * or escaped projectiles are dropped from tracking.
     */
    @Override
    public void upkeep() {
        age();

        // Spigot has no ProjectileMoveEvent (Paper does), so tracked projectiles are checked for entry manually here
        ArrayList<Projectile> projectileIterator = new ArrayList<>(projectiles);
        for (Projectile projectile : projectileIterator) {
            if (!projectile.isDead()) {
                Location projectileLocation = projectile.getLocation();

                if (isLocationInside(projectileLocation)) {
                    common.printDebugMessage("projectile in shield area", null, null, false);
                    projectile.remove();

                    if (!Ollivanders2.testMode)
                        world.createExplosion(projectileLocation, 0.0f, false, false);
                }
                else
                    continue;
            }

            projectiles.remove(projectile);
        }
    }

    /**
     * Cancel an outside entity's attempt to target a player or passive mob inside the area. Hostile mobs inside can
     * still be targeted.
     *
     * @param event the entity target event
     */
    void doOnEntityTargetEvent(@NotNull EntityTargetEvent event) {
        Entity target = event.getTarget();

        if (target == null || !isLocationInside(target.getLocation()))
            return;

        if ((target instanceof Player) || !EntityCommon.isHostile((LivingEntity) target))
            event.setCancelled(true);
    }

    /**
     * Block an OWL-level-or-lower spell projectile crossing into the area, with a non-damaging explosion. Higher-level
     * spells and spells cast by a player inside the area pass through.
     *
     * @param event the spell projectile move event
     */
    void doOnSpellProjectileMoveEvent(@NotNull OllivandersSpellProjectileMoveEvent event) {
        if (event.getSpell().getLevel().ordinal() > MagicLevel.OWL.ordinal()) {
            return;
        }

        for (Player player : getPlayersInsideSpellRadius()) {
            if (event.getPlayer().getUniqueId().equals(player.getUniqueId()))
                return;
        }

        Location projectileLocation = event.getTo();

        if (isLocationInside(projectileLocation)) {
            event.setCancelled(true);

            if (!Ollivanders2.testMode)
                world.createExplosion(projectileLocation, 0.0f, false, false);
        }
    }

    /**
     * Start tracking a projectile launched within {@link #maxDistance} of the spell center so it can be intercepted if
     * it later enters the area.
     *
     * @param event the projectile launch event
     */
    @Override
    void doOnProjectileLaunchEvent(@NotNull ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        EntityType type = event.getEntityType();

        if (Ollivanders2Common.isInside(location, projectile.getLocation(), maxDistance)) {
            common.printDebugMessage("adding projectile " + type, null, null, false);
            projectiles.add(projectile);
        }
    }

    /**
     * Cancel a projectile hitting a player or passive mob inside the area; a backstop for projectiles the upkeep check
     * missed. Hostile mobs inside can still be hit.
     *
     * @param event the projectile hit event
     */
    @Override
    void doOnProjectileHitEvent(@NotNull ProjectileHitEvent event) {
        Entity target = event.getHitEntity();

        if (target == null || !isLocationInside(target.getLocation()))
            return;

        if ((target instanceof Player) || !EntityCommon.isHostile((LivingEntity) target))
            event.setCancelled(true);
    }

    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    @Override
    void doCleanUp() {
    }
}