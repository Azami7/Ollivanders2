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

import net.pottercraft.ollivanders2.listeners.OllivandersListener;
import org.bukkit.Effect;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
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
public class O2Potions implements Listener {
    /**
     * Reference to the Ollivanders2 plugin instance.
     */
    final private Ollivanders2 p;

    /**
     * Utility functions for common operations across the plugin.
     */
    final private Ollivanders2Common common;

    /**
     * Loaded potion types indexed by lowercase display name. Excludes types disabled by missing dependencies.
     */
    final static private Map<String, O2PotionType> O2PotionMap = new HashMap<>();

    /**
     * NBT key storing the potion type name on a potion ItemStack, used to identify potions from a player's inventory.
     */
    static public NamespacedKey potionTypeKey;

    /**
     * The O2ItemType values that can be used as ingredients in cauldron recipes.
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
     * Magic difficulty level for each Minecraft potion effect type, used by FINITE_INCANTATEM to decide whether a
     * caster can dispel an effect.
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
     * Constructor. Call {@link #onEnable()} during plugin startup to load all available potions.
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    public O2Potions(@NotNull Ollivanders2 plugin) {
        p = plugin;

        common = new Ollivanders2Common(p);
        potionTypeKey = new NamespacedKey(p, "o2potion_type");

        p.getServer().getPluginManager().registerEvents(this, p);
    }

    /**
     * Populate the potion cache with all potion types, excluding any whose optional dependency (e.g. LibsDisguises)
     * is not loaded (unless in test mode).
     */
    public void onEnable() {
        for (O2PotionType potionType : O2PotionType.values()) {
            if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.requiresLibsDisguises(potionType) && !Ollivanders2.testMode)
                continue;

            O2PotionMap.put(potionType.getPotionName().toLowerCase(), potionType);
        }
    }

    /**
     * No-op; potion data is read-only, so there is no state to persist on disable.
     */
    public void onDisable() {
    }

    /**
     * Get all currently loaded potion types.
     *
     * @return the loaded potion types; may exclude potions whose optional dependency is unavailable
     */
    public static List<O2PotionType> getAllPotionTypes() {
        return new ArrayList<>(O2PotionMap.values());
    }

    /**
     * Create one instance of each loaded potion, excluding potions whose optional dependency is not loaded.
     *
     * @return a collection containing one instance of each loaded potion
     */
    @NotNull
    public Collection<O2Potion> getAllPotions() {
        Collection<O2Potion> potions = new ArrayList<>();

        for (O2PotionType potionType : O2PotionType.values()) {
            O2Potion potion = getPotionFromType(potionType);

            if (potion != null) {
                if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.requiresLibsDisguises(potion.getPotionType())) {
                    continue;
                }

                potions.add(potion);
            }
        }

        return potions;
    }

    /**
     * Get the display names of all potion types, including any disabled due to missing optional dependencies.
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
     * Brew a potion from the ingredients in a cauldron and put it in the player's off-hand.
     *
     * @param player   the player brewing the potion
     * @param cauldron the cauldron of ingredients
     * @apiNote No-ops unless the player holds a glass bottle in their off-hand and the cauldron is over a hot block.
     * Consumes one glass bottle and removes all dropped items within 1 block of the cauldron.
     */
    public void brewPotion(@NotNull Player player, @NotNull Block cauldron) {
        common.printDebugMessage("O2Potions.brewPotion: enter", null, null, false);

        ItemStack emptyBottle = player.getInventory().getItemInOffHand();
        if (emptyBottle.getType() != Material.GLASS_BOTTLE) {
            common.printDebugMessage("O2Potions.brewPotion: player not holding a glass bottle in off-hand", null, null, false);
            return;
        }

        if (!Ollivanders2Common.isHotBlock(cauldron.getRelative(BlockFace.DOWN))) {
            common.printDebugMessage("O2Potions.brewPotion: cauldron is not over a hot block", null, null, false);
            return;
        }

        ItemStack potion = checkRecipeAndBrew(cauldron, player);

        if (potion == null) {
            player.sendMessage(Ollivanders2.chatColor + "The cauldron appears unchanged. Perhaps you should check your recipe");
            return;
        }

        // remove ingredients from cauldron
        for (Entity e : cauldron.getWorld().getNearbyEntities(cauldron.getLocation(), 1, 1, 1)) {
            if (e instanceof Item)
                e.remove();
        }

        player.getWorld().playEffect(cauldron.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 1);

        int extraBottles = emptyBottle.getAmount() - 1;
        player.getInventory().setItemInOffHand(potion);
        if (extraBottles > 0)
            player.getInventory().addItem(new ItemStack(Material.GLASS_BOTTLE, extraBottles));
    }

    /**
     * Brew a potion from a cauldron's ingredients. Sends the brewer a message and returns a bad potion if the
     * ingredients match no known recipe.
     *
     * @param cauldron the cauldron block containing the ingredients
     * @param brewer   the player brewing this potion
     * @return a potion ItemStack if successful, a bad potion if recipe unknown, or null if the cauldron is not a
     * water cauldron or is empty
     */
    @Nullable
    public ItemStack checkRecipeAndBrew(@NotNull Block cauldron, @NotNull Player brewer) {
        if (cauldron.getType() != Material.WATER_CAULDRON)
            return null;

        Map<O2ItemType, Integer> ingredientsInCauldron = getIngredientsInCauldron(cauldron);
        if (ingredientsInCauldron.isEmpty())
            return null;

        O2Potion potion = findPotionByIngredients(ingredientsInCauldron);
        if (potion == null || (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.requiresLibsDisguises(potion.getPotionType()))) {
            brewer.sendMessage(Ollivanders2.chatColor + "You feel somewhat uncertain about this recipe.");
            return O2Potion.brewBadPotion();
        }

        return potion.brew(brewer, true);
    }

    /**
     * Match ingredient amounts to a known potion recipe. Both ingredient types and amounts must match exactly.
     *
     * @param ingredientsInCauldron a map of ingredient types to their amounts
     * @return the matching potion if found, null if no recipe matches
     */
    @Nullable
    public O2Potion findPotionByIngredients(@NotNull Map<O2ItemType, Integer> ingredientsInCauldron) {
        for (O2PotionType potionType : O2PotionType.values()) {
            O2Potion potion = getPotionFromType(potionType);

            if (potion != null && potion.checkRecipe(ingredientsInCauldron))
                return potion;
        }

        return null;
    }

    /**
     * Get all O2Item ingredients within a 1-block radius of the cauldron. Non-O2Items are ignored; multiple stacks of
     * the same ingredient are summed.
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
     * Find an O2Potion from an ItemStack's NBT metadata. Delegates to {@link #findPotionByItemMeta(ItemMeta)}.
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
     * Find an O2Potion by its type in item NBT tags, handling both current (display name) and legacy (enum name)
     * potion type formats.
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
     * Create an O2Potion instance from its type via reflection.
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
     * Check if a potion type is currently loaded. A potion is not loaded if its optional dependency (e.g.
     * LibsDisguises) is not installed.
     *
     * @param potionType the potion type to check
     * @return true if the potion type is loaded, false if disabled due to missing dependencies
     */
    public boolean isLoaded(@NotNull O2PotionType potionType) {
        return O2PotionMap.containsValue(potionType);
    }

    /**
     * Get the magic difficulty level for a Minecraft potion effect type, used by FINITE_INCANTATEM to decide whether
     * an effect can be dispelled.
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
     * Create a potion ItemStack directly, bypassing all brewing and experience checks. For player-initiated brewing
     * use {@link #checkRecipeAndBrew(Block, Player)} instead.
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

    /**
     * Apply an Ollivanders2 potion's effect when a player drinks it. Ignores anything that is not a tagged O2 potion,
     * and skips potions whose optional dependency (e.g. LibsDisguises) is not loaded. The effect is applied after a
     * short delay and only if the consume was not cancelled.
     *
     * @param event the player item consume event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDrink(@NotNull PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();

        if (item.getType() == Material.POTION) {
            Player player = event.getPlayer();
            common.printDebugMessage("O2Potions.onPlayerDrink: " + player.getDisplayName() + " drank a potion.", null, null, false);

            ItemMeta meta = item.getItemMeta();
            if (meta == null)
                return;

            PersistentDataContainer container = meta.getPersistentDataContainer();

            if (container.has(potionTypeKey, PersistentDataType.STRING) || meta.hasLore()) {
                O2Potion potion = findPotionByItemMeta(meta);
                if (potion != null) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!event.isCancelled()) {
                                if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.requiresLibsDisguises(potion.getPotionType()))
                                    return;

                                potion.drink(player);
                            }
                        }
                    }.runTaskLater(p, OllivandersListener.getThreadDelay());
                }
            }
        }
    }

    /**
     * Apply an {@link O2SplashPotion}'s impact effect when its thrown potion breaks. Does nothing for a splash potion
     * that is not an O2 splash potion.
     *
     * @param event the potion splash event
     * @apiNote Must run synchronously during the event: the impact effect may modify the splash (e.g. adjust per-entity
     * intensity), which has no effect once the event has been dispatched.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onSplashPotion(@NotNull PotionSplashEvent event) {
        ThrownPotion thrown = event.getEntity();
        ItemMeta meta = thrown.getItem().getItemMeta();
        if (meta == null)
            return;

        O2Potion potion = findPotionByItemMeta(meta);

        if (potion != null) {
            if (potion instanceof O2SplashPotion) {
                ((O2SplashPotion) potion).doOnPotionSplashEvent(event);
            }
        }
    }

    /**
     * Add an ingredient to a cauldron: when a player sneaks while facing a water cauldron, drop the item in their
     * off-hand into it. No-ops if the player is not sneaking, not facing a water cauldron, or has an empty off-hand.
     * On success drops the item as an entity in the cauldron, clears the player's off-hand, and messages them.
     *
     * @param event the player toggle sneak event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPotionBrewing(@NotNull PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        // is the player sneaking
        if (!event.isSneaking())
            return;

        Block cauldron = Ollivanders2Common.playerFacingBlockType(player, Material.WATER_CAULDRON);
        if (cauldron == null)
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!event.isCancelled())
                    addIngredientToCauldron(player, cauldron);
            }
        }.runTaskLater(p, 5);
    }

    /**
     * Add the item in the player's off-hand to a cauldron as a potion ingredient: drop it into the cauldron, clear the
     * player's off-hand, and message them. No-ops if the off-hand is empty.
     *
     * @param player   the player adding the ingredient
     * @param cauldron the cauldron to add the ingredient to
     */
    public void addIngredientToCauldron(@NotNull Player player, @NotNull Block cauldron) {
        ItemStack heldItem = player.getInventory().getItemInOffHand();
        if (heldItem.getType() == Material.AIR || heldItem.getAmount() == 0)
            return;

        ItemMeta meta = heldItem.getItemMeta();
        if (meta == null)
            return;

        // fall back to the material name for an un-renamed item, whose display name is empty
        String ingredientName = meta.getDisplayName();
        if (ingredientName.isEmpty())
            ingredientName = heldItem.getType().name();

        Location spawnLoc = cauldron.getLocation().clone();
        World world = cauldron.getWorld();

        Item item = world.dropItem(spawnLoc.add(0.5, 0.5, 0.5), heldItem.clone());
        player.sendMessage(Ollivanders2.chatColor + "Added " + ingredientName);

        item.setVelocity(new Vector(0, 0, 0));
        player.getInventory().setItemInOffHand(null);
    }
}
