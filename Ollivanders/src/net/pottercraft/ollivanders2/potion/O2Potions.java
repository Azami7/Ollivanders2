package net.pottercraft.ollivanders2.potion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import net.pottercraft.ollivanders2.common.MagicLevel;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages all Ollivanders2 potions.
 *
 * @author Azami7
 */
public class O2Potions {
    /**
     * callback to plugin
     */
    final private Ollivanders2 p;

    /**
     * common functions
     */
    final private Ollivanders2Common common;

    /**
     * a map of all the potions loaded in the game
     */
    final static private Map<String, O2PotionType> O2PotionMap = new HashMap<>();

    /**
     * Namespace keys for NBT tags
     */
    static public NamespacedKey potionTypeKey;

    /**
     * Potion ingredients
     */
    public static final List<O2ItemType> ingredients = new ArrayList<>() {{
        add(O2ItemType.ACONITE);
        add(O2ItemType.ARMADILLO_BILE);
        add(O2ItemType.BEZOAR);
        add(O2ItemType.BILLYWIG_STING_SLIME);
        add(O2ItemType.BLOOD);
        add(O2ItemType.BONE);
        add(O2ItemType.BOOM_BERRY_JUICE);
        add(O2ItemType.BOOMSLANG_SKIN);
        add(O2ItemType.CHIZPURFLE_FANGS);
        add(O2ItemType.CRUSHED_FIRE_SEEDS);
        add(O2ItemType.DEATHS_HEAD_MOTH_CHRYSALIS);
        add(O2ItemType.DEW_DROP);
        add(O2ItemType.DITTANY);
        add(O2ItemType.DRAGON_BLOOD);
        add(O2ItemType.DRAGONFLY_THORAXES);
        add(O2ItemType.DRIED_NETTLES);
        add(O2ItemType.FAIRY_WING);
        add(O2ItemType.FLOBBERWORM_MUCUS);
        add(O2ItemType.FLUXWEED);
        add(O2ItemType.FULGURITE);
        add(O2ItemType.GALANTHUS_NIVALIS);
        add(O2ItemType.GINGER_ROOT);
        add(O2ItemType.GROUND_DRAGON_HORN);
        add(O2ItemType.GROUND_PORCUPINE_QUILLS);
        add(O2ItemType.GROUND_SCARAB_BEETLE);
        add(O2ItemType.GROUND_SNAKE_FANGS);
        add(O2ItemType.HONEYWATER);
        add(O2ItemType.HORKLUMP_JUICE);
        add(O2ItemType.HORNED_SLUG_MUCUS);
        add(O2ItemType.HORN_OF_BICORN);
        add(O2ItemType.INFUSION_OF_WORMWOOD);
        add(O2ItemType.JOBBERKNOLL_FEATHER);
        add(O2ItemType.KNOTGRASS);
        add(O2ItemType.LACEWING_FLIES);
        add(O2ItemType.LAVENDER_SPRIG);
        add(O2ItemType.LEECHES);
        add(O2ItemType.LETHE_RIVER_WATER);
        add(O2ItemType.LIONFISH_SPINES);
        add(O2ItemType.MANDRAKE_LEAF);
        add(O2ItemType.MERCURY);
        add(O2ItemType.MINT_SPRIG);
        add(O2ItemType.MISTLETOE_BERRIES);
        add(O2ItemType.MOONDEW_DROP);
        add(O2ItemType.POISONOUS_POTATO);
        add(O2ItemType.POWDERED_ASHPODEL_ROOT);
        add(O2ItemType.POWDERED_SAGE);
        add(O2ItemType.ROTTEN_FLESH);
        add(O2ItemType.RUNESPOOR_EGG);
        add(O2ItemType.SALAMANDER_BLOOD);
        add(O2ItemType.SALAMANDER_FIRE);
        add(O2ItemType.SLOTH_BRAIN);
        add(O2ItemType.SLOTH_BRAIN_MUCUS);
        add(O2ItemType.SOPOPHORUS_BEAN_JUICE);
        add(O2ItemType.SPIDER_EYE);
        add(O2ItemType.STANDARD_POTION_INGREDIENT);
        add(O2ItemType.UNICORN_HAIR);
        add(O2ItemType.UNICORN_HORN);
        add(O2ItemType.VALERIAN_SPRIGS);
        add(O2ItemType.VALERIAN_ROOT);
        add(O2ItemType.WOLFSBANE);
    }};

    /**
     * The magic level for every minecraft potion effect type, primarily for use with Finite Incantatum
     * <p>
     * {@link net.pottercraft.ollivanders2.spell.FINITE_INCANTATEM}
     */
    static HashMap<PotionEffectType, MagicLevel> potionEffectLevels = new HashMap<>() {{
        put(PotionEffectType.ABSORPTION, MagicLevel.OWL);
        put(PotionEffectType.BAD_OMEN, MagicLevel.NEWT);
        put(PotionEffectType.BLINDNESS, MagicLevel.OWL);
        put(PotionEffectType.CONDUIT_POWER, MagicLevel.NEWT);
        put(PotionEffectType.DARKNESS, MagicLevel.OWL);
        put(PotionEffectType.DOLPHINS_GRACE, MagicLevel.NEWT);
        put(PotionEffectType.FIRE_RESISTANCE, MagicLevel.NEWT);
        put(PotionEffectType.GLOWING, MagicLevel.BEGINNER);
        put(PotionEffectType.HASTE, MagicLevel.BEGINNER);
        put(PotionEffectType.HEALTH_BOOST, MagicLevel.NEWT);
        put(PotionEffectType.HERO_OF_THE_VILLAGE, MagicLevel.NEWT);
        put(PotionEffectType.HUNGER, MagicLevel.BEGINNER);
        put(PotionEffectType.INFESTED, MagicLevel.OWL);
        put(PotionEffectType.INSTANT_DAMAGE, MagicLevel.OWL);
        put(PotionEffectType.INSTANT_HEALTH, MagicLevel.OWL);
        put(PotionEffectType.INVISIBILITY, MagicLevel.EXPERT);
        put(PotionEffectType.JUMP_BOOST, MagicLevel.BEGINNER);
        put(PotionEffectType.LEVITATION, MagicLevel.OWL);
        put(PotionEffectType.LUCK, MagicLevel.BEGINNER);
        put(PotionEffectType.MINING_FATIGUE, MagicLevel.BEGINNER);
        put(PotionEffectType.NAUSEA, MagicLevel.OWL);
        put(PotionEffectType.NIGHT_VISION, MagicLevel.BEGINNER);
        put(PotionEffectType.OOZING, MagicLevel.OWL);
        put(PotionEffectType.POISON, MagicLevel.OWL);
        put(PotionEffectType.RAID_OMEN, MagicLevel.NEWT);
        put(PotionEffectType.REGENERATION, MagicLevel.NEWT);
        put(PotionEffectType.RESISTANCE, MagicLevel.NEWT);
        put(PotionEffectType.SATURATION, MagicLevel.BEGINNER);
        put(PotionEffectType.SLOW_FALLING, MagicLevel.NEWT);
        put(PotionEffectType.SLOWNESS, MagicLevel.BEGINNER);
        put(PotionEffectType.SPEED, MagicLevel.BEGINNER);
        put(PotionEffectType.STRENGTH, MagicLevel.NEWT);
        put(PotionEffectType.TRIAL_OMEN, MagicLevel.NEWT);
        put(PotionEffectType.UNLUCK, MagicLevel.BEGINNER);
        put(PotionEffectType.WATER_BREATHING, MagicLevel.NEWT);
        put(PotionEffectType.WEAKNESS, MagicLevel.OWL);
        put(PotionEffectType.WEAVING, MagicLevel.OWL);
        put(PotionEffectType.WIND_CHARGED, MagicLevel.OWL);
        put(PotionEffectType.WITHER, MagicLevel.NEWT);
    }};

    /**
     * Constructor
     *
     * @param plugin a reference to the plugin
     */
    public O2Potions(@NotNull Ollivanders2 plugin) {
        p = plugin;

        common = new Ollivanders2Common(p);
        potionTypeKey = new NamespacedKey(p, "o2potion_type");
    }

    /**
     * Load all potions on plugin start
     */
    public void onEnable() {
        for (O2PotionType potionType : O2PotionType.values()) {
            if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.libDisguisesPotions.contains(potionType))
                continue;

            O2PotionMap.put(potionType.getPotionName().toLowerCase(), potionType);
        }
    }

    /**
     * Get all potions loaded
     *
     * @return a list of all potion types loaded
     */
    public static List<O2PotionType> getAllPotionTypes() {
        return new ArrayList<>(O2PotionMap.values());
    }

    /**
     * Return the set of all the potions
     *
     * @return a Collection of 1 of each O2Potion
     */
    @NotNull
    public Collection<O2Potion> getAllPotions() {
        Collection<O2Potion> potions = new ArrayList<>();

        for (O2PotionType potionType : O2PotionType.values()) {
            O2Potion potion = getPotionFromType(potionType);

            if (potion != null) {
                if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.libDisguisesPotions.contains(potion.getPotionType())) {
                    continue;
                }

                potions.add(potion);
            }
        }

        return potions;
    }

    /**
     * Get the names of all potions loaded in the game.
     *
     * @return the list of active potion names
     */
    @NotNull
    public List<String> getAllPotionNames() {
        ArrayList<String> potionNames = new ArrayList<>();

        for (O2PotionType potionType : O2PotionType.values()) {
            potionNames.add(potionType.getPotionName());
        }

        return potionNames;
    }

    /**
     * Brew a potion in a cauldron.
     *
     * @param cauldron the cauldron with the potion ingredients
     * @param brewer   the player brewing this potion
     * @return the brewed potion if the recipe matches a known potion, null otherwise
     */
    @Nullable
    public ItemStack brewPotion(@NotNull Block cauldron, @NotNull Player brewer) {
        // make sure the block passed to us is a cauldron
        if (cauldron.getType() != Material.WATER_CAULDRON)
            return null;

        // get ingredients from the cauldron
        Map<O2ItemType, Integer> ingredientsInCauldron = getIngredientsInCauldron(cauldron);

        // make sure cauldron has ingredients in it
        if (ingredientsInCauldron.isEmpty())
            return null;

        // match the ingredients in this potion to a known potion
        O2Potion potion = matchPotion(ingredientsInCauldron);
        if (potion == null || (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.libDisguisesPotions.contains(potion.getPotionType()))) {
            brewer.sendMessage(Ollivanders2.chatColor + "You feel somewhat uncertain about this recipe.");
            // make them a bad potion
            return O2Potion.brewBadPotion();
        }

        return potion.brew(brewer, true);
    }

    /**
     * Match the ingredients in this cauldron to a known potion.
     *
     * @param ingredientsInCauldron the ingredients in this cauldron
     * @return the matching potion if found, null otherwise
     */
    @Nullable
    private O2Potion matchPotion(@NotNull Map<O2ItemType, Integer> ingredientsInCauldron) {
        // compare ingredients in the cauldron to the recipe for each potion
        for (O2PotionType potionType : O2PotionType.values()) {
            O2Potion potion = getPotionFromType(potionType);

            if (potion != null && potion.checkRecipe(ingredientsInCauldron))
                return potion;
        }

        return null;
    }

    /**
     * Creates a map of all the ingredients in this cauldron.
     *
     * @param cauldron the brewing cauldron
     * @return a Map of the ingredients and count of each ingredient
     */
    @NotNull
    private Map<O2ItemType, Integer> getIngredientsInCauldron(@NotNull Block cauldron) {
        Map<O2ItemType, Integer> ingredientsInCauldron = new HashMap<>();
        Location location = cauldron.getLocation();

        for (Entity e : cauldron.getWorld().getNearbyEntities(location, 1, 1, 1)) {
            if (e instanceof Item) {
                Material material = ((Item) e).getItemStack().getType();

                // get the O2Item name, if it is an O2Item
                String ingredientName = Ollivanders2API.getItems().getO2ItemNameFromItem(((Item) e).getItemStack());
                if (ingredientName == null)
                    continue;

                // get the O2ItemType, if it is an O2Item
                O2ItemType ingredientType = Ollivanders2API.getItems().getTypeByDisplayName(ingredientName);
                if (ingredientType == null || material != Ollivanders2API.getItems().getItemMaterialByType(ingredientType))
                    continue;

                int count = ((Item) e).getItemStack().getAmount();

                common.printDebugMessage("Found " + count + " of ingredient " + ingredientType, null, null, false);

                if (ingredientsInCauldron.containsKey(ingredientType))
                    count = count + ingredientsInCauldron.get(ingredientType);

                ingredientsInCauldron.put(ingredientType, count);
            }
        }

        return ingredientsInCauldron;
    }

    /**
     * Get an O2Potion from ItemMeta
     *
     * @param meta the metadata for this item
     * @return the O2Potion, if one was found, null otherwise
     */
    @Nullable
    public O2Potion findPotionByItemMeta(@NotNull ItemMeta meta) {
        // check NBT
        O2PotionType potionType = null;
        String potionTypeString = null;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(potionTypeKey, PersistentDataType.STRING)) {
            potionTypeString = container.get(potionTypeKey, PersistentDataType.STRING);
        }

        if (potionTypeString != null && !potionTypeString.isEmpty()) {
            potionType = O2PotionType.potionTypeFromString(potionTypeString);
        }

        // if the type was found and this type is currently loaded
        if (potionType != null) {
            if (O2PotionMap.containsValue(potionType))
                return getPotionFromType(potionType);
            else
                return null;
        }

        return null;
    }

    /**
     * Get an O2Potions object from its type.
     *
     * @param potionType the type of potion to get
     * @return the O2Potion object, if it could be created, or null otherwise
     */
    @Nullable
    public O2Potion getPotionFromType(@NotNull O2PotionType potionType) {
        O2Potion potion;

        Class<?> potionClass = potionType.getClassName();

        try {
            potion = (O2Potion) potionClass.getConstructor(Ollivanders2.class).newInstance(p);
        }
        catch (Exception exception) {
            common.printDebugMessage("Exception trying to create new instance of " + potionType, exception, null, true);

            return null;
        }

        return potion;
    }

    /**
     * Get a potion type by name.
     *
     * @param name the name of the potion
     * @return the type if found, null otherwise
     */
    @Nullable
    public O2PotionType getPotionTypeByName(@NotNull String name) {
        return O2PotionMap.getOrDefault(name.toLowerCase(), null);
    }

    /**
     * Get a list of the names of every potion ingredient.
     *
     * @return a list of all potions ingredients
     */
    @NotNull
    public static List<String> getAllIngredientNames() {
        ArrayList<String> ingredientList = new ArrayList<>();

        for (O2ItemType i : ingredients) {
            ingredientList.add(Ollivanders2API.getItems().getItemDisplayNameByType(i));
        }

        return ingredientList;
    }

    /**
     * Verify this potion type is loaded. A potion may not be loaded if it depends on something such as LibsDisguises and that
     * dependency plugin does not exist.
     *
     * @param potionType the potion type to check
     * @return true if this potion type is loaded, false otherwise
     */
    public boolean isLoaded(@NotNull O2PotionType potionType) {
        return O2PotionMap.containsValue(potionType);
    }

    /**
     * Get the magic effect level for MC Potion Effects.
     *
     * @param potionEffectType the effect type to get the level of
     * @return the effect type level or OWL if it is not explicitly defined
     */
    @NotNull
    static public MagicLevel getPotionEffectMagicLevel(@NotNull PotionEffectType potionEffectType) {
        if (potionEffectLevels.containsKey(potionEffectType))
            return potionEffectLevels.get(potionEffectType);
        else
            return MagicLevel.OWL;
    }
}
