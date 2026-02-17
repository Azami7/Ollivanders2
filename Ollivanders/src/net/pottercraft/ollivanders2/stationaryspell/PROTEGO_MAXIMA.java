package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.MagicLevel;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A stationary shield spell that creates a comprehensive protective barrier over an area.
 *
 * <p>Protego Maxima is a powerful defensive charm that provides multiple layers of protection:
 * <ul>
 *   <li>Kills tracked projectiles that enter the protected area</li>
 *   <li>Prevents living entities outside the area from targeting players or passive mobs inside</li>
 *   <li>Blocks lower-level spell projectiles from crossing into the protected area</li>
 *   <li>Allows all spells cast by players inside the area to exit freely</li>
 *   <li>Prevents projectiles from hitting players or non-hostile mobs inside the area</li>
 *   <li>Allows projectiles to hit hostile mobs inside the area</li>
 * </ul>
 * </p>
 *
 * <p>Note: In the Harry Potter universe, this spell would also repel hostile entities like Dementors,
 * but Spigot does not provide an entity move event for this functionality.</p>
 *
 * @author Azami7
 * @version Ollivanders2
 * @see <a href="https://harrypotter.fandom.com/wiki/Protego_Maxima">https://harrypotter.fandom.com/wiki/Protego_Maxima</a>
 * @since 2.21
 */
public class PROTEGO_MAXIMA extends ShieldSpell {
    /**
     * Minimum spell radius (5 blocks).
     */
    public static final int minRadiusConfig = 5;

    /**
     * Maximum spell radius (30 blocks).
     */
    public static final int maxRadiusConfig = 30;

    /**
     * Minimum spell duration (5 minutes).
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerMinute * 5;

    /**
     * Maximum spell duration (30 minutes).
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    /**
     * The projectiles this spell tracks for killing when they enter the protected area.
     *
     * <p>Spigot does not provide a ProjectileMoveEvent, so projectiles must be manually tracked
     * and checked during upkeep() to determine if they've entered the spell area.</p>
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
     * Constructs a new PROTEGO_MAXIMA spell cast by a player.
     *
     * <p>Creates a protective shield at the specified location with the given radius and duration.
     * The shield will protect against projectiles, block lower-level spells, and prevent hostile
     * targeting of players and passive mobs inside the protected area.</p>
     *
     * @param plugin   a callback to the MC plugin (not null)
     * @param pid      the UUID of the player who cast the spell (not null)
     * @param location the center location of the spell (not null)
     * @param radius   the radius for this spell (will be clamped to min/max values)
     * @param duration the duration of the spell in ticks (will be clamped to min/max values)
     */
    public PROTEGO_MAXIMA(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);
        spellType = O2StationarySpellType.PROTEGO_MAXIMA;

        setRadius(radius);
        setDuration(duration);

        common.printDebugMessage("Creating stationary spell type " + spellType.name(), null, null, false);
    }

    /**
     * Initializes the radius and duration constraints for this spell.
     *
     * <p>Sets the spell's radius boundaries (5-30 blocks) and duration boundaries (5 to 30 minutes).</p>
     */
    @Override
    void initRadiusAndDurationMinMax() {
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
    }

    /**
     * Gets the projectiles currently being tracked by this spell.
     *
     * <p>Returns a copy of the tracked projectiles list to prevent external modifications.</p>
     *
     * @return a list of projectiles being tracked (not null, may be empty)
     */
    @NotNull
    public List<Projectile> getTrackedProjectiles() {
        return new ArrayList<>(projectiles);
    }

    /**
     * Ages the spell and kills tracked projectiles that enter the protected area.
     *
     * <p>Checks each tracked projectile to see if it has entered the spell area. If so, removes
     * the projectile and creates a non-damaging explosion at that location. Dead or escaped projectiles
     * are removed from the tracking list.</p>
     */
    @Override
    public void upkeep() {
        age();

        // we need to track projectile objects such as arrows and kill them when they hit the spell boundary. There
        // is no ProjectileMoveEvent in spigot like there is in Paper so we have to do this manually.
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
     * Prevents living entities outside the spell area from targeting players or passive mobs inside.
     *
     * <p>When a living entity targets another entity, this method checks if the target is inside
     * the spell area. If the target is a player or passive mob inside the area, the targeting event
     * is cancelled. Hostile mobs inside the area can still be targeted. Entities outside the area
     * are not affected by this protection.</p>
     *
     * @param event the entity target event (not null)
     */
    void doOnEntityTargetEvent(@NotNull EntityTargetEvent event) {
        Entity target = event.getTarget();

        // stop if the target is not in the spell area
        if (target == null || !isLocationInside(target.getLocation()))
            return;

        // stop if the targeted entity is not either a player or a passive mob
        if ((target instanceof Player) || !EntityCommon.isHostile((LivingEntity) target))
            event.setCancelled(true);
    }

    /**
     * Blocks lower-level spell projectiles from crossing into the protected area.
     *
     * <p>When a spell projectile moves, this method checks if:
     * <ul>
     *   <li>The spell is higher level (above OWL level) - if yes, allows it through</li>
     *   <li>The spell caster is inside the spell area - if yes, allows the spell to exit freely</li>
     *   <li>The spell is moving into the protected area - if yes, blocks it and creates a non-damaging explosion</li>
     * </ul>
     * </p>
     *
     * @param event the spell projectile move event (not null)
     */
    void doOnSpellProjectileMoveEvent(@NotNull OllivandersSpellProjectileMoveEvent event) {
        // do not stop higher level spells
        if (event.getSpell().getLevel().ordinal() > MagicLevel.OWL.ordinal()) {
            return;
        }

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
     * Starts tracking projectiles launched within range of the spell center.
     *
     * <p>When a projectile is launched, this method checks if it is within 120 blocks of the spell
     * center (the maximum range of an arrow). If so, the projectile is added to the tracked projectiles
     * list so it can be intercepted if it enters the protected area during upkeep.</p>
     *
     * @param event the projectile launch event (not null)
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
     * Prevents projectiles from hitting players or passive mobs inside the protected area.
     *
     * <p>This is a backup protection in case tracked projectiles slip through the upkeep() checks.
     * When a projectile hits an entity inside the spell area, this method checks if the hit target
     * is a player or non-hostile mob. If so, the hit event is cancelled to prevent damage. Hostile
     * mobs inside the area can still be hit by projectiles. Entities outside the area are not protected.</p>
     *
     * @param event the projectile hit event (not null)
     */
    @Override
    void doOnProjectileHitEvent(@NotNull ProjectileHitEvent event) {
        Entity target = event.getHitEntity();

        // stop if the target is not in the spell area
        if (target == null || !isLocationInside(target.getLocation()))
            return;

        // stop if the targeted entity is not either a player or a passive mob
        if ((target instanceof Player) || !EntityCommon.isHostile((LivingEntity) target))
            event.setCancelled(true);
    }

    /**
     * Serializes the protego maxima spell data for persistence.
     *
     * <p>The protego maxima spell has no extra data to serialize beyond the base spell properties,
     * so this method returns an empty map.</p>
     *
     * @return an empty map (the spell has no custom data to serialize)
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    /**
     * Deserializes protego maxima spell data from saved state.
     *
     * <p>The protego maxima spell has no extra data to deserialize, so this method does nothing.</p>
     *
     * @param spellData the serialized spell data map (not used)
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    /**
     * Cleans up when the protego maxima spell ends.
     *
     * <p>The protego maxima spell requires no special cleanup on termination. The tracked projectiles
     * are released when the spell is killed, and no other resources need to be freed.</p>
     */
    @Override
    void doCleanUp() {
    }
}