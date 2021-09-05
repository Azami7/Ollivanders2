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

   /**
    * Get all loaded books.
    *
    * @return the map of loaded book types and titles
    */
   @NotNull
   public Map<String, O2BookType> getLoadedBooks ()
   {
      return new HashMap<>(O2BookMap);
   }
}