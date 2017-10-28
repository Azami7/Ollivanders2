package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spell.Spells;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Super class for all Ollivanders2 books.
 *
 * Book limits:
 * Title - 32 characters
 * Pages - 50
 * Lines per page - 14
 * Characters per page - 256, newlines count as 2 characters
 * Characters per line - 16-18 depending on the exact characters
 *
 * @since 2.2.4
 * @author Azami7
 */
public abstract class Book
{
   protected String author;
   protected String title;
   /**
    * Cannot be more than 32 characters or it will appear blank.
    */
   protected String shortTitle;
   protected BookMeta bookMeta;
   protected O2MagicBranch branch;
   protected String toc;
   /**
    * No more than 256 characters
    */
   protected String openingPage;
   /**
    * No more than 256 characters
    */
   protected String closingPage;
   protected ArrayList<String> mainContent;

   protected Ollivanders2 p;

   /**
    * No more than 11 spells in a book or they won't fit on the table of contents.
    */
   protected ArrayList<Spells> spellList;

   /**
    * Constructor
    */
   public Book ()
   {
      author = new String ("Azami7");
      title = new String ("Untitled");
      shortTitle = new String ("Untitled");
      toc = "";
      openingPage = "";
      closingPage = "";
      mainContent = new ArrayList<>();

      spellList = new ArrayList<>();
      bookMeta = null;
      p = null;
   }

   /**
    * Write all the metadata for this book.
    *
    * @param bookItem the book item to write metadata on.
    */
   private void writeSpellBookMeta (ItemStack bookItem)
   {
      bookMeta = (BookMeta)bookItem.getItemMeta();
      bookMeta.setAuthor(author);
      bookMeta.setTitle(shortTitle);

      //write title page
      String titlePage = title + "\n\nby " + author;
      bookMeta.addPage(titlePage);

      toc = new String("Contents:\n\n");
      ArrayList<String> mainContent = new ArrayList<>();

      for (Spells s : spellList)
      {
         String spellName = Spells.recode(s);
         String spell = Spells.firstLetterCapitalize(spellName);
         toc = toc + spell + "\n";

         String text;
         String spellText = SpellText.getText(s);
         String spellFlavorText = SpellText.getFlavorText(s);

         if (spellFlavorText == null)
         {
            text = spell + "\n\n" + spellText;
         }
         else
            text = spell + "\n\n" + spellFlavorText + "\n\n" + spellText;

         // create the pages for this spell
         ArrayList<String> pages = createPages(text);

         for (String p : pages)
         {
            mainContent.add(p);
         }
      }

      // add TOC page
      bookMeta.addPage(toc);

      // add opening page
      if (openingPage.length() > 0)
         bookMeta.addPage(openingPage);

      // add spell pages
      for (String page : mainContent)
      {
         bookMeta.addPage(page);
      }

      // add closing page
      if (closingPage.length() > 0)
         bookMeta.addPage(closingPage);

      bookMeta.setGeneration(BookMeta.Generation.ORIGINAL);
      bookItem.setItemMeta(bookMeta);
   }

   /**
    * Create the pages for the spells.
    *
    * @param text
    * @return a list of the spell pages
    */
   private ArrayList<String> createPages (String text)
   {
      //get the words in a spell text
      ArrayList<String> words = getWords(text);

      // turn the word list in to pages
      ArrayList<String> pages = makePages(words);

      return pages;
   }

   /**
    * Get a list of all the words in a spell text.
    *
    * @param text
    * @return a list of the words in a text.
    */
   private ArrayList<String> getWords (String text)
   {
      ArrayList<String> words = new ArrayList<String>();
      String[] splits = text.split(" ");

      for (String s : splits)
      {
         words.add(s);
      }

      return words;
   }

   /**
    * Turn a spell text word list in to a set of pages that fit in an MC book.
    *
    * Book pages cannot be more than 14 lines with ~18 characters per line, 256 characters max
    * assume 2 lines for spell name, 1 blank line between name and flavor text, 1 blank link between flavor text
    * and description text, means the first page has 9 lines of ~15 characters + continue, subsequent pages are 13
    * lines of ~15 characters + continue.
    *
    * This means the first page for a spell can have ~175 characters and subsequent pages can be ~195.
    *
    * Assumes there is no word in the list that is >= 200 characters.
    *
    * @param words
    * @return a list of book pages
    */
   private ArrayList<String> makePages (ArrayList<String> words)
   {
      ArrayList<String> pages = new ArrayList<>();

      // first page
      int remaining = 175;
      String page = new String();

      // first page
      while (remaining > 0)
      {
         if (words.isEmpty())
            break;

         String word = words.get(0);

         if (word.length() >= remaining)
            break;

         page = page + word + " ";

         // decrement remaining, remove word from list
         remaining = remaining - word.length() - 1;
         words.remove(0);
      }
      if (!words.isEmpty())
         page = page + "(cont.)";
      pages.add(page);

      // remaining pages
      while (!words.isEmpty())
      {
         remaining = 200;
         page = new String();

         while (remaining > 0)
         {
            if (words.isEmpty())
               break;

            String word = words.get(0);

            if (word.length() >= remaining)
               break;

            page = page + word + " ";

            // decrement remaining, remove word from list
            remaining = remaining - word.length() - 1;
            words.remove(0);
         }
         if (!words.isEmpty())
            page = page + "(cont.)";

         pages.add(page);
      }

      return pages;
   }

   /**
    * Create the lore for this book. This will contain the name of each spell and is used by bookLearning
    * to know what spells are in this book.
    *
    * @return a String list of lore
    */
   private List<String> getSpellBookLore ()
   {
      List<String> lore = new ArrayList<String>();

      for (Spells s : spellList)
      {
         String spellName = Spells.recode(s);
         String spell = Spells.firstLetterCapitalize(spellName);

         lore.add(spell);
      }

      return lore;
   }

   /**
    * Create a BookItem of this book.
    *
    * @param plugin the O2 plugin
    * @return the book as a BookItem
    */
   public ItemStack createBook (Ollivanders2 plugin)
   {
      // make sure this is set
      p = plugin;

      ItemStack bookItem = new ItemStack(Material.WRITTEN_BOOK, 1);
      List<String> lore = null;

      if (bookMeta == null)
      {
         // only support spell books right now
         if (branch == O2MagicBranch.CHARMS || branch == O2MagicBranch.DARK_ARTS || branch == O2MagicBranch.TRANSFIGURATION
               || branch == O2MagicBranch.ARITHMANCY || branch == O2MagicBranch.HEALING)
         {
            writeSpellBookMeta(bookItem);
            lore = getSpellBookLore();
         }
      }

      bookMeta.setLore(lore);
      bookItem.setItemMeta(bookMeta);

      return bookItem;
   }

   /**
    * Get the title for this book.
    *
    * @return title
    */
   public String getTitle ()
   {
      return title;
   }

   /**
    * The author for this book.
    *
    * @return author
    */
   public String getAuthor ()
   {
      return author;
   }

   /**
    * The branch for this book.
    *
    * @return branch
    */
   public O2MagicBranch getBranch ()
   {
      return branch;
   }
}
