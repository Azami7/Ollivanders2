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
 * Central manager for all divination prophecies on the server.
 * <p>
 * Holds active prophecies (aging toward fulfillment for online players) and offline prophecies (waiting for their
 * targets to rejoin). {@link #upkeep()} ages and fulfills active prophecies each tick, and prophecies are persisted to
 * disk as JSON so they survive server restarts.
 * </p>
 *
 * @author Azami7
 * @see O2Prophecy
 * @see O2Divination
 */
public class O2Prophecies {
    /**
     * Reference to the plugin
     */
    final private Ollivanders2 p;

    /**
     * Common functions
     */
    final private Ollivanders2Common common;

    /**
     * Prophecies for online targets, aged and fulfilled each tick by {@link #upkeep()} until fulfilled or killed.
     */
    final private List<O2Prophecy> activeProphecies = new ArrayList<>();

    /**
     * Prophecies whose targets were offline at fulfillment time; {@link #onJoin(UUID)} moves them back to
     * {@link #activeProphecies} when the target rejoins.
     */
    final private List<O2Prophecy> offlineProphecies = new ArrayList<>();

    /**
     * JSON field labels for persisted prophecy data; must stay consistent with existing save files.
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
     * Add a newly created prophecy to the active list, where it will be aged and fulfilled each tick.
     *
     * @param prophecy the prophecy to add
     */
    public void addProphecy(@NotNull O2Prophecy prophecy) {
        common.printDebugMessage("Adding prophecy", null, null, false);
        activeProphecies.add(prophecy);
    }

    /**
     * Stash a prophecy whose target is offline; it is retried when the target rejoins via {@link #onJoin(UUID)}.
     *
     * @param prophecy the prophecy whose target is offline
     */
    void addOfflineProphecy(@NotNull O2Prophecy prophecy) {
        common.printDebugMessage("Adding prophecy", null, null, false);
        offlineProphecies.add(prophecy);
    }

    /**
     * Get the first active prophecy about the given player. Searches only the active list; use {@link #getProphecy(UUID)}
     * to include offline prophecies.
     *
     * @param pid the unique ID of the target player
     * @return the prophecy about this player, or null if none is active
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
     * Get the messages of all pending prophecies, both active and offline.
     *
     * @return the prophecy messages; empty if none, never null
     */
    @NotNull
    public List<String> getProphecies() {
        ArrayList<String> prophecies = new ArrayList<>();

        for (O2Prophecy prophecy : activeProphecies) {
            prophecies.add(prophecy.getProphecyMessage());
        }

        for (O2Prophecy prophecy : offlineProphecies) {
            prophecies.add(prophecy.getProphecyMessage());
        }

        return prophecies;
    }

    /**
     * Get the first active prophecy made by the given player, i.e. one where they are the prophet rather than the target.
     *
     * @param pid the unique ID of the prophet
     * @return the prophecy made by this player, or null if none is active
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
     * Advance all active prophecies one game tick: age each, fulfill any whose time has run out, and drop prophecies
     * that have been killed or moved to the offline list.
     */
    public void upkeep() {
        // iterate a snapshot so fulfill()/kill() can remove from activeProphecies without a ConcurrentModificationException
        ArrayList<O2Prophecy> prophecies = new ArrayList<>(activeProphecies);

        for (O2Prophecy prophecy : prophecies) {
            if (!prophecy.isKilled()) {
                prophecy.age();

                if (prophecy.getTime() < 1) {
                    prophecy.fulfill();

                    // fulfill() moves the prophecy to offlineProphecies if the target is offline; drop it from the
                    // active list so we stop trying to fulfill it here
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
     * Persist all pending prophecies to disk when the plugin disables, so they survive a server restart.
     */
    public void onDisable() {
        p.getLogger().info("Saving prophecies.");
        saveProphecies();
    }

    /**
     * Persist all non-killed active and offline prophecies to the prophecies save file as JSON.
     */
    public void saveProphecies() {
        List<Map<String, String>> prophecies = serializeProphecies();

        GsonDAO gsonLayer = new GsonDAO();
        gsonLayer.writeSaveData(prophecies, GsonDAO.o2PropheciesJSONFile);
    }

    /**
     * Restore saved prophecies from disk into the active list; no-ops if there is no save file.
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
     * Serialize all non-killed active and offline prophecies to a list of field-label maps for JSON storage.
     *
     * @return one map per pending prophecy; empty if none, never null
     */
    @NotNull
    private List<Map<String, String>> serializeProphecies() {
        List<Map<String, String>> prophecies = new ArrayList<>();

        for (O2Prophecy prophecy : activeProphecies) {
            if (prophecy.isKilled()) {
                continue;
            }

            prophecies.add(serializeProphecy(prophecy));
        }

        for (O2Prophecy prophecy : offlineProphecies) {
            if (prophecy.isKilled()) {
                continue;
            }

            prophecies.add(serializeProphecy(prophecy));
        }

        return prophecies;
    }

    /**
     * Serialize a single prophecy to a map keyed by the class's field labels.
     *
     * @param prophecy the prophecy to serialize
     * @return the prophecy's data as a field-label map
     */
    @NotNull
    private Map<String, String> serializeProphecy(@NotNull O2Prophecy prophecy) {
        Map<String, String> prophecyData = new HashMap<>();

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
     * Reconstruct a prophecy from a field-label map loaded from JSON.
     *
     * @param prophecyData the prophecy data keyed by field labels
     * @return the reconstructed prophecy, or null if any required field is missing or unparseable
     */
    @Nullable
    private O2Prophecy deserializeProphecy(@NotNull Map<String, String> prophecyData) {
        O2EffectType effectType = null;
        String prophecyMessage = null;
        UUID targetID = null;
        UUID prophetID = null;
        Long time = null;
        Integer duration = null;
        Integer accuracy = null;

        for (Map.Entry<String, String> entry : prophecyData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            try {
                // parse each field independently; a failure is caught and the missing field is detected in the check below
                switch (key) {
                    case String s when s.equals(effectTypeLabel) -> effectType = O2EffectType.valueOf(value);
                    case String s when s.equals(prophecyLabel) -> prophecyMessage = value;
                    case String s when s.equals(targetIDLabel) -> targetID = UUID.fromString(value);
                    case String s when s.equals(prophetIDLabel) -> prophetID = UUID.fromString(value);
                    case String s when s.equals(timeLabel) -> time = Long.valueOf(value);
                    case String s when s.equals(durationLabel) -> duration = Integer.valueOf(value);
                    case String s when s.equals(accuracyLabel) -> accuracy = Integer.valueOf(value);
                    default -> {
                    } // ignore unknown fields
                }
            }
            catch (Exception e) {
                common.printDebugMessage("Failure reading saved prophecy data.", e, null, true);
            }
        }

        // every field is required to reconstruct the prophecy
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
     * Move the newly joined player's offline prophecies back to the active list so they resume aging.
     *
     * @param pid the unique ID of the player that just logged in
     */
    public void onJoin(@NotNull UUID pid) {
        int count = 0;

        // snapshot so we can remove from offlineProphecies while iterating
        ArrayList<O2Prophecy> prophecies = new ArrayList<>(offlineProphecies);

        for (O2Prophecy prophecy : prophecies) {
            if (prophecy.getTargetID().equals(pid)) {
                activeProphecies.add(prophecy);
                offlineProphecies.remove(prophecy);

                count = count + 1;
            }
        }

        common.printDebugMessage("Loaded " + count + " prophecies for player.", null, null, false);
    }

    /**
     * Get the message of the first prophecy about the given player, checking active prophecies before offline ones. Use
     * {@link #getProphecyAboutPlayer(UUID)} if you need the prophecy object rather than just its text.
     *
     * @param targetID the unique ID of the target player
     * @return the prophecy message, or null if there is no prophecy about this player
     */
    @Nullable
    public String getProphecy(@NotNull UUID targetID) {
        for (O2Prophecy prop : activeProphecies) {
            if (prop.getTargetID().equals(targetID)) {
                return prop.getProphecyMessage();
            }
        }

        for (O2Prophecy prop : offlineProphecies) {
            if (prop.getTargetID().equals(targetID)) {
                return prop.getProphecyMessage();
            }
        }

        return null;
    }

    /**
     * Clear all pending prophecies from both the active and offline lists.
     */
    public void resetProphecies() {
        activeProphecies.clear();
        offlineProphecies.clear();
    }

    /**
     * Get the number of active prophecies, not counting offline ones.
     *
     * @return the number of active prophecies
     */
    public int activeProphecyCount() {
        return activeProphecies.size();
    }
}
