package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;

import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Sleep induction effect that forces the affected player into a deep sleep state.
 *
 * <p>SLEEPING is a temporary debilitating effect that simulates sleep for the target player since
 * Minecraft does not support actual sleep actions for non-player-initiated sleeps. When activated,
 * the effect immobilizes the player, tilts their head downward (pitch = 45 degrees), applies the
 * SLEEP_SPEECH effect to replace their chat with sleep sounds, and applies the IMMOBILIZE effect
 * to prevent all movement and interaction. The sleep state can be broken by applying the AWAKE effect,
 * which automatically kills this effect. When removed, all secondary effects (speech replacement and
 * immobilization) are cleaned up and the player is notified of awakening.</p>
 *
 * <p>Sleep Configuration:</p>
 * <ul>
 * <li>Secondary effects: SLEEP_SPEECH (sleep vocalizations) and IMMOBILIZE (paralysis)</li>
 * <li>Head tilt: downward pitch angle of 45 degrees</li>
 * <li>Sleep state tracking: boolean flag to detect sleep initialization</li>
 * <li>Breaking condition: AWAKE effect application</li>
 * <li>Permanent: false (temporary effect with time-based expiration)</li>
 * <li>Detection text: "is affected by an unnatural sleep"</li>
 * </ul>
 *
 * @author Azami7
 * @see SLEEP_SPEECH for the sleep vocalization effect applied during sleep
 * @see IMMOBILIZE for the paralysis effect applied during sleep
 * @see AWAKE for the effect that breaks the sleep state
 */
public class SLEEPING extends O2Effect {
    boolean sleeping = false;

    /**
     * Constructor for creating a sleep induction effect.
     *
     * <p>Creates a sleep effect that forces the target player into a deep sleep state. On the first
     * checkEffect() call, the player will be immobilized, their head will be tilted downward, sleep
     * vocalizations will begin, and they will receive a notification. The sleep persists until the
     * duration expires or the AWAKE effect is applied to break the sleep.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the sleep effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to put to sleep
     */
    public SLEEPING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.SLEEPING;
        checkDurationBounds();

        informousText = legilimensText = "is affected by an unnatural sleep";
    }

    /**
     * Age the sleep effect and apply sleep state once per effect lifetime.
     *
     * <p>Called each game tick. This method checks if the sleep state has been initialized. If not,
     * it first checks whether the AWAKE effect is present on the player. If AWAKE is detected, the
     * sleep effect is immediately terminated. Otherwise, the playerSleep() method is called once to
     * initialize the sleep state (applying secondary effects and head tilt). The effect duration is
     * then aged each tick until expiration.</p>
     */
    @Override
    public void checkEffect() {
        if (!sleeping) {
            if (Ollivanders2API.getPlayers().playerEffects.hasEffect(targetID, O2EffectType.AWAKE)) {
                kill();
            }
            else {
                playerSleep();
            }
        }

        if (!permanent) {
            age(1);
        }
    }

    /**
     * Initialize the sleep state by applying effects and visual changes.
     *
     * <p>Applies the complete sleep transformation to the target player: tilts their head downward
     * (pitch = 45 degrees), applies the SLEEP_SPEECH effect for sleep vocalizations, applies the
     * IMMOBILIZE effect for complete paralysis, and notifies the player they have fallen into a deep
     * sleep. This method is called once when the sleep effect first activates and marks the sleeping
     * flag as true to prevent re-application. If the player is offline when this is called, the sleep
     * effect is terminated.</p>
     */
    private void playerSleep() {
        Player target = p.getServer().getPlayer(targetID);
        if (target == null) {
            kill();
            return;
        }

        // tilt player head down
        Location loc = target.getLocation();
        Location newLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), 45);
        target.teleport(newLoc);

        // add sleep speech
        Ollivanders2API.getPlayers().playerEffects.addEffect(new SLEEP_SPEECH(p, -1, true, targetID));

        // immobilize them
        Ollivanders2API.getPlayers().playerEffects.addEffect(new IMMOBILIZE(p, -1, true, targetID));

        sleeping = true;
        target.sendMessage(Ollivanders2.chatColor + "You fall in to a deep sleep.");
    }

    /**
     * Clean up the sleep effect and restore the player to normal state.
     *
     * <p>When the sleep effect is removed, this method cleans up the secondary effects (SLEEP_SPEECH
     * and IMMOBILIZE) that were applied during sleep. The player is notified of awakening and the
     * sleeping flag is reset. If the player is offline, cleanup is gracefully skipped.</p>
     */
    @Override
    public void doRemove() {
        if (sleeping) {
            Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, O2EffectType.SLEEP_SPEECH);
            Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, O2EffectType.IMMOBILIZE);
            sleeping = false;

            Player target = p.getServer().getPlayer(targetID);
            if (target != null)
                target.sendMessage(Ollivanders2.chatColor + "You awaken from a deep sleep.");
        }
    }
}
