package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Mucus effect that spawns small slime entities on a player's head periodically.
 *
 * <p>MUCUS is a debilitating effect that forces the target player to continuously spawn small slime
 * entities at their eye level (typically on their head/face). Every 15 seconds (300 game ticks), a
 * new size-1 slime entity is spawned at the player's eye location, creating a visual and potentially
 * blocking effect. This effect persists until the duration expires or is manually killed. The effect
 * is detectable by information spells (Informous) which report the target "is unnaturally congested".</p>
 *
 * <p>Mechanism:</p>
 * <ul>
 * <li>Spawns size-1 slime entities periodically at player's eye location</li>
 * <li>Spawn interval: every 15 seconds (300 game ticks)</li>
 * <li>Creates continuous mucus/slime effect on the affected player</li>
 * <li>Detectable by information spells (Informous)</li>
 * <li>Detection text: "is unnaturally congested"</li>
 * <li>Effect expires naturally when duration reaches zero</li>
 * </ul>
 *
 * @author Azami7
 */
public class MUCUS extends O2Effect {
    /**
     * Constructor for creating a mucus spawning effect.
     *
     * <p>Creates an effect that forces the target player to spawn small slime entities periodically.
     * Sets the detection text for information spells to "is unnaturally congested". Slimes are spawned
     * at the player's eye level every 15 seconds throughout the effect duration.</p>
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the mucus effect in game ticks
     * @param pid      the unique ID of the player to afflict with mucus spawning
     */
    public MUCUS(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.MUCUS;
        informousText = "is unnaturally congested";
    }

    /**
     * Spawn a slime entity on the player's head every 15 seconds.
     *
     * <p>Called each game tick. This method ages the effect and checks if it's time to spawn a new slime
     * (every 300 ticks = 15 seconds). When spawn time arrives, a size-1 slime entity is spawned at the
     * player's eye location. If the player goes offline or is null, the effect is killed.</p>
     */
    @Override
    public void checkEffect() {
        age(1);
        if (duration % 300 == 0) {
            Player target = p.getServer().getPlayer(targetID);

            if (target != null) {
                World world = target.getWorld();
                Slime slime = (Slime) world.spawnEntity(target.getEyeLocation(), EntityType.SLIME);
                slime.setSize(1);
            }
            else
                kill();
        }
    }

    /**
     * Perform cleanup when the mucus effect is removed.
     *
     * <p>The default implementation does nothing, as the mucus effect has no persistent state to clean up.
     * When removed, the player stops spawning slime entities, though any previously spawned slimes remain
     * in the world and must be cleaned up separately.</p>
     */
    @Override
    public void doRemove() {
    }
}