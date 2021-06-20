package net.pottercraft.ollivanders2.book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
   private final Map<String, O2BookType> O2BookMap = new HashMap<>();

   Ollivanders2 p;
   Ollivanders2Common common;

   BookTexts spellText;

   private ItemStack library;

   public static final List<O2BookType> hogwartsReadingList = new ArrayList<>()
   {{
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
      add(O2BookType.UNFOGGING_THE_FUTURE);
      add(O2BookType.VOYAGES_WITH_VAMPIRES);
      add(O2BookType.WANDERINGS_WITH_WEREWOLVES);
      add(O2BookType.YEAR_WITH_A_YETI);
   }};

   /**
    * Constructor
    *
    * @param plugin the MC plugin
    */
   public O2Books(@NotNull Ollivanders2 plugin)
   {
      p = plugin;
      common = new Ollivanders2Common(plugin);

      spellText = new BookTexts(plugin);

      // add all books
      addBooks();

      library = null;

      common.printLogMessage("Created Ollivanders2 books.", null, null, false);
   }

   /**
    * Add all books in the O2BookType enum to the O2BooksMap.
    */
   private void addBooks ()
   {
      common.printDebugMessage("Adding all books...", null, null, false);

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
   @Nullable
   private O2Book getO2BookByType(@NotNull O2BookType bookType)
   {
      Class<?> bookClass = bookType.getClassName();

      O2Book book = null;

      try
      {
         book = (O2Book) bookClass.getConstructor(Ollivanders2.class).newInstance(p);
      }
      catch (Exception e)
      {
         common.printDebugMessage("Exception trying to create new instance of " + bookType, e, null, true);
      }

      return book;
   }

   /**
    * Get a book item of this book.
    *
    * @param title the title of the book
    * @return a book item version of this book if it exists, null otherwise.
    */
   @Nullable
   public ItemStack getBookByTitle(@NotNull String title)
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

      if (o2book != null)
         return o2book.getBookItem();

      return null;
   }

   /**
    * Gets all Ollivanders2 books.
    *
    * @return a ArrayList of all O2Book objects.
    */
   @NotNull
   public List<ItemStack> getAllBooks()
   {
      ArrayList<ItemStack> bookStack = new ArrayList<>();

      for (O2BookType bookType : O2BookType.values())
      {
         O2Book o2book = getO2BookByType(bookType);

         if (o2book != null)
         {
            bookStack.add(o2book.getBookItem());
         }
      }

      return bookStack;
   }

   /**
    * If bookLearning is enabled, read the lore for a book and learn all spells not yet known.
    *
    * @param bookLore the lore from a book
    * @param player   the player reading the book
    * @param p        the callback to the MC plugin
    */
   public static void readLore(@NotNull List<String> bookLore, @NotNull Player player, @NotNull Ollivanders2 p)
   {
      if (!Ollivanders2.bookLearning)
      {
         return;
      }

      O2Player o2p = Ollivanders2API.getPlayers(p).getPlayer(player.getUniqueId());
      if (o2p == null)
         return;

      for (String spell : bookLore)
      {
         // see if it is a spell
         O2SpellType spellEnum = Ollivanders2API.getSpells(p).getSpellTypeByName(spell);

         if (spellEnum != null)
         {
            int spellLevel = o2p.getSpellCount(spellEnum);

            // if spell count is less than 25, learn this spell
            if (spellLevel < 25)
            {
               p.incrementSpellCount(player, spellEnum);

               // if they have the improved learning effect, increment it again
               if (Ollivanders2API.getPlayers(p).playerEffects.hasEffect(o2p.getID(), O2EffectType.IMPROVED_BOOK_LEARNING))
               {
                  p.incrementSpellCount(player, spellEnum);
               }
            }
         }
         else // see if it is a potion
         {
            O2PotionType potionEnum = Ollivanders2API.getPotions(p).getPotionTypeByName(spell);

            if (potionEnum != null)
            {
               int potionCount = o2p.getPotionCount(potionEnum);

               // if potion count < 10, learn this potion
               if (potionCount < 10)
               {
                  p.incrementPotionCount(player, potionEnum);

                  // if they have the improved learning effect, increment it again
                  if (Ollivanders2API.getPlayers(p).playerEffects.hasEffect(o2p.getID(), O2EffectType.IMPROVED_BOOK_LEARNING))
                  {
                     p.incrementPotionCount(player, potionEnum);
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
   @Nullable
   public ItemStack getReadingList()
   {
      if (library != null)
      {
         // we already created this list, return it
         return library;
      }

      // being lazy, only do this when someone requests it the first time
      library = new ItemStack(Material.WRITTEN_BOOK, 1);
      BookMeta bookMeta = (BookMeta) library.getItemMeta();

      if (bookMeta == null)
         return null;

      bookMeta.setAuthor("Madam Pince");
      bookMeta.setTitle("Hogwarts Reading List");

      ArrayList<String> bookTitles = new ArrayList<>();

      for (Entry<String, O2BookType> e : O2BookMap.entrySet())
      {
         if (hogwartsReadingList.contains(e.getValue()))
            bookTitles.add(e.getKey());
      }

      Collections.sort(bookTitles);

      StringBuilder page = new StringBuilder();
      int lines = 0;

      for (String s : bookTitles)
      {
         if (page.length() > 220 || lines >= 7)
         {
            page.append("\n(cont.)");
            bookMeta.addPage(page.toString());
            page = new StringBuilder();
            lines = 0;
         }

         page.append("\n");
         page.append(s);
         lines++;
      }
      bookMeta.addPage(page.toString());
      library.setItemMeta(bookMeta);

      return library;
   }

   /**
    * Get all of the titles for all loaded O2 books
    *
    * @return a list of the titles for all loaded books
    */
   @NotNull
   public List<String> getAllBookTitles()
   {
      ArrayList<String> bookTitles = new ArrayList<>(O2BookMap.keySet());
      Collections.sort(bookTitles);

      return bookTitles;
   }
}
