package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Uncontrollable laughing effect that forces a player to repeatedly laugh.
 *
 * <p>LAUGHING is a specialized variant of the {@link BABBLING} effect that forces the affected
 * player to laugh uncontrollably instead of speaking babbling nonsense. Like BABBLING, this effect
 * applies periodic damage and forces the player to speak messages at random intervals. However,
 * LAUGHING uses a custom dictionary of laugh exclamations (16 different laugh variations) instead of
 * generic babbling words, and is configured with a 50% probability threshold for triggering laughs.
 * The effect displays continuous uncontrollable laughter, creating a humorous but harmful status
 * condition.</p>
 *
 * <p>Mechanism (inherited from BABBLING):</p>
 * <ul>
 * <li>Periodic damage applied every 3 seconds with clamped magnitude (0.5-10 health)</li>
 * <li>Forced speech output at random intervals (1-5 custom laugh messages per damage cycle)</li>
 * <li>50% probability threshold for triggering laugh messages each cycle</li>
 * <li>Custom laugh dictionary with 16 humorous laugh variations</li>
 * <li>Detectable by information spells (Informous)</li>
 * <li>Detection text: "cannot stop laughing"</li>
 * </ul>
 *
 * @author Azami7
 * @see BABBLING for the base periodic speech and damage mechanism
 */
public class LAUGHING extends BABBLING {
    /**
     * Constructor for creating an uncontrollable laughing effect.
     *
     * <p>Creates an effect that forces the target player to laugh uncontrollably by outputting
     * laugh messages periodically. Extends the parent BABBLING class with a custom laugh dictionary
     * (16 laugh variations ranging from simple "hahahaha" to elaborate Wizarding World references)
     * and is configured with 50% probability threshold (affectPercent) and maximum of 1 laugh message
     * (maxWords) per speech cycle. Inherits all damage mechanics from BABBLING: periodic damage
     * every 3 seconds with clamped 0.5-10 damage range.</p>
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the laughing effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to afflict with uncontrollable laughing
     */
    public LAUGHING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.LAUGHING;
        informousText = "cannot stop laughing";

        dictionary = new ArrayList<>() {{
            add("hahahahahahahaha");
            add("hehehehe");
            add("STAAAHP I can’t breathe!!!");
            add("omg who gave you a feather wand");
            add("This is TORTURE pls mercy!");
            add("dies of laughter, send help");
            add("Not the feet!! NOT THE FEET!!!");
            add("okokokok I surrender!! Just stop ticklingggg");
            add("I’m going to pass out from giggling pls");
            add("rolling on floor laughing - literally");
            add("Merlin’s beard! I can’t breathe—STOP the tickle charm!");
            add("Help! I’m under ATTACK—Unforgivable Giggles!");
            add("This must be dark magic… no one should LAUGH this hard");
            add("JUST STOP TICKLING ME");
            add("Someone call Madam Pomfrey—I think I’ve fractured a rib laughing");
            add("This isn’t dueling, it’s DIABOLICAL—who gave Peeves a feather?!");
            add("(incoherent giggling noises) Tell Dumbledore… I fought bravely…");
        }};

        maxWords = 1;
        affectPercent = 50;
    }
}
