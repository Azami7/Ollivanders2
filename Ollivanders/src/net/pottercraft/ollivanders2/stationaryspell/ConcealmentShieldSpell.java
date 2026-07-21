package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Abstract base class for concealment shield spells that hide and restrict access to protected areas.
 *
 * <p>Concealment shield spells create protective barriers that:</p>
 * <ul>
 *   <li>Hide players inside the area from those outside (via visibility toggle)</li>
 *   <li>Prevent certain entities from entering the protected area</li>
 *   <li>Block entities outside from targeting players inside</li>
 *   <li>Prevent chat from those outside reaching those inside</li>
 *   <li>Optionally trigger proximity alarms when hostile entities approach</li>
 * </ul>
 *
 * <p>Subclasses customize behavior through abstract methods: {@link #canEnter(LivingEntity)},
 * {@link #canSee(LivingEntity)}, {@link #canHear(LivingEntity)}, {@link #canTarget(LivingEntity)},
 * and {@link #checkAlarm(LivingEntity)}.</p>
 *
 * @author Azami7
 */
public abstract class ConcealmentShieldSpell extends ShieldSpell {
    /**
     * Whether this spell triggers proximity alarms when hostile entities approach.
     */
    protected boolean alarmOnProximity = false;

    /**
     * The distance (in blocks) beyond the spell radius that triggers proximity alarms.
     * Extends the effective alarm radius outward from the spell's boundary.
     */
    protected int proximityRadiusModifier = 3;

    /**
     * Current cooldown counter for proximity alarms. Prevents the alarm from triggering repeatedly.
     * Decremented each tick when active, prevents new alarms when greater than zero.
     */
    protected int proximityCooldownTimer = 0;

    /**
     * The fixed duration (in ticks) of the proximity alarm cooldown (1 minute).
     * Reset when a proximity alarm is triggered.
     */
    protected final int proximityCooldownLimit = Ollivanders2Common.ticksPerMinute;

    /**
     * Messages displayed to players who attempt to enter but are denied access.
     * One message is chosen randomly from this list each time entry is denied.
     */
    protected ArrayList<String> entryDenyMessages = new ArrayList<>();

    /**
     * Constructor for loading a saved spell from disk; do not use to cast a new spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public ConcealmentShieldSpell(@NotNull Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * Constructor for casting a new concealment shield spell. Immediately hides the players already inside the area.
     *
     * @param plugin   a callback to the MC plugin
     * @param pid      the UUID of the player who cast the spell
     * @param location the center location of the spell
     * @param radius   the initial radius for this spell
     * @param duration the initial duration for this spell in ticks
     */
    public ConcealmentShieldSpell(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);

        setRadius(radius);
        setDuration(duration, false);

        hidePlayersInSpellArea();
    }

    /**
     * Age the spell by one tick and count down the proximity-alarm cooldown.
     */
    @Override
    public void upkeep() {
        age();

        if (proximityCooldownTimer > 0)
            proximityCooldownTimer = proximityCooldownTimer - 1;

        if (duration <= 1)
            kill();
    }

    /**
     * Get the messages shown to entities denied entry to this spell's area.
     *
     * @return a copy of the entry-deny message list; empty if none are configured
     */
    public ArrayList<String> getEntryDenyMessages() {
        return new ArrayList<>(entryDenyMessages);
    }

    /**
     * Re-evaluate visibility for every player currently inside the spell area, hiding or showing them per
     * {@link #canSee(LivingEntity)}.
     */
    protected void hidePlayersInSpellArea() {
        for (Player player : getPlayersInsideSpellRadius()) {
            common.printDebugMessage("StationarySpell.hidePlayersInSpellArea: hiding " + player.getName(), null, null, false);
            toggleVisibility(player);
        }
    }

    /**
     * Hide the given player from every online viewer who cannot see into the spell area. No-op if the player is not
     * inside the area or is already invisible from another effect; the player is never hidden from themselves.
     *
     * @param player the player whose visibility to toggle
     */
    protected void toggleVisibility(@NotNull Player player) {
        if (!isLocationInside(player.getLocation()) || O2PlayerCommon.isInvisible(player))
            return;

        for (Player viewer : p.getServer().getOnlinePlayers()) {
            if (viewer.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }

            if (!canSee(viewer)) {
                viewer.hidePlayer(p, player);
            }
            else if (!player.isInvisible())
                viewer.showPlayer(p, player);
        }
    }

    /**
     * Show every player inside the spell area to all online players, removing this spell's concealment.
     */
    protected void unhidePlayersInSpellArea() {
        for (Player player : getPlayersInsideSpellRadius()) {
            unhidePlayer(player);
        }
    }

    /**
     * Show a player to all online players. No-op if the player is invisible from another effect, so this spell does
     * not reveal players concealed by other means.
     *
     * @param player the player to show
     */
    protected void unhidePlayer(@NotNull Player player) {
        if (O2PlayerCommon.isInvisible(player))
            return;

        for (Player viewer : p.getServer().getOnlinePlayers()) {
            viewer.showPlayer(p, player);
        }
    }

    /**
     * Get a random message to show a player denied entry to the spell area.
     *
     * @return a random configured entry-deny message, or null if none are configured
     */
    @Nullable
    protected String getAreaEntryDenialMessage() {
        if (entryDenyMessages == null || entryDenyMessages.isEmpty())
            return null;
        else
            return entryDenyMessages.get(Math.abs(Ollivanders2Common.random.nextInt()) % entryDenyMessages.size());
    }

    /**
     * Trigger the proximity alarm if the entity meets this spell's alarm conditions. No-op while the alarm cooldown
     * is active.
     *
     * @param entity the living entity that may trigger the alarm
     */
    protected void doProximityCheck(@NotNull LivingEntity entity) {
        if (proximityCooldownTimer > 0)
            return;

        if (entity instanceof Player) {
            if (checkAlarm((Player) entity)) {
                proximityAlarm();
            }
        }
        else {
            if (checkAlarm(entity)) {
                proximityAlarm();
            }
        }
    }

    /**
     * Check whether a location is within this spell's proximity-alarm radius.
     *
     * @param alertLocation the location to check
     * @return true if the location is within the proximity radius, false otherwise
     */
    public boolean isInProximity(Location alertLocation) {
        return Ollivanders2Common.isInside(location, alertLocation, getProximityRadius());
    }

    /**
     * Get the proximity-alarm radius: the spell radius extended by {@link #proximityRadiusModifier} blocks.
     *
     * @return the proximity radius
     */
    public int getProximityRadius() {
        return radius + proximityRadiusModifier;
    }

    /**
     * Check whether this spell triggers a proximity alarm.
     *
     * @return true if it alarms on proximity, false otherwise
     */
    public boolean doesAlarmOnProximty() {
        return alarmOnProximity;
    }

    //
    // Event handlers
    //

    /**
     * Cancel an outside entity's attempt to target a living entity inside the area unless {@link #canTarget} allows it.
     *
     * @param event the entity target event
     */
    @Override
    void doOnEntityTargetEvent(@NotNull EntityTargetEvent event) {
        Entity target = event.getTarget();
        Entity entity = event.getEntity();

        if (!(target instanceof LivingEntity) || !(entity instanceof LivingEntity))
            return;

        // only guard the case of an outside attacker targeting something inside the area
        if (isLocationInside(target.getLocation()) && !isLocationInside(entity.getLocation())) {
            if (!canTarget((LivingEntity) entity)) {
                event.setCancelled(true);
                common.printDebugMessage("StationarySpell.ConcealmentShieldSpell: canceled target of " + target.getName() + " by " + entity.getName(), null, null, false);
            }
        }
    }

    /**
     * Block a player from crossing into the area when {@link #canEnter} denies them (sending an entry-deny message),
     * otherwise schedule the visibility and proximity update for their move.
     *
     * @param event the player move event
     */
    @Override
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
        Location toLocation = event.getTo();
        Location fromLocation = event.getFrom();
        Player player = event.getPlayer();

        if (event.isCancelled())
            return;

        if (isLocationInside(toLocation) && !isLocationInside(fromLocation) && !canEnter(player)) {
            event.setCancelled(true);

            String areaEntryDenyMessage = getAreaEntryDenialMessage();
            if (areaEntryDenyMessage != null)
                player.sendMessage(Ollivanders2.chatColor + areaEntryDenyMessage);

            common.printDebugMessage("StationarySpell.ConcealmentShieldSpell: prevented " + player.getName() + " entering area.", null, null, false);

            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!event.isCancelled())
                    handleEntityMove(player, toLocation, fromLocation);
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
    }

    /**
     * Update concealment for a completed move: re-hide the area on entry, unhide a departing player and re-hide the
     * rest on exit, or run a proximity check when the entity moves near the boundary from outside.
     *
     * @param entity       the living entity that moved
     * @param toLocation   the location the entity moved to
     * @param fromLocation the location the entity moved from
     */
    protected void handleEntityMove(@NotNull LivingEntity entity, @NotNull Location toLocation, @NotNull Location fromLocation) {
        // moving in from outside
        if (isLocationInside(toLocation) && !isLocationInside(fromLocation)) {
            if (canEnter(entity)) {
                common.printDebugMessage(entity.getName() + " entering spell area", null, null, false);
                hidePlayersInSpellArea();
            }
        }
        // moving out from inside
        else if (!isLocationInside(toLocation) && isLocationInside(fromLocation) && entity instanceof Player) {
            common.printDebugMessage(entity.getName() + " leaving spell area", null, null, false);
            unhidePlayer((Player) entity);
            hidePlayersInSpellArea();
        }
        // moving around outside, within the proximity boundary
        else if (!isLocationInside(toLocation) && !isLocationInside(fromLocation) && isInProximity(toLocation)) {
            common.printDebugMessage(entity.getName() + " at proximity boundary", null, null, false);
            doProximityCheck(entity);
        }
    }

    /**
     * When a player inside the area speaks, drop any recipients who cannot hear into the area per {@link #canHear},
     * concealing the conversation.
     *
     * @param event the async player chat event
     */
    @Override
    void doOnAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event) {
        Player speaker = event.getPlayer();

        if (!isLocationInside(speaker.getLocation()))
            return;

        Set<Player> recipients = new HashSet<>(event.getRecipients());
        for (Player player : recipients) {
            if (!canHear(player)) {
                event.getRecipients().remove(player);
                common.printDebugMessage("StationarySpell.ConcealmentShieldSpell: removed " + player.getName() + " from chat recipients", null, null, false);
            }
        }
    }

    /**
     * Re-apply concealment when a player joins so the newcomer sees the correct visibility for the area.
     *
     * @param event the player join event
     */
    @Override
    void doOnPlayerJoinEvent(@NotNull PlayerJoinEvent event) {
        hidePlayersInSpellArea();
    }

    /**
     * Unhide every player this spell was concealing when it ends.
     */
    @Override
    void doCleanUp() {
        unhidePlayersInSpellArea();
    }

    //
    // serialize/deserialize functions
    //

    /**
     * Serialize this spell's extra data. The base implementation has none; subclasses override to persist their own
     * configuration.
     *
     * @return a map of serialized spell-specific data; empty in the base implementation
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    /**
     * Restore this spell's extra data. The base implementation has none; subclasses override to read back what they
     * serialized.
     *
     * @param spellData the map of saved spell-specific data
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    //
    // Abstract functions that need to be overridden
    //

    /**
     * Whether an entity outside the area can hear conversations inside it.
     *
     * @param entity the outside entity to check
     * @return true if the entity can hear inside the area, false if sounds are concealed
     */
    protected abstract boolean canHear(@NotNull LivingEntity entity);

    /**
     * Whether an entity outside the area can see players inside it.
     *
     * @param entity the outside entity to check
     * @return true if the entity can see players inside, false if they are hidden
     */
    protected abstract boolean canSee(@NotNull LivingEntity entity);

    /**
     * Whether an entity outside the area can target players inside it.
     *
     * @param entity the outside entity to check
     * @return true if the entity can target players inside, false if targeting is blocked
     */
    public abstract boolean canTarget(@NotNull LivingEntity entity);

    /**
     * Whether an entity may enter the area.
     *
     * @param entity the entity attempting to enter
     * @return true if the entity may enter, false if entry is blocked
     */
    public abstract boolean canEnter(@NotNull LivingEntity entity);

    /**
     * Whether a player near the boundary should trigger the proximity alarm.
     *
     * @param player the player at proximity distance
     * @return true if the alarm should fire, false otherwise
     */
    protected abstract boolean checkAlarm(@NotNull Player player);

    /**
     * Whether a non-player entity near the boundary should trigger the proximity alarm.
     *
     * @param entity the non-player entity at proximity distance
     * @return true if the alarm should fire, false otherwise
     */
    protected abstract boolean checkAlarm(@NotNull LivingEntity entity);

    /**
     * Perform this spell's proximity-alarm action, e.g. alerting the players inside the area.
     */
    protected abstract void proximityAlarm();
}
