package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Creates a line of up to {@link #maxLineLength} glowstone blocks along the projectile's path that revert to air
 * after a skill-scaled duration.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Lumos_Duo">Harry Potter Wiki - Lumos Duo</a>
 */
public final class LUMOS_DUO extends O2Spell {
    /**
     * Length of the line drawn so far by the spell.
     */
    private int lineLength = 0;

    /**
     * The maximum length the line of glowstone can be
     */
    public static final int maxLineLength = 5;

    /**
     * Max duration that the glowstones will remain.
     */
    public static final int maxDuration = Ollivanders2Common.ticksPerSecond * 300;

    /**
     * Min duration that the glowstones will remain.
     */
    public static final int minDuration = Ollivanders2Common.ticksPerSecond * 60;

    /**
     * How long the glowstone line will remain. Set by {@link #doInitSpell()} based on caster skill,
     * limited between {@link #minDuration} and {@link #maxDuration}.
     */
    private int spellDuration = minDuration;

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

    /**
     * Set how long the glowstone line lasts from the caster's skill, limited to [{@link #minDuration},
     * {@link #maxDuration}].
     */
    @Override
    void doInitSpell() {
        spellDuration = (int) ((usesModifier * 2) * Ollivanders2Common.ticksPerSecond);
        if (spellDuration > maxDuration)
            spellDuration = maxDuration;
        else if (spellDuration < minDuration)
            spellDuration = minDuration;
    }

    /**
     * Lay a glowstone block at the projectile's position each tick until the line reaches {@link #maxLineLength},
     * then keep the line until the spell's duration expires. The first two ticks are skipped so blocks are not placed
     * on top of the caster.
     */
    @Override
    protected void doCheckEffect() {
        if (getAge() < 2)
            return;

        if (!hasHitBlock()) {
            if (lineLength < maxLineLength) {
                Block currentBlock = location.getBlock();

                if (!Ollivanders2API.getBlocks().isTemporarilyChangedBlock(currentBlock)) {
                    Ollivanders2API.getBlocks().addTemporarilyChangedBlock(currentBlock, this);
                    currentBlock.setType(Material.GLOWSTONE);
                }

                lineLength = lineLength + 1;

                if (lineLength == maxLineLength)
                    stopProjectile();
            }
            else {
                stopProjectile();
            }
        }
        else {
            if (getAge() >= spellDuration)
                kill();
        }
    }

    /**
     * Revert all glowstone blocks created by this spell back to air.
     */
    @Override
    protected void revert() {
        Ollivanders2API.getBlocks().revertTemporarilyChangedBlocksBy(this);
    }

    /**
     * Gets the total duration for which glowstone blocks will remain.
     *
     * @return the spell duration in game ticks
     */
    public int getSpellDuration() {
        return spellDuration;
    }
}