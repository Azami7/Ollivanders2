package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Skurge can be used to clean up the sticky green ectoplasm created by passing ghosts - for minecraft it cleans up
 * slime.
 */
public class SKURGE extends O2Spell {
    /**
     * The radius of effect for this spell
     */
    int radius = 1;

    /**
     * The max radius for this spell
     */
    final int maxRadius = 20;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public SKURGE(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.SKURGE;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Skurge Charm");
            add("\"There is a way of cleaning away ectoplasm without resorting to a can of Mrs Skower's Magical Mess Remover, and this is with an effective scouring charm such as Skurge.\"");
        }};

        text = "Cleans up slime in a radius based on the caster's spell level.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public SKURGE(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.SKURGE;
        branch = O2MagicBranch.CHARMS;

        // world guard
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }

    /**
     * Set the radius of effect for this spell.
     */
    @Override
    void doInitSpell() {
        radius = (int) (usesModifier / 10);

        if (radius < 1)
            radius = 1;
        else if (radius > maxRadius)
            radius = maxRadius;
    }

    @Override
    protected void doCheckEffect() {
        if (!hasHitTarget())
            return;

        for (Block block : Ollivanders2Common.getBlocksInRadius(location, radius)) {
            if (block.getType() == Material.SLIME_BLOCK) {
                block.setType(Material.AIR);
            }
        }

        kill();
    }
}
