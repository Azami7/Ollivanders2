package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Complete immobilization: like {@link IMMOBILIZE} but also freezes rotation, and cancels teleport and apparate
 * attempts outright rather than allowing long-distance magical escape.
 *
 * @author Azami7
 */
public class FULL_IMMOBILIZE extends IMMOBILIZE {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the immobilization effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to immobilize
     */
    public FULL_IMMOBILIZE(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.FULL_IMMOBILIZE;
        checkDurationBounds();

        allowRotation = false;

        informousText = legilimensText = "is unable to move";
    }

    /**
     * Cancel any teleport attempt while immobilized; unlike {@link IMMOBILIZE}, no distance allows escape.
     *
     * @param event the player teleport event to cancel
     */
    @Override
    void doOnPlayerTeleportEvent(@NotNull PlayerTeleportEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        event.setCancelled(true);
    }

    /**
     * Cancel any coordinate-based apparition attempt while immobilized; unlike {@link IMMOBILIZE}, no distance allows
     * escape.
     *
     * @param event the apparate by coordinates event to cancel
     */
    @Override
    void doOnOllivandersApparateByCoordinatesEvent(@NotNull OllivandersApparateByCoordinatesEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        event.setCancelled(true);
    }

    /**
     * Cancel any name-based apparition attempt while immobilized; unlike {@link IMMOBILIZE}, no distance allows
     * escape.
     *
     * @param event the apparate by name event to cancel
     */
    @Override
    void doOnOllivandersApparateByNameEvent(@NotNull OllivandersApparateByNameEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        event.setCancelled(true);
    }
}
