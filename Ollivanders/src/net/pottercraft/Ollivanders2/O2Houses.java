package net.pottercraft.Ollivanders2;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

/**
 * "While you are here, your Houses will be something like your family within Hogwarts.  You will have classes with the
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
   private Map <String, O2HouseType> O2HouseMap = new HashMap<>();
   private Map <O2HouseType, Integer> O2HousePointsMap = new HashMap<>();
   private boolean isEnabled = true;

   private Scoreboard scoreboard;
   String objectiveName = "o2_hpoints";
   String objectiveDisplayName = "House Points";
   String houseMapFile = "plugins/Ollivanders2/O2HouseMap.bin";
   String housePointsMapFile = "plugins/Ollivanders2/O2HousePointsMap.bin";

   DisplaySlot scoreboardSlot = DisplaySlot.SIDEBAR;

   public O2Houses (Ollivanders2 plugin)
   {
      p = plugin;

      if (p.getConfig().getBoolean("houses"))
      {
         initHousePoints();
         createScoreboard();
      }
      else
      {
         p.getLogger().info("Houses not enabled.");
         isEnabled = false;
      }
   }

   /**
    * Initialize the house points map.
    */
   private void initHousePoints ()
   {
      O2HousePointsMap.put(O2HouseType.GRYFFINDOR, 0);
      O2HousePointsMap.put(O2HouseType.HUFFLEPUFF, 0);
      O2HousePointsMap.put(O2HouseType.RAVENCLAW, 0);
      O2HousePointsMap.put(O2HouseType.SLYTHERIN, 0);
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
      else // house == O2HouseType.SLYTHERIN
         return "Slytherin";
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
      try
      {
         O2HouseMap = (HashMap<String, O2HouseType>) Ollivanders2.SLAPI.load(houseMapFile);
         p.getLogger().finest("Loaded " + houseMapFile);
      }
      catch (Exception e)
      {
         p.getLogger().warning("Failed to load " + houseMapFile);
      }

      try
      {
         O2HousePointsMap = (HashMap<O2HouseType, Integer>) Ollivanders2.SLAPI.load(housePointsMapFile);
         p.getLogger().finest("Loaded " + housePointsMapFile);
      }
      catch (Exception e)
      {
         p.getLogger().warning("Failed to load " + housePointsMapFile);
      }

      showScoreboard();
   }

   /**
    * Save the house information to disk.
    */
   public void saveHouses()
   {
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

      O2HouseMap.put(player.getPlayerListName(), house);
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
      if (O2HouseMap.containsKey(player.getPlayerListName()))
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
         O2HouseMap.replace(player.getPlayerListName(), house);
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

      if (O2HouseMap.containsKey(player.getPlayerListName()))
      {
         try
         {
            house = O2HouseMap.get(player.getPlayerListName());
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failure retrieving player " + player.getPlayerListName() + " from O2HouseMap.");
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

      for(Entry<String, O2HouseType> entry: O2HouseMap.entrySet())
      {
         if (entry.getValue() == house)
         {
            houseMembers.add(entry.getKey());
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

      points.put(O2HouseType.HUFFLEPUFF, O2HousePointsMap.get(O2HouseType.HUFFLEPUFF));
      points.put(O2HouseType.GRYFFINDOR, O2HousePointsMap.get(O2HouseType.GRYFFINDOR));
      points.put(O2HouseType.RAVENCLAW, O2HousePointsMap.get(O2HouseType.RAVENCLAW));
      points.put(O2HouseType.SLYTHERIN, O2HousePointsMap.get(O2HouseType.SLYTHERIN));

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
      }
      else
      {
         O2HousePointsMap.put(house, points);
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
         p.getLogger().warning("Attempted to create scoreboard when houses is not enabled");
         return false;
      }

      if (scoreboard != null)
      {
         // do not allow create if the scoreboard already exists
         return false;
      }

      scoreboard = p.getServer().getScoreboardManager().getMainScoreboard();
      scoreboard.clearSlot(scoreboardSlot);

      p.getLogger().info("Created scoreboard...");

      // if there was a previous house points objective, remove it
      if (scoreboard.getObjective(objectiveName) != null)
      {
         scoreboard.getObjective(objectiveName).unregister();
         p.getLogger().info("Unregistered previous objective...");
      }

      scoreboard.registerNewObjective(objectiveName, "dummy");
      Objective objective = scoreboard.getObjective(objectiveName);
      objective.setDisplayName(objectiveDisplayName);

      registerHouseTeam(O2HouseType.HUFFLEPUFF);
      registerHouseTeam(O2HouseType.GRYFFINDOR);
      registerHouseTeam(O2HouseType.RAVENCLAW);
      registerHouseTeam(O2HouseType.SLYTHERIN);

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

      if (objective.getDisplaySlot() == null)
         objective.setDisplaySlot(scoreboardSlot);

      return true;
   }
}
