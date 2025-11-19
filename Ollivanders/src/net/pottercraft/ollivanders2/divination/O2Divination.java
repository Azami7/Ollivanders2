package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.common.TimeCommon;
import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Abstract parent class for all divination spell implementations in Ollivanders2.
 * <p>
 * Divination is a branch of magic that involves attempting to foresee the future or gather insights into past,
 * present, and future events. This class provides the framework for creating randomized prophecies based on the
 * specific divination method (astrology, cartomancy, crystal ball, etc.). Each divination subclass defines its own
 * prophecy prefixes and accuracy level, which are combined with randomly selected effects to create a complete prophecy.
 * </p>
 * <p>
 * The prophecy generation process ({@link #divine()}) follows a structured workflow:
 * </p>
 * <ol>
 * <li>Calculate accuracy based on prophet's experience level and the divination method's maximum accuracy cap</li>
 * <li>Select a random magical effect from the available divination effects pool</li>
 * <li>Select a prophecy prefix from the subclass-specific list (method-specific phrasing)</li>
 * <li>Determine the time of day and duration until the prophecy takes effect (1-4 days, varies by time)</li>
 * <li>Construct a complete prophecy message combining all elements</li>
 * <li>Create an O2Prophecy record and schedule the effect to occur at the designated time</li>
 * </ol>
 * <p>
 * Subclasses must define:
 * </p>
 * <ul>
 * <li>{@code divinationType} - The O2DivinationType constant for this divination method</li>
 * <li>{@code maxAccuracy} - The maximum accuracy percentage this divination method can achieve (caps prophet experience)</li>
 * <li>{@code prophecyPrefix} array - Method-specific prefixes (e.g., "Due to the influence of Mars," for astrology)</li>
 * </ul>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Divination">Harry Potter Wiki - Divination</a>
 */
public abstract class O2Divination {
    /**
     * Reference to the plugin for accessing configuration and logging
     */
    final Ollivanders2 p;

    /**
     * The type of divination this instance represents (e.g., ASTROLOGY, CARTOMANCY).
     * Set by subclass constructors to identify the specific divination method.
     */
    O2DivinationType divinationType = O2DivinationType.ASTROLOGY;

    /**
     * The number of game ticks in a minecraft "day".
     */
    static final int ticksPerDay = 24000;

    /**
     * The maximum accuracy percentage this divination method can achieve.
     * Prophet experience is capped by this value, preventing high-level prophets from exceeding the method's inherent limits.
     * Different divination methods have different caps reflecting their reliability:
     * astrology=20, cartomancy=25, crystal ball=30, ovomancy=40, tarot=35, centaur=80, tasseomancy=20.
     * Default: 10 (for base class).
     */
    int maxAccuracy = 10;

    /**
     * The maximum number of days in the future a prophecy can be scheduled.
     * Value is read from {@link Ollivanders2#divinationMaxDays} configuration.
     * Determines the range for prophecy duration calculation (1-4 days, varies by configuration).
     */
    int maxDelayDays = Ollivanders2.divinationMaxDays;

    /**
     * The player who is the subject of this divination prophecy.
     * The prophecy's effects will be applied to this player when the scheduled time arrives.
     */
    Player target;

    /**
     * The player performing/casting the divination spell.
     * Their experience level with this divination method affects the prophecy's accuracy.
     */
    Player prophet;

    /**
     * The experience level of the prophet with this specific divination method.
     * Affects prophecy accuracy calculation: accuracy = experience / 2, capped by {@code maxAccuracy}.
     * Higher experience = 0.5% accuracy per level (up to the method's maximum).
     */
    int experience;

    /**
     * The pool of method-specific prophecy prefixes for this divination type.
     * Subclasses populate this array with thematic phrases (e.g., "Due to the influence of Mars" for astrology).
     * A random prefix is selected and prepended to each generated prophecy to add method-specific flavor.
     * Default: includes a generic fallback prefix "The portents and omens say that"
     */
    ArrayList<String> prophecyPrefix = new ArrayList<>();

    /**
     * Static map of all possible magical effects that divination prophecies can trigger and the prophecy messages for each.
     * Each O2EffectType maps to an array of possible prophecy predicates (e.g., "shall be cursed", "will come to harm").
     * When a prophecy is created, a random effect type is selected, and then a random message from that effect's array
     * is chosen to create the prophetic text. This separation allows divination-specific phrasing instead of effect class descriptions.
     * Includes different effect types ranging from beneficial (HEAL, LUCK, WEALTH) to harmful (POISON, HARM, WEAKNESS).
     * A random effect from this pool is selected for each prophecy and will be applied to the target player
     * when the scheduled prophecy time arrives.
     */
    static final HashMap<O2EffectType, String[]> divinationEffects = new HashMap<>() {{
        put(O2EffectType.AGGRESSION, new String[]{"will suffer from an insatiable rage", "will succomb to a primal fear", "shall be afflicted in the mind", "shall lose their mind to insanity", "will be possessed by a demon spirit", "shall be cursed"});
        put(O2EffectType.BABBLING, new String[]{"shall be afflicted in the mind", "shall lose their mind to insanity", "will begin to speak in tongues", "will be possessed by a demon spirit"});
        put(O2EffectType.BLINDNESS, new String[]{"shall be cursed", "shall be afflicted in the mind", "will become unable to see", "will be struck by a terrible affliction", "will lose the light"});
        put(O2EffectType.BURNING, new String[]{"shall be cursed", "will be consumed by fire", "will burn from within"});
        put(O2EffectType.CONFUSION, new String[]{"shall be cursed", "will be afflicted in the mind", "will be struck by a terrible affliction", "will suffer a mental breakdown"});
        put(O2EffectType.HARM, new String[]{"shall be struck by a terrible affliction", "will come to harm", "shall be cursed", "will be develop a terrible illness"});
        put(O2EffectType.HEAL, new String[]{"will feel rejuvenated", "will be blessed by fortune", "shall be blessed"});
        put(O2EffectType.HEALTH_BOOST, new String[]{"will become stonger", "will be blessed by fortune", "shall be blessed", "will rise to become more powerful"});
        put(O2EffectType.HUNGER, new String[]{"shall be struck by a terrible affliction", "will starve", "shall be cursed", "will become insatiable"});
        put(O2EffectType.IMMOBILIZE, new String[]{"will be possessed by a demon spirit", "will succomb to a primal fear", "shall become as if frozen", "shall be struck by a terrible affliction", "shall be cursed"});
        put(O2EffectType.LAUGHING, new String[]{"must beware the hand unseen, the agent of giddy doom", "is marked not by lightning but by feather and folly", "shall fall victim to uncontrollable mirth"});
        put(O2EffectType.LUCK, new String[]{"will be blessed by fortune", "will have unnatural luck", "shall find success in everything they do", "will become infallible"});
        put(O2EffectType.MUTED_SPEECH, new String[]{"will be struck mute", "shall lose their mind to insanity", "shall be afflicted in the mind", "will fall silent"});
        put(O2EffectType.POISON, new String[]{"will be struck by a terrible affliction", "shall come to harm", "will be cursed", "will be possessed by a demon spirit"});
        put(O2EffectType.SHRINKING, new String[]{"will learn it is the little things that matter", "shall find a dreadful deflation drawing near", "shall be greatly reduced", "will find their footsteps diminished", "will become more grounded"});
        put(O2EffectType.SLEEPING, new String[]{"will fall silent", "shall fall in to a deep sleep", "shall pass beyond this realm", "will surrender to a sleeping death"});
        put(O2EffectType.SLOWNESS, new String[]{"shall be cursed", "will be afflicted in the mind", "will be struck by a terrible affliction", "will suffer a mental breakdown"});
        put(O2EffectType.SPEED, new String[]{"will make haste", "will wear the boots of Mercury", "will move with the power of the gods"});
        put(O2EffectType.SWELLING, new String[]{"has a big future", "shall find a dreadful inflation drawing near", "will be full of themselves", "has a bloated future"});
        put(O2EffectType.UNLUCK, new String[]{"will be cursed by misfortune", "shall be cursed", "will find nothing but misfortune"});
        put(O2EffectType.WATER_BREATHING, new String[]{"will swim with the mermaids", "will feel fishy", "will no longer fear water"});
        put(O2EffectType.WEAKNESS, new String[]{"shall be cursed", "will be cursed by weakness", "will be struck by a terrible affliction"});
        put(O2EffectType.WEALTH, new String[]{"will be blessed by fortune", "will have unnatural luck", "shall be granted a wish", "will be gifted by a leprechaun"});
    }};

    /**
     * Constructor
     *
     * @param plugin     a reference to the plugin
     * @param prophet    the player making the prophecy
     * @param target     the player the prophecy is about
     * @param experience the experience level of the prophet with this divination
     */
    public O2Divination(@NotNull Ollivanders2 plugin, @NotNull Player prophet, @NotNull Player target, int experience) {
        p = plugin;
        this.target = target;
        this.prophet = prophet;
        this.experience = experience;

        prophecyPrefix.add("The portents and omens say that");
    }

    /**
     * Generate and execute a prophecy for the target player.
     *
     * <p>This method orchestrates the complete prophecy generation workflow:</p>
     * <ol>
     * <li>Calculates the prophet's accuracy based on their experience level (capped by method's maxAccuracy)</li>
     * <li>Randomly selects a magical effect from the 21 available divination effects</li>
     * <li>Randomly selects a prophecy prefix from the method-specific list</li>
     * <li>Determines the prophecy timing (time of day and scheduled day offset)</li>
     * <li>Constructs the complete prophecy message with all components</li>
     * <li>Creates an O2Prophecy record and schedules the effect to trigger at the designated time</li>
     * </ol>
     *
     * <p>A complete prophecy message combines these components:</p>
     * <ul>
     * <li>Method-specific prefix (e.g., "Due to the influence of Mars," for astrology)</li>
     * <li>Time of day (midnight, dawn, midday, sunset)</li>
     * <li>Scheduled day (tomorrow, in two days, in three days, in four days)</li>
     * <li>Target player's name</li>
     * <li>Effect description from the selected magical effect</li>
     * </ul>
     *
     * <p>Example: "Due to the influence of Mars, at sunset tomorrow, Fred will fall into a deep sleep."</p>
     */
    public void divine() {
        UUID prophetID = prophet.getUniqueId();
        UUID targetID = target.getUniqueId();

        // determine the accuracy of this prophecy
        int accuracy = getAccuracy();

        // get the effect for this prophecy
        O2EffectType effectType = getProphecyEffect();

        // determine the number of days until fulfillment of the prophecy - 0 means tomorrow
        int numDays = (Math.abs(Ollivanders2Common.random.nextInt()) % maxDelayDays);

        // determine the time of day
        TimeCommon timeOfDay = TimeCommon.values()[(Math.abs(Ollivanders2Common.random.nextInt()) % TimeCommon.values().length)];

        // how many ticks until this time comes
        long delayTicks = getDelayTicks(numDays, timeOfDay);

        // get the duration of the effect (max 10 minutes)
        int effectDuration = getEffectDuration();

        // get the prophecy message
        String prophecyMessage = createProphecyMessage(numDays, timeOfDay, effectType);

        // create the prophecy and add it to the prophecy manager
        O2Prophecy prophecy = new O2Prophecy(p, effectType, prophecyMessage, targetID, prophetID, delayTicks, effectDuration, accuracy);
        Ollivanders2API.getProphecies().addProphecy(prophecy);

        // make the prophet speak the prophecy
        prophet.chat(prophecyMessage);
    }

    /**
     * Calculate the accuracy rating for this prophecy.
     *
     * <p>Accuracy is based on the prophet's experience level with this divination method.
     * The formula is: accuracy = experience / 2, capped by the divination method's maximum accuracy.
     * This means the prophet gains 0.5% accuracy per experience level, up to the method's limit.</p>
     *
     * <p>Examples:
     * <ul>
     * <li>Prophet with 100 experience in astrology (maxAccuracy=20): 100/2 = 50, capped to 20</li>
     * <li>Prophet with 30 experience in astrology (maxAccuracy=20): 30/2 = 15 (not capped)</li>
     * <li>Prophet with -10 experience: clamped to minimum 0</li>
     * </ul>
     * </p>
     *
     * @return the prophecy accuracy as a percentage (0-99)
     */
    private int getAccuracy() {
        // Calculation:
        // - the prophet gets a 0.5% accuracy per level of experience at this type of divination
        // - the type of divination method has a maximum accuracy level, regardless of skill, which caps accuracy
        //
        // Experience / 2 gives 0.5% accuracy per experience level (e.g., experience=100 -> 50% accuracy)
        int accuracy = experience / 2;
        if (accuracy > maxAccuracy)
            accuracy = maxAccuracy;
        else if (accuracy < 0)
            accuracy = 0;

        return accuracy;
    }

    /**
     * Calculate the number of game ticks until the prophecy should be fulfilled.
     *
     * <p>The calculation determines when the prophecy executes based on the number of days in the future
     * and the time of day selected. It works by:
     * <ol>
     * <li>Finding the number of ticks until the next midnight (end of current day)</li>
     * <li>Adding additional days worth of ticks for the delay (if delayDays > 0)</li>
     * <li>Adding the time-of-day offset (e.g., dawn is partway through the day)</li>
     * </ol>
     * </p>
     *
     * <p>Example: If it's currently noon (6000 ticks into a 24000 tick day), tomorrow at midnight:
     * <ul>
     * <li>ticksToMidnight = 24000 - 6000 = 18000</li>
     * <li>delayDays = 0 (tomorrow)</li>
     * <li>timeOfDay.getTick() = 0 (midnight)</li>
     * <li>Total = 18000 ticks</li>
     * </ul>
     * </p>
     *
     * @param delayDays the number of days in the future (0 = tomorrow, 1 = 2 days from now, etc.)
     * @param timeOfDay the time of day when the prophecy will execute
     * @return the number of game ticks until the prophecy should be fulfilled
     */
    private long getDelayTicks(int delayDays, TimeCommon timeOfDay) {
        // number of ticks until midnight (end of current day) = tickPerDay - the current time
        long ticksToMidnight = ticksPerDay - (target.getWorld().getTime());

        // number of ticks to midnight of the chose day = number of ticks to midnight + number of ticks per day to advance
        // so for delayDays = 0, the day is tomorrow, then ticksToMidnightOfChosenDay = ticksToMidnight
        // if delayDays = 1, advance one day, so 2 days from now, then ticksToMidnightOfChosenDay = ticksToMidnight + 1 day worth of ticks
        long ticksToMidnightOfChosenDay = ticksToMidnight + (delayDays * (long) ticksPerDay);

        // Add the specific time of day offset (midnight, dawn, midday, or sunset)
        return ticksToMidnightOfChosenDay + timeOfDay.getTick();
    }

    /**
     * Construct the complete prophecy message text.
     *
     * <p>Assembles all components into a grammatically correct prophecy sentence:
     * [prefix], at [timeOfDay] [day], [targetName] [effectDescription].</p>
     *
     * <p>Components assembled in order:
     * <ol>
     * <li>Method-specific prefix (randomly selected from prophecyPrefix array)</li>
     * <li>Time of day (dawn, midday, sunset, or midnight)</li>
     * <li>Day reference (tomorrow, in 2 days, in 3 days, etc.)</li>
     * <li>Target player's name</li>
     * <li>Effect description (randomly selected from divinationEffects)</li>
     * </ol>
     * </p>
     *
     * <p>Example output: "The cards have revealed that, at sunset in 3 days, Fred shall be cursed."</p>
     *
     * @param numDays the number of days offset (0 = tomorrow, 1 = 2 days from now, etc.)
     * @param timeOfDay the time of day for the prophecy
     * @param effectType the magical effect type that will be applied
     * @return the complete prophecy message
     */
    private String createProphecyMessage(int numDays, TimeCommon timeOfDay, O2EffectType effectType) {
        StringBuilder prophecyMessage = new StringBuilder();

        // start with any prophecy prefix - this comes from the specific divination method, so Cartomancy might be "The cards have revealed that"
        if (!prophecyPrefix.isEmpty()) {
            int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % prophecyPrefix.size());
            prophecyMessage.append(prophecyPrefix.get(rand)).append(", ");
        }

        // add time of day the prophecy will come to pass
        if (prophecyMessage.isEmpty())
            prophecyMessage.append("At "); // capitalize because it is the start of the sentence
        else
            prophecyMessage.append("at ");
        prophecyMessage.append(timeOfDay.toString().toLowerCase()).append(" ");

        // add in the day - numDays is the offset (0 = tomorrow, 1 = 2 days from now, etc.)
        if (numDays == 0)
            prophecyMessage.append("tomorrow ");
        else
            prophecyMessage.append("in ").append(numDays + 1).append(" days ");

        // add target name
        prophecyMessage.append(target.getName()).append(" ");

        // add prophecy predicate - this comes from the specific effect this prophecy will cause
        prophecyMessage.append(getProphecyPredicateText(effectType)).append(".");

        return prophecyMessage.toString();
    }

    /**
     * Select a random magical effect from the divination effects pool.
     *
     * <p>Randomly selects one of the available O2EffectType values that have been defined in the divinationEffects map.
     * Uses a modulo-based random selection to ensure uniform distribution across all available effects.</p>
     *
     * @return a randomly selected O2EffectType from the divination effects pool
     */
    private O2EffectType getProphecyEffect() {
        // Create a list of all effect types in the divination effects map, then select one at random
        int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % divinationEffects.size());
        return new ArrayList<>(divinationEffects.keySet()).get(rand);
    }

    /**
     * Get a random prophecy predicate text for the given effect type.
     *
     * <p>From the divinationEffects map, retrieves the array of possible prophecy text phrases for the given effect type,
     * then selects one at random. This allows multiple possible prophecy wordings for each effect, increasing variety.</p>
     *
     * <p>Example: For O2EffectType.HARM, might return "shall be struck by a terrible affliction" or "will come to harm".</p>
     *
     * @param effectType the magical effect type to get prophecy text for
     * @return a randomly selected prophecy predicate phrase for that effect type
     */
    private String getProphecyPredicateText(O2EffectType effectType) {
        String[] prophecyTexts = divinationEffects.get(effectType);

        // Select a random text phrase from the array for this effect type
        int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % prophecyTexts.length);
        return prophecyTexts[rand];
    }

    /**
     * Calculate the duration for the predicted magical effect.
     *
     * <p>The effect duration scales based on the prophet's experience level with this divination method:
     * 120 game ticks per experience level (roughly 6 seconds per level, given 20 ticks per second).
     * The duration is capped at a minimum of O2Effect.minDuration and a maximum of 10 minutes (12000 ticks).</p>
     *
     * <p>Duration scaling examples:
     * <ul>
     * <li>Experience = 10: 10 * 120 = 1200 ticks (60 seconds)</li>
     * <li>Experience = 50: 50 * 120 = 6000 ticks (300 seconds / 5 minutes)</li>
     * <li>Experience = 100: 100 * 120 = 12000 ticks, capped at 12000 (600 seconds / 10 minutes max)</li>
     * </ul>
     * </p>
     *
     * @return the effect duration in game ticks, ranging from O2Effect.minDuration to 12000
     */
    private int getEffectDuration() {
        // Calculate duration based on prophet's experience: 120 ticks per experience level
        // (20 ticks per second: 30 seconds = 600 ticks, 10 minutes = 12000 ticks)
        int effectDuration = 120 * experience;
        if (effectDuration > 12000)
            effectDuration = 12000;  // Cap at 10 minutes
        else if (effectDuration < O2Effect.minDuration)
            effectDuration = O2Effect.minDuration;    // Floor at O2Effect.minDuration

        return effectDuration;
    }
}
