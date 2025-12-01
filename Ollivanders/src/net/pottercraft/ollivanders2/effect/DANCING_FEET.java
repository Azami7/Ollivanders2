package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Effect that forces a player to move around uncontrollably through involuntary sneak toggling.
 *
 * <p>The DANCING_FEET effect causes affected players to dance uncontrollably by repeatedly toggling their
 * sneak state at half-second intervals (10 ticks). Each cycle alternates between sneaking and standing,
 * with a random directional rotation (0-360 degrees) applied before teleporting the player to the new
 * location. The teleportation is delayed by 5 ticks to prevent all movements from happening simultaneously.</p>
 *
 * <p>Mechanism:</p>
 * <ul>
 * <li>Sneak state toggled every half-second (10 tick cooldown)</li>
 * <li>Random rotation (0-360 degrees) applied each cycle</li>
 * <li>Player teleported to new location with 5-tick delay</li>
 * <li>Player movement events cancelled to prevent manual control</li>
 * <li>Sneak and sprint toggles cancelled to prevent state override</li>
 * <li>Detectable by information spells (Informous)</li>
 * <li>Informs player "You start dancing" on application</li>
 * </ul>
 *
 * @author Azami7
 */
public class DANCING_FEET extends O2Effect {
    /**
     * Countdown counter for the movement cooldown.
     *
     * <p>Tracks remaining ticks until the next sneak toggle and teleportation. Counts down from cooldown
     * (10 ticks) to 0 each cycle. When it reaches 0, the player's sneak state is toggled and they are
     * teleported with a new random rotation.</p>
     */
    int cooldownCounter = 0;

    /**
     * The duration of the movement cooldown in game ticks (half second).
     *
     * <p>Set to ticksPerSecond / 2, which equals 10 ticks (0.5 seconds). This determines the frequency
     * of involuntary sneak toggles and teleportations.</p>
     */
    final int cooldown = Ollivanders2Common.ticksPerSecond / 2;

    Player target = null;

    /**
     * Constructor for creating a dancing feet uncontrollable movement effect.
     *
     * <p>Creates an effect that forces the player to dance uncontrollably by repeatedly toggling their sneak
     * state and teleporting them with random rotations. Sets information detection text to "cannot control their feet"
     * and affected player text to "You start dancing" to notify the player they are affected.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to affect with dancing
     */
    public DANCING_FEET(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.DANCING_FEET;
        checkDurationBounds();

        informousText = "cannot control their feet.";
        affectedPlayerText = "You start dancing.";
    }

    /**
     * Age the effect and apply periodic sneak toggles with delayed teleportation.
     *
     * <p>Called each game tick. This method:</p>
     * <ol>
     * <li>Ages the effect (decrements duration until expiration)</li>
     * <li>Decrements the cooldownCounter by 1 each tick</li>
     * <li>When cooldownCounter reaches 0, executes a dance cycle:
     *   <ul>
     *   <li>Resets cooldownCounter to cooldown (10 ticks)</li>
     *   <li>Toggles player sneak state (returns early if turning sneak ON to split toggles across ticks)</li>
     *   <li>Generates random rotation (0-360 degrees)</li>
     *   <li>Schedules teleportation 5 ticks later with the new rotated location</li>
     *   </ul>
     * </li>
     * </ol>
     */
    @Override
    public void checkEffect() {
        if (target == null) {
            // save the player so we don't have to keep calling getPlayer()
            target = p.getServer().getPlayer(targetID);

            if (target == null) {
                kill();
                return;
            }
        }

        // age this effect
        age(1);

        if (cooldownCounter > 0) // decrement cooldown counter
            cooldownCounter = cooldownCounter - 1;
        else { // move the player if the cooldown counter is complete
            cooldownCounter = cooldown;

            // toggle sneaking - so if they are sneaking make them stand, if standing make them sneak
            if (target.isSneaking()) {
                target.setSneaking(false);
            }
            else {
                target.setSneaking(true);
                return;
            }

            // pick a random direction to face
            int rand = Math.abs(Ollivanders2Common.random.nextInt() % 360);
            float currentYaw = target.getLocation().getYaw();
            Location newLocation = target.getLocation();
            newLocation.setYaw(currentYaw + rand);

            // teleport the player 5 ticks later so that their movements do not seem to all happen at once
            new BukkitRunnable() {
                @Override
                public void run() {
                    target.teleport(newLocation);
                }
            }.runTaskLater(p, 5);
        }
    }

    /**
     * Prevent the player from manually toggling sneak state.
     *
     * <p>Cancels sneak toggle events to ensure the effect maintains complete control over the player's
     * sneak state. Manual sneak toggling would interfere with the involuntary dance movement cycles.</p>
     *
     * @param event the sneak toggle event to cancel
     */
    @Override
    void doOnPlayerToggleSneakEvent(@NotNull PlayerToggleSneakEvent event) {
        if (event.getPlayer().getUniqueId().equals(targetID)) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevent the player from toggling sprint while dancing.
     *
     * <p>Cancels sprint toggle events to prevent the player from activating sprint, which would
     * interfere with the controlled dance movement and teleportation cycles.</p>
     *
     * @param event the sprint toggle event to cancel
     */
    @Override
    void doOnPlayerToggleSprintEvent(@NotNull PlayerToggleSprintEvent event) {
        if (event.getPlayer().getUniqueId().equals(targetID)) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevent the player from manually moving while affected by dancing effect.
     *
     * <p>Cancels all player movement events to ensure the effect maintains complete control over the
     * player's position through involuntary teleportation. Manual movement would interfere with the
     * choreographed dance cycles.</p>
     *
     * @param event the player move event to cancel
     */
    @Override
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
        if (event.getPlayer().getUniqueId().equals(targetID)) {
            event.setCancelled(true);
        }
    }

    /**
     * Perform cleanup when the dancing effect is removed.
     *
     * <p>The default implementation does nothing, as the dancing effect has no persistent state to
     * clean up. The player regains normal control over movement once the effect expires or is killed.</p>
     */
    @Override
    public void doRemove() {
    }
}
