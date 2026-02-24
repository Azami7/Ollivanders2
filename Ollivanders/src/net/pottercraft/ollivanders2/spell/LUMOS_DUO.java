package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Creates a line of glowstone that goes away after a time.
 *
 * @author lownes
 * @author Azami7
 * @see <a href = "https://harrypotter.fandom.com/wiki/Lumos_Duo">https://harrypotter.fandom.com/wiki/Lumos_Duo</a>
 */
public final class LUMOS_DUO extends O2Spell {
    private final List<Block> line = new ArrayList<>();

    private int lineLength = 0;
    private static final int maxLineLength = 5;

    /**
     * If this is not permanent, how long it should last. Default is 15 seconds.
     */
    int spellDuration = Ollivanders2Common.ticksPerSecond * 15;

    /**
     * Max duration of this spell. Default is 5 minutes.
     */
    int maxDuration = Ollivanders2Common.ticksPerSecond * 300;
    int minDuration = Ollivanders2Common.ticksPerSecond * 60;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public LUMOS_DUO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.LUMOS_DUO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("A variation of the Wand-Lighting Charm.");
        }};

        text = "Creates a stream of glowstone to light your way.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public LUMOS_DUO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.LUMOS_DUO;
        branch = O2MagicBranch.CHARMS;

        // pass-through materials
        projectilePassThrough.clear();
        projectilePassThrough.add(Material.AIR);

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }

    @Override
    void doInitSpell() {
        spellDuration = (int) ((usesModifier / 10) * Ollivanders2Common.ticksPerSecond);
        if (spellDuration < maxDuration)
            spellDuration = maxDuration;
        else if (spellDuration < minDuration)
            spellDuration = minDuration;
    }

    @Override
    protected void doCheckEffect() {
        if (hasHitTarget()) {
            kill();
            return;
        }

        // wait 2 before starting to create the line
        if (getLifeTicks() < 2)
            return;

        if (lineLength < maxLineLength) {
            Block curBlock = location.getBlock();

            if (curBlock.getType() != Material.AIR) {
                kill();
                return;
            }

            curBlock.setType(Material.GLOWSTONE);
            line.add(curBlock);

            lineLength = lineLength + 1;
        }
        else {
            // check time to live on the spell
            if (spellDuration <= 0)
                // spell duration is up, kill the spell
                kill();
            else
                spellDuration = spellDuration - 1;
        }
    }

    @Override
    protected void revert() {
        // change the blocks back to air
        for (Block block : line)
            block.setType(Material.AIR);

        line.clear();
    }
}