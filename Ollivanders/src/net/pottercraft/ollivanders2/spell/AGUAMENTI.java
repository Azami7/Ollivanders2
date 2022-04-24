package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Spell which places a block of water against the targeted block.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Water-Making_Spell
 */
public final class AGUAMENTI extends BlockTransfiguration
{
    private static final int minRadiusConfig = 1;
    private static final int maxRadiusConfig = 1;
    private static final int minDurationConfig = 15 * Ollivanders2Common.ticksPerSecond;
    private static final int maxDurationConfig = 10 * Ollivanders2Common.ticksPerMinute;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public AGUAMENTI(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.AGUAMENTI;
        branch = O2MagicBranch.CHARMS;

        text = "Aguamenti will cause water to erupt against the surface you cast it on.";
        flavorText = new ArrayList<>()
        {{
            add("The Water-Making Spell conjures clean, drinkable water from the end of the wand.");
            add("The Water-Making Spell");
        }};
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public AGUAMENTI(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
        spellType = O2SpellType.AGUAMENTI;
        branch = O2MagicBranch.CHARMS;

        permanent = false;
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        durationModifier = 0.25; // 25% of usesModifier
        successMessage = "Water appears.";
        failureMessage = "Nothing seems to happen.";

        moveEffectData = Material.BLUE_ICE;

        // pass-through materials
        projectilePassThrough.remove(Material.WATER);

        // set materials that cannot be transfigured by this spell
        materialBlockedList.add(Material.FIRE);
        materialBlockedList.add(Material.SOUL_FIRE);
        materialBlockedList.add(Material.WATER);

        // what type blocks transfigure in to for this spell
        transfigureType = Material.WATER;

        // world-guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }

    /**
     * Override to make the target block the pass-through block before the actual target block
     *
     * @return the target block for aguamenti
     */
    @Override
    @Nullable
    public Block getTargetBlock()
    {
        if (hasHitTarget())
        {
            // we want to target block before this block
            return location.subtract(super.vector).getBlock();
        }

        return null;
    }
}
