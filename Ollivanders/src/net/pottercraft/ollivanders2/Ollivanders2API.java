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

   static void init (Ollivanders2 p)
   {
      common = new Ollivanders2Common(p);
   }

   static void initHouses (Ollivanders2 p)
   {
      houses = new O2Houses(p);
   }

   static void saveHouses ()
   {
      houses.saveHouses();
   }

   public static O2Houses getHouses ()
   {
      return houses;
   }

   static void initPlayers (Ollivanders2 p)
   {
      players = new O2Players(p);
      players.loadO2Players();
      playerCommon = new O2PlayerCommon(p);
   }

   static void savePlayers()
   {
      players.saveO2Players();
   }

   /**
    * Get the player management object.
    *
    * @return the player management object
    */
   @NotNull
   public static O2Players getPlayers()
   {
      return players;
   }

   static void initBooks(Ollivanders2 p)
   {
      books = new O2Books(p);
   }

   /**
    * Get he books managament object.
    *
    * @return the book management object
    */
   public static O2Books getBooks()
   {
      return books;
   }

   static void initSpells(Ollivanders2 p)
   {
      spells = new O2Spells(p);
   }

   public static O2Spells getSpells ()
   {
      return spells;
   }

   static void initPotions (Ollivanders2 p)
   {
      potions = new O2Potions(p);
   }

   public static O2Potions getPotions ()
   {
      return potions;
   }

   static void initStationarySpells (Ollivanders2 p)
   {
      stationarySpells = new O2StationarySpells(p);
   }

   static void saveStationarySpells ()
   {
      stationarySpells.saveO2StationarySpells();
   }

   public static O2StationarySpells getStationarySpells ()
   {
      return stationarySpells;
   }

   static void initProphecies (Ollivanders2 p)
   {
      prophecies = new O2Prophecies(p);
   }

   static void saveProphecies ()
   {
      prophecies.saveProphecies();
   }

   public static O2Prophecies getProphecies ()
   {
      return prophecies;
   }

   public static void initItems (Ollivanders2 p)
   {
      items = new O2Items(p);
   }

   public static O2Items getItems ()
   {
      return items;
   }
}
