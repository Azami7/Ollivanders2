package net.pottercraft.Ollivanders2.Player;

import org.bukkit.Material;

import java.util.ArrayList;

public enum O2WandWoodType
{
   SPRUCE (Material.SPRUCE_LOG, "Spruce"),
   JUNGLE (Material.JUNGLE_LOG, "Jungle"),
   BIRCH (Material.BIRCH_LOG, "Birch"),
   OAK (Material.OAK_LOG, "Oak");

   Material material;
   String label;

   O2WandWoodType (Material m, String l)
   {
      material = m;
      label = l;
   }

   public Material getMaterial ()
   {
      return material;
   }

   public String getLabel ()
   {
      return label;
   }

   /**
    * Get the wand wood type for this material.
    *
    * @param m the material to check
    * @return the wand core type if found, null otherwise
    */
   public static O2WandWoodType getWandWoodTypeByMaterial (Material m)
   {
      for (O2WandWoodType woodType : O2WandWoodType.values())
      {
         if (woodType.material == m)
            return woodType;
      }

      return null;
   }

   /**
    * Get a list of all the wand wood types by name.
    *
    * @return the names of all wand woods as a list
    */
   public static ArrayList<String> getAllWoodsByName ()
   {
      ArrayList<String> woods = new ArrayList<>();

      for (O2WandWoodType woodType : O2WandWoodType.values())
         woods.add(woodType.getLabel());

      return woods;
   }
}
