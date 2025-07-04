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
 * Prevents a player from moving in any way.
 *
 * @author Azami7
 * @since 2.2.9
 */
public class IMMOBILIZE extends O2Effect {
    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public IMMOBILIZE(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.IMMOBILIZE;
        informousText = legilimensText = "is unable to move";

        divinationText.add("will be possessed by a demon spirit");
        divinationText.add("will succomb to a primal fear");
        divinationText.add("shall become as if frozen");
        divinationText.add("shall be struck by a terrible affliction");
        divinationText.add("shall be cursed");
    }

    /**
     * Age this effect by 1, move the player up 1.5 blocks off the ground if they are not already suspended.
     */
    @Override
    public void checkEffect() {
        age(1);
    }

    /**
     * Do any cleanup related to removing this effect from the player
     */
    @Override
    public void doRemove() {
    }

    /**
     * Do any on player interact effects
     */
    @Override
    void doOnPlayerInteractEvent(@NotNull PlayerInteractEvent event) {
        event.setCancelled(true);
        common.printDebugMessage("IMMOBILIZE: cancelling PlayerInteractEvent", null, null, false);
    }

    /**
     * Do any effects when player toggles flight
     *
     * @param event the player toggle flight event
     */
    @Override
    void doOnPlayerToggleFlightEvent(@NotNull PlayerToggleFlightEvent event) {
        event.setCancelled(true);
        common.printDebugMessage("IMMBOLIZE: cancelling PlayerToggleFlightEvent", null, null, false);
    }

    /**
     * Do any effects when player toggles sneaking
     *
     * @param event the player toggle sneak event
     */
    @Override
    void doOnPlayerToggleSneakEvent(@NotNull PlayerToggleSneakEvent event) {
        event.setCancelled(true);
        common.printDebugMessage("IMMBOLIZE: cancelling PlayerToggleSneakEvent", null, null, false);
    }

    /**
     * Do any effects when player toggles sprinting
     *
     * @param event the player toggle sneak event
     */
    @Override
    void doOnPlayerToggleSprintEvent(@NotNull PlayerToggleSprintEvent event) {
        event.setCancelled(true);
        common.printDebugMessage("IMMBOLIZE: cancelling PlayerToggleSprintEvent", null, null, false);
    }

    /**
     * Do any effects when player velocity changes
     *
     * @param event the player velocity event
     */
    void doOnPlayerVelocityEvent(@NotNull PlayerVelocityEvent event) {
        event.setCancelled(true);
        common.printDebugMessage("IMMBOLIZE: cancelling PlayerVelocityEvent", null, null, false);
    }

    /**
     * Do any effects when player drops an item
     *
     * @param event the event
     */
    @Override
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
        event.setCancelled(true);
        common.printDebugMessage("IMMBOLIZE: cancelling PlayerMoveEvent", null, null, false);
    }
}
