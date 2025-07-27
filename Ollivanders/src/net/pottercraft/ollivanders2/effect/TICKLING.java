package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Causes the target to squat (sneak) due to being tickled
 *
 * @since 2.21
 * @author Azami7
 */
public class TICKLING extends O2Effect {
    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public TICKLING(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.TICKLING;
        informousText = "is doubled-over from tickling.";
        affectedPlayerText = "You buckle due to excessive tickling.";

        Player player = p.getServer().getPlayer(targetID);
        if (player == null) {
            common.printDebugMessage("TICKLING.constructor: player is null", null, null, true);
            kill();
            return;
        }
        else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setSneaking(true);
                }
            }.runTaskLater(p, 5);
        }
    }

    /**
     * Age this effect each game tick.
     */
    @Override
    public void checkEffect() {
        // age this effect
        age(1);
    }

    /**
     * Cancel any sneak toggle events
     *
     * @param event the event
     */
    @Override
    void doOnPlayerToggleSneakEvent(@NotNull PlayerToggleSneakEvent event) {
        if (event.getPlayer().getUniqueId().equals(targetID)) {
            event.setCancelled(true);
        }
    }

    /**
     * No cleanup for this spell
     */
    @Override
    public void doRemove() {
        Player player = p.getServer().getPlayer(targetID);

        if (player == null)
            common.printDebugMessage("TICKLING.doRemove: player is null", null, null, true);
        else
            player.setSneaking(false);
    }
}
