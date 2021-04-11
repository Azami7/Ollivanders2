package net.pottercraft.ollivanders2.item;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * All custom special items in Ollivanders2
 *
 * @author Azami7
 * @since 2.2.9
 */
public enum O2ItemType
{
   ACONITE(Material.ALLIUM, (short) 0, "Aconite", null),
   ARMADILLO_BILE(Material.POTION, (short) 9, "Armadillo Bile", null),
   BEZOAR(Material.COAL, (short) 1, "Bezoar", null), // charcoal
   BILLYWIG_STING_SLIME(Material.SLIME_BALL, (short) 0, "Billywig Sting Slime", null),
   BLOOD(Material.POTION, (short) 7, "Blood", null),
   BOOM_BERRY_JUICE(Material.POTION, (short) 11, "Boom Berry Juice", null),
   BOOMSLANG_SKIN(Material.ROTTEN_FLESH, (short) 0, "Boomslang Skin", null),
   BONE(Material.BONE, (short) 0, "Bone", null),
   BROOMSTICK(Material.STICK, (short) 0, "Broomstick", "Flying vehicle used by magical folk"),
   CHIZPURFLE_FANGS(Material.PUMPKIN_SEEDS, (short) 0, "Chizpurfle Fangs", null),
   CRUSHED_FIRE_SEEDS(Material.REDSTONE, (short) 0, "Crushed Fire Seeds", null),
   DEATHS_HEAD_MOTH_CHRYSALIS(Material.COAL, (short) 0, "Death's Head Moth Chrysalis", null),
   DEW_DROP(Material.GHAST_TEAR, (short) 0, "Dew Drop", null),
   DITTANY(Material.BIRCH_SAPLING, (short) 0, "Dittany", null),
   DRAGON_BLOOD(Material.POTION, (short) 1, "Dragon Blood", null),
   DRAGONFLY_THORAXES(Material.BEETROOT_SEEDS, (short) 0, "Dragonfly Thoraxes", null),
   DRIED_NETTLES(Material.OAK_SAPLING, (short) 0, "Dried Nettles", null),
   EGG(Material.EGG, (short) 0, "Egg", null),
   ELDER_WAND(Material.BLAZE_ROD, (short) 0, "Elder Wand", "Blaze and Ender Pearl"),
   FAIRY_WING(Material.GOLD_NUGGET, (short) 0, "Fairy Wing", null),
   FLOBBERWORM_MUCUS(Material.SLIME_BALL, (short) 0, "Flobberworm Mucus", null),
   FLUXWEED(Material.VINE, (short) 0, "Fluxweed", null),
   FLOO_POWDER(Material.REDSTONE, (short) 0, "Floo Powder", "Glittery, silver powder"),
   FULGURITE(Material.GLOWSTONE_DUST, (short) 0, "Fulgurite", null),
   GALANTHUS_NIVALIS(Material.AZURE_BLUET, (short) 0, "Galanthus Nivalis", null),
   GALLEON(Material.GOLD_INGOT, (short) 0, "Galleon", null),
   GINGER_ROOT(Material.BEETROOT, (short) 0, "Ginger Root", null),
   GROUND_DRAGON_HORN(Material.GLOWSTONE_DUST, (short) 0, "Ground Dragon Horn", null),
   GROUND_PORCUPINE_QUILLS(Material.GRAY_DYE, (short) 0, "Ground Porcupine Quills", null),
   GROUND_SCARAB_BEETLE(Material.GUNPOWDER, (short) 0, "Ground Scarab Beetle", null),
   GROUND_SNAKE_FANGS(Material.LIGHT_GRAY_DYE, (short) 0, "Ground Snake Fangs", null),
   HONEYWATER(Material.POTION, (short) 0, "Honeywater", null),
   HORKLUMP_JUICE(Material.DRAGON_BREATH, (short) 0, "Horklump Juice", null),
   HORNED_SLUG_MUCUS(Material.SLIME_BALL, (short) 0, "Horned Slug Mucus", null),
   HORN_OF_BICORN(Material.BLAZE_ROD, (short) 0, "Horn of Bicorn", null),
   INFUSION_OF_WORMWOOD(Material.POTION, (short) 5, "Infusion of Wormwood", null),
   INVISIBILITY_CLOAK(Material.CHAINMAIL_CHESTPLATE, (short) 0, "Cloak of Invisibility", "Silvery Transparent Cloak"),
   JOBBERKNOLL_FEATHER(Material.FEATHER, (short) 0, "Jobberknoll Feather", null),
   KNOTGRASS(Material.TALL_GRASS, (short) 0, "Knotgrass", null),
   KNUT(Material.NETHERITE_INGOT, (short) 0, "Knut", null),
   LACEWING_FLIES(Material.PUMPKIN_SEEDS, (short) 0, "Lacewing Flies", null),
   LAVENDER_SPRIG(Material.LILAC, (short) 0, "Lavender Sprig", null),
   LEECHES(Material.INK_SAC, (short) 0, "Leeches", null),
   LETHE_RIVER_WATER(Material.POTION, (short) 0, "Lethe River Water", null), //bottle of water
   LIONFISH_SPINES(Material.PUMPKIN_SEEDS, (short) 0, "Lionfish Spines", null),
   MANDRAKE_LEAF(Material.LILY_PAD, (short) 0, "Mandrake Leaf", null),
   MERCURY(Material.POTION, (short) 13, "Mercury", null), // silver liquid
   MINT_SPRIG(Material.MELON_STEM, (short) 0, "Mint Sprig", null),
   MISTLETOE_BERRIES(Material.NETHER_WART, (short) 0, "Mistletoe Berries", null),
   MOONDEW_DROP(Material.GHAST_TEAR, (short) 0, "Moondew Drop", null),
   PEWTER_CAULDRON (Material.CAULDRON, (short) 0, "Pewter Cauldron", null),
   PLAYING_CARDS(Material.PAPER, (short) 0, "Playing Cards", null),
   POISONOUS_POTATO(Material.POISONOUS_POTATO, (short) 0, "Poisonous Potato", null),
   POWDERED_ASHPODEL_ROOT(Material.ORANGE_DYE, (short) 0, "Powedered Root of Asphodel", null),
   POWDERED_SAGE(Material.LIME_DYE, (short) 0, "Powdered Sage", null),
   ROTTEN_FLESH(Material.ROTTEN_FLESH, (short) 0, "Rotten Flesh", null),
   RUNESPOOR_EGG(Material.EGG, (short) 0, "Runespoor Egg", null),
   SALAMANDER_BLOOD(Material.POTION, (short) 7, "Salamander Blood", null),
   SALAMANDER_FIRE(Material.BLAZE_POWDER, (short) 0, "Salamander Fire", null),
   SICKLE(Material.IRON_INGOT, (short) 0, "Sickle", null),
   SLOTH_BRAIN(Material.FERMENTED_SPIDER_EYE, (short) 0, "Sloth Brain", null),
   SLOTH_BRAIN_MUCUS(Material.POTION, (short) 4, "Sloth Brain Mucus", null),
   SOPOPHORUS_BEAN_JUICE(Material.POTION, (short) 13, "Sopophorus Bean Juice", null),
   SPIDER_EYE(Material.SPIDER_EYE, (short) 0, "Spider Eye", null),
   STANDARD_POTION_INGREDIENT(Material.SUGAR, (short) 0, "Standard Potion Ingredient", null),
   TAROT_CARDS(Material.PAPER, (short) 0, "Tarot Cards", null),
   TEA_LEAVES(Material.GREEN_DYE, (short) 0, "Tea Leaves", null),
   UNICORN_HAIR(Material.STRING, (short) 0, "Unicorn Hair", null),
   UNICORN_HORN(Material.BLAZE_ROD, (short) 0, "Unicorn Horn", null),
   VALERIAN_SPRIGS(Material.VINE, (short) 0, "Valerian Sprigs", null),
   VALERIAN_ROOT(Material.GOLDEN_CARROT, (short) 0, "Valerian Root", null),
   WAND(Material.STICK, (short) 0, "Wand", null),
   WOLFSBANE(Material.ALLIUM, (short) 0, "Wolfsbane", null);

   private Material material;
   final private String name;
   final private String lore;
   final private short variant;

   /**
    * Constructor
    *
    * @param m the material type
    * @param v the variant information, this is 0 for most items but for things like potions it can control color
    * @param n the name of the item
    * @param l the lore for this item
    */
   O2ItemType(@NotNull Material m, short v, @NotNull String n, @Nullable String l)
   {
      material = m;
      name = n;
      variant = v;
      lore = l;
   }

   /**
    * @return the name of this item
    */
   @NotNull
   public String getName()
   {
      return name;
   }

   /**
    * @return the lore for this item or its name if there is no lore
    */
   @NotNull
   public String getLore()
   {
      if (lore == null)
      {
         return name;
      }
      else
      {
         return lore;
      }
   }

   /**
    * @return the variant for this item
    */
   public short getVariant()
   {
      return variant;
   }

   /**
    * @return the material type for this item
    */
   @NotNull
   public Material getMaterial()
   {
      return material;
   }

   /**
    * Set the material for this item
    *
    * @param m the material type
    */
   public void setMaterial(@NotNull Material m)
   {
      material = m;
   }
}