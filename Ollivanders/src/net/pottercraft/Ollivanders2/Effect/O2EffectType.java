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
   BABBLING (BABBLING.class),
   BARUFFIOS_BRAIN_ELIXIR (net.pottercraft.Ollivanders2.Effect.BARUFFIOS_BRAIN_ELIXIR.class),
   FLYING (net.pottercraft.Ollivanders2.Effect.FLYING.class),
   LEVICORPUS (net.pottercraft.Ollivanders2.Effect.LEVICORPUS.class),
   LYCANTHROPY (net.pottercraft.Ollivanders2.Effect.LYCANTHROPY.class),
   LYCANTHROPY_SPEECH (net.pottercraft.Ollivanders2.Effect.LYCANTHROPY_SPEECH.class),
   MEMORY_POTION (net.pottercraft.Ollivanders2.Effect.MEMORY_POTION.class),
   MUCUS_AD_NAUSEAM (net.pottercraft.Ollivanders2.Effect.MUCUS_AD_NAUSEAM.class),
   SILENCIO (net.pottercraft.Ollivanders2.Effect.SILENCIO.class),
   SLEEP (net.pottercraft.Ollivanders2.Effect.SLEEP.class),
   SLEEP_SPEECH (net.pottercraft.Ollivanders2.Effect.SLEEP_SPEECH.class),
   WIT_SHARPENING_POTION (net.pottercraft.Ollivanders2.Effect.WIT_SHARPENING_POTION.class),
   WOLFSBANE_POTION (net.pottercraft.Ollivanders2.Effect.WOLFSBANE_POTION.class);

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