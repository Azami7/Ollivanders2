package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for all magical effects that can be applied to players.
 * <p>
 * An effect is a temporary or permanent magical alteration of a player, managed by the player effects system and
 * ticked while active: {@link #checkEffect()} runs each tick to apply behavior, {@link #age(int)} counts down the
 * duration, and {@link #doRemove()} cleans up when the effect expires or is killed. Subclasses may override the
 * {@code doOn...Event} hooks to react to server events while the effect is active.
 * </p>
 *
 * @author Azami7
 * @see O2EffectType
 */
public abstract class O2Effect {
    /**
     * The type of effect this instance represents; defaults to BABBLING until a subclass sets it.
     */
    public O2EffectType effectType = O2EffectType.BABBLING;

    /**
     * The remaining duration in game ticks, decremented by {@link #age(int)}; the effect is killed once it reaches 0.
     * Permanent effects hold -1 and do not age.
     */
    public int duration;

    /**
     * Reference to the plugin
     */
    final protected Ollivanders2 p;

    /**
     * Whether this effect is marked for removal on the next upkeep cycle.
     */
    protected boolean kill;

    /**
     * Whether this effect is permanent; permanent effects do not age and are removed only when explicitly killed.
     */
    protected boolean permanent;

    /**
     * The unique identifier of the player affected by this effect.
     */
    protected UUID targetID;

    /**
     * Predicate shown by information-detection spells (e.g. Informous), starting after the player's name and without
     * ending punctuation, e.g. "feels unnaturally tired". Null if this effect is undetectable by information spells.
     */
    protected String informousText;

    /**
     * Predicate shown by mind-reading spells (e.g. Legilimens), starting after the player's name and without ending
     * punctuation, e.g. "feels aggressive". Null if this effect is undetectable by mind-reading spells.
     */
    protected String legilimensText;

    /**
     * Chat message sent to the target when the effect is applied; null sends nothing.
     */
    protected String affectedPlayerText;

    /**
     * Common functions
     */
    Ollivanders2Common common;

    /**
     * The affected player; resolved in the constructor, and the effect is killed immediately if they are offline.
     * Child classes use this inherited reference rather than keeping their own.
     */
    Player target;

    /**
     * Constructor
     * <p>
     * If the target player is offline the effect is killed immediately.
     * </p>
     * <p>
     * TODO: keep this signature in sync with the reflection that instantiates effects (e.g. O2Prophecy.fulfill()).
     * </p>
     *
     * @param plugin          reference to the plugin
     * @param durationInTicks the duration in game ticks; negative values create a permanent effect
     * @param isPermanent     whether this effect is permanent (does not age)
     * @param pid             the unique ID of the target player
     */
    public O2Effect(@NotNull Ollivanders2 plugin, int durationInTicks, boolean isPermanent, @NotNull UUID pid) {
        p = plugin;
        common = new Ollivanders2Common(p);

        duration = durationInTicks;
        permanent = isPermanent;

        kill = false;
        targetID = pid;

        informousText = null;

        target = p.getServer().getPlayer(targetID);
        if (target == null)
            kill();
    }

    /**
     * Check and adjust duration to the min/max bounds for this effect type.
     */
    void checkDurationBounds() {
        if (duration < effectType.getMinDuration())
            duration = effectType.getMinDuration();
        else if (duration > effectType.getMaxDuration())
            duration = effectType.getMaxDuration();
    }

    /**
     * Get the minimum duration, in ticks, for this effect type
     *
     * @return the minimum duration
     */
    public int getMinDuration() {
        return effectType.getMinDuration();
    }

    /**
     * Get the maximum duration, in ticks, for this effect type
     *
     * @return the maximum duration
     */
    public int getMaxDuration() {
        return effectType.getMaxDuration();
    }

    /**
     * Get the ticks remaining for this effect
     *
     * @return duration remaining for this effect
     */
    public int getRemainingDuration() {
        return duration;
    }

    /**
     * Get the legiliments text for this effect
     *
     * @return the legilimens text
     */
    public String getLegilimensText() {
        return legilimensText;
    }

    /**
     * Get the informous text for this effect
     *
     * @return the informous text
     */
    public String getInformousText() {
        return informousText;
    }

    /**
     * Decrement this effect's duration, killing it if the duration runs out. Permanent effects are left unchanged.
     *
     * @param i the number of ticks to subtract
     */
    public void age(int i) {
        if (permanent)
            return;

        duration = duration - i;
        if (duration <= 0)
            kill();
    }

    /**
     * Set whether this effect is permanent. Making it permanent sets its duration to -1 so it no longer ages.
     *
     * @param perm true if this is permanent, false otherwise
     */
    public void setPermanent(boolean perm) {
        permanent = perm;

        if (permanent)
            duration = -1;
    }

    /**
     * This kills the effect.
     */
    public void kill() {
        kill = true;
    }

    /**
     * Get the unique identifier of the player affected by this effect.
     *
     * @return a copy of the target player's unique ID
     */
    @NotNull
    public UUID getTargetID() {
        return new UUID(targetID.getMostSignificantBits(), targetID.getLeastSignificantBits());
    }

    /**
     * Apply this effect's behavior for the current game tick. Called once per tick while the effect is active;
     * implementations must call {@link #age(int)} so the effect eventually expires, and should keep the work short as
     * this runs on the main server thread.
     */
    abstract public void checkEffect();

    /**
     * Clean up when this effect is removed (on expiration, kill, or logout), undoing any lasting changes it made such
     * as potion effects or restricted abilities.
     */
    abstract public void doRemove();

    /**
     * Check whether this effect is permanent.
     *
     * <p>Permanent effects have a duration of -1 and are not aged by the age() method.
     * They persist until explicitly killed or removed by doRemove().</p>
     *
     * @return true if the effect is permanent, false if it will eventually expire
     */
    public boolean isPermanent() {
        return permanent;
    }

    /**
     * Check whether this effect has been marked for removal.
     *
     * <p>Once killed, the effect will be removed from the player's effect list on the next upkeep cycle.</p>
     *
     * @return true if the effect has been killed, false otherwise
     */
    public boolean isKilled() {
        return kill;
    }

    /**
     * Get the message shown to the target when this effect is applied.
     *
     * @return the message, or null if none should be shown
     */
    @Nullable
    public String getAffectedPlayerText() {
        return affectedPlayerText;
    }

    /**
     * Hook called on {@link EntityDamageByEntityEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnEntityDamageByEntityEvent(@NotNull EntityDamageByEntityEvent event) {
    }

    /**
     * Hook called on {@link PlayerInteractEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnPlayerInteractEvent(@NotNull PlayerInteractEvent event) {
    }

    /**
     * Hook called on {@link AsyncPlayerChatEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event) {
    }

    /**
     * Hook called on {@link PlayerBedEnterEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnPlayerBedEnterEvent(@NotNull PlayerBedEnterEvent event) {
    }

    /**
     * Hook called on {@link PlayerToggleFlightEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnPlayerToggleFlightEvent(@NotNull PlayerToggleFlightEvent event) {
    }

    /**
     * Hook called on {@link PlayerToggleSneakEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnPlayerToggleSneakEvent(@NotNull PlayerToggleSneakEvent event) {
    }

    /**
     * Hook called on {@link PlayerToggleSprintEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnPlayerToggleSprintEvent(@NotNull PlayerToggleSprintEvent event) {
    }

    /**
     * Hook called on {@link PlayerVelocityEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnPlayerVelocityEvent(@NotNull PlayerVelocityEvent event) {
    }

    /**
     * Hook called on {@link EntityPickupItemEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnPlayerPickupItemEvent(@NotNull EntityPickupItemEvent event) {
    }

    /**
     * Hook called on {@link PlayerItemHeldEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnPlayerItemHeldEvent(@NotNull PlayerItemHeldEvent event) {
    }

    /**
     * Hook called on {@link PlayerItemConsumeEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnPlayerItemConsumeEvent(@NotNull PlayerItemConsumeEvent event) {
    }

    /**
     * Hook called on {@link PlayerDropItemEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnPlayerDropItemEvent(@NotNull PlayerDropItemEvent event) {
    }

    /**
     * Hook called on {@link PlayerMoveEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
    }

    /**
     * Hook called on {@link OllivandersSpellProjectileMoveEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnOllivandersSpellProjectileMoveEvent(@NotNull OllivandersSpellProjectileMoveEvent event) {
    }

    /**
     * Hook called on {@link PlayerQuitEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnPlayerQuitEvent(@NotNull PlayerQuitEvent event) {
    }

    /**
     * Hook called on {@link PlayerJoinEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnPlayerJoinEvent(@NotNull PlayerJoinEvent event) {
    }

    /**
     * Hook called on {@link EntityDeathEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnEntityDeathEvent(@NotNull EntityDeathEvent event) {
    }

    /**
     * Hook called on {@link EntityDamageEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnEntityDamageEvent(@NotNull EntityDamageEvent event) {
    }

    /**
     * Hook called on {@link EntityTargetEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnEntityTargetEvent(@NotNull EntityTargetEvent event) {
    }

    /**
     * Hook called on {@link ProjectileLaunchEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnProjectileLaunchEvent(@NotNull ProjectileLaunchEvent event) {
    }

    /**
     * Hook called on {@link ProjectileHitEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnProjectileHitEvent(@NotNull ProjectileHitEvent event) {
    }

    /**
     * Hook called on {@link PlayerTeleportEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnPlayerTeleportEvent(@NotNull PlayerTeleportEvent event) {
    }

    /**
     * Hook called on {@link OllivandersApparateByCoordinatesEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnOllivandersApparateByCoordinatesEvent(@NotNull OllivandersApparateByCoordinatesEvent event) {
    }

    /**
     * Hook called on {@link OllivandersApparateByNameEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnOllivandersApparateByNameEvent(@NotNull OllivandersApparateByNameEvent event) {
    }

    /**
     * Hook called on {@link EntityDismountEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnEntityDismountEvent(@NotNull EntityDismountEvent event) {
    }

    /**
     * Hook called on {@link VehicleExitEvent} while this effect is active; overridden by effects that react to it.
     */
    void doOnVehicleExitEvent(@NotNull VehicleExitEvent event) {
    }
}