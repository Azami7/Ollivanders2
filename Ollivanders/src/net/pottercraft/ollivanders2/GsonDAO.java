package net.pottercraft.ollivanders2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.pottercraft.ollivanders2.common.TimeCommon;
import net.pottercraft.ollivanders2.house.O2HouseType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * GSON data persistence layer, which reads and writes the plugin's save files as JSON under
 * {@link Ollivanders2#pluginDir}.
 * <p>
 * Save data is best-effort: writes that fail are logged and dropped, reads of a missing or unreadable file return
 * null, and entries within a file that cannot be parsed are skipped so one bad record does not cost the whole file.
 * </p>
 */
public class GsonDAO implements GenericDAO {
    /**
     * The serializer for all save files, configured to write human-readable JSON
     */
    final private Gson gson;

    /**
     * Where previous save files are moved to when {@link Ollivanders2#archivePreviousBackup} is on
     */
    private static final String archiveDirectory = Ollivanders2.pluginDir + "archive";

    /**
     * the house sort save file name
     */
    public static final String housesJSONFile = "O2Houses.txt";

    /**
     * the house points save file name
     */
    public static final String housePointsJSONFile = "O2HousePoints.txt";

    /**
     * the apparate locations save file name
     */
    public static final String apparateLocationsJSONFile = "O2ApparateLocations.txt";

    /**
     * the players save file name
     */
    public static final String o2PlayerJSONFile = "O2Players.txt";

    /**
     * the stationary spell save file name
     */
    public static final String o2StationarySpellsJSONFile = "O2StationarySpells.txt";

    /**
     * the prophecies save file name
     */
    public static final String o2PropheciesJSONFile = "O2Prophecies.txt";

    /**
     * the effects save file name
     */
    public static final String o2EffectsJSONFile = "O2Effects.txt";

    /**
     * Constructor.
     */
    public GsonDAO() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Write the house sort data, replacing any previously saved house sort file.
     *
     * @param map the house each player is sorted to
     */
    @Override
    public void writeHouses(@NotNull Map<UUID, O2HouseType> map) {
        // UUID and O2HouseType keys have no useful json representation, so save them by name
        Map<String, String> strMap = new HashMap<>();
        for (Entry<UUID, O2HouseType> e : map.entrySet()) {
            strMap.put(e.getKey().toString(), e.getValue().toString());
        }

        String json = gson.toJson(strMap);
        writeJSON(json, housesJSONFile);
    }

    /**
     * Write the house points, replacing any previously saved house points file.
     *
     * @param map the points each house has
     */
    @Override
    public void writeHousePoints(@NotNull Map<O2HouseType, Integer> map) {
        Map<String, String> strMap = new HashMap<>();
        for (Entry<O2HouseType, Integer> e : map.entrySet()) {
            strMap.put(e.getKey().toString(), e.getValue().toString());
        }

        String json = gson.toJson(strMap);
        writeJSON(json, housePointsJSONFile);
    }

    /**
     * Write the apparate locations, replacing any previously saved apparate locations file. Locations whose world is
     * not loaded cannot be saved and are left out.
     *
     * @param locations a map of location names to the location they name
     */
    @Override
    public void writeApparateData(@NotNull HashMap<String, Location> locations) {
        Map<String, String[]> serializedLocations = new HashMap<>();
        for (Entry<String, Location> entry : locations.entrySet()) {
            Location location = entry.getValue();
            World world = location.getWorld();

            // a location in an unloaded world cannot be saved, skip it rather than losing the whole file
            if (world == null) {
                Ollivanders2API.common.printDebugMessage("GsonDAO.writeApparateData: no world for location " + entry.getKey(), null, null, true);
                continue;
            }

            String[] locationAsArray = {world.getName(), String.valueOf(location.getX()), String.valueOf(location.getY()), String.valueOf(location.getZ())};
            serializedLocations.put(entry.getKey(), locationAsArray);
        }

        String json = gson.toJson(serializedLocations);
        writeJSON(json, apparateLocationsJSONFile);
    }

    /**
     * Write save data, replacing any previously saved file of the same name.
     *
     * @param map      the data to write
     * @param filename the name of the save file, relative to the plugin directory
     */
    @Override
    public void writeSaveData(@NotNull HashMap<String, String> map, @NotNull String filename) {
        String json = gson.toJson(map);
        writeJSON(json, filename);
    }

    /**
     * Write save data, replacing any previously saved file of the same name.
     *
     * @param map      the data to write
     * @param filename the name of the save file, relative to the plugin directory
     */
    @Override
    public void writeSaveData(@NotNull Map<String, Map<String, String>> map, @NotNull String filename) {
        String json = gson.toJson(map);
        writeJSON(json, filename);
    }

    /**
     * Write save data, replacing any previously saved file of the same name.
     *
     * @param list     the data to write
     * @param filename the name of the save file, relative to the plugin directory
     */
    @Override
    public void writeSaveData(@NotNull List<Map<String, String>> list, @NotNull String filename) {
        String json = gson.toJson(list);
        writeJSON(json, filename);
    }

    /**
     * Read the house sort data. Players whose saved id or house cannot be parsed are left out.
     *
     * @return the house each player is sorted to, or null if the save file is missing or unreadable
     */
    @Override
    @Nullable
    public Map<UUID, O2HouseType> readHouses() {
        String json = readJSON(housesJSONFile);
        if (json == null)
            return null;

        Map<String, String> strMap;

        try {
            strMap = gson.fromJson(json, new TypeToken<HashMap<String, String>>() {
            }.getType());
        }
        catch (JsonSyntaxException e) {
            Ollivanders2API.common.printDebugMessage("GsonDAO.readHouses: unable to parse " + housesJSONFile, null, e, true);
            return null;
        }

        if (strMap == null)
            return null;

        Map<UUID, O2HouseType> map = new HashMap<>();
        for (Entry<String, String> entry : strMap.entrySet()) {
            String playerID = entry.getKey();
            String house = entry.getValue();

            if (house == null || playerID == null)
                continue;

            UUID pid = Ollivanders2API.common.uuidFromString(playerID);
            if (pid == null) {
                continue;
            }

            O2HouseType hType;

            try {
                hType = O2HouseType.valueOf(house);
            }
            catch (Exception e) {
                Ollivanders2API.common.printDebugMessage("GsonDAO.readHouses: unknown house " + house, e, null, true);
                continue;
            }

            map.put(pid, hType);
        }

        return map;
    }

    /**
     * Read the house points. Entries whose saved house or points cannot be parsed are left out.
     *
     * @return the points each house has, or null if the save file is missing or unreadable
     */
    @Override
    @Nullable
    public Map<O2HouseType, Integer> readHousePoints() {
        String json = readJSON(housePointsJSONFile);
        if (json == null)
            return null;

        Map<String, String> strMap;

        try {
            strMap = gson.fromJson(json, new TypeToken<HashMap<String, String>>() {
            }.getType());
        }
        catch (JsonSyntaxException e) {
            Ollivanders2API.common.printDebugMessage("GsonDAO.readHousePoints: unable to parse " + housePointsJSONFile, null, e, true);
            return null;
        }

        if (strMap == null)
            return null;

        Map<O2HouseType, Integer> map = new HashMap<>();
        for (Entry<String, String> entry : strMap.entrySet()) {
            String house = entry.getKey();
            String points = entry.getValue();

            if (house == null || points == null)
                continue;

            O2HouseType hType;
            Integer pts;

            try {
                hType = O2HouseType.valueOf(house);
            }
            catch (Exception e) {
                Ollivanders2API.common.printDebugMessage("GsonDAO.readHousePoints: unknown house " + house, e, null, true);
                continue;
            }

            pts = Ollivanders2API.common.integerFromString(points);
            if (pts == null) {
                continue;
            }

            map.put(hType, pts);
        }

        return map;
    }

    /**
     * Read the apparate locations. Locations that are incomplete, unparseable, or in a world that is not loaded are
     * left out.
     *
     * @return a map of location names to the location they name, or null if the save file is missing or unreadable
     */
    @Override
    @Nullable
    public HashMap<String, Location> readApparateLocations() {
        String json = readJSON(apparateLocationsJSONFile);
        if (json == null) {
            return null;
        }

        Map<String, String[]> serializedLocations;

        try {
            serializedLocations = gson.fromJson(json, new TypeToken<Map<String, String[]>>() {
            }.getType());
        }
        catch (JsonSyntaxException e) {
            Ollivanders2API.common.printDebugMessage("GsonDAO.readApparateLocations: unable to parse " + apparateLocationsJSONFile, null, e, true);
            return null;
        }

        if (serializedLocations == null)
            return null;

        HashMap<String, Location> locations = new HashMap<>();
        for (Entry<String, String[]> entry : serializedLocations.entrySet()) {
            String locationName = entry.getKey();
            if (locationName == null || locationName.isEmpty()) {
                continue;
            }

            // a location is saved as world name plus x, y, and z, so anything else is not one we can restore
            if (entry.getValue().length != 4) {
                continue;
            }

            String worldName = entry.getValue()[0];
            World world = Bukkit.getServer().getWorld(worldName);
            if (world == null) {
                continue;
            }

            String xCoordStr = entry.getValue()[1];
            String yCoordStr = entry.getValue()[2];
            String zCoordStr = entry.getValue()[3];
            double xCoord;
            double yCoord;
            double zCoord;

            try {
                xCoord = Double.parseDouble(xCoordStr);
                yCoord = Double.parseDouble(yCoordStr);
                zCoord = Double.parseDouble(zCoordStr);
            }
            catch (Exception e) {
                continue;
            }

            locations.put(locationName, new Location(world, xCoord, yCoord, zCoord));
        }

        return locations;
    }

    /**
     * Read save data written as a map of maps.
     *
     * @param filename the name of the save file, relative to the plugin directory
     * @return the saved data, or null if the file is missing, unreadable, or not valid json
     */
    @Override
    @Nullable
    public Map<String, Map<String, String>> readSavedDataMapStringMap(@NotNull String filename) {
        String json = readJSON(filename);

        if (json == null)
            return null;

        Map<String, Map<String, String>> strMap;

        try {
            strMap = gson.fromJson(json, new TypeToken<Map<String, Map<String, String>>>() {
            }.getType());
        }
        catch (JsonSyntaxException e) {
            Ollivanders2API.common.printDebugMessage("GsonDAO.readSavedDataMapStringMap: unable to parse " + filename, null, e, true);
            return null;
        }

        return strMap;
    }

    /**
     * Read save data written as a list of maps.
     *
     * @param filename the name of the save file, relative to the plugin directory
     * @return the saved data, or null if the file is missing, unreadable, or not valid json
     */
    @Override
    @Nullable
    public List<Map<String, String>> readSavedDataListMap(@NotNull String filename) {
        String json = readJSON(filename);

        if (json == null)
            return null;

        List<Map<String, String>> strList;

        try {
            strList = gson.fromJson(json, new TypeToken<List<Map<String, String>>>() {
            }.getType());
        }
        catch (JsonSyntaxException e) {
            Ollivanders2API.common.printDebugMessage("GsonDAO.readSavedDataListMap: unable to parse " + filename, null, e, true);
            return null;
        }

        return strList;
    }

    /**
     * Write json data to a save file, replacing any file already there. Depending on
     * {@link Ollivanders2#archivePreviousBackup} the previous file is either moved to the archive directory or
     * deleted. Failures are logged and the data is dropped rather than thrown.
     *
     * @param json the json data to write
     * @param path the name of the save file, relative to the plugin directory
     */
    private synchronized void writeJSON(@NotNull String json, @NotNull String path) {
        String saveFile = Ollivanders2.pluginDir + path;

        File file = new File(saveFile);
        File dir = new File(Ollivanders2.pluginDir);

        try {
            // the previous save has to be moved out of the way, either to the archive or to the trash
            if (file.exists()) {
                try {
                    if (Ollivanders2.archivePreviousBackup) {
                        File archiveDir = new File(archiveDirectory);
                        if (!archiveDir.exists() && !archiveDir.mkdirs())
                            Ollivanders2API.common.printDebugMessage("GsonDAO.writeJSON: unable to create archive directory " + archiveDirectory, null, null, true);

                        String archiveFile = archiveDirectory + "/" + path + "-" + TimeCommon.getCurrentTimestamp();

                        File prev = new File(archiveFile);
                        if (!file.renameTo(prev))
                            Ollivanders2API.common.printDebugMessage("GsonDAO.writeJSON: unable to archive " + saveFile + " as " + archiveFile, null, null, true);
                    }
                    else if (!file.delete())
                        Ollivanders2API.common.printDebugMessage("GsonDAO.writeJSON: unable to delete previous " + saveFile, null, null, true);
                }
                catch (Exception e) {
                    Ollivanders2API.common.printDebugMessage("GsonDAO.writeJSON: unable to replace previous " + saveFile, e, null, true);
                }
            }

            if (!dir.exists() && !dir.mkdirs()) {
                Ollivanders2API.common.printDebugMessage("GsonDAO.writeJSON: unable to create plugin directory " + Ollivanders2.pluginDir, null, null, true);
                return;
            }

            // the previous save file could not be moved out of the way, so this save would be lost
            if (!file.createNewFile()) {
                Ollivanders2API.common.printDebugMessage("GsonDAO.writeJSON: unable to create " + saveFile + ", save data not written", null, null, true);
                return;
            }
        }
        catch (Exception e) {
            Ollivanders2API.common.printDebugMessage("GsonDAO.writeJSON: unable to create " + saveFile, e, null, true);
            return;
        }

        try (BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveFile), StandardCharsets.UTF_8))) {
            bWriter.write(json);
            bWriter.flush();
        }
        catch (Exception e) {
            Ollivanders2API.common.printDebugMessage("GsonDAO.writeJSON: unable to write " + saveFile, e, null, true);
        }
    }

    /**
     * Read json from a save file.
     *
     * @param path the name of the save file, relative to the plugin directory
     * @return the json read, or null if the file is missing, empty, or could not be read
     */
    @Nullable
    private synchronized String readJSON(@NotNull String path) {
        String saveFile = Ollivanders2.pluginDir + path;

        File file = new File(saveFile);

        try {
            if (!file.exists() || !file.canRead())
                return null;
        }
        catch (Exception e) {
            Ollivanders2API.common.printDebugMessage("GsonDAO.readJSON: unable to access " + saveFile, e, null, true);
            return null;
        }

        StringBuilder json = new StringBuilder();

        try (BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(saveFile), StandardCharsets.UTF_8))) {
            String curLine;
            while ((curLine = bReader.readLine()) != null) {
                json.append(curLine);
            }
        }
        catch (Exception e) {
            Ollivanders2API.common.printDebugMessage("GsonDAO.readJSON: unable to read " + saveFile, e, null, true);
            return null;
        }

        if (json.isEmpty())
            return null;

        return json.toString();
    }
}
