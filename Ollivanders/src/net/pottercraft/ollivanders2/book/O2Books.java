package net.pottercraft.ollivanders2.book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
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
public final class O2Books implements Listener
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
      p.getServer().getPluginManager().registerEvents(this, p);

      common.printLogMessage("Created Ollivanders2 books.", null, null, false);
   }

   /**
    * Process book read events when bookLearning is enabled.
    *
    * @param event the player interact event
    */
   @EventHandler(priority = EventPriority.LOWEST)
   public void onBookRead (@NotNull PlayerInteractEvent event)
   {
      // only run this if bookLearning is enabled
      if (!Ollivanders2.bookLearning)
         return;

      Action action = event.getAction();
      if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR)
      {
         Player player = event.getPlayer();

         ItemStack heldItem = player.getInventory().getItemInMainHand();
         if (heldItem.getType() == Material.WRITTEN_BOOK)
         {
            if (Ollivanders2.debug)
               p.getLogger().info(player.getDisplayName() + " reading a book and book learning is enabled.");

            // reading a book, if it is a spell book we want to let the player "learn" the spell.
            ItemMeta meta = heldItem.getItemMeta();
            if (meta == null)
               return;

            List<String> bookLore = heldItem.getItemMeta().getLore();
            if (bookLore == null)
               readNBT(meta, player, p);
            else
               readLore(bookLore, player, p);
         }
      }
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
      O2BookType match = getBookTypeByTitle(title);

      if (match == null)
         return null;

      return getBookByType(match);
   }

   /**
    * Get a book item of this book.
    *
    * @param bookType the type of book
    * @return a book item version of this book
    */
   @Nullable
   public ItemStack getBookByType(@NotNull O2BookType bookType)
   {
      O2Book o2book = getO2BookByType(bookType);

      if (o2book != null)
         return o2book.getBookItem();

      return null;
   }

   /**
    * Get a book type by book title
    *
    * @param title the book title to search for
    * @return the book type, if found, null otherwise
    */
   @Nullable
   public O2BookType getBookTypeByTitle (String title)
   {
      if (title.length() < 1)
         return null;

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

      return match;
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
    * Read the NBT tags for a magic book
    * @param itemMeta the item meta
    * @param player the player reading the book
    * @param p a callback to the plugin
    */
   public static void readNBT (@NotNull ItemMeta itemMeta, @NotNull Player player, @NotNull Ollivanders2 p)
   {
      if (!Ollivanders2.bookLearning)
         return;

      O2Player o2p = Ollivanders2API.getPlayers(p).getPlayer(player.getUniqueId());
      if (o2p == null)
         return;

      PersistentDataContainer container = itemMeta.getPersistentDataContainer();
      // read spells
      if (container.has(O2Book.o2BookSpellsKey, PersistentDataType.STRING))
      {
         String spells = container.get(O2Book.o2BookSpellsKey, PersistentDataType.STRING);
         if (spells != null)
         {
            for (O2SpellType spellType : parseSpells(spells))
            {
               incrementSpell(o2p, spellType, p);
            }
         }
      }

      // read potions
      if (container.has(O2Book.o2BookPotionsKey, PersistentDataType.STRING))
      {
         String potions = container.get(O2Book.o2BookPotionsKey, PersistentDataType.STRING);
         if (potions != null)
         {
            for (O2PotionType potionType : parsePotions(potions))
            {
               incrementPotion(o2p, potionType, p);
            }
         }
      }
   }

   /**
    * Parse the spell types from a space-separated list of spell types.
    *
    * @param spellList the space-separated list
    * @return the spells found in the list
    */
   @NotNull
   private static ArrayList<O2SpellType> parseSpells (@NotNull String spellList)
   {
      ArrayList<O2SpellType> spells = new ArrayList<>();

      for (String s : spellList.split(" "))
      {
         O2SpellType spellType;
         try
         {
            spellType = O2SpellType.valueOf(s);
         }
         catch (Exception e)
         {
            continue;
         }

         spells.add(spellType);
      }

      return spells;
   }

   /**
    * Increment the experience level of a spell for a player
    *
    * @param o2p the player
    * @param spellType the spell
    * @param p a callback to the plugin
    */
   private static void incrementSpell (@NotNull O2Player o2p, @NotNull O2SpellType spellType, @NotNull Ollivanders2 p)
   {
      int spellLevel = o2p.getSpellCount(spellType);

      // if spell count is less than 25, learn this spell
      if (spellLevel < 25)
      {
         o2p.incrementSpellCount(spellType);

         // if they have the improved learning effect, increment it again
         if (Ollivanders2API.getPlayers(p).playerEffects.hasEffect(o2p.getID(), O2EffectType.IMPROVED_BOOK_LEARNING))
         {
            o2p.incrementSpellCount(spellType);
         }
      }
   }

   /**
    * Parse the potion types from a space-separated list of potion types.
    *
    * @param potionList the space-separated list
    * @return the potions found in the list
    */
   @NotNull
   private static ArrayList<O2PotionType> parsePotions (@NotNull String potionList)
   {
      ArrayList<O2PotionType> potions = new ArrayList<>();

      for (String s : potionList.split(" "))
      {
         O2PotionType potionType;
         try
         {
            potionType = O2PotionType.valueOf(s);
         }
         catch (Exception e)
         {
            continue;
         }

         potions.add(potionType);
      }

      return potions;
   }

   /**
    * Increment the experience level of a potion for a player
    *
    * @param o2p the player
    * @param potionType the spell
    * @param p a callback to the plugin
    */
   private static void incrementPotion (@NotNull O2Player o2p, @NotNull O2PotionType potionType, @NotNull Ollivanders2 p)
   {
      int potionLevel = o2p.getPotionCount(potionType);

      // if spell count is less than 25, learn this spell
      if (potionLevel < 25)
      {
         o2p.incrementPotionCount(potionType);

         // if they have the improved learning effect, increment it again
         if (Ollivanders2API.getPlayers(p).playerEffects.hasEffect(o2p.getID(), O2EffectType.IMPROVED_BOOK_LEARNING))
         {
            o2p.incrementPotionCount(potionType);
         }
      }
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
            incrementSpell (o2p, spellEnum, p);
         }
         else // see if it is a potion
         {
            O2PotionType potionEnum = Ollivanders2API.getPotions(p).getPotionTypeByName(spell);

            if (potionEnum != null)
            {
               incrementPotion(o2p, potionEnum, p);
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