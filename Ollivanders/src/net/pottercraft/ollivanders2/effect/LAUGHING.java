package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Forces a player to laugh uncontrollably: a {@link BABBLING} variant that swaps the babble dictionary for laugh
 * exclamations. Inherits BABBLING's periodic damage and forced speech. Detectable via Informous.
 *
 * @author Azami7
 * @see BABBLING
 */
public class LAUGHING extends BABBLING {
    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the laughing effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to afflict with uncontrollable laughing
     */
    public LAUGHING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.LAUGHING;
        checkDurationBounds();

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
