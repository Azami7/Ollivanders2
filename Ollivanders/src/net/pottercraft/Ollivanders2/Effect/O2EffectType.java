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
   AGGRESSION (net.pottercraft.Ollivanders2.Effect.AGGRESSION.class),
   ANIMAGUS_EFFECT (net.pottercraft.Ollivanders2.Effect.ANIMAGUS_EFFECT.class),
   ANIMAGUS_INCANTATION (net.pottercraft.Ollivanders2.Effect.ANIMAGUS_INCANTATION.class),
   AWAKE (net.pottercraft.Ollivanders2.Effect.AWAKE.class),
   BABBLING(net.pottercraft.Ollivanders2.Effect.BABBLING.class),
   FAST_LEARNING(net.pottercraft.Ollivanders2.Effect.FAST_LEARNING.class),
   FLYING (net.pottercraft.Ollivanders2.Effect.FLYING.class),
   HIGHER_SKILL(net.pottercraft.Ollivanders2.Effect.HIGHER_SKILL.class),
   IMMOBILIZE(net.pottercraft.Ollivanders2.Effect.IMMOBILIZE.class),
   IMPROVED_BOOK_LEARNING(net.pottercraft.Ollivanders2.Effect.IMPROVED_BOOK_LEARNING.class),
   LUCK(net.pottercraft.Ollivanders2.Effect.LUCK.class),
   LYCANTHROPY (net.pottercraft.Ollivanders2.Effect.LYCANTHROPY.class),
   LYCANTHROPY_SPEECH (net.pottercraft.Ollivanders2.Effect.LYCANTHROPY_SPEECH.class),
   LYCANTHROPY_RELIEF(net.pottercraft.Ollivanders2.Effect.LYCANTHROPY_RELIEF.class),
   MUCUS (net.pottercraft.Ollivanders2.Effect.MUCUS.class),
   MUTED_SPEECH (net.pottercraft.Ollivanders2.Effect.MUTED_SPEECH.class),
   SLEEP_SPEECH (net.pottercraft.Ollivanders2.Effect.SLEEP_SPEECH.class),
   SLEEPING(net.pottercraft.Ollivanders2.Effect.SLEEPING.class),
   SUSPENSION(net.pottercraft.Ollivanders2.Effect.SUSPENSION.class),
   WEALTH(net.pottercraft.Ollivanders2.Effect.WEALTH.class),;

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