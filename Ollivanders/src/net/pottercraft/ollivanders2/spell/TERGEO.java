package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Siphoning Spell (Tergeo) — temporarily siphons away water by transfiguring water blocks to air within a
 * radius around the impact point. The affected water reverts when the transfiguration expires. Both the radius and
 * the duration scale with the caster's skill.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Wiping_Spell">Wiping Spell on Harry Potter Wiki</a>
 */
public final class TERGEO extends BlockTransfiguration {
    /**
     * Minimum effect radius, in blocks, regardless of caster skill.
     */
    private static final int minRadiusConfig = 1;

    /**
     * Maximum effect radius, in blocks, regardless of caster skill.
     */
    private static final int maxRadiusConfig = 20;

    /**
     * Minimum duration, in ticks, that siphoned water stays as air before reverting.
     */
    private static final int minDurationConfig = 15 * Ollivanders2Common.ticksPerSecond;

    /**
     * Maximum duration, in ticks, that siphoned water stays as air before reverting.
     */
    private static final int maxDurationConfig = 10 * Ollivanders2Common.ticksPerMinute;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public TERGEO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.TERGEO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Siphoning Spell");
            add("The wand siphoned off most of the grease. Looking rather pleased with himself, Ron handed the slightly smoking handkerchief to Hermione.");
            add("She raised her wand, said “Tergeo!” and siphoned off the dried blood.");
        }};

        text = "Tergeo will siphon water where it hits.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public TERGEO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.TERGEO;
        branch = O2MagicBranch.CHARMS;

        permanent = false;
        minEffectRadius = minRadiusConfig;
        maxEffectRadius = maxRadiusConfig;
        effectRadiusModifier = 0.2; // 20% of usesModifier
        minDuration = minDurationConfig;
        maxDuration = maxDurationConfig;
        durationModifier = 0.5; // 50% of usesModifier

        // pass-through
        projectilePassThrough.remove(Material.WATER);

        // set materials that can be transfigured by this spell
        materialAllowList.add(Material.WATER);

        // what type blocks transfigure in to for this spell
        transfigureType = Material.AIR;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }
}