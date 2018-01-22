package net.pottercraft.Ollivanders2.Spell;

/**
 * Represents allowable spells.
 *
 * @author Azami7
 * @version Ollivanders2
 */

public enum Spells
{
   ACCIO,
   AGUAMENTI,
   ALARTE_ASCENDARE,
   ALIQUAM_FLOO,
   ALOHOMORA,
   APARECIUM,
   APPARATE,
   AQUA_ERUCTO,
   ARANIA_EXUMAI,
   ARRESTO_MOMENTUM,
   ASCENDIO,
   AVADA_KEDAVRA,
   AVIFORS,
   AVIS,
   BOMBARDA,
   BOMBARDA_MAXIMA,
   BOTHYNUS,
   BOTHYNUS_DUO,
   BOTHYNUS_TRIA,
   BRACKIUM_EMENDO,
   CALAMUS,
   CARPE_RETRACTUM,
   COLLOPORTUS,
   COLORO_AURANTIACO,
   COLORO_CAERULUS,
   COLORO_FLAVO,
   COLORO_OSTRUM,
   COLORO_VERIDI,
   COLORO_VERMICULO,
   COLOVARIA,
   COMETES,
   COMETES_DUO,
   CONFUNDO,
   CONFUNDUS_DUO,
   CRESCERE_PROTEGAT,
   DEFODIO,
   DELETRIUS,
   DEPRIMO,
   DEPULSO,
   DIFFINDO,
   DISSENDIUM,
   DUCKLIFORS,
   DURO,
   DRACONIFORS,
   EBUBLIO,
   ENGORGIO,
   ENTOMORPHIS,
   EPISKEY,
   ET_INTERFICIAM_ANIMAM_LIGAVERIS,
   EQUUSIFORS,
   EVANESCO,
   FLIPENDO,
   EXPELLIARMUS,
   FATUUS_AURUM,
   FIANTO_DURI,
   FIENDFYRE,
   FINITE_INCANTATEM,
   FLAGRANTE,
   FRANGE_LIGNEA,
   FUMOS,
   FUMOS_DUO,
   GEMINO,
   GLACIUS,
   GLACIUS_DUO,
   GLACIUS_TRIA,
   HARMONIA_NECTERE_PASSUS,
   HERBIFORS,
   HERBIVICUS,
   HORREAT_PROTEGAT,
   IMMOBULUS,
   IMPEDIMENTA,
   INCARNATIO_DEVITO,
   INCARNATIO_EQUUS,
   INCARNATIO_FELIS,
   INCARNATIO_LAMA,
   INCARNATIO_LUPI,
   INCARNATIO_PORCILLI,
   INCARNATIO_URSUS,
   INCARNATIO_VACCULA,
   INCENDIO,
   INCENDIO_DUO,
   INCENDIO_TRIA,
   INFORMOUS,
   LACARNUM_INFLAMARI,
   LAPIFORS,
   LEGILIMENS,
   LEVICORPUS,
   LIBERACORPUS,
   LIGATIS_COR,
   LUMOS,
   LUMOS_DUO,
   LUMOS_MAXIMA,
   LUMOS_SOLEM,
   MELOFORS,
   METELOJINX,
   METELOJINX_RECANTO,
   MOLLIARE,
   MORSMORDRE,
   MORTUOS_SUSCITATE,
   MOV_FOTIA,
   MUCUS_AD_NAUSEAM,
   MUFFLIATO,
   MULTICORFORS,
   NOX,
   NULLUM_APPAREBIT,
   NULLUM_EVANESCUNT,
   OBLIVIATE,
   OBSCURO,
   OPPUGNO,
   PACK,
   PARTIS_TEMPORUS,
   PERICULUM,
   PERICULUM_DUO,
   PIERTOTUM_LOCOMOTOR,
   PORFYRO_ASTERI,
   PORFYRO_ASTERI_DUO,
   PORFYRO_ASTERI_TRIA,
   PORTUS,
   PRAEPANDO,
   PROTEGO,
   PROTEGO_HORRIBILIS,
   PROTEGO_MAXIMA,
   PROTEGO_TOTALUM,
   REDUCIO,
   REDUCTO,
   REPARIFARGE,
   REPARO,
   REPELLO_MUGGLETON,
   SCUTO_CONTERAM,
   SILENCIO,
   SPONGIFY,
   STUPEFY,
   TERGEO,
   VENTO_FOLIO,
   VERA_VERTO,
   VERDIMILLIOUS,
   VERDIMILLIOUS_DUO,
   VOLATUS,
   WINGARDIUM_LEVIOSA;

   /**
    * Find the Spell that corresponds to a string.
    *
    * @param s - string, doesn't have to correspond to a spell
    * @return spell such that the spell resembles the string in spelling. null if no such spell exists
    */
   public static Spells decode (String s)
   {
      String[] words = s.split(" ");
      Spells spell;

      // handle spells with target words first
      if (words[0].equalsIgnoreCase("Apparate"))
      {
         spell = Spells.APPARATE;
      }
      else if (words[0].equalsIgnoreCase("Portus"))
      {
         spell = Spells.PORTUS;
      }
      else // normal spells
      {
         for (int i = 0; i < words.length; i++)
         {
            words[i] = words[i].toUpperCase();
         }

         StringBuilder completeSB = new StringBuilder();
         for (String word : words)
         {
            completeSB.append(word);
            completeSB.append("_");
         }

         String complete = completeSB.substring(0, completeSB.length() - 1);
         try
         {
            spell = Spells.valueOf(complete);
         }
         catch (Exception e)
         {
            spell = null;
         }
      }

      return spell;
   }

   /**
    * Find the lowercase string that corresponds to a spell name
    *
    * @param s - spell
    * @return string such that it is the lowercase version of the spell minus underscores
    */
   public static String recode (Spells s)
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

   /**
    * Converts a string to have it's first letter of each word be in upper case, and all other letters lower case.
    *
    * @param str - String to convert.
    * @return String with correct formatting.
    */
   public static String firstLetterCapitalize (String str)
   {
      StringBuilder sb = new StringBuilder();
      String[] wordList = str.split(" ");
      for (String s : wordList)
      {
         sb.append(s.substring(0, 1).toUpperCase());
         if (s.length() > 1)
         {
            sb.append(s.substring(1, s.length()).toLowerCase());
         }
         sb.append(" ");
      }
      return sb.substring(0, sb.length() - 1);
   }
}
