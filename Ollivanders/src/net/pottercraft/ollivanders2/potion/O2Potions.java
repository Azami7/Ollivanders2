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
 * Manager for all Ollivanders2 potions and potion-related operations.
 * <p>
 * Handles potion brewing in cauldrons, potion instantiation from types, ingredient management,
 * and mapping of Minecraft potion effects to magic difficulty levels.
 * </p>
 * <p>
 * Potions can be disabled if they depend on optional plugins (e.g., LibsDisguises).
 * </p>
 *
 * @author Azami7
 * @see O2PotionType enumeration of all available potion types
 * @see O2Potion abstract base class for all potion implementations
 */
public class O2Potions {
    /**
     * Reference to the Ollivanders2 plugin instance.
     */
    final private Ollivanders2 p;

    /**
     * Utility functions for common operations across the plugin.
     */
    final private Ollivanders2Common common;

    /**
     * Cache of loaded potion types indexed by name (lowercase).
     * <p>
     * Maps potion display names to their corresponding O2PotionType enum values for fast lookup.
     * Only includes potion types that are currently loaded (not disabled by missing dependencies).
     * </p>
     */
    final static private Map<String, O2PotionType> O2PotionMap = new HashMap<>();

    /**
     * Namespace key for storing potion type in item NBT tags.
     * <p>
     * Used to store the potion type name on potion ItemStack metadata to identify potions when retrieved from player inventory.
     * </p>
     */
    static public NamespacedKey potionTypeKey;

    /**
     * List of all valid potion ingredients.
     * <p>
     * These are the O2ItemType values that can be used in cauldron recipes to brew potions.
     * </p>
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
     * Magic difficulty levels for each Minecraft potion effect type.
     * <p>
     * Used primarily by FINITE_INCANTATEM counter-spell to determine if a spell can dispel an effect
     * based on the caster's magic level. Maps each Minecraft PotionEffectType to its corresponding MagicLevel.
     * </p>
     *
     * @see net.pottercraft.ollivanders2.spell.FINITE_INCANTATEM
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
     * Constructor for initializing the O2Potions manager.
     * <p>
     * Creates the NamespacedKey used for storing potion type information in item NBT tags.
     * Call {@link #onEnable()} during plugin startup to load all available potions.
     * </p>
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    public O2Potions(@NotNull Ollivanders2 plugin) {
        p = plugin;

        common = new Ollivanders2Common(p);
        potionTypeKey = new NamespacedKey(p, "o2potion_type");
    }

    /**
     * Load all potions when the plugin enables.
     * <p>
     * Populates the potion cache with all available potion types from O2PotionType enum,
     * excluding any that depend on optional plugins (e.g., LibsDisguises) that are not loaded except in test mode.
     * </p>
     */
    public void onEnable() {
        for (O2PotionType potionType : O2PotionType.values()) {
            if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.libDisguisesPotions.contains(potionType) && !Ollivanders2.testMode)
                continue;

            O2PotionMap.put(potionType.getPotionName().toLowerCase(), potionType);
        }
    }

    /**
     * Cleanup when the plugin disables.
     *
     * <p>Called when the Ollivanders2 plugin is being shut down. The O2Potions manager
     * performs no cleanup on disable because potion data is read-only and loaded from
     * configuration on startup. No persistent state needs to be saved.</p>
     */
    public void onDisable() {
    }

    /**
     * Get all currently loaded potion types.
     * <p>
     * Returns the potion types that are active in the O2PotionMap (may exclude potions
     * that depend on unavailable optional plugins).
     * </p>
     *
     * @return a list of all currently loaded potion types
     */
    public static List<O2PotionType> getAllPotionTypes() {
        return new ArrayList<>(O2PotionMap.values());
    }

    /**
     * Get instances of all currently loaded potions.
     * <p>
     * Creates one instance of each O2Potion subclass for all loaded potion types.
     * Excludes potions that depend on optional plugins that are not loaded.
     * </p>
     *
     * @return a collection containing one instance of each loaded potion
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
     * Get the display names of all potion types.
     * <p>
     * Returns the display names for all potion types defined in the O2PotionType enum,
     * including those that may be disabled due to missing optional dependencies.
     * </p>
     *
     * @return a list of all potion display names
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
     * <p>
     * Attempts to match cauldron ingredients to a known potion recipe. Returns null if the
     * cauldron is not a water cauldron or contains no ingredients. If ingredients don't match
     * any known recipe, returns a bad potion and sends a message to the brewer.
     * </p>
     *
     * @param cauldron the cauldron block containing the ingredients
     * @param brewer   the player brewing this potion
     * @return a potion ItemStack if successful, a bad potion if recipe unknown, or null if cauldron is invalid/empty
     */
    @Nullable
    public ItemStack brewPotion(@NotNull Block cauldron, @NotNull Player brewer) {
        // make sure the block passed to us is a cauldron with water
        if (cauldron.getType() != Material.WATER_CAULDRON)
            return null;

        // get ingredients from the cauldron
        Map<O2ItemType, Integer> ingredientsInCauldron = getIngredientsInCauldron(cauldron);

        // make sure cauldron has ingredients in it
        if (ingredientsInCauldron.isEmpty())
            return null;

        // match the ingredients in this potion to a known potion
        O2Potion potion = findPotionByIngredients(ingredientsInCauldron);
        if (potion == null || (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.libDisguisesPotions.contains(potion.getPotionType()))) {
            brewer.sendMessage(Ollivanders2.chatColor + "You feel somewhat uncertain about this recipe.");
            // make them a bad potion
            return O2Potion.brewBadPotion();
        }

        return potion.brew(brewer, true);
    }

    /**
     * Match ingredient amounts to a known potion recipe.
     * <p>
     * Compares the provided ingredients against all known potion recipes to find an exact match.
     * Both ingredient types and amounts must match a recipe exactly for the match to succeed.
     * </p>
     *
     * @param ingredientsInCauldron a map of ingredient types to their amounts
     * @return the matching potion if found, null if no recipe matches
     */
    @Nullable
    public O2Potion findPotionByIngredients(@NotNull Map<O2ItemType, Integer> ingredientsInCauldron) {
        // compare ingredients in the cauldron to the recipe for each potion
        for (O2PotionType potionType : O2PotionType.values()) {
            O2Potion potion = getPotionFromType(potionType);

            if (potion != null && potion.checkRecipe(ingredientsInCauldron))
                return potion;
        }

        return null;
    }

    /**
     * Get all O2Item ingredients near a cauldron.
     * <p>
     * Searches for O2Items in a 1-block radius around the cauldron. Only counts items that are
     * valid potion ingredients (items in the ingredients list). Non-O2Items are ignored.
     * Aggregates multiple stacks of the same ingredient by amount.
     * </p>
     *
     * @param cauldron the brewing cauldron block
     * @return a map of ingredient types to their total amounts
     */
    @NotNull
    public Map<O2ItemType, Integer> getIngredientsInCauldron(@NotNull Block cauldron) {
        Map<O2ItemType, Integer> ingredientsInCauldron = new HashMap<>();
        Location location = cauldron.getLocation();

        for (Entity e : cauldron.getWorld().getNearbyEntities(location, 1, 1, 1)) {
            if (e instanceof Item) {
                Material material = ((Item) e).getItemStack().getType();

                // get the O2Item name, if it is an O2Item
                String ingredientName = Ollivanders2API.getItems().getO2ItemNameFromItemStack(((Item) e).getItemStack());
                if (ingredientName == null)
                    continue;

                // get the O2ItemType, if it is an O2Item
                O2ItemType ingredientType = Ollivanders2API.getItems().getItemTypeByName(ingredientName);
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
     * Find an O2Potion by looking up an ItemStack's NBT metadata.
     * <p>
     * Extracts the ItemMeta from the ItemStack and looks up the potion type from its
     * persistent data container. This is a convenience method that delegates to
     * {@link #findPotionByItemMeta(ItemMeta)}.
     * </p>
     *
     * @param itemStack the ItemStack to look up
     * @return the O2Potion if found and loaded, null otherwise
     */
    @Nullable
    public O2Potion findPotionByItemStack(@NotNull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null)
            return null;
        else
            return findPotionByItemMeta(itemMeta);
    }

    /**
     * Find an O2Potion by looking up its type in item NBT tags.
     * <p>
     * Retrieves the potion type from the item's persistent data container, handling both current
     * (display name) and legacy (enum name) potion type formats.
     * </p>
     *
     * @param meta the ItemMeta containing the potion type NBT tag
     * @return the O2Potion instance if the type is found and currently loaded, null otherwise
     */
    @Nullable
    public O2Potion findPotionByItemMeta(@NotNull ItemMeta meta) {
        // check NBT
        O2PotionType potionType = null;
        String potionName = null;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(potionTypeKey, PersistentDataType.STRING))
            potionName = container.get(potionTypeKey, PersistentDataType.STRING);

        if (potionName != null && !potionName.isEmpty()) {
            common.printDebugMessage("O2Potions.findPotionByItemMeta: potion name is " + potionName, null, null, false);
            potionType = O2PotionType.getPotionTypeByName(potionName);

            // handle the case of legacy potions created before potion name was added
            if (potionType == null)
                potionType = O2PotionType.getPotionTypeFromString(potionName);

        }
        else {
            common.printDebugMessage("O2Potions.findPotionByItemMeta: potion name not found in NBT", null, null, false);
        }

        // if the type was found and this type is currently loaded
        if (potionType != null) {
            if (O2PotionMap.containsValue(potionType)) {
                common.printDebugMessage("O2Potions.findPotionByItemMeta: found potion", null, null, false);
                return getPotionFromType(potionType);
            }
            else {
                common.printDebugMessage("O2Potions.findPotionByItemMeta: did not find potion", null, null, false);
                return null;
            }
        }

        return null;
    }

    /**
     * Create an O2Potion instance from its type using reflection.
     * <p>
     * Instantiates the potion class defined in the O2PotionType enum using reflection,
     * calling the constructor that takes an Ollivanders2 plugin parameter.
     * </p>
     *
     * @param potionType the potion type to instantiate
     * @return the O2Potion instance if created successfully, null if instantiation fails
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
     * Get the display names of all valid potion ingredients.
     *
     * @return a list of all potion ingredient display names
     */
    @NotNull
    public static List<String> getAllIngredientNames() {
        ArrayList<String> ingredientList = new ArrayList<>();

        for (O2ItemType i : ingredients) {
            ingredientList.add(Ollivanders2API.getItems().getItemNameByType(i));
        }

        return ingredientList;
    }

    /**
     * Check if a potion type is currently loaded.
     * <p>
     * A potion may not be loaded if it depends on an optional plugin (e.g., LibsDisguises)
     * that is not installed on the server.
     * </p>
     *
     * @param potionType the potion type to check
     * @return true if the potion type is loaded, false if disabled due to missing dependencies
     */
    public boolean isLoaded(@NotNull O2PotionType potionType) {
        return O2PotionMap.containsValue(potionType);
    }

    /**
     * Get the magic difficulty level for a Minecraft potion effect type.
     * <p>
     * Used by FINITE_INCANTATEM counter-spell to determine if a potion effect can be dispelled.
     * If an effect is not explicitly defined, defaults to OWL level.
     * </p>
     *
     * @param potionEffectType the Minecraft potion effect type to get the level of
     * @return the magic difficulty level (defaults to OWL if not explicitly defined)
     */
    @NotNull
    static public MagicLevel getPotionEffectMagicLevel(@NotNull PotionEffectType potionEffectType) {
        if (potionEffectLevels.containsKey(potionEffectType))
            return potionEffectLevels.get(potionEffectType);
        else
            return MagicLevel.OWL;
    }

    /**
     * Create an ItemStack for a potion type without brewing checks.
     * <p>
     * Creates a potion ItemStack of the specified amount. This bypasses all brewing requirements
     * and experience checks. For player-initiated brewing, use {@link #brewPotion(Block, Player)}
     * instead, which validates player experience levels and handles bad potions.
     * </p>
     *
     * @param potionType the type of potion to create
     * @param amount the number of potions in the ItemStack
     * @return a potion ItemStack with the specified amount, or null if instantiation fails
     */
    @Nullable
    public ItemStack getPotionItemStackByType(@NotNull O2PotionType potionType, int amount) {
        O2Potion potion = getPotionFromType(potionType);
        if (potion == null)
            return null;

        return potion.createPotionItemStack(amount);
    }
}
