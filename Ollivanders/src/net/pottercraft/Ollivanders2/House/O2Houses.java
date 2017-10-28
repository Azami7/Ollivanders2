package net.pottercraft.Ollivanders2.House;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.UUID;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.GsonDataPersistenceLayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.bukkit.Server;

/**
 * "While you are here, your House will be something like your family within Hogwarts.  You will have classes with the
 * rest of your O2HouseType, sleep in your O2HouseType dormitory, and spend free time in your O2HouseType common room."
 *
 * @author Azami7
 */
public class O2Houses
{
   public enum O2HouseType
   {
      HUFFLEPUFF,
      GRYFFINDOR,
      RAVENCLAW,
      SLYTHERIN
   }

   private Ollivanders2 p;
   private Map <UUID, O2HouseType> O2HouseMap = new HashMap<>();
   @Deprecated
   private Map <String, O2HouseType> O2HouseMapOld = new HashMap<>();
   private Map <O2HouseType, Integer> O2HousePointsMap = new HashMap<>();
   private Map <O2HouseType, ChatColor> O2HouseColors = new HashMap<>();
   private boolean isEnabled = true;

   private Scoreboard scoreboard;
   private String objectiveName = "o2_hpoints";
   private String objectiveDisplayName = "House Points";
   private String houseMapFile = "plugins/Ollivanders2/O2HouseMap.bin";
   private String housePointsMapFile = "plugins/Ollivanders2/O2HousePointsMap.bin";

   DisplaySlot scoreboardSlot = DisplaySlot.SIDEBAR;

   /**
    * Constructor.
    *
    * @param plugin
    */
   public O2Houses (Ollivanders2 plugin)
   {
      p = plugin;

      if (p.getConfig().getBoolean("houses"))
      {
         loadHouses();
         initHousePoints();
         createScoreboard();
         updateScoreboard();
         showScoreboard();
      }
      else
      {
         p.getLogger().info("O2Houses not enabled.");
         isEnabled = false;
      }
   }

   /**
    * Initialize the house points map.
    */
   private void initHousePoints ()
   {
      if (!O2HousePointsMap.containsKey(O2HouseType.GRYFFINDOR))
         O2HousePointsMap.put(O2HouseType.GRYFFINDOR, 0);

      if (!O2HousePointsMap.containsKey(O2HouseType.HUFFLEPUFF))
         O2HousePointsMap.put(O2HouseType.HUFFLEPUFF, 0);

      if (!O2HousePointsMap.containsKey(O2HouseType.RAVENCLAW))
         O2HousePointsMap.put(O2HouseType.RAVENCLAW, 0);

      if (!O2HousePointsMap.containsKey(O2HouseType.SLYTHERIN))
         O2HousePointsMap.put(O2HouseType.SLYTHERIN, 0);

      //set house nametag colors
      if (!O2HouseColors.containsKey(O2HouseType.HUFFLEPUFF))
         O2HouseColors.put(O2HouseType.HUFFLEPUFF, ChatColor.YELLOW);

      if (!O2HouseColors.containsKey(O2HouseType.GRYFFINDOR))
         O2HouseColors.put(O2HouseType.GRYFFINDOR, ChatColor.RED);

      if (!O2HouseColors.containsKey(O2HouseType.RAVENCLAW))
         O2HouseColors.put(O2HouseType.RAVENCLAW, ChatColor.BLUE);

      if (!O2HouseColors.containsKey(O2HouseType.SLYTHERIN))
         O2HouseColors.put(O2HouseType.SLYTHERIN, ChatColor.GREEN);
   }

   /**
    * Get the name associated with this house.
    *
    * @return the house name or null if house is null
    */
   public String getHouseName(O2HouseType house)
   {
      //TODO allow house names to be set via config
      if (house == null)
         return null;

      if (house == O2HouseType.GRYFFINDOR)
         return "Gryffindor";
      else if (house == O2HouseType.HUFFLEPUFF)
         return "Hufflepuff";
      else if (house == O2HouseType.RAVENCLAW)
         return "Ravenclaw";
      else if (house == O2HouseType.SLYTHERIN)
         return "Slytherin";
      else
      {
         // this should never happen...
         p.getLogger().warning("Invalid house type " + house.toString());
         return null;
      }
   }

   /**
    * Get the house type by name.
    *
    * @param h the name of the house
    * @return the house type or null if the name is not valid.
    */
   public O2HouseType getHouseType(String h)
   {
      if (h == null)
      {
         if (Ollivanders2.debug)
         {
            p.getLogger().info("getHouseType: null house passed in");
         }

         return null;
      }

      h.trim();

      if (Ollivanders2.debug)
         p.getLogger().info("getHouseType: getting type for " + h);

      //TODO allow house names to be set via config
      if (h.equalsIgnoreCase("Hufflepuff"))
         return O2HouseType.HUFFLEPUFF;
      else if (h.equalsIgnoreCase("Gryffindor"))
         return O2HouseType.GRYFFINDOR;
      else if (h.equalsIgnoreCase("Ravenclaw"))
         return O2HouseType.RAVENCLAW;
      else if (h.equalsIgnoreCase("Slytherin"))
         return O2HouseType.SLYTHERIN;
      else
         return null;
   }

   /**
    * Get all the house names.
    *
    * @return all house names.
    */
   public ArrayList getAllHouseNames ()
   {
      //TODO allow house names to be set via config
      ArrayList<String> houseNames = new ArrayList<>();
      houseNames.add("Hufflepuff");
      houseNames.add("Gryffindor");
      houseNames.add("Ravenclaw");
      houseNames.add("Slytherin");

      return houseNames;
   }

   /**
    * Load the house information saved to disk.
    */
   public void loadHouses()
   {
      // try loading it from json first
      // load houses from JSON
      GsonDataPersistenceLayer gsonLayer = new GsonDataPersistenceLayer(p);
      Map <UUID, O2HouseType> houses = gsonLayer.readHouses();
      if (houses != null)
      {
         O2HouseMap = houses;
      }

      Map <O2HouseType, Integer> housePoints = gsonLayer.readHousePoints();
      if (housePoints != null)
      {
         O2HousePointsMap = housePoints;
      }

      // try reading the old binary files if the maps are still empty
      if (O2HouseMap.isEmpty())
      {
         oldLoadHouses();
      }
   }

   /**
    * Loads house info from the old binary files.  Only use if the json files don't work.
    */
   private void oldLoadHouses()
   {
      // try reading the old binary files if the maps are still empty
      if (O2HouseMap.isEmpty())
      {
         try
         {
            O2HouseMapOld = (HashMap<String, O2HouseType>) Ollivanders2.SLAPI.load(houseMapFile);

            p.getLogger().info("Loaded " + houseMapFile);
         }
         catch (FileNotFoundException e)
         {
            p.getLogger().info("Save file " + houseMapFile + " not found, skipping.");
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failed to load " + houseMapFile);
            if (p.debug)
            {
               e.printStackTrace();
               return;
            }
         }
      }

      if (!O2HouseMapOld.isEmpty())
      {
         Server server = p.getServer();

         // backfill the old house in to the new one
         for (Entry<String, O2HouseType> e : O2HouseMapOld.entrySet())
         {
            UUID playerID = server.getOfflinePlayer(e.getKey()).getUniqueId();
            if (!O2HouseMap.containsKey(playerID))
               O2HouseMap.put(playerID, e.getValue());
         }
      }

      if (O2HousePointsMap.isEmpty())
      {
         try
         {
            O2HousePointsMap = (HashMap<O2HouseType, Integer>) Ollivanders2.SLAPI.load(housePointsMapFile);
            p.getLogger().info("Loaded " + housePointsMapFile);
         }
         catch (FileNotFoundException e)
         {
            p.getLogger().info("Save file " + housePointsMapFile + " not found, skipping.");
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failed to load " + housePointsMapFile);
            if (p.debug)
            {
               e.printStackTrace();
               return;
            }
         }
      }

      if (!O2HousePointsMap.isEmpty())
      {
         // backfill the old house points in to the new one
         for (Entry<O2HouseType, Integer> e : O2HousePointsMap.entrySet())
         {
            O2HouseType house = e.getKey();
            if (!O2HousePointsMap.containsKey(house))
               O2HousePointsMap.put(house, e.getValue());
         }
      }
   }

   /**
    * Save the house information to disk.
    */
   public void saveHouses()
   {
      /*
      try
      {
         Ollivanders2.SLAPI.save (O2HouseMap, houseMapFile);
         p.getLogger().finest("Saved " + houseMapFile);
      }
      catch (Exception e)
      {
         p.getLogger().warning("Failed to save " + houseMapFile);
      }

      try
      {
         Ollivanders2.SLAPI.save (O2HousePointsMap, housePointsMapFile);
         p.getLogger().finest("Saved " + housePointsMapFile);
      }
      catch (Exception e)
      {
         p.getLogger().warning("Failed to save " + housePointsMapFile);
      }
      */

      // write house data out as JSON
      GsonDataPersistenceLayer gsonLayer = new GsonDataPersistenceLayer(p);
      gsonLayer.writeHouses(O2HouseMap);
      p.getLogger().info("O2HousePointsMap size = " + O2HousePointsMap.size());
      gsonLayer.writeHousePoints(O2HousePointsMap);
   }

   /**
    * Sort a player in to a house.
    *
    * @param player
    * @param house
    * @return true if the player is successfully sorted, false otherwise.
    */
   public boolean sort (Player player, O2HouseType house)
   {
      //make sure player is not already sorted
      if (isSorted(player))
         return false;

      O2HouseMap.put(player.getUniqueId(), house);
      setChatColor(player);
      return true;
   }

   /**
    * Determines if a player has been sorted already or not.
    *
    * @param player
    * @return true if the player has been sorted, false otherwise.
    */
   public boolean isSorted (Player player)
   {
      if (O2HouseMap.containsKey(player.getUniqueId()))
         return true;
      else
         return false;
   }

   /**
    * Force sets the players house to a house.  This will happen even if a player has been previously sorted.  This
    * is a separate function so that sort() is not accidentally used once a player has been sorted.
    *
    * @param player
    * @param house
    */
   public void forceSetHouse(Player player, O2HouseType house)
   {
      if (!sort(player, house))
      {
         O2HouseMap.replace(player.getUniqueId(), house);
         setChatColor(player);
      }
   }

   /**
    * Get the house a player is sorted in to.
    *
    * @param player
    * @return the House the player is sorted in to, null otherwise.
    */
   public O2HouseType getHouse (Player player)
   {
      O2HouseType house = null;

      if (O2HouseMap.containsKey(player.getUniqueId()))
      {
         try
         {
            house = O2HouseMap.get(player.getUniqueId());
            p.getLogger().info(player.getDisplayName() + " is in " + house.toString());
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failure retrieving player " + player.getName() + " from O2HouseMap.");
            if (p.debug)
               e.printStackTrace();
         }
      }

      return house;
   }

   /**
    * Get a list of the players sorted in to a house.
    *
    * @param house
    * @return the names of all members of the specified house.
    */
   public ArrayList getHouseMembers (O2HouseType house)
   {
      ArrayList<String> houseMembers = new ArrayList<>();
      Server server = p.getServer();

      for(Entry<UUID, O2HouseType> entry: O2HouseMap.entrySet())
      {
         if (entry.getValue() == house)
         {
            UUID playerID = entry.getKey();
            String playerName = server.getOfflinePlayer(playerID).getName();
            houseMembers.add(playerName);
         }
      }

      return houseMembers;
   }

   /**
    * Get the points for a specific house.
    *
    * @param house
    * @return the points for the house specified, 0 if not found
    */
   public int getHousePoints (O2HouseType house)
   {
      int points = 0;

      if (O2HousePointsMap.containsKey(house))
      {
         points = O2HousePointsMap.get(house);
      }

      return points;
   }

   /**
    * Get the points for all houses.
    *
    * @return
    */
   public HashMap<O2HouseType, Integer> getAllHousePoints ()
   {
      HashMap<O2HouseType, Integer> points = new HashMap<>();

      points.put(O2HouseType.HUFFLEPUFF, O2HousePointsMap.get(O2HouseType.HUFFLEPUFF.toString()));
      points.put(O2HouseType.GRYFFINDOR, O2HousePointsMap.get(O2HouseType.GRYFFINDOR.toString()));
      points.put(O2HouseType.RAVENCLAW, O2HousePointsMap.get(O2HouseType.RAVENCLAW.toString()));
      points.put(O2HouseType.SLYTHERIN, O2HousePointsMap.get(O2HouseType.SLYTHERIN.toString()));

      return points;
   }

   /**
    * Sets the points for a house.
    *
    * @param house
    * @param points
    * @return true if the operation was successful, false if house was not found
    */
   public boolean setHousePoints (O2HouseType house, int points)
   {
      if (O2HousePointsMap.containsKey(house))
      {
         O2HousePointsMap.replace(house, points);
         if (p.debug)
            p.getLogger().info("Set house points for " + house.toString() + " to " + points);
      }
      else
      {
         //O2HousePointsMap.put(house, points);
         p.getLogger().warning("House " + house.toString() + " not found in house points map.");
      }

      return updateScoreboard();
   }

   /**
    * Resets all house points to 0.
    */
   public boolean resetHousePoints ()
   {
      O2HousePointsMap.clear();

      initHousePoints();

      return updateScoreboard();
   }

   /**
    * Resets houses completely.
    */
   public boolean reset()
   {
      p.getLogger().info("Resetting houses...");

      O2HouseMapOld.clear();
      O2HousePointsMap.clear();
      O2HouseMap.clear();

      initHousePoints();
      return updateScoreboard();
   }

   /**
    * Add points to a specific house.
    *
    * @param house
    * @param points
    * @return true if the operation was successful, false if house was not found
    */
   public boolean addHousePoints (O2HouseType house, int points)
   {
      int pts = points;

      if (O2HousePointsMap.containsKey(house))
      {
         pts += O2HousePointsMap.get(house);
      }
      else
      {
         p.getLogger().warning("House " + house.toString() + " not found in house points map.");
      }

      return setHousePoints(house, pts);
   }

   /**
    * Remove points from a specific house.
    *
    * @param house
    * @param points
    * @return true if the operation was successful, false if house was not found
    */
   public boolean subtractHousePoints (O2HouseType house, int points)
   {
      int pts = 0;

      if (O2HousePointsMap.containsKey(house))
      {
         int curPoints = O2HousePointsMap.get(house);
         if (curPoints >= points)
            pts = curPoints - points;
      }
      else
      {
         p.getLogger().warning("House " + house.toString() + " not found in house points map.");
      }

      return setHousePoints(house, pts);
   }

   /**
    * Creates the house points scoreboard.
    *
    * @return true if the operation was successful, false otherwise
    */
   public boolean createScoreboard ()
   {
      if (isEnabled == false)
      {
         // do not allow if houses is not enabled
         p.getLogger().warning("Attempted to create scoreboard when houses is not enabled.");
         return false;
      }

      // hide the main scoreboard
      /*
      Scoreboard mainScoreboard = p.getServer().getScoreboardManager().getMainScoreboard();
      mainScoreboard.clearSlot(scoreboardSlot);
      */

      //scoreboard = p.getServer().getScoreboardManager().getNewScoreboard();
      scoreboard = p.getServer().getScoreboardManager().getMainScoreboard();

      p.getLogger().info("Created scoreboard...");

      // if there was a previous house points objective, remove it
      if (scoreboard.getObjective(objectiveName) != null)
      {
         scoreboard.getObjective(objectiveName).unregister();
         p.getLogger().info("Unregistered previous house points objective...");
      }

      // if there is another objective on the slot we want, remove it
      if (scoreboard.getObjective(scoreboardSlot) != null)
      {
         scoreboard.getObjective(scoreboardSlot).unregister();
         p.getLogger().info("Unregistered previous scoreboard objective...");
      }

      scoreboard.registerNewObjective(objectiveName, "dummy");
      Objective objective = scoreboard.getObjective(objectiveName);
      objective.setDisplayName(objectiveDisplayName);
      objective.setDisplaySlot(scoreboardSlot);

      //register houses
      registerHouseTeam(O2HouseType.HUFFLEPUFF);
      registerHouseTeam(O2HouseType.GRYFFINDOR);
      registerHouseTeam(O2HouseType.RAVENCLAW);
      registerHouseTeam(O2HouseType.SLYTHERIN);

      //set house nametag colors
      scoreboard.getTeam(getHouseName(O2HouseType.HUFFLEPUFF)).setColor(O2HouseColors.get(O2HouseType.HUFFLEPUFF));
      scoreboard.getTeam(getHouseName(O2HouseType.GRYFFINDOR)).setColor(O2HouseColors.get(O2HouseType.GRYFFINDOR));
      scoreboard.getTeam(getHouseName(O2HouseType.RAVENCLAW)).setColor(O2HouseColors.get(O2HouseType.RAVENCLAW));
      scoreboard.getTeam(getHouseName(O2HouseType.SLYTHERIN)).setColor(O2HouseColors.get(O2HouseType.SLYTHERIN));

      updateScoreboard();
      p.getLogger().info("Updated scoreboard with current house points...");

      return true;
   }

   /**
    * Register a team with the scoreboard.
    *
    * @param house the house to register
    */
   private void registerHouseTeam (O2HouseType house)
   {
      String houseName = getHouseName(house);
      try
      {
         scoreboard.registerNewTeam(houseName);
         if (p.debug)
            p.getLogger().info("Added team " + houseName + " to scoreboard.");
      }
      catch (IllegalArgumentException e)
      {
         if (p.debug)
            p.getLogger().info("Team " + houseName + " already registered.");
      }
   }

   /**
    * Updates the scores on the scoreboard to match the current house points standing.
    *
    * @return true if the operation was successful, false otherwise
    */
   public boolean updateScoreboard ()
   {
      if (isEnabled == false)
      {
         p.getLogger().warning("Tried to update scoreboard when houses are not enabled.");
         return false;
      }

      if (scoreboard == null)
      {
         createScoreboard();
      }

      Objective objective = scoreboard.getObjective(objectiveName);
      if (objective != null)
      {
         updateScoreboardScore(O2HouseType.GRYFFINDOR);
         updateScoreboardScore(O2HouseType.HUFFLEPUFF);
         updateScoreboardScore(O2HouseType.RAVENCLAW);
         updateScoreboardScore(O2HouseType.SLYTHERIN);

         return true;
      }

      p.getLogger().warning("updateScoreboard: house points objective not found.");

      return false;
   }

   private void updateScoreboardScore (O2HouseType house)
   {
      String houseName = getHouseName(house);
      Objective objective = scoreboard.getObjective(objectiveName);

      int points = 0;
      try
      {
         points = O2HousePointsMap.get(house);
      }
      catch (Exception e)
      {
         if (p.debug)
            p.getLogger().info("updateScoreboardScore: house points not found in points map, setting score to 0.");
      }

      try
      {
         Score score = objective.getScore(houseName);
         score.setScore(points);
      }
      catch (Exception e)
      {
         p.getLogger().warning("updateScoreboardScore: failed to update score for " + houseName);
      }
   }

   /**
    * Hide the house points scoreboard.
    *
    * @return true if the operation was successful, false otherwise
    */
   public boolean hideScoreboard ()
   {
      if (isEnabled == false)
      {
         p.getLogger().warning("Tried to hide scoreboard when houses are not enabled.");
         return false;
      }

      Objective objective = scoreboard.getObjective(objectiveName);

      if (objective.getDisplaySlot() != null)
         scoreboard.clearSlot(scoreboardSlot);

      return true;
   }

   /**
    * Show the house points scoreboard.
    *
    * @return true if the operation was successful, false otherwise
    */
   public boolean showScoreboard ()
   {
      if (isEnabled == false)
      {
         p.getLogger().warning("Tried to show scoreboard when houses are not enabled.");
         return false;
      }

      Objective objective = scoreboard.getObjective(objectiveName);

      if (objective != null)
         objective.setDisplaySlot(scoreboardSlot);

      return true;
   }

   /**
    * Set player chat color to match their house if they are sorted.
    *
    * @param player
    */
   public void setChatColor (Player player)
   {
      if (isSorted(player))
      {
         O2HouseType house = getHouse(player);
         ChatColor color = O2HouseColors.get(house);

         try
         {
            player.setDisplayName(color + player.getName());
         }
         catch (Exception e)
         {
            p.getLogger().warning("Exception setting " + player.getDisplayName() + " chat color.");
            if (p.debug)
               e.printStackTrace();
         }

         if (p.debug)
            p.getLogger().info("Setting " + player.getDisplayName() + " name color to " + color.toString());
      }
   }
}
