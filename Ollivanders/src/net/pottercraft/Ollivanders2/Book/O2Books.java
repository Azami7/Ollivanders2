package net.pottercraft.Ollivanders2.Book;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Collections;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.pottercraft.Ollivanders2.Effect.WIT_SHARPENING_POTION;
import net.pottercraft.Ollivanders2.Effect.OEffect;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.Spell.Spells;
import org.bukkit.inventory.meta.BookMeta;

/**
 * Ollivanders2 O2BookType
 *
 * These books will work with the new implementation of bookLearning.  Every Ollivanders2 spell must be in an
 * O2Book (or some other book with lore set up correctly) or players will not be able to learn them when bookLearning
 * is enabled.
 *
 * @since 2.2.4
 * @author Azami7
 */
public final class O2Books
{
   //private Map <O2BookType, O2Book> O2BooksMap = new HashMap<>();
   //private Map <O2BookType, ItemStack> O2BookItemMap = new HashMap<>();
   private Map <String, O2BookType> O2BookMap = new HashMap<>();

   private Ollivanders2 p;
   //public BookTexts spellText;

   private ItemStack library;

   /**
    * Constructor
    *
    * @param plugin the MC plugin
    */
   public O2Books (Ollivanders2 plugin)
   {
      p = plugin;

      // add all books
      addBooks();

      // add all spell text
      spellText = new BookTexts(p);

      library = null;

      p.getLogger().info("Created Ollivanders2 books.");
   }

   /**
    * Add all books in the O2BookType enum to the O2BooksMap.
    */
   private void addBooks ()
   {
      p.getLogger().info("Adding all books...");
      for (O2BookType b : O2BookType.values())
      {
         String bookName = "net.pottercraft.Ollivanders2.O2Book." + b.toString();

         O2Book book = null;

         try
         {
            book = (O2Book)Class.forName(bookName).getConstructor(Ollivanders2.class).newInstance(p);
         }
         catch (Exception exception)
         {
            p.getLogger().info("Exception trying to create new instance of " + bookName);
            if (Ollivanders2.debug)
               exception.printStackTrace();

            continue;
         }

         if (!O2BooksMap.containsKey(b))
         {
            O2BooksMap.put(b, book);
         }
      }
   }

   /**
    * Get a book by title.
    *
    * @param bookType the book to be returned
    * @return the BookItem if found bookType was found, null otherwise.
    */
   public ItemStack getBook (O2BookType bookType)
   {
      ItemStack bookItem;

      O2Book book = O2BooksMap.get(bookType);

      if (book == null)
         return null;

      if (O2BookItemMap.containsKey(bookType))
      {
         bookItem = O2BookItemMap.get(bookType);
      }
      else
      {
         bookItem = book.createBook();
         O2BookItemMap.put(bookType, bookItem);
      }

      return bookItem;
   }

   /**
    * Gets all Ollivanders2 books.
    *
    * @return a ArrayList of all O2Book objects.
    */
   public ArrayList<ItemStack> getAllBooks ()
   {
      ArrayList<ItemStack> bookStack = new ArrayList<>();

      for (Entry<O2BookType, O2Book> entry : O2BooksMap.entrySet())
      {
         O2Book book = entry.getValue();
         O2BookType bookType = entry.getKey();

         //only make this book if it has not already been made
         if (O2BookItemMap.containsKey(bookType))
         {
            bookStack.add(O2BookItemMap.get(bookType));
         }
         else
         {
            ItemStack bookItem = book.createBook();
            O2BookItemMap.put(bookType, bookItem);

            bookStack.add(bookItem);
         }
      }

      return bookStack;
   }

   /**
    * If bookLearning is enabled, read the lore for a book and learn all spells not yet known.
    *
    * @param bookLore the lore from a book
    * @param player the player reading the book
    * @param p
    */
   public static void readLore (List<String> bookLore, Player player, Ollivanders2 p)
   {
      if (bookLore == null || player == null || !p.getConfig().getBoolean("bookLearning"))
      {
         return;
      }

      O2Player o2p = p.getO2Player(player);

      for (String spell : bookLore)
      {
         Spells spellEnum = Spells.decode(spell);
         int spellLevel = 0;

         if (spellEnum != null)
            spellLevel = o2p.getSpellCount(spellEnum);
         else
            spellLevel = o2p.getPotionCount(spell);

         // if spell count is less than 25, learn this spell
         if (spellLevel < 25)
         {
            // check to see if they have the Wit-Sharpening Potion effect
            boolean witSharpening = false;
            for (OEffect effect : o2p.getEffects())
            {
               if (effect instanceof WIT_SHARPENING_POTION)
               {
                  witSharpening = true;
                  break;
               }
            }

            if (spellEnum != null)
            {
               p.incSpellCount(player, spellEnum);
            }
            else
            {
               p.incPotionCount(player, spell);
            }

            // if they have the wit sharpening effect, increment it again
            if (witSharpening)
            {
               if (spellEnum != null)
                  p.incSpellCount(player, spellEnum);
               else
                  p.incPotionCount(player, spell);
            }
         }
      }
   }

   /**
    * Gets a book that lists all the available books.
    *
    * @return a BookItem that lists all O2 books.
    */
   public ItemStack getReadingList ()
   {
      if (library != null)
      {
         // we already created this list, return it
         return library;
      }

      // being lazy, only do this when someone requests it the first time
      library = new ItemStack(Material.WRITTEN_BOOK, 1);
      BookMeta bookMeta = (BookMeta)library.getItemMeta();

      bookMeta.setAuthor("Madam Pince");
      bookMeta.setTitle("Hogwarts Reading List");

      ArrayList<String> bookTitles = new ArrayList<>();
      for (Entry<O2BookType, O2Book> e : O2BooksMap.entrySet())
      {
         O2BookType b = e.getKey();
         bookTitles.add(b.toString().replaceAll("_", " "));
      }
      Collections.sort(bookTitles);

      String page = new String();
      int lines = 0;

      for (String s : bookTitles)
      {
         if (page.length() > 220 || lines >= 7)
         {
            page = page + "\n(cont.)";
            bookMeta.addPage(page);
            page = new String();
            lines = 0;
         }

         page = page + "\n" + s;
         lines++;
      }
      bookMeta.addPage(page);
      library.setItemMeta(bookMeta);

      return library;
   }

   /**
    * Get all of the short titles for all loaded O2 books
    *
    * @return a list of the titles for all loaded books
    */
   public ArrayList<String> getAllBookTitles ()
   {
      ArrayList<String> bookTitles = new ArrayList<>();

      for (Entry <O2BookType, O2Book> e : O2BooksMap.entrySet())
      {
         O2Book book = e.getValue();
         bookTitles.add(book.getTitle());
      }

      return bookTitles;
   }
}
