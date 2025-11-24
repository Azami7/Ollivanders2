package net.pottercraft.ollivanders2.house;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.GsonDAO;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.house.events.OllivandersPlayerSortedEvent;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * "While you are here, your house will be something like your family within Hogwarts. You will have classes with the
 * rest of your house, sleep in your house dormitory, and spend free time in your house common room."
 *
 * <p>O2Houses manages the Hogwarts house system including sorting players, tracking house membership, managing house
 * points, and displaying a scoreboard with house standings. Houses are optional and configurable, allowing servers to
 * customize house names and chat colors. The class persists house data to disk and fires events when players are sorted.</p>
 *
 * <p>House Features:</p>
 * <ul>
 * <li>Player sorting into four Hogwarts houses</li>
 * <li>House membership persistence across server restarts</li>
 * <li>House points system with scoreboard display</li>
 * <li>Customizable house names and colors via configuration</li>
 * <li>Team-based player grouping with colored display names</li>
 * <li>OllivandersPlayerSortedEvent fire on successful sorting</li>
 * </ul>
 *
 * @author Azami7
 * @see O2HouseType for the house type enumeration
 * @see OllivandersPlayerSortedEvent for the event fired on sorting
 * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Houses">House Configuration Wiki</a>
 * @see <a href="https://harrypotter.fandom.com/wiki/Hogwarts_Houses">Hogwarts Houses - Harry Potter Wiki</a>
 */
public class O2Houses {
    /**
     * Reference to the plugin for accessing configuration, logging, and server API.
     */
    private final Ollivanders2 p;

    /**
     * Common utility functions for the plugin.
     */
    private final Ollivanders2Common common;

    /**
     * Whether houses are enabled.
     *
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#houses">Houses Configuration Wiki</a>
     */
    public static boolean useHouses = false;

    /**
     * Whether to display sorting messages on broadcast to all players.
     *
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#sorting-announcements">Sorting Announcements Configuration Wiki</a>
     */
    public static boolean displayMessageOnSort = false;

    /**
     * A map of all house memberships for players on- and offline
     */
    private Map<UUID, O2HouseType> O2HouseMap = new HashMap<>();

    /**
     * A map of all the house teams for scoreboard
     */
    private final Map<O2HouseType, Team> O2HouseTeamMap = new HashMap<>();

    /**
     * House points display using scoreboard.
     *
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Houses#points">House Points Configuration Wiki</a>
     */
    private Scoreboard scoreboard;

    /**
     * House points scoreboard objective
     */
    private final String objectiveName = "o2_hpoints";

    /**
     * House points scoreboard label
     */
    private final String scoreboardDisplayName = "House Points";

    /**
     * The scoreboard objective display name for house points
     */
    private final String objectiveDisplayName = "House Points";

    /**
     * Where to display the house points scoreboard
     */
    private final DisplaySlot scoreboardSlot = DisplaySlot.SIDEBAR;

    /**
     * Constructor for the house management system.
     *
     * <p>Initializes the O2Houses manager with a reference to the Ollivanders2 plugin and creates common utility instances
     * for logging and debugging.</p>
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    public O2Houses(@NotNull Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(p);
    }

    /**
     * Initialize and enable the house system on plugin startup.
     *
     * <p>Reads house configuration options, creates the scoreboard infrastructure, initializes house points, loads
     * persisted house data from disk, and displays the house points scoreboard to players. If houses are disabled in
     * the configuration, this method returns early without performing initialization.</p>
     */
    public void onEnable() {
        //
        // houses
        //
        useHouses = p.getConfig().getBoolean("houses");

        if (useHouses)
            p.getLogger().info("Enabling school houses.");
        else {
            p.getLogger().info("Disabling school houses.");
            return;
        }

        displayMessageOnSort = p.getConfig().getBoolean("displayMessageOnSort");

        readHouseConfig();
        createScoreboard();
        initHousePoints();
        loadHouses();
        updateScoreboard();
        showScoreboard();
    }

    /**
     * Read customizable house configuration from the plugin config file.
     *
     * <p>Loads custom names and chat colors for each house from the plugin configuration. If custom names or colors are
     * set in the configuration and are not empty, they override the default values for Gryffindor, Hufflepuff, Ravenclaw,
     * and Slytherin houses.</p>
     */
    private void readHouseConfig() {
        //
        // house names
        //
        String s;
        if (p.getConfig().isSet("gryffindorName")) {
            s = p.getConfig().getString("gryffindorName");
            if (s != null && !s.isEmpty())
                O2HouseType.GRYFFINDOR.setName(s);
        }

        if (p.getConfig().isSet("hufflepuffName")) {
            s = p.getConfig().getString("hufflepuffName");
            if (s != null && !s.isEmpty())
                O2HouseType.HUFFLEPUFF.setName(s);
        }

        if (p.getConfig().isSet("ravenclawName")) {
            s = p.getConfig().getString("ravenclawName");
            if (s != null && !s.isEmpty())
                O2HouseType.RAVENCLAW.setName(s);
        }

        if (p.getConfig().isSet("slytherinName")) {
            s = p.getConfig().getString("slytherinName");
            if (s != null && !s.isEmpty())
                O2HouseType.SLYTHERIN.setName(s);
        }

        //
        // house colors
        //
        if (p.getConfig().isSet("gryffindorColor")) {
            s = p.getConfig().getString("gryffindorColor");
            if (s != null && !s.isEmpty())
                O2HouseType.GRYFFINDOR.setColor(s);
        }

        if (p.getConfig().isSet("hufflepuffColor")) {
            s = p.getConfig().getString("hufflepuffColor");
            if (s != null && !s.isEmpty())
                O2HouseType.HUFFLEPUFF.setColor(s);
        }

        if (p.getConfig().isSet("ravenclawColor")) {
            s = p.getConfig().getString("ravenclawColor");
            if (s != null && !s.isEmpty())
                O2HouseType.RAVENCLAW.setColor(s);
        }

        if (p.getConfig().isSet("slytherinColor")) {
            s = p.getConfig().getString("slytherinColor");
            if (s != null && !s.isEmpty())
                O2HouseType.SLYTHERIN.setColor(s);
        }
    }

    /**
     * Initialize all house points to zero.
     *
     * <p>Sets the initial score for each house type to 0 points. This is called during startup and
     * when resetting the house system.</p>
     */
    private void initHousePoints() {
        for (O2HouseType houseType : O2HouseType.values()) {
            setHousePoints(houseType, 0);
        }
    }

    /**
     * Get the house type by name.
     *
     * <p>Performs case-insensitive matching against all house types. The input name is automatically
     * trimmed of whitespace before comparison. Returns null if no matching house is found.</p>
     *
     * @param name the name of the house (case-insensitive, spaces are trimmed)
     * @return the matching house type, or null if no match is found
     */
    @Nullable
    public O2HouseType getHouseType(@Nullable String name) {
        if (name == null) {
            common.printDebugMessage("getHouseType: null house passed in", null, null, false);
            return null;
        }

        name = name.trim();
        common.printDebugMessage("getHouseType: getting type for " + name, null, null, false);

        for (O2HouseType houseType : O2HouseType.values()) {
            if (name.equalsIgnoreCase(houseType.getName()))
                return houseType;
        }

        return null;
    }

    /**
     * Get all the house names.
     *
     * <p>Returns a fresh list of all house names. The list reflects the current names of each house type,
     * which may have been customized via configuration. This method returns a new list each time it is called.</p>
     *
     * @return a new ArrayList containing all house names in order
     */
    @NotNull
    public ArrayList<String> getAllHouseNames() {
        ArrayList<String> houseNames = new ArrayList<>();

        for (O2HouseType houseType : O2HouseType.values()) {
            houseNames.add(houseType.getName());
        }

        return houseNames;
    }

    /**
     * Load all persisted house data from disk.
     *
     * <p>Reads player-to-house mappings and house point scores from JSON files via GsonDAO. Both the
     * player house assignments and the current house points are restored from disk. If either file does
     * not exist or is empty, that data is skipped.</p>
     */
    private void loadHouses() {
        GsonDAO gsonLayer = new GsonDAO();
        Map<UUID, O2HouseType> houses = gsonLayer.readHouses();
        if (houses != null) {
            O2HouseMap = houses;
        }

        Map<O2HouseType, Integer> housePoints = gsonLayer.readHousePoints();
        if (housePoints != null) {
            for (Entry<O2HouseType, Integer> e : housePoints.entrySet()) {
                O2HouseType houseType = e.getKey();

                houseType.setScore(e.getValue());
                p.getLogger().info(e.getKey().getName() + " : " + e.getValue());
            }
        }
    }

    /**
     * Cleanup when the plugin disables.
     *
     * <p>Called when the Ollivanders2 plugin is being shut down. The O2Houses manager persists all house-related
     * data to disk before the plugin terminates. This ensures that house memberships and point scores are preserved
     * across server restarts.</p>
     *
     * <p>Saved Data:</p>
     * <ul>
     * <li>Player-to-house mappings: which players are sorted into which houses</li>
     * <li>House point scores: accumulated points for each of the four houses</li>
     * <li>All data is persisted as JSON files via GsonDAO</li>
     * </ul>
     *
     * @see #saveHouses() for the house data persistence implementation
     * @see #loadHouses() for restoring house data on plugin startup
     */
    public void onDisable() {
        p.getLogger().info("Saving house data.");

        saveHouses();
    }

    /**
     * Save all house data to disk as JSON files.
     *
     * <p>Persists the current player-to-house mappings and house point scores to disk via GsonDAO.
     * This method writes two JSON files: one containing player house assignments and one containing
     * house points. Does nothing if houses are disabled via configuration.</p>
     */
    public void saveHouses() {
        if (!useHouses)
            return;

        // write house data out as JSON
        GsonDAO gsonLayer = new GsonDAO();
        gsonLayer.writeHouses(O2HouseMap);

        Map<O2HouseType, Integer> housePoints = new HashMap<>();
        for (O2HouseType houseType : O2HouseType.values()) {
            housePoints.put(houseType, houseType.getScore());
        }

        gsonLayer.writeHousePoints(housePoints);
    }

    /**
     * Sort a player into a house.
     *
     * <p>Assigns the player to the specified house if they are not already sorted. Adds the player
     * to the house team with house-colored display name, broadcasts a title message to all sorted
     * players (if configured), and fires an OllivandersPlayerSortedEvent. Returns false if the
     * player is already sorted.</p>
     *
     * @param player    the player to sort
     * @param houseType the house to sort them into
     * @return true if the player is successfully sorted, false if already sorted
     */
    public boolean sort(@NotNull Player player, @NotNull O2HouseType houseType) {
        //make sure player is not already sorted
        if (isSorted(player)) {
            common.printDebugMessage("O2Houses.sort(): " + player.getName() + " is already sorted", null, null, false);
            return false;
        }

        O2HouseMap.put(player.getUniqueId(), houseType);
        addPlayerToHouseTeam(player);

        common.printDebugMessage("O2Houses.sort(): " + player.getName() + " sorted to " + houseType.getName(), null, null, false);

        // display sort message
        if (displayMessageOnSort) {
            String title = houseType.getChatColorCode() + player.getName();
            String subtitle = houseType.getChatColorCode() + "better be " + houseType.getName();
            Ollivanders2Common.sendTitleMessage(title, subtitle, Ollivanders2API.playerCommon.getAllOnlineSortedPlayers());
        }

        // throw the sort event
        OllivandersPlayerSortedEvent event = new OllivandersPlayerSortedEvent(player);
        p.getServer().getPluginManager().callEvent(event);
        common.printDebugMessage("O2Houses.sort(): Fired OllivandersPlayerSortedEvent", null, null, false);

        return true;
    }

    /**
     * Remove a player from their house, making them unsorted.
     *
     * <p>Removes the player from their current house team and the house map. This should only be used
     * on a player reset or when explicitly removing a player from the house system. If the player is
     * not currently sorted, this method does nothing.</p>
     *
     * @param player the player to unsort
     */
    public void unsort(@NotNull Player player) {
        if (isSorted(player)) {
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
    public boolean isSorted(@NotNull Player player) {
        return isSorted(player.getUniqueId());
    }

    /**
     * Determines if a player has been sorted already or not.
     *
     * @param pid the uuid of the player to check
     * @return true if the player has been sorted, false otherwise.
     */
    public boolean isSorted(@NotNull UUID pid) {
        return O2HouseMap.containsKey(pid);
    }

    /**
     * Force sets the players house to a house.
     *
     * <p>This will happen even if a player has been previously sorted. This is a separate function so that sort() is not
     * accidentally used once a player has been sorted.</p>
     *
     * @param player    the player to sort
     * @param houseType the house to add them to
     */
    public void forceSetHouse(@NotNull Player player, @NotNull O2HouseType houseType) {
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
    public O2HouseType getHouse(@NotNull Player player) {
        return getHouse(player.getUniqueId());
    }

    /**
     * Get the house a player is sorted in to.
     *
     * @param pid the uuid of the player to search for
     * @return the House the player is sorted in to, null otherwise.
     */
    @Nullable
    public O2HouseType getHouse(@NotNull UUID pid) {
        if (O2HouseMap.containsKey(pid)) {
            return O2HouseMap.get(pid);
        }

        return null;
    }

    /**
     * Get a list of the players sorted in to a house.
     *
     * @param houseType the house to get the members of
     * @return the names of all members of the specified house.
     */
    @NotNull
    public ArrayList<String> getHouseMembers(@NotNull O2HouseType houseType) {
        ArrayList<String> houseMembers = new ArrayList<>();
        Server server = p.getServer();

        for (Entry<UUID, O2HouseType> entry : O2HouseMap.entrySet()) {
            if (entry.getValue() == houseType) {
                UUID playerID = entry.getKey();
                String playerName = server.getOfflinePlayer(playerID).getName();
                houseMembers.add(playerName);
            }
        }

        return houseMembers;
    }

    /**
     * Set the house points for a specific house.
     *
     * <p>Updates the point score for the specified house and refreshes the scoreboard display.
     * This is a thread-safe operation that sets the exact point value (not an increment).</p>
     *
     * @param houseType the house to set points for
     * @param points    the new point value to set for this house
     * @return true if the scoreboard was successfully updated, false otherwise
     */
    public synchronized boolean setHousePoints(@NotNull O2HouseType houseType, int points) {
        houseType.setScore(points);

        return updateScoreboard();
    }

    /**
     * Reset all house points to zero.
     *
     * <p>Sets the point score for each house back to 0 and refreshes the scoreboard display to reflect
     * the changes. This method does not affect player house membership; only the accumulated house points
     * are cleared. The scoreboard is immediately updated to show the reset values to all players.</p>
     *
     * @return true if the scoreboard was successfully updated after resetting points, false otherwise
     * @see #initHousePoints() for internal point initialization
     * @see #reset() for a complete house system reset including membership clearing
     */
    public boolean resetHousePoints() {
        initHousePoints();

        return updateScoreboard();
    }

    /**
     * Reset the complete house system to its initial state.
     *
     * <p>Performs a full reset of the house management system by clearing all player-to-house mappings and
     * resetting all house points to zero. This removes all players from their assigned houses and clears the
     * accumulated points for all houses. The scoreboard display is updated immediately to reflect the reset
     * state. Use this when you need a complete wipe of all house data.</p>
     *
     * @return true if the scoreboard was successfully updated after the reset, false otherwise
     * @see #resetHousePoints() for resetting only house points while keeping player assignments
     * @see #unsort(Player) for removing a single player from their house
     */
    public boolean reset() {
        p.getLogger().info("Resetting houses...");

        O2HouseMap.clear();

        initHousePoints();
        return updateScoreboard();
    }

    /**
     * Add points to a specific house.
     *
     * <p>Increments the point score for the specified house by the given amount. The new total is calculated
     * by adding the points parameter to the house's current score. The scoreboard is automatically updated to
     * display the new point value to all players. This is equivalent to calling setHousePoints() with the
     * sum of the current score and the increment value.</p>
     *
     * @param houseType the house to add points to
     * @param points    the amount of points to add (can be positive or negative)
     * @return true if the scoreboard was successfully updated, false otherwise
     * @see #setHousePoints(O2HouseType, int) for setting an exact point value
     * @see #subtractHousePoints(O2HouseType, int) for subtracting points with floor clamping
     */
    public boolean addHousePoints(@NotNull O2HouseType houseType, int points) {
        int pts = points + houseType.getScore();

        return setHousePoints(houseType, pts);
    }

    /**
     * Subtract points from a specific house.
     *
     * <p>Decrements the point score for the specified house by the given amount. The new total is calculated
     * by subtracting the points parameter from the house's current score. If the subtraction would result in a
     * negative value, the house points are clamped to zero instead. The scoreboard is automatically updated to
     * display the new point value to all players.</p>
     *
     * @param houseType the house to subtract points from
     * @param points    the amount of points to subtract; if this is greater than or equal to the house's total,
     *                  points are set to 0
     * @return true if the scoreboard was successfully updated, false otherwise
     * @see #setHousePoints(O2HouseType, int) for setting an exact point value
     * @see #addHousePoints(O2HouseType, int) for adding points without floor clamping
     */
    public boolean subtractHousePoints(@NotNull O2HouseType houseType, int points) {
        int pts = houseType.getScore() - points;

        if (pts < 0)
            pts = 0; // house points cannot go negative

        return setHousePoints(houseType, pts);
    }

    /**
     * Create and initialize the house points scoreboard infrastructure.
     *
     * <p>Initializes the main Bukkit scoreboard for displaying house standings. This method removes any
     * previous house points objective and clears the sidebar display slot before registering a new
     * objective with DUMMY criteria. The scoreboard is configured with house teams and their color codes
     * for visual distinction. After initialization, all house scores are populated from the current
     * house points state.</p>
     *
     * <p>Scoreboard Configuration:</p>
     * <ul>
     * <li>Objective name: "o2_hpoints" (internal identifier)</li>
     * <li>Objective display name: "House Points"</li>
     * <li>Scoring criteria: DUMMY (manual score control)</li>
     * <li>Display location: SIDEBAR (right side of HUD)</li>
     * <li>House teams: registered with house-specific colors and options</li>
     * </ul>
     *
     * @see #registerHouseTeam(O2HouseType) for team initialization
     * @see #updateScoreboard() for score updates after initialization
     */
    private void createScoreboard() {
        if (!useHouses) {
            // do not allow if houses is not enabled
            common.printDebugMessage("Attempted to create scoreboard when houses is not enabled.", null, null, false);
            return;
        }

        ScoreboardManager scoreboardManager = p.getServer().getScoreboardManager();
        if (scoreboardManager == null)
            return;

        scoreboard = p.getServer().getScoreboardManager().getMainScoreboard();

        common.printDebugMessage("Created scoreboard...", null, null, false);

        // reset the scoreboard - we don't know what may have been saved on it
        // 1. if there was a previous house points objective, remove it
        Objective objective = scoreboard.getObjective(objectiveName);

        if (objective != null) {
            objective.unregister();
            common.printDebugMessage("Unregistered previous house points objective...", null, null, false);
        }

        // 2. if there is another objective on the slot we want, remove it
        objective = scoreboard.getObjective(scoreboardSlot);
        if (objective != null) {
            objective.unregister();
            common.printDebugMessage("Unregistered previous scoreboard objective...", null, null, false);
        }

        scoreboard.registerNewObjective(objectiveName, Criteria.DUMMY, scoreboardDisplayName);

        objective = scoreboard.getObjective(objectiveName);
        if (objective == null) {
            common.printDebugMessage("createScoreboard: Failed to create scoreboard objective", null, null, false);
            return;
        }

        objective.setDisplayName(objectiveDisplayName);
        objective.setDisplaySlot(scoreboardSlot);

        // register houses on scoreboard
        for (O2HouseType houseType : O2HouseType.values()) {
            registerHouseTeam(houseType);
        }

        updateScoreboard();
    }

    /**
     * Register a house team on the scoreboard with configuration.
     *
     * <p>Creates or retrieves a team for the specified house on the scoreboard and configures its
     * display and team options. If the team does not already exist, it is created with the house name.
     * The team's color is set to the house's configured chat color for visual identification. Team
     * options are configured to allow friendly fire and restrict death messages to only team members,
     * creating appropriate team-based gameplay behavior.</p>
     *
     * <p>Team Configuration:</p>
     * <ul>
     * <li>Team name: house display name (customizable via configuration)</li>
     * <li>Color code: house-specific chat color</li>
     * <li>Friendly fire: enabled</li>
     * <li>Death message visibility: FOR_OWN_TEAM (visible only to team members)</li>
     * </ul>
     *
     * @param houseType the house type to register as a team
     * @see O2HouseType for house names and colors
     * @see #updateTeam(Player, O2HouseType, boolean) for adding/removing players from teams
     */
    private void registerHouseTeam(@NotNull O2HouseType houseType) {
        String houseName = houseType.getName();

        Team team = scoreboard.getTeam(houseName);

        if (team == null) {
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
     * Synchronize and refresh all house scores on the scoreboard display.
     *
     * <p>Updates the scoreboard objective and refreshes all house point scores to match the current
     * standings. If the scoreboard has not yet been initialized, this method performs lazy initialization
     * by calling createScoreboard(). This is a thread-safe operation that ensures all house scores
     * displayed to players are synchronized with the internal house points state.</p>
     *
     * @return true if the scoreboard was successfully updated, false if houses are disabled, the
     * scoreboard is not initialized, or the objective cannot be found
     * @see #createScoreboard() for scoreboard initialization
     * @see #updateScoreboardScore(O2HouseType) for individual score updates
     */
    private synchronized boolean updateScoreboard() {
        if (!useHouses) {
            common.printDebugMessage("Tried to update scoreboard when houses are not enabled.", null, null, false);
            return false;
        }

        if (scoreboard == null) {
            createScoreboard();
        }

        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective != null) {
            for (O2HouseType houseType : O2HouseType.values()) {
                updateScoreboardScore(houseType);
            }

            return true;
        }

        common.printDebugMessage("updateScoreboard: house points objective not found.", null, null, false);
        return false;
    }

    /**
     * Update the score display for a single house on the scoreboard.
     *
     * <p>Retrieves the objective and team for the specified house type and updates its score on the
     * scoreboard to match the house's current point value. If the objective or team cannot be found,
     * this method returns silently. Errors during score updates are caught and logged as debug messages,
     * allowing the operation to continue gracefully even if individual score updates fail.</p>
     *
     * @param houseType the house type whose score should be updated
     * @see #setHousePoints(O2HouseType, int) for changing house point values
     */
    private void updateScoreboardScore(@NotNull O2HouseType houseType) {
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective == null)
            return;

        Team team = O2HouseTeamMap.get(houseType);

        if (team == null)
            return;

        try {
            Score score = objective.getScore(houseType.getName());
            score.setScore(houseType.getScore());
        }
        catch (Exception e) {
            common.printDebugMessage("updateScoreboardScore: failed to update score for " + houseType.getName(), e, null, false);
        }
    }

    /**
     * Hide the house points scoreboard from display.
     *
     * <p>Clears the SIDEBAR display slot, removing the house points scoreboard from the player's HUD.
     * This is a visibility toggle independent of the scoreboard's internal state. The scoreboard data
     * is not cleared or deleted; only its display is toggled. This method does nothing if houses are
     * disabled or the scoreboard objective cannot be found.</p>
     *
     * @return true if the scoreboard display was successfully hidden, false if houses are disabled or
     * the scoreboard objective does not exist
     * @see #showScoreboard() to display the scoreboard again
     */
    private boolean hideScoreboard() {
        if (!useHouses) {
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
     * Display the house points scoreboard on the HUD.
     *
     * <p>Sets the SIDEBAR display slot to show the house points scoreboard to all players. This makes
     * the scoreboard visible on the right side of the player's screen. If the scoreboard objective has
     * not been initialized, this method returns silently without throwing errors. This method does
     * nothing if houses are disabled.</p>
     *
     * @see #hideScoreboard() to remove the scoreboard from display
     * @see #updateScoreboard() for score synchronization
     */
    private void showScoreboard() {
        if (!useHouses) {
            common.printDebugMessage("Tried to show scoreboard when houses are not enabled.", null, null, false);
            return;
        }

        Objective objective = scoreboard.getObjective(objectiveName);

        if (objective != null)
            objective.setDisplaySlot(scoreboardSlot);
    }

    /**
     * Update player team membership on the scoreboard.
     *
     * <p>Adds a player to or removes a player from the specified house team on the scoreboard. When adding,
     * the player's display name is prefixed with the house's color code. When removing, the display name
     * is reverted to its original name if it previously had a color code applied (indicated by the § character).
     * If the house team cannot be found on the scoreboard, this method returns silently.</p>
     *
     * @param player    the player whose team membership should be updated
     * @param houseType the house team to update
     * @param add       true to add the player to the team with colored display name, false to remove the player
     * @see #addPlayerToHouseTeam(Player) for adding players with automatic house lookup
     * @see O2HouseType for house color information
     */
    private synchronized void updateTeam(@NotNull Player player, @NotNull O2HouseType houseType, boolean add) {
        String name = player.getName();
        String displayName = player.getDisplayName();
        Team team = O2HouseTeamMap.get(houseType);

        if (team == null) {
            common.printDebugMessage("Team " + houseType + " does not exist.", null, null, false);
            return;
        }

        if (add) {
            team.addEntry(name);
            player.setDisplayName(houseType.getColorPrefix() + name);
        }
        else {
            team.removeEntry(name);
            if (displayName.startsWith("§")) {
                // we have set a team color on their display name, change it back when we remove them from the team
                player.setDisplayName(name);
            }
        }
    }

    /**
     * Add a player to their assigned house team with colored display name.
     *
     * <p>Convenience method that automatically retrieves the player's assigned house from the house map
     * and adds them to the corresponding team with house-colored display name. This method is called during
     * player sorting and when players rejoin the server. If the player is not in the house map or their house
     * team does not exist, this method returns silently.</p>
     *
     * @param player the player to add to their house team
     * @see #updateTeam(Player, O2HouseType, boolean) for manual team updates
     * @see #sort(Player, O2HouseType) for initial player sorting
     */
    public void addPlayerToHouseTeam(@NotNull Player player) {
        UUID pid = player.getUniqueId();
        if (O2HouseMap.containsKey(pid)) {
            updateTeam(player, O2HouseMap.get(pid), true);
        }
    }
}