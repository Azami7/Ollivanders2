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
 * Partial immobilization effect that prevents player movement but allows rotation.
 *
 * <p>IMMOBILIZE prevents the affected player from moving or interacting with the environment while
 * still allowing them to look around (change pitch and yaw). The effect cancels all movement-related
 * events (location changes, velocity changes, flight toggling, sprinting, sneaking) as well as player
 * interaction events. Teleport and apparate attempts are evaluated based on distance: long-distance
 * teleports (> 100 blocks) remove this effect, allowing magical escape, while short-distance attempts
 * are blocked to prevent trivial escape.</p>
 *
 * <p>Mechanism:</p>
 * <ul>
 * <li>Location changes are cancelled but rotation (pitch/yaw) changes are allowed</li>
 * <li>Velocity changes are cancelled</li>
 * <li>Flight toggling is cancelled</li>
 * <li>Sneak toggling is cancelled</li>
 * <li>Sprint toggling is cancelled</li>
 * <li>Interaction events (block breaking, placing, etc.) are cancelled</li>
 * <li>Long-distance teleport/apparate (> 100 blocks) removes the effect</li>
 * <li>Short-distance teleport/apparate (≤ 100 blocks) is cancelled</li>
 * <li>Detectable by mind-reading spells (Legilimens)</li>
 * <li>Detection text: "is unable to move"</li>
 * </ul>
 *
 * @author Azami7
 */
public class IMMOBILIZE extends O2Effect {
    boolean allowRotation = true;

    /**
     * Constructor for creating a complete immobilization effect.
     *
     * <p>Creates an effect that completely paralyzes the target player, preventing all movement
     * and interaction. Sets the detection text for mind-reading spells to "is unable to move".</p>
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

    /**
     * Age the immobilize effect each game tick.
     *
     * <p>Called each game tick. This effect tracks its remaining duration. All movement prevention
     * is handled through event cancellation in the various event handler methods. When the duration
     * reaches zero, the effect is automatically killed and removed from the player.</p>
     */
    @Override
    public void checkEffect() {
        age(1);
    }

    /**
     * Perform cleanup when the immobilization effect is removed.
     *
     * <p>The default implementation does nothing, as the immobilization effect has no persistent
     * state to clean up. When removed, the player regains normal control over movement and
     * interaction.</p>
     */
    @Override
    public void doRemove() {
    }

    /**
     * Prevent player interaction (block breaking, placing, etc.) while immobilized.
     *
     * <p>Cancels all player interact events to ensure the immobilized player cannot interact
     * with the environment.</p>
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
     * <p>Cancels flight toggle events to ensure the immobilized player cannot enable or disable
     * flight.</p>
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
     * <p>Cancels sneak toggle events to ensure the immobilized player cannot change their sneak
     * state.</p>
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
     * <p>Cancels sprint toggle events to ensure the immobilized player cannot activate or
     * deactivate sprinting.</p>
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
     * <p>Cancels velocity events to ensure the immobilized player cannot be moved by any
     * velocity-changing mechanism.</p>
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
     * Prevent the player from moving location while immobilized.
     *
     * <p>Cancels player move events when location coordinates change (x, y, z). Rotation-only changes
     * (pitch and yaw) are allowed unless allowRotation is false. This allows immobilized players to look
     * around but prevents them from walking, jumping, or otherwise changing their position.</p>
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
     * Handle teleportation attempts based on distance threshold.
     *
     * <p>When an immobilized player attempts to teleport via Bukkit's teleport system, the distance of
     * the teleport is checked. If the distance is greater than 100 blocks, the effect is automatically
     * removed, allowing long-distance magical escape from immobilization. For shorter distances (≤ 100
     * blocks), the event is cancelled to prevent trivial escape attempts.</p>
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
     * Handle coordinate-based apparition attempts based on distance threshold.
     *
     * <p>When an immobilized player attempts to use the APPARATE spell with specific coordinates, the
     * distance of the apparition is checked. If the distance is greater than 100 blocks, the effect is
     * automatically removed, allowing long-distance magical escape from immobilization. For shorter
     * distances (≤ 100 blocks), the event is cancelled to prevent trivial escape attempts.</p>
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
     * Handle name-based apparition attempts based on distance threshold.
     *
     * <p>When an immobilized player attempts to use the APPARATE spell with a player name destination,
     * the distance of the apparition is checked. If the distance is greater than 100 blocks, the effect
     * is automatically removed, allowing long-distance magical escape from immobilization. For shorter
     * distances (≤ 100 blocks), the event is cancelled to prevent trivial escape attempts.</p>
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
