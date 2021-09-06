package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.spell.O2SpellType;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

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
   protected O2BookType bookType;

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

   /**
    * Namespace keys for NBT tags
    */
   public static NamespacedKey o2BookTypeKey;
   public static NamespacedKey o2BookSpellsKey;
   public static NamespacedKey o2BookPotionsKey;

   /**
    * Callback to the plugin
    */
   protected Ollivanders2 p;

   /**
    * No more than 11 spells + potions in a book or they won't fit on the table of contents.
    */
   protected ArrayList<O2SpellType> spells;
   protected ArrayList<O2PotionType> potions;

   /**
    * Constructor
    */
   public O2Book(@NotNull Ollivanders2 plugin)
   {
      bookType = O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_1;

      openingPage = "";
      closingPage = "";

      // create the book on first request to ensure texts, etc are loaded before trying to create it
      bookItem = null;

      spells = new ArrayList<>();
      potions = new ArrayList<>();
      p = plugin;

      o2BookTypeKey = new NamespacedKey(p, "O2BookType");
      o2BookSpellsKey = new NamespacedKey(p, "O2SpellTypes");;
      o2BookPotionsKey = new NamespacedKey(p, "O2PotionTypes");;
   }

   /**
    * Write all the metadata for this book.
    */
   private void writeSpellBookMeta ()
   {
      if (bookItem == null)
         return;

      BookMeta bookMeta = (BookMeta) bookItem.getItemMeta();
      if (bookMeta == null)
         return;

      bookMeta.setAuthor(bookType.author);
      bookMeta.setTitle(bookType.shortTitle);

      //write title page
      String titlePage = bookType.title + "\n\nby " + bookType.author;
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
         String name = Ollivanders2API.getBooks(p).spellText.getName(content);
         if (name == null)
         {
            p.getLogger().warning(bookType.title + " contains unknown spell or potion " + content);
            continue;
         }

         toc.append(name).append("\n");

         String text;
         String mainText = Ollivanders2API.getBooks(p).spellText.getText(content);
         String flavorText = Ollivanders2API.getBooks(p).spellText.getFlavorText(content);

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

      // add NBT tags
      StringBuilder spellsTag = new StringBuilder();
      for (O2SpellType spellType : spells)
      {
         spellsTag.append(spellType.toString()).append(" ");
      }

      StringBuilder potionsTag = new StringBuilder();
      for (O2PotionType potionType : potions)
      {
         potionsTag.append(potionType.toString()).append(" ");
      }

      PersistentDataContainer container = bookMeta.getPersistentDataContainer();
      container.set(o2BookTypeKey, PersistentDataType.STRING, bookType.toString());
      container.set(o2BookSpellsKey, PersistentDataType.STRING, spellsTag.toString().trim());
      container.set(o2BookPotionsKey, PersistentDataType.STRING, potionsTag.toString().trim());

      bookMeta.setGeneration(BookMeta.Generation.ORIGINAL);

      bookItem.setItemMeta(bookMeta);
   }

   /**
    * Create the pages for the spells.
    *
    * @param text the text for this spell
    * @return a list of the spell pages
    */
   @NotNull
   private ArrayList<String> createPages(@NotNull String text)
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
   @NotNull
   private ArrayList<String> getWords(@NotNull String text)
   {
      ArrayList<String> words = new ArrayList<>();
      String[] splits = text.split(" ");

      Collections.addAll(words, splits);

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
   @NotNull
   private ArrayList<String> makePages(@NotNull ArrayList<String> words)
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
    * Get the book item for this book
    *
    * @return the book item
    */
   @NotNull
   public ItemStack getBookItem()
   {
      if (bookItem == null)
      {
         bookItem = new ItemStack(Material.WRITTEN_BOOK, 1);
         writeSpellBookMeta();
      }

      return bookItem;
   }

   /**
    * Get the title for this book.
    *
    * @return title
    */
   @NotNull
   public String getTitle ()
   {
      return bookType.title;
   }

   /**
    * Get the short title for this book
    *
    * @return short title for this book
    */
   @NotNull
   public String getShortTitle ()
   {
      return bookType.shortTitle;
   }

   /**
    * The author for this book.
    *
    * @return author
    */
   @NotNull
   public String getAuthor ()
   {
      return bookType.author;
   }

   /**
    * The branch for this book.
    *
    * @return branch
    */
   @NotNull
   public O2MagicBranch getBranch ()
   {
      return bookType.branch;
   }
}
