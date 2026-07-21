package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;

import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Forces the affected player into a simulated deep sleep: tilts their head down and applies {@link SLEEP_SPEECH} and
 * {@link FULL_IMMOBILIZE} for the duration. Broken early by the {@link AWAKE} effect. Detectable via Informous and
 * Legilimens.
 *
 * @author Azami7
 * @see SLEEP_SPEECH
 * @see FULL_IMMOBILIZE
 * @see AWAKE
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
     * Whether the sleep state has been applied, so it is only set up once.
     */
    private boolean sleeping = false;

    /**
     * Constructor
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

    @Override
    public void checkEffect() {
        age(1);

        if (!sleeping) {
            playerSleep();
        }
    }

    /**
     * Set up the sleep state: tilt the head down, immobilize the player, apply {@link SLEEP_SPEECH}, and notify them.
     * Runs once, guarded by the sleeping flag.
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
            Ollivanders2API.getPlayers().playerEffects.addEffect(new FULL_IMMOBILIZE(p, -1, true, targetID));
        }

        // add sleep speech
        Ollivanders2API.getPlayers().playerEffects.addEffect(new SLEEP_SPEECH(p, -1, true, targetID));

        // send the sleeping message
        target.sendMessage(Ollivanders2.chatColor + SLEEP_MESSAGE);

        sleeping = true;
    }

    /**
     * Remove the secondary effects ({@link SLEEP_SPEECH}, {@link FULL_IMMOBILIZE}), wake the player, and notify them.
     */
    @Override
    public void doRemove() {
        // if we simulated sleep, remove those effects
        Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, O2EffectType.SLEEP_SPEECH);
        Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, O2EffectType.FULL_IMMOBILIZE);

        // if the player was properly put to sleep, wake them up
        if (!Ollivanders2.testMode) // MockBukkit does not have player sleep and doing this will cause an UnimplementedOperationException
            target.wakeup(true);

        // send the player a message that they awaken
        target.sendMessage(Ollivanders2.chatColor + SLEEP_WAKEUP_MESSAGE);

        sleeping = false;
    }
}
