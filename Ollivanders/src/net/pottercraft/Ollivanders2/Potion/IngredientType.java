package net.pottercraft.Ollivanders2.Potion;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public enum IngredientType
{
   ACONITE (Material.SPIDER_EYE, (short)0, "Aconite"),
   ARMADILLO_BILE (Material.POTION, (short)9, "Armadillo Bile"),
   ASPHODEL (Material.DEAD_BUSH, (short)0, "Asphodel"),
   BEZOAR (Material.COAL, (short)1, "Bezoar"), // charcoal
   BILLYWIG_STING_SLIME (Material.SLIME_BALL, (short)0, "Billywig Sting Slime"),
   BOOM_BERRY_JUICE (Material.POTION, (short)11, "Boom Berry Juice"),
   BOOMSLANG_SKIN (Material.ROTTEN_FLESH, (short)0, "Boomslang Skin"),
   BONE (Material.BONE, (short)0, "Bone"),
   BLOOD (Material.POTION, (short)7, "Blood"),
   CHIZPURFLE_FANGS (Material.PUMPKIN_SEEDS, (short)0, "Chizpurfle Fangs"),
   DEATHS_HEAD_MOTH_CHRYSALIS (Material.COAL, (short)0, "Death's Head Moth Chrysalis"),
   DEW_DROP (Material.GHAST_TEAR, (short)0, "Dew Drop"),
   DITTANY (Material.SAPLING, (short)2, "Dittany"), //birch sapling
   DRAGON_BLOOD (Material.SPLASH_POTION, (short)1, "Dragon Blood"),
   DRAGONFLY_THORAXES (Material.BEETROOT_SEEDS, (short)0, "Dragonfly Thoraxes"),
   DRIED_NETTLES (Material.SAPLING, (short)0, "Dried Nettles"), // oak sapling
   FAIRY_WING (Material.GOLD_NUGGET, (short)0, "Fairy Wing"),
   FIRE_SEEDS (Material.NETHER_WARTS, (short)0, "Fire Seeds"),
   FLOBBERWORM_MUCUS (Material.SLIME_BALL, (short)0, "Flobberworm Mucus"),
   FLUXWEED (Material.DEAD_BUSH, (short)0, "Fluxweed"),
   FULGURITE (Material.GLOWSTONE_DUST, (short)0, "Fulgurite)"),
   GALANTHUS_NIVALIS (Material.RED_ROSE, (short)3, "Galanthus Nivalis"), //azure bluet
   GINGER_ROOT (Material.BEETROOT, (short)0, "Ginger Root"),
   GROUND_DRAGON_HORN (Material.GLOWSTONE_DUST, (short)0, "Ground Dragon Horn"),
   GROUND_PORCUPINE_QUILLS (Material.INK_SACK, (short)3, "Ground Porcupine Quills"),
   GROUND_SCARAB_BEETLE (Material.SULPHUR, (short)0, "Ground Scarab Beetle"),
   GROUND_SNAKE_FANGS (Material.INK_SACK, (short)15, "Ground Snake Fangs"),
   HONEYWATER (Material.POTION, (short)0, "Honeywater"),
   HORKLUMP_JUICE (Material.DRAGONS_BREATH, (short)0, "Horklump Juice"),
   HORNED_SLUG_MUCUS (Material.SLIME_BALL, (short)0, "Horned Slug Mucus"),
   HORN_OF_BICORN (Material.BLAZE_ROD, (short)0, "Horn of Bicorn"),
   INFUSION_OF_WORMWOOD (Material.POTION, (short)5, "Infusion of Wormwood"),
   JOBBERKNOLL_FEATHER (Material.FEATHER, (short)0, "Jobberknoll Feather"),
   Knotgrass (Material.LONG_GRASS, (short)0, "Knotgrass"),
   LACEWING_FLIES (Material.PUMPKIN_SEEDS, (short)0, "Lacewing Flies"),
   LAVENDER_SPRIG (Material.DOUBLE_PLANT, (short)1, "Lavender Sprig"), //lilac
   LEECHES (Material.INK_SACK, (short)0, "Leeches"),
   LETHE_RIVER_WATER (Material.POTION, (short)0, "Lethe River Water"), //bottle of water
   LIONFISH_SPINES (Material.RAW_FISH, (short)0, "Lionfish Spines"),
   MANDRAKE_LEAF (Material.WATER_LILY, (short)0, "Mandrake Leaf"),
   MERCURY (Material.INK_SACK, (short)0, "Mercury"),
   MINT_SPRIG (Material.MELON_STEM, (short)0, "Mint Sprig"),
   MISTLETOE_BERRIES (Material.INK_SACK, (short)3, "Mistletoe Berries"),
   MOONDEW_DROP (Material.GHAST_TEAR, (short)0, "Moondew Drop"),
   PHILOSOPHERS_STONE (Material.REDSTONE, (short)0, "Philosophers Stone"),
   POISONOUS_POTATO (Material.POISONOUS_POTATO, (short)0, "Poisonous Potato"),
   POWDERED_ASHPODEL (Material.INK_SACK, (short)14, "Powedered Root of Asphodel"), // orange dye
   POWDERED_SAGE (Material.INK_SACK, (short)10, "Powdered Sage"), //lime dye
   ROTTEN_FLESH (Material.ROTTEN_FLESH, (short)0, "Rotten Flesh"),
   RUNESPOOR_EGG (Material.EGG, (short)0, "Runespoor Egg"),
   SALAMANDER_BLOOD (Material.POTION, (short)7, "Salamander Blood"),
   SALAMANDER_FIRE (Material.BLAZE_POWDER, (short)0, "Salamander Fire"),
   SLOTH_BRAIN (Material.FERMENTED_SPIDER_EYE, (short)0, "Sloth Brain"),
   SLOTH_BRAIN_MUCUS (Material.POTION, (short)4, "Sloth Brain Mucus"),
   SOPOPHORUS_BEAN_JUICE (Material.POTION, (short)13, "Sopophorus Bean Juice"),
   SPIDER_EYE (Material.SPIDER_EYE, (short)0, "Spider Eye"),
   STANDARD_POTION_INGREDIENT (Material.SUGAR, (short)0, "Standard Potion Ingredient"),
   UNICORN_HAIR(Material.STRING, (short)0, "Unicorn Hair"),
   UNICORN_HORN(Material.BLAZE_ROD, (short)0, "Unicorn Horn"),
   VALERIAN_SPRIGS (Material.VINE, (short)0, "Valerian Sprigs"),
   VALERIAN_ROOT (Material.GOLDEN_CARROT, (short)0, "Valerian Root"),
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

   /**
    * Get an ingredient type by name
    *
    * @param name the name of the ingredient
    * @return the ingredient type if found, null otherwise
    */
   public static IngredientType getIngredientType (String name)
   {
      for (IngredientType i : IngredientType.values())
      {
         if (i.getName().equalsIgnoreCase(name))
            return i;
      }

      return null;
   }

   /**
    * Get a list of the names of every potion ingredient.
    *
    * @return a list of all potions ingredients
    */
   public static List<String> getAllIngredientNames ()
   {
      ArrayList<String> ingredientList = new ArrayList<>();

      for (IngredientType i : IngredientType.values())
      {
         ingredientList.add(i.getName());
      }

      return ingredientList;
   }
}
