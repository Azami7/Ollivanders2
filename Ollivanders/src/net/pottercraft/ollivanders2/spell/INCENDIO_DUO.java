package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * A stronger fire-making charm that ignites multiple targets in a larger radius.
 *
 * <p>INCENDIO_DUO is an enhanced version of INCENDIO with increased area of effect and duration.
 * It uses strafe mode to affect all blocks and entities within a 2-block radius, making it effective
 * against multiple targets simultaneously.</p>
 *
 * <p>Spell characteristics:
 * <ul>
 * <li><strong>Target:</strong> Multiple targets - strafe behavior enabled</li>
 * <li><strong>Block Radius:</strong> 2 blocks - affects blocks in a small area</li>
 * <li><strong>Entity Radius:</strong> 2 blocks - affects multiple entities in the area</li>
 * <li><strong>Duration Modifier:</strong> 2x - double the baseline burn duration</li>
 * <li><strong>Max Burn Duration:</strong> 20 seconds (400 ticks)</li>
 * </ul>
 *
 * <p>The spell will set all blocks and entities on fire within the 2-block radius, with burn duration
 * scaled by the caster's spell proficiency and doubled compared to basic INCENDIO.</p>
 *
 * @see IncendioSuper
 * @see <a href="https://harrypotter.fandom.com/wiki/Incendio_Duo_Spell">https://harrypotter.fandom.com/wiki/Incendio_Duo_Spell</a>
 */
public final class INCENDIO_DUO extends IncendioSuper {
    private static final int maxBurnDurationConfig = Ollivanders2Common.ticksPerSecond * 20;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public INCENDIO_DUO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.INCENDIO_DUO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("A Stronger Fire-Making Charm");
        }};

        text = "Incendio Duo will burn blocks and entities it passes by. It's radius is twice that of Incendio and it's duration half.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public INCENDIO_DUO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.INCENDIO_DUO;
        branch = O2MagicBranch.CHARMS;

        strafe = true;
        blockRadius = 2;
        entityRadius = 2;
        durationModifier = 2;

        maxBurnDuration = maxBurnDurationConfig;

        initSpell();
    }
}