package net.pottercraft.ollivanders2.effect;

import java.util.ArrayList;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Replaces the affected player's chat messages with random gibberish (1 to {@link #maxWords} nonsense words), on an
 * {@link #affectPercent} chance per message. Detectable via Informous.
 *
 * @author Azami7
 */
public class BABBLING extends O2Effect {
    /**
     * Percent chance (0-100) that a given chat message is replaced with gibberish.
     */
    int affectPercent = 100;

    /**
     * Nonsense words drawn from at random to build babbling messages.
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
     */
    int maxWords = 5;

    /**
     * Constructor
     *
     * @param plugin      a callback to the MC plugin
     * @param duration    the duration of the babbling effect in game ticks
     * @param isPermanent is this effect permanent (does not age)
     * @param pid         the unique ID of the player to affect with babbling
     */
    public BABBLING(@NotNull Ollivanders2 plugin, int duration, boolean isPermanent, @NotNull UUID pid) {
        super(plugin, duration, isPermanent, pid);

        effectType = O2EffectType.BABBLING;
        checkDurationBounds();

        informousText = "is unable to speak clearly";
    }

    @Override
    public void checkEffect() {
        age(1);
    }

    /**
     * Build a babbling message of 1 to {@link #maxWords} random nonsense words.
     *
     * @return a space-separated string of nonsense words
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

    @Override
    public void doRemove() {
    }

    /**
     * Replace the target's chat message with gibberish, subject to the {@link #affectPercent} chance.
     */
    @Override
    void doOnAsyncPlayerChatEvent(@NotNull AsyncPlayerChatEvent event) {
        if (!event.getPlayer().getUniqueId().equals(targetID))
            return;

        int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);

        if (rand > affectPercent)
            return;

        String message = event.getMessage();
        String newMessage = getNonsense();

        event.setMessage(newMessage);

        common.printDebugMessage("Changed " + event.getPlayer().getDisplayName() + "'s chat from \"" + message + "\" to \"" + newMessage + "\"", null, null, false);
    }
}