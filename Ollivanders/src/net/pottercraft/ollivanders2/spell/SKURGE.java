package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.block.BlockCommon;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Skurge is a scouring charm used to clean up the sticky green ectoplasm left behind by passing ghosts.
 * <p>
 * Minecraft has no ectoplasm, so this implementation clears slime blocks instead. When the spell projectile
 * strikes a block, every slime block within {@link #radius} of the impact point is turned to air. The radius
 * scales with the caster's experience and is capped at {@link #maxRadius}.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Skurge_Charm">Harry Potter Wiki - Skurge Charm</a>
 */
public class SKURGE extends O2Spell {
    /**
     * The radius, in blocks, within which slime blocks are cleared. Set from the caster's experience in
     * {@link #doInitSpell()} and limited to the range 1 to {@link #maxRadius}.
     */
    int radius = 1;

    /**
     * The maximum radius this spell can reach, regardless of the caster's experience.
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
     * Set the effect radius from the caster's experience, limited to the range 1 to {@link #maxRadius}.
     */
    @Override
    void doInitSpell() {
        radius = (int) (usesModifier / 10);

        if (radius < 1)
            radius = 1;
        else if (radius > maxRadius)
            radius = maxRadius;
    }

    /**
     * Clear every slime block within {@link #radius} of the impact point once the projectile has hit a block, then
     * end the spell.
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitBlock())
            return;

        for (Block block : BlockCommon.getBlocksInRadiusByType(location, radius, Material.SLIME_BLOCK)) {
            block.setType(Material.AIR);
        }

        kill();
    }

    /**
     * Get the radius, in blocks, this cast clears slime blocks within. Set by {@link #doInitSpell()} from the
     * caster's experience and limited to the range 1 to {@link #maxRadius}.
     *
     * @return the affected radius
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Get the maximum radius this spell can clear, regardless of the caster's experience.
     *
     * @return the maximum radius
     */
    public int getMaxRadius() {
        return maxRadius;
    }
}
