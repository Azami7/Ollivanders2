package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Tickling Hex - Titillando - also known as the Tickling Spell, is a hex that tickled and subsequently weakened the
 * target.
 *
 * @author Azami7
 * @see <a href = "https://harrypotter.fandom.com/wiki/Tickling_Hex">https://harrypotter.fandom.com/wiki/Tickling_Hex</a>
 * @since 2.21
 */
public class TITILLANDO extends AddO2Effect {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public TITILLANDO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.TITILLANDO;
        branch = O2MagicBranch.DARK_ARTS;

        flavorText = new ArrayList<>() {{
            add("The Tickling Hex");
        }};

        text = "The Tickling Hex weakens the target and causes them to buckle with laughter from uncontrollable tickling.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public TITILLANDO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.TITILLANDO;
        branch = O2MagicBranch.CHARMS;

        // effect
        effectsToAdd.add(O2EffectType.LAUGHING);
        effectsToAdd.add(O2EffectType.TICKLING);
        effectsToAdd.add(O2EffectType.WEAKNESS);

        maxDurationInSeconds = 180;

        // pass-through materials
        projectilePassThrough.remove(Material.WATER);

        initSpell();
    }
}
