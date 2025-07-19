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
 * Parent class for all concealment shield spells.
 *
 * @author Azami7
 * @since 2.21
 */
public abstract class ConcealmentShieldSpell extends ShieldSpell {
    /**
     * Does this spell alarm to the players inside it on an entity within proximity
     */
    protected boolean alarmOnProximity = false;

    /**
     * The distance away from the edge of this spell that a proximity alarm will trigger
     */
    protected int proximityRadiusModifier = 3;

    /**
     * The proximity alarm cooldown - so that this doesn't go off all the time
     */
    protected int proximityCooldownTimer = 0;

    /**
     * The duration of the proximity alarm cooldown
     */
    protected final int proximityCooldownLimit = Ollivanders2Common.ticksPerMinute;

    /**
     * The messages that a player gets when they try to enter the area and they are not allowed
     */
    public ArrayList<String> messages = new ArrayList<>();

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public ConcealmentShieldSpell(@NotNull Ollivanders2 plugin) {
        super(plugin);
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
    public ConcealmentShieldSpell(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);

        setRadius(radius);
        setDuration(duration);

        hidePlayersInSpellArea();
    }

    /**
     * Upkeep - age the spell, check proximity timer
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
     * Hide the players in the spell area
     */
    protected void hidePlayersInSpellArea() {
        for (Player player : getPlayersInsideSpellRadius()) {
            toggleVisibility(player);
        }
    }

    /**
     * Toggles visibility for players in the spell area
     *
     * @param player the player to hide
     * @see <a href = "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Player.html#hidePlayer(org.bukkit.plugin.Plugin,org.bukkit.entity.Player)">https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Player.html#hidePlayer(org.bukkit.plugin.Plugin,org.bukkit.entity.Player)</a>
     */
    protected void toggleVisibility(@NotNull Player player) {
        // do not do anything if they are not inside the spell area or are otherwise invisible already
        if (!isLocationInside(player.getLocation()) || O2PlayerCommon.isInvisible(player))
            return;

        for (Player viewer : p.getServer().getOnlinePlayers()) {
            // don't hide the player from themselves :)
            if (viewer.getUniqueId() == player.getUniqueId()) {
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
     * Unhide the players in the spell area
     */
    protected void unhidePlayersInSpellArea() {
        for (Player player : getPlayersInsideSpellRadius()) {
            unhidePlayer(player);
        }
    }

    /**
     * Show this player because they are outside the spell area
     *
     * @param player the player to unhide
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
     * The message shown to players if they try to enter this area and cannot
     *
     * @return a message or null if this spell does not have one
     */
    @Nullable
    protected String getAreaEntryDenialMessage() {
        if (messages.isEmpty())
            return null;
        else
            return messages.get(Math.abs(Ollivanders2Common.random.nextInt()) % messages.size());
    }

    /**
     * Do a proximity alarm for this player in the alarm radius
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
     * Is this location in the proximity alarm radius?
     *
     * @param alertLocation the location to check
     * @return true if it is in the proximity alarm area, false otherwise
     */
    protected boolean isInProximity(Location alertLocation) {
        if (!alarmOnProximity)
            return false;

        return Ollivanders2Common.isInside(location, alertLocation, radius + proximityRadiusModifier);
    }

    //
    // Event handlers
    //

    /**
     * Prevent entities targeting a player inside the area.
     *
     * @param event the event
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
     * Hide the player if they go into the spell area
     *
     * @param event the event
     */
    @Override
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
        Location toLocation = event.getTo();
        Location fromLocation = event.getFrom();
        Player player = event.getPlayer();

        if (event.isCancelled() || toLocation == null)
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
     * Handle a player moving. Assumes the move event completed and that we have already checked they can cross the spell boundary
     *
     * @param entity       the player that moved
     * @param toLocation   the location the player moved to
     * @param fromLocation the location the player moved from
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
        // player moving out of the spell area from inside it, unhide them
        else if (!isLocationInside(toLocation) && isLocationInside(fromLocation) && entity instanceof Player) {
            common.printDebugMessage(entity.getName() + " leaving spell area", null, null, false);
            unhidePlayer((Player) entity);
        }
        // entity is moving around outside the spell area and this spell has a proximity alarm
        else if (!isLocationInside(toLocation) && !isLocationInside(fromLocation) && isInProximity(toLocation)) {
            common.printDebugMessage(entity.getName() + " at proximity boundary", null, null, false);
            doProximityCheck(entity);
        }
    }

    /**
     * Remove muggles from the recipients of any chats by players in the spell area
     *
     * @param event the event
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
     * When players join, hide any players in this concealment spell area from them.
     *
     * @param event the player join event
     */
    @Override
    void doOnPlayerJoinEvent(@NotNull PlayerJoinEvent event) {
        // re-evaluate all hidden players in the spell area
        hidePlayersInSpellArea();
    }

    /**
     * Unhide all players that were in the spell area
     */
    @Override
    void doCleanUp() {
        unhidePlayersInSpellArea();
    }

    //
    // serialize/deserialize functions
    //

    /**
     * Serialize all data specific to this spell so it can be saved.
     *
     * @return a map of the serialized data
     */
    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    /**
     * Deserialize the data for this spell and load the data to this spell.
     *
     * @param spellData the serialized spell data
     */
    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }

    //
    // Abstract functions that need to be overridden
    //

    /**
     * Can this entity "hear" sounds from inside the spell area? Assumes the entity being checked is outside the spell area.
     *
     * @param entity the entity entering the area
     * @return true if the entity can hear inside the area, false otherwise
     */
    protected abstract boolean canHear(@NotNull LivingEntity entity);

    /**
     * Can this entity see players inside the spell area? Assumes the entity being checked is outside the spell area.
     *
     * @param entity the entity looking inside the area
     * @return true if the entity can target inside the area, false otherwise
     */
    protected abstract boolean canSee(@NotNull LivingEntity entity);

    /**
     * Can this entity target players inside the spell area? Assumes the entity being checked is outside the spell area.
     *
     * @param entity the entity targeting inside the area
     * @return true if the entity can target inside the area, false otherwise
     */
    protected abstract boolean canTarget(@NotNull LivingEntity entity);

    /**
     * Can this entity enter the spell area? Assumes the entity being checked is outside the spell area.
     *
     * @param entity the entity entering the area
     * @return true if the entity can enter the area, false otherwise
     */
    protected abstract boolean canEnter(@NotNull LivingEntity entity);

    /**
     * Check the proximity alarm conditions at the location. Assumes that a check to determine that a proximity alarm
     * should go off for this location has happened and called this.
     *
     * @param player the player that triggered the alarm
     */
    protected abstract boolean checkAlarm(@NotNull Player player);

    /**
     * Check the proximity alarm conditions at the location. Assumes that a check to determine that a proximity alarm
     * should go off for this location has happened and called this.
     *
     * @param entity the entity that triggered the alarm
     */
    protected abstract boolean checkAlarm(@NotNull LivingEntity entity);

    /**
     * Do the proximity alarm action for this spell.
     */
    protected abstract void proximityAlarm();
}
