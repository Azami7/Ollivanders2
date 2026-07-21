package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Periodically spawns a small slime at the target's eye location, as if they were congested. Spawned slimes are
 * left in the world when the effect ends. Detectable via Informous.
 *
 * @author Azami7
 */
public class MUCUS extends O2Effect {
    /**
     * How often, in ticks, we spawn another slime
     */
    public final static int mucusFrequency = 300;

    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the mucus effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to afflict with mucus spawning
     */
    public MUCUS(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.MUCUS;
        checkDurationBounds();

        informousText = "is unnaturally congested";
    }

    @Override
    public void checkEffect() {
        age(1);
        if (duration % mucusFrequency == 0) {
            World world = target.getWorld();
            Slime slime = (Slime) world.spawnEntity(target.getEyeLocation(), EntityType.SLIME);
            slime.setSize(1);
        }
    }

    @Override
    public void doRemove() {
    }
}