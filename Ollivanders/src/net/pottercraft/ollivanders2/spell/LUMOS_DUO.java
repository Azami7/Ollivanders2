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
 * Creates a line of glowstone blocks that dissipate after a duration based on caster skill.
 *
 * <p><strong>Behavior:</strong></p>
 * <ul>
 * <li>Projectile travels forward, ignoring air blocks</li>
 * <li>Line creation begins after 2 ticks to avoid blocks too close to caster</li>
 * <li>Creates up to 5 glowstone blocks along projectile path</li>
 * <li>Blocks remain for a duration scaled by caster experience (60-300 seconds)</li>
 * <li>Blocks automatically revert to air when duration expires or spell is killed</li>
 * </ul>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Lumos_Duo">https://harrypotter.fandom.com/wiki/Lumos_Duo</a>
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
     * clamped between {@link #minDuration} and {@link #maxDuration}.
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
     * Calculates spell duration based on caster skill level.
     *
     * <p>Duration is calculated as: {@code usesModifier * 2 * ticksPerSecond}, clamped to
     * the range [{@link #minDuration}, {@link #maxDuration}]. At spell mastery (usesModifier=100),
     * duration is 200 seconds.</p>
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
     * Updates spell state each game tick.
     *
     * <p><strong>Phases:</strong></p>
     * <ol>
     * <li><strong>Delay:</strong> For first 2 ticks, no action (avoids blocks too close to caster)</li>
     * <li><strong>Line Creation:</strong> While projectile is moving and line length &lt; {@link #maxLineLength},
     * place glowstone blocks at current location</li>
     * <li><strong>Countdown:</strong> After projectile stops, count down until duration expires</li>
     * </ol>
     */
    @Override
    protected void doCheckEffect() {
        if (getAge() < 2)
            return;

        if (!hasHitTarget()) {
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
     * Reverts all glowstone blocks created by this spell back to air.
     *
     * <p>Called when the spell is terminated, either by duration expiration or manual kill.</p>
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