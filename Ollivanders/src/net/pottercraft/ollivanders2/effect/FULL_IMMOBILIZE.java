package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Complete immobilization effect that prevents all player movement including rotation.
 *
 * <p>FULL_IMMOBILIZE completely paralyzes the affected player by preventing all forms of movement,
 * rotation, and interaction. Unlike IMMOBILIZE which allows rotation (pitch and yaw changes), this
 * effect prevents any movement whatsoever. The effect cancels all movement-related events (location
 * changes, velocity changes, rotation changes, flight toggling, sprinting, sneaking) as well as
 * teleportation and interaction events. The player is effectively frozen in place for the duration
 * of the effect and cannot escape via teleportation magic.</p>
 *
 * <p>Key Differences from IMMOBILIZE:</p>
 * <ul>
 * <li>Prevents rotation (pitch and yaw changes), whereas IMMOBILIZE allows rotation</li>
 * <li>Cancels teleport and apparate events instead of removing the effect</li>
 * </ul>
 *
 * <p>Mechanism:</p>
 * <ul>
 * <li>Player movement events are cancelled (including rotation changes)</li>
 * <li>Velocity changes are cancelled</li>
 * <li>Flight toggling is cancelled</li>
 * <li>Sneak toggling is cancelled</li>
 * <li>Sprint toggling is cancelled</li>
 * <li>Teleport and apparate events are cancelled (prevents magical escape)</li>
 * <li>Interaction events (block breaking, placing, etc.) are cancelled</li>
 * <li>Detectable by mind-reading spells (Legilimens)</li>
 * <li>Detection text: "is unable to move"</li>
 * </ul>
 *
 * @author Azami7
 */
public class FULL_IMMOBILIZE extends IMMOBILIZE {
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
    public FULL_IMMOBILIZE(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.FULL_IMMOBILIZE;
        checkDurationBounds();

        allowRotation = false;

        informousText = legilimensText = "is unable to move";
    }

    /**
     * Prevent the player from using standard Bukkit teleportation while immobilized.
     *
     * <p>When a fully immobilized player attempts to teleport via Bukkit's teleport system, this
     * event is cancelled, preventing the teleportation. Unlike IMMOBILIZE which removes itself to
     * allow magical escape, FULL_IMMOBILIZE actively blocks teleportation, trapping the player
     * completely. The effect remains active after the cancelled teleport event.</p>
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
     * Prevent the player from using coordinate-based apparition while immobilized.
     *
     * <p>When a fully immobilized player attempts to use the APPARATE spell with specific coordinates,
     * this event is cancelled, preventing the apparition. Unlike IMMOBILIZE which removes itself to
     * allow magical escape, FULL_IMMOBILIZE actively blocks apparition, trapping the player
     * completely. The effect remains active after the cancelled apparate event.</p>
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
     * Prevent the player from using name-based apparition while immobilized.
     *
     * <p>When a fully immobilized player attempts to use the APPARATE spell with a player name
     * destination, this event is cancelled, preventing the apparition. Unlike IMMOBILIZE which
     * removes itself to allow magical escape, FULL_IMMOBILIZE actively blocks apparition, trapping
     * the player completely. The effect remains active after the cancelled apparate event.</p>
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
