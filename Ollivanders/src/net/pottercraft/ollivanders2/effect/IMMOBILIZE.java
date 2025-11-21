package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Complete immobilization effect that prevents all player movement and actions.
 *
 * <p>IMMOBILIZE completely paralyzes the affected player by preventing all forms of movement
 * and interaction. The effect cancels all movement-related events (movement, velocity changes,
 * flight toggling, sprinting, sneaking) as well as player interaction events to ensure the
 * player cannot perform any action. The player is effectively frozen in place for the duration
 * of the effect.</p>
 *
 * <p>Mechanism:</p>
 * <ul>
 * <li>Player movement events are cancelled</li>
 * <li>Velocity changes are cancelled</li>
 * <li>Flight toggling is cancelled</li>
 * <li>Sneak toggling is cancelled</li>
 * <li>Sprint toggling is cancelled</li>
 * <li>Interaction events (block breaking, placing, etc.) are cancelled</li>
 * <li>Detectable by mind-reading spells (Legilimens)</li>
 * <li>Detection text: "is unable to move"</li>
 * </ul>
 *
 * @author Azami7
 */
public class IMMOBILIZE extends O2Effect {
    /**
     * Constructor for creating a complete immobilization effect.
     *
     * <p>Creates an effect that completely paralyzes the target player, preventing all movement
     * and interaction. Sets the detection text for mind-reading spells to "is unable to move".</p>
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the immobilization effect in game ticks
     * @param pid      the unique ID of the player to immobilize
     */
    public IMMOBILIZE(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.IMMOBILIZE;
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
        event.setCancelled(true);
        common.printDebugMessage("IMMBOLIZE: cancelling PlayerVelocityEvent", null, null, false);
    }

    /**
     * Prevent the player from moving while immobilized.
     *
     * <p>Cancels player move events to ensure the immobilized player cannot change their location.</p>
     *
     * @param event the player move event to cancel
     */
    @Override
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
        event.setCancelled(true);
        common.printDebugMessage("IMMBOLIZE: cancelling PlayerMoveEvent", null, null, false);
    }
}
