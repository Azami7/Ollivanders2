package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Keeps the affected player awake, blocking bed entry and removing any active {@link SLEEPING} effect (the two are
 * mutually exclusive). Detectable via Informous and Legilimens.
 *
 * @author Azami7
 * @see SLEEPING
 */
public class AWAKE extends O2Effect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the awake state in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to keep awake
     */
    public AWAKE(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.AWAKE;
        checkDurationBounds();

        informousText = legilimensText = "is unnaturally alert";
    }

    /**
     * Age the effect and, each tick, remove any active {@link SLEEPING} effect and wake the player.
     */
    @Override
    public void checkEffect() {
        age(1);

        // AWAKE overrides SLEEPING so remove it if the player has it
        if (Ollivanders2API.getPlayers().playerEffects.hasEffect(targetID, O2EffectType.SLEEPING))
            Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, O2EffectType.SLEEPING);

        // make sure target is not sleeping
        if (target.isSleeping()) {
            if (!Ollivanders2.testMode) // MockBukkit does not have player sleep and doing this will cause an UnimplementedOperationException
                target.wakeup(true);
        }
    }

    @Override
    public void doRemove() {
    }

    /**
     * Cancel the target's bed entry to keep them awake.
     */
    @Override
    void doOnPlayerBedEnterEvent(@NotNull PlayerBedEnterEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        event.setCancelled(true);
    }
}
