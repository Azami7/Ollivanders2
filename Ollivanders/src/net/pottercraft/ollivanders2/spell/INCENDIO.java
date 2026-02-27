package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The basic fire-making charm that ignites blocks, entities, and items.
 *
 * <p>INCENDIO is a fundamental fire spell that sets fire to a single target location and any entities/items
 * in that immediate area. It is a single-target spell (non-strafe) with a 1-block effective radius.</p>
 *
 * <p>Spell characteristics:
 * <ul>
 * <li><strong>Target:</strong> Single block - non-strafe behavior</li>
 * <li><strong>Block Radius:</strong> 1 block - only the directly hit block</li>
 * <li><strong>Entity Radius:</strong> 1 block - only entities at the target location</li>
 * <li><strong>Duration Modifier:</strong> 1x - baseline burn duration</li>
 * <li><strong>Max Burn Duration:</strong> 10 seconds (200 ticks)</li>
 * </ul>
 *
 * <p>The spell will set blocks on fire and apply burn effects to living entities and items within the
 * 1-block radius, with burn duration scaled by the caster's spell proficiency.</p>
 *
 * @see IncendioSuper
 * @see <a href="https://harrypotter.fandom.com/wiki/Fire-Making_Spell">https://harrypotter.fandom.com/wiki/Fire-Making_Spell</a>
 */
public final class INCENDIO extends IncendioSuper {
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