package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Fire-Making Charm: sets fire to the block and any entities at the target location.
 *
 * @see IncendioBase
 * @see <a href="https://harrypotter.fandom.com/wiki/Fire-Making_Spell">Harry Potter Wiki - Fire-Making Spell</a>
 */
public final class INCENDIO extends IncendioBase {
    private static final int maxBurnDurationConfig = Ollivanders2Common.ticksPerSecond * 10;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public INCENDIO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.INCENDIO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Fire-Making Charm");
            add("The ability to produce fire with the flick or a wand can be dangerous to your fellow students (and worse, your books).");
            add("From lighting a warm hearth to igniting a Christmas pudding, the Fire-Making Spell is always useful around the wizarding household.");
        }};

        text = "Will set alight blocks and entities it passes by.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public INCENDIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.INCENDIO;
        branch = O2MagicBranch.CHARMS;

        strafe = false;
        entityRadius = 1;
        blockRadius = 1;
        durationModifier = 1;

        maxBurnDuration = maxBurnDurationConfig;

        initSpell();
    }
}