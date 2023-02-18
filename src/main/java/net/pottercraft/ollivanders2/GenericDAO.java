package net.pottercraft.ollivanders2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.pottercraft.ollivanders2.house.O2HouseType;

public interface GenericDAO {
	void writeHouses(@NotNull Map<UUID, O2HouseType> map);

	void writeHousePoints(@NotNull Map<O2HouseType, Integer> map);

	void writeSaveData(@NotNull HashMap<String, String> map, String filename);

	void writeSaveData(@NotNull Map<String, Map<String, String>> map, String filename);

	void writeSaveData(@NotNull List<Map<String, String>> list, String filename);

	@Nullable
	Map<UUID, O2HouseType> readHouses();

	@Nullable
	Map<O2HouseType, Integer> readHousePoints();

	@Nullable
	Map<String, Map<String, String>> readSavedDataMapStringMap(@NotNull String filename);

	@Nullable
	List<Map<String, String>> readSavedDataListMap(@NotNull String filename);
}
