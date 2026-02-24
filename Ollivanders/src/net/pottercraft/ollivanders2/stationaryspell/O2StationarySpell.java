package net.pottercraft.ollivanders2.stationaryspell;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Collection;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for stationary spells (area-of-effect persistent spells).
 *
 * <p>Stationary spells create protective barriers or effects that persist at a fixed location and affect
 * all players and entities within their radius. They age over time (unless permanent), emit visual effects
 * (flair), and provide a hook system for handling game events. Each spell has configurable minimum and
 * maximum radius and duration constraints that subclasses must define.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Fixed location with configurable radius</li>
 *   <li>Time-based duration (ages each tick unless permanent)</li>
 *   <li>Active/inactive state (duration continues to age when inactive)</li>
 *   <li>Event handler overrides for spell-specific behavior</li>
 *   <li>Serialization/deserialization for persistence across server restarts</li>
 *   <li>Visual effects (flair) at spell location</li>
 * </ul>
 *
 * @author Azami7
 * @version Ollivanders2
 */
public abstract class O2StationarySpell implements Serializable {
    /**
     * The minimum radius for this spell, this must be set by the specific spell
     */
    int minRadius = 0;

    /**
     * The maximum radius for this spell, this must be set by the specific spell
     */
    int maxRadius = 0;

    /**
     * The minimum duration for the spell, this must be set by the specific spell
     */
    int minDuration = 0;

    /**
     * The maximum duration for the spell, this must be set by the specific spell
     */
    int maxDuration = 0;

    /**
     * Whether this spell is permanent (never ages or expires).
     */
    boolean permanent = false;

    /**
     * A reference to the plugin
     */
    Ollivanders2 p;

    /**
     * Common functions
     */
    final Ollivanders2Common common;

    /**
     * The UUID of the caster of this stationary spell
     */
    UUID playerUUID;

    /**
     * The type of stationary spell
     */
    O2StationarySpellType spellType;

    /**
     * The location of this stationary spell
     */
    Location location;

    /**
     * The remaining duration of this stationary spell
     */
    int duration = 10;

    /**
     * Is this spell currently active. Duration still counts down when spell is inactive.
     */
    boolean active = true;

    /**
     * Whether this spell is marked for removal (expired and waiting to be cleaned up).
     */
    boolean kill = false;

    /**
     * The radius of this stationary spell from the location
     */
    int radius = 1;

    /**
     * Constructs a stationary spell from deserialized data at server startup.
     *
     * <p>Used only for loading saved spells from disk. Subclasses should not call this directly
     * when casting a new spell - use the full constructor instead. Calls initRadiusAndDurationMinMax()
     * to set spell-specific radius and duration constraints.</p>
     *
     * @param plugin a callback to the MC plugin
     */
    public O2StationarySpell(@NotNull Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(p);

        initRadiusAndDurationMinMax();
    }

    /**
     * Constructs a new stationary spell cast by a player.
     *
     * <p>Creates a new spell at the specified location with the given caster. Initializes spell-specific
     * constraints via initRadiusAndDurationMinMax(). This constructor should be used when casting a new spell.</p>
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the UUID of the player who cast the spell
     * @param location the center location of the spell
     */
    public O2StationarySpell(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location) {
        p = plugin;
        common = new Ollivanders2Common(p);

        initRadiusAndDurationMinMax();

        playerUUID = pid;
        this.location = location;
    }

    /**
     * Initializes the minimum and maximum radius and duration bounds for this spell.
     *
     * <p>Subclasses must implement to set spell-specific constraints. These values are used to clamp
     * radius and duration values when they're set or modified. Must be called during object construction.</p>
     */
    abstract void initRadiusAndDurationMinMax();

    /**
     * Get the duration remaining for this spell
     *
     * @return the duration remaining
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Set the duration remaining for this stationary spell. This can be lower than the min duration since spells may reduce the time remaining and loading stationary spells from the
     * save file on restart should set them to however much time was left
     *
     * @param duration the duration in game ticks
     */
    void setDuration(int duration) {
        if (maxDuration == 0 || minDuration == 0) {
            common.printDebugMessage("Min or max duration not set in " + spellType.getSpellName(), null, null, true);
        }
        else {
            if (duration > maxDuration)
                duration = maxDuration;
            else if (duration < minDuration)
                duration = minDuration;
        }

        this.duration = duration;
    }

    /**
     * Get the spell's current radius
     *
     * @return the radius of this spell
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Set the radius of this stationary spell.
     *
     * @param radius the radius in blocks
     */
    void setRadius(int radius) {
        if (minRadius == 0 || maxRadius == 0) {
            common.printDebugMessage("Min or max radius not set in " + spellType.getSpellName(), null, null, true);
        }
        else {
            if (radius < minRadius)
                radius = minRadius;
            else if (radius > maxRadius)
                radius = maxRadius;
        }

        this.radius = radius;
    }

    /**
     * Increase the radius of this stationary spell.
     *
     * @param increase the amount to increase the radius by
     */
    public void increaseRadius(int increase) {
        // if they sent a negative number, do nothing
        if (increase <= 0)
            return;

        radius = radius + increase;

        if (maxRadius == 0) {
            common.printDebugMessage("Max radius not set in " + spellType.getSpellName(), null, null, true);
        }
        else {
            if (radius > maxRadius)
                radius = maxRadius;
        }
    }

    /**
     * Decrease the radius of this stationary spell.
     *
     * @param decrease the amount to decrease the radius
     */
    public void decreaseRadius(int decrease) {
        // if they sent negative number, do nothing
        if (decrease <= 0)
            return;

        radius = radius - decrease;

        // if radius is smaller than the min, kill this spell
        if (minRadius == 0) {
            common.printDebugMessage("Min radius not set in " + spellType.getSpellName(), null, null, true);
        }
        else {
            if (radius < minRadius)
                kill();
        }
    }

    /**
     * Increase the duration of this stationary spell.
     *
     * @param increase the amount to increase the duration by
     */
    public void increaseDuration(int increase) {
        duration = duration + increase;

        if (maxDuration == 0) {
            common.printDebugMessage("Max duration not set in " + spellType.getSpellName(), null, null, true);
        }
        else {
            if (duration > maxDuration)
                duration = maxDuration;
        }
    }

    /**
     * Set the center location of this stationary spell. This should only be used at start up to set up the saved
     * stationary spells.
     *
     * @param location the spell location
     */
    void setLocation(@NotNull Location location) {
        this.location = location;
    }

    /**
     * Set the ID of the player who cast this spell. This should only be used at start up to set up the saved
     * stationary spells.
     *
     * @param pid the player ID
     */
    void setPlayerID(@NotNull UUID pid) {
        playerUUID = pid;
    }

    /**
     * Get the type of this stationary spell
     *
     * @return the spell type
     */
    @NotNull
    public O2StationarySpellType getSpellType() {
        return spellType;
    }

    /**
     * Set whether this spell should be active or not. Duration counts down regardless of this state.
     *
     * @param active true if active, false is not active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Checks if this spell is permanent (never ages or expires).
     *
     * @return true if the spell is permanent, false if it ages over time
     */
    public boolean isPermanent() {
        return permanent;
    }

    /**
     * Ages the StationarySpellObj
     */
    public void age() {
        if (permanent)
            return;

        age(1);
    }

    /**
     * Ages the StationarySpellObj
     *
     * @param i number of ticks to age the spell by
     */
    public void age(int i) {
        if (permanent)
            return;

        duration = duration - i;

        if (duration <= 0)
            kill();
    }

    /**
     * Ages the stationary spell by the specified percent.
     *
     * @param percent the percent (as a decimal) to age the spell by, 0.1-1 where 0.1 = 10%
     */
    public void ageByPercent(double percent) {
        if (permanent)
            return;

        if (percent <= 0) // if they sent an invalid percent, do nothing
            return;
        else if (percent > 1.0)
            percent = 1.0;

        if (percent == 1.0)
            kill();
        else
            age((int)(duration * percent));
    }

    /**
     * This kills the spell
     */
    public void kill() {
        flair(20);
        kill = true;

        // clean up for the spell, if relevant
        doCleanUp();

        Player caster = p.getServer().getPlayer(playerUUID);
        if (caster != null)
            caster.sendMessage(Ollivanders2.chatColor + "Your " + spellType.getSpellName() + " spell has ended.");
    }

    /**
     * Is the location specified inside the stationary spell's radius?
     *
     * @param loc the location specified.
     * @return true if the location is inside of this spell radius, false otherwise
     */
    public boolean isLocationInside(@NotNull Location loc) {
        return Ollivanders2Common.isInside(location, loc, radius);
    }

    /**
     * Gets the block at the center of this spell radius
     *
     * @return the center block for this spell
     */
    @NotNull
    public Block getBlock() {
        return location.getBlock();
    }

    /**
     * Get living entities whose eye location is within the radius. We use eye radius to handle entities bigger than 1 block.
     *
     * @return a list of living entities with an eye location within radius
     */
    @NotNull
    public List<LivingEntity> getLivingEntitiesInsideSpellRadius() {
        Collection<LivingEntity> entities = EntityCommon.getLivingEntitiesInRadius(location, radius);

        List<LivingEntity> close = new ArrayList<>();

        /* only add living entities if their eye location is within the radius */
        for (LivingEntity e : entities) {
            if (location.distance(e.getEyeLocation()) < radius)
                close.add(e);
        }

        return close;
    }

    /**
     * Get the players inside this spell radius. We use eye radius to handle entities bigger than 1 block.
     *
     * @return a list of the players with an eye location within the radius
     */
    public List<Player> getPlayersInsideSpellRadius() {
        List<Player> players = new ArrayList<>();

        for (Player player : p.getServer().getOnlinePlayers()) {
            if (isLocationInside(player.getLocation()))
                players.add(player);
        }

        return players;
    }

    /**
     * Makes a particle effect at all points along the radius of spell and at spell loc
     * <p>
     * {@link Ollivanders2Common}
     *
     * @param intensity intensity of the flair
     */
    public void flair(int intensity) {
        Ollivanders2Common.flair(location, radius, intensity);
    }

    /**
     * Get the ID of the player that cast the spell
     *
     * @return the MC UUID of the player that cast the spell
     */
    @NotNull
    public UUID getCasterID() {
        return playerUUID;
    }

    /**
     * Get the max radius for this spell
     *
     * @return the max radius possible for this spell
     */
    public int getMaxRadius() {
        return maxRadius;
    }

    /**
     * Get the min radius for this spell
     *
     * @return the min default radius for this spell
     */
    public int getMinRadius() {
        return minRadius;
    }

    /**
     * Get the max duration for this spell
     *
     * @return the max duration possible for this spell
     */
    public int getMaxDuration() {
        return maxDuration;
    }

    /**
     * Get the min duration for this spell
     *
     * @return the min default duration for this spell
     */
    public int getMinDuration() {
        return minDuration;
    }

    /**
     * Is this stationary spell currently active?
     *
     * @return true if active, false if not active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Is this spell killed (marked for removal)?
     *
     * @return true if killed, false otherwise
     */
    public boolean isKilled() {
        return kill;
    }

    /**
     * Get a copy of the location for this stationary spell. A copy is returned because a stationary spell location cannot be changed.
     *
     * @return a clone of the spell location
     */
    @NotNull
    public Location getLocation() {
        return location.clone();
    }

    /**
     * Performs per-tick upkeep for this spell.
     *
     * <p>Subclasses must implement to handle spell lifecycle management. Should call age() or age(int)
     * to decrement the spell duration each tick, which will automatically kill the spell when duration
     * reaches zero. Permanent spells will never age.</p>
     */
    abstract public void upkeep();

    /**
     * Serialize all data specific to this spell so it can be saved.
     *
     * @return a map of the serialized data
     */
    @NotNull
    abstract Map<String, String> serializeSpellData();

    /**
     * Deserialize the data for this spell and load the data to this spell.
     *
     * @param spellData the serialized spell data
     */
    abstract void deserializeSpellData(@NotNull Map<String, String> spellData);

    /**
     * Handles player movement events across the spell area.
     *
     * <p>Subclasses can override to implement spell-specific behavior when players move relative to
     * the spell area, such as entry restrictions, visibility toggling, or boundary detection.</p>
     *
     * @param event the player move event
     */
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
    }

    /**
     * Handle creatures from spawning
     *
     * @param event the event
     */
    void doOnCreatureSpawnEvent(@NotNull CreatureSpawnEvent event) {
    }

    /**
     * Handle entities spawning
     *
     * @param event the event
     */
    void doOnEntityTargetEvent(@NotNull EntityTargetEvent event) {
    }

    /**
     * Handles asynchronous player chat events.
     *
     * <p>Subclasses can override to implement spell-specific chat behavior, such as filtering recipients
     * or blocking messages from reaching certain areas.</p>
     *
     * @param event the async player chat event
     */
    void doOnAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event) {
    }

    /**
     * Handle block break event
     *
     * @param event the event
     */
    void doOnBlockBreakEvent(@NotNull BlockBreakEvent event) {
    }

    /**
     * Handle break door event
     *
     * @param event the event
     */
    void doOnEntityBreakDoorEvent(@NotNull EntityBreakDoorEvent event) {
    }

    /**
     * Handle entity change block event
     *
     * @param event the event
     */
    void doOnEntityChangeBlockEvent(@NotNull EntityChangeBlockEvent event) {
    }

    /**
     * Handle entity interact event
     *
     * @param event the event
     */
    void doOnEntityInteractEvent(@NotNull EntityInteractEvent event) {
    }

    /**
     * Handle player interact event
     *
     * @param event the event
     */
    void doOnPlayerInteractEvent(@NotNull PlayerInteractEvent event) {
    }

    /**
     * Handle entity damage
     *
     * @param event the event
     */
    void doOnEntityDamageEvent(@NotNull EntityDamageEvent event) {
    }

    /**
     * Handle apparate by name event
     *
     * @param event the event
     */
    void doOnOllivandersApparateByNameEvent(@NotNull OllivandersApparateByNameEvent event) {
    }

    /**
     * Handle apparate by coordinate event
     *
     * @param event the event
     */
    void doOnOllivandersApparateByCoordinatesEvent(@NotNull OllivandersApparateByCoordinatesEvent event) {
    }

    /**
     * Handle entity teleport event
     *
     * @param event the event
     */
    void doOnEntityTeleportEvent(@NotNull EntityTeleportEvent event) {
    }

    /**
     * Handle player teleport event
     *
     * @param event the event
     */
    void doOnPlayerTeleportEvent(@NotNull PlayerTeleportEvent event) {
    }

    /**
     * Handle entity combust by block events
     *
     * @param event the event
     */
    void doOnEntityCombustEvent(@NotNull EntityCombustEvent event) {
    }

    /**
     * Handle spell projectile move events
     *
     * @param event the spell projectile move event
     */
    void doOnSpellProjectileMoveEvent(@NotNull OllivandersSpellProjectileMoveEvent event) {
    }

    /**
     * Handle spell world load events
     *
     * @param event the world load event
     */
    void doOnPlayerJoinEvent(@NotNull PlayerJoinEvent event) {
    }

    /**
     * Handle item despawn events
     *
     * @param event the item despawn event
     */
    void doOnItemDespawnEvent(@NotNull ItemDespawnEvent event) {
    }

    /**
     * Handle items being picked up by entities
     *
     * @param event the event
     */
    void doOnEntityPickupItemEvent(@NotNull EntityPickupItemEvent event) {
    }

    /**
     * Handle items being picked up by things like hoppers
     *
     * @param event the event
     */
    void doOnInventoryItemPickupEvent(@NotNull InventoryPickupItemEvent event) {
    }

    /**
     * Handle player death event
     *
     * @param event the event
     */
    void doOnPlayerDeathEvent(@NotNull PlayerDeathEvent event) {
    }

    /**
     * Handle block from to events (like water or lava flowing)
     *
     * @param event the event
     */
    void doOnBlockFromToEvent(@NotNull BlockFromToEvent event) {
    }

    /**
     * Handle player bucket empty events
     *
     * @param event the event
     */
    void doOnPlayerBucketEmptyEvent(@NotNull PlayerBucketEmptyEvent event) {
    }

    /**
     * Handle projectile launch events
     *
     * @param event the event
     */
    void doOnProjectileLaunchEvent(@NotNull ProjectileLaunchEvent event) {
    }

    /**
     * Handle projectile hit events
     *
     * @param event the event
     */
    void doOnProjectileHitEvent(@NotNull ProjectileHitEvent event) {
    }

    /**
     * Performs cleanup when this spell ends.
     *
     * <p>Called by kill() when the spell's duration expires. Subclasses implement to remove spell effects,
     * unhide hidden players, undo restrictions, or perform any other cleanup necessary for spell termination.</p>
     */
    abstract void doCleanUp();

    /**
     * Checks if this spell has been properly deserialized with required data.
     *
     * <p>Verifies that the spell has both a caster UUID and a location set, which are required
     * for a spell to function correctly.</p>
     *
     * @return true if both playerUUID and location are set, false otherwise
     */
    public boolean checkSpellDeserialization() {
        return playerUUID != null && location != null;
    }
}