package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.divination.O2DivinationType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Centaur divination spell, the most accurate form of divination but the most difficult to learn.
 * <p>
 * Centaurs read the future in the movements of the celestial bodies over centuries of observation. This spell produces
 * a {@link O2DivinationType#CENTAUR_DIVINATION} prophecy about its target and, when mastered, is the most accurate
 * divination.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Divination#Centaur_divination">Harry Potter Wiki - Centaur divination</a>
 */
public class MANTEIA_KENTAVROS extends Divination {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public MANTEIA_KENTAVROS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.MANTEIA_KENTAVROS;
        divinationType = O2DivinationType.CENTAUR_DIVINATION;

        flavorText = new ArrayList<>() {{
            add("\"Trivial hurts, tiny human accidents, these are of no more significance than the scurryings of ants to the wide universe, and are unaffected by planetary movements.\" -Firenze");
            add("\"I know that you have learned the names of the planets and their moons in Astronomy, and that you have mapped the stars’ progress through the heavens. Centaurs have unravelled the mysteries of these movements over centuries. Our findings teach us that the future may be glimpsed in the sky above us.\" -Firenze");
            add("\"I am here to explain the wisdom of centaurs, which is impersonal and impartial.\" -Firenze");
        }};

        text = "Centaurs have spent hundreds of years observing celestial movements, unlocking their secrets and thereby learning to see signs. The ancient knowledge practice Centaur divination, when mastered, is the most accurate form of divination.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public MANTEIA_KENTAVROS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.MANTEIA_KENTAVROS;
        divinationType = O2DivinationType.CENTAUR_DIVINATION;

        initSpell();
    }
}
