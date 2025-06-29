package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Hides all players within its area.
 *
 * {@link net.pottercraft.ollivanders2.spell.REPELLO_MUGGLETON}
 */
public class REPELLO_MUGGLETON extends ShieldSpell {
    /**
     * min radius for this spell
     */
    public static final int minRadiusConfig = 5;
    /**
     * max radius for this spell
     */
    public static final int maxRadiusConfig = 20;
    /**
     * min duration for this spell
     */
    public static final int minDurationConfig = Ollivanders2Common.ticksPerSecond * 30;
    /**
     * max duration for this spell
     */
    public static final int maxDurationConfig = Ollivanders2Common.ticksPerMinute * 30;

    public ArrayList<String> messages = new ArrayList<>() {{
        add("You just remembered you need to do something someplace else.");
        add("You just recalled an important appointment you need to get to somewhere else.");
        add("Why were you going that way? You want to go a different way.");
        add("You realize you don't actually want to go that way.");
        add("You hear someone behind you calling your name.");
    }};

    /**
     * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
     *
     * @param plugin a callback to the MC plugin
     */
    public REPELLO_MUGGLETON(@NotNull Ollivanders2 plugin) {
        super(plugin);

        spellType = O2StationarySpellType.REPELLO_MUGGLETON;
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
    public REPELLO_MUGGLETON(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, int radius, int duration) {
        super(plugin, pid, location);

        setRadius(radius);
        setDuration(duration);

        spellType = O2StationarySpellType.REPELLO_MUGGLETON;

        hidePlayersInSpellArea();
    }

    /**
     * Hide the players in the spell area
     */
    private void hidePlayersInSpellArea() {
        World world = location.getWorld();
        if (world == null) {
            common.printDebugMessage("StationarySpell.REPELLO_MUGGLETON: world is null", null, null, true);
            return;
        }

        for (Entity player : EntityCommon.getNearbyEntitiesByType(location, radius, EntityType.PLAYER)) {
            if (isLocationInside(player.getLocation())) {
                for (Player viewer : world.getPlayers()) {
                    hidePlayer((Player)player, viewer);
                }
            }
        }
    }

    /**
     * Hide a player inside this stationary spell if the viewer is a muggle
     *
     * @param player the player to hide
     * @param viewer the viewer
     */
    private void hidePlayer (Player player, Player viewer) {
        if (p.getO2Player(viewer).isMuggle()) {
            viewer.hidePlayer(p, player);
            common.printDebugMessage("StationarySpell.REPELLO_MUGGLETON: hid " + player.getName() + " from " + viewer.getName(), null, null, false);
        }
    }

    /**
     * Unhides the player from other players - for use in spell clean up and when players leave the spell area
     *
     * @param player the player to unhide
     * @param viewer the viewer
     */
    private void showPlayer (Player player, Player viewer) {
        viewer.showPlayer(p, player);
        common.printDebugMessage("REPELLO_MUGGLETON: unhid " + player.getName() + " from " + viewer.getName(), null, null, false);
    }

    /**
     * Upkeep - age the spell
     */
    @Override
    public void checkEffect() {
        age();

        if (duration <= 1)
            kill();
    }

    /**
     * Prevent muggle players targeting a player inside the area.
     *
     * @param event the event
     */
    @Override
    void doOnEntityTargetEvent(@NotNull EntityTargetEvent event) {
        Entity target = event.getTarget();
        Entity entity = event.getEntity(); // will never be null

        if (!(target instanceof Player))
            return;

        if (isLocationInside(target.getLocation()) && !isLocationInside(entity.getLocation())) {
            event.setCancelled(true);
            common.printDebugMessage("REPELLO_MUGGLETON: canceled target of " + target.getName() + " by " + entity.getName(),  null, null, false);
        }
    }

    /**
     * Hide the player from muggles if they go into the spell area
     *
     * @see <a href = "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Player.html#hidePlayer(org.bukkit.plugin.Plugin,org.bukkit.entity.Player)">https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Player.html#hidePlayer(org.bukkit.plugin.Plugin,org.bukkit.entity.Player)</a>
     * @param event the event
     */
    @Override
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
        Location toLocation = event.getTo();
        Location fromLocation = event.getFrom();
        Player player = event.getPlayer();

        if (event.isCancelled() || toLocation == null)
            return;

        if (p.getO2Player(player).isMuggle()) {
            // do not let muggles enter the spell area if they are outside of it
            if (isLocationInside(toLocation) && !(isLocationInside(fromLocation))) {
                event.setCancelled(true);
                player.sendMessage(Ollivanders2.chatColor + getMessage());
                common.printDebugMessage("REPELLO_MUGGLETON: prevented " + player.getName() + " entering area.", null, null, false);
                return; // return because they never entered the area so we don't need to do the rest of the steps in this function
            }

            // if they were inside the area and move out, hide the players inside the spell area from them
            else if (!(isLocationInside(toLocation)) && isLocationInside(fromLocation)) {
                for (Entity hiddenPlayer : EntityCommon.getNearbyEntitiesByType(location, radius, EntityType.PLAYER)) {
                    hidePlayer((Player)hiddenPlayer, player);
                }
            }
        }

        // handle toggling visibility for this player with muggles if they cross the spell area boundary
        handleVisibility(toLocation, fromLocation, player);
    }

    /**
     * Get a random message to show muggles when they try to walk in to the spell area.
     *
     * @return one of the messages at random
     */
    private String getMessage() {
        return messages.get(Math.abs(Ollivanders2Common.random.nextInt()) % messages.size());
    }

    /**
     * Handle hiding or unhiding the player from muggles if the player crosses the spell area boundary
     *
     * @param toLocation the location the player is moving to
     * @param fromLocation the location the player is moving from
     * @param player the player moving
     */
    private void handleVisibility(Location toLocation, Location fromLocation, Player player) {
        // if they move from outside the spell area to inside of it, hide them from muggles
        if (isLocationInside(toLocation) && !(isLocationInside(fromLocation))) {
            for (Player viewer : p.getServer().getOnlinePlayers()) {
                hidePlayer(player, viewer);
            }
        }
        // if they move from inside the spell area to outside of it, unhide them from all players
        else if (!(isLocationInside(toLocation)) && isLocationInside(fromLocation)) {
            for (Player viewer : p.getServer().getOnlinePlayers()) {
                showPlayer(player, viewer);
            }
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
            if (p.getO2Player(player).isMuggle()) {
                event.getRecipients().remove(player);
                common.printDebugMessage("REPELLO_MUGGLETON: removed " + player.getName() + " from chat recipients", null, null, false);
            }
        }
    }

    /**
     * Unhide all players that were in the spell area
     */
    @Override
    void doCleanUp() {
        World world = location.getWorld();
        if (world == null) {
            common.printDebugMessage("StationarySpell.REPELLO_MUGGLETON: world is null", null, null, true);
            return;
        }

        for (Player player : world.getPlayers()) {
            if (isLocationInside(player.getLocation())) {
                for (Player viewer : location.getWorld().getPlayers()) {
                    showPlayer(player, viewer);
                }
            }
        }
    }

    /**
     * When players join, hide any players in this repello muggleton from them.
     *
     * @param event the player join event
     */
    @Override
    void doOnPlayerJoinEvent(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!p.getO2Player(player).isMuggle())
            return;

        // hide any players inside this spell area from this player if they are a muggle
        for (Entity hiddenPlayer : EntityCommon.getNearbyEntitiesByType(location, radius, EntityType.PLAYER)) {
            if (hiddenPlayer instanceof Player) {
                hidePlayer((Player) hiddenPlayer, player);
            }
        }
    }

    @Override
    @NotNull
    public Map<String, String> serializeSpellData() {
        return new HashMap<>();
    }

    @Override
    public void deserializeSpellData(@NotNull Map<String, String> spellData) {
    }
}