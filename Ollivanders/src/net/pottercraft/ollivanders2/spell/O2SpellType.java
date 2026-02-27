package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.common.MagicLevel;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * All supported spells.
 *
 * @author Azami7
 */
public enum O2SpellType {
    /**
     * {@link ABERTO}
     */
    ABERTO(ABERTO.class, MagicLevel.BEGINNER),
    /**
     * {@link ACCIO}
     */
    ACCIO(ACCIO.class, MagicLevel.OWL),
    /**
     * {@link AGUAMENTI}
     */
    AGUAMENTI(AGUAMENTI.class, MagicLevel.NEWT),
    /**
     * {@link ALARTE_ASCENDARE}
     */
    ALARTE_ASCENDARE(ALARTE_ASCENDARE.class, MagicLevel.NEWT),
    /**
     * {@link ALIQUAM_FLOO}
     */
    ALIQUAM_FLOO(ALIQUAM_FLOO.class, MagicLevel.NEWT),
    /**
     * {@link ALOHOMORA}
     */
    ALOHOMORA(ALOHOMORA.class, MagicLevel.BEGINNER),
    /**
     * {@link AMATO_ANIMO_ANIMATO_ANIMAGUS}
     */
    AMATO_ANIMO_ANIMATO_ANIMAGUS(AMATO_ANIMO_ANIMATO_ANIMAGUS.class, MagicLevel.NEWT),
    /**
     * {@link APARECIUM}
     */
    APARECIUM(APARECIUM.class, MagicLevel.BEGINNER),
    /**
     * {@link APPARATE}
     */
    APPARATE(APPARATE.class, MagicLevel.NEWT),
    /**
     * {@link AQUA_ERUCTO}
     */
    AQUA_ERUCTO(AQUA_ERUCTO.class, MagicLevel.OWL),
    /**
     * {@link ARANIA_EXUMAI}
     */
    ARANIA_EXUMAI(ARANIA_EXUMAI.class, MagicLevel.OWL),
    /**
     * {@link ARRESTO_MOMENTUM}
     */
    ARRESTO_MOMENTUM(ARRESTO_MOMENTUM.class, MagicLevel.BEGINNER),
    /**
     * {@link ASCENDIO}
     */
    ASCENDIO(ASCENDIO.class, MagicLevel.NEWT),
    /**
     * {@link ASTROLOGIA}
     */
    ASTROLOGIA(ASTROLOGIA.class, MagicLevel.BEGINNER),
    /**
     * {@link AVADA_KEDAVRA}
     */
    AVADA_KEDAVRA(AVADA_KEDAVRA.class, MagicLevel.NEWT),
    /**
     * {@link AVIFORS}
     */
    AVIFORS(AVIFORS.class, MagicLevel.OWL),
    /**
     * {@link AVIS}
     */
    AVIS(AVIS.class, MagicLevel.NEWT),
    /**
     * {@link BAO_ZHONG_CHA}
     */
    BAO_ZHONG_CHA(BAO_ZHONG_CHA.class, MagicLevel.BEGINNER),
    /**
     * {@link BOMBARDA}
     */
    BOMBARDA(BOMBARDA.class, MagicLevel.OWL),
    /**
     * {@link BOMBARDA_MAXIMA}
     */
    BOMBARDA_MAXIMA(BOMBARDA_MAXIMA.class, MagicLevel.NEWT),
    /**
     * {@link BOS}
     */
    BOS(BOS.class, MagicLevel.NEWT),
    /**
     * {@link BOTHYNUS}
     */
    BOTHYNUS(BOTHYNUS.class, MagicLevel.BEGINNER),
    /**
     * {@link BOTHYNUS_DUO}
     */
    BOTHYNUS_DUO(BOTHYNUS_DUO.class, MagicLevel.OWL),
    /**
     * {@link BOTHYNUS_TRIA}
     */
    BOTHYNUS_TRIA(BOTHYNUS_TRIA.class, MagicLevel.OWL),
    /**
     * {@link BRACKIUM_EMENDO}
     */
    BRACKIUM_EMENDO(BRACKIUM_EMENDO.class, MagicLevel.OWL),
    /**
     * {@link CALAMUS}
     */
    CALAMUS(CALAMUS.class, MagicLevel.BEGINNER),
    /**
     * {@link CANIS}
     */
    CANIS(CANIS.class, MagicLevel.NEWT),
    /**
     * {@link CARPE_RETRACTUM}
     */
    CARPE_RETRACTUM(CARPE_RETRACTUM.class, MagicLevel.OWL),
    /**
     * {@link CARTOMANCIE}
     */
    CARTOMANCIE(CARTOMANCIE.class, MagicLevel.OWL),
    /**
     * {@link CAVE_INIMICUM}
     */
    CAVE_INIMICUM(CAVE_INIMICUM.class, MagicLevel.NEWT),
    /**
     * {@link CELATUM}
     */
    CELATUM(CELATUM.class, MagicLevel.BEGINNER),
    /**
     * {@link CHARTIA}
     */
    CHARTIA(CHARTIA.class, MagicLevel.NEWT),
    /**
     * {@link CISTERN_APERIO}
     */
    CISTERN_APERIO(CISTERN_APERIO.class, MagicLevel.BEGINNER),
    /**
     * {@link COLLOPORTUS}
     */
    COLLOPORTUS(COLLOPORTUS.class, MagicLevel.BEGINNER),
    /**
     * {@link COLOVARIA}
     */
    COLOVARIA(COLOVARIA.class, MagicLevel.OWL),
    /**
     * {@link COLOVARIA_ALBUM}
     */
    COLOVARIA_ALBUM(COLOVARIA_ALBUM.class, MagicLevel.OWL),
    /**
     * {@link COLOVARIA_AURANTIACO}
     */
    COLOVARIA_AURANTIACO(COLOVARIA_AURANTIACO.class, MagicLevel.OWL),
    /**
     * {@link COLOVARIA_CAERULUS}
     */
    COLOVARIA_CAERULUS(COLOVARIA_CAERULUS.class, MagicLevel.OWL),
    /**
     * {@link COLOVARIA_FLAVO}
     */
    COLOVARIA_FLAVO(COLOVARIA_FLAVO.class, MagicLevel.OWL),
    /**
     * {@link COLOVARIA_OSTRUM}
     */
    COLOVARIA_OSTRUM(COLOVARIA_OSTRUM.class, MagicLevel.OWL),
    /**
     * {@link COLOVARIA_VERIDI}
     */
    COLOVARIA_VERIDI(COLOVARIA_VERIDI.class, MagicLevel.OWL),
    /**
     * {@link COLOVARIA_VERMICULO}
     */
    COLOVARIA_VERMICULO(COLOVARIA_VERMICULO.class, MagicLevel.OWL),
    /**
     * {@link COMETES}
     */
    COMETES(COMETES.class, MagicLevel.BEGINNER),
    /**
     * {@link COMETES_DUO}
     */
    COMETES_DUO(COMETES_DUO.class, MagicLevel.OWL),
    /**
     * {@link CONFUNDO}
     */
    CONFUNDO(CONFUNDO.class, MagicLevel.OWL),
    /**
     * {@link CONFUNDUS_DUO}
     */
    CONFUNDUS_DUO(CONFUNDUS_DUO.class, MagicLevel.NEWT),
    /**
     * {@link CRESCERE_PROTEGAT}
     */
    CRESCERE_PROTEGAT(CRESCERE_PROTEGAT.class, MagicLevel.NEWT),
    /**
     * {@link DEFODIO}
     */
    DEFODIO(DEFODIO.class, MagicLevel.NEWT),
    /**
     * {@link DELETRIUS}
     */
    DELETRIUS(DELETRIUS.class, MagicLevel.OWL),
    /**
     * {@link DEPRIMO}
     */
    DEPRIMO(DEPRIMO.class, MagicLevel.OWL),
    /**
     * {@link DEPULSO}
     */
    DEPULSO(DEPULSO.class, MagicLevel.OWL),
    /**
     * {@link DIAMAS_REPARO}
     */
    DIAMAS_REPARO(DIAMAS_REPARO.class, MagicLevel.OWL),
    /**
     * {@link DIFFINDO}
     */
    DIFFINDO(DIFFINDO.class, MagicLevel.OWL),
    /**
     * {@link DISSENDIUM}
     */
    DISSENDIUM(DISSENDIUM.class, MagicLevel.OWL),
    /**
     * {@link DRACONIFORS}
     */
    DRACONIFORS(DRACONIFORS.class, MagicLevel.NEWT),
    /**
     * {@link DUCKLIFORS}
     */
    DUCKLIFORS(DUCKLIFORS.class, MagicLevel.OWL),
    /**
     * {@link DURO}
     */
    DURO(DURO.class, MagicLevel.OWL),
    /**
     * {@link EBUBLIO}
     */
    EBUBLIO(EBUBLIO.class, MagicLevel.NEWT),
    /**
     * {@link ENGORGIO}
     */
    ENGORGIO(ENGORGIO.class, MagicLevel.BEGINNER),
    /**
     * {@link ENTOMORPHIS}
     */
    ENTOMORPHIS(ENTOMORPHIS.class, MagicLevel.NEWT),
    /**
     * {@link EPISKEY}
     */
    EPISKEY(EPISKEY.class, MagicLevel.NEWT),
    /**
     * {@link ET_INTERFICIAM_ANIMAM_LIGAVERIS}
     */
    ET_INTERFICIAM_ANIMAM_LIGAVERIS(ET_INTERFICIAM_ANIMAM_LIGAVERIS.class, MagicLevel.EXPERT),
    /**
     * {@link EQUUS}
     */
    EQUUS(EQUUS.class, MagicLevel.NEWT),
    /**
     * {@link EQUUSIFORS}
     */
    EQUUSIFORS(EQUUSIFORS.class, MagicLevel.OWL),
    /**
     * {@link EVANESCO}
     */
    EVANESCO(EVANESCO.class, MagicLevel.OWL),
    /**
     * {@link EXPELLIARMUS}
     */
    EXPELLIARMUS(EXPELLIARMUS.class, MagicLevel.BEGINNER),
    /**
     * {@link FATUUS_AURUM}
     */
    FATUUS_AURUM(FATUUS_AURUM.class, MagicLevel.OWL),
    /**
     * {@link FELIS}
     */
    FELIS(FELIS.class, MagicLevel.NEWT),
    /**
     * {@link FIANTO_DURI}
     */
    FIANTO_DURI(FIANTO_DURI.class, MagicLevel.OWL),
    /**
     * {@link FIENDFYRE}
     */
    FIENDFYRE(FIENDFYRE.class, MagicLevel.EXPERT),
    /**
     * {@link FINESTRA}
     */
    FINESTRA(FINESTRA.class, MagicLevel.OWL),
    /**
     * {@link FINITE_INCANTATEM}
     */
    FINITE_INCANTATEM(FINITE_INCANTATEM.class, MagicLevel.BEGINNER),
    /**
     * {@link FLAGRANTE}
     */
    FLAGRANTE(FLAGRANTE.class, MagicLevel.NEWT),
    /**
     * {@link FLIPENDO}
     */
    FLIPENDO(FLIPENDO.class, MagicLevel.BEGINNER),
    /**
     * {@link FRANGE_LIGNEA}
     */
    FRANGE_LIGNEA(FRANGE_LIGNEA.class, MagicLevel.OWL),
    /**
     * {@link FUMOS}
     */
    FUMOS(FUMOS.class, MagicLevel.BEGINNER),
    /**
     * {@link FUMOS_DUO}
     */
    FUMOS_DUO(FUMOS_DUO.class, MagicLevel.OWL),
    /**
     * {@link GEMINO}
     */
    GEMINO(GEMINO.class, MagicLevel.NEWT),
    // todo add GEMINIO the charm - https://harrypotter.fandom.com/wiki/Doubling_Charm
    /**
     * {@link GLACIUS}
     */
    GLACIUS(GLACIUS.class, MagicLevel.OWL),
    /**
     * {@link GLACIUS_DUO}
     */
    GLACIUS_DUO(GLACIUS_DUO.class, MagicLevel.OWL),
    /**
     * {@link GLACIUS_TRIA}
     */
    GLACIUS_TRIA(GLACIUS_TRIA.class, MagicLevel.NEWT),
    /**
     * {@link HARMONIA_NECTERE_PASSUS}
     */
    HARMONIA_NECTERE_PASSUS(HARMONIA_NECTERE_PASSUS.class, MagicLevel.EXPERT),
    /**
     * {@link HERBIFORS}
     */
    HERBIFORS(HERBIFORS.class, MagicLevel.BEGINNER),
    /**
     * {@link HERBIVICUS}
     */
    HERBIVICUS(HERBIVICUS.class, MagicLevel.NEWT),
    /**
     * {@link HORREAT_PROTEGAT}
     */
    HORREAT_PROTEGAT(HORREAT_PROTEGAT.class, MagicLevel.NEWT),
    /**
     * {@link IMMOBULUS}
     */
    IMMOBULUS(IMMOBULUS.class, MagicLevel.OWL),
    /**
     * {@link IMPEDIMENTA}
     */
    IMPEDIMENTA(IMPEDIMENTA.class, MagicLevel.OWL),
    /**
     * {@link INCENDIO}
     */
    INCENDIO(INCENDIO.class, MagicLevel.BEGINNER),
    /**
     * {@link INCENDIO_DUO}
     */
    INCENDIO_DUO(INCENDIO_DUO.class, MagicLevel.OWL),
    /**
     * {@link INCENDIO_TRIA}
     */
    INCENDIO_TRIA(INCENDIO_TRIA.class, MagicLevel.NEWT),
    /**
     * {@link INFORMOUS}
     */
    INFORMOUS(INFORMOUS.class, MagicLevel.NEWT),
    /**
     * {@link INTUEOR}
     */
    INTUEOR(INTUEOR.class, MagicLevel.BEGINNER),
    /**
     * {@link LACARNUM_INFLAMARI}
     */
    LACARNUM_INFLAMARI(LACARNUM_INFLAMARI.class, MagicLevel.OWL),
    /**
     * {@link LAGOMORPHA}
     */
    LAGOMORPHA(LAGOMORPHA.class, MagicLevel.OWL),
    /**
     * {@link LAMA}
     */
    LAMA(LAMA.class, MagicLevel.NEWT),
    /**
     * {@link LAPIFORS}
     */
    LAPIFORS(net.pottercraft.ollivanders2.spell.LAPIFORS.class, MagicLevel.OWL),
    /**
     * {@link LEGILIMENS}
     */
    LEGILIMENS(LEGILIMENS.class, MagicLevel.NEWT),
    /**
     * {@link LEPUS_SACCULUM}
     */
    LEPUS_SACCULUM(LEPUS_SACCULUM.class, MagicLevel.BEGINNER),
    /**
     * {@link LEVICORPUS}
     */
    LEVICORPUS(LEVICORPUS.class, MagicLevel.NEWT),
    /**
     * {@link LIBERACORPUS}
     */
    LIBERACORPUS(LIBERACORPUS.class, MagicLevel.NEWT),
    /**
     * {@link LIGATIS_COR}
     */
    LIGATIS_COR(LIGATIS_COR.class, MagicLevel.NEWT),
    /**
     * {@link LOQUELA_INEPTIAS}
     */
    LOQUELA_INEPTIAS(LOQUELA_INEPTIAS.class, MagicLevel.OWL),
    /**
     * {@link LUMOS}
     */
    LUMOS(LUMOS.class, MagicLevel.BEGINNER),
    /**
     * {@link LUMOS_CAERULEUM}
     */
    LUMOS_CAERULEUM(LUMOS_CAERULEUM.class, MagicLevel.OWL),
    /**
     * {@link LUMOS_DUO}
     */
    LUMOS_DUO(LUMOS_DUO.class, MagicLevel.OWL),
    /**
     * {@link LUMOS_FERVENS}
     */
    LUMOS_FERVENS(LUMOS_FERVENS.class, MagicLevel.OWL),
    /*
    /**
     * {@link LUMOS_MAXIMA}
     */
    //LUMOS_MAXIMA(LUMOS_MAXIMA.class, MagicLevel.OWL),
    /**
     * {@link LUMOS_SOLEM}
     */
    LUMOS_SOLEM(LUMOS_SOLEM.class, MagicLevel.NEWT),
    /**
     * {@link MANTEIA_KENTAVROS}
     */
    MANTEIA_KENTAVROS(MANTEIA_KENTAVROS.class, MagicLevel.NEWT),
    /**
     * {@link MEGA_PYRO_PRASINA}
     */
    MEGA_PYRO_PRASINA(MEGA_PYRO_PRASINA.class, MagicLevel.OWL),
    /**
     * {@link MELOFORS}
     */
    MELOFORS(MELOFORS.class, MagicLevel.OWL),
    /**
     * {@link METELOJINX}
     */
    METELOJINX(METELOJINX.class, MagicLevel.OWL),
    /**
     * {@link METELOJINX_RECANTO}
     */
    METELOJINX_RECANTO(METELOJINX_RECANTO.class, MagicLevel.OWL),
    /**
     * {@link MOLLIARE}
     */
    MOLLIARE(MOLLIARE.class, MagicLevel.NEWT),
    /**
     * {@link MORSMORDRE}
     */
    MORSMORDRE(MORSMORDRE.class, MagicLevel.NEWT),
    /**
     * {@link MORTUOS_SUSCITATE}
     */
    MORTUOS_SUSCITATE(MORTUOS_SUSCITATE.class, MagicLevel.NEWT),
    /**
     * {@link MOV_FOTIA}
     */
    MOV_FOTIA(MOV_FOTIA.class, MagicLevel.NEWT),
    /**
     * {@link MUCUS_AD_NAUSEAM}
     */
    MUCUS_AD_NAUSEAM(MUCUS_AD_NAUSEAM.class, MagicLevel.BEGINNER),
    /**
     * {@link MUFFLIATO}
     */
    MUFFLIATO(MUFFLIATO.class, MagicLevel.OWL),
    /**
     * {@link MULTICORFORS}
     */
    MULTICORFORS(MULTICORFORS.class, MagicLevel.BEGINNER),
    /**
     * {@link NOX}
     */
    NOX(NOX.class, MagicLevel.BEGINNER),
    /**
     * {@link NULLUM_APPAREBIT}
     */
    NULLUM_APPAREBIT(NULLUM_APPAREBIT.class, MagicLevel.NEWT),
    /**
     * {@link NULLUM_EVANESCUNT}
     */
    NULLUM_EVANESCUNT(NULLUM_EVANESCUNT.class, MagicLevel.NEWT),
    /**
     * {@link OBLIVIATE}
     */
    OBLIVIATE(OBLIVIATE.class, MagicLevel.NEWT),
    /**
     * {@link OBSCURO}
     */
    OBSCURO(OBSCURO.class, MagicLevel.BEGINNER),
    /**
     * {@link OPPUGNO}
     */
    OPPUGNO(OPPUGNO.class, MagicLevel.NEWT),
    /**
     * {@link OVOGNOSIS}
     */
    OVOGNOSIS(OVOGNOSIS.class, MagicLevel.NEWT),
    /**
     * {@link PACK}
     */
    PACK(PACK.class, MagicLevel.BEGINNER),
    /**
     * {@link PARTIS_TEMPORUS}
     */
    PARTIS_TEMPORUS(PARTIS_TEMPORUS.class, MagicLevel.NEWT),
    /**
     * {@link PERICULUM}
     */
    PERICULUM(PERICULUM.class, MagicLevel.BEGINNER),
    /**
     * {@link PERICULUM_DUO}
     */
    PERICULUM_DUO(PERICULUM_DUO.class, MagicLevel.OWL),
    /**
     * {@link PERMURATE}
     */
    PERMURATE(PERMURATE.class, MagicLevel.BEGINNER),
    /**
     * {@link PETRIFICUS_TOTALUS}
     */
    PETRIFICUS_TOTALUS(PETRIFICUS_TOTALUS.class, MagicLevel.BEGINNER),
    /**
     * {@link PIERTOTUM_LOCOMOTOR}
     */
    PIERTOTUM_LOCOMOTOR(PIERTOTUM_LOCOMOTOR.class, MagicLevel.OWL),
    /**
     * {@link POINT_ME}
     */
    POINT_ME(POINT_ME.class, MagicLevel.BEGINNER),
    /**
     * {@link PORFYRO_ASTERI}
     */
    PORFYRO_ASTERI(PORFYRO_ASTERI.class, MagicLevel.BEGINNER),
    /**
     * {@link PORFYRO_ASTERI_DUO}
     */
    PORFYRO_ASTERI_DUO(PORFYRO_ASTERI_DUO.class, MagicLevel.OWL),
    /**
     * {@link PORFYRO_ASTERI_TRIA}
     */
    PORFYRO_ASTERI_TRIA(PORFYRO_ASTERI_TRIA.class, MagicLevel.OWL),
    /**
     * {@link PORTUS}
     */
    PORTUS(PORTUS.class, MagicLevel.NEWT),
    /**
     * {@link PRIOR_INCANTATO}
     */
    PRIOR_INCANTATO(PRIOR_INCANTATO.class, MagicLevel.NEWT),
    /**
     * {@link PROPHETEIA}
     */
    PROPHETEIA(PROPHETEIA.class, MagicLevel.NEWT),
    /**
     * {@link PROTEGO}
     */
    PROTEGO(PROTEGO.class, MagicLevel.OWL),
    /**
     * {@link PROTEGO_HORRIBILIS}
     */
    PROTEGO_HORRIBILIS(PROTEGO_HORRIBILIS.class, MagicLevel.EXPERT),
    /**
     * {@link PROTEGO_MAXIMA}
     */
    PROTEGO_MAXIMA(PROTEGO_MAXIMA.class, MagicLevel.NEWT),
    /**
     * {@link PROTEGO_TOTALUM}
     */
    PROTEGO_TOTALUM(PROTEGO_TOTALUM.class, MagicLevel.NEWT),
    /**
     * {@link PULLUS}
     */
    PULLUS(PULLUS.class, MagicLevel.NEWT),
    /**
     * {@link PYRO_PRASINA}
     */
    PYRO_PRASINA(PYRO_PRASINA.class, MagicLevel.BEGINNER),
    /**
     * {@link PYROSVESTIRAS}
     */
    PYROSVESTIRAS(PYROSVESTIRAS.class, MagicLevel.NEWT),
    /**
     * {@link RANACULUS_AMPHORAM}
     */
    RANACULUS_AMPHORAM(RANACULUS_AMPHORAM.class, MagicLevel.BEGINNER),
    /**
     * {@link REDUCIO}
     */
    REDUCIO(REDUCIO.class, MagicLevel.BEGINNER),
    /**
     * {@link REDUCTO}
     */
    REDUCTO(REDUCTO.class, MagicLevel.OWL),
    /**
     * {@link REPARIFARGE}
     */
    REPARIFARGE(REPARIFARGE.class, MagicLevel.BEGINNER),
    /**
     * {@link REPARIFORS}
     */
    REPARIFORS(REPARIFORS.class, MagicLevel.OWL),
    /**
     * {@link REPARO}
     */
    REPARO(REPARO.class, MagicLevel.BEGINNER),
    /**
     * {@link REPELLO_MUGGLETON}
     */
    REPELLO_MUGGLETON(REPELLO_MUGGLETON.class, MagicLevel.NEWT),
    /**
     * {@link RICTUSEMPRA}
     */
    RICTUSEMPRA(RICTUSEMPRA.class, MagicLevel.BEGINNER),
    /**
     * {@link SCARABAEUS_FIBULUM}
     */
    SCARABAEUS_FIBULUM(SCARABAEUS_FIBULUM.class, MagicLevel.BEGINNER),
    /**
     * {@link SCUTO_CONTERAM}
     */
    SCUTO_CONTERAM(SCUTO_CONTERAM.class, MagicLevel.NEWT),
    /**
     * {@link SILENCIO}
     */
    SILENCIO(SILENCIO.class, MagicLevel.OWL),
    /**
     * {@link SKURGE}
     */
    SKURGE(SKURGE.class, MagicLevel.BEGINNER),
    /**
     * {@link SNUFFLIFORS}
     */
    SNUFFLIFORS(SNUFFLIFORS.class, MagicLevel.BEGINNER),
    /**
     * {@link SPONGIFY}
     */
    SPONGIFY(SPONGIFY.class, MagicLevel.BEGINNER),
    /**
     * {@link STUPEFY}
     */
    STUPEFY(STUPEFY.class, MagicLevel.OWL),
    /**
     * {@link SUS}
     */
    SUS(SUS.class, MagicLevel.NEWT),
    /**
     * {@link TARANTALLEGRA}
     */
    TARANTALLEGRA(TARANTALLEGRA.class, MagicLevel.BEGINNER),
    /**
     * {@link TERGEO}
     */
    TERGEO(TERGEO.class, MagicLevel.OWL),
    /**
     * {@link TITILLANDO}
     */
    TITILLANDO(TITILLANDO.class, MagicLevel.OWL),
    /**
     * {@link URSUS}
     */
    URSUS(URSUS.class, MagicLevel.NEWT),
    /**
     * {@link VENTO_FOLIO}
     */
    VENTO_FOLIO(VENTO_FOLIO.class, MagicLevel.EXPERT),
    /**
     * {@link VERA_VERTO}
     */
    VERA_VERTO(VERA_VERTO.class, MagicLevel.BEGINNER),
    /**
     * {@link VERDIMILLIOUS}
     */
    VERDIMILLIOUS(VERDIMILLIOUS.class, MagicLevel.BEGINNER),
    /**
     * {@link VERDIMILLIOUS_DUO}
     */
    VERDIMILLIOUS_DUO(VERDIMILLIOUS_DUO.class, MagicLevel.OWL),
    /**
     * {@link VERDIMILLIOUS_TRIA}
     */
    VERDIMILLIOUS_TRIA(VERDIMILLIOUS_TRIA.class, MagicLevel.NEWT),
    /**
     * {@link VERMILLIOUS}
     */
    VERMILLIOUS(VERMILLIOUS.class, MagicLevel.BEGINNER),
    /**
     * {@link VERMILLIOUS_DUO}
     */
    VERMILLIOUS_DUO(VERMILLIOUS_DUO.class, MagicLevel.OWL),
    /**
     * {@link VERMILLIOUS_TRIA}
     */
    VERMILLIOUS_TRIA(VERMILLIOUS_TRIA.class, MagicLevel.NEWT),
    /**
     * {@link VOLATUS}
     */
    VOLATUS(VOLATUS.class, MagicLevel.NEWT),
    /**
     * {@link WINGARDIUM_LEVIOSA}
     */
    WINGARDIUM_LEVIOSA(WINGARDIUM_LEVIOSA.class, MagicLevel.BEGINNER),
    //end
    ;

    /**
     * The class of spell this creates
     */
    private final Class<?> className;

    /**
     * The level of this spell
     */
    private final MagicLevel level;

    /**
     * Constructor
     *
     * @param className the name of the spell class associated with this spell type
     * @param level     the level of this spell
     */
    O2SpellType(@NotNull Class<?> className, MagicLevel level) {
        this.className = className;
        this.level = level;
    }

    /**
     * Get the class name associated with this spell type
     *
     * @return the class name for this spell type
     */
    @NotNull
    public Class<?> getClassName() {
        return className;
    }

    /**
     * Get the level for this spell.
     *
     * @return the level of this spell
     */
    @NotNull
    public MagicLevel getLevel() {
        return level;
    }

    /**
     * Get the spell name for this spell type.
     *
     * @return the spell name for this spell type.
     */
    @NotNull
    public String getSpellName() {
        String spellTypeString = this.toString().toLowerCase();

        return Ollivanders2Common.firstLetterCapitalize(spellTypeString.replace("_", " "));
    }

    /**
     * Get a O2SpellType enum from a string. This should be used as the opposite of toString() on the enum.
     *
     * @param spellString the name of the spell type, ex. "AQUA_ERUCTO"
     * @return the spell type
     */
    @Nullable
    public static O2SpellType spellTypeFromString(@NotNull String spellString) {
        O2SpellType spellType = null;

        try {
            spellType = O2SpellType.valueOf(spellString);
        }
        catch (Exception e) {
            // this is expected when spellString is not a valid spell
        }

        return spellType;
    }
}
