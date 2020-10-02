package net.pottercraft.ollivanders2.effect;

import org.jetbrains.annotations.NotNull;

/**
 * All effects
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public enum O2EffectType
{
   AGGRESSION(AGGRESSION.class),
   ANIMAGUS_EFFECT(ANIMAGUS_EFFECT.class),
   ANIMAGUS_INCANTATION(ANIMAGUS_INCANTATION.class),
   AWAKE(AWAKE.class),
   BABBLING(BABBLING.class),
   BLINDNESS(BLINDNESS.class),
   BURNING(BURNING.class),
   CONFUSION(CONFUSION.class),
   FAST_LEARNING(FAST_LEARNING.class),
   FLYING(FLYING.class),
   HARM(HARM.class),
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
   SLEEP_SPEECH(SLEEP_SPEECH.class),
   SLEEPING(SLEEPING.class),
   SLOWNESS (SLOWNESS.class),
   SUSPENSION (SUSPENSION.class),
   UNLUCK (UNLUCK.class),
   WATER_BREATHING(WATER_BREATHING.class),
   WEAKNESS (WEAKNESS.class),
   WEALTH (WEALTH.class),
   ;

   final private Class<?> className;

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
}