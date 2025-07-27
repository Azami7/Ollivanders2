package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Dancing Feet Spell - Tarantallegra - is a charm that makes a target's legs spasm wildly out of control, making it
 * appear as though they are dancing.
 *
 * @since 2.21
 * @author Azami7
 * @see <a href = "https://harrypotter.fandom.com/wiki/Dancing_Feet_Spell">https://harrypotter.fandom.com/wiki/Dancing_Feet_Spell</a>
 */
public class TARANTALLEGRA extends AddO2Effect {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public TARANTALLEGRA(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.DARK_ARTS;
        spellType = O2SpellType.TARANTALLEGRA;

        flavorText = new ArrayList<>() {{
            add("\"The 'Dancing Feet' spell has its origins in ancient Italy, but is best remembered for its improper usage by Warlock Zaccaria Innocenti who is credited with conjuring a 'dance' within Mt. Vesuvius in 79 AD.\"");
            add("\"Malfoy pointed his wand at Harry's knees, choked, 'Tarantallegra!' and the next second Harry's legs began to jerk around out of his control in a kind of quickstep.\"");
            add("The Dancing Feet Spell");
        }};

        text = "The Dancing Feet Spell - Tarantallegra - is a charm that makes a target's legs spasm wildly out of control, making it appear as though they are dancing.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public TARANTALLEGRA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.CHARMS;
        spellType = O2SpellType.TARANTALLEGRA;

        effectsToAdd.add(O2EffectType.DANCING_FEET);

        // duration limits
        minDurationInSeconds = 10;
        maxDurationInSeconds = 180;

        // pass-through materials
        projectilePassThrough.remove(Material.WATER);

        initSpell();
    }
}
