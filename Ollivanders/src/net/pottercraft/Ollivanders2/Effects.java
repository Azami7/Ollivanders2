package net.pottercraft.Ollivanders2;

/**
 * All effects
 *
 * @author lownes
 */
public enum Effects
{
   BARUFFIOS_BRAIN_ELIXIR,
   LEVICORPUS,
   LYCANTHROPY,
   MEMORY_POTION,
   MUCUS_AD_NAUSEAM,
   SILENCIO,
   VENTO_FOLIO,
   WOLFSBANE_POTION;

   /**
    * Find the lowercase string that corresponds to an effect name
    *
    * @param s - effect
    * @return string such that it is the lowercase version of the effect minus underscores
    */
   public static String recode (Effects s)
   {
      String nameLow = s.toString().toLowerCase();
      String[] words = nameLow.split("_");
      String comp = "";
      for (String st : words)
      {
         comp = comp.concat(st);
         comp = comp.concat(" ");
      }
      comp = comp.substring(0, comp.length() - 1);
      return comp;
   }
}