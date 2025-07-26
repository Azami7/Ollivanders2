package net.pottercraft.ollivanders2.item;

import net.pottercraft.ollivanders2.common.O2Color;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * All custom special items in Ollivanders2
 */
public enum O2ItemType {
    /**
     * potion ingredient
     */
    ACONITE(Material.ALLIUM, (short) 0, "Aconite", null, null),
    /**
     * potion ingredient
     */
    ARMADILLO_BILE(Material.POTION, (short) 9, "Armadillo Bile", null, null),
    /**
     * potion ingredient
     */
    ASPHODEL(Material.LILY_OF_THE_VALLEY, (short) 0, "Asphodel", null, null),
    /**
     * unenchanted broomstick
     */
    BASIC_BROOM(Material.STICK, (short) 0, "Broomstick", null, null),
    /**
     * potion ingredient
     */
    BEZOAR(Material.COAL, (short) 1, "Bezoar", null, null), // charcoal
    /**
     * potion ingredient
     */
    BILLYWIG_STING_SLIME(Material.SLIME_BALL, (short) 0, "Billywig Sting Slime", null, null),
    /**
     * potion ingredient
     */
    BLOOD(Material.POTION, (short) 7, "Blood", null, null),
    /**
     * potion ingredient
     */
    BOOM_BERRY_JUICE(Material.POTION, (short) 11, "Boom Berry Juice", null, null),
    /**
     * potion ingredient
     */
    BOOMSLANG_SKIN(Material.ROTTEN_FLESH, (short) 0, "Boomslang Skin", null, null),
    /**
     * potion ingredient
     */
    BONE(Material.BONE, (short) 0, "Bone", null, null),
    /**
     * enchanted broomstick
     */
    BROOMSTICK(Material.STICK, (short) 0, "Broomstick", null, ItemEnchantmentType.VOLATUS),
    /**
     * potion ingredient
     */
    CHIZPURFLE_FANGS(Material.PUMPKIN_SEEDS, (short) 0, "Chizpurfle Fangs", null, null),
    /**
     * potion ingredient
     */
    CRUSHED_CATS_EYE_OPAL(Material.ORANGE_DYE, (short) 0, "Crushed Cat's Eye Opal", null, null),
    /**
     * potion ingredient
     */
    CRUSHED_FIRE_SEEDS(Material.REDSTONE, (short) 0, "Crushed Fire Seeds", null, null),
    /**
     * potion ingredient
     */
    DEATHS_HEAD_MOTH_CHRYSALIS(Material.COAL, (short) 0, "Death's Head Moth Chrysalis", null, null),
    /**
     * potion ingredient
     */
    DEW_DROP(Material.GHAST_TEAR, (short) 0, "Dew Drop", null, null),
    /**
     * potion ingredient
     */
    DITTANY(Material.BIRCH_SAPLING, (short) 0, "Dittany", null, null),
    /**
     * potion ingredient
     */
    DRAGON_BLOOD(Material.POTION, (short) 1, "Dragon Blood", null, null),
    /**
     * Wand core
     */
    DRAGON_HEARTSTRING(Material.FERMENTED_SPIDER_EYE, (short) 0, "Dragon Heartstring", null, null),
    /**
     * potion ingredient
     */
    DRAGONFLY_THORAXES(Material.BEETROOT_SEEDS, (short) 0, "Dragonfly Thoraxes", null, null),
    /**
     * potion ingredient
     */
    DRIED_NETTLES(Material.OAK_SAPLING, (short) 0, "Dried Nettles", null, null),
    /**
     * divination object
     */
    EGG(Material.EGG, (short) 0, "Egg", null, null),
    /**
     * the elder wand
     */
    ELDER_WAND(Material.BLAZE_ROD, (short) 0, "Elder Wand", "Blaze and Ender Pearl", null),
    /**
     * potion ingredient
     */
    FAIRY_WING(Material.GOLD_NUGGET, (short) 0, "Fairy Wing", null, null),
    /**
     * potion ingredient
     */
    FLOBBERWORM_MUCUS(Material.SLIME_BALL, (short) 0, "Flobberworm Mucus", null, null),
    /**
     * potion ingredient
     */
    FLUXWEED(Material.VINE, (short) 0, "Fluxweed", null, null),
    /**
     * floo powder
     */
    FLOO_POWDER(Material.REDSTONE, (short) 0, "Floo Powder", "Glittery, silver powder", null),
    /**
     * potion ingredient
     */
    FULGURITE(Material.GLOWSTONE_DUST, (short) 0, "Fulgurite", null, null),
    /**
     * potion ingredient
     */
    GALANTHUS_NIVALIS(Material.AZURE_BLUET, (short) 0, "Galanthus Nivalis", null, null),
    /**
     * wizard money
     */
    GALLEON(Material.GOLD_INGOT, (short) 0, "Galleon", "Galleon", null),
    /**
     * potion ingredient
     */
    GINGER_ROOT(Material.BEETROOT, (short) 0, "Ginger Root", null, null),
    /**
     * potion ingredient
     */
    GROUND_DRAGON_HORN(Material.GLOWSTONE_DUST, (short) 0, "Ground Dragon Horn", null, null),
    /**
     * potion ingredient
     */
    GROUND_PORCUPINE_QUILLS(Material.GRAY_DYE, (short) 0, "Ground Porcupine Quills", null, null),
    /**
     * potion ingredient
     */
    GROUND_SCARAB_BEETLE(Material.GUNPOWDER, (short) 0, "Ground Scarab Beetle", null, null),
    /**
     * potion ingredient
     */
    GROUND_SNAKE_FANGS(Material.LIGHT_GRAY_DYE, (short) 0, "Ground Snake Fangs", null, null),
    /**
     * potion ingredient
     */
    HONEYWATER(Material.POTION, (short) 0, "Honeywater", null, null),
    /**
     * potion ingredient
     */
    HORKLUMP_JUICE(Material.DRAGON_BREATH, (short) 0, "Horklump Juice", null, null),
    /**
     * potion ingredient
     */
    HORNED_SLUG_MUCUS(Material.SLIME_BALL, (short) 0, "Horned Slug Mucus", null, null),
    /**
     * potion ingredient
     */
    HORN_OF_BICORN(Material.BLAZE_ROD, (short) 0, "Horn of Bicorn", null, null),
    /**
     * potion ingredient
     */
    INFUSION_OF_WORMWOOD(Material.POTION, (short) 5, "Infusion of Wormwood", null, null),
    /**
     * an invisibility cloak
     */
    INVISIBILITY_CLOAK(Material.CHAINMAIL_CHESTPLATE, (short) 0, "Cloak of Invisibility", "Silvery Transparent Cloak", null),
    /**
     * potion ingredient
     */
    JOBBERKNOLL_FEATHER(Material.FEATHER, (short) 0, "Jobberknoll Feather", null, null),
    /**
     * wand core
     */
    KELPIE_HAIR(Material.PALE_HANGING_MOSS, (short) 0, "Kelpie Hair", null, null),
    /**
     * potion ingredient
     */
    KNOTGRASS(Material.TALL_GRASS, (short) 0, "Knotgrass", null, null),
    /**
     * wizard money
     */
    KNUT(Material.NETHERITE_INGOT, (short) 0, "Knut", "Knut", null),
    /**
     * potion ingredient
     */
    LACEWING_FLIES(Material.PUMPKIN_SEEDS, (short) 0, "Lacewing Flies", null, null),
    /**
     * potion ingredient
     */
    LAVENDER_SPRIG(Material.LILAC, (short) 0, "Lavender Sprig", null, null),
    /**
     * potion ingredient
     */
    LEECHES(Material.INK_SAC, (short) 0, "Leeches", null, null),
    /**
     * potion ingredient
     */
    LETHE_RIVER_WATER(Material.POTION, (short) 0, "Lethe River Water", null, null), //bottle of water
    /**
     * potion ingredient
     */
    LIONFISH_SPINES(Material.PUMPKIN_SEEDS, (short) 0, "Lionfish Spines", null, null),
    /**
     * potion ingredient
     */
    MANDRAKE_LEAF(Material.LILY_PAD, (short) 0, "Mandrake Leaf", null, null),
    /**
     * potion ingredient
     */
    MERCURY(Material.POTION, (short) 13, "Mercury", null, null), // silver liquid
    /**
     * potion ingredient
     */
    MINT_SPRIG(Material.KELP, (short) 0, "Mint Sprig", null, null),
    /**
     * potion ingredient
     */
    MISTLETOE_BERRIES(Material.NETHER_WART, (short) 0, "Mistletoe Berries", null, null),
    /**
     * potion ingredient
     */
    MOONCALF_MILK(Material.POTION, (short) -1, "Moonclaf Milk", null, null),
    /**
     * potion ingredient
     */
    MOONDEW_DROP(Material.GHAST_TEAR, (short) 0, "Moondew Drop", null, null),
    /**
     * cauldron for making potions
     */
    PEWTER_CAULDRON(Material.CAULDRON, (short) 0, "Pewter Cauldron", null, null),
    /**
     * wand core
     */
    PHOENIX_FEATHER(Material.FEATHER, (short) 0, "Phoenix Feather", null, null),
    /**
     * divination object
     */
    PLAYING_CARDS(Material.PAPER, (short) 0, "Playing Cards", null, null),
    /**
     * potion ingredient
     */
    POISONOUS_POTATO(Material.POISONOUS_POTATO, (short) 0, "Poisonous Potato", null, null),
    /**
     * potion ingredient
     */
    POWDERED_ASHPODEL_ROOT(Material.ORANGE_DYE, (short) 0, "Powdered Root of Asphodel", null, null),
    /**
     * potion ingredient
     */
    POWDERED_SAGE(Material.LIME_DYE, (short) 0, "Powdered Sage", null, null),
    /**
     * potion ingredient
     */
    ROTTEN_FLESH(Material.ROTTEN_FLESH, (short) 0, "Rotten Flesh", null, null),
    /**
     * potion ingredient
     */
    RUNESPOOR_EGG(Material.EGG, (short) 0, "Runespoor Egg", null, null),
    /**
     * potion ingredient
     */
    SALAMANDER_BLOOD(Material.POTION, (short) 7, "Salamander Blood", null, null),
    /**
     * potion ingredient
     */
    SALAMANDER_FIRE(Material.BLAZE_POWDER, (short) 0, "Salamander Fire", null, null),
    /**
     * wizard money
     */
    SICKLE(Material.IRON_INGOT, (short) 0, "Sickle", "Sickle", null),
    /**
     * potion ingredient
     */
    SLOTH_BRAIN(Material.FERMENTED_SPIDER_EYE, (short) 0, "Sloth Brain", null, null),
    /**
     * potion ingredient
     */
    SLOTH_BRAIN_MUCUS(Material.POTION, (short) 4, "Sloth Brain Mucus", null, null),
    /**
     * potion ingredient
     */
    SOPOPHORUS_BEAN_JUICE(Material.POTION, (short) 13, "Sopophorus Bean Juice", null, null),
    /**
     * potion ingredient
     */
    SPIDER_EYE(Material.SPIDER_EYE, (short) 0, "Spider Eye", null, null),
    /**
     * potion ingredient
     */
    STANDARD_POTION_INGREDIENT(Material.SUGAR, (short) 0, "Standard Potion Ingredient", null, null),
    /**
     * divination object
     */
    TAROT_CARDS(Material.PAPER, (short) 0, "Tarot Cards", null, null),
    /**
     * divination object
     */
    TEA_LEAVES(Material.GREEN_DYE, (short) 0, "Tea Leaves", null, null),
    /**
     * potion ingredient, wand core
     */
    UNICORN_HAIR(Material.STRING, (short) 0, "Unicorn Hair", null, null),
    /**
     * potion ingredient
     */
    UNICORN_HORN(Material.BLAZE_ROD, (short) 0, "Unicorn Horn", null, null),
    /**
     * potion ingredient
     */
    VALERIAN_SPRIGS(Material.VINE, (short) 0, "Valerian Sprigs", null, null),
    /**
     * potion ingredient
     */
    VALERIAN_ROOT(Material.GOLDEN_CARROT, (short) 0, "Valerian Root", null, null),
    /**
     * wand core
     */
    VEELA_HAIR(Material.STRING, (short) 0, "Veela Hair", null, null),
    /**
     * unenchanted wand
     */
    WAND(Material.STICK, (short) 0, "Wand", null, null),
    /**
     * potion ingredient
     */
    WOLFSBANE(Material.ALLIUM, (short) 0, "Wolfsbane", null, null);

    /**
     * Item material
     */
    private Material material;

    /**
     * Item name
     */
    final private String name;

    /**
     * Item lore, optional
     */
    final private String lore;

    /**
     * Item variant
     */
    final private short variant;

    /**
     * Item enchantment
     */
    final private ItemEnchantmentType itemEnchantment;

    /**
     * Constructor
     *
     * @param material    the material type
     * @param variant     the variant information, this is 0 for most items but for things like potions it can control color
     * @param name        the name of the item
     * @param lore        the lore for this item
     * @param enchantment the optional enchantment for this item
     */
    O2ItemType(@NotNull Material material, short variant, @NotNull String name, @Nullable String lore, @Nullable ItemEnchantmentType enchantment) {
        this.material = material;
        this.name = name;
        this.variant = variant;
        this.lore = lore;
        this.itemEnchantment = enchantment;
    }

    /**
     * get the name of this item
     * @return the name of this item
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * get the lore for this item
     * @return the lore for this item or its name if there is no lore
     */
    @NotNull
    public String getLore() {
        if (lore == null)
            return name;
        else
            return lore;
    }

    /**
     * get the material for this item
     * @return the material type for this item
     */
    @NotNull
    public Material getMaterial() {
        return material;
    }

    /**
     * get the enchantment on this item
     * @return the enchantment for this item if there is one, null otherwise
     */
    @Nullable
    public ItemEnchantmentType getItemEnchantment() {
        return itemEnchantment;
    }

    /**
     * Set the material for this item
     *
     * @param m the material type
     */
    public void setMaterial(@NotNull Material m) {
        material = m;
    }

    /**
     * Get the item type by name
     *
     * @param itemName the name of the item
     * @return the type if found, null otherwise
     */
    @Nullable
    public static O2ItemType getTypeByName(@NotNull String itemName) {
        for (O2ItemType itemType : O2ItemType.values()) {
            if (itemType.name.equalsIgnoreCase(itemName))
                return itemType;
        }

        return null;
    }

    /**
     * Is the specified itemstack this item type
     *
     * @param item the item to check
     * @return true if it is this type, false otherwise
     */
    public boolean isItemThisType(@NotNull Item item) {
        return isItemThisType(item.getItemStack());
    }

    /**
     * Is the specified itemstack this item type
     *
     * @param itemStack the item stack to check
     * @return true if it is this type, false otherwise
     */
    public boolean isItemThisType(@NotNull ItemStack itemStack) {
        // check item type
        if (itemStack.getType() != material)
            return false;

        // check item NBT
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            Ollivanders2API.common.printDebugMessage("Item meta is null", null, null, true);
            return false;
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(O2Items.o2ItemTypeKey, PersistentDataType.STRING)) {
            String itemTypeKey = container.get(O2Items.o2ItemTypeKey, PersistentDataType.STRING);
            if (itemTypeKey == null)
                return false;

            if (!(itemTypeKey.equalsIgnoreCase(name)))
                return false;
        }

        return true;
    }

    /**
     * Get an ItemStack of this item type
     *
     * @param amount the amount of items in the stack
     * @return an ItemStack of this item
     */
    @Nullable
    public ItemStack getItem(int amount) {
        ItemStack o2Item = new ItemStack(material, amount);

        ItemMeta meta = o2Item.getItemMeta();
        if (meta == null)
            return null;

        // set name
        meta.setDisplayName(name);

        // set lore
        if (lore != null) {
            ArrayList<String> itemLore = new ArrayList<>();
            itemLore.add(getLore());
            meta.setLore(itemLore);
        }

        // if potion, set potion meta
        if (material == Material.POTION) {
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            ((PotionMeta) meta).setColor(O2Color.getBukkitColorByNumber(variant).getBukkitColor());
        }

        // add custom NBT tag
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(O2Items.o2ItemTypeKey, PersistentDataType.STRING, name);

        o2Item.setItemMeta(meta);

        return o2Item;
    }
}