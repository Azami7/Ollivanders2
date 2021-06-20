package net.pottercraft.ollivanders2.player;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Wand cores
 */
public enum O2WandCoreType
{
   SPIDER_EYE(Material.SPIDER_EYE, "Spider Eye"),
   BONE(Material.BONE, "Bone"),
   ROTTEN_FLESH(Material.ROTTEN_FLESH, "Rotten Flesh"),
   GUNPOWDER(Material.GUNPOWDER, "Gunpowder");

   final Material material;
   final String label;

   O2WandCoreType(@NotNull Material m, @NotNull String l)
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
    * Get the wand core type for this material.
    *
    * @param m the material to check
    * @return the wand core type if found, null otherwise
    */
   @Nullable
   public static O2WandCoreType getWandCoreTypeByMaterial(@NotNull Material m)
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
   @NotNull
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
   @NotNull
   public static ArrayList<Material> getAllCoresByMaterial()
   {
      ArrayList<Material> cores = new ArrayList<>();

      for (O2WandCoreType coreType : O2WandCoreType.values())
         cores.add(coreType.getMaterial());

      return cores;
   }

   /**
    * Get a random wand core by name
    *
    * @return a random wand core name
    */
   @NotNull
   public static String getRandomCoreByName()
   {
      int rand = Ollivanders2Common.random.nextInt();
      List<String> cores = getAllCoresByName();

      int index = Math.abs(rand % cores.size());
      return cores.get(index);
   }
}
