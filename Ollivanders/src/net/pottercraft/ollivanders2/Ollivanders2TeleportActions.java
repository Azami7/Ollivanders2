package net.pottercraft.ollivanders2;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A queue of pending player teleport actions, drained by the plugin scheduler each tick. Teleports are queued
 * rather than executed immediately because teleporting a player while an event is being dispatched is unsafe.
 *
 * @author Azami7
 */
public class Ollivanders2TeleportActions {
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
         * @param p         the player teleporting
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
         * get the player to teleport
         *
         * @return the player to teleport
         */
        @NotNull
        public Player getPlayer() {
            return player;
        }

        /**
         * get the location the player is teleporting to
         *
         * @return the destination
         */
        @NotNull
        public Location getToLocation() {
            return toLocation;
        }

        /**
         * get the location the player is teleporting from
         *
         * @return the source
         */
        @NotNull
        public Location getFromLocation() {
            return fromLocation;
        }

        /**
         * does this teleport create an explosion effect on teleport?
         *
         * @return true if it creates an explosion effect, false otherwise
         */
        public boolean isExplosionOnTeleport() {
            return explosionOnTeleport;
        }
    }

    /**
     * Constructor
     */
    public Ollivanders2TeleportActions() {
    }

    /**
     * Get all the pending teleport actions.
     *
     * @return a copy of the pending teleport action list; changes to it do not affect the queue
     */
    @NotNull
    public List<O2TeleportAction> getTeleportActions() {
        return new ArrayList<>(teleportActions);
    }

    /**
     * Add a teleport action to the queue and load the destination chunk so it is ready when the teleport runs.
     *
     * @param player              the player teleporting
     * @param from                the location they are teleporting from
     * @param to                  the location they are teleporting to
     * @param explosionOnTeleport should there be an explosion effect on teleport
     */
    public void addTeleportAction(@NotNull Player player, @NotNull Location from, @NotNull Location to, boolean explosionOnTeleport) {
        O2TeleportAction teleportAction = new O2TeleportAction(player, from, to, explosionOnTeleport);

        Ollivanders2API.common.printDebugMessage("Created teleport action: " + player.getName() + " from " + from + " to " + to, null, null, false);
        teleportActions.add(teleportAction);

        to.getChunk().load();
    }

    /**
     * Remove a teleport action from the queue; does nothing if the action is not queued.
     *
     * @param teleportAction the teleport action to remove
     */
    public void removeTeleportAction(@NotNull Ollivanders2TeleportActions.O2TeleportAction teleportAction) {
        if (teleportActions.remove(teleportAction))
            Ollivanders2API.common.printDebugMessage("Removing teleport action for " + teleportAction.getPlayer().getName(), null, null, false);
        else
            Ollivanders2API.common.printDebugMessage("Unable to remove teleport action, not found.", null, null, false);
    }
}
