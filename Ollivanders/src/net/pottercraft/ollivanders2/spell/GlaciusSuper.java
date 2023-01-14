package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Glacius will cause a great cold to descend in a radius from its impact point which freezes blocks. The radius and
 * duration of the freeze depend on your experience.
 */
public abstract class GlaciusSuper extends BlockTransfiguration
{
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public GlaciusSuper(Ollivanders2 plugin)
    {
        super(plugin);

        branch = O2MagicBranch.CHARMS;
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public GlaciusSuper(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
        branch = O2MagicBranch.CHARMS;

        permanent = false;

        transfigurationMap.put(Material.FIRE, Material.AIR);
        transfigurationMap.put(Material.WATER, Material.ICE);
        transfigurationMap.put(Material.LAVA, Material.OBSIDIAN);
        transfigurationMap.put(Material.ICE, Material.PACKED_ICE);

        // materials that can be targeted by this spell
        materialAllowList.add(Material.FIRE);
        materialAllowList.add(Material.WATER);
        materialAllowList.add(Material.LAVA);
        materialAllowList.add(Material.ICE);

        // world-guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);
    }
}
