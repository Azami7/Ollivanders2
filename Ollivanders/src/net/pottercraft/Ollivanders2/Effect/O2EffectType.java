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
   ANIMAGUS_INCANTATION,
   BABBLING_EFFECT,
   BARUFFIOS_BRAIN_ELIXIR,
   FLYING,
   LEVICORPUS,
   LYCANTHROPY,
   MEMORY_POTION,
   MUCUS_AD_NAUSEAM,
   SILENCIO,
   WIT_SHARPENING_POTION,
   WOLFSBANE_POTION;

   /**
    * Find the lowercase string that corresponds to an effect name
    *
    * @param effectType effect name as an enum
    * @return string such that it is the lowercase version of the effect minus underscores
    */
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