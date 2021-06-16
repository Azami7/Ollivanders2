package net.pottercraft.ollivanders2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.pottercraft.ollivanders2.house.O2HouseType;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;

/**
 * GSON data persistance layer
 */
public class GsonDAO implements GenericDAO
{
   final private Gson gson;

   private static final String saveDirectory = "plugins/Ollivanders2";
   private static final String archiveDirectory = "plugins/Ollivanders2/archive";
   public static final String housesJSONFile = "O2Houses.txt";
   public static final String housePointsJSONFile = "O2HousePoints.txt";
   public static final String apparateLocationsJSONFile = "O2ApparateLocations.txt";
   public static final String o2PlayerJSONFile = "O2Players.txt";
   public static final String o2StationarySpellsJSONFile = "O2StationarySpells.txt";
   public static final String o2PropheciesJSONFile = "O2Prophecies.txt";

   /**
    * Constructor
    */
   public GsonDAO()
   {
      gson = new GsonBuilder().setPrettyPrinting().create();
   }

   /**
    * Write the O2house data
    *
    * @param map a map of player and house data as strings
    */
   @Override
   public void writeHouses(@NotNull Map<UUID, O2HouseType> map)
   {
      // convert to something that can be properly serialized
      Map<String, String> strMap = new HashMap<>();
      for (Entry<UUID, O2HouseType> e : map.entrySet())
      {
         strMap.put(e.getKey().toString(), e.getValue().toString());
      }

      String json = gson.toJson(strMap);
      writeJSON(json, housesJSONFile);
   }

   /**
    * Write O2House points
    *
    * @param map a map of the O2House points data as strings
    */
   @Override
   public void writeHousePoints(@NotNull Map<O2HouseType, Integer> map)
   {
      Map<String, String> strMap = new HashMap<>();
      for (Entry<O2HouseType, Integer> e : map.entrySet())
      {
         strMap.put(e.getKey().toString(), e.getValue().toString());
      }

      String json = gson.toJson(strMap);
      writeJSON(json, housePointsJSONFile);
   }

   /**
    * Save the apparate locations
    *
    * @param locations the map of location names to Locations
    */
   public void writeApparateData(@NotNull HashMap<String, Location> locations)
   {
      Map<String, String []> serializedLocations = new HashMap<>();
      for (Entry<String, Location> entry : locations.entrySet())
      {
         Location location = entry.getValue();
         String[] locationAsArray = {location.getWorld().getName(), String.valueOf(location.getX()), String.valueOf(location.getY()), String.valueOf(location.getZ())};
         serializedLocations.put(entry.getKey(), locationAsArray);
      }

      String json = gson.toJson(serializedLocations);
      writeJSON(json, apparateLocationsJSONFile);
   }

   /**
    * Write save data serialized in to a hashmap of String, String pairs
    *
    * @param map      the map of saved data
    * @param filename the file to write to
    */
   @Override
   public void writeSaveData(@NotNull HashMap<String, String> map, @NotNull String filename)
   {
      String json = gson.toJson(map);
      writeJSON(json, filename);
   }

   /**
    * Write save data serialized in to a map of String, Map pairs
    *
    * @param map a map of the player data as strings
    */
   @Override
   public void writeSaveData(@NotNull Map<String, Map<String, String>> map, @NotNull String filename)
   {
      String json = gson.toJson(map);
      writeJSON(json, filename);
   }

   /**
    * Write the prophecies to json file
    *
    * @param map a map of prophecy data as strings
    */
   @Override
   public void writeSaveData(@NotNull List<Map<String, String>> map, @NotNull String filename)
   {
      String json = gson.toJson(map);
      writeJSON(json, filename);
   }

   /**
    * Read the O2House data from json
    *
    * @return a map of player UUIDs and their O2House
    */
   @Override
   @Nullable
   public Map<UUID, O2HouseType> readHouses ()
   {
      String json = readJSON(housesJSONFile);
      if (json == null)
         return null;

      Map<String, String> strMap = gson.fromJson(json, new TypeToken<HashMap<String, String>>(){}.getType());


      Map<UUID, O2HouseType> map = new HashMap<>();
      for (Entry <String, String> entry : strMap.entrySet())
      {
         String playerID = entry.getKey();
         String house = entry.getValue();

         if (house == null || playerID == null)
            continue;

         UUID pid = Ollivanders2API.common.uuidFromString(playerID);
         if (pid == null)
         {
            continue;
         }

         O2HouseType hType;

         try
         {
            hType = O2HouseType.valueOf(house);
         }
         catch (Exception e)
         {
            if (Ollivanders2.debug)
               e.printStackTrace();

            continue;
         }

         map.put(pid, hType);
      }

      return map;
   }

   /**
    * read the house points json data
    *
    * @return a map of O2Houses and their points
    */
   @Override
   @Nullable
   public Map<O2HouseType, Integer> readHousePoints ()
   {
      String json = readJSON(housePointsJSONFile);
      if (json == null)
         return null;

      Map<String, String> strMap =  gson.fromJson(json, new TypeToken<HashMap<String, String>>(){}.getType());

      Map<O2HouseType, Integer> map = new HashMap<>();
      for (Entry <String, String> entry : strMap.entrySet())
      {
         String house = entry.getKey();
         String points = entry.getValue();

         if (house == null || points == null)
            continue;

         O2HouseType hType;
         Integer pts;

         try
         {
            hType = O2HouseType.valueOf(house);
         }
         catch (Exception e)
         {
            if (Ollivanders2.debug)
               e.printStackTrace();

            continue;
         }

         pts = Ollivanders2API.common.integerFromString(points);
         if (pts == null)
         {
            continue;
         }

         map.put(hType, pts);
      }

      return map;
   }

   /**
    * Read a serilaized map of strings and maps from json file
    *
    * @return a map of the player json data
    */
   @Override
   @Nullable
   public Map<String, Map<String, String>> readSavedDataMapStringMap(@NotNull String filename)
   {
      String json = readJSON(filename);

      if (json == null)
         return null;

      Map<String, Map<String, String>> strMap = new HashMap<>();
      strMap = (Map<String, Map<String, String>>) gson.fromJson(json, strMap.getClass());

      return strMap;
   }

   /**
    * Read a serilized list of maps from json file
    *
    * @return a list of the serialized stationary spells
    */
   @Override
   @Nullable
   public List<Map<String, String>> readSavedDataListMap(@NotNull String filename)
   {
      String json = readJSON(filename);

      if (json == null)
         return null;

      List<Map<String, String>> strList = new ArrayList<>();
      strList = (List<Map<String, String>>) gson.fromJson(json, strList.getClass());

      return strList;
   }

   /**
    * Write json data to a file.
    *
    * @param json the json data to write
    * @param path the path to the json file
    */
   private synchronized void writeJSON(@NotNull String json, @NotNull String path)
   {
      String saveFile = saveDirectory + "/" + path;

      File file = new File(saveFile);
      File dir = new File(saveDirectory);

      try
      {
         // if the file exists and archiving is turned on, we want to move it, otherwise delete it so we can write a new one
         if (file.exists())
         {
            try
            {
               if (Ollivanders2.archivePreviousBackup)
               {
                  File archiveDir = new File(archiveDirectory);
                  archiveDir.mkdirs();
                  String archiveFile = archiveDirectory + "/" + path + "-" + Ollivanders2API.common.getCurrentTimestamp();

                  File prev = new File(archiveFile);
                  file.renameTo(prev);
               }
               else
                  file.delete();
            }
            catch (Exception e)
            {
               if (Ollivanders2.debug)
               {
                  e.printStackTrace();
               }
            }
         }

         // create the directory and file
         if (!dir.exists())
            dir.mkdirs();

         if (!file.createNewFile())
         {
            return;
         }
      }
      catch (Exception e)
      {
         if (Ollivanders2.debug)
         {
            e.printStackTrace();
         }

         return;
      }

      try
      {
         BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(
                 new FileOutputStream(saveFile), StandardCharsets.UTF_8));

         bWriter.write(json);
         bWriter.flush();
         bWriter.close();
      }
      catch (Exception e)
      {
         if (Ollivanders2.debug)
         {
            e.printStackTrace();
         }
      }
   }

   /**
    * Reads json from the specified file.
    *
    * @param path path to the json file
    * @return the json read or null if the file could not be read
    */
   private String readJSON(@NotNull String path)
   {
      String json = null;
      String saveFile = saveDirectory + "/" + path;

      File file = new File(saveFile);

      // see if the file exists and we can access it
      try
      {
         if (!file.exists())
         {
            return null;
         }

         if (!file.canRead())
         {
            return null;
         }
      }
      catch (Exception e)
      {
         if (Ollivanders2.debug)
         {
            e.printStackTrace();
         }
         return null;
      }

      try
      {
         BufferedReader bReader = new BufferedReader(new InputStreamReader(
                 new FileInputStream(saveFile), StandardCharsets.UTF_8));

         json = bReader.readLine();

         // read the rest of the file if we're not done
         String curLine;
         while ((curLine = bReader.readLine()) != null)
         {
            json = json + curLine;
         }

         bReader.close();
      }
      catch (Exception e)
      {
         if (Ollivanders2.debug)
         {
            e.printStackTrace();
         }
      }

      return json;
   }
}
