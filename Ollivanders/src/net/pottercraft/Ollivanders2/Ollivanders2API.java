package net.pottercraft.Ollivanders2;

import net.pottercraft.Ollivanders2.Book.O2Books;
import net.pottercraft.Ollivanders2.House.O2Houses;
import net.pottercraft.Ollivanders2.Player.O2PlayerCommon;
import net.pottercraft.Ollivanders2.Player.O2Players;
import net.pottercraft.Ollivanders2.Potion.O2Potions;
import net.pottercraft.Ollivanders2.Spell.O2Spells;
import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpells;

public class Ollivanders2API
{
   static O2Houses houses;
   static O2Players players;
   static O2Books books;
   static O2Spells spells;
   static O2Potions potions;
   static O2StationarySpells stationarySpells;

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

   static void savePlayers ()
   {
      players.saveO2Players();
   }

   public static O2Players getPlayers ()
   {
      return players;
   }

   static void initBooks (Ollivanders2 p)
   {
      books = new O2Books(p);
   }

   public static O2Books getBooks ()
   {
      return books;
   }

   static void initSpells (Ollivanders2 p)
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
}
