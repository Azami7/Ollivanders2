package net.pottercraft.Ollivanders2.Divination;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Class for managing all prophecies. A prophecy is made via divination and concerns future effects on a player.
 *
 * @author Azami7
 * @since 2.2.9
 */
public class Prophecies
{
   private Ollivanders2 p;

   /**
    * Class representing a prophecy. Every prophecy involves predicting an effect on a player in the future and
    * has a specific accuracy which changes the likelihood the prophecy will come to pass.
    */

   private ArrayList<Prophecy> prophecies = new ArrayList<>();

   /**
    * Constructor
    *
    * @param plugin a callback to the plugin
    */
   public Prophecies (Ollivanders2 plugin)
   {
      p = plugin;
   }

   /**
    * Add a prophecy
    *
    * @param prophecy
    */
   public void addProphecy (Prophecy prophecy)
   {
      prophecies.add(prophecy);
   }

   /**
    * Get a prophecy made about a player
    *
    * @param pid the player
    * @return a prophecy if found, null otherwise
    */
   public Prophecy getProphecyAboutPlayer (UUID pid)
   {
      for (Prophecy prophecy : prophecies)
      {
         if (prophecy.getTargetID() == pid)
         {
            return prophecy;
         }
      }

      return null;
   }

   /**
    * Get a prophecy made by a player
    *
    * @param pid the player
    * @return a prophecy if found, null otherwise
    */
   public Prophecy getProphecyByPlayer (UUID pid)
   {
      for (Prophecy prophecy : prophecies)
      {
         if (prophecy.getProphetID() == pid)
         {
            return prophecy;
         }
      }

      return null;
   }

   /**
    * Process all active prophecies
    */
   public void upkeep ()
   {
      ArrayList<Prophecy> activeProphecies = new ArrayList<>(prophecies);

      for (Prophecy prophecy : activeProphecies)
      {
         if (!prophecy.isKilled())
         {
            prophecy.age();

            if (prophecy.getTime() < 1)
            {
               prophecy.fulfill();
            }
         }

         if (prophecy.isKilled())
         {
            prophecies.remove(prophecy);
         }
      }
   }
}
