package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
 * @since 2.21
 * @author Azami7
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
    protected int proximityCooldownTimer;

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
     */
    public ConcealmentShieldSpell(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location) {
        super(plugin, pid, location);

        hidePlayersInSpellArea();
    }

    /**
     * Upkeep - age the spell
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
            hidePlayer(player);
        }
    }

    /**
     * Hide a player in this spell area from those who cannot see in the spell area.
     *
     * @param player the player to hide
     * @see <a href = "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Player.html#hidePlayer(org.bukkit.plugin.Plugin,org.bukkit.entity.Player)">https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Player.html#hidePlayer(org.bukkit.plugin.Plugin,org.bukkit.entity.Player)</a>
     */
    protected void hidePlayer(@NotNull Player player) {
        // do not do anything if they are not inside the spell area or are otherwise invisible already
        if (!isLocationInside(player.getLocation()) || O2PlayerCommon.isInvisible(player))
            return;

        for (Player viewer : p.getServer().getOnlinePlayers()) {
            // if the viewer cannot see players in this spell area, hide the player from them
            if (!canSee(player)) {
                viewer.hidePlayer(p, player);
                common.printDebugMessage("StationarySpell.ConcealmentShieldSpell: hid " + player.getName() + " from " + viewer.getName(), null, null, false);
            }
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
        // do not do anything if they are inside the spell area or are invisible for other reasons
        if (isLocationInside(player.getLocation()) || O2PlayerCommon.isInvisible(player))
            return;

        for (Player viewer : p.getServer().getOnlinePlayers()) {
            viewer.showPlayer(p, player);
            common.printDebugMessage("StationarySpell.ConcealmentShieldSpell: unhid " + player.getName() + " from " + viewer.getName(), null, null, false);
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
     * Do a proximity alarm if the alertLocation is within the proximity area of this spell
     */
    protected void doProximityCheck(@NotNull Location alertLocation) {
        // do not run the proximity alarm if it is in cooldown
        if (proximityCooldownTimer > 0)
            return;

        if (Ollivanders2Common.isInside(location, alertLocation, radius + proximityRadiusModifier))
            if (checkAlarm(alertLocation)) {
                proximityAlarm();
            }
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

        if (!(target instanceof Player))
            return;

        if (isLocationInside(target.getLocation()) && !isLocationInside(entity.getLocation()) && !canTarget(entity)) {
            event.setCancelled(true);
            common.printDebugMessage("StationarySpell.ConcealmentShieldSpell: canceled target of " + target.getName() + " by " + entity.getName(), null, null, false);
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

        // player moving in to the spell area when they were outside it
        if (isLocationInside(toLocation) && !isLocationInside(fromLocation)) {
            // if they can enter the area, hide them
            if (canEnter(player)) {
                hidePlayer(player);
            }
            // if they cannot enter the area, cancel the move event
            else {
                event.setCancelled(true);

                String areaEntryDenyMessage = getAreaEntryDenialMessage();
                if (areaEntryDenyMessage != null)
                    player.sendMessage(Ollivanders2.chatColor + areaEntryDenyMessage);

                common.printDebugMessage("StationarySpell.ConcealmentShieldSpell: prevented " + player.getName() + " entering area.", null, null, false);
            }
        }
        // player moving out of the spell area from inside it, unhide them
        else if (!isLocationInside(toLocation) && isLocationInside(fromLocation)) {
            unhidePlayer(player);
        }
        // player is moving around outside the spell area
        else if (!isLocationInside(toLocation) && !isLocationInside(fromLocation)) {
            if (alarmOnProximity)
                doProximityCheck(toLocation);
        }
        // else the player is moving around inside the spell area
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

    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

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
    protected abstract boolean canHear(@NotNull Entity entity);

    /**
     * Can this entity see players inside the spell area? Assumes the entity being checked is outside the spell area.
     *
     * @param entity the entity looking inside the area
     * @return true if the entity can target inside the area, false otherwise
     */
    protected abstract boolean canSee(@NotNull Entity entity);

    /**
     * Can this entity target players inside the spell area? Assumes the entity being checked is outside the spell area.
     *
     * @param entity the entity targeting inside the area
     * @return true if the entity can target inside the area, false otherwise
     */
    protected abstract boolean canTarget(@NotNull Entity entity);

    /**
     * Can this entity enter the spell area? Assumes the entity being checked is outside the spell area.
     *
     * @param entity the entity entering the area
     * @return true if the entity can enter the area, false otherwise
     */
    protected abstract boolean canEnter(@NotNull Entity entity);

    /**
     * Check the proximity alarm conditions at the location. Assumes that a check to determine that a proximity alarm
     * should go off for this location has happened and called this.
     */
    protected abstract boolean checkAlarm(@NotNull Location alertLocation);

    /**
     * Do the proximity alarm action for this spell.
     */
    protected abstract void proximityAlarm();
}
