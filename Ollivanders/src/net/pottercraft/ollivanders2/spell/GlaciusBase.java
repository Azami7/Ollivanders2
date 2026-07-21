package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for Glacius spells that freeze a radius of liquids and ice: water becomes ice, lava becomes obsidian,
 * and ice becomes packed ice. The effect is temporary and reverts when the spell expires.
 *
 * @author Azami7
 */
public abstract class GlaciusBase extends BlockTransfiguration {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    public GlaciusBase(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.CHARMS;
    }

    /**
     * @param plugin    the Ollivanders2 plugin instance
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand, affects skill modifier)
     */
    public GlaciusBase(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        branch = O2MagicBranch.CHARMS;

        permanent = false;

        transfigurationMap.put(Material.WATER, Material.ICE);
        transfigurationMap.put(Material.LAVA, Material.OBSIDIAN);
        transfigurationMap.put(Material.ICE, Material.PACKED_ICE);

        // materials that can be targeted by this spell
        materialAllowList.add(Material.WATER);
        materialAllowList.add(Material.LAVA);
        materialAllowList.add(Material.ICE);

        projectilePassThrough.removeAll(materialAllowList);

        // world-guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);
    }
}
