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
import java.util.List;

/**
 * Spell that breaks glass blocks in a radius around the target.
 *
 * <p>When cast at a block, the spell shatters all glass and glass pane blocks within a radius
 * determined by the caster's experience. The radius scales with skill level up to a maximum of
 * {@value #maxRadius} blocks. Non-glass blocks are unaffected.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Finestra_spell">Harry Potter Wiki - Finestra</a>
 */
public final class FINESTRA extends O2Spell {
    /**
     * The maximum radius in blocks for the glass-breaking effect.
     */
    private static final int maxRadius = 10;

    /**
     * Materials considered glass for the purposes of this spell. Populated in {@link #doInitSpell()}.
     */
    private final List<Material> glass = new ArrayList<>();

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public FINESTRA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.FINESTRA;
        branch = O2MagicBranch.CHARMS;

        text = "Breaks glass in a radius.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public FINESTRA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.FINESTRA;
        branch = O2MagicBranch.CHARMS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }

    /**
     * Populate the glass material list with all glass and glass pane types.
     */
    @Override
    void doInitSpell() {
        glass.add(Material.GLASS);
        glass.add(Material.GLASS_PANE);
        for (Material material : Material.values()) {
            if (material.toString().endsWith("_GLASS_PANE") || material.toString().endsWith("_GLASS"))
                glass.add(material);
        }
    }

    /**
     * Break all glass blocks within a skill-scaled radius of the target.
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitTarget())
            return;

        kill();

        Block target = getTargetBlock();
        if (target == null) {
            common.printDebugMessage("FINESTRA.doCheckEffect: target block is null", null, null, false);
            return;
        }

        double radius = usesModifier / 10;
        if (radius < 1)
            radius = 1;
        else if (radius > maxRadius)
            radius = maxRadius;

        for (Block nearbyBlock : BlockCommon.getBlocksInRadius(location, radius)) {
            if (glass.contains(nearbyBlock.getType()))
                nearbyBlock.breakNaturally();
        }
    }

    /**
     * Get the maximum radius for the glass-breaking effect.
     *
     * @return the maximum radius in blocks
     */
    public int getMaxRadius() {
        return maxRadius;
    }
}