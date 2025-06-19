package net.pottercraft.ollivanders2.common;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Common functions that deal with MC entities
 */
public class EntityCommon {
    /**
     * Undead entities (for use with magic that targets undead)
     */
    public static final List<EntityType> undeadEntities = new ArrayList<>() {{
        add(EntityType.BOGGED);
        add(EntityType.DROWNED);
        add(EntityType.GIANT);
        add(EntityType.HUSK);
        add(EntityType.PHANTOM);
        add(EntityType.SKELETON);
        add(EntityType.SKELETON_HORSE);
        add(EntityType.STRAY);
        add(EntityType.WITHER);
        add(EntityType.WITHER_SKELETON);
        add(EntityType.ZOMBIE);
        add(EntityType.ZOMBIE_HORSE);
        add(EntityType.ZOMBIE_VILLAGER);
        add(EntityType.ZOMBIFIED_PIGLIN);
        add(EntityType.ZOGLIN);
    }};

    /**
     * Reasons an entity may get damaged which are caused by an attack (rather than falling, etc.)
     */
    public static final List<EntityDamageEvent.DamageCause> attackDamageCauses = new ArrayList<>() {{
        add(EntityDamageEvent.DamageCause.DRAGON_BREATH);
        add(EntityDamageEvent.DamageCause.ENTITY_ATTACK);
        add(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK);
        add(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION);
        add(EntityDamageEvent.DamageCause.PROJECTILE);
        add(EntityDamageEvent.DamageCause.THORNS);
    }};

    /**
     * All minecart entity types.
     */
    public static final List<org.bukkit.entity.EntityType> minecarts = new ArrayList<>() {{
        add(EntityType.FURNACE_MINECART);
        add(EntityType.MINECART);
        add(EntityType.CHEST_MINECART);
        add(EntityType.COMMAND_BLOCK_MINECART);
        add(EntityType.HOPPER_MINECART);
        add(EntityType.SPAWNER_MINECART);
        add(EntityType.TNT_MINECART);
    }};

    /**
     * All boat entity types.
     */
    public static final List<org.bukkit.entity.EntityType> boats = new ArrayList<>() {{
        add(EntityType.ACACIA_BOAT);
        add(EntityType.ACACIA_CHEST_BOAT);
        add(EntityType.BIRCH_BOAT);
        add(EntityType.BIRCH_CHEST_BOAT);
        add(EntityType.CHERRY_BOAT);
        add(EntityType.CHERRY_CHEST_BOAT);
        add(EntityType.DARK_OAK_BOAT);
        add(EntityType.DARK_OAK_BOAT);
        add(EntityType.JUNGLE_BOAT);
        add(EntityType.JUNGLE_CHEST_BOAT);
        add(EntityType.MANGROVE_BOAT);
        add(EntityType.MANGROVE_CHEST_BOAT);
        add(EntityType.OAK_BOAT);
        add(EntityType.OAK_CHEST_BOAT);
        add(EntityType.PALE_OAK_BOAT);
        add(EntityType.PALE_OAK_CHEST_BOAT);
        add(EntityType.SPRUCE_BOAT);
        add(EntityType.SPRUCE_CHEST_BOAT);
        add(EntityType.BAMBOO_RAFT);
        add(EntityType.BAMBOO_CHEST_RAFT);
    }};

    /**
     * The magic level for every potion effect type, primarily for use with Finite Incantatum
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
     * Reference to the plugin object
     */
    final private Ollivanders2 p;

    /**
     * Common functions
     */
    final private Ollivanders2Common common;

    /**
     * Constructor
     *
     * @param plugin a reference to the plugin using this common
     */
    public EntityCommon(@NotNull Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(p);
    }

    /**
     * Get an EntityType enum from a string.
     *
     * @param entityTypeString the entity type as a string
     * @return the EntityType or null if an exception occurred
     */
    @Nullable
    public EntityType entityTypeFromString(@NotNull String entityTypeString) {
        EntityType entityType = null;

        try {
            entityType = EntityType.valueOf(entityTypeString);
        }
        catch (Exception e) {
            common.printDebugMessage("Failed to parse EntityType " + entityTypeString, e, null, true);
        }
        return entityType;
    }

    /**
     * Get all the entities with a bounding box of a specific location.
     *
     * @param location the location to check from
     * @param x        the distance +/- on the x-plane
     * @param y        the distance +/- on the y-plane
     * @param z        the distance +/- on the z-plane
     * @return a Collection of all entities within the bounding box.
     */
    @NotNull
    static public Collection<Entity> getEntitiesInBounds(@NotNull Location location, double x, double y, double z) {
        World world = location.getWorld();
        if (world == null)
            return new ArrayList<>();

        return world.getNearbyEntities(location, x, y, z);
    }

    /**
     * Gets all entities within a radius of a specific location
     *
     * @param location the location to search for entities from
     * @param radius   radius within which to get entities
     * @return List of entities within the radius
     */
    @NotNull
    static public Collection<Entity> getEntitiesInRadius(@NotNull Location location, double radius) {
        return getEntitiesInBounds(location, radius, radius, radius);
    }

    /**
     * Gets all living entities within a radius of a specific location
     *
     * @param location the location to search for entities from
     * @param radius   radius within which to get entities
     * @return List of living entities within the radius
     */
    @NotNull
    static public List<LivingEntity> getLivingEntitiesInRadius(@NotNull Location location, double radius) {
        Collection<Entity> entities = getEntitiesInRadius(location, radius);
        List<LivingEntity> close = new ArrayList<>();

        for (Entity e : entities) {
            if (e instanceof LivingEntity)
                close.add(((LivingEntity) e));
        }

        return close;
    }

    /**
     * Gets all of a specific entity type within a radius of a specific location
     *
     * @param location   the location to search for entities from
     * @param radius     radius within which to get entities
     * @param entityType the type of entity to look for
     * @return List of entities of the specified type within the radius
     */
    @NotNull
    static public List<Entity> getNearbyEntitiesByType(@NotNull Location location, double radius, @NotNull EntityType entityType) {
        Collection<Entity> entities = getEntitiesInRadius(location, radius);
        List<Entity> close = new ArrayList<>();

        for (Entity e : entities) {
            if (e.getType() == entityType)
                close.add(e);
        }
        return close;
    }

    /**
     * Gets item entities within bounding box of the projectile
     *
     * @return List of item entities within bounding box of projectile
     */
    @NotNull
    static public List<Item> getItemsInBounds(@NotNull Location location, double x, double y, double z) {
        Collection<Entity> entities = getEntitiesInBounds(location, x, y, z);

        List<Item> items = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof Item)
                items.add((Item) e);
        }

        return items;
    }

    /**
     * Gets item entities within radius of the projectile
     *
     * @return List of item entities within radius of projectile
     */
    @NotNull
    static public List<Item> getItemsInRadius(@NotNull Location location, double radius) {
        return getItemsInBounds(location, radius, radius, radius);
    }

    /**
     * Gets item entities within radius of the projectile
     *
     * @param radius radius within which to get entities
     * @return List of item entities within one block of projectile
     */
    @NotNull
    static public List<Item> getItems(@NotNull Location location, double radius) {
        return getItemsInBounds(location, radius, radius, radius);
    }

    /**
     * Get an item by material
     *
     * @param material the material to look for
     * @param radius   the radius to look in
     * @return an item if found, null otherwise
     */
    @Nullable
    static public Item getNearbyItemByMaterial(@NotNull Location location, @NotNull Material material, double radius) {
        List<Item> items = getItems(location, radius);

        for (Item item : items) {
            if (item.getItemStack().getType() == material)
                return item;
        }

        return null;
    }

    /**
     * Get an item by material
     *
     * @param materials the list of materials to look for
     * @param radius    the radius to look in
     * @return an item if found, null otherwise
     */
    @Nullable
    static public Item getNearbyItemByMaterialList(@NotNull Location location, @NotNull ArrayList<Material> materials, double radius) {
        List<Item> items = getItems(location, radius);

        for (Item item : items) {
            if (materials.contains(item.getItemStack().getType()))
                return item;
        }

        return null;
    }

    /**
     * Get nearby items by O2ItemType
     *
     * @param itemType the item type to get
     * @param radius   the radius to look in
     * @return the item if found, null otherwise
     */
    @Nullable
    static public Item getNearbyItemByType(@NotNull Location location, @NotNull O2ItemType itemType, double radius) {
        List<Item> items = getItems(location, radius);

        for (Item item : items) {
            if (itemType.isItemThisType(item))
                return item;
        }

        return null;
    }

    /**
     * Generate a random Cat type.
     *
     * @param seed the base value that the percentile check will use
     * @return the Cat type
     */
    @NotNull
    static public Cat.Type getRandomCatType(int seed) {
        int rand = Math.abs(Ollivanders2Common.random.nextInt(seed)) % 11;

        Cat.Type type;

        switch (rand) {
            case 0:
                type = Cat.Type.ALL_BLACK;
                break;
            case 1:
                type = Cat.Type.BLACK;
                break;
            case 2:
                type = Cat.Type.BRITISH_SHORTHAIR;
                break;
            case 3:
                type = Cat.Type.CALICO;
                break;
            case 4:
                type = Cat.Type.JELLIE;
                break;
            case 5:
                type = Cat.Type.PERSIAN;
                break;
            case 6:
                type = Cat.Type.RAGDOLL;
                break;
            case 7:
                type = Cat.Type.RED;
                break;
            case 8:
                type = Cat.Type.SIAMESE;
                break;
            case 9:
                type = Cat.Type.TABBY;
                break;
            default:
                type = Cat.Type.WHITE;
                break;
        }

        return type;
    }

    /**
     * Generate a random Cat type.
     *
     * @return the Cat type
     */
    @NotNull
    static public Cat.Type getRandomCatType() {
        return getRandomCatType((int) TimeCommon.getDefaultWorldTime());
    }

    /**
     * Get a random rabbit type. Odds are 1/60 to get a Killer Bunny.
     *
     * @param seed the base value that the percentile check will use
     * @return the type color for the rabbit
     */
    @NotNull
    static public Rabbit.Type getRandomRabbitType(int seed) {
        Rabbit.Type type;

        int rand = Math.abs(Ollivanders2Common.random.nextInt(seed)) % 61;

        if (rand < 10)
            type = Rabbit.Type.BROWN;
        else if (rand < 20)
            type = Rabbit.Type.BLACK;
        else if (rand < 30)
            type = Rabbit.Type.WHITE;
        else if (rand < 40)
            type = Rabbit.Type.GOLD;
        else if (rand < 50)
            type = Rabbit.Type.BLACK_AND_WHITE;
        else if (rand < 60)
            type = Rabbit.Type.SALT_AND_PEPPER;
        else
            type = Rabbit.Type.THE_KILLER_BUNNY;

        return type;
    }

    /**
     * Get a random rabbit type. Odds are 1/60 to get a Killer Bunny.
     *
     * @return the type color for the rabbit
     */
    @NotNull
    static public org.bukkit.entity.Rabbit.Type getRandomRabbitType() {
        return getRandomRabbitType((int) TimeCommon.getDefaultWorldTime());
    }

    /**
     * Generate a random horse style.
     *
     * @param seed the base value that the percentile check will use
     * @return the horse style
     */
    @NotNull
    static public Horse.Style getRandomHorseStyle(int seed) {
        Horse.Style style;

        int rand = Math.abs(Ollivanders2Common.random.nextInt(seed)) % 20;

        switch (rand) {
            case 0:
                style = Horse.Style.WHITE;
                break;
            case 1:
                style = Horse.Style.WHITE_DOTS;
                break;
            case 2:
                style = Horse.Style.WHITEFIELD;
                break;
            case 3:
                style = Horse.Style.BLACK_DOTS;
                break;
            default:
                style = Horse.Style.NONE;
                break;
        }

        return style;
    }

    /**
     * Generate a random horse style.
     *
     * @return the horse style
     */
    @NotNull
    static public Horse.Style getRandomHorseStyle() {
        return getRandomHorseStyle((int) TimeCommon.getDefaultWorldTime());
    }

    /**
     * Generate a random horse color.
     *
     * @param seed the base value that the percentile check will use
     * @return the color
     */
    @NotNull
    static public Horse.Color getRandomHorseColor(int seed) {
        Horse.Color color;

        int rand = Math.abs(Ollivanders2Common.random.nextInt(seed)) % 7;

        switch (rand) {
            case 0:
                color = Horse.Color.BLACK;
                break;
            case 1:
                color = Horse.Color.BROWN;
                break;
            case 2:
                color = Horse.Color.CHESTNUT;
                break;
            case 3:
                color = Horse.Color.CREAMY;
                break;
            case 4:
                color = Horse.Color.DARK_BROWN;
                break;
            case 5:
                color = Horse.Color.GRAY;
                break;
            default:
                color = Horse.Color.WHITE;
                break;
        }

        return color;
    }

    /**
     * Generate a random horse color.
     *
     * @return the color
     */
    @NotNull
    static public Horse.Color getRandomHorseColor() {
        return getRandomHorseColor((int) TimeCommon.getDefaultWorldTime());
    }

    /**
     * Generate random llama color.
     *
     * @param seed the base value that the percentile check will use
     * @return the color
     */
    @NotNull
    static public Llama.Color getRandomLlamaColor(int seed) {
        Llama.Color color;

        int rand = Math.abs(Ollivanders2Common.random.nextInt(seed)) % 4;

        switch (rand) {
            case 0:
                color = Llama.Color.BROWN;
                break;
            case 1:
                color = Llama.Color.CREAMY;
                break;
            case 2:
                color = Llama.Color.GRAY;
                break;
            default:
                color = Llama.Color.WHITE;
                break;
        }

        return color;
    }

    /**
     * Generate random llama color.
     *
     * @return the color
     */
    @NotNull
    static public Llama.Color getRandomLlamaColor() {
        return getRandomLlamaColor((int) TimeCommon.getDefaultWorldTime());
    }

    /**
     * Genenrate a random parrot color.
     *
     * @param seed the base value that the percentile check will use
     * @return the color
     */
    @NotNull
    static public Parrot.Variant getRandomParrotColor(int seed) {
        Parrot.Variant variant;

        int rand = Math.abs(Ollivanders2Common.random.nextInt(seed)) % 5;

        switch (rand) {
            case 0:
                variant = Parrot.Variant.CYAN;
                break;
            case 1:
                variant = Parrot.Variant.GRAY;
                break;
            case 2:
                variant = Parrot.Variant.BLUE;
                break;
            case 3:
                variant = Parrot.Variant.GREEN;
                break;
            default:
                variant = Parrot.Variant.RED;
        }

        return variant;
    }

    /**
     * Genenrate a random parrot color.
     *
     * @return the color
     */
    @NotNull
    static public Parrot.Variant getRandomParrotColor() {
        return getRandomParrotColor((int) TimeCommon.getDefaultWorldTime());
    }

    /**
     * Generate a random natural sheep color
     *
     * @param seed the base value that the percentile check will use
     * @return the color
     */
    @NotNull
    static public DyeColor getRandomNaturalSheepColor(int seed) {
        int rand = Math.abs(Ollivanders2Common.random.nextInt(seed)) % 100;

        if (rand < 2) // 2% chance
            return DyeColor.BLACK;
        else if (rand < 22) // 20% chance
            return DyeColor.BROWN;
        else if (rand < 32) // 10% chance
            return DyeColor.LIGHT_GRAY;
        else
            return DyeColor.WHITE;
    }

    /**
     * Generate a random natural sheep color
     *
     * @return the color
     */
    @NotNull
    static public DyeColor getRandomNaturalSheepColor() {
        return getRandomNaturalSheepColor((int) TimeCommon.getDefaultWorldTime());
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
