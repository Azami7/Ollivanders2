package net.pottercraft.ollivanders2.item;

import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
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
   ACONITE(Material.ALLIUM, (short) 0, "Aconite", null, null),
   ARMADILLO_BILE(Material.POTION, (short) 9, "Armadillo Bile", null, null),
   BEZOAR(Material.COAL, (short) 1, "Bezoar", null, null), // charcoal
   BILLYWIG_STING_SLIME(Material.SLIME_BALL, (short) 0, "Billywig Sting Slime", null, null),
   BLOOD(Material.POTION, (short) 7, "Blood", null, null),
   BOOM_BERRY_JUICE(Material.POTION, (short) 11, "Boom Berry Juice", null, null),
   BOOMSLANG_SKIN(Material.ROTTEN_FLESH, (short) 0, "Boomslang Skin", null, null),
   BONE(Material.BONE, (short) 0, "Bone", null, null),
   BROOMSTICK(Material.STICK, (short) 0, "Broomstick", null, ItemEnchantmentType.VOLATUS),
   CHIZPURFLE_FANGS(Material.PUMPKIN_SEEDS, (short) 0, "Chizpurfle Fangs", null, null),
   CRUSHED_FIRE_SEEDS(Material.REDSTONE, (short) 0, "Crushed Fire Seeds", null, null),
   DEATHS_HEAD_MOTH_CHRYSALIS(Material.COAL, (short) 0, "Death's Head Moth Chrysalis", null, null),
   DEW_DROP(Material.GHAST_TEAR, (short) 0, "Dew Drop", null, null),
   DITTANY(Material.BIRCH_SAPLING, (short) 0, "Dittany", null, null),
   DRAGON_BLOOD(Material.POTION, (short) 1, "Dragon Blood", null, null),
   DRAGONFLY_THORAXES(Material.BEETROOT_SEEDS, (short) 0, "Dragonfly Thoraxes", null, null),
   DRIED_NETTLES(Material.OAK_SAPLING, (short) 0, "Dried Nettles", null, null),
   EGG(Material.EGG, (short) 0, "Egg", null, null),
   ELDER_WAND(Material.BLAZE_ROD, (short) 0, "Elder Wand", "Blaze and Ender Pearl", null ),
   FAIRY_WING(Material.GOLD_NUGGET, (short) 0, "Fairy Wing", null, null),
   FLOBBERWORM_MUCUS(Material.SLIME_BALL, (short) 0, "Flobberworm Mucus", null, null),
   FLUXWEED(Material.VINE, (short) 0, "Fluxweed", null, null),
   FLOO_POWDER(Material.REDSTONE, (short) 0, "Floo Powder", "Glittery, silver powder", null),
   FULGURITE(Material.GLOWSTONE_DUST, (short) 0, "Fulgurite", null, null),
   GALANTHUS_NIVALIS(Material.AZURE_BLUET, (short) 0, "Galanthus Nivalis", null, null),
   GALLEON(Material.GOLD_INGOT, (short) 0, "Galleon", null, null),
   GINGER_ROOT(Material.BEETROOT, (short) 0, "Ginger Root", null, null),
   GROUND_DRAGON_HORN(Material.GLOWSTONE_DUST, (short) 0, "Ground Dragon Horn", null, null),
   GROUND_PORCUPINE_QUILLS(Material.GRAY_DYE, (short) 0, "Ground Porcupine Quills", null, null),
   GROUND_SCARAB_BEETLE(Material.GUNPOWDER, (short) 0, "Ground Scarab Beetle", null, null),
   GROUND_SNAKE_FANGS(Material.LIGHT_GRAY_DYE, (short) 0, "Ground Snake Fangs", null, null),
   HONEYWATER(Material.POTION, (short) 0, "Honeywater", null, null),
   HORKLUMP_JUICE(Material.DRAGON_BREATH, (short) 0, "Horklump Juice", null, null),
   HORNED_SLUG_MUCUS(Material.SLIME_BALL, (short) 0, "Horned Slug Mucus", null, null),
   HORN_OF_BICORN(Material.BLAZE_ROD, (short) 0, "Horn of Bicorn", null, null),
   INFUSION_OF_WORMWOOD(Material.POTION, (short) 5, "Infusion of Wormwood", null, null),
   INVISIBILITY_CLOAK(Material.CHAINMAIL_CHESTPLATE, (short) 0, "Cloak of Invisibility", "Silvery Transparent Cloak", null),
   JOBBERKNOLL_FEATHER(Material.FEATHER, (short) 0, "Jobberknoll Feather", null, null),
   KNOTGRASS(Material.TALL_GRASS, (short) 0, "Knotgrass", null, null),
   KNUT(Material.NETHERITE_INGOT, (short) 0, "Knut", null, null),
   LACEWING_FLIES(Material.PUMPKIN_SEEDS, (short) 0, "Lacewing Flies", null, null),
   LAVENDER_SPRIG(Material.LILAC, (short) 0, "Lavender Sprig", null, null),
   LEECHES(Material.INK_SAC, (short) 0, "Leeches", null, null),
   LETHE_RIVER_WATER(Material.POTION, (short) 0, "Lethe River Water", null, null), //bottle of water
   LIONFISH_SPINES(Material.PUMPKIN_SEEDS, (short) 0, "Lionfish Spines", null, null),
   MANDRAKE_LEAF(Material.LILY_PAD, (short) 0, "Mandrake Leaf", null, null),
   MERCURY(Material.POTION, (short) 13, "Mercury", null, null), // silver liquid
   MINT_SPRIG(Material.KELP, (short) 0, "Mint Sprig", null, null),
   MISTLETOE_BERRIES(Material.NETHER_WART, (short) 0, "Mistletoe Berries", null, null),
   MOONDEW_DROP(Material.GHAST_TEAR, (short) 0, "Moondew Drop", null, null),
   PEWTER_CAULDRON (Material.CAULDRON, (short) 0, "Pewter Cauldron", null, null),
   PLAYING_CARDS(Material.PAPER, (short) 0, "Playing Cards", null, null),
   POISONOUS_POTATO(Material.POISONOUS_POTATO, (short) 0, "Poisonous Potato", null, null),
   POWDERED_ASHPODEL_ROOT(Material.ORANGE_DYE, (short) 0, "Powdered Root of Asphodel", null, null),
   POWDERED_SAGE(Material.LIME_DYE, (short) 0, "Powdered Sage", null, null),
   ROTTEN_FLESH(Material.ROTTEN_FLESH, (short) 0, "Rotten Flesh", null, null),
   RUNESPOOR_EGG(Material.EGG, (short) 0, "Runespoor Egg", null, null),
   SALAMANDER_BLOOD(Material.POTION, (short) 7, "Salamander Blood", null, null),
   SALAMANDER_FIRE(Material.BLAZE_POWDER, (short) 0, "Salamander Fire", null, null),
   SICKLE(Material.IRON_INGOT, (short) 0, "Sickle", null, null),
   SLOTH_BRAIN(Material.FERMENTED_SPIDER_EYE, (short) 0, "Sloth Brain", null, null),
   SLOTH_BRAIN_MUCUS(Material.POTION, (short) 4, "Sloth Brain Mucus", null, null),
   SOPOPHORUS_BEAN_JUICE(Material.POTION, (short) 13, "Sopophorus Bean Juice", null, null),
   SPIDER_EYE(Material.SPIDER_EYE, (short) 0, "Spider Eye", null, null),
   STANDARD_POTION_INGREDIENT(Material.SUGAR, (short) 0, "Standard Potion Ingredient", null, null),
   TAROT_CARDS(Material.PAPER, (short) 0, "Tarot Cards", null, null),
   TEA_LEAVES(Material.GREEN_DYE, (short) 0, "Tea Leaves", null, null),
   UNICORN_HAIR(Material.STRING, (short) 0, "Unicorn Hair", null, null),
   UNICORN_HORN(Material.BLAZE_ROD, (short) 0, "Unicorn Horn", null, null),
   VALERIAN_SPRIGS(Material.VINE, (short) 0, "Valerian Sprigs", null, null),
   VALERIAN_ROOT(Material.GOLDEN_CARROT, (short) 0, "Valerian Root", null, null),
   WAND(Material.STICK, (short) 0, "Wand", null, null),
   WOLFSBANE(Material.ALLIUM, (short) 0, "Wolfsbane", null, null);

   private Material material;
   final private String name;
   final private String lore;
   final private short variant;
   final private ItemEnchantmentType itemEnchantment;

   /**
    * Constructor
    *
    * @param m the material type
    * @param v the variant information, this is 0 for most items but for things like potions it can control color
    * @param n the name of the item
    * @param l the lore for this item
    */
   O2ItemType(@NotNull Material m, short v, @NotNull String n, @Nullable String l, @Nullable ItemEnchantmentType enchantment)
   {
      material = m;
      name = n;
      variant = v;
      lore = l;
      itemEnchantment = enchantment;
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
    * @return the enchantment for this item if there is one, null otherwise
    */
   @Nullable
   public ItemEnchantmentType getItemEnchantment() { return itemEnchantment; }

   /**
    * Set the material for this item
    *
    * @param m the material type
    */
   public void setMaterial(@NotNull Material m)
   {
      material = m;
   }

   /**
    * Return the type by name
    * @param itemName the name of the item
    * @return the type if found, null otherwise
    */
   @Nullable
   public static O2ItemType getTypeByName (@NotNull String itemName)
   {
      for (O2ItemType itemType : O2ItemType.values())
      {
         if (itemType.name.equalsIgnoreCase(itemName))
            return itemType;
      }

      return null;
   }
}