package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Doubles the target over with tickling, forcing them to sneak and locking them out of standing up. Also applies a
 * companion {@link LAUGHING} effect for its duration. Detectable via Informous.
 *
 * @author Azami7
 */
public class TICKLING extends O2Effect {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the tickling effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to tickle
     */
    public TICKLING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.TICKLING;
        checkDurationBounds();

        informousText = "is doubled-over from tickling.";
        affectedPlayerText = "You buckle due to excessive tickling.";

        Ollivanders2API.getPlayers().playerEffects.addEffect(new LAUGHING(p, -1, true, targetID));
    }

    @Override
    public void checkEffect() {
        age(1);

        // re-assert the sneak every 5 seconds so the player stays doubled over
        if ((duration % (5 * Ollivanders2Common.ticksPerSecond)) == 0) {
            target.setSneaking(true);
        }
    }

    /**
     * Keep the tickled player sneaking by cancelling their attempts to stand up.
     *
     * @param event the sneak toggle event
     */
    @Override
    void doOnPlayerToggleSneakEvent(@NotNull PlayerToggleSneakEvent event) {
        if (event.getPlayer().getUniqueId().equals(targetID))
            event.setCancelled(true);
    }

    @Override
    public void doRemove() {
        Ollivanders2API.getPlayers().playerEffects.removeEffect(targetID, O2EffectType.LAUGHING);
    }
}
