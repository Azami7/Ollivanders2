package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
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
   ACCIO(ACCIO.class, Ollivanders2Common.MagicLevel.OWL),
   AGUAMENTI(AGUAMENTI.class, Ollivanders2Common.MagicLevel.OWL),
   ALARTE_ASCENDARE(ALARTE_ASCENDARE.class, Ollivanders2Common.MagicLevel.OWL),
   ALIQUAM_FLOO(ALIQUAM_FLOO.class, Ollivanders2Common.MagicLevel.NEWT),
   ALOHOMORA(ALOHOMORA.class, Ollivanders2Common.MagicLevel.BEGINNER),
   AMATO_ANIMO_ANIMATO_ANIMAGUS(AMATO_ANIMO_ANIMATO_ANIMAGUS.class, Ollivanders2Common.MagicLevel.NEWT),
   APARECIUM(APARECIUM.class, Ollivanders2Common.MagicLevel.NEWT),
   APPARATE(APPARATE.class, Ollivanders2Common.MagicLevel.NEWT),
   AQUA_ERUCTO(AQUA_ERUCTO.class, Ollivanders2Common.MagicLevel.OWL),
   ARANIA_EXUMAI(ARANIA_EXUMAI.class, Ollivanders2Common.MagicLevel.OWL),
   ARRESTO_MOMENTUM(ARRESTO_MOMENTUM.class, Ollivanders2Common.MagicLevel.OWL),
   ASCENDIO(ASCENDIO.class, Ollivanders2Common.MagicLevel.NEWT),
   ASTROLOGIA(ASTROLOGIA.class, Ollivanders2Common.MagicLevel.BEGINNER),
   AVADA_KEDAVRA(AVADA_KEDAVRA.class, Ollivanders2Common.MagicLevel.NEWT),
   AVIFORS(AVIFORS.class, Ollivanders2Common.MagicLevel.BEGINNER),
   AVIS(AVIS.class, Ollivanders2Common.MagicLevel.NEWT),
   BAO_ZHONG_CHA(BAO_ZHONG_CHA.class, Ollivanders2Common.MagicLevel.BEGINNER),
   BOMBARDA(BOMBARDA.class, Ollivanders2Common.MagicLevel.BEGINNER),
   BOMBARDA_MAXIMA(BOMBARDA_MAXIMA.class, Ollivanders2Common.MagicLevel.BEGINNER),
   BOTHYNUS(BOTHYNUS.class, Ollivanders2Common.MagicLevel.BEGINNER),
   BOTHYNUS_DUO(BOTHYNUS_DUO.class, Ollivanders2Common.MagicLevel.OWL),
   BOTHYNUS_TRIA(BOTHYNUS_TRIA.class, Ollivanders2Common.MagicLevel.OWL),
   BRACKIUM_EMENDO(BRACKIUM_EMENDO.class, Ollivanders2Common.MagicLevel.OWL),
   CALAMUS(CALAMUS.class, Ollivanders2Common.MagicLevel.BEGINNER),
   CARPE_RETRACTUM(CARPE_RETRACTUM.class, Ollivanders2Common.MagicLevel.BEGINNER),
   CARTOMANCIE(CARTOMANCIE.class, Ollivanders2Common.MagicLevel.OWL),
   CHARTIA(CHARTIA.class, Ollivanders2Common.MagicLevel.BEGINNER),
   COLLOPORTUS(COLLOPORTUS.class, Ollivanders2Common.MagicLevel.BEGINNER),
   COLOVARIA(COLOVARIA.class, Ollivanders2Common.MagicLevel.BEGINNER),
   COLOVARIA_ALBUM(COLOVARIA_ALBUM.class, Ollivanders2Common.MagicLevel.OWL),
   COLOVARIA_AURANTIACO(COLOVARIA_AURANTIACO.class, Ollivanders2Common.MagicLevel.OWL),
   COLOVARIA_CAERULUS(COLOVARIA_CAERULUS.class, Ollivanders2Common.MagicLevel.OWL),
   COLOVARIA_FLAVO(COLOVARIA_FLAVO.class, Ollivanders2Common.MagicLevel.OWL),
   COLOVARIA_OSTRUM(COLOVARIA_OSTRUM.class, Ollivanders2Common.MagicLevel.OWL),
   COLOVARIA_VERIDI(COLOVARIA_VERIDI.class, Ollivanders2Common.MagicLevel.OWL),
   COLOVARIA_VERMICULO(COLOVARIA_VERMICULO.class, Ollivanders2Common.MagicLevel.OWL),
   COMETES(COMETES.class, Ollivanders2Common.MagicLevel.BEGINNER),
   COMETES_DUO(COMETES_DUO.class, Ollivanders2Common.MagicLevel.OWL),
   CONFUNDO(CONFUNDO.class, Ollivanders2Common.MagicLevel.NEWT),
   CONFUNDUS_DUO(CONFUNDUS_DUO.class, Ollivanders2Common.MagicLevel.NEWT),
   CRESCERE_PROTEGAT(CRESCERE_PROTEGAT.class, Ollivanders2Common.MagicLevel.NEWT),
   DEFODIO(DEFODIO.class, Ollivanders2Common.MagicLevel.NEWT),
   DELETRIUS(DELETRIUS.class, Ollivanders2Common.MagicLevel.OWL),
   DEPRIMO(DEPRIMO.class, Ollivanders2Common.MagicLevel.BEGINNER),
   DEPULSO(DEPULSO.class, Ollivanders2Common.MagicLevel.OWL),
   DIFFINDO(DIFFINDO.class, Ollivanders2Common.MagicLevel.BEGINNER),
   DISSENDIUM(DISSENDIUM.class, Ollivanders2Common.MagicLevel.OWL),
   DRACONIFORS(DRACONIFORS.class, Ollivanders2Common.MagicLevel.BEGINNER),
   DUCKLIFORS(DUCKLIFORS.class, Ollivanders2Common.MagicLevel.BEGINNER),
   DURO(DURO.class, Ollivanders2Common.MagicLevel.BEGINNER),
   EBUBLIO(EBUBLIO.class, Ollivanders2Common.MagicLevel.OWL),
   ENGORGIO(ENGORGIO.class, Ollivanders2Common.MagicLevel.BEGINNER),
   ENTOMORPHIS(ENTOMORPHIS.class, Ollivanders2Common.MagicLevel.OWL),
   EPISKEY(EPISKEY.class, Ollivanders2Common.MagicLevel.OWL),
   ET_INTERFICIAM_ANIMAM_LIGAVERIS(ET_INTERFICIAM_ANIMAM_LIGAVERIS.class, Ollivanders2Common.MagicLevel.EXPERT),
   EQUUSIFORS(EQUUSIFORS.class, Ollivanders2Common.MagicLevel.OWL),
   EVANESCO(EVANESCO.class, Ollivanders2Common.MagicLevel.OWL),
   EXPELLIARMUS(EXPELLIARMUS.class, Ollivanders2Common.MagicLevel.BEGINNER),
   FATUUS_AURUM(FATUUS_AURUM.class, Ollivanders2Common.MagicLevel.BEGINNER),
   FIANTO_DURI(FIANTO_DURI.class, Ollivanders2Common.MagicLevel.NEWT),
   FIENDFYRE(FIENDFYRE.class, Ollivanders2Common.MagicLevel.EXPERT),
   FINESTRA(FINESTRA.class, Ollivanders2Common.MagicLevel.OWL),
   FINITE_INCANTATEM(FINITE_INCANTATEM.class, Ollivanders2Common.MagicLevel.OWL),
   FLAGRANTE(FLAGRANTE.class, Ollivanders2Common.MagicLevel.NEWT),
   FLIPENDO(FLIPENDO.class, Ollivanders2Common.MagicLevel.BEGINNER),
   FRANGE_LIGNEA(FRANGE_LIGNEA.class, Ollivanders2Common.MagicLevel.OWL),
   FUMOS(FUMOS.class, Ollivanders2Common.MagicLevel.OWL),
   FUMOS_DUO(FUMOS_DUO.class, Ollivanders2Common.MagicLevel.OWL),
   GEMINIO(GEMINIO.class, Ollivanders2Common.MagicLevel.NEWT),
   GLACIUS(GLACIUS.class, Ollivanders2Common.MagicLevel.OWL),
   GLACIUS_DUO(GLACIUS_DUO.class, Ollivanders2Common.MagicLevel.OWL),
   GLACIUS_TRIA(GLACIUS_TRIA.class, Ollivanders2Common.MagicLevel.NEWT),
   HARMONIA_NECTERE_PASSUS(HARMONIA_NECTERE_PASSUS.class, Ollivanders2Common.MagicLevel.EXPERT),
   HERBIFORS(HERBIFORS.class, Ollivanders2Common.MagicLevel.BEGINNER),
   HERBIVICUS(HERBIVICUS.class, Ollivanders2Common.MagicLevel.NEWT),
   HORREAT_PROTEGAT(HORREAT_PROTEGAT.class, Ollivanders2Common.MagicLevel.NEWT),
   IMMOBULUS(IMMOBULUS.class, Ollivanders2Common.MagicLevel.BEGINNER),
   IMPEDIMENTA(IMPEDIMENTA.class, Ollivanders2Common.MagicLevel.BEGINNER),
   INCARNATIO_DEVITO(INCARNATIO_DEVITO.class, Ollivanders2Common.MagicLevel.NEWT),
   INCARNATIO_EQUUS(INCARNATIO_EQUUS.class, Ollivanders2Common.MagicLevel.NEWT),
   INCARNATIO_FELIS(INCARNATIO_FELIS.class, Ollivanders2Common.MagicLevel.NEWT),
   INCARNATIO_LAMA(INCARNATIO_LAMA.class, Ollivanders2Common.MagicLevel.NEWT),
   INCARNATIO_LUPI(INCARNATIO_LUPI.class, Ollivanders2Common.MagicLevel.NEWT),
   INCARNATIO_PORCILLI(INCARNATIO_PORCILLI.class, Ollivanders2Common.MagicLevel.NEWT),
   INCARNATIO_URSUS(INCARNATIO_URSUS.class, Ollivanders2Common.MagicLevel.NEWT),
   INCARNATIO_VACCULA(INCARNATIO_VACCULA.class, Ollivanders2Common.MagicLevel.NEWT),
   INCENDIO(INCENDIO.class, Ollivanders2Common.MagicLevel.BEGINNER),
   INCENDIO_DUO(INCENDIO_DUO.class, Ollivanders2Common.MagicLevel.OWL),
   INCENDIO_TRIA(INCENDIO_TRIA.class, Ollivanders2Common.MagicLevel.NEWT),
   INFORMOUS(INFORMOUS.class, Ollivanders2Common.MagicLevel.OWL),
   INTUEOR(INTUEOR.class, Ollivanders2Common.MagicLevel.BEGINNER),
   LACARNUM_INFLAMARI(LACARNUM_INFLAMARI.class, Ollivanders2Common.MagicLevel.NEWT),
   LAPIDO(LAPIDO.class, Ollivanders2Common.MagicLevel.BEGINNER),
   LAPIFORS(LAPIFORS.class, Ollivanders2Common.MagicLevel.BEGINNER),
   LEGILIMENS(LEGILIMENS.class, Ollivanders2Common.MagicLevel.NEWT),
   LEVICORPUS(LEVICORPUS.class, Ollivanders2Common.MagicLevel.OWL),
   LIBERACORPUS(LIBERACORPUS.class, Ollivanders2Common.MagicLevel.OWL),
   LIGATIS_COR(LIGATIS_COR.class, Ollivanders2Common.MagicLevel.NEWT),
   LOQUELA_INEPTIAS(LOQUELA_INEPTIAS.class, Ollivanders2Common.MagicLevel.BEGINNER),
   LUMOS(LUMOS.class, Ollivanders2Common.MagicLevel.BEGINNER),
   LUMOS_DUO(LUMOS_DUO.class, Ollivanders2Common.MagicLevel.BEGINNER),
   LUMOS_MAXIMA(LUMOS_MAXIMA.class, Ollivanders2Common.MagicLevel.BEGINNER),
   LUMOS_SOLEM(LUMOS_SOLEM.class, Ollivanders2Common.MagicLevel.NEWT),
   MANTEIA_KENTAVROS(MANTEIA_KENTAVROS.class, Ollivanders2Common.MagicLevel.NEWT),
   MELOFORS(MELOFORS.class, Ollivanders2Common.MagicLevel.BEGINNER),
   METELOJINX(METELOJINX.class, Ollivanders2Common.MagicLevel.OWL),
   METELOJINX_RECANTO(METELOJINX_RECANTO.class, Ollivanders2Common.MagicLevel.OWL),
   MOLLIARE(MOLLIARE.class, Ollivanders2Common.MagicLevel.OWL),
   MORSMORDRE(MORSMORDRE.class, Ollivanders2Common.MagicLevel.NEWT),
   MORTUOS_SUSCITATE(MORTUOS_SUSCITATE.class, Ollivanders2Common.MagicLevel.NEWT),
   MOV_FOTIA(MOV_FOTIA.class, Ollivanders2Common.MagicLevel.OWL),
   MUCUS_AD_NAUSEAM(MUCUS_AD_NAUSEAM.class, Ollivanders2Common.MagicLevel.BEGINNER),
   MUFFLIATO(MUFFLIATO.class, Ollivanders2Common.MagicLevel.NEWT),
   MULTICORFORS(MULTICORFORS.class, Ollivanders2Common.MagicLevel.BEGINNER),
   NOX(NOX.class, Ollivanders2Common.MagicLevel.BEGINNER),
   NULLUM_APPAREBIT(NULLUM_APPAREBIT.class, Ollivanders2Common.MagicLevel.NEWT),
   NULLUM_EVANESCUNT(NULLUM_EVANESCUNT.class, Ollivanders2Common.MagicLevel.NEWT),
   OBLIVIATE(OBLIVIATE.class, Ollivanders2Common.MagicLevel.BEGINNER),
   OBSCURO(OBSCURO.class, Ollivanders2Common.MagicLevel.BEGINNER),
   OPPUGNO(OPPUGNO.class, Ollivanders2Common.MagicLevel.NEWT),
   OVOGNOSIS(OVOGNOSIS.class, Ollivanders2Common.MagicLevel.OWL),
   PACK(PACK.class, Ollivanders2Common.MagicLevel.BEGINNER),
   PARTIS_TEMPORUS(PARTIS_TEMPORUS.class, Ollivanders2Common.MagicLevel.NEWT),
   PERICULUM(PERICULUM.class, Ollivanders2Common.MagicLevel.BEGINNER),
   PERICULUM_DUO(PERICULUM_DUO.class, Ollivanders2Common.MagicLevel.OWL),
   PETRIFICUS_TOTALUS(PETRIFICUS_TOTALUS.class, Ollivanders2Common.MagicLevel.OWL),
   PIERTOTUM_LOCOMOTOR(PIERTOTUM_LOCOMOTOR.class, Ollivanders2Common.MagicLevel.OWL),
   POINT_ME(POINT_ME.class, Ollivanders2Common.MagicLevel.BEGINNER),
   PORFYRO_ASTERI(PORFYRO_ASTERI.class, Ollivanders2Common.MagicLevel.BEGINNER),
   PORFYRO_ASTERI_DUO(PORFYRO_ASTERI_DUO.class, Ollivanders2Common.MagicLevel.OWL),
   PORFYRO_ASTERI_TRIA(PORFYRO_ASTERI_TRIA.class, Ollivanders2Common.MagicLevel.OWL),
   PORTUS(PORTUS.class, Ollivanders2Common.MagicLevel.NEWT),
   PRIOR_INCANTATO(PRIOR_INCANTATO.class, Ollivanders2Common.MagicLevel.OWL),
   PROPHETEIA(PROPHETEIA.class, Ollivanders2Common.MagicLevel.OWL),
   PROTEGO(PROTEGO.class, Ollivanders2Common.MagicLevel.OWL),
   PROTEGO_HORRIBILIS(PROTEGO_HORRIBILIS.class, Ollivanders2Common.MagicLevel.EXPERT),
   PROTEGO_MAXIMA(PROTEGO_MAXIMA.class, Ollivanders2Common.MagicLevel.NEWT),
   PROTEGO_TOTALUM(PROTEGO_TOTALUM.class, Ollivanders2Common.MagicLevel.NEWT),
   PYROSVESTIRAS(PYROSVESTIRAS.class, Ollivanders2Common.MagicLevel.BEGINNER),
   REDUCIO(REDUCIO.class, Ollivanders2Common.MagicLevel.BEGINNER),
   REDUCTO(REDUCTO.class, Ollivanders2Common.MagicLevel.OWL),
   REPARIFARGE(REPARIFARGE.class, Ollivanders2Common.MagicLevel.NEWT),
   REPARIFORS(REPARIFORS.class, Ollivanders2Common.MagicLevel.OWL),
   REPARO(REPARO.class, Ollivanders2Common.MagicLevel.BEGINNER),
   REPELLO_MUGGLETON(REPELLO_MUGGLETON.class, Ollivanders2Common.MagicLevel.NEWT),
   SCUTO_CONTERAM(SCUTO_CONTERAM.class, Ollivanders2Common.MagicLevel.NEWT),
   SILENCIO(SILENCIO.class, Ollivanders2Common.MagicLevel.OWL),
   SNUFFLIFORS(SNUFFLIFORS.class, Ollivanders2Common.MagicLevel.BEGINNER),
   SPONGIFY(SPONGIFY.class, Ollivanders2Common.MagicLevel.BEGINNER),
   STUPEFY(STUPEFY.class, Ollivanders2Common.MagicLevel.OWL),
   TERGEO(TERGEO.class, Ollivanders2Common.MagicLevel.OWL),
   VENTO_FOLIO(VENTO_FOLIO.class, Ollivanders2Common.MagicLevel.NEWT),
   VERA_VERTO(VERA_VERTO.class, Ollivanders2Common.MagicLevel.BEGINNER),
   VERDIMILLIOUS(VERDIMILLIOUS.class, Ollivanders2Common.MagicLevel.BEGINNER),
   VERDIMILLIOUS_DUO(VERDIMILLIOUS_DUO.class, Ollivanders2Common.MagicLevel.OWL),
   VOLATUS(VOLATUS.class, Ollivanders2Common.MagicLevel.NEWT),
   WINGARDIUM_LEVIOSA(WINGARDIUM_LEVIOSA.class, Ollivanders2Common.MagicLevel.BEGINNER),
   //end
   ;

   /**
    * The class of spell this creates
    */
   private final Class<?> className;

   /**
    * The level of this spell
    */
   private final Ollivanders2Common.MagicLevel level;

   /**
    * Constructor
    *
    * @param className the name of the spell class associated with this spell type
    * @param level the level of this spell
    */
   O2SpellType(@NotNull Class<?> className, Ollivanders2Common.MagicLevel level)
   {
      this.className = className;
      this.level = level;
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
    * Get the level for this spell.
    *
    * @return the level of this spell
    */
   @NotNull Ollivanders2Common.MagicLevel getLevel()
   {
      return level;
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
