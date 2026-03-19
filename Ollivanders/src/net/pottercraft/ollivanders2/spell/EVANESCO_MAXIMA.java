package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Permanently vanishes non-living entities. Unlike EVANESCO, the vanished entity is not restored
 * when the spell ends.
 *
 * @author Azami7
 * @see EVANESCO
 * @see <a href="https://harrypotter.fandom.com/wiki/Vanishing_Spell">https://harrypotter.fandom.com/wiki/Vanishing_Spell</a>
 */
public final class EVANESCO_MAXIMA extends EVANESCO {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public EVANESCO_MAXIMA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.EVANESCO_MAXIMA;
        branch = O2MagicBranch.TRANSFIGURATION;

        flavorText = new ArrayList<>() {{
            add("The Vanishing Spell");
            add("The contents of Harry’s potion vanished; he was left standing foolishly beside an empty cauldron.");
        }};

        text = "Evanesco Maxima will permanently vanish any non-living entity.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public EVANESCO_MAXIMA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.EVANESCO_MAXIMA;
        branch = O2MagicBranch.TRANSFIGURATION;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.USE);
            worldGuardFlags.add(Flags.BUILD);
        }

        permanent = true;

        initSpell();
    }
}
