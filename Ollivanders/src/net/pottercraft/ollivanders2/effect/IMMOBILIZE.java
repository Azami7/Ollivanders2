package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Prevents the target from moving or interacting with the environment while still allowing them to look around
 * (change pitch and yaw). Teleport or apparate attempts over 100 blocks remove this effect, allowing magical escape;
 * shorter attempts are cancelled. Detectable via Informous and Legilimens.
 *
 * @author Azami7
 */
public class IMMOBILIZE extends O2Effect {
    /**
     * Whether the target may still change pitch and yaw; subclasses set this false to freeze rotation too.
     */
    boolean allowRotation = true;

    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the immobilization effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to immobilize
     */
    public IMMOBILIZE(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.IMMOBILIZE;
        checkDurationBounds();

        informousText = legilimensText = "is unable to move";
    }

    @Override
    public void checkEffect() {
        age(1);
    }

    @Override
    public void doRemove() {
    }

    /**
     * Prevent player interaction (block breaking, placing, etc.) while immobilized.
     *
     * @param event the player interact event to cancel
     */
    @Override
    void doOnPlayerInteractEvent(@NotNull PlayerInteractEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        event.setCancelled(true);
        common.printDebugMessage("IMMOBILIZE: cancelling PlayerInteractEvent", null, null, false);
    }

    /**
     * Prevent the player from toggling flight while immobilized.
     *
     * @param event the player toggle flight event to cancel
     */
    @Override
    void doOnPlayerToggleFlightEvent(@NotNull PlayerToggleFlightEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        event.setCancelled(true);
        common.printDebugMessage("IMMBOLIZE: cancelling PlayerToggleFlightEvent", null, null, false);
    }

    /**
     * Prevent the player from toggling sneak while immobilized.
     *
     * @param event the player toggle sneak event to cancel
     */
    @Override
    void doOnPlayerToggleSneakEvent(@NotNull PlayerToggleSneakEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        event.setCancelled(true);
        common.printDebugMessage("IMMBOLIZE: cancelling PlayerToggleSneakEvent", null, null, false);
    }

    /**
     * Prevent the player from toggling sprint while immobilized.
     *
     * @param event the player toggle sprint event to cancel
     */
    @Override
    void doOnPlayerToggleSprintEvent(@NotNull PlayerToggleSprintEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        event.setCancelled(true);
        common.printDebugMessage("IMMBOLIZE: cancelling PlayerToggleSprintEvent", null, null, false);
    }

    /**
     * Prevent velocity changes to the immobilized player.
     *
     * @param event the player velocity event to cancel
     */
    @Override
    void doOnPlayerVelocityEvent(@NotNull PlayerVelocityEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        event.setCancelled(true);
        common.printDebugMessage("IMMBOLIZE: cancelling PlayerVelocityEvent", null, null, false);
    }

    /**
     * Prevent the player from moving location while immobilized. Rotation-only changes (pitch and yaw) are allowed
     * unless {@link #allowRotation} is false.
     *
     * @param event the player move event to evaluate
     */
    @Override
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        Location fromLocation = event.getFrom();
        Location toLocation = event.getTo();

        boolean cancel = false;

        if (allowRotation) {
            common.printDebugMessage("IMMBOLIZE: allowRotation", null, null, false);
            if ((toLocation.getX() != fromLocation.getX()) || (toLocation.getY() != fromLocation.getY()) || (toLocation.getZ() != fromLocation.getZ())) {
                cancel = true;
            }
            // else they are only changing pitch and yaw
        }
        else {
            cancel = true;
        }

        if (cancel) {
            event.setCancelled(true);
            common.printDebugMessage("IMMBOLIZE: cancelling PlayerMoveEvent", null, null, false);
        }
    }

    /**
     * Handle a teleport attempt: a teleport over 100 blocks removes this effect (magical escape); a shorter one is
     * cancelled.
     *
     * @param event the player teleport event
     */
    @Override
    void doOnPlayerTeleportEvent(@NotNull PlayerTeleportEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        Location from = event.getFrom();
        Location to = event.getTo();

        if (to != null && from.distance(to) > 100) {
            // remove the immobilize effect
            Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, effectType);
        }
        else
            event.setCancelled(true);
    }

    /**
     * Handle a coordinate-based apparition attempt: an apparition over 100 blocks removes this effect (magical
     * escape); a shorter one is cancelled.
     *
     * @param event the apparate by coordinates event
     */
    @Override
    void doOnOllivandersApparateByCoordinatesEvent(@NotNull OllivandersApparateByCoordinatesEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        Location from = event.getPlayer().getLocation();
        Location to = event.getDestination();

        if (from.distance(to) > 100) {
            // remove the immobilize effect
            Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, effectType);
        }
        else
            event.setCancelled(true);
    }

    /**
     * Handle a name-based apparition attempt: an apparition over 100 blocks removes this effect (magical escape); a
     * shorter one is cancelled.
     *
     * @param event the apparate by name event
     */
    @Override
    void doOnOllivandersApparateByNameEvent(@NotNull OllivandersApparateByNameEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        Location from = event.getPlayer().getLocation();
        Location to = event.getDestination();

        if (from.distance(to) > 100) {
            // remove the immobilize effect
            Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, effectType);
        }
        else
            event.setCancelled(true);
    }
}
