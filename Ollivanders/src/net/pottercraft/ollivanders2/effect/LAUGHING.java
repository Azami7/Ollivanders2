package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Causes a player to laugh uncontrollably
 *
 * @since 2.21
 * @author Azami7
 */
public class LAUGHING extends BABBLING {
    /**
     * Constructor
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the effect
     * @param pid      the ID of the player this effect acts on
     */
    public LAUGHING(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.LAUGHING;
        informousText = "cannot stop laughing";

        divinationText.add("must beware the hand unseen, the agent of giddy doom");
        divinationText.add("is marked not by lightning but by feather and folly");
        divinationText.add("shall fall victim to uncontrollable mirth");

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
