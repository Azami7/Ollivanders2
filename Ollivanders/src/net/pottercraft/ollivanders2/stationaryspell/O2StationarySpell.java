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
import org.bukkit.World;
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
 * Abstract base class for stationary spells: area-of-effect spells that persist at a fixed location and act on the
 * players and entities within their radius until their duration expires.
 * <p>
 * Subclasses define their radius and duration bounds via {@link #initRadiusAndDurationMinMax()}, drive their own
 * lifecycle in {@link #upkeep()}, and override the {@code doOn...Event} hooks to react to game events. Spells age each
 * tick (unless {@link #permanent}) and are serialized to disk so they survive server restarts.
 *
 * @author Azami7
 * @version Ollivanders2
 */
public abstract class O2StationarySpell implements Serializable {
    /**
     * Radius bounds this spell clamps to; set by the subclass in {@link #initRadiusAndDurationMinMax()}.
     */
    int minRadius = 0;

    /**
     * Radius bounds this spell clamps to; set by the subclass in {@link #initRadiusAndDurationMinMax()}.
     */
    int maxRadius = 0;

    /**
     * Duration bounds this spell clamps to; set by the subclass in {@link #initRadiusAndDurationMinMax()}.
     */
    int minDuration = 0;

    /**
     * Duration bounds this spell clamps to; set by the subclass in {@link #initRadiusAndDurationMinMax()}.
     */
    int maxDuration = 0;

    /**
     * Whether this spell is permanent; a permanent spell never ages or expires.
     */
    boolean permanent = false;

    Ollivanders2 p;

    final Ollivanders2Common common;

    /**
     * The UUID of the player who cast this spell.
     */
    UUID playerUUID;

    O2StationarySpellType spellType;

    Location location;

    World world;

    /**
     * Remaining duration in ticks. Counts down each tick regardless of {@link #active}.
     */
    int duration = 10;

    /**
     * Whether this spell is currently acting on its area. Duration still counts down while inactive.
     */
    boolean active = true;

    /**
     * Whether this spell is marked for removal. See {@link #isKilled()}.
     */
    boolean kill = false;

    int radius = 1;

    /**
     * Constructor for loading a saved spell from disk. The caller must populate the spell's location, caster, and
     * serialized state afterwards; use the location constructor when casting a new spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public O2StationarySpell(@NotNull Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(p);

        initRadiusAndDurationMinMax();
    }

    /**
     * Constructor for casting a new stationary spell. The spell is killed immediately if the location has no world.
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

        world = location.getWorld();
        if (world == null) {
            common.printLogMessage("O2StationarySpell: null world", null, null, true);
            kill();
        }
    }

    /**
     * Set this spell's {@link #minRadius}, {@link #maxRadius}, {@link #minDuration}, and {@link #maxDuration} bounds.
     * Called during construction, before radius or duration are clamped.
     */
    abstract void initRadiusAndDurationMinMax();

    /**
     * Get the remaining duration in ticks.
     *
     * @return the duration remaining
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Set the remaining duration, clamped to {@code [minDuration, maxDuration]} unless allowLowerThanMin then it only
     * limits maxDuration.
     *
     * @param duration the duration in game ticks
     * @param allowLowerThanMin if set to true, allows duration to be set below min - this is used for reloading the spell from save
     */
    void setDuration(int duration, boolean allowLowerThanMin) {
        if (duration > maxDuration)
            duration = maxDuration;
        else if (!allowLowerThanMin && duration < minDuration)
            duration = minDuration;

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
     * Set the radius, clamped to {@code [minRadius, maxRadius]}. No clamping happens (and a debug message is logged)
     * if either bound is still zero.
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
     * Increase the radius, capped at {@link #maxRadius}.
     *
     * @param increase the amount to increase the radius by; ignored if not positive
     */
    public void increaseRadius(int increase) {
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
     * Decrease the radius. Kills the spell if the radius drops below {@link #minRadius}.
     *
     * @param decrease the amount to decrease the radius by; ignored if not positive
     */
    public void decreaseRadius(int decrease) {
        if (decrease <= 0)
            return;

        radius = radius - decrease;

        if (minRadius == 0) {
            common.printDebugMessage("Min radius not set in " + spellType.getSpellName(), null, null, true);
        }
        else {
            if (radius < minRadius)
                kill();
        }
    }

    /**
     * Increase the remaining duration, capped at {@link #maxDuration}.
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
     * Set the center location. Kills the spell if the location has no world. Used when restoring saved spells.
     *
     * @param location the spell location
     */
    void setLocation(@NotNull Location location) {
        this.location = location;

        world = location.getWorld();
        if (world == null) {
            common.printLogMessage("O2StationarySpell.setLocation: null world", null, null, true);
            kill();
        }
    }

    /**
     * Set the UUID of the caster. Used when restoring saved spells.
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
     * Age this spell by one tick. No-op if the spell is permanent.
     */
    public void age() {
        if (permanent)
            return;

        age(1);
    }

    /**
     * Age this spell, killing it once duration reaches zero. No-op if the spell is permanent.
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
     * Age this spell by a fraction of its remaining duration. No-op if the spell is permanent.
     *
     * @param percent the fraction to age by, in the range (0, 1]; values &gt; 1 are treated as 1, which kills the
     *                spell; non-positive values are ignored
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
     * End this spell: runs a final flair, marks it for removal, runs {@link #doCleanUp()}, and notifies the caster if
     * they are online.
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
     * Check whether a location falls within this spell's radius.
     *
     * @param loc the location to test
     * @return true if the location is inside this spell's radius, false otherwise
     */
    public boolean isLocationInside(@NotNull Location loc) {
        return Ollivanders2Common.isInside(location, loc, radius);
    }

    /**
     * Get the block at the center of this spell.
     *
     * @return the center block for this spell
     */
    @NotNull
    public Block getBlock() {
        return location.getBlock();
    }

    /**
     * Get the living entities whose eye location is within the radius. Eye location is used so that entities taller
     * than one block are caught.
     *
     * @return the matching living entities; empty if none
     */
    @NotNull
    public List<LivingEntity> getLivingEntitiesInsideSpellRadius() {
        Collection<LivingEntity> entities = EntityCommon.getLivingEntitiesInRadius(location, radius);

        List<LivingEntity> close = new ArrayList<>();

        for (LivingEntity e : entities) {
            if (location.distance(e.getEyeLocation()) < radius)
                close.add(e);
        }

        return close;
    }

    /**
     * Get the online players whose location is within this spell's radius.
     *
     * @return the matching players; empty if none
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
     * Emit a particle effect around this spell's radius and center as a visual cue.
     *
     * @param intensity the intensity of the flair
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
     * Run this spell's per-tick behavior. Implementations should call {@link #age()} or {@link #age(int)} so the
     * spell expires when its duration runs out.
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
     * Handle a player move event. Override to react to players moving into, out of, or within the spell area.
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
     * Handle an entity target event
     *
     * @param event the event
     */
    void doOnEntityTargetEvent(@NotNull EntityTargetEvent event) {
    }

    /**
     * Handle an async player chat event. Override to filter recipients or block messages around the spell area.
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
     * Handle a player join event
     *
     * @param event the player join event
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
     * Undo this spell's effects when it ends. Called by {@link #kill()}; implementations remove effects, unhide
     * players, and reverse any restrictions the spell put in place.
     */
    abstract void doCleanUp();

    /**
     * Check that this spell was deserialized with the caster UUID and location it needs to function.
     *
     * @return true if both the caster UUID and location are set, false otherwise
     */
    public boolean checkSpellDeserialization() {
        return playerUUID != null && location != null;
    }
}