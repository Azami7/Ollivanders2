package net.pottercraft.Ollivanders2;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.List;
import net.pottercraft.Ollivanders2.House.O2HouseType;

public interface GenericDAO
{
   void writeHouses (Map<UUID, O2HouseType> map);

   void writeHousePoints (Map<O2HouseType, Integer> map);

   void writeSaveData (HashMap<String, String> map, String filename);

   void writeSaveData (Map<String, Map<String, String>> map, String filename);

   void writeSaveData (List<Map<String, String>> list, String filename);

   Map<UUID, O2HouseType> readHouses ();

   Map<O2HouseType, Integer> readHousePoints ();

   Map<String, Map<String, String>> readSavedDataMapStringMap (String filename);

   List<Map<String, String>> readSavedDataListMap (String filename);
}
