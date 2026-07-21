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
 * <p>O2Houses manages the Hogwarts house system: sorting players into the four houses, tracking membership, keeping
 * house points on a scoreboard, and persisting all of it to disk. Houses are optional and configurable (custom names
 * and colors), and a {@link OllivandersPlayerSortedEvent} fires when a player is sorted.</p>
 *
 * @author Azami7
 * @see O2HouseType
 * @see OllivandersPlayerSortedEvent
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
     * Constructor
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    public O2Houses(@NotNull Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(p);
    }

    /**
     * Enable and initialize the house system on plugin startup: read config, build the scoreboard, load persisted house
     * data, and show the standings. No-ops if houses are disabled in config.
     */
    public void onEnable() {
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
     * Override each house's default name and color with any non-empty custom values set in the plugin config.
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
     * Set every house's points to zero.
     */
    private void initHousePoints() {
        for (O2HouseType houseType : O2HouseType.values()) {
            setHousePoints(houseType, 0);
        }
    }

    /**
     * Get the house with the given name, matched case-insensitively after trimming whitespace.
     *
     * @param name the house name; may be null
     * @return the matching house, or null if the name is null or matches no house
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
     * Get the current (possibly customized) display names of all houses.
     *
     * @return a new list of the house names
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
     * Restore player-to-house assignments and house point scores from disk; skips whichever file is missing or empty.
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
     * Persist house memberships and point scores to disk when the plugin disables, so they survive a restart.
     */
    public void onDisable() {
        p.getLogger().info("Saving house data.");

        saveHouses();
    }

    /**
     * Persist the current player-to-house assignments and house point scores to disk as JSON. No-ops if houses are
     * disabled.
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
     * Sort a player into a house: add them to the house team with a colored display name, broadcast a sorting title if
     * configured, and fire an {@link OllivandersPlayerSortedEvent}. No-ops if the player is already sorted.
     *
     * @param player    the player to sort
     * @param houseType the house to sort them into
     * @return true if the player was sorted, false if they were already sorted
     */
    public boolean sort(@NotNull Player player, @NotNull O2HouseType houseType) {
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
     * Remove a player from their house team and the house map, making them unsorted. No-ops if they are not sorted.
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
     * Set a player's house even if they are already sorted, by unsorting then re-sorting them. Kept separate from
     * {@link #sort} so sort() is not accidentally used on an already-sorted player.
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
     * Set a house's points to an exact value (not an increment) and refresh the scoreboard.
     *
     * @param houseType the house to set points for
     * @param points    the new point value
     * @return true if the scoreboard was successfully updated, false otherwise
     */
    public synchronized boolean setHousePoints(@NotNull O2HouseType houseType, int points) {
        houseType.setScore(points);

        return updateScoreboard();
    }

    /**
     * Reset every house's points to zero, leaving player house membership untouched, and refresh the scoreboard.
     *
     * @return true if the scoreboard was successfully updated, false otherwise
     * @see #reset() for a full reset that also clears membership
     */
    public boolean resetHousePoints() {
        initHousePoints();

        return updateScoreboard();
    }

    /**
     * Fully reset the house system: clear all player-to-house assignments and zero all house points, then refresh the
     * scoreboard.
     *
     * @return true if the scoreboard was successfully updated, false otherwise
     * @see #resetHousePoints() for resetting only points
     */
    public boolean reset() {
        p.getLogger().info("Resetting houses...");

        O2HouseMap.clear();

        initHousePoints();
        return updateScoreboard();
    }

    /**
     * Add points to a house's score (a negative value subtracts, with no floor) and refresh the scoreboard.
     *
     * @param houseType the house to add points to
     * @param points    the points to add; may be negative
     * @return true if the scoreboard was successfully updated, false otherwise
     * @see #subtractHousePoints(O2HouseType, int) for subtracting with a zero floor
     */
    public boolean addHousePoints(@NotNull O2HouseType houseType, int points) {
        int pts = points + houseType.getScore();

        return setHousePoints(houseType, pts);
    }

    /**
     * Subtract points from a house's score, clamped so it never goes below zero, and refresh the scoreboard.
     *
     * @param houseType the house to subtract points from
     * @param points    the points to subtract
     * @return true if the scoreboard was successfully updated, false otherwise
     * @see #addHousePoints(O2HouseType, int) for adding without a floor
     */
    public boolean subtractHousePoints(@NotNull O2HouseType houseType, int points) {
        int pts = houseType.getScore() - points;

        if (pts < 0)
            pts = 0; // house points cannot go negative

        return setHousePoints(houseType, pts);
    }

    /**
     * Build the house points scoreboard on the server's main scoreboard: clear any prior objective on the sidebar,
     * register the house points objective and a team per house, and populate current scores. No-ops if houses are
     * disabled.
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
     * Register (or reconfigure) the scoreboard team for a house, setting its color, enabling friendly fire, and
     * restricting death messages to team members.
     *
     * @param houseType the house to register as a team
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
     * Refresh every house's score on the scoreboard, lazily building the scoreboard first if it does not yet exist.
     *
     * @return true if the scoreboard was updated, false if houses are disabled or the objective cannot be found
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
     * Update one house's displayed score to its current point value; no-ops if its objective or team is missing, and
     * swallows any scoreboard errors as debug messages.
     *
     * @param houseType the house whose score should be updated
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
     * Hide the house points scoreboard by clearing its sidebar slot, without deleting the underlying scores. No-ops if
     * houses are disabled or the objective is missing.
     *
     * @return true if the scoreboard was hidden, false if houses are disabled or the objective does not exist
     * @see #showScoreboard()
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
     * Show the house points scoreboard on the sidebar. No-ops if houses are disabled or the objective is missing.
     *
     * @see #hideScoreboard()
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
     * Add a player to or remove a player from a house's scoreboard team. Adding prefixes their display name with the
     * house color; removing reverts a previously colored display name. No-ops if the team is missing.
     *
     * @param player    the player whose team membership to update
     * @param houseType the house team to update
     * @param add       true to add with a colored display name, false to remove
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
     * Add a player to the scoreboard team of their assigned house. No-ops if the player is not sorted. Called on sort
     * and on rejoin.
     *
     * @param player the player to add to their house team
     */
    public void addPlayerToHouseTeam(@NotNull Player player) {
        UUID pid = player.getUniqueId();
        if (O2HouseMap.containsKey(pid)) {
            updateTeam(player, O2HouseMap.get(pid), true);
        }
    }
}