package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.common.TimeCommon;
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
 * Abstract parent for all divination spell implementations.
 * <p>
 * A divination generates a randomized prophecy about a target player: a method-specific prefix, a randomly chosen
 * magical effect, and a scheduled time are combined into a prophetic message. The prophet speaks the message, and the
 * effect is applied to the target when the prophesied time arrives. Subclasses supply their {@link O2DivinationType},
 * their maximum accuracy cap, and their pool of prophecy prefixes; the more accurate the method, the more likely the
 * prophecy comes true.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Divination">Harry Potter Wiki - Divination</a>
 */
public abstract class O2Divination {
    /**
     * Reference to the plugin
     */
    final Ollivanders2 p;

    /**
     * The divination method this instance represents; set by the subclass constructor.
     */
    O2DivinationType divinationType = O2DivinationType.ASTROLOGY;

    /**
     * The number of game ticks in a minecraft "day".
     */
    static final int ticksPerDay = 24000;

    /**
     * The maximum accuracy percentage this method can achieve, capping the prophet's experience-based accuracy.
     * Subclasses override this; the base default is 10.
     */
    int maxAccuracy = 10;

    /**
     * The maximum number of days ahead a prophecy can be scheduled.
     */
    int maxDelayDays = Ollivanders2.divinationMaxDays;

    /**
     * The player the prophecy is about; the prophesied effect is applied to them.
     */
    Player target;

    /**
     * The player performing the divination.
     */
    Player prophet;

    /**
     * The prophet's experience with this divination method; higher experience raises accuracy up to {@link #maxAccuracy}.
     */
    int experience;

    /**
     * Method-specific prophecy prefixes; one is chosen at random per prophecy. The base list holds only a generic
     * fallback ("The portents and omens say that"); subclasses add their own thematic phrases.
     */
    ArrayList<String> prophecyPrefix = new ArrayList<>();

    /**
     * Maps each magical effect a prophecy can trigger to the pool of prophetic phrases that describe it. A prophecy
     * picks a random effect, then a random phrase from that effect's pool, so the same effect can be foretold many ways.
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
     * @param experience the prophet's experience level with this divination
     */
    public O2Divination(@NotNull Ollivanders2 plugin, @NotNull Player prophet, @NotNull Player target, int experience) {
        p = plugin;
        this.target = target;
        this.prophet = prophet;
        this.experience = experience;

        prophecyPrefix.add("The portents and omens say that");
    }

    /**
     * Generate a prophecy about the target, speak it as the prophet, and register it so its effect is applied to the
     * target at the prophesied time.
     * <p>
     * Example message: "Due to the influence of Mars, at sunset tomorrow, Fred shall fall in to a deep sleep."
     * </p>
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
        int effectDuration = getEffectDuration(effectType);

        // get the prophecy message
        String prophecyMessage = createProphecyMessage(numDays, timeOfDay, effectType);

        // create the prophecy and add it to the prophecy manager
        O2Prophecy prophecy = new O2Prophecy(p, effectType, prophecyMessage, targetID, prophetID, delayTicks, effectDuration, accuracy);
        Ollivanders2API.getProphecies().addProphecy(prophecy);

        // make the prophet speak the prophecy
        prophet.chat(prophecyMessage);
    }

    /**
     * Calculate this prophecy's accuracy: half the prophet's experience (0.5% per experience level), capped at
     * {@link #maxAccuracy} and floored at 0.
     *
     * @return the prophecy accuracy as a percentage, from 0 to {@link #maxAccuracy}
     */
    private int getAccuracy() {
        int accuracy = experience / 2;
        if (accuracy > maxAccuracy)
            accuracy = maxAccuracy;
        else if (accuracy < 0)
            accuracy = 0;

        return accuracy;
    }

    /**
     * Calculate the number of game ticks from now until the prophecy is fulfilled, measured from the target's world time.
     *
     * @param delayDays the day offset (0 = tomorrow, 1 = 2 days from now, etc.)
     * @param timeOfDay the time of day when the prophecy will execute
     * @return the number of game ticks until fulfillment
     */
    private long getDelayTicks(int delayDays, TimeCommon timeOfDay) {
        long ticksToMidnight = ticksPerDay - (target.getWorld().getTime());
        long ticksToMidnightOfChosenDay = ticksToMidnight + (delayDays * (long) ticksPerDay);

        return ticksToMidnightOfChosenDay + timeOfDay.getTick();
    }

    /**
     * Construct the prophecy message, of the form "[prefix], at [time of day] [day] [target] [effect predicate]."
     *
     * @param numDays the day offset (0 = tomorrow, 1 = 2 days from now, etc.)
     * @param timeOfDay the time of day for the prophecy
     * @param effectType the magical effect the prophecy foretells
     * @return the complete prophecy message, e.g. "The cards have revealed that, at sunset in 3 days Fred shall be cursed."
     */
    private String createProphecyMessage(int numDays, TimeCommon timeOfDay, O2EffectType effectType) {
        StringBuilder prophecyMessage = new StringBuilder();

        if (!prophecyPrefix.isEmpty()) {
            int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % prophecyPrefix.size());
            prophecyMessage.append(prophecyPrefix.get(rand)).append(", ");
        }

        // capitalize "At" only when no prefix preceded it and it starts the sentence
        if (prophecyMessage.isEmpty())
            prophecyMessage.append("At ");
        else
            prophecyMessage.append("at ");
        prophecyMessage.append(timeOfDay.toString().toLowerCase()).append(" ");

        if (numDays == 0)
            prophecyMessage.append("tomorrow ");
        else
            prophecyMessage.append("in ").append(numDays + 1).append(" days ");

        prophecyMessage.append(target.getName()).append(" ");

        prophecyMessage.append(getProphecyPredicateText(effectType)).append(".");

        return prophecyMessage.toString();
    }

    /**
     * Select a random effect from the {@link #divinationEffects} pool for this prophecy to foretell.
     *
     * @return a randomly selected effect type
     */
    private O2EffectType getProphecyEffect() {
        int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % divinationEffects.size());
        return new ArrayList<>(divinationEffects.keySet()).get(rand);
    }

    /**
     * Get a random prophecy predicate phrase for the given effect, e.g. "shall be struck by a terrible affliction" for
     * {@link O2EffectType#HARM}.
     *
     * @param effectType the effect to describe; must be a key in {@link #divinationEffects}
     * @return a randomly selected predicate phrase for that effect
     */
    private String getProphecyPredicateText(O2EffectType effectType) {
        String[] prophecyTexts = divinationEffects.get(effectType);

        int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % prophecyTexts.length);
        return prophecyTexts[rand];
    }

    /**
     * Calculate the duration of the prophesied effect: 120 ticks per experience level, capped at 10 minutes (12000
     * ticks) and then clamped to the effect type's own min and max duration.
     *
     * @param effectType the effect whose duration bounds apply
     * @return the effect duration in game ticks
     */
    private int getEffectDuration(O2EffectType effectType) {
        int effectDuration = 120 * experience;
        if (effectDuration > 12000)
            effectDuration = 12000;

        if (effectDuration < effectType.getMinDuration())
            effectDuration = effectType.getMinDuration();
        else if (effectDuration > effectType.getMaxDuration())
            effectDuration = effectType.getMaxDuration();

        return effectDuration;
    }
}
