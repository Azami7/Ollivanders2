package net.pottercraft.Ollivanders2;

/**
 * Represents allowable spells.
 *
 */
public enum Spells {
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
	DISSENDIUM,
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
	MORTUOS_SUSCITATE,
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
	VOLATUS,
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
		StringBuilder completeSB = new StringBuilder();
		for (String word : words){
			completeSB.append(word);
			completeSB.append("_");
		}
		//getLogger().info(complete);
		String complete = completeSB.substring(0, completeSB.length()-1);
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
	
	/**Converts a string to have it's first letter of  each word be in upper case, and all other letters lower case.
	 * @param str - String to convert.
	 * @return String with correct formatting.
	 */
	public static String firstLetterCapitalize(String str){
		StringBuilder sb = new StringBuilder();
		String[] wordList = str.split(" ");
		for (String s : wordList){
			sb.append(s.substring(0, 1).toUpperCase());
			if (s.length() > 1){
				sb.append(s.substring(1, s.length()).toLowerCase());
			}
			sb.append(" ");
		}
		return sb.substring(0, sb.length()-1);
	}
}
