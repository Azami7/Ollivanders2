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
public class O2Prophecies
{
   private Ollivanders2 p;

   private ArrayList<O2Prophecy> activeProphecies = new ArrayList<>();
   private ArrayList<O2Prophecy> offlineProphecies = new ArrayList<>();

   /**
    * Constructor
    *
    * @param plugin a callback to the plugin
    */
   public O2Prophecies (Ollivanders2 plugin)
   {
      p = plugin;
   }

   /**
    * Add a prophecy
    *
    * @param prophecy
    */
   public void addProphecy (O2Prophecy prophecy)
   {
      if (Ollivanders2.debug)
      {
         p.getLogger().info("Adding prophecy");
      }

      activeProphecies.add(prophecy);
   }

   /**
    * Add a prophecy to the offline prophecies when the target user is offline
    *
    * @param prophecy
    */
   void addOfflineProphecy (O2Prophecy prophecy)
   {
      if (Ollivanders2.debug)
      {
         p.getLogger().info("Adding prophecy");
      }

      offlineProphecies.add(prophecy);
   }

   /**
    * Get a prophecy made about a player
    *
    * @param pid the player
    * @return a prophecy if found, null otherwise
    */
   public O2Prophecy getProphecyAboutPlayer (UUID pid)
   {
      for (O2Prophecy prophecy : activeProphecies)
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
   public O2Prophecy getProphecyByPlayer (UUID pid)
   {
      for (O2Prophecy prophecy : activeProphecies)
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
      ArrayList<O2Prophecy> prophecies = new ArrayList<>(activeProphecies);

      for (O2Prophecy prophecy : prophecies)
      {
         if (!prophecy.isKilled())
         {
            prophecy.age();

            if (prophecy.getTime() < 1)
            {
               prophecy.fulfill();

               if (offlineProphecies.contains(prophecy))
               {
                  activeProphecies.remove(prophecy);
               }
            }
         }

         if (prophecy.isKilled())
         {
            activeProphecies.remove(prophecy);
         }
      }
   }

   public void saveProphecies ()
   {

   }
}
