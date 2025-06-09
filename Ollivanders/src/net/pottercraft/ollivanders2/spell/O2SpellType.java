package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.common.MagicLevel;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * All supported spells.
 */
public enum O2SpellType {
    //todo adjust levels to match books refactor
    ABERTO(ABERTO.class, MagicLevel.BEGINNER),
    ACCIO(ACCIO.class, MagicLevel.OWL),
    AGUAMENTI(AGUAMENTI.class, MagicLevel.NEWT),
    ALARTE_ASCENDARE(ALARTE_ASCENDARE.class, MagicLevel.NEWT),
    ALIQUAM_FLOO(ALIQUAM_FLOO.class, MagicLevel.NEWT),
    ALOHOMORA(ALOHOMORA.class, MagicLevel.BEGINNER),
    AMATO_ANIMO_ANIMATO_ANIMAGUS(AMATO_ANIMO_ANIMATO_ANIMAGUS.class, MagicLevel.NEWT),
    APARECIUM(APARECIUM.class, MagicLevel.BEGINNER),
    APPARATE(APPARATE.class, MagicLevel.NEWT),
    AQUA_ERUCTO(AQUA_ERUCTO.class, MagicLevel.OWL),
    ARANIA_EXUMAI(ARANIA_EXUMAI.class, MagicLevel.OWL),
    ARRESTO_MOMENTUM(ARRESTO_MOMENTUM.class, MagicLevel.BEGINNER),
    ASCENDIO(ASCENDIO.class, MagicLevel.NEWT),
    ASTROLOGIA(ASTROLOGIA.class, MagicLevel.BEGINNER),
    AVADA_KEDAVRA(AVADA_KEDAVRA.class, MagicLevel.NEWT),
    AVIFORS(AVIFORS.class, MagicLevel.OWL),
    AVIS(AVIS.class, MagicLevel.NEWT),
    BAO_ZHONG_CHA(BAO_ZHONG_CHA.class, MagicLevel.BEGINNER),
    BOMBARDA(BOMBARDA.class, MagicLevel.OWL),
    BOMBARDA_MAXIMA(BOMBARDA_MAXIMA.class, MagicLevel.NEWT),
    BOS(BOS.class, MagicLevel.NEWT),
    BOTHYNUS(BOTHYNUS.class, MagicLevel.BEGINNER),
    BOTHYNUS_DUO(BOTHYNUS_DUO.class, MagicLevel.OWL),
    BOTHYNUS_TRIA(BOTHYNUS_TRIA.class, MagicLevel.OWL),
    BRACKIUM_EMENDO(BRACKIUM_EMENDO.class, MagicLevel.OWL),
    CALAMUS(CALAMUS.class, MagicLevel.BEGINNER),
    CANIS(CANIS.class, MagicLevel.NEWT),
    CARPE_RETRACTUM(CARPE_RETRACTUM.class, MagicLevel.OWL),
    CARTOMANCIE(CARTOMANCIE.class, MagicLevel.OWL),
    CHARTIA(CHARTIA.class, MagicLevel.NEWT),
    CISTERN_APERIO(CISTERN_APERIO.class, MagicLevel.BEGINNER),
    COLLOPORTUS(COLLOPORTUS.class, MagicLevel.BEGINNER),
    COLOVARIA(COLOVARIA.class, MagicLevel.OWL),
    COLOVARIA_ALBUM(COLOVARIA_ALBUM.class, MagicLevel.OWL),
    COLOVARIA_AURANTIACO(COLOVARIA_AURANTIACO.class, MagicLevel.OWL),
    COLOVARIA_CAERULUS(COLOVARIA_CAERULUS.class, MagicLevel.OWL),
    COLOVARIA_FLAVO(COLOVARIA_FLAVO.class, MagicLevel.OWL),
    COLOVARIA_OSTRUM(COLOVARIA_OSTRUM.class, MagicLevel.OWL),
    COLOVARIA_VERIDI(COLOVARIA_VERIDI.class, MagicLevel.OWL),
    COLOVARIA_VERMICULO(COLOVARIA_VERMICULO.class, MagicLevel.OWL),
    COMETES(COMETES.class, MagicLevel.BEGINNER),
    COMETES_DUO(COMETES_DUO.class, MagicLevel.OWL),
    CONFUNDO(CONFUNDO.class, MagicLevel.OWL),
    CONFUNDUS_DUO(CONFUNDUS_DUO.class, MagicLevel.NEWT),
    CRESCERE_PROTEGAT(CRESCERE_PROTEGAT.class, MagicLevel.NEWT),
    DEFODIO(DEFODIO.class, MagicLevel.NEWT),
    DELETRIUS(DELETRIUS.class, MagicLevel.OWL),
    DEPRIMO(DEPRIMO.class, MagicLevel.OWL),
    DEPULSO(DEPULSO.class, MagicLevel.OWL),
    DIAMAS_REPARO(DIAMAS_REPARO.class, MagicLevel.OWL),
    DIFFINDO(DIFFINDO.class, MagicLevel.OWL),
    DISSENDIUM(DISSENDIUM.class, MagicLevel.OWL),
    DRACONIFORS(DRACONIFORS.class, MagicLevel.NEWT),
    DUCKLIFORS(DUCKLIFORS.class, MagicLevel.OWL),
    DURO(DURO.class, MagicLevel.OWL),
    EBUBLIO(EBUBLIO.class, MagicLevel.NEWT),
    ENGORGIO(ENGORGIO.class, MagicLevel.BEGINNER),
    ENTOMORPHIS(ENTOMORPHIS.class, MagicLevel.NEWT),
    EPISKEY(EPISKEY.class, MagicLevel.NEWT),
    ET_INTERFICIAM_ANIMAM_LIGAVERIS(ET_INTERFICIAM_ANIMAM_LIGAVERIS.class, MagicLevel.EXPERT),
    EQUUS(EQUUS.class, MagicLevel.NEWT),
    EQUUSIFORS(EQUUSIFORS.class, MagicLevel.OWL),
    EVANESCO(EVANESCO.class, MagicLevel.OWL),
    EXPELLIARMUS(EXPELLIARMUS.class, MagicLevel.BEGINNER),
    FATUUS_AURUM(FATUUS_AURUM.class, MagicLevel.OWL),
    FELIS(FELIS.class, MagicLevel.NEWT),
    FIANTO_DURI(FIANTO_DURI.class, MagicLevel.OWL),
    FIENDFYRE(FIENDFYRE.class, MagicLevel.EXPERT),
    FINESTRA(FINESTRA.class, MagicLevel.OWL),
    FINITE_INCANTATEM(FINITE_INCANTATEM.class, MagicLevel.BEGINNER),
    FLAGRANTE(FLAGRANTE.class, MagicLevel.NEWT),
    FLIPENDO(FLIPENDO.class, MagicLevel.BEGINNER),
    FRANGE_LIGNEA(FRANGE_LIGNEA.class, MagicLevel.OWL),
    FUMOS(FUMOS.class, MagicLevel.BEGINNER),
    FUMOS_DUO(FUMOS_DUO.class, MagicLevel.OWL),
    GEMINIO(GEMINIO.class, MagicLevel.NEWT),
    GLACIUS(GLACIUS.class, MagicLevel.OWL),
    GLACIUS_DUO(GLACIUS_DUO.class, MagicLevel.OWL),
    GLACIUS_TRIA(GLACIUS_TRIA.class, MagicLevel.NEWT),
    HARMONIA_NECTERE_PASSUS(HARMONIA_NECTERE_PASSUS.class, MagicLevel.EXPERT),
    HERBIFORS(HERBIFORS.class, MagicLevel.BEGINNER),
    HERBIVICUS(HERBIVICUS.class, MagicLevel.NEWT),
    HORREAT_PROTEGAT(HORREAT_PROTEGAT.class, MagicLevel.NEWT),
    IMMOBULUS(IMMOBULUS.class, MagicLevel.OWL),
    IMPEDIMENTA(IMPEDIMENTA.class, MagicLevel.OWL),
    INCENDIO(INCENDIO.class, MagicLevel.BEGINNER),
    INCENDIO_DUO(INCENDIO_DUO.class, MagicLevel.OWL),
    INCENDIO_TRIA(INCENDIO_TRIA.class, MagicLevel.NEWT),
    INFORMOUS(INFORMOUS.class, MagicLevel.NEWT),
    INTUEOR(INTUEOR.class, MagicLevel.BEGINNER),
    LACARNUM_INFLAMARI(LACARNUM_INFLAMARI.class, MagicLevel.OWL),
    LAGOMORPHA(LAGOMORPHA.class, MagicLevel.OWL),
    LAMA(LAMA.class, MagicLevel.NEWT),
    LAPIFORS(net.pottercraft.ollivanders2.spell.LAPIFORS.class, MagicLevel.OWL),
    LEGILIMENS(LEGILIMENS.class, MagicLevel.NEWT),
    LEPUS_SACCULUM(LEPUS_SACCULUM.class, MagicLevel.BEGINNER),
    LEVICORPUS(LEVICORPUS.class, MagicLevel.NEWT),
    LIBERACORPUS(LIBERACORPUS.class, MagicLevel.NEWT),
    LIGATIS_COR(LIGATIS_COR.class, MagicLevel.NEWT),
    LOQUELA_INEPTIAS(LOQUELA_INEPTIAS.class, MagicLevel.OWL),
    LUMOS(LUMOS.class, MagicLevel.BEGINNER),
    LUMOS_DUO(LUMOS_DUO.class, MagicLevel.OWL),
    LUMOS_MAXIMA(LUMOS_MAXIMA.class, MagicLevel.OWL),
    LUMOS_SOLEM(LUMOS_SOLEM.class, MagicLevel.NEWT),
    MANTEIA_KENTAVROS(MANTEIA_KENTAVROS.class, MagicLevel.NEWT),
    MEGA_PYRO_PRASINA(MEGA_PYRO_PRASINA.class, MagicLevel.OWL),
    MELOFORS(MELOFORS.class, MagicLevel.OWL),
    METELOJINX(METELOJINX.class, MagicLevel.OWL),
    METELOJINX_RECANTO(METELOJINX_RECANTO.class, MagicLevel.OWL),
    MOLLIARE(MOLLIARE.class, MagicLevel.NEWT),
    MORSMORDRE(MORSMORDRE.class, MagicLevel.NEWT),
    MORTUOS_SUSCITATE(MORTUOS_SUSCITATE.class, MagicLevel.NEWT),
    MOV_FOTIA(MOV_FOTIA.class, MagicLevel.NEWT),
    MUCUS_AD_NAUSEAM(MUCUS_AD_NAUSEAM.class, MagicLevel.BEGINNER),
    MUFFLIATO(MUFFLIATO.class, MagicLevel.OWL),
    MULTICORFORS(MULTICORFORS.class, MagicLevel.BEGINNER),
    NOX(NOX.class, MagicLevel.BEGINNER),
    NULLUM_APPAREBIT(NULLUM_APPAREBIT.class, MagicLevel.NEWT),
    NULLUM_EVANESCUNT(NULLUM_EVANESCUNT.class, MagicLevel.NEWT),
    OBLIVIATE(OBLIVIATE.class, MagicLevel.NEWT),
    OBSCURO(OBSCURO.class, MagicLevel.BEGINNER),
    OPPUGNO(OPPUGNO.class, MagicLevel.NEWT),
    OVOGNOSIS(OVOGNOSIS.class, MagicLevel.NEWT),
    PACK(PACK.class, MagicLevel.BEGINNER),
    PARTIS_TEMPORUS(PARTIS_TEMPORUS.class, MagicLevel.NEWT),
    PERICULUM(PERICULUM.class, MagicLevel.BEGINNER),
    PERICULUM_DUO(PERICULUM_DUO.class, MagicLevel.OWL),
    PERMURATE(PERMURATE.class, MagicLevel.BEGINNER),
    PETRIFICUS_TOTALUS(PETRIFICUS_TOTALUS.class, MagicLevel.BEGINNER),
    PIERTOTUM_LOCOMOTOR(PIERTOTUM_LOCOMOTOR.class, MagicLevel.OWL),
    POINT_ME(POINT_ME.class, MagicLevel.BEGINNER),
    PORFYRO_ASTERI(PORFYRO_ASTERI.class, MagicLevel.BEGINNER),
    PORFYRO_ASTERI_DUO(PORFYRO_ASTERI_DUO.class, MagicLevel.OWL),
    PORFYRO_ASTERI_TRIA(PORFYRO_ASTERI_TRIA.class, MagicLevel.OWL),
    PORTUS(PORTUS.class, MagicLevel.NEWT),
    PRIOR_INCANTATO(PRIOR_INCANTATO.class, MagicLevel.NEWT),
    PROPHETEIA(PROPHETEIA.class, MagicLevel.NEWT),
    PROTEGO(PROTEGO.class, MagicLevel.OWL),
    PROTEGO_HORRIBILIS(PROTEGO_HORRIBILIS.class, MagicLevel.EXPERT),
    PROTEGO_MAXIMA(PROTEGO_MAXIMA.class, MagicLevel.NEWT),
    PROTEGO_TOTALUM(PROTEGO_TOTALUM.class, MagicLevel.NEWT),
    PULLUS(PULLUS.class, MagicLevel.NEWT),
    PYRO_PRASINA(PYRO_PRASINA.class, MagicLevel.BEGINNER),
    PYROSVESTIRAS(PYROSVESTIRAS.class, MagicLevel.NEWT),
    RANACULUS_AMPHORAM(RANACULUS_AMPHORAM.class, MagicLevel.BEGINNER),
    REDUCIO(REDUCIO.class, MagicLevel.BEGINNER),
    REDUCTO(REDUCTO.class, MagicLevel.OWL),
    REPARIFARGE(REPARIFARGE.class, MagicLevel.BEGINNER),
    REPARIFORS(REPARIFORS.class, MagicLevel.OWL),
    REPARO(REPARO.class, MagicLevel.BEGINNER),
    REPELLO_MUGGLETON(REPELLO_MUGGLETON.class, MagicLevel.NEWT),
    SCARABAEUS_FIBULUM(SCARABAEUS_FIBULUM.class, MagicLevel.BEGINNER),
    SCUTO_CONTERAM(SCUTO_CONTERAM.class, MagicLevel.NEWT),
    SILENCIO(SILENCIO.class, MagicLevel.OWL),
    SNUFFLIFORS(SNUFFLIFORS.class, MagicLevel.BEGINNER),
    SPONGIFY(SPONGIFY.class, MagicLevel.BEGINNER),
    STUPEFY(STUPEFY.class, MagicLevel.OWL),
    SUS(SUS.class, MagicLevel.NEWT),
    TERGEO(TERGEO.class, MagicLevel.OWL),
    URSUS(URSUS.class, MagicLevel.NEWT),
    VENTO_FOLIO(VENTO_FOLIO.class, MagicLevel.EXPERT),
    VERA_VERTO(VERA_VERTO.class, MagicLevel.BEGINNER),
    VERDIMILLIOUS(VERDIMILLIOUS.class, MagicLevel.BEGINNER),
    VERDIMILLIOUS_DUO(VERDIMILLIOUS_DUO.class, MagicLevel.OWL),
    VERDIMILLIOUS_TRIA(VERDIMILLIOUS_TRIA.class, MagicLevel.NEWT),
    VERMILLIOUS(VERMILLIOUS.class, MagicLevel.BEGINNER),
    VERMILLIOUS_DUO(VERMILLIOUS_DUO.class, MagicLevel.OWL),
    VERMILLIOUS_TRIA(VERMILLIOUS_TRIA.class, MagicLevel.NEWT),
    VOLATUS(VOLATUS.class, MagicLevel.NEWT),
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
     * @return the classname for this spell type
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
