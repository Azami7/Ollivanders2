package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.common.TimeCommon;
import net.pottercraft.ollivanders2.effect.O2Effect;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Super class for all divination methods. Creates a prophecy which may come to pass at a future time.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Divination
 */
public abstract class O2Divination
{
    /**
     * Reference to the plugin
     */
    final Ollivanders2 p;

    /**
     * The type of divination this is.
     */
    O2DivinationType divintationType = O2DivinationType.ASTROLOGY;

    /**
     * The maximum odds this divination will happen.
     */
    int maxAccuracy = 10;

    /**
     * The length of time from this divination until this it happens
     */
    int maxDurationDays = Ollivanders2.divinationMaxDays;

    /**
     * The target of this divination
     */
    Player target;

    /**
     * The player making the prophecy/divination
     */
    Player prophet;

    /**
     * The experience level of the prophet in this type of divination. Affects accuracy.
     */
    int experience;

    /**
     * The possible text prefixes for this prophecy.
     */
    ArrayList<String> prophecyPrefix = new ArrayList<>();

    /**
     * Possible effects the divination can cause.
     */
    static final ArrayList<O2EffectType> divinationEffects = new ArrayList<>()
    {{
        add(O2EffectType.AGGRESSION);
        add(O2EffectType.BABBLING);
        add(O2EffectType.BLINDNESS);
        add(O2EffectType.BURNING);
        add(O2EffectType.CONFUSION);
        add(O2EffectType.IMMOBILIZE);
        add(O2EffectType.HARM);
        add(O2EffectType.HEAL);
        add(O2EffectType.HUNGER);
        add(O2EffectType.HEALTH_BOOST);
        add(O2EffectType.LUCK);
        add(O2EffectType.MUTED_SPEECH);
        add(O2EffectType.POISON);
        add(O2EffectType.SLEEPING);
        add(O2EffectType.SLOWNESS);
        add(O2EffectType.UNLUCK);
        add(O2EffectType.WEAKNESS);
        add(O2EffectType.WEALTH);
    }};

    /**
     * Constructor
     *
     * @param plugin     a referenc to the plugin
     * @param prophet    the player making the prophecy
     * @param target     the player the prophecy is about
     * @param experience the experience level of the prophet with this divination
     */
    O2Divination(@NotNull Ollivanders2 plugin, @NotNull Player prophet, @NotNull Player target, int experience)
    {
        p = plugin;
        this.target = target;
        this.prophet = prophet;
        this.experience = experience;

        prophecyPrefix.add("The portents and omens say that");
    }

    /**
     * Make a prophecy by the prophet about the target.
     * <p>
     * Parts of a prophecy:
     * <p>
     * 1. prophecyPrefix - optional prefix based on the specific divination method, such as "Due to the influence of Mars"
     * 2. the time of day - midnight, sunrise, midday, sunset
     * 3. the day - today, tomorrow, 3rd day, 4th day
     * 4. the target's name
     * 5. what will happen to them - see
     * A prophecy should read like:
     * "After the sun sets on the 3rd day, Fred will fall in to a deep sleep."
     */
    public void divine()
    {
        UUID prophetID = prophet.getUniqueId();
        UUID targetID = target.getUniqueId();

        StringBuilder prophecyMessage = new StringBuilder();

        //
        // first, determine the accuracy of this prophecy
        //
        // Calculation:
        // - the prophet gets a 0.5% accuracy per level of experience at this type of divination
        // - the type of divination method has a maximum accuracy level, regardless of skill, which caps accuracy
        //
        int accuracy = experience / 2;
        if (accuracy > maxAccuracy)
            accuracy = maxAccuracy;
        else if (accuracy < 0)
            accuracy = 0;

        //
        // second, pick the effect
        //
        int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % divinationEffects.size());
        O2EffectType effectType = divinationEffects.get(rand);

        O2Effect effect = getEffect(targetID, effectType);
        if (effect == null)
            return;

        if (prophecyPrefix.size() > 0)
        {
            rand = (Math.abs(Ollivanders2Common.random.nextInt()) % prophecyPrefix.size());
            prophecyMessage.append(prophecyPrefix.get(rand)).append(" ");
        }

        //
        // finally, the time of day and duration - via a lot of random chance
        //
        rand = (Math.abs(Ollivanders2Common.random.nextInt()) % TimeCommon.values().length);
        TimeCommon timeOfDay = TimeCommon.values()[rand];

        prophecyMessage.append("at ").append(timeOfDay.toString().toLowerCase()).append(" ");

        rand = (Math.abs(Ollivanders2Common.random.nextInt()) % maxDurationDays);
        long curTime = target.getWorld().getTime();
        long ticks = 24000 - curTime;

        if (rand == 0)
            //tomorrow
            prophecyMessage.append("tomorrow, ");
        else if (rand == 1)
        {
            //tomorrow
            prophecyMessage.append("in two days, ");
            ticks = ticks + 24000;
        }
        else if (rand == 2)
        {
            //3rd day
            prophecyMessage.append("in three days, ");
            ticks = ticks + 48000;
        }
        else
        {
            //4th day
            prophecyMessage.append("in four days, ");
            ticks = ticks + 72000;
        }

        ticks = ticks + timeOfDay.getTick();

        //
        // duration (min 30 seconds, max 10 minutes)
        //
        int effectDuration = 120 * experience;
        if (effectDuration > 12000)
            effectDuration = 12000;
        else if (effectDuration < 600)
            effectDuration = 600;

        //
        // finish prophecy
        //
        prophecyMessage.append(target.getName()).append(" ").append(effect.getDivinationText()).append(".");
        String finalMessage = prophecyMessage.toString();

        prophet.chat(finalMessage);
        O2Prophecy prophecy = new O2Prophecy(p, effectType, finalMessage, targetID, prophetID, ticks, effectDuration, accuracy);
        Ollivanders2API.getProphecies().addProphecy(prophecy);
    }

    /**
     * Get the effect of this divination.
     *
     * @param targetID   the ID of the target player for this effect
     * @param effectType the type of effect
     * @return the effect this prophecy causes
     */
    @Nullable
    private O2Effect getEffect(@NotNull UUID targetID, @NotNull O2EffectType effectType)
    {
        Class<?> effectClass = effectType.getClassName();
        O2Effect effect;

        try
        {
            effect = (O2Effect) effectClass.getConstructor(Ollivanders2.class, int.class, UUID.class).newInstance(p, 1, targetID);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        return effect;
    }
}
