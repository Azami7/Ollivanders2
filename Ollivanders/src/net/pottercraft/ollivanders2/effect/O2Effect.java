package net.pottercraft.ollivanders2.effect;

import java.util.ArrayList;
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
 * An effect is either a temporary or semi-permanent alteration of an O2Player.
 *
 * <p>O2EffectType cannot permanently change the O2Player but some effects may remain until a specific removal action/effect is taken.</p>
 */
public abstract class O2Effect {
    /**
     * The type this effect is. Set to babbling as a safe default.
     */
    public O2EffectType effectType = O2EffectType.BABBLING;

    /**
     * The number of game ticks this effect lasts. 24000 ticks is one MC day and should be ~20 real minutes.
     */
    public int duration;

    /**
     * The minimum duration for this effect
     */
    static public final int minDuration = 5 * Ollivanders2Common.ticksPerSecond;

    /**
     * A callback to the MC plugin
     */
    final protected Ollivanders2 p;

    /**
     * Whether this effect should be killed next upkeep.
     */
    protected boolean kill;

    /**
     * Whether this effect is permanent. Permanent effects do not age and can only be removed by explicitly killing them.
     */
    protected boolean permanent = false;

    /**
     * The id of the player this effect affects.
     */
    protected UUID targetID;

    /**
     * The output to be shown for information spells like Informous, if this effect can be detected in this way. This string is the predicate of
     * a sentence that starts with the player's name and should not include ending punctuation. Example: "feels unnaturally tired"
     */
    protected String informousText;

    /**
     * The output to be shown for mind-reading spells like Legilimens, if this effect can be detected in this way. This string is the predicate of
     * a sentence that starts with the player's name and should not include ending punctuation. Example: "feels aggressive"
     */
    protected String legilimensText;

    /**
     * The text to be shown to a player when they become affected by the effect. If not set, no message is sent.
     */
    protected String affectedPlayerText;

    /**
     * Common functions
     */
    Ollivanders2Common common;

    /**
     * Constructor. If you change this method signature, be sure to update all reflection code that uses it.
     *
     * @param plugin          a callback to the MC plugin
     * @param durationInTicks the length this effect should remain
     * @param pid             the player this effect acts on
     */
    public O2Effect(@NotNull Ollivanders2 plugin, int durationInTicks, @NotNull UUID pid) {
        p = plugin;
        common = new Ollivanders2Common(p);

        duration = durationInTicks;
        if (duration < 0)
            permanent = true;

        if (!permanent && duration < minDuration)
            duration = minDuration;

        kill = false;
        targetID = pid;

        informousText = null;
    }

    /**
     * Ages the effect.
     *
     * @param i the amount to age this effect
     */
    public void age(int i) {
        if (permanent)
            return;

        duration = duration - i;
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
     * Get the id of the player affected by this effect.
     *
     * @return the id of the player
     */
    @NotNull
    public UUID getTargetID() {
        return new UUID(targetID.getMostSignificantBits(), targetID.getLeastSignificantBits());
    }

    /**
     * This is the effect's action. age() must be called in this if you want the effect to age and die eventually.
     */
    abstract public void checkEffect();

    /**
     * Do any cleanup related to removing this effect from the player
     */
    abstract public void doRemove();

    /**
     * Is this effect permanent.
     *
     * @return true if the effect is permanent, false if it is not
     */
    public boolean isPermanent() {
        return permanent;
    }

    /**
     * Has this effect been killed.
     *
     * @return true if it is killed, false otherwise
     */
    public boolean isKilled() {
        return kill;
    }

    /**
     * Get the text to be shown to a player when they become affected by this effect.
     *
     * @return the message to show or null if there isn't one.
     */
    @Nullable
    public String getAffectedPlayerText() {
        return affectedPlayerText;
    }

    /**
     * Do any on damage effects
     *
     * @param event the event
     */
    void doOnEntityDamageByEntityEvent(@NotNull EntityDamageByEntityEvent event) {
    }

    /**
     * Do any on player interact effects
     *
     * @param event the event
     */
    void doOnPlayerInteractEvent(@NotNull PlayerInteractEvent event) {
    }

    /**
     * Do any on player chat effects
     *
     * @param event the event
     */
    void doOnAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event) {
    }

    /**
     * Do any effects when player sleeps
     *
     * @param event the event
     */
    void doOnPlayerBedEnterEvent(@NotNull PlayerBedEnterEvent event) {
    }

    /**
     * Do any effects when player toggles flight
     *
     * @param event the event
     */
    void doOnPlayerToggleFlightEvent(@NotNull PlayerToggleFlightEvent event) {
    }

    /**
     * Do any effects when player toggles sneaking
     *
     * @param event the event
     */
    void doOnPlayerToggleSneakEvent(@NotNull PlayerToggleSneakEvent event) {
    }

    /**
     * Do any effects when player toggles sneaking
     *
     * @param event the event
     */
    void doOnPlayerToggleSprintEvent(@NotNull PlayerToggleSprintEvent event) {
    }

    /**
     * Do any effects when player velocity changes
     *
     * @param event the event
     */
    void doOnPlayerVelocityEvent(@NotNull PlayerVelocityEvent event) {
    }

    /**
     * Do any effects when player picks up an item
     *
     * @param event the event
     */
    void doOnPlayerPickupItemEvent(@NotNull EntityPickupItemEvent event) {
    }

    /**
     * Do any effects when player holds an item
     *
     * @param event the event
     */
    void doOnPlayerItemHeldEvent(@NotNull PlayerItemHeldEvent event) {
    }

    /**
     * Do any effects when player consumes an item
     *
     * @param event the event
     */
    void doOnPlayerItemConsumeEvent(@NotNull PlayerItemConsumeEvent event) {
    }

    /**
     * Do any effects when player drops an item
     *
     * @param event the event
     */
    void doOnPlayerDropItemEvent(@NotNull PlayerDropItemEvent event) {
    }

    /**
     * Do any effects when player drops an item
     *
     * @param event the event
     */
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
    }

    /**
     * handle any effects when a spell projectile moves
     *
     * @param event the event
     */
    void doOnOllivandersSpellProjectileMoveEvent(@NotNull OllivandersSpellProjectileMoveEvent event) {
    }

    /**
     * handle on player quit event
     *
     * @param event the event
     */
    void doOnPlayerQuitEvent(@NotNull PlayerQuitEvent event) {
    }

    /**
     * handle entity target event
     *
     * @param event the event
     */
    void doOnEntityTargetEvent(@NotNull EntityTargetEvent event) {
    }

    /**
     * handle projectile launch events
     *
     * @param event the event
     */
    void doOnProjectileLaunchEvent(@NotNull ProjectileLaunchEvent event) {
    }

    /**
     * handle projectile hit events
     *
     * @param event the event
     */
    void doOnProjectileHitEvent(@NotNull ProjectileHitEvent event) {
    }
}