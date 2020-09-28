package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Creates an explosion at the target which scales with the player's level in the spell. Doesn't break blocks.
 *
 * @author Azami7
 */
public abstract class BombardaSuper extends O2Spell
{
    double minStrength = 1;
    double maxStrength = 4.0; // 4.0 is the strength of TNT

    double strengthMultiplier = 0.25;

    BombardaSuper()
    {
        super();
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    BombardaSuper(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        // world guard flags
        worldGuardFlags.add(Flags.TNT);
        worldGuardFlags.add(Flags.OTHER_EXPLOSION);
    }

    /**
     * Create an explosion in front of the target block
     */
    protected void doCheckEffect()
    {
        if (hasHitTarget())
        {
            double strength = (usesModifier / 10) * strengthMultiplier;
            if (strength < minStrength)
            {
                strength = minStrength;
            }
            else if (strength > maxStrength)
            {
                strength = maxStrength;
            }

            Location backLoc = location.clone().subtract(vector);
            World world = backLoc.getWorld();
            if (world == null)
            {
                p.getLogger().warning("BombardaSuper.doCheckEffect: world is null");
                kill();
                return;
            }

            world.createExplosion(backLoc.getX(), backLoc.getY(), backLoc.getZ(), (float) strength, false, true);
            kill();
        }
    }
}