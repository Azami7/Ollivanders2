package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Mines a line of blocks of length depending on the player's level in this spell.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Gouging_Spell
 */
public final class DEFODIO extends O2Spell
{
    /**
     * The maximum depth this spell can dig
     */
    private static int maxDepth = 10;

    /**
     * The number of blocks remaining to be mined
     */
    private int remainingCount;

    /**
     * The current block position
     */
    private Block curBlock = null;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public DEFODIO(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.DEFODIO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>()
        {{
            add("The Gouging Spell enables a witch or wizard to carve through earth and stone with ease. From budding Herbologists digging for Snargaluff seedlings to treasure-hunting curse breakers uncovering ancient wizard tombs, the Gouging Spell makes all manner of heavy labour a matter of pointing a wand.");
            add("The Gouging Charm");
        }};

        text = "Mines a line of blocks.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public DEFODIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
        spellType = O2SpellType.DEFODIO;
        branch = O2MagicBranch.CHARMS;

        // world-guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        // material blocked list
        materialBlockedList.add(Material.WATER);
        materialBlockedList.add(Material.LAVA);
        materialBlockedList.add(Material.FIRE);

        for (Material material : Ollivanders2Common.unbreakableMaterials)
        {
            if (!materialBlockedList.contains(material))
                materialBlockedList.add(material);
        }

        initSpell();
    }

    @Override
    void doInitSpell()
    {
        remainingCount = (int) usesModifier / 4;

        if (remainingCount > maxDepth)
            remainingCount = maxDepth;
        else if (remainingCount < 1)
            remainingCount = 1;
    }

    /**
     * Break a row of blocks along the vector of the spell projectile
     */
    @Override
    protected void doCheckEffect()
    {
        if (!hasHitTarget())
            return;

        curBlock = getTargetBlock();
        if (curBlock == null)
        {
            common.printDebugMessage("DEFODIO.doCheckEffect: target block is null", null, null, true);
            kill();
            return;
        }

        // stop the spell if we hit a block type on the blocked list or when the max depth is reached
        if (materialBlockedList.contains(curBlock.getType()) || remainingCount <= 0)
        {
            kill();
            return;
        }

        Location curLoc = curBlock.getLocation();

        // stop the spell if something prevented the current block breaking naturally
        if (curBlock.breakNaturally())
        {
            remainingCount = remainingCount - 1;

            Location nextLoc = curLoc.add(vector);
            curBlock = nextLoc.getBlock();
        }
        else
            kill();
    }
}