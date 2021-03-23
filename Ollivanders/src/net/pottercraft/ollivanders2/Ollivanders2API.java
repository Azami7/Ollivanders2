package net.pottercraft.ollivanders2;

import net.pottercraft.ollivanders2.book.O2Books;
import net.pottercraft.ollivanders2.divination.O2Prophecies;
import net.pottercraft.ollivanders2.house.O2Houses;
import net.pottercraft.ollivanders2.item.O2Items;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import net.pottercraft.ollivanders2.player.O2Players;
import net.pottercraft.ollivanders2.potion.O2Potions;
import net.pottercraft.ollivanders2.spell.O2Spells;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpells;
import org.jetbrains.annotations.NotNull;

/**
 * API for allowing other plugins to interact with Ollivanders.
 */
public class Ollivanders2API
{
   private static O2Houses houses;
   private static O2Players players;
   private static O2Books books;
   private static O2Spells spells;
   private static O2Potions potions;
   private static O2StationarySpells stationarySpells;
   private static O2Prophecies prophecies;
   private static O2Items items;

   public static O2PlayerCommon playerCommon;
   public static Ollivanders2Common common;

   static void init (@NotNull Ollivanders2 p)
   {
      common = new Ollivanders2Common(p);
   }

   static void initHouses (@NotNull Ollivanders2 p)
   {
      houses = new O2Houses(p);
   }

   static void saveHouses ()
   {
      if (houses != null)
         houses.saveHouses();
   }

   /**
    * Get the house management object
    *
    * @param p a reference to the Ollivanders2 plugin
    * @return houses management object
    */
   @NotNull
   public static O2Houses getHouses (@NotNull Ollivanders2 p)
   {
      if (houses == null)
         initHouses(p);

      return houses;
   }

   static void initPlayers (@NotNull Ollivanders2 p)
   {
      players = new O2Players(p);
      players.loadO2Players();
      playerCommon = new O2PlayerCommon(p);
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

   static void initBooks (@NotNull Ollivanders2 p)
   {
      books = new O2Books(p);
   }

   /**
    * Get the books management object.
    *
    * @param p a reference to the Ollivanders2 plugin
    * @return the book management object
    */
   @NotNull
   public static O2Books getBooks(@NotNull Ollivanders2 p)
   {
      if (books == null)
         initBooks(p);

      return books;
   }

   static void initSpells(@NotNull Ollivanders2 p)
   {
      spells = new O2Spells(p);
   }

   /**
    * Get the spells management object.
    *
    * @param p a reference to the Ollivanders2 plugin
    * @return the spells management object
    */
   @NotNull
   public static O2Spells getSpells (@NotNull Ollivanders2 p)
   {
      if (spells == null)
         initSpells(p);

      return spells;
   }

   static void initPotions (@NotNull Ollivanders2 p)
   {
      potions = new O2Potions(p);
   }

   /**
    * Get the potions management object.
    *
    * @param p a reference to the Ollivanders2 plugin
    * @return the potions management object
    */
   @NotNull
   public static O2Potions getPotions (@NotNull Ollivanders2 p)
   {
      if (potions == null)
         initPotions(p);

      return potions;
   }

   static void initStationarySpells (@NotNull Ollivanders2 p)
   {
      stationarySpells = new O2StationarySpells(p);
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
      prophecies = new O2Prophecies(p);
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
      items = new O2Items(p);
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
}
