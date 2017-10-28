package net.pottercraft.Ollivanders2;

import java.util.Map;
import java.util.UUID;
import net.pottercraft.Ollivanders2.House.O2Houses;

public interface DataPersistenceLayer
{
   void writeHouses (Map<UUID, O2Houses.O2HouseType> map);

   void writeHousePoints (Map<O2Houses.O2HouseType, Integer> map);

   void writeO2Players (Map <String, Map<String, String>> map);

   Map<UUID, O2Houses.O2HouseType> readHouses ();

   Map<O2Houses.O2HouseType, Integer> readHousePoints ();

   Map <String, Map<String, String>> readO2Players ();
}
