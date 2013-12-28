package me.cakenggt.Ollivanders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Provides the methods to parse a book for spells
 * @author cakenggt
 *
 */
public class SpellBookParser{
	
	/**
	 * Encodes in the lore of the book the spells and levels the author is at
	 * @param p - The plugin
	 * @param player - The author
	 * @param meta - The BookMeta of the book
	 * @return newMeta - The new BookMeta of the book, which is passed to the event
	 */
	public static BookMeta encode(Ollivanders p, Player player, BookMeta meta){
		String pageString = getPageString(meta);
		List<String> spellStrings = spellList();
		List<String> lore = new ArrayList<String>();
		for (String spell : spellStrings){
			if (pageString.contains(spell)){
				String newLore = spell + ":" + p.getSpellNum(player, Spells.decode(spell));
				lore.add(newLore);
			}
		}
		BookMeta newMeta = meta.clone();
		newMeta.setLore(lore);
		return newMeta;
	}
	
	/**
	 * Gets a lowercase string composed of all pages of the book
	 * @param meta BookMeta from PlayerEditBookEvent.getNewBookMeta()
	 * @return String in lowercase of all page text
	 */
	private static String getPageString(BookMeta meta){
		List<String> pages = meta.getPages();
		String pageString = "";
		for (String page : pages){
			pageString = pageString.concat(page + " ");
		}
		pageString = pageString.toLowerCase();
		pageString = pageString.replace('\n', ' ');
		return pageString;
	}
	
	/**
	 * Gets a list of all Spells converted into lower case and spaces for underscores
	 * @return List of strings of human readable spells
	 */
	private static List<String> spellList(){
		Spells[] spellsList = Spells.values();
		List<String> spellStrings = new ArrayList<String>();
		for (Spells spell : spellsList){
			spellStrings.add(Spells.recode(spell));
		}
		return spellStrings;
	}
	
	/**
	 * Takes a book and decodes the spells in lore, if any, into player uses
	 * @param p - The Plugin
	 * @param player - The player reading the book
	 * @param imeta - The book's metadata
	 */
	public static void decode(Ollivanders p, Player player, ItemMeta imeta){
		List<String> lore = imeta.getLore();
		String[] line;
		int bookNum;
		int pSpellNum;
		Spells spell = null;
		if (lore != null){
			for (String s : lore){
				line = s.split(":");
				if (line.length == 2){
					spell = Spells.decode(line[0]);
					bookNum = Integer.parseInt(line[1]);
					if (spell != null){
						pSpellNum = p.getSpellNum(player, spell);
						if (pSpellNum < bookNum){
							p.setSpellNum(player, spell, (int)(pSpellNum+((bookNum-pSpellNum)/2)));
						}
					}
				}
			}
		}
	}
	
	/**
	 * This creates the books for the /Okit command
	 * @return - A list of books
	 */
	public static List<ItemStack> makeBooks(){
		Map<String, String> bookMap = books();
		List<ItemStack> books = new ArrayList<ItemStack>();
		for (String title : bookMap.keySet()){
			ItemStack item = new ItemStack(Material.WRITTEN_BOOK, 1);
			BookMeta bm = (BookMeta) item.getItemMeta();
			bm.setAuthor("cakenggt");
			bm.setTitle(title);
			bm.setPages(splitEqually(bookMap.get(title), 250));
			bm = kitEncode(bm);
			item.setItemMeta(bm);
			System.out.println(title);
			System.out.println("Adding " + bm.getTitle());
			books.add(item);
		}
		//code for the debug book
		ItemStack item = new ItemStack(Material.WRITTEN_BOOK, 1);
		BookMeta bm = (BookMeta) item.getItemMeta();
		String title = "DEBUGGER";
		bm.setAuthor("cakenggt");
		bm.setTitle(title);
		String inside = "";
		for (String str : spellList()){
			inside.concat(str + " ");
		}
		bm.setPages(splitEqually(inside, 250));
		bm = kitEncode(bm);
		item.setItemMeta(bm);
		System.out.println(title);
		System.out.println("Adding " + bm.getTitle());
		books.add(item);
		return books;
	}
	
	/**
	 * This splits a string into equal segments.
	 * @param text
	 * @param size
	 * @return List of strings of size size or less
	 */
	private static List<String> splitEqually(String text, int size) {
	    List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);
	    for (int start = 0; start < text.length(); start += size) {
	        ret.add(text.substring(start, Math.min(text.length(), start + size)));
	    }
	    return ret;
	}
	
	/**
	 * Encodes in the lore of the book the spells and levels the author is at
	 * @param p - The plugin
	 * @param player - The author
	 * @param meta - The BookMeta of the book
	 * @return newMeta - The new BookMeta of the book, which is passed to the event
	 */
	private static BookMeta kitEncode(BookMeta meta){
		String pageString = getPageString(meta);
		List<String> spellStrings = spellList();
		List<String> lore = new ArrayList<String>();
		for (String spell : spellStrings){
			if (pageString.contains(spell)){
				String newLore = spell + ":" + 200;
				lore.add(newLore);
			}
		}
		BookMeta newMeta = meta.clone();
		newMeta.setLore(lore);
		return newMeta;
	}
	
	/**
	 * Returns a map of all books mapped to their titles
	 * @return Map whose keys are the titles, entries are the book text
	 */
	private static Map<String, String> books(){
		Map<String, String> bookMap = new HashMap<String, String>();
		// \n is a newline
		bookMap.put("The Standard Book of Spells, Grade 1",
				"The beginning wizard will find great fun with the " +
				"spell bombarda, which blasts the target area.\n" +
				"But perhaps too much fun, too much. Apparate.");
		bookMap.put("The Secrets of Wandlore",
				"The secrets of wandlore are not to be easily had, however " +
				"they will be related in this book with the greatest of ease.\n" +
				"Frange lignea will split a log in twain, producing coreless " +
				"wands. Those wands can be paired with cores using the spell " +
				"ligatis cor, but only one wand and core at a time.");
		bookMap.put("Magick Moste Evile", "Fiendfyre is a dark sort of magic which " +
				"summons magefire in an unpredictable way. Only those skilled in " +
				"the use of this spell may even hope to use it effectively in " +
				"dueling.");
		return bookMap;
	}
}