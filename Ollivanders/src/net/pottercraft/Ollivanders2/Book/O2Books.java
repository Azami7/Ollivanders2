package net.pottercraft.Ollivanders2.Book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Potion.O2PotionType;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Player.O2Player;
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
   private Map <String, O2BookType> O2BookMap = new HashMap<>();

   private Ollivanders2 p;
   BookTexts spellText;

   private ItemStack library;

   public static final ArrayList<O2BookType> hogwartsReadingList = new ArrayList<O2BookType>() {{
      add(O2BookType.A_BEGINNERS_GUIDE_TO_TRANSFIGURATION);
      add(O2BookType.ACHIEVEMENTS_IN_CHARMING);
      add(O2BookType.ADVANCED_POTION_MAKING);
      add(O2BookType.ADVANCED_TRANSFIGURATION);
      add(O2BookType.BREAK_WITH_A_BANSHEE);
      add(O2BookType.CONFRONTING_THE_FACELESS);
      add(O2BookType.ESSENTIAL_DARK_ARTS);
      add(O2BookType.EXTREME_INCANTATIONS);
      add(O2BookType.GADDING_WITH_GHOULS);
      add(O2BookType.HOLIDAYS_WITH_HAGS);
      add(O2BookType.INTERMEDIATE_TRANSFIGURATION);
      add(O2BookType.MAGICAL_DRAFTS_AND_POTIONS);
      add(O2BookType.NUMEROLOGY_AND_GRAMMATICA);
      add(O2BookType.QUINTESSENCE_A_QUEST);
      add(O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_1);
      add(O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_2);
      add(O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_3);
      add(O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_4);
      add(O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_5);
      add(O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_6);
      add(O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_7);
      add(O2BookType.THE_DARK_FORCES);
      add(O2BookType.TRAVELS_WITH_TROLLS);
      add(O2BookType.VOYAGES_WITH_VAMPIRES);
      add(O2BookType.WANDERINGS_WITH_WEREWOLVES);
      add(O2BookType.YEAR_WITH_A_YETI);
   }};

   /**
    * Constructor
    *
    * @param plugin the MC plugin
    */
   public O2Books (Ollivanders2 plugin)
   {
      p = plugin;

      spellText = new BookTexts(plugin);

      // add all books
      addBooks();

      library = null;

      p.getLogger().info("Created Ollivanders2 books.");
   }

   /**
    * Add all books in the O2BookType enum to the O2BooksMap.
    */
   private void addBooks ()
   {
      p.getLogger().info("Adding all books...");
      for (O2BookType bookType : O2BookType.values())
      {
         O2Book book = getO2BookByType(bookType);

         if (book != null)
         {
            O2BookMap.put(book.getTitle(), bookType);
         }
      }
   }

   /**
    * Get an o2book by book type.
    *
    * @param bookType the book to be returned
    * @return the BookItem if found bookType was found, null otherwise.
    */
   private O2Book getO2BookByType (O2BookType bookType)
   {
      Class bookClass = bookType.getClassName();

      O2Book book = null;

      try
      {
         book = (O2Book)bookClass.getConstructor(Ollivanders2.class).newInstance(p);
      }
      catch (Exception exception)
      {
         p.getLogger().info("Exception trying to create new instance of " + bookType);
         if (Ollivanders2.debug)
            exception.printStackTrace();
      }

      return book;
   }

   /**
    * Get a book item of this book.
    *
    * @param title the title of the book
    * @return a book item version of this book if it exists, null otherwise.
    */
   public ItemStack getBookByTitle (String title)
   {
      String searchFor = title.toLowerCase();
      O2BookType match = null;

      // Iterate through all keys rather than a direct lookup so that we can:
      // - allow case insensitive lookup
      // - allow partial match for lazy typing
      for (String key : O2BookMap.keySet())
      {
         String bookTitle = key.toLowerCase();

         if (bookTitle.startsWith(searchFor))
         {
            match = O2BookMap.get(key);
         }
      }

      if (match == null)
         return null;

      O2Book o2book = getO2BookByType(match);

      return o2book.createBook();
   }

   /**
    * Gets all Ollivanders2 books.
    *
    * @return a ArrayList of all O2Book objects.
    */
   public ArrayList<ItemStack> getAllBooks ()
   {
      ArrayList<ItemStack> bookStack = new ArrayList<>();

      for (O2BookType bookType : O2BookType.values())
      {
         O2Book o2book = getO2BookByType(bookType);

         if (o2book != null)
         {
            bookStack.add(o2book.createBook());
         }
      }

      return bookStack;
   }

   /**
    * If bookLearning is enabled, read the lore for a book and learn all spells not yet known.
    *
    * @param bookLore the lore from a book
    * @param player the player reading the book
    * @param p the callback to the MC plugin
    */
   public static void readLore (List<String> bookLore, Player player, Ollivanders2 p)
   {
      if (bookLore == null || player == null || !Ollivanders2.useBookLearning)
      {
         return;
      }

      O2Player o2p = p.players.getPlayer(player.getUniqueId());
      if (o2p == null)
         return;

      for (String spell : bookLore)
      {
         // see if it is a spell
         O2SpellType spellEnum = p.spells.getSpellTypeByName(spell);

         if (spellEnum != null)
         {
            int spellLevel = o2p.getSpellCount(spellEnum);

            // if spell count is less than 25, learn this spell
            if (spellLevel < 25)
            {
               p.incSpellCount(player, spellEnum);

               // if they have the improved learning effect, increment it again
               if (p.players.playerEffects.hasEffect(o2p.getID(), O2EffectType.IMPROVED_BOOK_LEARNING))
               {
                  p.incSpellCount(player, spellEnum);
               }
            }
         }
         else // see if it is a potion
         {
            O2PotionType potionEnum = p.potions.getPotionTypeByName(spell);

            if (potionEnum != null)
            {
               int potionCount = o2p.getPotionCount(potionEnum);

               // if potion count < 10, learn this potion
               if (potionCount < 10)
               {
                  p.incPotionCount(player, potionEnum);

                  // if they have the improved learning effect, increment it again
                  if (p.players.playerEffects.hasEffect(o2p.getID(), O2EffectType.IMPROVED_BOOK_LEARNING))
                  {
                     p.incPotionCount(player, potionEnum);
                  }
               }
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

      for (Entry<String, O2BookType> e : O2BookMap.entrySet())
      {
         if (hogwartsReadingList.contains(e.getValue()))
            bookTitles.add(e.getKey());
      }

      Collections.sort(bookTitles);

      String page = "";
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
    * Get all of the titles for all loaded O2 books
    *
    * @return a list of the titles for all loaded books
    */
   public ArrayList<String> getAllBookTitles ()
   {
      ArrayList<String> bookTitles = new ArrayList<>();

      bookTitles.addAll(O2BookMap.keySet());
      Collections.sort(bookTitles);

      return bookTitles;
   }
}
