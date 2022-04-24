package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Spell shoots a block of water at a target, extinguishing fire.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Aqua_Eructo
 */
public final class AQUA_ERUCTO extends BlockTransfiguration
{
    private static final int minRadiusConfig = 1;
    private static final int maxRadiusConfig = 1;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public AQUA_ERUCTO(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.AQUA_ERUCTO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>()
        {{
            add("The Aqua Eructo Charm");
            add("\"Very good. You'll need to use Aqua Eructo to put out the fires.\" -Bartemius Crouch Jr (disguised as Alastor Moody)");
        }};

        text = "Shoots a jet of water from your wand tip to extinguish a fire.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public AQUA_ERUCTO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
        spellType = O2SpellType.AQUA_ERUCTO;
        branch = O2MagicBranch.CHARMS;

        permanent = true;
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        successMessage = "A fire is doused by the water.";
        failureMessage = "Nothing seems to happen.";

        moveEffectData = Material.BLUE_ICE;

        // remove fire as a pass-through material
        projectilePassThrough.remove(Material.FIRE);

        // materials that can be transfigured by this spell
        materialAllowList.add(Material.LAVA);
        materialAllowList.add(Material.FIRE);

        // the map of what each material transfigures in to for this spell
        transfigurationMap.put(Material.LAVA, Material.OBSIDIAN);
        transfigurationMap.put(Material.FIRE, Material.AIR);

        // world-guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }
}