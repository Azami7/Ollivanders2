package net.pottercraft.Ollivanders2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.pottercraft.Ollivanders2.House.O2HouseType;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class GsonDataPersistenceLayer implements DataPersistenceLayer
{
   private Gson gson;
   private Ollivanders2 p;

   private String saveDirectory = "plugins/Ollivanders2";
   private String archiveDirectory = "plugins/Ollivanders2/archive";
   private String housesJSONFile = "O2Houses.txt";
   private String housePointsJSONFile = "O2HousePoints.txt";
   private String o2PlayerJSONFile = "O2Players.txt";
   private String o2StationarySpellsJSONFile = "O2StationarySpells.txt";

   /**
    * Constructor
    *
    * @param plugin
    */
   public GsonDataPersistenceLayer (Ollivanders2 plugin)
   {
      gson = new GsonBuilder().setPrettyPrinting().create();
      p = plugin;
   }

   /**
    * Write the O2house data
    *
    * @param map a map of player and house data as strings
    */
   @Override
   public void writeHouses (Map<UUID, O2HouseType> map)
   {
      // convert to something that can be properly serialized
      Map <String, String> strMap = new HashMap<>();
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
   public void writeHousePoints (Map<O2HouseType, Integer> map)
   {
      Map <String, String> strMap = new HashMap<>();
      for (Entry<O2HouseType, Integer> e : map.entrySet())
      {
         strMap.put(e.getKey().toString(), e.getValue().toString());
      }

      String json = gson.toJson(strMap);
      writeJSON(json, housePointsJSONFile);
   }

   /**
    * Write the O2Player data
    *
    * @param map a map of the player data as strings
    */
   @Override
   public void writeO2Players (Map <String, Map<String, String>> map)
   {
      String json = gson.toJson(map);
      writeJSON(json, o2PlayerJSONFile);
   }

   /**
    * Write the stationary spells json file
    *
    * @param map a map of stationary spell data as strings
    */
   @Override
   public void writeO2StationarySpells (List <Map<String, String>> map)
   {
      String json = gson.toJson(map);
      writeJSON(json, o2StationarySpellsJSONFile);
   }

   /**
    * Read the O2House data from json
    *
    * @return a map of player UUIDs and their O2House
    */
   @Override
   public Map<UUID, O2HouseType> readHouses ()
   {
      String json = readJSON(housesJSONFile);
      if (json == null)
         return null;

      Map<String, String> strMap = new HashMap<>();
      strMap = (Map<String, String>) gson.fromJson(json, strMap.getClass());

      Map<UUID, O2HouseType> map = new HashMap<>();
      for (Entry <String, String> entry : strMap.entrySet())
      {
         String playerID = entry.getKey();
         String house = entry.getValue();

         if (house == null || playerID == null)
            continue;

         UUID pid = p.common.uuidFromString(playerID);
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
            p.getLogger().warning("Failed to convert house " + house);
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
   public Map<O2HouseType, Integer> readHousePoints ()
   {
      String json = readJSON(housePointsJSONFile);
      if (json == null)
         return null;

      Map<String, String> strMap = new HashMap<>();
      strMap = (Map<String, String>) gson.fromJson(json, strMap.getClass());

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
            p.getLogger().warning("Failed to convert house " + house);
            if (Ollivanders2.debug)
               e.printStackTrace();

            continue;
         }

         pts = p.common.integerFromString(points);
         if (pts == null)
         {
            continue;
         }

         map.put(hType, pts);
      }

      return map;
   }

   /**
    * Read the O2Players json file
    *
    * @return a map of the player json data
    */
   @Override
   public Map <String, Map<String, String>> readO2Players ()
   {
      String json = readJSON(o2PlayerJSONFile);

      if (json == null)
         return null;

      Map <String, Map<String, String>> strMap = new HashMap<>();
      strMap = (Map <String, Map<String, String>>) gson.fromJson(json, strMap.getClass());

      return strMap;
   }

   /**
    * Write json data to a file.
    *
    * @param json the json data to write
    * @param path the path to the json file
    */
   private synchronized void writeJSON (String json, String path)
   {
      // make sure directory exists
      String saveFile = saveDirectory + "/" + path;

      File file = new File(saveFile);
      File dir = new File(saveDirectory);

      try
      {
         if(file.exists())
         {
            // if the file exists, we want to move it
            File archiveDir = new File(archiveDirectory);
            archiveDir.mkdirs();
            String archiveFile = archiveDirectory + "/" + path + "-" + getCurrentTimestamp();

            File prev = new File(archiveFile);
            file.renameTo(prev);
         }

         // create the directory and file
         dir.mkdirs();
         if (!file.createNewFile())
         {
            p.getLogger().warning("Unable to create save file " + saveFile);
            return;
         }
      }
      catch (Exception e)
      {
         p.getLogger().warning("Error creating save file " + saveFile);
         if (Ollivanders2.debug)
         {
            e.printStackTrace();
         }

         return;
      }

      try
      {
         BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(
               new FileOutputStream(saveFile), "UTF-8"));

         bWriter.write(json);
         bWriter.flush();
         bWriter.close();
      }
      catch (Exception e)
      {
         p.getLogger().warning("Unable to write save file " + saveFile);
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
   private String readJSON (String path)
   {
      String json = null;
      String saveFile = saveDirectory + "/" + path;

      File file = new File(saveFile);

      // see if the file exists and we can access it
      try
      {
         if (!file.exists())
         {
            p.getLogger().info("Save file " + saveFile + " not found, skipping.");
            return null;
         }

         if (!file.canRead())
         {
            p.getLogger().warning("No permissions to read " + saveFile + ". Skipping.");
            return null;
         }
      }
      catch (Exception e)
      {
         p.getLogger().warning("Error trying to read " + saveFile + ". Skipping.");
         if (Ollivanders2.debug)
         {
            e.printStackTrace();
         }
         return null;
      }

      try
      {
         BufferedReader bReader = new BufferedReader(new InputStreamReader(
               new FileInputStream(saveFile), "UTF-8"));

         json = bReader.readLine();

         // read the rest of the file if we're not done
         String curLine;
         while ((curLine = bReader.readLine()) != null)
         {
               json = json + curLine;
         }

         bReader.close();
         p.getLogger().info("Loaded save file " + saveFile);
      }
      catch (Exception e)
      {
         p.getLogger().warning("Error trying to read " + saveFile + ". Skipping.");
         if (Ollivanders2.debug)
         {
            e.printStackTrace();
         }
      }

      return json;
   }

   private String getCurrentTimestamp ()
   {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
      Date date = new Date();

      return dateFormat.format(date);
   }
}
