package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents allowable spells.
 *
 * @author Azami7
 * @version Ollivanders2
 */
public enum O2SpellType
{
   ACCIO(net.pottercraft.ollivanders2.spell.ACCIO.class),
   AGUAMENTI(net.pottercraft.ollivanders2.spell.AGUAMENTI.class),
   ALARTE_ASCENDARE(net.pottercraft.ollivanders2.spell.ALARTE_ASCENDARE.class),
   ALIQUAM_FLOO(net.pottercraft.ollivanders2.spell.ALIQUAM_FLOO.class),
   ALOHOMORA(net.pottercraft.ollivanders2.spell.ALOHOMORA.class),
   AMATO_ANIMO_ANIMATO_ANIMAGUS(net.pottercraft.ollivanders2.spell.AMATO_ANIMO_ANIMATO_ANIMAGUS.class),
   APARECIUM(net.pottercraft.ollivanders2.spell.APARECIUM.class),
   APPARATE(net.pottercraft.ollivanders2.spell.APPARATE.class),
   AQUA_ERUCTO(net.pottercraft.ollivanders2.spell.AQUA_ERUCTO.class),
   ARANIA_EXUMAI(net.pottercraft.ollivanders2.spell.ARANIA_EXUMAI.class),
   ARRESTO_MOMENTUM(net.pottercraft.ollivanders2.spell.ARRESTO_MOMENTUM.class),
   ASCENDIO(net.pottercraft.ollivanders2.spell.ASCENDIO.class),
   ASTROLOGIA(net.pottercraft.ollivanders2.spell.ASTROLOGIA.class),
   AVADA_KEDAVRA(net.pottercraft.ollivanders2.spell.AVADA_KEDAVRA.class),
   AVIFORS(net.pottercraft.ollivanders2.spell.AVIFORS.class),
   AVIS(net.pottercraft.ollivanders2.spell.AVIS.class),
   BAO_ZHONG_CHA(net.pottercraft.ollivanders2.spell.BAO_ZHONG_CHA.class),
   BOMBARDA(net.pottercraft.ollivanders2.spell.BOMBARDA.class),
   BOMBARDA_MAXIMA(net.pottercraft.ollivanders2.spell.BOMBARDA_MAXIMA.class),
   BOTHYNUS(net.pottercraft.ollivanders2.spell.BOTHYNUS.class),
   BOTHYNUS_DUO(net.pottercraft.ollivanders2.spell.BOTHYNUS_DUO.class),
   BOTHYNUS_TRIA(net.pottercraft.ollivanders2.spell.BOTHYNUS_TRIA.class),
   BRACKIUM_EMENDO(net.pottercraft.ollivanders2.spell.BRACKIUM_EMENDO.class),
   CALAMUS(net.pottercraft.ollivanders2.spell.CALAMUS.class),
   CARPE_RETRACTUM(net.pottercraft.ollivanders2.spell.CARPE_RETRACTUM.class),
   CARTOMANCIE(net.pottercraft.ollivanders2.spell.CARTOMANCIE.class),
   CHARTIA(net.pottercraft.ollivanders2.spell.CHARTIA.class),
   COLLOPORTUS(net.pottercraft.ollivanders2.spell.COLLOPORTUS.class),
   COLOVARIA(net.pottercraft.ollivanders2.spell.COLOVARIA.class),
   COLOVARIA_ALBUM(COLOVARIA_ALBUM.class),
   COLOVARIA_AURANTIACO(COLOVARIA_AURANTIACO.class),
   COLOVARIA_CAERULUS(COLOVARIA_CAERULUS.class),
   COLOVARIA_FLAVO(COLOVARIA_FLAVO.class),
   COLOVARIA_OSTRUM(net.pottercraft.ollivanders2.spell.COLOVARIA_OSTRUM.class),
   COLOVARIA_VERIDI(net.pottercraft.ollivanders2.spell.COLOVARIA_VERIDI.class),
   COLOVARIA_VERMICULO(COLOVARIA_VERMICULO.class),
   COMETES(net.pottercraft.ollivanders2.spell.COMETES.class),
   COMETES_DUO(net.pottercraft.ollivanders2.spell.COMETES_DUO.class),
   CONFUNDO(net.pottercraft.ollivanders2.spell.CONFUNDO.class),
   CONFUNDUS_DUO(net.pottercraft.ollivanders2.spell.CONFUNDUS_DUO.class),
   CRESCERE_PROTEGAT(net.pottercraft.ollivanders2.spell.CRESCERE_PROTEGAT.class),
   DEFODIO(net.pottercraft.ollivanders2.spell.DEFODIO.class),
   DELETRIUS(net.pottercraft.ollivanders2.spell.DELETRIUS.class),
   DEPRIMO(net.pottercraft.ollivanders2.spell.DEPRIMO.class),
   DEPULSO(net.pottercraft.ollivanders2.spell.DEPULSO.class),
   DIFFINDO(net.pottercraft.ollivanders2.spell.DIFFINDO.class),
   DISSENDIUM(net.pottercraft.ollivanders2.spell.DISSENDIUM.class),
   DRACONIFORS(net.pottercraft.ollivanders2.spell.DRACONIFORS.class),
   DUCKLIFORS(net.pottercraft.ollivanders2.spell.DUCKLIFORS.class),
   DURO(net.pottercraft.ollivanders2.spell.DURO.class),
   EBUBLIO(net.pottercraft.ollivanders2.spell.EBUBLIO.class),
   ENGORGIO(net.pottercraft.ollivanders2.spell.ENGORGIO.class),
   ENTOMORPHIS(net.pottercraft.ollivanders2.spell.ENTOMORPHIS.class),
   EPISKEY(net.pottercraft.ollivanders2.spell.EPISKEY.class),
   ET_INTERFICIAM_ANIMAM_LIGAVERIS(net.pottercraft.ollivanders2.spell.ET_INTERFICIAM_ANIMAM_LIGAVERIS.class),
   EQUUSIFORS(net.pottercraft.ollivanders2.spell.EQUUSIFORS.class),
   EVANESCO(net.pottercraft.ollivanders2.spell.EVANESCO.class),
   FLIPENDO(net.pottercraft.ollivanders2.spell.FLIPENDO.class),
   EXPELLIARMUS(net.pottercraft.ollivanders2.spell.EXPELLIARMUS.class),
   FATUUS_AURUM(net.pottercraft.ollivanders2.spell.FATUUS_AURUM.class),
   FIANTO_DURI(net.pottercraft.ollivanders2.spell.FIANTO_DURI.class),
   FIENDFYRE(net.pottercraft.ollivanders2.spell.FIENDFYRE.class),
   FINESTRA(net.pottercraft.ollivanders2.spell.FINESTRA.class),
   FINITE_INCANTATEM(net.pottercraft.ollivanders2.spell.FINITE_INCANTATEM.class),
   FLAGRANTE(net.pottercraft.ollivanders2.spell.FLAGRANTE.class),
   FRANGE_LIGNEA(net.pottercraft.ollivanders2.spell.FRANGE_LIGNEA.class),
   FUMOS(net.pottercraft.ollivanders2.spell.FUMOS.class),
   FUMOS_DUO(net.pottercraft.ollivanders2.spell.FUMOS_DUO.class),
   GEMINIO(GEMINIO.class),
   GLACIUS(net.pottercraft.ollivanders2.spell.GLACIUS.class),
   GLACIUS_DUO(net.pottercraft.ollivanders2.spell.GLACIUS_DUO.class),
   GLACIUS_TRIA(net.pottercraft.ollivanders2.spell.GLACIUS_TRIA.class),
   HARMONIA_NECTERE_PASSUS(net.pottercraft.ollivanders2.spell.HARMONIA_NECTERE_PASSUS.class),
   HERBIFORS(net.pottercraft.ollivanders2.spell.HERBIFORS.class),
   HERBIVICUS(net.pottercraft.ollivanders2.spell.HERBIVICUS.class),
   HORREAT_PROTEGAT(net.pottercraft.ollivanders2.spell.HORREAT_PROTEGAT.class),
   IMMOBULUS(net.pottercraft.ollivanders2.spell.IMMOBULUS.class),
   IMPEDIMENTA(net.pottercraft.ollivanders2.spell.IMPEDIMENTA.class),
   INCARNATIO_DEVITO(net.pottercraft.ollivanders2.spell.INCARNATIO_DEVITO.class),
   INCARNATIO_EQUUS(net.pottercraft.ollivanders2.spell.INCARNATIO_EQUUS.class),
   INCARNATIO_FELIS(net.pottercraft.ollivanders2.spell.INCARNATIO_FELIS.class),
   INCARNATIO_LAMA(net.pottercraft.ollivanders2.spell.INCARNATIO_LAMA.class),
   INCARNATIO_LUPI(net.pottercraft.ollivanders2.spell.INCARNATIO_LUPI.class),
   INCARNATIO_PORCILLI(net.pottercraft.ollivanders2.spell.INCARNATIO_PORCILLI.class),
   INCARNATIO_URSUS(net.pottercraft.ollivanders2.spell.INCARNATIO_URSUS.class),
   INCARNATIO_VACCULA(net.pottercraft.ollivanders2.spell.INCARNATIO_VACCULA.class),
   INCENDIO(net.pottercraft.ollivanders2.spell.INCENDIO.class),
   INCENDIO_DUO(net.pottercraft.ollivanders2.spell.INCENDIO_DUO.class),
   INCENDIO_TRIA(net.pottercraft.ollivanders2.spell.INCENDIO_TRIA.class),
   INFORMOUS(net.pottercraft.ollivanders2.spell.INFORMOUS.class),
   INTUEOR(net.pottercraft.ollivanders2.spell.INTUEOR.class),
   LACARNUM_INFLAMARI(net.pottercraft.ollivanders2.spell.LACARNUM_INFLAMARI.class),
   LAPIDO(net.pottercraft.ollivanders2.spell.LAPIDO.class),
   LAPIFORS(net.pottercraft.ollivanders2.spell.LAPIFORS.class),
   LEGILIMENS(net.pottercraft.ollivanders2.spell.LEGILIMENS.class),
   LEVICORPUS(net.pottercraft.ollivanders2.spell.LEVICORPUS.class),
   LIBERACORPUS(net.pottercraft.ollivanders2.spell.LIBERACORPUS.class),
   LIGATIS_COR(net.pottercraft.ollivanders2.spell.LIGATIS_COR.class),
   LOQUELA_INEPTIAS(net.pottercraft.ollivanders2.spell.LOQUELA_INEPTIAS.class),
   LUMOS(net.pottercraft.ollivanders2.spell.LUMOS.class),
   LUMOS_DUO(net.pottercraft.ollivanders2.spell.LUMOS_DUO.class),
   LUMOS_MAXIMA(net.pottercraft.ollivanders2.spell.LUMOS_MAXIMA.class),
   LUMOS_SOLEM(net.pottercraft.ollivanders2.spell.LUMOS_SOLEM.class),
   MANTEIA_KENTAVROS(net.pottercraft.ollivanders2.spell.MANTEIA_KENTAVROS.class),
   MELOFORS(net.pottercraft.ollivanders2.spell.MELOFORS.class),
   METELOJINX(net.pottercraft.ollivanders2.spell.METELOJINX.class),
   METELOJINX_RECANTO(net.pottercraft.ollivanders2.spell.METELOJINX_RECANTO.class),
   MOLLIARE(net.pottercraft.ollivanders2.spell.MOLLIARE.class),
   MORSMORDRE(net.pottercraft.ollivanders2.spell.MORSMORDRE.class),
   MORTUOS_SUSCITATE(net.pottercraft.ollivanders2.spell.MORTUOS_SUSCITATE.class),
   MOV_FOTIA(net.pottercraft.ollivanders2.spell.MOV_FOTIA.class),
   MUCUS_AD_NAUSEAM(net.pottercraft.ollivanders2.spell.MUCUS_AD_NAUSEAM.class),
   MUFFLIATO(net.pottercraft.ollivanders2.spell.MUFFLIATO.class),
   MULTICORFORS(net.pottercraft.ollivanders2.spell.MULTICORFORS.class),
   NOX(net.pottercraft.ollivanders2.spell.NOX.class),
   NULLUM_APPAREBIT(net.pottercraft.ollivanders2.spell.NULLUM_APPAREBIT.class),
   NULLUM_EVANESCUNT(net.pottercraft.ollivanders2.spell.NULLUM_EVANESCUNT.class),
   OBLIVIATE(net.pottercraft.ollivanders2.spell.OBLIVIATE.class),
   OBSCURO(net.pottercraft.ollivanders2.spell.OBSCURO.class),
   OPPUGNO(net.pottercraft.ollivanders2.spell.OPPUGNO.class),
   OVOGNOSIS(net.pottercraft.ollivanders2.spell.OVOGNOSIS.class),
   PACK(net.pottercraft.ollivanders2.spell.PACK.class),
   PARTIS_TEMPORUS(net.pottercraft.ollivanders2.spell.PARTIS_TEMPORUS.class),
   PERICULUM(net.pottercraft.ollivanders2.spell.PERICULUM.class),
   PERICULUM_DUO(net.pottercraft.ollivanders2.spell.PERICULUM_DUO.class),
   PETRIFICUS_TOTALUS(net.pottercraft.ollivanders2.spell.PETRIFICUS_TOTALUS.class),
   PIERTOTUM_LOCOMOTOR(net.pottercraft.ollivanders2.spell.PIERTOTUM_LOCOMOTOR.class),
   POINT_ME(net.pottercraft.ollivanders2.spell.POINT_ME.class),
   PORFYRO_ASTERI(net.pottercraft.ollivanders2.spell.PORFYRO_ASTERI.class),
   PORFYRO_ASTERI_DUO(net.pottercraft.ollivanders2.spell.PORFYRO_ASTERI_DUO.class),
   PORFYRO_ASTERI_TRIA(net.pottercraft.ollivanders2.spell.PORFYRO_ASTERI_TRIA.class),
   PORTUS(net.pottercraft.ollivanders2.spell.PORTUS.class),
   PRAEPANDO(net.pottercraft.ollivanders2.spell.PRAEPANDO.class),
   PRIOR_INCANTATO(net.pottercraft.ollivanders2.spell.PRIOR_INCANTATO.class),
   PROPHETEIA(net.pottercraft.ollivanders2.spell.PROPHETEIA.class),
   PROTEGO(net.pottercraft.ollivanders2.spell.PROTEGO.class),
   PROTEGO_HORRIBILIS(net.pottercraft.ollivanders2.spell.PROTEGO_HORRIBILIS.class),
   PROTEGO_MAXIMA(net.pottercraft.ollivanders2.spell.PROTEGO_MAXIMA.class),
   PROTEGO_TOTALUM(net.pottercraft.ollivanders2.spell.PROTEGO_TOTALUM.class),
   PYROSVESTIRAS(net.pottercraft.ollivanders2.spell.PYROSVESTIRAS.class),
   REDUCIO(net.pottercraft.ollivanders2.spell.REDUCIO.class),
   REDUCTO(net.pottercraft.ollivanders2.spell.REDUCTO.class),
   REPARIFARGE(net.pottercraft.ollivanders2.spell.REPARIFARGE.class),
   REPARIFORS(net.pottercraft.ollivanders2.spell.REPARIFORS.class),
   REPARO(net.pottercraft.ollivanders2.spell.REPARO.class),
   REPELLO_MUGGLETON(net.pottercraft.ollivanders2.spell.REPELLO_MUGGLETON.class),
   SCUTO_CONTERAM(net.pottercraft.ollivanders2.spell.SCUTO_CONTERAM.class),
   SILENCIO(net.pottercraft.ollivanders2.spell.SILENCIO.class),
   SNUFFLIFORS(net.pottercraft.ollivanders2.spell.SNUFFLIFORS.class),
   SPONGIFY(net.pottercraft.ollivanders2.spell.SPONGIFY.class),
   STUPEFY(net.pottercraft.ollivanders2.spell.STUPEFY.class),
   TERGEO(net.pottercraft.ollivanders2.spell.TERGEO.class),
   VENTO_FOLIO(net.pottercraft.ollivanders2.spell.VENTO_FOLIO.class),
   VERA_VERTO(net.pottercraft.ollivanders2.spell.VERA_VERTO.class),
   VERDIMILLIOUS(net.pottercraft.ollivanders2.spell.VERDIMILLIOUS.class),
   VERDIMILLIOUS_DUO(net.pottercraft.ollivanders2.spell.VERDIMILLIOUS_DUO.class),
   VOLATUS(net.pottercraft.ollivanders2.spell.VOLATUS.class),
   WINGARDIUM_LEVIOSA(net.pottercraft.ollivanders2.spell.WINGARDIUM_LEVIOSA.class),
   //end
   ;

   final Class<?> className;

   /**
    * Constructor
    *
    * @param className the name of the spell class associated with this spell type
    */
   O2SpellType(@NotNull Class<?> className)
   {
      this.className = className;
   }

   /**
    * Get the class name associated with this spell type
    *
    * @return the classname for this spell type
    */
   @NotNull
   public Class<?> getClassName()
   {
      return className;
   }

   /**
    * Get the spell name for this spell type.
    *
    * @return the spell name for this spell type.
    */
   @NotNull
   public String getSpellName ()
   {
      String spellTypeString = this.toString().toLowerCase();

      return Ollivanders2API.common.firstLetterCapitalize(spellTypeString.replace("_", " "));
   }

   /**
    * Get a O2SpellType enum from a string. This should be used as the opposite of toString() on the enum.
    *
    * @param spellString the name of the spell type, ex. "AQUA_ERUCTO"
    * @return the spell type
    */
   @Nullable
   public static O2SpellType spellTypeFromString(@NotNull String spellString)
   {
      O2SpellType spellType = null;

      try
      {
         spellType = O2SpellType.valueOf(spellString);
      }
      catch (Exception e)
      {
         // this is expected when spellString is not a valid spell
      }

      return spellType;
   }
}
