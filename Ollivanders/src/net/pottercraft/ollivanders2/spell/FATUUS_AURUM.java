package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Temporarily turns items to gold but this transition is not permanent.
 */
public final class FATUUS_AURUM extends BlockTransfiguration {
    private static final int minRadiusConfig = 1;
    private static final int maxRadiusConfig = 1;
    private static final int minDurationConfig = 15 * Ollivanders2Common.ticksPerSecond;
    private static final int maxDurationConfig = 10 * Ollivanders2Common.ticksPerMinute;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public FATUUS_AURUM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.FATUUS_AURUM;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("What glitters may not be gold; and even wolves may smile; and fools will be led by promises to their deaths.");
            add("There is thy gold, worse poison to men's souls.");
            add("Stone to Gold Charm");
        }};

        text = "Turns a stone block in to gold.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public FATUUS_AURUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.FATUUS_AURUM;
        branch = O2MagicBranch.CHARMS;

        permanent = false;
        minRadius = minRadiusConfig;
        maxRadius = maxRadiusConfig;
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        successMessage = "Gold!";

        // set materials that can be transfigured by this spell
        materialAllowList.add(Material.STONE);

        // what type blocks transfigure in to for this spell
        transfigureType = Material.GOLD_BLOCK;

        // world-guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }
}
