package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Tickling effect that forces the affected player into a continuous sneak state.
 *
 * <p>TICKLING is a debilitating effect that causes the target player to be overcome with tickling,
 * forcing them to crouch (sneak) continuously. The player is forced into a sneak state and cannot
 * toggle out of it while the effect is active. Any attempts to stop sneaking are cancelled. When
 * the effect expires, the player is restored to normal standing state. The effect is detectable by
 * both mind-reading spells (Legilimens) and information spells (Informous) which report the target
 * "is doubled-over from tickling".</p>
 *
 * @author Azami7
 */
public class TICKLING extends O2Effect {
    /**
     * Constructor for creating a tickling effect.
     *
     * <p>Creates a tickling effect that forces the target player into a sneak state. The sneak state
     * is applied with a 5-tick delay to ensure proper initialization. Detection text is set for
     * information spells and player notifications are configured. If the player is offline when the
     * effect is created, the effect is immediately terminated.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the tickling effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to tickle
     */
    public TICKLING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.TICKLING;
        informousText = "is doubled-over from tickling.";
        affectedPlayerText = "You buckle due to excessive tickling.";

        Player player = p.getServer().getPlayer(targetID);
        if (player == null) {
            common.printDebugMessage("TICKLING.constructor: player is null", null, null, true);
            kill();
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
     * Age the tickling effect each game tick.
     *
     * <p>Called each game tick. This method simply ages the effect counter, allowing the duration to
     * expire naturally. The forced sneak state is maintained by cancelling toggle sneak events.</p>
     */
    @Override
    public void checkEffect() {
        // age this effect
        age(1);
    }

    /**
     * Prevent the tickled player from toggling out of sneak.
     *
     * <p>Cancels any sneak toggle events from the affected player, ensuring they remain locked in the
     * sneak state for the duration of the tickling effect.</p>
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
     * Restore the tickled player to normal standing state.
     *
     * <p>When the tickling effect is removed, this method stops the player's sneak state, allowing them
     * to return to normal standing. If the player is offline, the operation is gracefully skipped.</p>
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
