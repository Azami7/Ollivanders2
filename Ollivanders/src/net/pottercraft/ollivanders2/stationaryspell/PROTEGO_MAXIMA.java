package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import org.bukkit.Location;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.World;
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
import java.util.Map;
import java.util.UUID;

/**
 * A stronger version of Protego that covers an area (rather than the caster) and protects against stronger spells. In HP this
 * spell would also repel hostile entities like dementors but Spigot does not provide an entity move event so we have no way
 * to handle controlling entity movement.
 * <p>
 * {@link net.pottercraft.ollivanders2.spell.PROTEGO_MAXIMA}
 *
 * @author Azami7
 * @version Ollivanders2
 * @see <a href="https://harrypotter.fandom.com/wiki/Protego_Maxima">https://harrypotter.fandom.com/wiki/Protego_Maxima</a>
 * @since 2.21
 */
public class PROTEGO_MAXIMA extends ShieldSpell {
    /**
     * min radius for this spell
     */
    public static final int minRadiusConfig = 5;

    /**
     * max radius for this spell
     */
    public static final int maxRadiusConfig = 30;

    /**
     * min duration for this spell
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * max duration for this spell
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

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
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public PROTEGO_MAXIMA(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.PROTEGO_MAXIMA;
    }

    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the radius for this spell
     * @param duration the duration of the spell
     */
    public PROTEGO_MAXIMA(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.PROTEGO_MAXIMA;

        setRadius(radius);
        setDuration(duration);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * Age the spell and damage any entities nearby
     */
    @Override
    public void upkeep() {
        age();

        // check to see if any of the projectiles we're tracking have crossed the shield boundary
        ArrayList<Projectile> projectileIterator = new ArrayList<>(projectiles);
        for (Projectile projectile : projectileIterator) {
            if (!projectile.isDead()) {
                Location projectileLocation = projectile.getLocation();

                if (isLocationInside(projectileLocation)) {
                    common.printDebugMessage("projectile in shield area", null, null, false);
                    projectile.remove();

                    // do an explosion
                    doExplosion(projectileLocation);
                }
                else
                    continue;
            }

            // dead projectile
            projectiles.remove(projectile);
        }
    }

    /**
     * Prevent living entities outside the spell area targeting players inside the spell area
     *
     * @param event the event
     */
    void doOnEntityTargetEvent(@NotNull EntityTargetEvent event) {
        Entity entity = event.getEntity();
        Entity target = event.getTarget();

        // stop if the entity and target are not both living entities
        if (!(entity instanceof LivingEntity) || !(target instanceof LivingEntity))
            return;

        // stop if the targeted entity is not either a player or a passive mob
        if (!(target instanceof Player) || EntityCommon.isHostile((LivingEntity) target))
            return;

        if (isLocationInside(entity.getLocation()) && isLocationInside(target.getLocation()))
            event.setCancelled(true);
    }

    /**
     * Stop spell projectiles when they hit the boundary of the spell.
     *
     * @param event the spell projectile move event
     */
    void doOnSpellProjectileMoveEvent(@NotNull OllivandersSpellProjectileMoveEvent event) {
        // don't stop spells of players inside the spell area from going across the spell boundary
        for (Player player : getPlayersInsideSpellRadius()) {
            if (event.getPlayer().getUniqueId().equals(player.getUniqueId()))
                return;
        }

        Location projectileLocation = event.getTo();

        if (isLocationInside(projectileLocation)) {
            event.setCancelled(true);

            // do an explosion
            doExplosion(projectileLocation);
        }
    }

    /**
     * Do an explosion at the location that does not break blocks or start a fire
     *
     * @param explosionLocation the location to do the explosion
     */
    private void doExplosion(Location explosionLocation) {
        World world = explosionLocation.getWorld();
        if (world == null)
            common.printDebugMessage("PROTEGO_MAXIMAA.doExplosion", null, null, false);
        else
            world.createExplosion(explosionLocation, 0.0f, false, false);
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

        // is this projectile within maxDistance of this spell center?
        if (Ollivanders2Common.isInside(location, projectile.getLocation(), maxDistance)) {
            common.printDebugMessage("adding projectile " + type, null, null, false);
            projectiles.add(projectile);
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

        for (Player player : getPlayersInsideSpellRadius()) {
            if (entity != null && (entity.getUniqueId().equals(player.getUniqueId())))
                event.setCancelled(true);
        }
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