package net.pottercraft.ollivanders2.effect;

import org.jetbrains.annotations.NotNull;

/**
 * All effects
 *
 * @author Azami7
 * @version Ollivanders2
 */
public enum O2EffectType
{
   AGGRESSION(AGGRESSION.class),
   ANIMAGUS_EFFECT(ANIMAGUS_EFFECT.class),
   ANIMAGUS_INCANTATION(ANIMAGUS_INCANTATION.class),
   AWAKE(AWAKE.class),
   AWAKE_ANTIDOTE_LESSER(AWAKE_ANTIDOTE_LESSER.class),
   BABBLING(BABBLING.class),
   BABBLING_ANTIDOTE_LESSER (BABBLING_ANTIDOTE_LESSER.class),
   BLINDNESS(BLINDNESS.class),
   BURNING(BURNING.class),
   CONFUSION(CONFUSION.class),
   FAST_LEARNING(FAST_LEARNING.class),
   FLYING(FLYING.class),
   HARM(HARM.class),
   HARM_ANTIDOTE_LESSER(HARM_ANTIDOTE_LESSER.class),
   HEAL(HEAL.class),
   HEALTH_BOOST(HEALTH_BOOST.class),
   HUNGER(HUNGER.class),
   HIGHER_SKILL(HIGHER_SKILL.class),
   IMMOBILIZE(IMMOBILIZE.class),
   IMPROVED_BOOK_LEARNING(IMPROVED_BOOK_LEARNING.class),
   LUCK(LUCK.class),
   LYCANTHROPY(LYCANTHROPY.class),
   LYCANTHROPY_SPEECH(LYCANTHROPY_SPEECH.class),
   LYCANTHROPY_RELIEF(LYCANTHROPY_RELIEF.class),
   MUCUS(MUCUS.class),
   MUTED_SPEECH(MUTED_SPEECH.class),
   NIGHT_VISION(NIGHT_VISION.class),
   POISON(POISON.class),
   POISON_ANTIDOTE_LESSER(POISON_ANTIDOTE_LESSER.class),
   SLEEP_SPEECH(SLEEP_SPEECH.class),
   SLEEPING(SLEEPING.class),
   SPEED(SPEED.class),
   SPEED_SPEEDIER(SPEED_SPEEDIER.class),
   SPEED_SPEEDIEST(SPEED_SPEEDIEST.class),
   SLOWNESS (SLOWNESS.class),
   SUSPENSION (SUSPENSION.class),
   UNLUCK (UNLUCK.class),
   UNLUCK_ANTIDOTE_LESSER (UNLUCK_ANTIDOTE_LESSER.class),
   WATER_BREATHING(WATER_BREATHING.class),
   WEAKNESS (WEAKNESS.class),
   WEALTH (WEALTH.class),
   ;

   final private Class<?> className;
   boolean enabled = true;

   /**
    * Enum constructor.
    *
    * @param className the name of the effect class this type represents.
    */
   O2EffectType(@NotNull Class<?> className)
   {
      this.className = className;
   }

   /**
    * Get the class name for the O2Effect class that this type represents.
    *
    * @return the effect class
    */
   @NotNull
   public Class<?> getClassName()
   {
      return className;
   }

   /**
    * Is this effect currently enabled
    *
    * @return
    */
   public boolean isEnabled()
   {
      return enabled;
   }

   /**
    * Set whether this effect is currently enabled
    *
    * @param enabled
    * @return
    */
   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }
}