package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Potion.O2PotionType;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;

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
public abstract class O2Book
{
   /**
    * The book author
    */
   protected String author;

   /**
    * The full book title
    */
   protected String title;

   /**
    * The book title for item lore, cannot be more than 32 characters or it will appear blank.
    */
   protected String shortTitle;

   /**
    * The branch of magic this book covers
    */
   protected O2MagicBranch branch;

   /**
    * The book item object.
    */
   private ItemStack bookItem;

   /**
    * No more than 256 characters
    */
   String openingPage;

   /**
    * No more than 256 characters
    */
   String closingPage;

   protected Ollivanders2 p;

   /**
    * No more than 11 spells + potions in a book or they won't fit on the table of contents.
    */
   protected ArrayList<O2SpellType> spells;
   protected ArrayList<O2PotionType> potions;

   /**
    * Constructor
    */
   public O2Book (Ollivanders2 plugin)
   {
      author = "Unknown";
      title = "Untitled";
      shortTitle = "Untitled";

      openingPage = "";
      closingPage = "";

      bookItem = new ItemStack(Material.WRITTEN_BOOK, 1);

      spells = new ArrayList<>();
      potions = new ArrayList<>();
      p = plugin;

      bookItem = new ItemStack(Material.WRITTEN_BOOK, 1);
      writeSpellBookMeta();
   }

   /**
    * Write all the metadata for this book.
    */
   private void writeSpellBookMeta ()
   {
      if (bookItem == null)
         return;

      BookMeta bookMeta = (BookMeta)bookItem.getItemMeta();
      bookMeta.setAuthor(author);
      bookMeta.setTitle(shortTitle);

      //write title page
      String titlePage = title + "\n\nby " + author;
      bookMeta.addPage(titlePage);

      StringBuilder toc = new StringBuilder();
      toc.append("Contents:\n\n");
      ArrayList<String> mainContent = new ArrayList<>();

      // add the names of all spells in the book
      ArrayList<String> bookContents = new ArrayList<>();
      for (O2SpellType spell : spells)
      {
         bookContents.add(spell.toString());
      }

      // add the name of all the potions in the book
      for (O2PotionType potionType : potions)
      {
         bookContents.add(potionType.toString());
      }

      for (String content : bookContents)
      {
         String name = p.books.spellText.getName(content);
         if (name == null)
         {
            p.getLogger().warning(this.title + " contains unknown spell or potion " + content);
            continue;
         }

         toc.append(name).append("\n");

         String text;
         String mainText = p.books.spellText.getText(content);
         String flavorText = p.books.spellText.getFlavorText(content);

         if (flavorText == null)
         {
            text = name + "\n\n" + mainText;
         }
         else
            text = name + "\n\n" + flavorText + "\n\n" + mainText;

         // create the pages for this spell
         ArrayList<String> pages = createPages(text);

         mainContent.addAll(pages);
      }

      // add TOC page
      bookMeta.addPage(toc.toString());

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

      // add lore
      List<String> lore = getBookLore();

      bookMeta.setLore(lore);
      bookMeta.setGeneration(BookMeta.Generation.ORIGINAL);

      bookItem.setItemMeta(bookMeta);
   }

   /**
    * Create the pages for the spells.
    *
    * @param text the text for this spell
    * @return a list of the spell pages
    */
   private ArrayList<String> createPages (String text)
   {
      //get the words in a spell text
      ArrayList<String> words = getWords(text);

      // turn the word list in to pages
      return makePages(words);
   }

   /**
    * Get a list of all the words in a spell text.
    *
    * @param text the book text for this spell
    * @return a list of the words in a text.
    */
   private ArrayList<String> getWords (String text)
   {
      ArrayList<String> words = new ArrayList<>();
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
    * @param words an array of all the words in the book
    * @return a list of book pages
    */
   private ArrayList<String> makePages (ArrayList<String> words)
   {
      ArrayList<String> pages = new ArrayList<>();

      // first page
      int remaining = 175;
      StringBuilder page = new StringBuilder();

      // first page
      while (remaining > 0)
      {
         if (words.isEmpty())
            break;

         String word = words.get(0);

         if (word.length() >= remaining)
            break;

         page.append(word).append(" ");

         // decrement remaining, remove word from list
         remaining = remaining - word.length() - 1;
         words.remove(0);
      }
      if (!words.isEmpty())
         page.append("(cont.)");

      pages.add(page.toString());

      // remaining pages
      while (!words.isEmpty())
      {
         remaining = 200;
         page = new StringBuilder();

         while (remaining > 0)
         {
            if (words.isEmpty())
               break;

            String word = words.get(0);

            if (word.length() >= remaining)
               break;

            page.append(word).append(" ");

            // decrement remaining, remove word from list
            remaining = remaining - word.length() - 1;
            words.remove(0);
         }
         if (!words.isEmpty())
            page.append("(cont.)");

         pages.add(page.toString());
      }

      return pages;
   }

   /**
    * Create the lore for this book. This will contain the name of each spell and is used by bookLearning
    * to know what spells are in this book.
    *
    * @return a String list of lore
    */
   private List<String> getBookLore ()
   {
      List<String> lore = new ArrayList<>();

      for (O2SpellType spellType : spells)
      {
         String s = spellType.getSpellName();
         lore.add(s);
      }

      for (O2PotionType potionType : potions)
      {
         String s = potionType.getPotionName();
         lore.add(s);
      }

      return lore;
   }

   /**
    * Get the book item for this book
    *
    * @return the book item
    */
   public ItemStack getBookItem()
   {
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
    * Get the short title for this book
    *
    * @return short title for this book
    */
   public String getShortTitle ()
   {
      return shortTitle;
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
