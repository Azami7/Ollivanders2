package net.pottercraft.Ollivanders2.Potion;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum IngredientType
{
   ARMADILLO_BILE (Material.POTION, (short)9, "Armadillo Bile"),
   BEZOAR (Material.COAL, (short)1, "Bezoar"), // charcoal
   BONE (Material.BONE, (short)0, "Bone"),
   BLOOD (Material.POTION, (short)7, "Blood"),
   DEATHS_HEAD_MOTH_CHRYSALIS (Material.COAL, (short)0, "Death's Head Moth Chrysalis"),
   DEW_DROP (Material.GHAST_TEAR, (short)0, "Dew Drop"),
   DITTANY (Material.SAPLING, (short)2, "Dittany"), //birch sapling
   FLOBBERWORM_MUCUS (Material.SLIME_BALL, (short)0, "Flobberworm Mucus"),
   GALANTHUS_NIVALIS (Material.RED_ROSE, (short)3, "Galanthus Nivalis"), //azure bluet
   GINGER_ROOT (Material.BEETROOT, (short)0, "Ginger Root"),
   GROUND_SCARAB_BEETLE (Material.SULPHUR, (short)0, "Ground Scarab Beetle"),
   HORKLUMP_JUICE (Material.DRAGONS_BREATH, (short)0, "Horklump Juice"),
   JOBBERKNOLL_FEATHER (Material.FEATHER, (short)0, "Jobberknoll Feather"),
   LETHE_RIVER_WATER (Material.POTION, (short)0, "Lethe River Water"), //bottle of water
   LIONFISH_SPINES (Material.RAW_FISH, (short)0, "Lionfish Spines"),
   MANDRAKE_LEAF (Material.WATER_LILY, (short)0, "Mandrake Leaf"),
   MISTLETOE_BERRIES (Material.INK_SACK, (short)3, "Mistletoe Berries"),
   POISONOUS_POTATO (Material.POISONOUS_POTATO, (short)0, "Poisonous Potato"),
   POWDERED_SAGE (Material.INK_SACK, (short)10, "Powdered Sage"), //lime dye
   ROTTEN_FLESH (Material.ROTTEN_FLESH, (short)0, "Rotten Flesh"),
   RUNESPOOR_EGG (Material.EGG, (short)0, "Runespoor Egg"),
   SALAMANDER_FIRE (Material.BLAZE_POWDER, (short)0, "Salamander Fire"),
   SPIDER_EYE (Material.SPIDER_EYE, (short)0, "Spider Eye"),
   STANDARD_POTION_INGREDIENT (Material.SUGAR, (short)0, "Standard Potion Ingredient"),
   UNICORN_HAIR(Material.STRING, (short)0, "Unicorn Hair"),
   VALERIAN_SPRIGS (Material.VINE, (short)0, "Valerian Sprigs"),
   WOLFSBANE (Material.RED_ROSE, (short)2, "Wolfsbane"); // allium

   private Material material;
   private String name;
   private short variant;

   IngredientType (Material material, short variant, String name)
   {
      this.material = material;
      this.name = name;
      this.variant = variant;
   }

   public String getName ()
   {
      return name;
   }

   public Material getMaterial ()
   {
      return material;
   }

   public short getVariant ()
   {
      return variant;
   }
}