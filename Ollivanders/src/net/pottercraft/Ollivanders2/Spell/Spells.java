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
   AMATO_ANIMO_ANIMATO_ANIMAGUS,
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
   FINESTRA,
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
   LOQUELA_INEPTIAS,
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
   POINT_ME,
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
   SNUFFLIFORS,
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
    * Get a Spells enum from a string.
    *
    * @param spellString
    * @return
    */
   public static Spells spellsFromString (String spellString)
   {
      Spells spell = null;

      try
      {
         spell = Spells.valueOf(spellString);
      }
      catch (Exception e) { }

      return spell;
   }
}
