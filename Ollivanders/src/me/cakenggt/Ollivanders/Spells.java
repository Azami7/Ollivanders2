package me.cakenggt.Ollivanders;

/**
 * Represents allowable spells.
 *
 */
public enum Spells {
	ACCIO,
	AGUAMENTI,
	ALARTE_ASCENDARE,
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
	BRACKIUM_EMENDO,
	CARPE_RETRACTUM,
	COLLOPORTUS,
	COLOVARIA,
	CONFUNDO,
	CRESCERE_PROTEGAT,
	DEFODIO,
	DELETRIUS,
	DEPRIMO,
	DEPULSO,
	DIFFINDO,
	DIMMINUENDO,
	DUCKLIFORS,
	DURO,
	DRACONIFORS,
	EBUBLIO,
	ENGORGIO,
	ENTOMORPHIS,
	EPISKEY,
	EQUUSIFORS,
	ET_INTERFICIAM_ANIMAM_LIGAVERIS,
	EVANESCO,
	EVERTE_STATUM,
	EXPELLIARMUS,
	FIANTO_DURI,
	FIENDFYRE,
	FINITE_INCANTATEM,
	FLAGRANTE,
	FORSKNING,
	FRANGE_LIGNEA,
	FUMOS,
	FUMOS_DUO,
	GEMINIO,
	GLACIUS,
	GLACIUS_DUO,
	GLACIUS_TRIA,
	HARMONIA_NECTERE_PASSUS,
	HERBIVICUS,
	HORREAT_PROTEGAT,
	IMMOBULUS,
	IMPEDIMENTA,
	INCENDIO,
	INCENDIO_DUO,
	INCENDIO_TRIA,
	INFORMOUS,
	LACARNUM_INFLAMARI,
	LEGILIMENS,
	LEVICORPUS,
	LIBERACORPUS,
	LIGATIS_COR,
	LUMOS,
	LUMOS_DUO,
	LUMOS_MAXIMA,
	LUMOS_SOLEM,
	MELOFORS,
	METEOLOJINX,
	METEOLOJINX_RECANTO,
	MUCUS_AD_NAUSEAM,
	MUFFLIATO,
	MULTICORFORS,
	NULLUM_APPAREBIT,
	NULLUM_EVANESCUNT,
	OBLIVIATE,
	OBSCURO,
	OPPUGNO,
	PACK,
	PARTIS_TEMPORUS,
	PERICULUM,
	PIERTOTUM_LOCOMOTOR,
	PORTUS,
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
	WINGARDIUM_LEVIOSA;
	
	/**
	 * Find the Spell that corresponds to a string.
	 * @param s - string, doesn't have to correspond to a spell
	 * @return spell such that the spell resembles the string in spelling. null if no such spell exists
	 */
	public static Spells decode(String s){
		//getLogger().info(s);
		String[] words = s.split(" ");
		//getLogger().info(words.length);
		for (int i = 0; i < words.length; i++){
			words[i] = words[i].toUpperCase();
		}
		//getLogger().info(words.length);
		String complete = "";
		for (String word : words){
			complete = complete.concat(word);
			complete = complete.concat("_");
		}
		//getLogger().info(complete);
		complete = complete.substring(0, complete.length()-1);
		Spells spell;
		try {
			spell = Spells.valueOf(complete);
		} catch (IllegalArgumentException e) {
			spell = null;
		} catch (NullPointerException e) {
			spell = null;
		}
		return spell;
	}
	
	/**
	 * Find the lowercase string that corresponds to a spell name
	 * @param s - spell
	 * @return string such that it is the lowercase version of the spell minus underscores
	 */
	public static String recode(Spells s){
		String nameLow = s.toString().toLowerCase();
		String[] words = nameLow.split("_");
		String comp = "";
		for (String st : words){
			comp = comp.concat(st);
			comp = comp.concat(" ");
		}
		comp = comp.substring(0, comp.length()-1);
		return comp;
	}
}
