package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.GsonDAO;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Central manager for all divination prophecies in the server.
 * <p>
 * O2Prophecies maintains two separate lists of prophecies: active prophecies for online players and offline
 * prophecies waiting for players to return. Every game tick, the upkeep() method processes all active prophecies,
 * aging them by one tick and fulfilling those whose time has reached zero. Prophecies are automatically persisted
 * to disk using JSON serialization, allowing them to survive server restarts.
 * </p>
 * <p>
 * This class is responsible for:
 * </p>
 * <ul>
 * <li>Adding new prophecies created by divination spells</li>
 * <li>Processing active prophecies each tick (age and fulfill)</li>
 * <li>Managing offline prophecies that wait for players to log back in</li>
 * <li>Serializing prophecies to JSON for persistent storage</li>
 * <li>Deserializing prophecies from JSON when the server starts</li>
 * <li>Querying prophecies by target player or prophet player</li>
 * </ul>
 *
 * @author Azami7
 * @see O2Prophecy for the individual prophecy data structure
 * @see O2Divination for the divination system that creates prophecies
 */
public class O2Prophecies {
    /**
     * Reference to the plugin for accessing configuration and logging.
     */
    final private Ollivanders2 p;

    /**
     * Common utility functions for the plugin.
     */
    final private Ollivanders2Common common;

    /**
     * List of prophecies actively aging and scheduled for execution.
     * These prophecies are for online players and will be processed every game tick by upkeep().
     * When a prophecy's time reaches zero, it is fulfilled (effect applied to target).
     * Prophecies remain in this list until they are either fulfilled or killed.
     */
    final private List<O2Prophecy> activeProphecies = new ArrayList<>();

    /**
     * List of prophecies waiting for their target players to rejoin the server.
     * When a prophecy is fulfilled for an offline player, it is automatically moved here.
     * When the target player logs back in, onJoin() moves relevant prophecies back to activeProphecies.
     */
    final private List<O2Prophecy> offlineProphecies = new ArrayList<>();

    /**
     * JSON serialization field labels for persisting prophecy data.
     * These constants are used as keys when converting prophecies to/from Maps during save/load operations.
     * They must remain consistent with previously saved prophecy data files for backward compatibility.
     */
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
    public O2Prophecies(@NotNull Ollivanders2 plugin) {
        p = plugin;

        loadProphecies();
        common = new Ollivanders2Common(p);
    }

    /**
     * Add a newly created prophecy to the active prophecies list.
     *
     * <p>This method is called by {@link O2Divination#divine()} when a divination prophecy is created.
     * The prophecy is added to the active list and will be processed by upkeep() every game tick.</p>
     *
     * @param prophecy the newly created prophecy to add
     */
    public void addProphecy(@NotNull O2Prophecy prophecy) {
        common.printDebugMessage("Adding prophecy", null, null, false);
        activeProphecies.add(prophecy);
    }

    /**
     * Add a prophecy to the offline prophecies list when its target player is offline.
     *
     * <p>Called by O2Prophecy.fulfill() when a prophecy is fulfilled for a player who is not online.
     * The prophecy is stashed in the offline list and will be re-executed when the target player logs back in.</p>
     *
     * @param prophecy the prophecy whose target is offline
     */
    void addOfflineProphecy(@NotNull O2Prophecy prophecy) {
        common.printDebugMessage("Adding prophecy", null, null, false);
        offlineProphecies.add(prophecy);
    }

    /**
     * Get the first active prophecy where the given player is the target (subject of the prophecy).
     *
     * <p>Searches only the active prophecies list (prophecies scheduled for online players).
     * If you need to check for offline prophecies as well, use getProphecy() instead.</p>
     *
     * @param pid the unique ID of the target player
     * @return the prophecy about this player if found in active list, or null if not found
     */
    @Nullable
    public O2Prophecy getProphecyAboutPlayer(@NotNull UUID pid) {
        for (O2Prophecy prophecy : activeProphecies) {
            if (prophecy.getTargetID().equals(pid)) {
                return prophecy;
            }
        }

        return null;
    }

    /**
     * Get a list of all pending prophecy messages (both active and offline).
     *
     * <p>Returns the human-readable prophecy text messages from both active and offline prophecies combined.
     * Useful for displaying all pending prophecies to a player or admin.</p>
     *
     * @return a list of prophecy message strings from all pending prophecies
     */
    @NotNull
    public List<String> getProphecies() {
        ArrayList<String> prophecies = new ArrayList<>();

        // Add messages from active prophecies (scheduled for online players)
        for (O2Prophecy prophecy : activeProphecies) {
            prophecies.add(prophecy.getProphecyMessage());
        }

        // Add messages from offline prophecies (waiting for player to rejoin)
        for (O2Prophecy prophecy : offlineProphecies) {
            prophecies.add(prophecy.getProphecyMessage());
        }

        return prophecies;
    }

    /**
     * Get the first active prophecy made by (created by) the given player.
     *
     * <p>Searches only the active prophecies list to find a prophecy where this player is the prophet (creator).
     * This is distinct from getProphecyAboutPlayer() which finds prophecies where the player is the target.</p>
     *
     * @param pid the unique ID of the prophet (spell caster)
     * @return the prophecy created by this player if found in active list, or null if not found
     */
    @Nullable
    public O2Prophecy getProphecyByPlayer(@NotNull UUID pid) {
        for (O2Prophecy prophecy : activeProphecies) {
            if (prophecy.getProphetID().equals(pid)) {
                return prophecy;
            }
        }

        return null;
    }

    /**
     * Process all active prophecies for one game tick.
     *
     * <p>This method is called every server tick and handles the lifecycle of all active prophecies:</p>
     * <ol>
     * <li>Iterates through a snapshot of all active prophecies (to allow safe removal during iteration)</li>
     * <li>For each non-killed prophecy: ages it by one tick (decrements time counter)</li>
     * <li>If time reaches zero or below: calls fulfill() to execute the prophecy</li>
     * <li>If the fulfilled prophecy is in the offline list: removes it from active list</li>
     * <li>Removes any killed prophecies from the active list</li>
     * </ol>
     *
     * <p>A snapshot of the active prophecies list is used to avoid ConcurrentModificationException when
     * removing prophecies during iteration.</p>
     */
    public void upkeep() {
        ArrayList<O2Prophecy> prophecies = new ArrayList<>(activeProphecies);

        for (O2Prophecy prophecy : prophecies) {
            if (!prophecy.isKilled()) {
                prophecy.age();

                if (prophecy.getTime() < 1) {
                    prophecy.fulfill();

                    // fulfill() will move the prophecy to offlineProphecies if the target is not online, make sure it was
                    // removed from activeProphecies so we don't keep trying to fulfill it.
                    if (offlineProphecies.contains(prophecy)) {
                        activeProphecies.remove(prophecy);
                    }
                }
            }

            if (prophecy.isKilled()) {
                common.printDebugMessage("Removing prophecy", null, null, false);
                activeProphecies.remove(prophecy);
            }
        }
    }

    /**
     * Persist all prophecies to disk in JSON format.
     *
     * <p>Called when the server shuts down (via the prophecy save scheduler). Serializes all active and offline
     * prophecies to JSON Maps and writes them to the prophecies save file. Only non-killed prophecies are saved.</p>
     */
    public void saveProphecies() {
        List<Map<String, String>> prophecies = serializeProphecies();

        GsonDAO gsonLayer = new GsonDAO();
        gsonLayer.writeSaveData(prophecies, GsonDAO.o2PropheciesJSONFile);
    }

    /**
     * Load all saved prophecies from disk.
     *
     * <p>Called during plugin initialization to restore prophecies from the last server session.
     * Deserializes JSON data back into O2Prophecy objects and adds them to the active prophecies list.
     * If no save file exists, logs a message and continues without any prophecies.</p>
     */
    public void loadProphecies() {
        GsonDAO gsonLayer = new GsonDAO();
        List<Map<String, String>> prophecies = gsonLayer.readSavedDataListMap(GsonDAO.o2PropheciesJSONFile);

        if (prophecies == null) {
            p.getLogger().info("No saved prophecies.");
            return;
        }

        for (Map<String, String> prophecyData : prophecies) {
            O2Prophecy prophecy = deserializeProphecy(prophecyData);

            if (prophecy != null)
                activeProphecies.add(prophecy);
        }

        p.getLogger().info("Loaded " + activeProphecies.size() + " prophecies.");
    }

    /**
     * Serialize all pending prophecies to a list of Maps for JSON storage.
     *
     * <p>Converts both active and offline prophecies into Map<String,String> format, skipping any killed prophecies.
     * Each Map contains the prophecy data using the field labels defined at the class level.</p>
     *
     * @return a list of Map objects representing all pending prophecies ready for JSON serialization
     */
    @NotNull
    private List<Map<String, String>> serializeProphecies() {
        List<Map<String, String>> prophecies = new ArrayList<>();

        // Serialize active prophecies (those still waiting for online targets)
        for (O2Prophecy prophecy : activeProphecies) {
            if (prophecy.isKilled()) {
                continue;  // Skip killed prophecies, they don't need to be saved
            }

            prophecies.add(serializeProphecy(prophecy));
        }

        // Serialize offline prophecies (those waiting for offline targets to rejoin)
        for (O2Prophecy prophecy : offlineProphecies) {
            if (prophecy.isKilled()) {
                continue;  // Skip killed prophecies, they don't need to be saved
            }

            prophecies.add(serializeProphecy(prophecy));
        }

        return prophecies;
    }

    /**
     * Serialize a single prophecy into a Map of string key-value pairs.
     *
     * <p>Converts all prophecy data into a Map using the static field labels as keys.
     * This is used both for JSON serialization and for transferring prophecy data in the system.</p>
     *
     * @param prophecy the prophecy to serialize
     * @return a Map containing all prophecy data with field labels as keys
     */
    @NotNull
    private Map<String, String> serializeProphecy(@NotNull O2Prophecy prophecy) {
        Map<String, String> prophecyData = new HashMap<>();

        // Populate the map with all prophecy fields using the defined labels
        prophecyData.put(prophecyLabel, prophecy.getProphecyMessage());
        prophecyData.put(prophetIDLabel, prophecy.getProphetID().toString());
        prophecyData.put(targetIDLabel, prophecy.getTargetID().toString());
        prophecyData.put(effectTypeLabel, prophecy.getEffect().toString());
        prophecyData.put(durationLabel, Integer.toString(prophecy.getDuration()));
        prophecyData.put(accuracyLabel, Integer.toString(prophecy.getAccuracy()));
        prophecyData.put(timeLabel, Long.toString(prophecy.getTime()));

        return prophecyData;
    }

    /**
     * Deserialize a prophecy from a Map of string key-value pairs.
     *
     * <p>Reconstructs an O2Prophecy object from Map data loaded from JSON. Parses all fields and validates
     * that all required fields are present before creating the prophecy. If any field is missing or parsing fails,
     * returns null and logs the error.</p>
     *
     * @param prophecyData a Map containing prophecy data with field labels as keys
     * @return a reconstructed O2Prophecy object, or null if deserialization failed
     */
    @Nullable
    private O2Prophecy deserializeProphecy(@NotNull Map<String, String> prophecyData) {
        // Initialize all fields to null before parsing
        O2EffectType effectType = null;
        String prophecyMessage = null;
        UUID targetID = null;
        UUID prophetID = null;
        Long time = null;
        Integer duration = null;
        Integer accuracy = null;

        // Parse each field from the map, gracefully handling any parsing errors
        for (Map.Entry<String, String> entry : prophecyData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            try {
                // Parse each field independently. If one field fails, we catch the exception and continue
                // parsing the remaining fields. Missing or invalid fields are detected below when checking
                // that all required fields were successfully deserialized.
                switch (key) {
                    case String s when s.equals(effectTypeLabel) -> effectType = O2EffectType.valueOf(value);
                    case String s when s.equals(prophecyLabel) -> prophecyMessage = value;
                    case String s when s.equals(targetIDLabel) -> targetID = UUID.fromString(value);
                    case String s when s.equals(prophetIDLabel) -> prophetID = UUID.fromString(value);
                    case String s when s.equals(timeLabel) -> time = Long.valueOf(value);
                    case String s when s.equals(durationLabel) -> duration = Integer.valueOf(value);
                    case String s when s.equals(accuracyLabel) -> accuracy = Integer.valueOf(value);
                    default -> {
                    } // Ignore unknown fields
                }
            }
            catch (Exception e) {
                common.printDebugMessage("Failure reading saved prophecy data.", e, null, true);
            }
        }

        // Only create a prophecy if all required fields were successfully deserialized. We need all attributes to re-create the prophecy object
        if (effectType != null && prophecyMessage != null && targetID != null && prophetID != null &&
                time != null && duration != null && accuracy != null) {
            return new O2Prophecy(p, effectType, prophecyMessage, targetID, prophetID, time, duration, accuracy);
        }
        else {
            p.getLogger().info("Failure reading saved prophecy data - one or more fields missing.");
            return null;
        }
    }

    /**
     * Move any prophecies for the newly joined player from offline to active list.
     *
     * <p>When a player logs in, check if there are any prophecies waiting in the offline list for them.
     * If so, move those prophecies to the active list so they resume being processed by upkeep().</p>
     *
     * <p>A snapshot of the offline prophecies list is used to allow safe removal during iteration.</p>
     *
     * @param pid the unique ID of the player that just logged in
     */
    public void onJoin(@NotNull UUID pid) {
        int count = 0;

        // Create a snapshot to allow safe removal during iteration
        ArrayList<O2Prophecy> prophecies = new ArrayList<>(offlineProphecies);

        for (O2Prophecy prophecy : prophecies) {
            if (prophecy.getTargetID().equals(pid)) {
                // Move this prophecy from offline to active list
                activeProphecies.add(prophecy);
                offlineProphecies.remove(prophecy);

                count = count + 1;
            }
        }

        common.printDebugMessage("Loaded " + count + " prophecies for player.", null, null, false);
    }

    /**
     * Get the prophecy message text for a specific player (searches both active and offline prophecies).
     *
     * <p>Returns the first prophecy message found for the player, checking active prophecies first,
     * then offline prophecies. Only returns the message text, not the full O2Prophecy object.
     * Use getProphecyAboutPlayer() if you need the full prophecy object.</p>
     *
     * @param targetID the unique ID of the target player
     * @return the prophecy message text if found, or null if no prophecy exists for this player
     */
    @Nullable
    public String getProphecy(@NotNull UUID targetID) {
        // Check active prophecies first
        for (O2Prophecy prop : activeProphecies) {
            if (prop.getTargetID().equals(targetID)) {
                return prop.getProphecyMessage();
            }
        }

        // If not found, check offline prophecies
        for (O2Prophecy prop : offlineProphecies) {
            if (prop.getTargetID().equals(targetID)) {
                return prop.getProphecyMessage();
            }
        }

        return null;
    }

    /**
     * Clear all pending prophecies (both active and offline).
     *
     * <p>Removes all prophecies from both lists. Used primarily for testing and configuration resets.</p>
     */
    public void resetProphecies() {
        activeProphecies.clear();
        offlineProphecies.clear();
    }

    /**
     * Get the count of active prophecies currently pending.
     *
     * <p>Returns the number of prophecies in the active list only (not including offline prophecies).
     * Useful for status checking and debugging.</p>
     *
     * @return the number of active prophecies
     */
    public int activeProphecyCount() {
        return activeProphecies.size();
    }
}
