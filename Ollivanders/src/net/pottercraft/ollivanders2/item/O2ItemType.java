package net.pottercraft.ollivanders2.item;

import net.pottercraft.ollivanders2.common.TimeCommon;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.Color;
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
    ACONITE(Material.ALLIUM, null, "Aconite", null, null),
    /**
     * potion ingredient
     */
    ARMADILLO_BILE(Material.POTION, Color.OLIVE, "Armadillo Bile", null, null),
    /**
     * potion ingredient
     */
    ASPHODEL(Material.LILY_OF_THE_VALLEY, null, "Asphodel", null, null),
    /**
     * unenchanted broomstick
     */
    BASIC_BROOM(Material.STICK, null, "Broom", null, null),
    /**
     * potion ingredient
     */
    BAT_SPLEEN(Material.BEEF, null, "Bat Spleen", null, null),
    /**
     * potion ingredient
     */
    BEZOAR(Material.COAL, null, "Bezoar", null, null), // charcoal
    /**
     * potion ingredient
     */
    BILLYWIG_STING_SLIME(Material.SLIME_BALL, null, "Billywig Sting Slime", null, null),
    /**
     * potion ingredient
     */
    BLOOD(Material.POTION, Color.fromRGB(120, 6, 6), "Blood", null, null),
    /**
     * potion ingredient
     */
    BOOM_BERRY_JUICE(Material.POTION, Color.fromRGB(70, 65, 150), "Boom Berry Juice", null, null),
    /**
     * potion ingredient
     */
    BOOMSLANG_SKIN(Material.ROTTEN_FLESH, null, "Boomslang Skin", null, null),
    /**
     * potion ingredient
     */
    BONE(Material.BONE, null, "Bone", null, null),
    /**
     * enchanted broomstick
     */
    BROOMSTICK(Material.STICK, null, "Broomstick", null, ItemEnchantmentType.VOLATUS),
    /**
     * potion ingredient
     */
    BURSTING_MUSHROOM(Material.CRIMSON_FUNGUS, null, "Bursting Mushroom", null, null),
    /**
     * potion ingredient
     */
    CHIZPURFLE_FANGS(Material.PUMPKIN_SEEDS, null, "Chizpurfle Fangs", null, null),
    /**
     * potion ingredient
     */
    CHOPPED_MALLOW_LEAVES(Material.GREEN_DYE, null, "Chopped Mallow Leaves", null, null),
    /**
     * potion ingredient
     */
    CRUSHED_CATS_EYE_OPAL(Material.ORANGE_DYE, null, "Crushed Cat's Eye Opal", null, null),
    /**
     * potion ingredient
     */
    CRUSHED_FIRE_SEEDS(Material.REDSTONE, null, "Crushed Fire Seeds", null, null),
    /**
     * potion ingredient
     */
    CRUSHED_GURDYROOT(Material.BROWN_DYE, null, "Crushed Gurdyroot", null, null),
    /**
     * potion ingredient
     */
    DAISY_ROOTS(Material.SHORT_GRASS, null, "Daisy Roots", null, null),
    /**
     * potion ingredient
     */
    DEATHS_HEAD_MOTH_CHRYSALIS(Material.COAL, null, "Death's Head Moth Chrysalis", null, null),
    /**
     * potion ingredient
     */
    DEW_DROP(Material.GHAST_TEAR, null, "Dew Drop", null, null),
    /**
     * potion ingredient
     */
    DITTANY(Material.BIRCH_SAPLING, null, "Dittany", null, null),
    /**
     * potion ingredient
     */
    DOXY_VENOM(Material.POTION, Color.PURPLE, "Doxy Venom", null, null),
    /**
     * potion ingredient
     */
    DRAGON_BLOOD(Material.POTION, Color.BLACK, "Dragon Blood", null, null),
    /**
     * Wand core
     */
    DRAGON_HEARTSTRING(Material.FERMENTED_SPIDER_EYE, null, "Dragon Heartstring", null, null),
    /**
     * potion ingredient
     */
    DRAGONFLY_THORAXES(Material.BEETROOT_SEEDS, null, "Dragonfly Thoraxes", null, null),
    /**
     * potion ingredient
     */
    DRIED_NETTLES(Material.OAK_SAPLING, null, "Dried Nettles", null, null),
    /**
     * divination object
     */
    EGG(Material.EGG, null, "Egg", null, null),
    /**
     * the elder wand
     */
    ELDER_WAND(Material.BLAZE_ROD, null, "Elder Wand", "Blaze and Ender Pearl", null),
    /**
     * potion ingredient
     */
    FAIRY_WING(Material.GOLD_NUGGET, null, "Fairy Wing", null, null),
    /**
     * potion ingredient
     */
    FLOBBERWORM_MUCUS(Material.SLIME_BALL, null, "Flobberworm Mucus", null, null),
    /**
     * potion ingredient
     */
    FLUXWEED(Material.VINE, null, "Fluxweed", null, null),
    /**
     * floo powder
     */
    FLOO_POWDER(Material.REDSTONE, null, "Floo Powder", "Glittery, silver powder", null),
    /**
     * potion ingredient
     */
    FULGURITE(Material.GLOWSTONE_DUST, null, "Fulgurite", null, null),
    /**
     * potion ingredient
     */
    GALANTHUS_NIVALIS(Material.AZURE_BLUET, null, "Galanthus Nivalis", null, null),
    /**
     * wizard money
     */
    GALLEON(Material.GOLD_INGOT, null, "Galleon", "Galleon", null),
    /**
     * potion ingredient
     */
    GINGER_ROOT(Material.BEETROOT, null, "Ginger Root", null, null),
    /**
     * potion ingredient
     */
    GROUND_DRAGON_HORN(Material.GLOWSTONE_DUST, null, "Ground Dragon Horn", null, null),
    /**
     * potion ingredient
     */
    GROUND_PORCUPINE_QUILLS(Material.GRAY_DYE, null, "Ground Porcupine Quills", null, null),
    /**
     * potion ingredient
     */
    GROUND_SCARAB_BEETLE(Material.GUNPOWDER, null, "Ground Scarab Beetle", null, null),
    /**
     * potion ingredient
     */
    GROUND_SNAKE_FANGS(Material.LIGHT_GRAY_DYE, null, "Ground Snake Fangs", null, null),
    /**
     * legacy wand core
     */
    GUNPOWDER(Material.GUNPOWDER, null, "Gunpowder", null, null),
    /**
     * potion ingredient
     */
    HONEYWATER(Material.POTION, Color.fromRGB(247, 110, 2), "Honeywater", null, null),
    /**
     * potion ingredient
     */
    HORKLUMP_JUICE(Material.DRAGON_BREATH, null, "Horklump Juice", null, null),
    /**
     * potion ingredient
     */
    HORNED_SLUG_MUCUS(Material.SLIME_BALL, null, "Horned Slug Mucus", null, null),
    /**
     * potion ingredient
     */
    HORN_OF_BICORN(Material.GOAT_HORN, null, "Horn of Bicorn", null, null),
    /**
     * potion ingredient
     */
    INFUSION_OF_COWBANE(Material.POTION, Color.fromRGB(255, 248, 220), "Infusion of Cowbane", null, null),
    /**
     * potion ingredient
     */
    INFUSION_OF_WORMWOOD(Material.POTION, Color.fromRGB(138, 154, 91), "Infusion of Wormwood", null, null),
    /**
     * an invisibility cloak
     */
    INVISIBILITY_CLOAK(Material.CHAINMAIL_CHESTPLATE, null, "Cloak of Invisibility", "Silvery Transparent Cloak", null),
    /**
     * potion ingredient
     */
    JOBBERKNOLL_FEATHER(Material.FEATHER, null, "Jobberknoll Feather", null, null),
    /**
     * wand core
     */
    KELPIE_HAIR(Material.PALE_HANGING_MOSS, null, "Kelpie Hair", null, null),
    /**
     * potion ingredient
     */
    KNOTGRASS(Material.TALL_GRASS, null, "Knotgrass", null, null),
    /**
     * wizard money
     */
    KNUT(Material.NETHERITE_INGOT, null, "Knut", "Knut", null),
    /**
     * potion ingredient
     */
    LACEWING_FLIES(Material.PUMPKIN_SEEDS, null, "Lacewing Flies", null, null),
    /**
     * potion ingredient
     */
    LAVENDER_SPRIG(Material.LILAC, null, "Lavender Sprig", null, null),
    /**
     * potion ingredient
     */
    LEECH_JUICE(Material.POTION, Color.MAROON, "Leech Juice", null, null),
    /**
     * potion ingredient
     */
    LEECHES(Material.INK_SAC, null, "Leeches", null, null),
    /**
     * potion ingredient
     */
    LETHE_RIVER_WATER(Material.POTION, Color.AQUA, "Lethe River Water", null, null), //bottle of water
    /**
     * potion ingredient
     */
    LIONFISH_SPINES(Material.PUMPKIN_SEEDS, null, "Lionfish Spines", null, null),
    /**
     * potion ingredient
     */
    MANDRAKE_LEAF(Material.LILY_PAD, null, "Mandrake Leaf", null, null),
    /**
     * potion ingredient
     */
    MERCURY(Material.POTION, Color.SILVER, "Mercury", null, null), // silver liquid
    /**
     * potion ingredient
     */
    MINT_SPRIG(Material.KELP, null, "Mint Sprig", null, null),
    /**
     * potion ingredient
     */
    MISTLETOE_BERRIES(Material.NETHER_WART, null, "Mistletoe Berries", null, null),
    /**
     * potion ingredient
     */
    MOONCALF_MILK(Material.POTION, Color.WHITE, "Moonclaf Milk", null, null),
    /**
     * potion ingredient
     */
    MOONDEW_DROP(Material.GHAST_TEAR, null, "Moondew Drop", null, null),
    /**
     * cauldron for making potions
     */
    PEWTER_CAULDRON(Material.CAULDRON, null, "Pewter Cauldron", null, null),
    /**
     * wand core
     */
    PHOENIX_FEATHER(Material.FEATHER, null, "Phoenix Feather", null, null),
    /**
     * divination object
     */
    PLAYING_CARDS(Material.PAPER, null, "Playing Cards", null, null),
    /**
     * potion ingredient
     */
    POISONOUS_POTATO(Material.POISONOUS_POTATO, null, "Poisonous Potato", null, null),
    /**
     * potion ingredient
     */
    POWDERED_ASHPODEL_ROOT(Material.ORANGE_DYE, null, "Powdered Root of Asphodel", null, null),
    /**
     * potion ingredient
     */
    POWDERED_GRIFFIN_CLAW(Material.LIGHT_GRAY_DYE, null, "Powdered Griffin Claw", null, null),
    /**
     * potion ingredient
     */
    POWDERED_SAGE(Material.LIME_DYE, null, "Powdered Sage", null, null),
    /**
     * potion ingredient
     */
    PUFFERFISH_EYE(Material.SPIDER_EYE, null, "Pufferfish Eye", null, null),
    /**
     * potion ingredient
     */
    RAT_SPLEEN(Material.INK_SAC, null, "Rat Spleen", null, null),
    /**
     * potion ingredient
     */
    ROTTEN_FLESH(Material.ROTTEN_FLESH, null, "Rotten Flesh", null, null),
    /**
     * potion ingredient
     */
    RUNESPOOR_EGG(Material.EGG, null, "Runespoor Egg", null, null),
    /**
     * potion ingredient
     */
    SALAMANDER_BLOOD(Material.POTION, Color.fromRGB(136, 8, 8), "Salamander Blood", null, null),
    /**
     * potion ingredient
     */
    SALAMANDER_FIRE(Material.BLAZE_POWDER, null, "Salamander Fire", null, null),
    /**
     * potion ingredient
     */
    SHRIVELIG(Material.CHORUS_FRUIT, null, "Shrivelfig", null, null),
    /**
     * wizard money
     */
    SICKLE(Material.IRON_INGOT, null, "Sickle", "Sickle", null),
    /**
     * potion ingredient
     */
    SLICED_CATERPILLARS(Material.WHEAT_SEEDS, null, "Sliced Caterpillar", null, null),
    /**
     * potion ingredient
     */
    SLOTH_BRAIN(Material.FERMENTED_SPIDER_EYE, null, "Sloth Brain", null, null),
    /**
     * potion ingredient
     */
    SLOTH_BRAIN_MUCUS(Material.POTION, Color.GRAY, "Sloth Brain Mucus", null, null),
    /**
     * potion ingredient
     */
    SOPOPHORUS_BEAN_JUICE(Material.POTION, Color.SILVER, "Sopophorus Bean Juice", null, null),
    /**
     * potion ingredient
     */
    SPIDER_EYE(Material.SPIDER_EYE, null, "Spider Eye", null, null),
    /**
     * potion ingredient
     */
    STANDARD_POTION_INGREDIENT(Material.SUGAR, null, "Standard Potion Ingredient", null, null),
    /**
     * divination object
     */
    TAROT_CARDS(Material.PAPER, null, "Tarot Cards", null, null),
    /**
     * divination object
     */
    TEA_LEAVES(Material.GREEN_DYE, null, "Tea Leaves", null, null),
    /**
     * potion ingredient, wand core
     */
    UNICORN_HAIR(Material.STRING, null, "Unicorn Hair", null, null),
    /**
     * potion ingredient
     */
    UNICORN_HORN(Material.BREEZE_ROD, null, "Unicorn Horn", null, null),
    /**
     * potion ingredient
     */
    VALERIAN_SPRIGS(Material.VINE, null, "Valerian Sprigs", null, null),
    /**
     * potion ingredient
     */
    VALERIAN_ROOT(Material.GOLDEN_CARROT, null, "Valerian Root", null, null),
    /**
     * wand core
     */
    VEELA_HAIR(Material.STRING, null, "Veela Hair", null, null),
    /**
     * unenchanted wand
     */
    WAND(Material.STICK, null, "Wand", null, null),
    /**
     * potion ingredient
     */
    WARTCAP_POWDER(Material.BROWN_DYE, null, "Wartcap Powder", null, null),
    /**
     * potion ingredient
     */
    WOLFSBANE(Material.ALLIUM, null, "Wolfsbane", null, null);

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
    final private Color color;

    /**
     * Item enchantment
     */
    final private ItemEnchantmentType itemEnchantment;

    /**
     * Constructor
     *
     * @param material    the material type
     * @param color     the variant information, this is 0 for most items but for things like potions it can control color
     * @param name        the name of the item
     * @param lore        the lore for this item
     * @param enchantment the optional enchantment for this item
     */
    O2ItemType(@NotNull Material material, @Nullable Color color, @NotNull String name, @Nullable String lore, @Nullable ItemEnchantmentType enchantment) {
        this.material = material;
        this.name = name;
        this.color = color;
        this.lore = lore;
        this.itemEnchantment = enchantment;
    }

    /**
     * get the name of this item
     *
     * @return the name of this item
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * get the lore for this item
     *
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
     *
     * @return the material type for this item
     */
    @NotNull
    public Material getMaterial() {
        return material;
    }

    /**
     * get the enchantment on this item
     *
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
     * Is the specified ItemStack this item type
     *
     * @param item the item to check
     * @return true if it is this type, false otherwise
     */
    public boolean isItemThisType(@NotNull Item item) {
        return isItemThisType(item.getItemStack());
    }

    /**
     * Is the specified ItemStack this item type
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
        if (!container.has(O2Items.o2ItemTypeKey, PersistentDataType.STRING))
            return false;

        String itemTypeKey = container.get(O2Items.o2ItemTypeKey, PersistentDataType.STRING);
        if (itemTypeKey == null)
            return false;

        if (!(itemTypeKey.equalsIgnoreCase(name)))
            return false;

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
            if (color != null)
                ((PotionMeta) meta).setColor(color);
            else
                ((PotionMeta) meta).setColor(Color.WHITE);
        }

        // add custom NBT tag
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(O2Items.o2ItemTypeKey, PersistentDataType.STRING, name);

        o2Item.setItemMeta(meta);

        // apply enchantment if this item type has one
        if (itemEnchantment != null) {
            String eid = TimeCommon.getCurrentTimestamp() + " " + name + " " + itemEnchantment.getName();
            Ollivanders2API.getItems().enchantedItems.addEnchantedItem(o2Item, itemEnchantment, 1, eid, "");
        }

        return o2Item;
    }
}