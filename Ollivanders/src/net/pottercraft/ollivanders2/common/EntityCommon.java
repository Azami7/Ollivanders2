package net.pottercraft.ollivanders2.common;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility class providing static lists and methods for working with Minecraft entities.
 * <p>
 * Provides:
 * <ul>
 * <li>Pre-defined entity type lists (undead, minecarts, boats)</li>
 * <li>Methods to find entities by type, material, or location</li>
 * <li>Random entity attribute generators (colors, styles, types)</li>
 * <li>Entity classification methods (hostile detection)</li>
 * </ul>
 *
 * @author Azami7
 */
public class EntityCommon {
    /**
     * Undead entities (for use with magic that targets undead)
     */
    private static final List<EntityType> undeadMobs = new ArrayList<>() {{
        add(EntityType.BOGGED);
        //add(EntityType.CAMEL_HUSK); MC 26
        add(EntityType.DROWNED);
        add(EntityType.GIANT);
        add(EntityType.HUSK);
        //add(EntityType.PARCHED); MC 26
        add(EntityType.PHANTOM);
        add(EntityType.SKELETON);
        add(EntityType.SKELETON_HORSE);
        add(EntityType.STRAY);
        add(EntityType.WITHER);
        add(EntityType.WITHER_SKELETON);
        add(EntityType.ZOGLIN);
        add(EntityType.ZOMBIE);
        add(EntityType.ZOMBIE_HORSE);
        //add(EntityType.ZOMBIE_NAUTILUS); MC 26
        add(EntityType.ZOMBIE_VILLAGER);
        add(EntityType.ZOMBIFIED_PIGLIN);
    }};

    /**
     * Skeleton entities (for use with magic that targets skeletons)
     */
    private static final List<EntityType> skeletonMobs = new ArrayList<>() {{
        add(EntityType.BOGGED);
        //add(EntityType.PARCHED); MC 26
        add(EntityType.SKELETON);
        add(EntityType.SKELETON_HORSE);
        add(EntityType.STRAY);
        add(EntityType.WITHER_SKELETON);
    }};

    /**
     * Fire entities (for use with magic that targets fire mobs)
     */
    private static final List<EntityType> fireMobs = new ArrayList<>() {{
        add(EntityType.BLAZE);
        add(EntityType.MAGMA_CUBE);
        add(EntityType.GHAST);
        add(EntityType.ENDER_DRAGON);
    }};

    /**
     * Reasons an entity may get damaged which are caused by an attack (rather than falling, etc.)
     */
    private static final List<EntityDamageEvent.DamageCause> attackDamageCauses = new ArrayList<>() {{
        add(EntityDamageEvent.DamageCause.DRAGON_BREATH);
        add(EntityDamageEvent.DamageCause.ENTITY_ATTACK);
        add(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK);
        add(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION);
        add(EntityDamageEvent.DamageCause.MAGIC);
        add(EntityDamageEvent.DamageCause.POISON);
        add(EntityDamageEvent.DamageCause.PROJECTILE);
        add(EntityDamageEvent.DamageCause.SONIC_BOOM);
        add(EntityDamageEvent.DamageCause.THORNS);
        add(EntityDamageEvent.DamageCause.WITHER);
    }};

    /**
     * All minecart entity types. Populated lazily by {@link #initEntityLists()}.
     */
    private static final List<org.bukkit.entity.EntityType> minecarts = new ArrayList<>();

    /**
     * All boat entity types. Populated lazily by {@link #initEntityLists()}.
     */
    private static final List<org.bukkit.entity.EntityType> boats = new ArrayList<>();

    /**
     * Reference to the plugin object
     */
    final private Ollivanders2 p;

    /**
     * Utility class for common operations and debug message printing
     */
    final private Ollivanders2Common common;

    /**
     * Populate the static boat and minecart type lists. Idempotent: each list is filled only if still empty, so
     * repeated calls are harmless. Must run before {@link #getBoats()} or {@link #getMinecarts()} return any data.
     */
    public static void initEntityLists() {
        if (boats.isEmpty()) {
            boats.addAll(getallEntitiesThatEndWith("_BOAT"));
            boats.addAll(getallEntitiesThatEndWith("_RAFT"));
        }

        if (minecarts.isEmpty()) {
            minecarts.addAll(getallEntitiesThatEndWith("_MINECART"));
        }
    }

    /**
     * Get all entity types whose enum name ends with the given suffix.
     *
     * @param endsWith the suffix to match, e.g. "_BOAT"; matched against the entity type's enum name
     * @return the matching entity types; empty if none match
     */
    @NotNull
    public static List<EntityType> getallEntitiesThatEndWith(@NotNull String endsWith) {
        ArrayList<EntityType> entityTypes = new ArrayList<>();

        for (EntityType entityType : EntityType.values()) {
            if (entityType.toString().endsWith(endsWith)) {
                entityTypes.add(entityType);
            }
        }

        return entityTypes;
    }

    /**
     * Create an entity utility bound to the given plugin.
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
     * Get the item entity at a specific location
     *
     * @param location the location to get the item from
     * @return the Item entity or null if none present at the location
     */
    @Nullable
    public static Item getItemAtLocation(@NotNull Location location) {
        if (location.getWorld() == null)
            return null;

        for (Entity entity : location.getWorld().getEntities()) {
            if (entity instanceof Item && entity.getLocation().equals(location)) {
                return (Item) entity;
            }
        }

        return null;
    }

    /**
     * Get the living entity at a specific location
     *
     * @param location the location to get the living entity at
     * @return the living entity or null if none present at the location
     */
    @Nullable
    public static LivingEntity getLivingEntityAtLocation(@NotNull Location location) {
        if (location.getWorld() == null)
            return null;

        for (Entity entity : location.getWorld().getEntities()) {
            if (entity instanceof LivingEntity && entity.getLocation().equals(location)) {
                return (LivingEntity) entity;
            }
        }

        return null;
    }

    /**
     * Gets item entities within a bounding box around a location.
     *
     * @param location the center location for the bounding box
     * @param x        the distance +/- on the x-plane
     * @param y        the distance +/- on the y-plane
     * @param z        the distance +/- on the z-plane
     * @return list of item entities within the bounding box
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
     * Gets item entities within a radius of a location.
     *
     * @param location the center location
     * @param radius   the radius to search within
     * @return list of item entities within the radius
     */
    @NotNull
    static public List<Item> getItemsInRadius(@NotNull Location location, double radius) {
        return getItemsInBounds(location, radius, radius, radius);
    }

    /**
     * Get an item by material
     *
     * @param location the location to check
     * @param material the material to look for
     * @param radius   the radius to look in
     * @return an item if found, null otherwise
     */
    @Nullable
    static public Item getNearbyItemByMaterial(@NotNull Location location, @NotNull Material material, double radius) {
        List<Item> items = getItemsInRadius(location, radius);

        for (Item item : items) {
            if (item.getItemStack().getType() == material)
                return item;
        }

        return null;
    }

    /**
     * Get an item by material
     *
     * @param location  the location to check
     * @param materials the list of materials to look for
     * @param radius    the radius to look in
     * @return an item if found, null otherwise
     */
    @Nullable
    static public Item getNearbyItemByMaterialList(@NotNull Location location, @NotNull ArrayList<Material> materials, double radius) {
        List<Item> items = getItemsInRadius(location, radius);

        for (Item item : items) {
            if (materials.contains(item.getItemStack().getType()))
                return item;
        }

        return null;
    }

    /**
     * Get nearby items by O2ItemType
     *
     * @param location the location to check
     * @param itemType the item type to get
     * @param radius   the radius to look in
     * @return the item if found, null otherwise
     */
    @Nullable
    static public Item getNearbyO2ItemByType(@NotNull Location location, @NotNull O2ItemType itemType, double radius) {
        List<Item> items = getItemsInRadius(location, radius);

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
        // nextInt() rejects zero and negative bounds, so force seed to a positive, non-zero value
        seed = Math.abs(seed);
        if (seed == 0)
            seed = 1;

        int rand = Math.abs(Ollivanders2Common.random.nextInt(seed)) % Cat.Type.values().length;

        return Cat.Type.values()[rand];
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
     * Get a random fox type. Odds are 1/10 to get a snow fox.
     *
     * @param seed the base value that the percentile check will use
     * @return the fox type
     */
    @NotNull
    static public Fox.Type getRandomFoxType(int seed) {
        // nextInt() rejects zero and negative bounds, so force seed to a positive, non-zero value
        seed = Math.abs(seed);
        if (seed == 0)
            seed = 1;

        // Mod 10 to make SNOW rare (1/10 chance)
        int rand = Math.abs(Ollivanders2Common.random.nextInt(seed)) % 10;

        if (rand == 0)
            return Fox.Type.SNOW;

        return Fox.Type.RED;
    }

    /**
     * Get a random fox type. Odds are 1/10 to get a snow fox.
     *
     * @return the fox type
     */
    @NotNull
    static public Fox.Type getRandomFoxType() {
        return getRandomFoxType((int) TimeCommon.getDefaultWorldTime());
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

        // nextInt() cannot accept 0, so ensure seed is positive and non-zero
        seed = Math.abs(seed);
        if (seed == 0)
            seed = 1;

        // Mod 61 to make THE_KILLER_BUNNY rare (1/60 chance, cases 60)
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

        // nextInt() cannot accept 0, so ensure seed is positive and non-zero
        seed = Math.abs(seed);
        if (seed == 0)
            seed = 1;

        // Mod 20 to make Horse.Style.NONE most common (16 out of 20 cases, 80% chance)
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
        seed = Math.abs(seed);
        if (seed == 0)
            seed = 1;

        int rand = Math.abs(Ollivanders2Common.random.nextInt(seed)) % Horse.Color.values().length;

        return Horse.Color.values()[rand];
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
        seed = Math.abs(seed);
        if (seed == 0)
            seed = 1;

        int rand = Math.abs(Ollivanders2Common.random.nextInt(seed)) % Llama.Color.values().length;

        return Llama.Color.values()[rand];
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
     * Generates a random parrot variant.
     *
     * @param seed the base value used with random number generation for variant selection
     * @return a random parrot variant
     */
    @NotNull
    static public Parrot.Variant getRandomParrotVariant(int seed) {
        seed = Math.abs(seed);
        if (seed == 0)
            seed = 1;

        int rand = Math.abs(Ollivanders2Common.random.nextInt(seed)) % Parrot.Variant.values().length;

        return Parrot.Variant.values()[rand];
    }

    /**
     * Generates a random parrot variant using the default world time as seed.
     *
     * @return a random parrot variant
     */
    @NotNull
    static public Parrot.Variant getRandomParrotVariant() {
        return getRandomParrotVariant((int) TimeCommon.getDefaultWorldTime());
    }

    /**
     * Generate a random natural sheep color
     *
     * @param seed the base value that the percentile check will use
     * @return the color
     */
    @NotNull
    static public DyeColor getRandomNaturalSheepColor(int seed) {
        seed = Math.abs(seed);
        if (seed == 0)
            seed = 1;

        // Mod 100 provides a percentile range (0-99). Sheep should generally be white (68% chance).
        int rand = Math.abs(Ollivanders2Common.random.nextInt(seed)) % 100;

        if (rand < 2) // 2% chance
            return DyeColor.BLACK;
        else if (rand < 22) // 20% chance
            return DyeColor.BROWN;
        else if (rand < 32) // 10% chance
            return DyeColor.LIGHT_GRAY;
        else // 68% chance - white is the most common natural sheep color
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
     * Is this living entity a hostile? This assumes Player entities are not hostile.
     *
     * @param livingEntity the entity to check
     * @return true if it is a hostile or angry mob, false otherwise
     * @see <a href="https://minecraft.fandom.com/wiki/Mob#Hostile_mobs">Minecraft Wiki - Hostile mobs</a>
     */
    static public boolean isHostile(@NotNull LivingEntity livingEntity) {
        if (livingEntity instanceof Enemy)
            return true;

        // a neutral mob that has picked a target is currently hostile
        if (livingEntity instanceof Mob && ((Mob) livingEntity).getTarget() != null)
            return true;

        return false;
    }

    /**
     * Get the distance to the water surface if an entity is underwater.
     *
     * @param entity the entity to measure
     * @return the distance in blocks from the entity to the water surface, capped at 128; 0 if not underwater
     * @see <a href="https://minecraft.wiki/w/Altitude">Minecraft Wiki - Altitude</a>
     */
    public static int distanceToSurface(Entity entity) {
        if (!entity.isUnderWater())
            return 0;

        Block above = entity.getLocation().getBlock().getRelative(BlockFace.UP);

        // limit to 128 since this is the smallest world max height (being the max height of The Nether)
        int loopLimit = 128;

        int distance = 0;
        while (above.getType() == Material.WATER && distance < loopLimit) {
            distance = distance + 1;
            above = above.getRelative(BlockFace.UP);
        }

        return distance;
    }

    /**
     * Determines whether an entity type is an undead mob.
     *
     * @param entityType the entity type to check
     * @return true if the entity type is an undead mob, false otherwise
     */
    public static boolean isUndeadMob(EntityType entityType) {
        return undeadMobs.contains(entityType);
    }

    /**
     * Determines whether an entity type is a fire-based mob.
     *
     * @param entityType the entity type to check
     * @return true if the entity type is a fire mob, false otherwise
     */
    public static boolean isFireMob(EntityType entityType) {
        return fireMobs.contains(entityType);
    }

    /**
     * Determines whether an entity type is a skeleton.
     *
     * @param entityType the entity type to check
     * @return true if the entity type is a skeleton, false otherwise
     */
    public static boolean isSkeleton(EntityType entityType) {
        return skeletonMobs.contains(entityType);
    }

    /**
     * Determines whether a damage cause is from an attack.
     *
     * @param damageCause the damage cause to check
     * @return true if the damage cause is an attack type, false otherwise
     */
    public static boolean isDamageCausedByAttack(EntityDamageEvent.DamageCause damageCause) {
        return attackDamageCauses.contains(damageCause);
    }

    /**
     * Gets a copy of all minecart entity types.
     *
     * @return a list of all minecart entity types
     */
    public static List<EntityType> getMinecarts() {
        return new ArrayList<>() {{
            addAll(minecarts);
        }};
    }

    /**
     * Gets a copy of all boat entity types.
     *
     * @return a list of all boat entity types
     */
    public static List<EntityType> getBoats() {
        return new ArrayList<>() {{
            addAll(boats);
        }};
    }

    /**
     * Shoots a firework rocket with the specified effects at the location.
     *
     * @param location the starting location for the rocket
     * @param hasTrails does this firework have trails
     * @param hasFade does this firework do fade
     * @param hasFlicker does this firework have flicker
     * @param fireworkPower the power for the firework, power of 1 or 2 is a normal firework
     * @param fireworkColors the list of colors for the firework
     * @param fadeColors the colors the firework fades to; applied only when hasFade is true
     * @param fireworkType the firework shape
     */
    public static void shootFirework(Location location, boolean hasTrails, boolean hasFade, boolean hasFlicker, int fireworkPower, @NotNull List<Color> fireworkColors, @Nullable List<Color> fadeColors, @NotNull FireworkEffect.Type fireworkType) {
        World world = location.getWorld();
        if (world == null) {
            Ollivanders2API.common.printDebugMessage("Pyrotechnia.checkEffect: world is null", null, null, true);
            return;
        }

        Firework firework = (Firework) (world.spawnEntity(location, EntityType.FIREWORK_ROCKET));

        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(fireworkPower);

        FireworkEffect.Builder builder = FireworkEffect.builder();

        builder.withColor(fireworkColors);
        builder.with(fireworkType);

        builder.flicker(hasFlicker);
        builder.trail(hasTrails);

        if (hasFade) {
            if (fadeColors != null && !fadeColors.isEmpty())
                builder.withFade(fadeColors);
        }

        meta.addEffect(builder.build());
        firework.setFireworkMeta(meta);
    }
}
