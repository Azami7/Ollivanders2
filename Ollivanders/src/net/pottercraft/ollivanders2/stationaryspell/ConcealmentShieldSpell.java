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
 * <p>Concealment shield spells create protective barriers that:
 * <ul>
 *   <li>Hide players inside the area from those outside (via visibility toggle)</li>
 *   <li>Prevent certain entities from entering the protected area</li>
 *   <li>Block entities outside from targeting players inside</li>
 *   <li>Prevent chat from those outside reaching those inside</li>
 *   <li>Optionally trigger proximity alarms when hostile entities approach</li>
 * </ul>
 * </p>
 *
 * <p>Subclasses customize behavior through abstract methods: {@link #canEnter(LivingEntity)},
 * {@link #canSee(LivingEntity)}, {@link #canHear(LivingEntity)}, {@link #canTarget(LivingEntity)},
 * and {@link #checkAlarm(LivingEntity)}.</p>
 *
 * @author Azami7
 * @since 2.21
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
     * Constructs a concealment spell from deserialized data at server start.
     *
     * <p>Used only for loading saved spells from disk. Subclasses should not call this
     * for spell casting - use the full constructor instead.</p>
     *
     * @param plugin a callback to the MC plugin
     */
    public ConcealmentShieldSpell(@NotNull Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * Constructs a new concealment shield spell cast by a player.
     *
     * <p>Initializes the spell with the specified properties and immediately hides all players
     * currently in the spell area (based on the {@link #canSee(LivingEntity)} implementation).</p>
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
        setDuration(duration);

        hidePlayersInSpellArea();
    }

    /**
     * Performs per-tick upkeep for this concealment spell.
     *
     * <p>Ages the spell by one tick, decrements the proximity alarm cooldown timer,
     * and kills the spell when duration reaches zero.</p>
     */
    @Override
    public void upkeep() {
        // age the spell
        age();

        // decrement the proximity timer if it is running
        if (proximityCooldownTimer > 0)
            proximityCooldownTimer = proximityCooldownTimer - 1;

        // kill the spell if the age limit is reached
        if (duration <= 1)
            kill();
    }

    /**
     * Get the entry deny messages.
     *
     * @return the entry deny messages
     */
    public ArrayList<String> getEntryDenyMessages() {
        return new ArrayList<>(entryDenyMessages);
    }

    /**
     * Hides or shows players in the spell area based on visibility rules.
     *
     * <p>Iterates through all players in the spell radius and toggles their visibility
     * according to the {@link #toggleVisibility(Player)} logic and {@link #canSee(LivingEntity)} implementation.</p>
     */
    protected void hidePlayersInSpellArea() {
        for (Player player : getPlayersInsideSpellRadius()) {
            common.printDebugMessage("StationarySpell.hidePlayersInSpellArea: hiding " + player.getName(), null, null, false);
            toggleVisibility(player);
        }
    }

    /**
     * Toggles the visibility of a specific player based on concealment rules.
     *
     * <p>For each online player (viewer), hides or shows the target player based on:
     * <ul>
     *   <li>Whether the target is inside the spell area</li>
     *   <li>Whether the viewer can see into the spell area (via {@link #canSee(LivingEntity)})</li>
     *   <li>Whether the target has other invisibility effects</li>
     * </ul>
     * Does not hide the target from themselves.</p>
     *
     * @param player the player whose visibility to toggle
     */
    protected void toggleVisibility(@NotNull Player player) {
        // do not do anything if they are not inside the spell area or are otherwise invisible already
        if (!isLocationInside(player.getLocation()) || O2PlayerCommon.isInvisible(player))
            return;

        for (Player viewer : p.getServer().getOnlinePlayers()) {
            // don't hide the player from themselves :)
            if (viewer.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }

            // if the viewer cannot see players in this spell area, hide the player from them
            if (!canSee(viewer)) {
                viewer.hidePlayer(p, player);
            }
            else if (!player.isInvisible()) // show the player to viewer can see them and they are not otherwise invisible
                viewer.showPlayer(p, player);
        }
    }

    /**
     * Shows all players in the spell area to other online players.
     *
     * <p>Called when the spell ends to remove the concealment effect from all affected players.</p>
     */
    protected void unhidePlayersInSpellArea() {
        for (Player player : getPlayersInsideSpellRadius()) {
            unhidePlayer(player);
        }
    }

    /**
     * Shows a player to all online players (removes concealment).
     *
     * <p>Called when a player leaves the spell area or when the spell ends.
     * Does not unhide players who have other invisibility effects.</p>
     *
     * @param player the player to show
     */
    protected void unhidePlayer(@NotNull Player player) {
        // do not unhide them if they are otherwise invisible
        if (O2PlayerCommon.isInvisible(player))
            return;

        for (Player viewer : p.getServer().getOnlinePlayers()) {
            viewer.showPlayer(p, player);
        }
    }

    /**
     * Gets a random entry denial message to display to players blocked from entering.
     *
     * <p>Selects a random message from the configured {@link #entryDenyMessages} list.
     * Used when a player attempts to enter the spell area but fails the {@link #canEnter(LivingEntity)} check.</p>
     *
     * @return a random message from the configured list, or null if no messages are configured
     */
    @Nullable
    protected String getAreaEntryDenialMessage() {
        if (entryDenyMessages == null || entryDenyMessages.isEmpty())
            return null;
        else
            return entryDenyMessages.get(Math.abs(Ollivanders2Common.random.nextInt()) % entryDenyMessages.size());
    }

    /**
     * Checks and triggers a proximity alarm if conditions are met.
     *
     * <p>Tests whether the entity meets alarm conditions via {@link #checkAlarm(LivingEntity)} or
     * {@link #checkAlarm(Player)} and triggers {@link #proximityAlarm()} if conditions match.
     * Does nothing if the proximity alarm cooldown is active.</p>
     *
     * @param entity the living entity that may trigger the alarm
     */
    protected void doProximityCheck(@NotNull LivingEntity entity) {
        // do not run the proximity alarm if it is in cooldown
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
     * Checks if a location is within the proximity alarm detection radius.
     *
     * <p>The proximity radius extends {@link #proximityRadiusModifier} blocks beyond the spell's
     * boundary.</p>
     *
     * @param alertLocation the location to check
     * @return true if the location is within the proximity alarm radius, false otherwise
     */
    public boolean isInProximity(Location alertLocation) {
        return Ollivanders2Common.isInside(location, alertLocation, getProximityRadius());
    }

    public int getProximityRadius() {
        return radius + proximityRadiusModifier;
    }

    public boolean doesAlarmOnProximty() {
        return alarmOnProximity;
    }

    //
    // Event handlers
    //

    /**
     * Prevents entities outside the spell from targeting players inside.
     *
     * <p>If an entity outside the area targets a player inside, checks {@link #canTarget(LivingEntity)}
     * to determine if the targeting should be allowed. Cancels the event if targeting is not permitted.</p>
     *
     * @param event the entity target event
     */
    @Override
    void doOnEntityTargetEvent(@NotNull EntityTargetEvent event) {
        Entity target = event.getTarget();
        Entity entity = event.getEntity(); // will never be null

        // make sure this event is targeting a living entity inside the spell area
        if (!(target instanceof LivingEntity) || !(entity instanceof LivingEntity))
            return;

        // if the target is inside the area, the entity is outside the area, check if the entity can target in the area
        if (isLocationInside(target.getLocation()) && !isLocationInside(entity.getLocation())) {
            if (!canTarget((LivingEntity) entity)) {
                event.setCancelled(true);
                common.printDebugMessage("StationarySpell.ConcealmentShieldSpell: canceled target of " + target.getName() + " by " + entity.getName(), null, null, false);
            }
        }
    }

    /**
     * Manages player movement across spell boundaries and handles area entry/exit logic.
     *
     * <p>Prevents players from entering the spell area if {@link #canEnter(LivingEntity)} returns false.
     * Asynchronously handles visibility updates when players enter or leave the spell area, and checks
     * for proximity alarms when players move near the boundary.</p>
     *
     * @param event the player move event
     */
    @Override
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
        Location toLocation = event.getTo();
        Location fromLocation = event.getFrom();
        Player player = event.getPlayer();

        // if the event is canceled, skip
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
     * Handles visibility and proximity logic when an entity moves relative to the spell boundary.
     *
     * <p>Called asynchronously after a player move event completes successfully. Handles three cases:
     * <ul>
     *   <li>Entity entering: Re-evaluates visibility for all players in the area if entry is allowed</li>
     *   <li>Entity leaving: Unhides the departing player to all viewers</li>
     *   <li>Proximity check: Triggers proximity alarm if entity moves near the boundary</li>
     * </ul>
     * Assumes the move event completed and entry restrictions have already been checked.</p>
     *
     * @param entity       the living entity that moved
     * @param toLocation   the location the entity moved to
     * @param fromLocation the location the entity moved from
     */
    protected void handleEntityMove(@NotNull LivingEntity entity, @NotNull Location toLocation, @NotNull Location fromLocation) {
        // entity moving in to the spell area when they were outside it
        if (isLocationInside(toLocation) && !isLocationInside(fromLocation)) {
            // if they entered the area, recheck all visibility
            if (canEnter(entity)) {
                common.printDebugMessage(entity.getName() + " entering spell area", null, null, false);
                hidePlayersInSpellArea();
            }
        }
        // player moving out of the spell area from inside it, unhide them, then recheck visibility of players in the spell area
        else if (!isLocationInside(toLocation) && isLocationInside(fromLocation) && entity instanceof Player) {
            common.printDebugMessage(entity.getName() + " leaving spell area", null, null, false);
            unhidePlayer((Player) entity);
            hidePlayersInSpellArea();
        }
        // entity is moving around outside the spell area and this spell has a proximity alarm
        else if (!isLocationInside(toLocation) && !isLocationInside(fromLocation) && isInProximity(toLocation)) {
            common.printDebugMessage(entity.getName() + " at proximity boundary", null, null, false);
            doProximityCheck(entity);
        }
    }

    /**
     * Filters chat recipients to prevent players outside from hearing players inside.
     *
     * <p>If a player inside the spell area chats, removes any recipients who cannot hear inside the area
     * (as determined by {@link #canHear(LivingEntity)}). Allows concealment of conversation within
     * the protected area.</p>
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
     * Re-evaluates visibility for all players in the spell area when a new player joins.
     *
     * <p>Called when a player joins the server to ensure that newly joined players see the correct
     * visibility state for concealed players in the spell area. Uses {@link #hidePlayersInSpellArea()}
     * to update visibility based on current {@link #canSee(LivingEntity)} rules.</p>
     *
     * @param event the player join event
     */
    @Override
    void doOnPlayerJoinEvent(@NotNull PlayerJoinEvent event) {
        // re-evaluate all hidden players in the spell area
        hidePlayersInSpellArea();
    }

    /**
     * Removes all concealment effects when the spell ends.
     *
     * <p>Unhides all players that were previously concealed by this spell, making them visible to all
     * online players again.</p>
     */
    @Override
    void doCleanUp() {
        unhidePlayersInSpellArea();
    }

    //
    // serialize/deserialize functions
    //

    /**
     * Serializes concealment spell data for persistence.
     *
     * <p>This base implementation returns an empty map. Subclasses can override to save
     * spell-specific configuration like proximity alarm settings or custom messages.</p>
     *
     * @return a map of serialized spell-specific data
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    /**
     * Deserializes concealment spell data from storage.
     *
     * <p>This base implementation does nothing. Subclasses can override to restore
     * spell-specific configuration that was previously saved.</p>
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
     * Determines if an entity outside the spell area can hear sounds from inside.
     *
     * <p>Used by {@link #doOnAsyncPlayerChatEvent(AsyncPlayerChatEvent)} to determine if chat
     * from players inside should reach this entity.</p>
     *
     * @param entity the entity outside the spell area to check (not null)
     * @return true if the entity can hear conversations inside the spell area, false if sounds are concealed
     */
    protected abstract boolean canHear(@NotNull LivingEntity entity);

    /**
     * Determines if an entity outside the spell area can see players inside.
     *
     * <p>Used by {@link #toggleVisibility(Player)} to determine whether to hide concealed players
     * from this entity's view.</p>
     *
     * @param entity the entity outside the spell area to check (not null)
     * @return true if the entity can see players inside the spell area, false if players are hidden
     */
    protected abstract boolean canSee(@NotNull LivingEntity entity);

    /**
     * Determines if an entity outside the spell area can target players inside.
     *
     * <p>Used by {@link #doOnEntityTargetEvent(EntityTargetEvent)} to determine if targeting
     * attempts from outside should succeed.</p>
     *
     * @param entity the entity outside the spell area to check (not null)
     * @return true if the entity can target players inside the spell area, false if targeting is blocked
     */
    public abstract boolean canTarget(@NotNull LivingEntity entity);

    /**
     * Determines if an entity can enter the spell area.
     *
     * <p>Used by {@link #doOnPlayerMoveEvent(PlayerMoveEvent)} to determine if a player attempting
     * to cross the spell boundary should be allowed entry.</p>
     *
     * @param entity the entity attempting to enter the spell area (not null)
     * @return true if the entity can enter the spell area, false if entry is blocked
     */
    public abstract boolean canEnter(@NotNull LivingEntity entity);

    /**
     * Checks if a player outside the spell area should trigger a proximity alarm.
     *
     * <p>Called by {@link #doProximityCheck(LivingEntity)} when an entity moves within the
     * proximity alarm radius. Allows subclasses to implement spell-specific alarm conditions.</p>
     *
     * @param player the player outside the spell area at proximity distance (not null)
     * @return true if the alarm conditions are met, false otherwise
     */
    protected abstract boolean checkAlarm(@NotNull Player player);

    /**
     * Checks if a non-player entity outside the spell area should trigger a proximity alarm.
     *
     * <p>Called by {@link #doProximityCheck(LivingEntity)} when an entity moves within the
     * proximity alarm radius. Allows subclasses to implement spell-specific alarm conditions.</p>
     *
     * @param entity the non-player entity outside the spell area at proximity distance (not null)
     * @return true if the alarm conditions are met, false otherwise
     */
    protected abstract boolean checkAlarm(@NotNull LivingEntity entity);

    /**
     * Performs the proximity alarm action for this spell.
     *
     * <p>Called by {@link #doProximityCheck(LivingEntity)} when proximity alarm conditions are met.
     * Typically, sends a message or triggers an effect to alert players inside the spell area.</p>
     */
    protected abstract void proximityAlarm();
}
