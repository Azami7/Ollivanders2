package net.pottercraft.ollivanders2.effect;

import java.util.ArrayList;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Effect that forces a player to speak gibberish instead of coherent sentences.
 *
 * <p>The BABBLING effect causes affected players to be unable to speak clearly, replacing their chat
 * messages with random nonsense words. When the effect is active, on each chat message the player sends,
 * there is an affectPercent% chance (default 100%) that the message will be replaced with gibberish
 * consisting of 1-5 randomly selected nonsense words from an internal dictionary.</p>
 *
 * <p>Mechanism:</p>
 * <ul>
 * <li>On each chat message: generates a random value 0-99</li>
 * <li>If random value is less than affectPercent, the message is replaced with nonsense</li>
 * <li>Nonsense messages consist of 1 to maxWords (default 5) randomly selected gibberish words</li>
 * <li>Dictionary contains 40+ nonsense words from Harry Potter and literature sources</li>
 * <li>Effect is detectable by information spells (Informous)</li>
 * <li>Can be permanent (won't age) or temporary (will expire)</li>
 * </ul>
 *
 * @author Azami7
 */
public class BABBLING extends O2Effect {
    /**
     * The probability threshold (0-100) that determines what percentage of chat messages are replaced.
     *
     * <p>On each chat message, a random value 0-99 is generated. If this value is less than affectPercent,
     * the message is replaced with nonsense. Examples:
     * <ul>
     * <li>affectPercent = 100: all chat messages are replaced (100% chance)</li>
     * <li>affectPercent = 50: approximately 50% of chat messages are replaced</li>
     * <li>affectPercent = 10: approximately 10% of chat messages are replaced</li>
     * </ul>
     * </p>
     */
    int affectPercent = 100;

    /**
     * Collection of 40+ nonsense words used to generate babbling speech.
     *
     * <p>This dictionary contains nonsense words from the Harry Potter universe (mimble, oddment, nitwit),
     * classic literature (jabberwock, slithy toves), and whimsical language sources (flummadiddle, razzmatazz).
     * When generating a babbling message, 1 to maxWords random words are selected from this dictionary
     * and concatenated with spaces to form the replacement chat message.</p>
     */
    public ArrayList<String> dictionary = new ArrayList<>() {{
        add("mimble");
        add("wimble");
        add("oddment");
        add("tweak");
        add("nitwit");
        add("blubber");
        add("buzzlepop");
        add("ackamarackus");
        add("crodsquinkled");
        add("fuddle");
        add("woozle");
        add("hibber-gibber");
        add("tootle");
        add("tarradiddle");
        add("skittles");
        add("schmegeggy");
        add("bletherskate");
        add("flimflam");
        add("flapdoodle");
        add("razzmatazz");
        add("linsey-woolsey");
        add("pussel-skwonk");
        add("slithy toves");
        add("twas brillig");
        add("rannygazoo");
        add("quatsch");
        add("jiggery-pokery");
        add("bandersnatch");
        add("snicker-snack");
        add("peskipiksi");
        add("pesternomi");
        add("piddle");
        add("clatfart");
        add("phonus-bolonus");
        add("jabberwock");
        add("nugament");
        add("narrischkeit");
        add("mumbo-jumbo");
        add("flummadiddle");
        add("me me big boy");
    }};

    /**
     * The maximum number of nonsense words in a single babbling message.
     *
     * <p>When generating a babbling replacement for a chat message, a random number of words between 1
     * and maxWords (inclusive) is selected from the dictionary. For example, with maxWords = 5, a babbling
     * message might be "mimble", "oddment flummadiddle", or "jabberwock razzmatazz woozle" etc.</p>
     */
    int maxWords = 5;

    /**
     * Constructor for creating a babbling effect.
     *
     * <p>Creates an effect that forces a player to speak gibberish by replacing their chat messages with
     * nonsense. Sets both information and mind-reading detection text to "is unable to speak clearly".</p>
     *
     * @param plugin   a callback to the MC plugin
     * @param duration the duration of the babbling effect in game ticks
     * @param pid      the unique ID of the player to affect with babbling
     */
    public BABBLING(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid) {
        super(plugin, duration, pid);

        effectType = O2EffectType.BABBLING;
        informousText = "is unable to speak clearly";
    }

    /**
     * Age the babbling effect to track its remaining duration.
     *
     * <p>Called each game tick to decrement the effect's duration. The actual babbling message replacement
     * is handled by the event handler method (doOnAsyncPlayerChatEvent). If the effect is permanent, it
     * does not age and will persist indefinitely.</p>
     */
    @Override
    public void checkEffect() {
        if (!permanent) {
            age(1);
        }
    }

    /**
     * Generate a random string of nonsense words for chat message replacement.
     *
     * <p>Selects a random number of words between 1 and maxWords (inclusive) from the dictionary
     * and concatenates them with spaces. Each invocation produces a different babbling message.</p>
     *
     * @return a string of random nonsense words (1 to maxWords in length)
     */
    private String getNonsense() {
        StringBuilder nonsense = new StringBuilder();

        int numWords = Math.abs(Ollivanders2Common.random.nextInt() % maxWords) + 1;

        for (int i = 0; i < numWords; i++) {
            String random = dictionary.get(Math.abs(Ollivanders2Common.random.nextInt()) % dictionary.size());

            if (!nonsense.isEmpty())
                nonsense.append(" ");

            nonsense.append(random);
        }

        return nonsense.toString();
    }

    /**
     * Perform cleanup when the babbling effect is removed.
     *
     * <p>The default implementation does nothing, as the babbling effect has no state to clean up.
     * When removed, the player is simply allowed to speak normally again.</p>
     */
    @Override
    public void doRemove() {
    }

    /**
     * Replace the player's chat message with babbling nonsense based on probability.
     *
     * <p>Called when the player sends a chat message. This method applies the affectPercent probability
     * threshold to determine whether the message is replaced:
     * <ol>
     * <li>Generates a random value 0-99</li>
     * <li>If random value is less than affectPercent, replaces the message with nonsense via getNonsense()</li>
     * <li>Otherwise, allows the original message to pass through unchanged</li>
     * </ol>
     * </p>
     *
     * @param event the player chat event to potentially modify
     */
    @Override
    void doOnAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event) {
        // check to see if we will replace this chat with babbling chat
        // generate a random number between 0-99
        int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);

        if (rand > affectPercent)
            return;

        // replace the chat message with the babbling message
        String message = event.getMessage();
        String newMessage = getNonsense();

        event.setMessage(newMessage);

        common.printDebugMessage("Changed " + event.getPlayer().getDisplayName() + "'s chat from \"" + message + "\" to \"" + newMessage + "\"", null, null, false);
    }
}