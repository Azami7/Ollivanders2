package net.pottercraft.Ollivanders2.Effect;

/**
 * All effects
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public enum Effects
{
   ANIMAGUS_INCANTATION,
   BABBLING_EFFECT,
   BARUFFIOS_BRAIN_ELIXIR,
   HERBIFORS,
   INCARNATIO_DEVITO,
   INCARNATIO_EQUUS,
   INCARNATIO_FELIS,
   INCARNATIO_LAMA,
   INCARNATIO_LUPI,
   INCARNATIO_PORCILLI,
   INCARNATIO_VACCULA,
   LEVICORPUS,
   LYCANTHROPY,
   MEMORY_POTION,
   MUCUS_AD_NAUSEAM,
   SILENCIO,
   VENTO_FOLIO,
   WIT_SHARPENING_POTION,
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