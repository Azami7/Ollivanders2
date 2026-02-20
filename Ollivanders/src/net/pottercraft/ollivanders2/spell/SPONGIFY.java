package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Softening Charm (Spongify) is a spell that softens a target area or object, making it rubbery and bouncy.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Softening_Charm">https://harrypotter.fandom.com/wiki/Softening_Charm</a>
 */
public class SPONGIFY extends BlockTransfiguration
{
    private static final int minRadiusConfig = 1;
    private static final int maxRadiusConfig = 15;
    private static final int minDurationConfig = 15 * Ollivanders2Common.ticksPerSecond;
    private static final int maxDurationConfig = 10 * Ollivanders2Common.ticksPerMinute;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public SPONGIFY(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.SPONGIFY;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>()
        {{
            add("\"Today's lesson will most assuredly involve learning how to cast the Softening Charm, Spongify.\" -Filius Flitwick");
            add("The Softening Charm");
        }};

        text = "Turns the blocks in a radius in to slime blocks.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public SPONGIFY(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
        spellType = O2SpellType.SPONGIFY;
        branch = O2MagicBranch.CHARMS;

        permanent = false;
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        radiusModifier = 0.25; // 25% of usesModifier
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        durationModifier = 0.5; // 50% of usesModifier

        // what type blocks transfigure in to for this spell
        transfigureType = Material.SPONGE;

        // types of materials this spell cannot change or pass through
        materialBlockedList.add(Material.WATER);
        materialBlockedList.add(Material.LAVA);
        materialBlockedList.add(Material.FIRE);

        // make sure none of these are on the pass-through list
        projectilePassThrough.removeAll(materialBlockedList);

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }
}
