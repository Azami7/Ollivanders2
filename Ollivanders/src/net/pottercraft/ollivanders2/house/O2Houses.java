package net.pottercraft.ollivanders2.house;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.GsonDAO;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Team;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * "While you are here, your house will be something like your family within Hogwarts.  You will have classes with the
 * rest of your house, sleep in your house dormitory, and spend free time in your house common room."
 *
 * @author Azami7
 */
public class O2Houses
{
   private Ollivanders2 p;
   private Ollivanders2Common common;
   private Map<UUID, O2HouseType> O2HouseMap = new HashMap<>();
   private Map <O2HouseType, Team> O2HouseTeamMap = new HashMap<>();

   private Scoreboard scoreboard;
   private String objectiveName = "o2_hpoints";
   private String objectiveDisplayName = "House Points";

   private DisplaySlot scoreboardSlot = DisplaySlot.SIDEBAR;

   /**
    * Constructor.
    *
    * @param plugin the callback for the plugin
    */
   public O2Houses (@NotNull Ollivanders2 plugin)
   {
      p = plugin;
      common = new Ollivanders2Common(p);

      if (!Ollivanders2.useHouses)
         return;

      readHouseConfig();
      createScoreboard();
      initHousePoints();
      loadHouses();
      updateScoreboard();
      showScoreboard();
   }

   /**
    * Read house config options
    */
   private void readHouseConfig ()
   {
      //
      // house names
      //
      if (p.getConfig().isSet("gryffindorName"))
         O2HouseType.GRYFFINDOR.setName(p.getConfig().getString("gryffindorName"));

      if (p.getConfig().isSet("hufflepuffName"))
         O2HouseType.HUFFLEPUFF.setName(p.getConfig().getString("hufflepuffName"));

      if (p.getConfig().isSet("ravenclawName"))
         O2HouseType.RAVENCLAW.setName(p.getConfig().getString("ravenclawName"));

      if (p.getConfig().isSet("slytherinName"))
         O2HouseType.SLYTHERIN.setName(p.getConfig().getString("slytherinName"));

      //
      // house colors
      //
      if (p.getConfig().isSet("gryffindorColor"))
         O2HouseType.GRYFFINDOR.setColor(p.getConfig().getString("gryffindorColor"));

      if (p.getConfig().isSet("hufflepuffColor"))
         O2HouseType.HUFFLEPUFF.setColor(p.getConfig().getString("hufflepuffColor"));

      if (p.getConfig().isSet("ravenclawColor"))
         O2HouseType.RAVENCLAW.setColor(p.getConfig().getString("ravenclawColor"));

      if (p.getConfig().isSet("slytherinColor"))
         O2HouseType.SLYTHERIN.setColor(p.getConfig().getString("slytherinColor"));
   }

   /**
    * Initialize the house points map.
    */
   private void initHousePoints ()
   {
      for (O2HouseType houseType : O2HouseType.values())
      {
         setHousePoints(houseType, 0);
      }
   }

   /**
    * Get the house type by name.
    *
    * @param name the name of the house
    * @return the house type or null if the name is not valid.
    */
   @Nullable
   public O2HouseType getHouseType(String name)
   {
      if (name == null)
      {
         if (Ollivanders2.debug)
         {
            p.getLogger().info("getHouseType: null house passed in");
         }

         return null;
      }

      name = name.trim();

      if (Ollivanders2.debug)
         p.getLogger().info("getHouseType: getting type for " + name);

      for (O2HouseType houseType : O2HouseType.values())
      {
         if (name.equalsIgnoreCase(houseType.getName()))
            return houseType;
      }

      return null;
   }

   /**
    * Get all the house names.
    *
    * @return all house names.
    */
   @NotNull
   public ArrayList<String> getAllHouseNames ()
   {
      ArrayList<String> houseNames = new ArrayList<>();

      for (O2HouseType houseType : O2HouseType.values())
      {
         houseNames.add(houseType.getName());
      }

      return houseNames;
   }

   /**
    * Load the house information saved to disk.
    */
   private void loadHouses()
   {
      GsonDAO gsonLayer = new GsonDAO(p);
      Map <UUID, O2HouseType> houses = gsonLayer.readHouses();
      if (houses != null)
      {
         O2HouseMap = houses;
      }

      Map<O2HouseType, Integer> housePoints = gsonLayer.readHousePoints();
      if (housePoints != null)
      {
         for (Entry<O2HouseType, Integer> e : housePoints.entrySet())
         {
            O2HouseType houseType = e.getKey();

            houseType.setScore(e.getValue());
            p.getLogger().info(e.getKey().getName() + " : " + e.getValue());
         }
      }
   }

   /**
    * Save the house information to disk.
    */
   public void saveHouses()
   {
      // write house data out as JSON
      GsonDAO gsonLayer = new GsonDAO(p);
      gsonLayer.writeHouses(O2HouseMap);

      Map <O2HouseType, Integer> housePoints = new HashMap<>();
      for (O2HouseType houseType : O2HouseType.values())
      {
         housePoints.put(houseType, houseType.getScore());
      }

      gsonLayer.writeHousePoints(housePoints);
   }

   /**
    * Sort a player in to a house.
    *
    * @param player the player to sort
    * @param houseType the house to sort them in to
    * @return true if the player is successfully sorted, false otherwise.
    */
   public boolean sort (Player player, O2HouseType houseType)
   {
      //make sure player is not already sorted
      if (isSorted(player))
         return false;

      O2HouseMap.put(player.getUniqueId(), houseType);
      addPlayerToHouseTeam(player);

      if (Ollivanders2.displayMessageOnSort)
      {
         String title = houseType.getChatColorCode() + player.getName();
         String subtitle = houseType.getChatColorCode() + "let it be " + houseType.getName();
         common.sendTitleMessage(title, subtitle, common.getAllOnlineSortedPlayers());
      }

      return true;
   }

   /**
    * Remove a player from any house, making them unsorted. This should only be used on a player reset.
    *
    * @param player the player to unsort
    */
   public void unsort (Player player)
   {
      if (isSorted(player))
      {
         O2HouseType houseType = O2HouseMap.get(player.getUniqueId());
         updateTeam(player, houseType, false);

         O2HouseMap.remove(player.getUniqueId());
      }
   }

   /**
    * Determines if a player has been sorted already or not.
    *
    * @param player the player to check
    * @return true if the player has been sorted, false otherwise.
    */
   public boolean isSorted (Player player)
   {
      return isSorted(player.getUniqueId());
   }

   /**
    * Determines if a player has been sorted already or not.
    *
    * @param pid the uuid of the player to check
    * @return true if the player has been sorted, false otherwise.
    */
   public boolean isSorted (UUID pid)
   {
      return O2HouseMap.containsKey(pid);
   }

   /**
    * Force sets the players house to a house.  This will happen even if a player has been previously sorted.  This
    * is a separate function so that sort() is not accidentally used once a player has been sorted.
    *
    * @param player the player to sort
    * @param houseType the house to add them to
    */
   public void forceSetHouse(Player player, O2HouseType houseType)
   {
      unsort(player);
      sort(player, houseType);
   }

   /**
    * Get the house a player is sorted in to.
    *
    * @param player the player to get the house for
    * @return the House the player is sorted in to, null otherwise.
    */
   public O2HouseType getHouse (Player player)
   {
      return getHouse(player.getUniqueId());
   }

   /**
    * Get the house a player is sorted in to.
    *
    * @param pid the uuid of the player to search for
    * @return the House the player is sorted in to, null otherwise.
    */
   public O2HouseType getHouse (UUID pid)
   {
      O2HouseType houseType = null;

      if (O2HouseMap.containsKey(pid))
      {
         try
         {
            houseType = O2HouseMap.get(pid);
         }
         catch (Exception e)
         {
            p.getLogger().warning("Failure retrieving player from O2HouseMap.");
            if (Ollivanders2.debug)
               e.printStackTrace();
         }
      }

      return houseType;
   }

   /**
    * Get a list of the players sorted in to a house.
    *
    * @param houseType the house to get the members of
    * @return the names of all members of the specified house.
    */
   public ArrayList<String> getHouseMembers (O2HouseType houseType)
   {
      ArrayList<String> houseMembers = new ArrayList<>();
      Server server = p.getServer();

      for(Entry<UUID, O2HouseType> entry: O2HouseMap.entrySet())
      {
         if (entry.getValue() == houseType)
         {
            UUID playerID = entry.getKey();
            String playerName = server.getOfflinePlayer(playerID).getName();
            houseMembers.add(playerName);
         }
      }

      return houseMembers;
   }

   /**
    * Sets the points for a house.
    *
    * @param houseType the house to add points to
    * @param points the point value to set for this house
    * @return true if the operation was successful, false if house was not found
    */
   public synchronized boolean setHousePoints (O2HouseType houseType, int points)
   {
      houseType.setScore(points);

      return updateScoreboard();
   }

   /**
    * Resets all house points to 0.
    */
   public boolean resetHousePoints ()
   {
      initHousePoints();

      return updateScoreboard();
   }

   /**
    * Resets houses completely.
    */
   public boolean reset()
   {
      p.getLogger().info("Resetting houses...");

      O2HouseMap.clear();

      initHousePoints();
      return updateScoreboard();
   }

   /**
    * Add points to a specific house.
    *
    * @param houseType the house to add points to
    * @param points the amount of points to add
    * @return true if the operation was successful, false if house was not found
    */
   public boolean addHousePoints (O2HouseType houseType, int points)
   {
      int pts = points + houseType.getScore();

      return setHousePoints(houseType, pts);
   }

   /**
    * Remove points from a specific house.
    *
    * @param houseType the house to subtract points from
    * @param points the amount of points to subtract, if this is greater than the total points, points will be set to 0
    * @return true if the operation was successful, false if house was not found
    */
   public boolean subtractHousePoints (O2HouseType houseType, int points)
   {
      int pts = 0;

      if (points < houseType.getScore())
      {
         pts = houseType.getScore() - points;
      }

      return setHousePoints(houseType, pts);
   }

   /**
    * Creates the house points scoreboard.
    */
   private void createScoreboard ()
   {
      if (!Ollivanders2.useHouses)
      {
         // do not allow if houses is not enabled
         p.getLogger().warning("Attempted to create scoreboard when houses is not enabled.");
         return;
      }

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

      scoreboard.registerNewObjective(objectiveName, "dummy", "House Points");
      Objective objective = scoreboard.getObjective(objectiveName);
      objective.setDisplayName(objectiveDisplayName);
      objective.setDisplaySlot(scoreboardSlot);

      // register houses on scoreboard
      for (O2HouseType houseType : O2HouseType.values())
      {
         registerHouseTeam(houseType);
      }

      updateScoreboard();
   }

   /**
    * Register a team with the scoreboard.
    *
    * @param houseType the house to register
    */
   private void registerHouseTeam (O2HouseType houseType)
   {
      String houseName = houseType.getName();

      Team team = scoreboard.getTeam(houseName);

      if (team == null)
      {
         team = scoreboard.registerNewTeam(houseName);
         if (Ollivanders2.debug)
            p.getLogger().info("Added team " + houseName + " to scoreboard.");
      }
      else
      {
         if (Ollivanders2.debug)
            p.getLogger().info("Team " + houseName + " already registered.");
      }

      team.setColor(houseType.getChatColorCode());
      team.setAllowFriendlyFire(true);
      team.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);

      O2HouseTeamMap.put(houseType, team);
   }

   /**
    * Updates the scores on the scoreboard to match the current house points standing.
    *
    * @return true if the operation was successful, false otherwise
    */
   private synchronized boolean updateScoreboard ()
   {
      if (!Ollivanders2.useHouses)
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
         for (O2HouseType houseType : O2HouseType.values())
         {
            updateScoreboardScore(houseType);
         }

         return true;
      }

      p.getLogger().warning("updateScoreboard: house points objective not found.");

      return false;
   }

   /**
    * Update the scoreboard for a specific house
    *
    * @param houseType the house to update
    */
   private void updateScoreboardScore (O2HouseType houseType)
   {
      Objective objective = scoreboard.getObjective(objectiveName);
      Team team = O2HouseTeamMap.get(houseType);

      if (team == null)
         return;

      try
      {
         Score score = objective.getScore(houseType.getName());
         score.setScore(houseType.getScore());
      }
      catch (Exception e)
      {
         p.getLogger().warning("updateScoreboardScore: failed to update score for " + houseType.getName());
         if (Ollivanders2.debug)
            e.printStackTrace();
      }
   }

   /**
    * Hide the house points scoreboard.
    *
    * @return true if the operation was successful, false otherwise
    */
   private boolean hideScoreboard ()
   {
      if (!Ollivanders2.useHouses)
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
    */
   private void showScoreboard ()
   {
      if (!Ollivanders2.useHouses)
      {
         p.getLogger().warning("Tried to show scoreboard when houses are not enabled.");
         return;
      }

      Objective objective = scoreboard.getObjective(objectiveName);

      if (objective != null)
         objective.setDisplaySlot(scoreboardSlot);
   }

   /**
    * Update player team membership - either add or remove.
    *
    * @param player the player to update
    * @param houseType the team to update
    * @param add true if an add action, false if it is a remove
    */
   private synchronized void updateTeam (Player player, O2HouseType houseType, boolean add)
   {
      String name = player.getName();
      String displayName = player.getDisplayName();
      Team team = O2HouseTeamMap.get(houseType);

      if (team == null)
      {
         if (Ollivanders2.debug)
         {
            p.getLogger().info("Team " + houseType + " does not exist.");
            return;
         }
      }

      if (add)
      {
         team.addEntry(name);
         player.setDisplayName(houseType.getColorPrefix() + name);
      }
      else
      {
         team.removeEntry(name);
         if (displayName.startsWith("§"));
         {
            // we have set a color on their display name, change it back
            player.setDisplayName(name);
         }
      }
   }

   /**
    * Add a player to their house team.
    *
    * @param player the player to add
    */
   public void addPlayerToHouseTeam (Player player)
   {
      UUID pid = player.getUniqueId();
      if (O2HouseMap.containsKey(pid))
      {
         updateTeam(player, O2HouseMap.get(pid), true);
      }
   }
}