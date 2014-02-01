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
	ARRESTO_MOMENTUM,
	ASCENDIO,
	AVADA_KEDAVRA,
	AVIS,
	BOMBARDA,
	BOMBARDA_MAXIMA,
	CARPE_RETRACTUM,
	COLLOPORTUS,
	CONFUNDO,
	CRESCERE_PROTEGAT,
	DEFODIO,
	DELETRIUS,
	DEPRIMO,
	DEPULSO,
	DIMMINUENDO,
	DUCKLIFORS,
	DURO,
	DRACONIFORS,
	EBUBLIO,
	ENGORGIO,
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
	FRANGE_LIGNEA,
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
	LIGATIS_COR,
	LUMOS,
	LUMOS_DUO,
	LUMOS_MAXIMA,
	MELOFORS,
	METEOLOJINX,
	METEOLOJINX_RECANTO,
	MUCUS_AD_NAUSEAM,
	MUFFLIATO,
	MULTICORFORS,
	NULLUM_APPAREBIT,
	NULLUM_EVANESCUNT,
	OBLIVIATE,
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
		//System.out.println(s);
		String[] words = s.split(" ");
		//System.out.println(words.length);
		for (int i = 0; i < words.length; i++){
			words[i] = words[i].toUpperCase();
		}
		//System.out.println(words.length);
		String complete = "";
		for (String word : words){
			complete = complete.concat(word);
			complete = complete.concat("_");
		}
		//System.out.println(complete);
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
