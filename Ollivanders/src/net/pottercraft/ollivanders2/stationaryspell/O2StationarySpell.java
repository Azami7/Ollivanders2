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
 * Stationary spell object in Ollivanders2
 *
 * @author Azami7
 * @version Ollivanders2
 */
public abstract class O2StationarySpell implements Serializable {
    /**
     * The minimum radius for this spell
     */
    int minRadius = 5;

    /**
     * The maximum radius for this spell
     */
    int maxRadius = 5;

    /**
     * The minimum duration for the spell
     */
    int minDuration = Ollivanders2Common.ticksPerSecond * 30; // 30 seconds

    /**
     * The maximum duration for the spell
     */
    int maxDuration = Ollivanders2Common.ticksPerMinute * 30; // 30 minutes

    /**
     * True if this spell permanent
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
     * Is this spell expired
     */
    boolean kill = false;

    /**
     * The radius of this stationary spell from the location
     */
    int radius = 1;

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public O2StationarySpell(@NotNull Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(p);
    }

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the player who cast the spell
     * @param location the center location of the spell
     */
    public O2StationarySpell(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location) {
        p = plugin;
        common = new Ollivanders2Common(p);

        playerUUID = pid;
        this.location = location;
    }

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
        if (duration < 0)
            duration = 0;
        else if (duration > maxDuration)
            duration = maxDuration;

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
        if (radius < minRadius)
            radius = minRadius;
        else if (radius > maxRadius)
            radius = maxRadius;

        this.radius = radius;
    }

    /**
     * Increase the radius of this stationary spell.
     *
     * @param increase the amount to increase the radius by
     */
    public void increaseRadius(int increase) {
        // if they sent a negative number, do decrease
        if (increase < 0)
            decreaseRadius(increase);

        radius = radius + increase;

        if (radius > maxRadius)
            radius = maxRadius;
    }

    /**
     * Decrease the radius of this stationary spell.
     *
     * @param decrease the amount to decrease the radius
     */
    public void decreaseRadius(int decrease) {
        // if they sent negative number, do increase
        if (decrease < 0)
            increaseRadius(decrease);

        radius = radius - decrease;

        // if radius is smaller than the min, kill this spell
        if (radius < minRadius)
            kill();
    }

    /**
     * Increase the duration of this stationary spell.
     *
     * @param increase the amount to increase the duration by
     */
    public void increaseDuration(int increase) {
        duration = duration + increase;

        if (duration > maxDuration)
            duration = maxDuration;
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
     * @param percent the percent to age the spell by
     */
    public void ageByPercent(int percent) {
        if (permanent)
            return;

        if (percent < 1)
            percent = 1;
        else if (percent > 100)
            percent = 100;

        age(duration * (percent / 100));
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
    public List<LivingEntity> getEntitiesInsideSpellRadius() {
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

        for (Player player : p.getServer().getOnlinePlayers())
        {
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
     * Get the duration remaining for this spell.
     *
     * @return the duration remaining for this spell.
     */
    public int getDurationRemaining() {
        return duration;
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
     * This is the stationary spell's upkeep, age() must be called in this if you want the spell to age and die eventually.
     */
    abstract void upkeep();

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
     * Handle players moving
     *
     * @param event the event
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
     * Handle player chat
     *
     * @param event the event
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
     * Clean up needed for this spell when it ends.
     */
    abstract void doCleanUp();
}