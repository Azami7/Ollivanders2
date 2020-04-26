package net.pottercraft.ollivanders2.player;

import org.bukkit.Material;

import java.util.ArrayList;

public enum O2WandCoreType
{
   SPIDER_EYE (Material.SPIDER_EYE, "Spider Eye"),
   BONE (Material.BONE, "Bone"),
   ROTTEN_FLESH (Material.ROTTEN_FLESH, "Rotten Flesh"),
   GUNPOWDER (Material.GUNPOWDER, "Gunpowder");

   Material material;
   String label;

   O2WandCoreType (Material m, String l)
   {
      material = m;
      label = l;
   }

   public Material getMaterial ()
   {
      return material;
   }

   public String getLabel() { return label; }

   /**
    * Get the wand core type for this material.
    *
    * @param m the material to check
    * @return the wand core type if found, null otherwise
    */
   public static O2WandCoreType getWandCoreTypeByMaterial (Material m)
   {
      for (O2WandCoreType coreType : O2WandCoreType.values())
      {
         if (coreType.material == m)
            return coreType;
      }

      return null;
   }

   /**
    * Get a list of all the wand core types by name.
    *
    * @return the names of all wand cores as a list
    */
   public static ArrayList<String> getAllCoresByName()
   {
      ArrayList<String> cores = new ArrayList<>();

      for (O2WandCoreType coreType : O2WandCoreType.values())
         cores.add(coreType.getLabel());

      return cores;
   }

   /**
    * Get a list of all the wand core types by material type.
    *
    * @return the names of all wand cores as a list
    */
   public static ArrayList<Material> getAllCoresByMaterial()
   {
      ArrayList<Material> cores = new ArrayList<>();

      for (O2WandCoreType coreType : O2WandCoreType.values())
         cores.add(coreType.getMaterial());

      return cores;
   }
}
