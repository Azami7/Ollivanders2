package net.pottercraft.ollivanders2.player;

import net.pottercraft.ollivanders2.Ollivanders2Common;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Wand wood types
 */
public enum O2WandWoodType
{
   SPRUCE(Material.SPRUCE_LOG, "Spruce"),
   JUNGLE(Material.JUNGLE_LOG, "Jungle"),
   BIRCH(Material.BIRCH_LOG, "Birch"),
   OAK(Material.OAK_LOG, "Oak");

   final Material material;
   final String label;

   O2WandWoodType(@NotNull Material m, @NotNull String l)
   {
      material = m;
      label = l;
   }

   /**
    * Get the material for this type
    *
    * @return the material
    */
   @NotNull
   public Material getMaterial()
   {
      return material;
   }

   /**
    * Get the label for this type
    *
    * @return the label
    */
   @NotNull
   public String getLabel()
   {
      return label;
   }

   /**
    * Get the wand wood type for this material.
    *
    * @param m the material to check
    * @return the wand core type if found, null otherwise
    */
   @Nullable
   public static O2WandWoodType getWandWoodTypeByMaterial(@NotNull Material m)
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
   @NotNull
   public static ArrayList<String> getAllWoodsByName ()
   {
      ArrayList<String> woods = new ArrayList<>();

      for (O2WandWoodType woodType : O2WandWoodType.values())
         woods.add(woodType.getLabel());

      return woods;
   }

   /**
    * Get a random wand wood by name
    *
    * @return a random wand wood name
    */
   @NotNull
   public static String getRandomWoodByName()
   {
      int rand = Ollivanders2Common.random.nextInt();
      List<String> woods = getAllWoodsByName();

      int index = Math.abs(rand % woods.size());
      return woods.get(index);
   }
}
