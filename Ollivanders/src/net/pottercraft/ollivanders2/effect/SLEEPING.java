package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;

import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Location;
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
    /**
     * Message sent to the player when they fall asleep.
     */
    public static final String SLEEP_MESSAGE = "You fall in to a deep sleep.";

    /**
     * Message sent to the player when they wake up.
     */
    public static final String SLEEP_WAKEUP_MESSAGE = "You awaken from a deep sleep.";

    /**
     * Track if we put them in to sleep, real or simulated
     */
    private boolean sleeping = false;

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
     * <p>Called each game tick. This method first ages the effect by 1 tick. Then it checks if the sleep
     * state has been initialized via the sleeping flag. If not, it calls playerSleep() once to initialize
     * the sleep state (applying secondary effects, head tilt, and messaging). The sleep persists each tick
     * until the effect duration expires or is explicitly removed.</p>
     */
    @Override
    public void checkEffect() {
        age(1);

        // if the player is not sleeping, put them to sleep
        if (!sleeping) {
            playerSleep();
        }
    }

    /**
     * Initialize the sleep state by applying effects and visual changes.
     *
     * <p>Applies the complete sleep transformation to the target player in multiple steps:</p>
     * <ol>
     * <li>Attempts to put the player to sleep via Bukkit API (skipped in test mode)</li>
     * <li>If the player is not actually sleeping after the attempt, simulates sleep by tilting their head
     * downward (pitch = 45 degrees) and applying the IMMOBILIZE effect for paralysis</li>
     * <li>Always applies the SLEEP_SPEECH effect for sleep vocalizations</li>
     * <li>Notifies the player they have fallen into a deep sleep</li>
     * <li>Marks the sleeping flag as true to prevent re-application</li>
     * </ol>
     *
     * <p>This method is called once when the sleep effect first activates via checkEffect(). The head tilt
     * and IMMOBILIZE are conditional to handle both real sleep (via Bukkit API) and simulated sleep
     * (when the player is not actually in a bed).</p>
     */
    private void playerSleep() {
        if (!Ollivanders2.testMode) { // MockBukkit does not have player sleep and doing this will cause an UnimplementedOperationException
            // attempt to put them to sleep, may not work if there is no bed
            target.sleep(target.getLocation(), true);
        }

        // simulate sleeping since the sleep() call did not actually force sleep. This way they act asleep regardless of location.
        if (!target.isSleeping()) {
            // tilt player head down
            Location loc = target.getLocation();
            Location newLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), 45);
            target.teleport(newLoc);

            // immobilize them so they cannot move out of this position
            Ollivanders2API.getPlayers().playerEffects.addEffect(new IMMOBILIZE(p, -1, true, targetID));
        }

        // add sleep speech
        Ollivanders2API.getPlayers().playerEffects.addEffect(new SLEEP_SPEECH(p, -1, true, targetID));

        // send the sleeping message
        target.sendMessage(Ollivanders2.chatColor + SLEEP_MESSAGE);

        sleeping = true;
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
        // if we simulated sleep, remove those effects
        Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, O2EffectType.SLEEP_SPEECH);
        Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, O2EffectType.IMMOBILIZE);

        // if the player was properly put to sleep, wake them up
        if (!Ollivanders2.testMode) // MockBukkit does not have player sleep and doing this will cause an UnimplementedOperationException
            target.wakeup(true);

        // send the player a message that they awaken
        target.sendMessage(Ollivanders2.chatColor + SLEEP_WAKEUP_MESSAGE);

        sleeping = false;
    }
}
