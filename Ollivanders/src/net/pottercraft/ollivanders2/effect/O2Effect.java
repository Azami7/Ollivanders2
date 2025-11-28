package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for all magical effects that can be applied to players.
 *
 * <p>An O2Effect represents a temporary or semi-permanent magical alteration of a player's behavior or abilities.
 * Effects are managed by the player effects system and processed each game tick. While O2Effect itself cannot
 * permanently modify a player's core data, some effects may persist indefinitely until explicitly removed or
 * until a specific in-game action cancels them.</p>
 *
 * <p>Effect Lifecycle:</p>
 * <ol>
 * <li>Effect is created with a duration (in game ticks)</li>
 * <li>checkEffect() is called every game tick to apply the effect's behavior</li>
 * <li>age() is called to decrement the duration counter</li>
 * <li>When duration reaches zero (or effect is manually killed), doRemove() is called for cleanup</li>
 * <li>Effect is removed from the player's effect list</li>
 * </ol>
 *
 * <p>Event Integration:
 * Subclasses can override event handler methods (doOnPlayerInteractEvent, doOnPlayerChatEvent, etc.)
 * to respond to specific server events. These methods are called automatically when their corresponding
 * events occur and the effect is active.</p>
 *
 * @author Azami7
 * @see O2EffectType for available effect types and their implementation classes
 */
public abstract class O2Effect {
    /**
     * The type of magical effect this instance represents.
     * Used to identify the specific effect and is set to BABBLING as a safe default.
     */
    public O2EffectType effectType = O2EffectType.BABBLING;

    /**
     * The remaining duration of this effect, measured in game ticks.
     * One game tick = 1/20 of a second. One Minecraft day = 24000 ticks (~20 real minutes).
     * Decremented each tick by age(). When it reaches zero or below, the effect is automatically killed.
     * Permanent effects have a duration of -1 and do not age.
     */
    public int duration;

    /**
     * The minimum duration for non-permanent effects (in game ticks).
     * Currently set to 5 seconds. Effects created with durations shorter than this will be extended
     * to meet the minimum, ensuring effects are not too ephemeral.
     */
    static public int minDuration = 5 * Ollivanders2Common.ticksPerSecond;

    /**
     * Reference to the plugin for accessing configuration, logging, and server API.
     */
    final protected Ollivanders2 p;

    /**
     * Flag indicating this effect has been marked for removal.
     * When true, the effect will be removed from the player's effect list on the next upkeep cycle.
     */
    protected boolean kill;

    /**
     * Flag indicating whether this effect is permanent.
     * Permanent effects do not age (duration stays at -1) and are only removed when explicitly killed or
     * when a specific game action triggers their removal via doRemove().
     */
    protected boolean permanent;

    /**
     * The unique identifier of the player affected by this effect.
     */
    protected UUID targetID;

    /**
     * The text displayed by information detection spells (e.g., Informous) if this effect can be detected.
     * This string is the predicate of a sentence that starts with the player's name and should not include
     * ending punctuation. Example: "feels unnaturally tired" (resulting in "Player feels unnaturally tired").
     * If null, the effect is not detectable by information spells.
     */
    protected String informousText;

    /**
     * The text displayed by mind-reading detection spells (e.g., Legilimens) if this effect can be detected.
     * This string is the predicate of a sentence that starts with the player's name and should not include
     * ending punctuation. Example: "feels aggressive" (resulting in "Player feels aggressive").
     * If null, the effect is not detectable by mind-reading spells.
     */
    protected String legilimensText;

    /**
     * The message shown to a player when they become affected by this effect.
     * This is sent as a chat message to the target player immediately after the effect is applied.
     * If null, no message is sent to the affected player.
     */
    protected String affectedPlayerText;

    /**
     * Common utility functions for the plugin.
     */
    Ollivanders2Common common;

    /**
     * Constructor for creating a new magical effect.
     *
     * <p>Creates an effect with the given duration and target player. If the duration is negative,
     * the effect is automatically set to permanent (duration = -1). Non-permanent effects with
     * durations shorter than minDuration are automatically extended to meet the minimum.</p>
     *
     * <p><strong>IMPORTANT:</strong> If you change this method signature, be sure to update all reflection
     * code that instantiates effects using this constructor (e.g., O2Prophecy.fulfill()).</p>
     *
     * @param plugin          reference to the plugin for API access
     * @param durationInTicks the duration in game ticks. Negative values create permanent effects
     * @param pid             the unique ID of the target player for this effect
     */
    public O2Effect(@NotNull Ollivanders2 plugin, int durationInTicks, boolean isPermanent, @NotNull UUID pid) {
        p = plugin;
        common = new Ollivanders2Common(p);

        duration = durationInTicks;
        permanent = isPermanent;

        if (duration < minDuration)
            duration = minDuration;

        kill = false;
        targetID = pid;

        informousText = null;
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
     * Decrement the effect's duration by the specified amount.
     *
     * <p>Called by the player effects system each game tick to age the effect. Permanent effects
     * are not aged and immediately return. Non-permanent effects have their duration decremented;
     * when duration drops below zero, the effect is automatically killed.</p>
     *
     * @param i the number of ticks to subtract from the effect's remaining duration
     */
    public void age(int i) {
        // Skip aging for permanent effects - they never expire
        if (permanent)
            return;

        duration = duration - i;
        // Auto-kill when duration expires (reaches or goes below zero)
        if (duration < 0)
            kill();
    }

    /**
     * Override default permanent setting for an effect.
     *
     * @param perm true if this is permanent, false otherwise
     */
    public void setPermanent(boolean perm) {
        permanent = perm;

        // Set duration to -1 to mark permanent effects (consistent with negative duration constructor signal)
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
     * <p>Returns a copy of the target UUID to prevent external modification of the original.</p>
     *
     * @return the unique ID of the target player
     */
    @NotNull
    public UUID getTargetID() {
        // Return a copy of the UUID to prevent external code from modifying our internal state
        // UUIDs are immutable, so creating a new instance with the same bits is the safest approach
        return new UUID(targetID.getMostSignificantBits(), targetID.getLeastSignificantBits());
    }

    /**
     * Execute this effect's behavior for the current game tick.
     *
     * <p>This method is called once per game tick for active effects on a player. Subclasses should override
     * this method to implement the effect's specific behavior (e.g., applying potion effects, restricting actions,
     * modifying movement, etc.). Subclasses should call age() in this method to ensure the effect ages and eventually expires.</p>
     *
     * <p><strong>Implementation Note:</strong> This method is called within the main server thread on each tick.
     * Keep execution time short to avoid lag.</p>
     */
    abstract public void checkEffect();

    /**
     * Perform cleanup when this effect is removed from the player.
     *
     * <p>Called when the effect is about to be removed from the player's effect list (either due to expiration,
     * being killed, or a player logout). Subclasses should override this method to undo any permanent changes
     * made by the effect, such as removing potion effects, restoring abilities, or updating the player's state.</p>
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
     * Get the message to display to a player when this effect is applied.
     *
     * <p>This message is sent to the target player as a chat message immediately after the effect is applied.
     * If no message is desired, subclasses can leave affectedPlayerText as null, and no message will be sent.</p>
     *
     * @return the message to show, or null if no message should be displayed
     */
    @Nullable
    public String getAffectedPlayerText() {
        return affectedPlayerText;
    }

    /**
     * Handle effects when an entity is damaged by another entity.
     *
     * <p>Called when an EntityDamageByEntityEvent occurs and this effect is active.
     * Subclasses can override to prevent damage, modify damage amounts, or trigger additional effects.</p>
     *
     * @param event the damage event
     */
    void doOnEntityDamageByEntityEvent(@NotNull EntityDamageByEntityEvent event) {
    }

    /**
     * Handle effects when the player interacts with a block or entity.
     *
     * <p>Called when a PlayerInteractEvent occurs and this effect is active.
     * Subclasses can override to prevent interactions or trigger action-based effects.</p>
     *
     * @param event the interact event
     */
    void doOnPlayerInteractEvent(@NotNull PlayerInteractEvent event) {
    }

    /**
     * Handle effects when the player sends a chat message.
     *
     * <p>Called when an AsyncPlayerChatEvent occurs and this effect is active.
     * Subclasses can override to prevent chat, modify messages, or trigger chat-based effects.</p>
     *
     * @param event the chat event
     */
    void doOnAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event) {
    }

    /**
     * Handle effects when the player attempts to sleep in a bed.
     *
     * <p>Called when a PlayerBedEnterEvent occurs and this effect is active.
     * Subclasses can override to prevent sleeping or trigger sleep-related effects.</p>
     *
     * @param event the bed enter event
     */
    void doOnPlayerBedEnterEvent(@NotNull PlayerBedEnterEvent event) {
    }

    /**
     * Handle effects when the player toggles creative flight.
     *
     * <p>Called when a PlayerToggleFlightEvent occurs and this effect is active.
     * Subclasses can override to prevent flight or apply effects related to flying.</p>
     *
     * @param event the toggle flight event
     */
    void doOnPlayerToggleFlightEvent(@NotNull PlayerToggleFlightEvent event) {
    }

    /**
     * Handle effects when the player toggles sneaking.
     *
     * <p>Called when a PlayerToggleSneakEvent occurs and this effect is active.
     * Subclasses can override to prevent sneaking or trigger sneaking-related effects.</p>
     *
     * @param event the toggle sneak event
     */
    void doOnPlayerToggleSneakEvent(@NotNull PlayerToggleSneakEvent event) {
    }

    /**
     * Handle effects when the player toggles sprinting.
     *
     * <p>Called when a PlayerToggleSprintEvent occurs and this effect is active.
     * Subclasses can override to prevent sprinting or trigger sprint-related effects.</p>
     *
     * @param event the toggle sprint event
     */
    void doOnPlayerToggleSprintEvent(@NotNull PlayerToggleSprintEvent event) {
    }

    /**
     * Handle effects when the player's velocity changes.
     *
     * <p>Called when a PlayerVelocityEvent occurs and this effect is active.
     * Subclasses can override to modify velocity or prevent movement effects.</p>
     *
     * @param event the velocity event
     */
    void doOnPlayerVelocityEvent(@NotNull PlayerVelocityEvent event) {
    }

    /**
     * Handle effects when the player picks up an item.
     *
     * <p>Called when an EntityPickupItemEvent occurs and this effect is active.
     * Subclasses can override to prevent pickup or trigger item-based effects.</p>
     *
     * @param event the pickup item event
     */
    void doOnPlayerPickupItemEvent(@NotNull EntityPickupItemEvent event) {
    }

    /**
     * Handle effects when the player changes which item they are holding.
     *
     * <p>Called when a PlayerItemHeldEvent occurs and this effect is active.
     * Subclasses can override to trigger effects based on item changes.</p>
     *
     * @param event the item held event
     */
    void doOnPlayerItemHeldEvent(@NotNull PlayerItemHeldEvent event) {
    }

    /**
     * Handle effects when the player consumes an item (food, potion, etc.).
     *
     * <p>Called when a PlayerItemConsumeEvent occurs and this effect is active.
     * Subclasses can override to prevent consumption or trigger consumption-based effects.</p>
     *
     * @param event the item consume event
     */
    void doOnPlayerItemConsumeEvent(@NotNull PlayerItemConsumeEvent event) {
    }

    /**
     * Handle effects when the player drops an item.
     *
     * <p>Called when a PlayerDropItemEvent occurs and this effect is active.
     * Subclasses can override to prevent dropping or trigger drop-based effects.</p>
     *
     * @param event the drop item event
     */
    void doOnPlayerDropItemEvent(@NotNull PlayerDropItemEvent event) {
    }

    /**
     * Handle effects when the player moves.
     *
     * <p>Called when a PlayerMoveEvent occurs and this effect is active.
     * Subclasses can override to prevent movement, modify movement, or trigger position-based effects.</p>
     *
     * @param event the move event
     */
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
    }

    /**
     * Handle effects when a spell projectile moves.
     *
     * <p>Called when an OllivandersSpellProjectileMoveEvent occurs and this effect is active.
     * Subclasses can override to interfere with projectile movement or trigger projectile-based effects.</p>
     *
     * @param event the projectile move event
     */
    void doOnOllivandersSpellProjectileMoveEvent(@NotNull OllivandersSpellProjectileMoveEvent event) {
    }

    /**
     * Handle effects when the player quits the server.
     *
     * <p>Called when a PlayerQuitEvent occurs and this effect is active.
     * Subclasses can override to save effect state or trigger logout-related cleanup.</p>
     *
     * @param event the player quit event
     */
    void doOnPlayerQuitEvent(@NotNull PlayerQuitEvent event) {
    }

    /**
     * Handle effects when an entity targets the affected player.
     *
     * <p>Called when an EntityTargetEvent occurs and this effect is active.
     * Subclasses can override to prevent targeting or trigger aggression-related effects.</p>
     *
     * @param event the entity target event
     */
    void doOnEntityTargetEvent(@NotNull EntityTargetEvent event) {
    }

    /**
     * Handle effects when a projectile is launched.
     *
     * <p>Called when a ProjectileLaunchEvent occurs and this effect is active.
     * Subclasses can override to prevent projectile launch or apply projectile-based effects.</p>
     *
     * @param event the projectile launch event
     */
    void doOnProjectileLaunchEvent(@NotNull ProjectileLaunchEvent event) {
    }

    /**
     * Handle effects when a projectile hits a target.
     *
     * <p>Called when a ProjectileHitEvent occurs and this effect is active.
     * Subclasses can override to trigger effects on projectile impact.</p>
     *
     * @param event the projectile hit event
     */
    void doOnProjectileHitEvent(@NotNull ProjectileHitEvent event) {
    }
}