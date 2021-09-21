package net.pottercraft.ollivanders2;

import net.pottercraft.ollivanders2.book.O2Books;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.divination.O2Prophecies;
import net.pottercraft.ollivanders2.house.O2Houses;
import net.pottercraft.ollivanders2.item.O2Items;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.player.O2Players;
import net.pottercraft.ollivanders2.potion.O2Potions;
import net.pottercraft.ollivanders2.spell.APPARATE;
import net.pottercraft.ollivanders2.spell.O2Spells;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpells;
import org.jetbrains.annotations.NotNull;

/**
 * API for allowing other plugins to interact with Ollivanders.
 */
public class Ollivanders2API
{
   private static O2Players players;
   private static O2StationarySpells stationarySpells;
   private static O2Prophecies prophecies;
   private static O2Items items;
   private static Ollivanders2OwlPost owlPost;

   public static O2PlayerCommon playerCommon;
   public static Ollivanders2Common common;

   static void init (@NotNull Ollivanders2 p)
   {
      if (common == null)
      {
         common = new Ollivanders2Common(p);
      }
   }

   /**
    * Get the house management object
    *
    * @return houses management object
    */
   @NotNull
   public static O2Houses getHouses ()
   {
      return Ollivanders2.houses;
   }

   static void initPlayers (@NotNull Ollivanders2 p)
   {
      if (players == null)
      {
         players = new O2Players(p);
         players.loadO2Players();
      }

      if (playerCommon == null)
      {
         playerCommon = new O2PlayerCommon(p);
      }
   }

   static void savePlayers()
   {
      if (players != null)
         players.saveO2Players();
   }

   /**
    * Get the player management object.
    *
    * @param p a reference to the Ollivanders2 plugin
    * @return the player management object
    */
   @NotNull
   public static O2Players getPlayers(@NotNull Ollivanders2 p)
   {
      if (players == null)
         initPlayers(p);

      return players;
   }

   /**
    * Get the books management object.
    *
    * @return the book management object
    */
   @NotNull
   public static O2Books getBooks()
   {
      return Ollivanders2.books;
   }

   /**
    * Get the spells management object.
    *
    * @return the spells management object
    */
   @NotNull
   public static O2Spells getSpells ()
   {
      return Ollivanders2.spells;
   }

   /**
    * Get the potions management object.
    *
    * @return the potions management object
    */
   @NotNull
   public static O2Potions getPotions ()
   {
      return Ollivanders2.potions;
   }

   static void initStationarySpells (@NotNull Ollivanders2 p)
   {
      if (stationarySpells == null)
      {
         stationarySpells = new O2StationarySpells(p);
      }
   }

   static void saveStationarySpells ()
   {
      if (stationarySpells != null)
         stationarySpells.saveO2StationarySpells();
   }

   /**
    * Get the stationary spells management object.
    *
    * @param p a reference to the Ollivanders2 plugin
    * @return the stationary spells management object
    */
   @NotNull
   public static O2StationarySpells getStationarySpells (@NotNull Ollivanders2 p)
   {
      if (stationarySpells == null)
         initStationarySpells(p);

      return stationarySpells;
   }

   static void initProphecies (@NotNull Ollivanders2 p)
   {
      if (prophecies == null)
      {
         prophecies = new O2Prophecies(p);
      }
   }

   static void saveProphecies ()
   {
      if (prophecies != null)
         prophecies.saveProphecies();
   }

   /**
    * Get the prophecy management object.
    *
    * @param p a reference to the Ollivanders2 plugin
    * @return the prophecy management object
    */
   @NotNull
   public static O2Prophecies getProphecies (@NotNull Ollivanders2 p)
   {
      if (prophecies == null)
         initProphecies(p);

      return prophecies;
   }

   public static void initItems (@NotNull Ollivanders2 p)
   {
      if (items == null)
      {
         items = new O2Items(p);
      }
   }

   /**
    * Get the item management object.
    *
    * @param p a reference to the Ollivanders2 plugin
    * @return the item management object
    */
   @NotNull
   public static O2Items getItems (@NotNull Ollivanders2 p)
   {
      if (items == null)
         initItems(p);

      return items;
   }

   /**
    * Set up owl post
    *
    * @param p a callback to the plugin
    */
   public static void initOwlPost (@NotNull Ollivanders2 p)
   {
      if (owlPost == null)
      {
         owlPost = new Ollivanders2OwlPost(p);
      }
   }

   /**
    * Get the owl post management object
    *
    * @param p a callback to the plugin
    * @return the owlPost management object
    */
   @NotNull
   public static Ollivanders2OwlPost getOwlPost (@NotNull Ollivanders2 p)
   {
      if (owlPost == null)
         initOwlPost(p);

      return owlPost;
   }
}
