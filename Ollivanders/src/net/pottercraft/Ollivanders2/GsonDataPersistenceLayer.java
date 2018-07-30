package net.pottercraft.Ollivanders2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.pottercraft.Ollivanders2.House.O2Houses;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

public class GsonDataPersistenceLayer implements DataPersistenceLayer
{
   Gson gson;
   Ollivanders2 p;
   Ollivanders2Common common;

   private String saveDirectory = "plugins/Ollivanders2";
   private String housesJSONFile = "O2Houses.txt";
   private String housePointsJSONFile = "O2HousePoints.txt";
   private String o2PlayerJSONFile = "O2Players.txt";
   private String o2StationarySpellsJSONFile = "O2StationarySpells.txt";

   public GsonDataPersistenceLayer (Ollivanders2 plugin)
   {
      gson = new GsonBuilder().setPrettyPrinting().create();
      p = plugin;

      common = new Ollivanders2Common(p);
   }

   @Override
   public void writeHouses (Map<UUID, O2Houses.O2HouseType> map)
   {
      // convert to something that can be properly serialized
      Map <String, String> strMap = new HashMap<>();
      for (Entry<UUID, O2Houses.O2HouseType> e : map.entrySet())
      {
         strMap.put(e.getKey().toString(), e.getValue().toString());
      }

      String json = gson.toJson(strMap);
      writeJSON(json, housesJSONFile);
   }

   @Override
   public void writeHousePoints (Map<O2Houses.O2HouseType, Integer> map)
   {
      Map <String, String> strMap = new HashMap<>();
      for (Entry<O2Houses.O2HouseType, Integer> e : map.entrySet())
      {
         strMap.put(e.getKey().toString(), e.getValue().toString());
      }

      String json = gson.toJson(strMap);
      writeJSON(json, housePointsJSONFile);
   }

   @Override
   public void writeO2Players (Map <String, Map<String, String>> map)
   {
      String json = gson.toJson(map);
      writeJSON(json, o2PlayerJSONFile);
   }

   @Override
   public void writeO2O2StationarySpells (List <Map<String, String>> map)
   {
      String json = gson.toJson(map);
      writeJSON(json, o2StationarySpellsJSONFile);
   }

   @Override
   public Map<UUID, O2Houses.O2HouseType> readHouses ()
   {
      String json = readJSON(housesJSONFile);
      if (json == null)
         return null;

      Map<String, String> strMap = new HashMap<>();
      strMap = (Map<String, String>) gson.fromJson(json, strMap.getClass());

      Map<UUID, O2Houses.O2HouseType> map = new HashMap<>();
      for (Entry <String, String> entry : strMap.entrySet())
      {
         String playerID = entry.getKey();
         String house = entry.getValue();

         if (house == null || playerID == null)
            continue;

         UUID pid = common.uuidFromString(playerID);
         if (pid == null)
         {
            continue;
         }

         O2Houses.O2HouseType hType;

         try
         {
            hType = O2Houses.O2HouseType.valueOf(house);
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failed to convert house " + house);
            if (p.debug)
               e.printStackTrace();

            continue;
         }

         map.put(pid, hType);

         if (p.debug)
            p.getLogger().info("Read " + playerID + " : " + house);
      }

      return map;
   }

   @Override
   public Map<O2Houses.O2HouseType, Integer> readHousePoints ()
   {
      String json = readJSON(housePointsJSONFile);
      if (json == null)
         return null;

      Map<String, String> strMap = new HashMap<>();
      strMap = (Map<String, String>) gson.fromJson(json, strMap.getClass());

      Map<O2Houses.O2HouseType, Integer> map = new HashMap<>();
      for (Entry <String, String> entry : strMap.entrySet())
      {
         String house = entry.getKey();
         String points = entry.getValue();

         if (house == null || points == null)
            continue;

         O2Houses.O2HouseType hType;
         Integer pts;

         try
         {
            hType = O2Houses.O2HouseType.valueOf(house);
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failed to convert house " + house);
            if (p.debug)
               e.printStackTrace();

            continue;
         }

         pts = common.integerFromString(points);
         if (pts == null)
         {
            continue;
         }

         map.put(hType, pts);

         if (p.debug)
            p.getLogger().info("Read " + house + " : " + points);
      }

      return map;
   }

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
            // if the file exists, we want to rewrite it
            file.delete();
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
         if (p.debug)
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
         //bWriter.newLine();
         bWriter.flush();
         bWriter.close();
      }
      catch (Exception e)
      {
         p.getLogger().warning("Unable to write save file " + saveFile);
         if (p.debug)
         {
            e.printStackTrace();
         }

         return;
      }
   }

   /**
    * Reads json from the specified file.
    *
    * @param path
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
         if (p.debug)
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
         if (p.debug)
         {
            e.printStackTrace();
         }
      }

      return json;
   }
}
