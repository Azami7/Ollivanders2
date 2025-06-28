package net.pottercraft.ollivanders2;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.List;

import net.pottercraft.ollivanders2.house.O2HouseType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Generic data access object layer
 */
public interface GenericDAO {
    /**
     * Write house membership data
     *
     * @param map a map of players UUIDs and which house type they belong to
     */
    void writeHouses(@NotNull Map<UUID, O2HouseType> map);

    /**
     * Write house points data
     *
     * @param map a map of house type and the number of points
     */
    void writeHousePoints(@NotNull Map<O2HouseType, Integer> map);

    /**
     * Data writer
     *
     * @param map      the data to write
     * @param filename the name of the output stream
     */
    void writeSaveData(@NotNull HashMap<String, String> map, String filename);

    /**
     * Data writer
     *
     * @param map      the data to write
     * @param filename the name of the output stream
     */
    void writeSaveData(@NotNull Map<String, Map<String, String>> map, String filename);

    /**
     * Data writer
     *
     * @param list     the data to write
     * @param filename the name of the output stream
     */
    void writeSaveData(@NotNull List<Map<String, String>> list, String filename);

    /**
     * Read saved house sort data
     *
     * @return a map of player IDs and the houses they are sorted to
     */
    @Nullable
    Map<UUID, O2HouseType> readHouses();

    /**
     * Read saved house points data
     *
     * @return the house types and their points
     */
    @Nullable
    Map<O2HouseType, Integer> readHousePoints();

    /**
     * Data reader
     *
     * @param filename the input stream to read
     * @return the saved data
     */
    @Nullable
    Map<String, Map<String, String>> readSavedDataMapStringMap(@NotNull String filename);

    /**
     * Data reader
     *
     * @param filename the input stream to read
     * @return the saved data
     */
    @Nullable
    List<Map<String, String>> readSavedDataListMap(@NotNull String filename);
}
