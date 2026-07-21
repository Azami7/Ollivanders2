package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Forces a player to dance uncontrollably: cancels their movement, sneak, and sprint input and periodically
 * teleports them to a random new facing. Detectable via Informous.
 *
 * @author Azami7
 */
public class DANCING_FEET extends O2Effect {
    /**
     * Ticks remaining until the next dance cycle; counts down to 0 each cycle.
     */
    int cooldownCounter = 0;

    /**
     * Interval between dance cycles, in ticks.
     */
    final int cooldown = Ollivanders2Common.ticksPerSecond / 2;

    /**
     * Constructor
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

    @Override
    public void checkEffect() {
        age(1);

        if (cooldownCounter > 0)
            cooldownCounter = cooldownCounter - 1;
        else {
            cooldownCounter = cooldown;

            // toggle sneak; when turning it on, return so the stand-up and reposition happen on a later cycle
            if (target.isSneaking()) {
                target.setSneaking(false);
            }
            else {
                target.setSneaking(true);
                return;
            }

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
     * Cancel the target's sneak toggles so the effect keeps control of their sneak state.
     */
    @Override
    void doOnPlayerToggleSneakEvent(@NotNull PlayerToggleSneakEvent event) {
        if (event.getPlayer().getUniqueId().equals(targetID)) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancel the target's sprint toggles while dancing.
     */
    @Override
    void doOnPlayerToggleSprintEvent(@NotNull PlayerToggleSprintEvent event) {
        if (event.getPlayer().getUniqueId().equals(targetID)) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancel the target's own movement so the effect controls their position via teleportation.
     */
    @Override
    void doOnPlayerMoveEvent(@NotNull PlayerMoveEvent event) {
        if (event.getPlayer().getUniqueId().equals(targetID)) {
            event.setCancelled(true);
        }
    }

    @Override
    public void doRemove() {
    }
}
