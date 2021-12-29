package net.pottercraft.ollivanders2.house;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.GsonDAO;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

/**
 * "While you are here, your house will be something like your family within Hogwarts.  You will have classes with the
 * rest of your house, sleep in your house dormitory, and spend free time in your house common room."
 *
 * @author Azami7
 */
public class O2Houses
{
   private final Ollivanders2 p;
   private final Ollivanders2Common common;

   public static boolean useHouses = false;
   public static boolean displayMessageOnSort = false;

   private Map<UUID, O2HouseType> O2HouseMap = new HashMap<>();
   private final Map <O2HouseType, Team> O2HouseTeamMap = new HashMap<>();

   private Scoreboard scoreboard;
   private final String objectiveName = "o2_hpoints";
   private final String objectiveDisplayName = "House Points";

   private final DisplaySlot scoreboardSlot = DisplaySlot.SIDEBAR;

   /**
    * Constructor.
    *
    * @param plugin the callback for the plugin
    */
   public O2Houses (@NotNull Ollivanders2 plugin)
   {
      p = plugin;
      common = new Ollivanders2Common(p);
   }

   /**
    * Set up houses on enable
    */
   public void onEnable ()
   {
      //
      // houses
      //
      useHouses = p.getConfig().getBoolean("houses");

      if (useHouses)
         p.getLogger().info("Enabling school houses.");
      else
         return;

      displayMessageOnSort = p.getConfig().getBoolean("displayMessageOnSort");

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
      String s;
      if (p.getConfig().isSet("gryffindorName"))
      {
         s = p.getConfig().getString("gryffindorName");
         if (s != null && s.length() > 0)
            O2HouseType.GRYFFINDOR.setName(s);
      }

      if (p.getConfig().isSet("hufflepuffName"))
      {
         s = p.getConfig().getString("hufflepuffName");
         if (s != null && s.length() > 0)
            O2HouseType.HUFFLEPUFF.setName(s);
      }

      if (p.getConfig().isSet("ravenclawName"))
      {
         s = p.getConfig().getString("ravenclawName");
         if (s != null && s.length() > 0)
            O2HouseType.RAVENCLAW.setName(s);
      }

      if (p.getConfig().isSet("slytherinName"))
      {
         s = p.getConfig().getString("slytherinName");
         if (s != null && s.length() > 0)
            O2HouseType.SLYTHERIN.setName(s);
      }

      //
      // house colors
      //
      if (p.getConfig().isSet("gryffindorColor"))
      {
         s = p.getConfig().getString("gryffindorColor");
         if (s != null && s.length() > 0)
            O2HouseType.GRYFFINDOR.setColor(s);
      }

      if (p.getConfig().isSet("hufflepuffColor"))
      {
         s = p.getConfig().getString("hufflepuffColor");
         if (s != null && s.length() > 0)
            O2HouseType.HUFFLEPUFF.setColor(s);
      }

      if (p.getConfig().isSet("ravenclawColor"))
      {
         s = p.getConfig().getString("ravenclawColor");
         if (s != null && s.length() > 0)
            O2HouseType.RAVENCLAW.setColor(s);
      }

      if (p.getConfig().isSet("slytherinColor"))
      {
         s = p.getConfig().getString("slytherinColor");
         if (s != null && s.length() > 0)
            O2HouseType.SLYTHERIN.setColor(s);
      }
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
   public O2HouseType getHouseType (@Nullable String name)
   {
      if (name == null)
      {
         common.printDebugMessage("getHouseType: null house passed in", null, null, false);
         return null;
      }

      name = name.trim();
      common.printDebugMessage("getHouseType: getting type for " + name, null, null, false);

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
      GsonDAO gsonLayer = new GsonDAO();
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
      if (!useHouses)
         return;

      // write house data out as JSON
      GsonDAO gsonLayer = new GsonDAO();
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
   public boolean sort (@NotNull Player player, @NotNull O2HouseType houseType)
   {
      //make sure player is not already sorted
      if (isSorted(player))
         return false;

      O2HouseMap.put(player.getUniqueId(), houseType);
      addPlayerToHouseTeam(player);

      if (displayMessageOnSort)
      {
         String title = houseType.getChatColorCode() + player.getName();
         String subtitle = houseType.getChatColorCode() + "better be " + houseType.getName();
         common.sendTitleMessage(title, subtitle, common.getAllOnlineSortedPlayers());
      }

      return true;
   }

   /**
    * Remove a player from any house, making them unsorted. This should only be used on a player reset.
    *
    * @param player the player to unsort
    */
   public void unsort (@NotNull Player player)
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
   public boolean isSorted (@NotNull Player player)
   {
      return isSorted(player.getUniqueId());
   }

   /**
    * Determines if a player has been sorted already or not.
    *
    * @param pid the uuid of the player to check
    * @return true if the player has been sorted, false otherwise.
    */
   public boolean isSorted (@NotNull UUID pid)
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
   public void forceSetHouse(@NotNull Player player, @NotNull O2HouseType houseType)
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
   @Nullable
   public O2HouseType getHouse (@NotNull Player player)
   {
      return getHouse(player.getUniqueId());
   }

   /**
    * Get the house a player is sorted in to.
    *
    * @param pid the uuid of the player to search for
    * @return the House the player is sorted in to, null otherwise.
    */
   @Nullable
   public O2HouseType getHouse (@NotNull UUID pid)
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
            common.printDebugMessage("Failure retrieving player from O2HouseMap.", e, null, false);
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
   @NotNull
   public ArrayList<String> getHouseMembers (@NotNull O2HouseType houseType)
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
   public synchronized boolean setHousePoints (@NotNull O2HouseType houseType, int points)
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
   public boolean addHousePoints (@NotNull O2HouseType houseType, int points)
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
   public boolean subtractHousePoints (@NotNull O2HouseType houseType, int points)
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
      if (!useHouses)
      {
         // do not allow if houses is not enabled
         common.printDebugMessage("Attempted to create scoreboard when houses is not enabled.", null, null, false);
         return;
      }

      ScoreboardManager scoreboardManager = p.getServer().getScoreboardManager();
      if (scoreboardManager == null)
         return;

      scoreboard = p.getServer().getScoreboardManager().getMainScoreboard();

      common.printDebugMessage("Created scoreboard...", null, null, false);

      // if there was a previous house points objective, remove it
      Objective objective = scoreboard.getObjective(objectiveName);

      if (objective != null)
      {
         objective.unregister();
         common.printDebugMessage("Unregistered previous house points objective...", null, null, false);
      }

      // if there is another objective on the slot we want, remove it
      objective = scoreboard.getObjective(scoreboardSlot);
      if (objective != null)
      {
         objective.unregister();
         common.printDebugMessage("Unregistered previous scoreboard objective...", null, null, false);
      }

      scoreboard.registerNewObjective(objectiveName, "dummy", "House Points");
      objective = scoreboard.getObjective(objectiveName);
      if (objective == null)
      {
         common.printDebugMessage("createScoreboard: Failed to create scoreboard objective", null, null, false);
         return;
      }

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
   private void registerHouseTeam(@NotNull O2HouseType houseType)
   {
      String houseName = houseType.getName();

      Team team = scoreboard.getTeam(houseName);

      if (team == null)
      {
         team = scoreboard.registerNewTeam(houseName);
         common.printDebugMessage("Added team " + houseName + " to scoreboard.", null, null, false);
      }
      else
         common.printDebugMessage("Team " + houseName + " already registered.", null, null, false);

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
      if (!useHouses)
      {
         common.printDebugMessage("Tried to update scoreboard when houses are not enabled.", null, null, false);
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

      common.printDebugMessage("updateScoreboard: house points objective not found.", null, null, false);
      return false;
   }

   /**
    * Update the scoreboard for a specific house
    *
    * @param houseType the house to update
    */
   private void updateScoreboardScore(@NotNull O2HouseType houseType)
   {
      Objective objective = scoreboard.getObjective(objectiveName);
      if (objective == null)
         return;

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
         common.printDebugMessage("updateScoreboardScore: failed to update score for " + houseType.getName(), e, null, false);
      }
   }

   /**
    * Hide the house points scoreboard.
    *
    * @return true if the operation was successful, false otherwise
    */
   private boolean hideScoreboard()
   {
      if (!useHouses)
      {
         common.printDebugMessage("Tried to hide scoreboard when houses are not enabled.", null, null, false);
         return false;
      }

      Objective objective = scoreboard.getObjective(objectiveName);
      if (objective == null)
         return false;

      if (objective.getDisplaySlot() != null)
         scoreboard.clearSlot(scoreboardSlot);

      return true;
   }

   /**
    * Show the house points scoreboard.
    */
   private void showScoreboard ()
   {
      if (!useHouses)
      {
         common.printDebugMessage("Tried to show scoreboard when houses are not enabled.", null, null, false);
         return;
      }

      Objective objective = scoreboard.getObjective(objectiveName);

      if (objective != null)
         objective.setDisplaySlot(scoreboardSlot);
   }

   /**
    * Update player team membership - either add or remove.
    *
    * @param player    the player to update
    * @param houseType the team to update
    * @param add       true if an add action, false if it is a remove
    */
   private synchronized void updateTeam(@NotNull Player player, @NotNull O2HouseType houseType, boolean add)
   {
      String name = player.getName();
      String displayName = player.getDisplayName();
      Team team = O2HouseTeamMap.get(houseType);

      if (team == null)
      {
         common.printDebugMessage("Team " + houseType + " does not exist.", null, null, false);
         return;
      }

      if (add)
      {
         team.addEntry(name);
         player.setDisplayName(houseType.getColorPrefix() + name);
      }
      else
      {
         team.removeEntry(name);
         if (displayName.startsWith("§"))
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
   public void addPlayerToHouseTeam(@NotNull Player player)
   {
      UUID pid = player.getUniqueId();
      if (O2HouseMap.containsKey(pid))
      {
         updateTeam(player, O2HouseMap.get(pid), true);
      }
   }
}