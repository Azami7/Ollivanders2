package net.pottercraft.Ollivanders2.Effect;

/**
 * All effects
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public enum O2EffectType
{
   AGGRESSION (AGGRESSION.class),
   ANIMAGUS_EFFECT (ANIMAGUS_EFFECT.class),
   ANIMAGUS_INCANTATION (ANIMAGUS_INCANTATION.class),
   AWAKE (AWAKE.class),
   BABBLING (BABBLING.class),
   BLINDNESS (BLINDNESS.class),
   CONFUSION (CONFUSION.class),
   FAST_LEARNING (FAST_LEARNING.class),
   FLYING (FLYING.class),
   HARM (HARM.class),
   HEAL (HEAL.class),
   HEALTH_BOOST (HEALTH_BOOST.class),
   HUNGER (HUNGER.class),
   HIGHER_SKILL (HIGHER_SKILL.class),
   IMMOBILIZE (IMMOBILIZE.class),
   IMPROVED_BOOK_LEARNING (IMPROVED_BOOK_LEARNING.class),
   LUCK (LUCK.class),
   LYCANTHROPY (LYCANTHROPY.class),
   LYCANTHROPY_SPEECH (LYCANTHROPY_SPEECH.class),
   LYCANTHROPY_RELIEF (LYCANTHROPY_RELIEF.class),
   MUCUS (MUCUS.class),
   MUTED_SPEECH (MUTED_SPEECH.class),
   NIGHT_VISION(NIGHT_VISION.class),
   POISON (POISON.class),
   SLEEP_SPEECH (SLEEP_SPEECH.class),
   SLEEPING (SLEEPING.class),
   SLOWNESS (SLOWNESS.class),
   SUSPENSION (SUSPENSION.class),
   UNLUCK (UNLUCK.class),
   WATER_BREATHING(WATER_BREATHING.class),
   WEAKNESS (WEAKNESS.class),
   WEALTH (WEALTH.class),
   ;

   private Class className;

   /**
    * Enum constructor.
    *
    * @param className the name of the effect class this type represents.
    */
   O2EffectType (Class className)
   {
      this.className = className;
   }

   /**
    * Get the class name for the O2Effect class that this type represents.
    *
    * @return the effect class
    */
   public Class getClassName ()
   {
      return className;
   }

   /**
    * Find the lowercase string that corresponds to an effect effectType
    *
    * @param effectType effect effectType as an enum
    * @return string such that it is the lowercase version of the effect minus underscores
    */
   @Deprecated
   public static String recode (O2EffectType effectType)
   {
      String nameLow = effectType.toString().toLowerCase();
      String[] words = nameLow.split("_");
      String comp = "";

      for (String word : words)
      {
         comp = comp.concat(word);
         comp = comp.concat(" ");
      }
      comp = comp.substring(0, comp.length() - 1);
      return comp;
   }
}