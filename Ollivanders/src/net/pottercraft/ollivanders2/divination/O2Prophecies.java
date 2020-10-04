package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.GsonDAO;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Class for managing all prophecies. A prophecy is made via divination and concerns future effects on a player.
 *
 * @author Azami7
 * @since 2.2.9
 */
public class O2Prophecies
{
   final private Ollivanders2 p;

   final private List<O2Prophecy> activeProphecies = new ArrayList<>();
   final private List<O2Prophecy> offlineProphecies = new ArrayList<>();

   final static private String effectTypeLabel = "Effect_Type";
   final static private String targetIDLabel = "Target_ID";
   final static private String prophetIDLabel = "Prophet_ID";
   final static private String timeLabel = "Time";
   final static private String durationLabel = "Duration";
   final static private String accuracyLabel = "Accuracy";
   final static private String prophecyLabel = "Prophecy";

   /**
    * Constructor
    *
    * @param plugin a callback to the plugin
    */
   public O2Prophecies(@NotNull Ollivanders2 plugin)
   {
      p = plugin;

      loadProphecies();
   }

   /**
    * Add a prophecy
    *
    * @param prophecy the prophecy to add
    */
   public void addProphecy(@NotNull O2Prophecy prophecy)
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
    * @param prophecy the prophecy to add
    */
   void addOfflineProphecy(@NotNull O2Prophecy prophecy)
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
   @Nullable
   public O2Prophecy getProphecyAboutPlayer(@NotNull UUID pid)
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
    * Get a list of all unfulfilled prophecies
    *
    * @return a list of all prophecies
    */
   @NotNull
   public List<String> getProphecies ()
   {
      ArrayList<String> prophecies = new ArrayList<>();

      for (O2Prophecy prophecy : activeProphecies)
      {
         prophecies.add(prophecy.getProphecyMessage());
      }

      for (O2Prophecy prophecy : offlineProphecies)
      {
         prophecies.add(prophecy.getProphecyMessage());
      }

      return prophecies;
   }

   /**
    * Get a prophecy made by a player
    *
    * @param pid the player
    * @return a prophecy if found, null otherwise
    */
   @Nullable
   public O2Prophecy getProphecyByPlayer(@NotNull UUID pid)
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

   /**
    * Save all prophecies
    */
   public void saveProphecies ()
   {
      List<Map<String, String>> prophecies = serializeProphecies();

      GsonDAO gsonLayer = new GsonDAO(p);
      gsonLayer.writeSaveData(prophecies, GsonDAO.o2PropheciesJSONFile);
   }

   /**
    * Load saved prophecies
    */
   private void loadProphecies ()
   {
      GsonDAO gsonLayer = new GsonDAO(p);
      List<Map<String, String>> prophecies = gsonLayer.readSavedDataListMap(GsonDAO.o2PropheciesJSONFile);

      if (prophecies == null)
      {
         p.getLogger().info("No saved prophecies.");
         return;
      }

      for (Map<String, String> prophecyData : prophecies)
      {
         O2Prophecy prophecy = deserializeProphecy(prophecyData);

         if (prophecy != null)
         {
            activeProphecies.add(prophecy);
         }
      }

      p.getLogger().info("Loaded " + activeProphecies.size() + " prophecies.");
   }

   /**
    * Serialize prophecies to strings so they can be saved.
    *
    * @return a list of all prophecies serialized to strings
    */
   @NotNull
   private List<Map<String, String>> serializeProphecies ()
   {
      List<Map<String, String>> prophecies = new ArrayList<>();

      // add active prophecies
      for (O2Prophecy prophecy : activeProphecies)
      {
         if (prophecy.isKilled())
         {
            continue;
         }

         prophecies.add(serializeProphecy(prophecy));
      }

      // add offline prophecies
      for (O2Prophecy prophecy : offlineProphecies)
      {
         if (prophecy.isKilled())
         {
            continue;
         }

         prophecies.add(serializeProphecy(prophecy));
      }

      return prophecies;
   }

   /**
    * Serialize the data for a prophecy in to a map of Strings
    *
    * @param prophecy the prophecy to serialize
    * @return the map of serialized prophecy data
    */
   @NotNull
   private Map<String, String> serializeProphecy(@NotNull O2Prophecy prophecy)
   {
      Map<String, String> prophecyData = new HashMap<>();

      // prophecy
      prophecyData.put(prophecyLabel, prophecy.getProphecyMessage());

      // prophet
      prophecyData.put(prophetIDLabel, prophecy.getProphetID().toString());

      // target
      prophecyData.put(targetIDLabel, prophecy.getTargetID().toString());

      // effect
      prophecyData.put(effectTypeLabel, prophecy.getEffect().toString());

      // duration
      prophecyData.put(durationLabel, Integer.toString(prophecy.getDuration()));

      // accuracy
      prophecyData.put(accuracyLabel, Integer.toString(prophecy.getAccuracy()));

      // time
      prophecyData.put(timeLabel, Long.toString(prophecy.getTime()));

      return prophecyData;
   }

   /**
    * Deserialize prophecy data in to a prophecy.
    *
    * @param prophecyData the serialized prophecy data
    * @return a prophecy if the data was read successfully, null otherwise
    */
   @Nullable
   private O2Prophecy deserializeProphecy(@NotNull Map<String, String> prophecyData)
   {
      O2Prophecy prophecy = null;

      O2EffectType effectType = null;
      String prophecyMessage = null;
      UUID targetID = null;
      UUID prophetID = null;
      Long time = null;
      Integer duration = null;
      Integer accuracy = null;

      for (Map.Entry<String, String> entry : prophecyData.entrySet())
      {
         String key = entry.getKey();
         String value = entry.getValue();

         try
         {
            // effect type
            if (key.equals(effectTypeLabel))
            {
               effectType = O2EffectType.valueOf(value);
            }
            // prophecy
            else if (key.equals(prophecyLabel))
            {
               prophecyMessage = value;
            }
            // target
            else if (key.equals(targetIDLabel))
            {
               targetID = UUID.fromString(value);
            }
            // prophet
            else if (key.equals(prophetIDLabel))
            {
               prophetID = UUID.fromString(value);
            }
            // time
            else if (key.equals(timeLabel))
            {
               time = Long.valueOf(value);
            }
            // duration
            else if (key.equals(durationLabel))
            {
               duration = Integer.valueOf(value);
            }
            // accuracy
            else if (key.equals(accuracyLabel))
            {
               accuracy = Integer.valueOf(value);
            }
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failure reading saved prophecy data.");
            e.printStackTrace();
            return null;
         }
      }

      if (effectType != null && prophecyMessage != null && targetID != null && prophetID != null && time != null && duration != null && accuracy != null)
      {
         prophecy = new O2Prophecy(p, effectType, prophecyMessage, targetID, prophetID, time, duration, accuracy);
      }
      else
      {
         p.getLogger().info("Failure reading saved prophecy data - one or more fields missing.");
      }

      return prophecy;
   }

   /**
    * On join, load in to active prophecy list any prophecies about this player.
    *
    * @param pid the ID of the player that joined
    */
   public void onJoin(@NotNull UUID pid)
   {
      int count = 0;

      ArrayList<O2Prophecy> prophecies = new ArrayList<>(offlineProphecies);

      for (O2Prophecy prophecy : prophecies)
      {
         if (prophecy.getTargetID() == pid)
         {
            activeProphecies.add(prophecy);
            offlineProphecies.remove(prophecy);

            count++;
         }
      }

      if (Ollivanders2.debug)
      {
         p.getLogger().info("Loaded " + count + " prophecies for player.");
      }
   }

   /**
    * Get a prophecy about a specific player.
    *
    * @param targetID the player to get a prophecy about
    * @return the prophecy text if found, null otherwise
    */
   @Nullable
   public String getProphecy(@NotNull UUID targetID)
   {
      String prophecy = null;

      for (O2Prophecy prop : activeProphecies)
      {
         if (prop.getTargetID() == targetID)
         {
            prophecy = prop.getProphecyMessage();
         }
      }

      if (prophecy != null)
      {
         return prophecy;
      }

      for (O2Prophecy prop : offlineProphecies)
      {
         if (prop.getTargetID() == targetID)
         {
            prophecy = prop.getProphecyMessage();
         }
      }

      return prophecy;
   }
}
