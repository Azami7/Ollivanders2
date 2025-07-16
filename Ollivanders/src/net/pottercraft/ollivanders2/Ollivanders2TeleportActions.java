package net.pottercraft.ollivanders2;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * With MC 1.14, triggering PlayerTeleportEvents from other events is no longer thread-safe. Need to create a queue of
 * teleport events like we use for things like spell projectiles and effects.
 *
 * @author Azami7
 * @since 2.4
 */
public class Ollivanders2TeleportActions {
    final private Ollivanders2 p;
    final private Ollivanders2Common common;

    /**
     * The list of all queued teleport actions
     */
    final private List<O2TeleportAction> teleportActions = new ArrayList<>();

    /**
     * A teleport action
     */
    static public class O2TeleportAction {
        /**
         * The player to teleport
         */
        final private Player player;

        /**
         * The location they are teleporting from
         */
        final private Location fromLocation;

        /**
         * The location they are teleporting to
         */
        final private Location toLocation;

        /**
         * Create explosion effect on teleport
         */
        private boolean explosionOnTeleport = false;

        /**
         * Constructor
         *
         * @param p    the player teleporting
         * @param from the location they are teleporting from
         * @param to   the location they are teleporting to
         */
        O2TeleportAction(@NotNull Player p, @NotNull Location from, @NotNull Location to) {
            player = p;
            fromLocation = from;
            toLocation = to;
        }

        /**
         * Constructor
         *
         * @param from      the location they are teleporting from
         * @param to        the location they are teleporting to
         * @param explosion should this teleport create an explosion effect when it happens
         */
        O2TeleportAction(@NotNull Player p, @NotNull Location from, @NotNull Location to, boolean explosion) {
            player = p;
            fromLocation = from;
            toLocation = to;

            explosionOnTeleport = explosion;
        }

        /**
         * get the player who teleported
         *
         * @return the player who teleported
         */
        @NotNull
        public Player getPlayer() {
            return player;
        }

        /**
         * get the location the player teleported to
         *
         * @return the destination
         */
        @NotNull
        public Location getToLocation() {
            return toLocation;
        }

        /**
         * get the location the player teleported from
         *
         * @return the source
         */
        @NotNull
        public Location getFromLocation() {
            return fromLocation;
        }

        /**
         * does this teleport do an explosion sound on teleport?
         *
         * @return true if it does an explosion sound, false otherwise
         */
        public boolean isExplosionOnTeleport() {
            return explosionOnTeleport;
        }
    }

    /**
     * Constructor
     *
     * @param plugin a callback to the MC plugin
     */
    public Ollivanders2TeleportActions(@NotNull Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(p);
    }

    /**
     * Get all the teleport events.
     *
     * @return a list of the pending teleport events
     */
    @NotNull
    public List<O2TeleportAction> getTeleportActions() {
        return new ArrayList<>(teleportActions);
    }

    /**
     * Add a teleport action to the list.
     *
     * @param player the player teleporting
     * @param from   the location they are teleporting from
     * @param to     the location they are teleporting to
     */
    public void addTeleportEvent(@NotNull Player player, @NotNull Location from, @NotNull Location to) {
        addTeleportEvent(player, from, to, false);
    }

    /**
     * Add a teleport action to the list.
     *
     * @param player              the player teleporting
     * @param from                the location they are teleporting from
     * @param to                  the location they are teleporting to
     * @param explosionOnTeleport should there be an explosion effect on teleport
     */
    public void addTeleportEvent(@NotNull Player player, @NotNull Location from, @NotNull Location to, boolean explosionOnTeleport) {
        O2TeleportAction teleportEvent = new O2TeleportAction(player, from, to, explosionOnTeleport);

        common.printDebugMessage("Created teleport action: " + player.getName() + " from " + from + " to " + to, null, null, false);
        teleportActions.add(teleportEvent);

        to.getChunk().load();
    }

    /**
     * Remove a teleport action from the list.
     *
     * @param teleportAction the teleport action to remove
     */
    public void removeTeleportEvent(@NotNull Ollivanders2TeleportActions.O2TeleportAction teleportAction) {
        if (teleportActions.contains(teleportAction)) {
            common.printDebugMessage("Removing teleport action for " + teleportAction.getPlayer().getName(), null, null, false);
            teleportActions.remove(teleportAction);
        }
        else {
            common.printDebugMessage("Unable to remove teleport action, not found.", null, null, false);
        }
    }
}
