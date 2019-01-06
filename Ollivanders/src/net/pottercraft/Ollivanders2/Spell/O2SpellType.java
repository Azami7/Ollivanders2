package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2API;

/**
 * Represents allowable spells.
 *
 * @author Azami7
 * @version Ollivanders2
 */

public enum O2SpellType
{
   ACCIO(ACCIO.class),
   AGUAMENTI(AGUAMENTI.class),
   ALARTE_ASCENDARE(ALARTE_ASCENDARE.class),
   ALIQUAM_FLOO(ALIQUAM_FLOO.class),
   ALOHOMORA(ALOHOMORA.class),
   AMATO_ANIMO_ANIMATO_ANIMAGUS(AMATO_ANIMO_ANIMATO_ANIMAGUS.class),
   APARECIUM(APARECIUM.class),
   APPARATE(APPARATE.class),
   AQUA_ERUCTO(AQUA_ERUCTO.class),
   ARANIA_EXUMAI(ARANIA_EXUMAI.class),
   ARRESTO_MOMENTUM(ARRESTO_MOMENTUM.class),
   ASCENDIO(ASCENDIO.class),
   ASTROLOGIA(ASTROLOGIA.class),
   AVADA_KEDAVRA(AVADA_KEDAVRA.class),
   AVIFORS(AVIFORS.class),
   AVIS(AVIS.class),
   BOMBARDA(BOMBARDA.class),
   BOMBARDA_MAXIMA(BOMBARDA_MAXIMA.class),
   BOTHYNUS(BOTHYNUS.class),
   BOTHYNUS_DUO(BOTHYNUS_DUO.class),
   BOTHYNUS_TRIA(BOTHYNUS_TRIA.class),
   BRACKIUM_EMENDO(BRACKIUM_EMENDO.class),
   CALAMUS(CALAMUS.class),
   CARPE_RETRACTUM(CARPE_RETRACTUM.class),
   COLLOPORTUS(COLLOPORTUS.class),
   COLORO_ALBUM(COLORO_ALBUM.class),
   COLORO_AURANTIACO(COLORO_AURANTIACO.class),
   COLORO_CAERULUS(COLORO_CAERULUS.class),
   COLORO_FLAVO(COLORO_FLAVO.class),
   COLORO_OSTRUM(COLORO_OSTRUM.class),
   COLORO_VERIDI(COLORO_VERIDI.class),
   COLORO_VERMICULO(COLORO_VERMICULO.class),
   COLOVARIA(COLOVARIA.class),
   COMETES(COMETES.class),
   COMETES_DUO(COMETES_DUO.class),
   CONFUNDO(CONFUNDO.class),
   CONFUNDUS_DUO(CONFUNDUS_DUO.class),
   CRESCERE_PROTEGAT(CRESCERE_PROTEGAT.class),
   DEFODIO(DEFODIO.class),
   DELETRIUS(DELETRIUS.class),
   DEPRIMO(DEPRIMO.class),
   DEPULSO(DEPULSO.class),
   DIFFINDO(DIFFINDO.class),
   DISSENDIUM(DISSENDIUM.class),
   DRACONIFORS(DRACONIFORS.class),
   DUCKLIFORS(DUCKLIFORS.class),
   DURO(DURO.class),
   EBUBLIO(EBUBLIO.class),
   ENGORGIO(ENGORGIO.class),
   ENTOMORPHIS(ENTOMORPHIS.class),
   EPISKEY(EPISKEY.class),
   ET_INTERFICIAM_ANIMAM_LIGAVERIS(ET_INTERFICIAM_ANIMAM_LIGAVERIS.class),
   EQUUSIFORS(EQUUSIFORS.class),
   EVANESCO(EVANESCO.class),
   FLIPENDO(FLIPENDO.class),
   EXPELLIARMUS(EXPELLIARMUS.class),
   FATUUS_AURUM(FATUUS_AURUM.class),
   FIANTO_DURI(FIANTO_DURI.class),
   FIENDFYRE(FIENDFYRE.class),
   FINESTRA(FINESTRA.class),
   FINITE_INCANTATEM(FINITE_INCANTATEM.class),
   FLAGRANTE(FLAGRANTE.class),
   FRANGE_LIGNEA(FRANGE_LIGNEA.class),
   FUMOS(FUMOS.class),
   FUMOS_DUO(FUMOS_DUO.class),
   GEMINO(GEMINO.class),
   GLACIUS(GLACIUS.class),
   GLACIUS_DUO(GLACIUS_DUO.class),
   GLACIUS_TRIA(GLACIUS_TRIA.class),
   HARMONIA_NECTERE_PASSUS(HARMONIA_NECTERE_PASSUS.class),
   HERBIFORS(HERBIFORS.class),
   HERBIVICUS(HERBIVICUS.class),
   HORREAT_PROTEGAT(HORREAT_PROTEGAT.class),
   IMMOBULUS(IMMOBULUS.class),
   IMPEDIMENTA(IMPEDIMENTA.class),
   INCARNATIO_DEVITO(INCARNATIO_DEVITO.class),
   INCARNATIO_EQUUS(INCARNATIO_EQUUS.class),
   INCARNATIO_FELIS(INCARNATIO_FELIS.class),
   INCARNATIO_LAMA(INCARNATIO_LAMA.class),
   INCARNATIO_LUPI(INCARNATIO_LUPI.class),
   INCARNATIO_PORCILLI(INCARNATIO_PORCILLI.class),
   INCARNATIO_URSUS(INCARNATIO_URSUS.class),
   INCARNATIO_VACCULA(INCARNATIO_VACCULA.class),
   INCENDIO(INCENDIO.class),
   INCENDIO_DUO(INCENDIO_DUO.class),
   INCENDIO_TRIA(INCENDIO_TRIA.class),
   INFORMOUS(INFORMOUS.class),
   INTUEOR(INTUEOR.class),
   LACARNUM_INFLAMARI(LACARNUM_INFLAMARI.class),
   LAPIDO(LAPIDO.class),
   LAPIFORS(LAPIFORS.class),
   LEGILIMENS(LEGILIMENS.class),
   LEVICORPUS(LEVICORPUS.class),
   LIBERACORPUS(LIBERACORPUS.class),
   LIGATIS_COR(LIGATIS_COR.class),
   LOQUELA_INEPTIAS(LOQUELA_INEPTIAS.class),
   LUMOS(LUMOS.class),
   LUMOS_DUO(LUMOS_DUO.class),
   LUMOS_MAXIMA(LUMOS_MAXIMA.class),
   LUMOS_SOLEM(LUMOS_SOLEM.class),
   MANTEIA_KENTAVROS(MANTEIA_KENTAVROS.class),
   MELOFORS(MELOFORS.class),
   METELOJINX(METELOJINX.class),
   METELOJINX_RECANTO(METELOJINX_RECANTO.class),
   MOLLIARE(MOLLIARE.class),
   MORSMORDRE(MORSMORDRE.class),
   MORTUOS_SUSCITATE(MORTUOS_SUSCITATE.class),
   MOV_FOTIA(MOV_FOTIA.class),
   MUCUS_AD_NAUSEAM(MUCUS_AD_NAUSEAM.class),
   MUFFLIATO(MUFFLIATO.class),
   MULTICORFORS(MULTICORFORS.class),
   NOX(NOX.class),
   NULLUM_APPAREBIT(NULLUM_APPAREBIT.class),
   NULLUM_EVANESCUNT(NULLUM_EVANESCUNT.class),
   OBLIVIATE(OBLIVIATE.class),
   OBSCURO(OBSCURO.class),
   OPPUGNO(OPPUGNO.class),
   OVOGNOSIS(net.pottercraft.Ollivanders2.Spell.OVOGNOSIS.class),
   PACK(PACK.class),
   PARTIS_TEMPORUS(PARTIS_TEMPORUS.class),
   PERICULUM(PERICULUM.class),
   PERICULUM_DUO(PERICULUM_DUO.class),
   PETRIFICUS_TOTALUS(PETRIFICUS_TOTALUS.class),
   PIERTOTUM_LOCOMOTOR(PIERTOTUM_LOCOMOTOR.class),
   POINT_ME(POINT_ME.class),
   PORFYRO_ASTERI(PORFYRO_ASTERI.class),
   PORFYRO_ASTERI_DUO(PORFYRO_ASTERI_DUO.class),
   PORFYRO_ASTERI_TRIA(PORFYRO_ASTERI_TRIA.class),
   PORTUS(PORTUS.class),
   PRAEPANDO(PRAEPANDO.class),
   PRIOR_INCANTATO(PRIOR_INCANTATO.class),
   PROPHETEIA(PROPHETEIA.class),
   PROTEGO(PROTEGO.class),
   PROTEGO_HORRIBILIS(PROTEGO_HORRIBILIS.class),
   PROTEGO_MAXIMA(PROTEGO_MAXIMA.class),
   PROTEGO_TOTALUM(PROTEGO_TOTALUM.class),
   PYROSVESTIRAS(PYROSVESTIRAS.class),
   REDUCIO(REDUCIO.class),
   REDUCTO(REDUCTO.class),
   REPARIFARGE(REPARIFARGE.class),
   REPARIFORS(REPARIFORS.class),
   REPARO(REPARO.class),
   REPELLO_MUGGLETON(REPELLO_MUGGLETON.class),
   SCUTO_CONTERAM(SCUTO_CONTERAM.class),
   SILENCIO(SILENCIO.class),
   SNUFFLIFORS(SNUFFLIFORS.class),
   SPONGIFY(SPONGIFY.class),
   STUPEFY(STUPEFY.class),
   TERGEO(TERGEO.class),
   VENTO_FOLIO(VENTO_FOLIO.class),
   VERA_VERTO(VERA_VERTO.class),
   VERDIMILLIOUS(VERDIMILLIOUS.class),
   VERDIMILLIOUS_DUO(VERDIMILLIOUS_DUO.class),
   VOLATUS(VOLATUS.class),
   WINGARDIUM_LEVIOSA(WINGARDIUM_LEVIOSA.class),;

   Class className;

   /**
    * Constructor
    *
    * @param className the name of the spell class associated with this spell type
    */
   O2SpellType (Class className)
   {
      this.className = className;
   }

   /**
    * Get the class name associated with this spell type
    *
    * @return the classname for this spell type
    */
   public Class getClassName ()
   {
      return className;
   }

   /**
    * Get the spell name for this spell type.
    *
    * @return the spell name for this spell type.
    */
   public String getSpellName ()
   {
      String spellTypeString = this.toString().toLowerCase();
      String name = Ollivanders2API.common.firstLetterCapitalize(spellTypeString.replace("_", " "));

      return name;
   }

   /**
    * Get a O2SpellType enum from a string. This should be used as the opposite of toString() on the enum.
    *
    * @param spellString the name of the spell type, ex. "AQUA_ERUCTO"
    * @return the spell type
    */
   public static O2SpellType spellTypeFromString (String spellString)
   {
      O2SpellType spellType = null;

      try
      {
         spellType = O2SpellType.valueOf(spellString);
      }
      catch (Exception e) { }

      return spellType;
   }
}
