package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.MagicLevel;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;

import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.spell.events.OllivandersSpellProjectileMoveEvent;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.protection.flags.StateFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for all Ollivanders2 spells.
 *
 * <p>Provides core spell functionality including projectile movement, collision detection, validation,
 * cooldowns, and basic lifecycle management. All spells cast by players extend this class and must
 * implement spell-specific behavior in {@link #doCheckEffect()}.</p>
 *
 * <p><strong>Spell Lifecycle:</strong></p>
 * <ol>
 * <li>Spell is instantiated with player and wand information</li>
 * <li>{@link #initSpell()} is called to perform player-based initialization (uses, skills, etc.)</li>
 * <li>Each game tick, {@link #checkEffect()} is called to update spell state</li>
 * <li>Projectile moves via {@link #move()} until it hits a block or reaches max distance</li>
 * <li>Spell-specific effects are applied via {@link #doCheckEffect()}</li>
 * <li>Spell is terminated via {@link #kill()}, which calls {@link #revert()} for cleanup</li>
 * </ol>
 *
 * <p><strong>Spell Validation:</strong> Spells are validated against Ollivanders2 configuration settings
 * and WorldGuard permissions. Invalid spells are automatically terminated.</p>
 *
 * @author Azami7
 */
public abstract class O2Spell {
    /**
     * Max spell lifetime in gameticks, 10 minutes, this is used to ensure a code bug doesn't create never-ending
     * spells. Permanent and semi-permanent spells need to use stationary spells or effects.
     */
    public static final int maxSpellLifetime = 10 * Ollivanders2Common.ticksPerMinute;

    /**
     * The max distance a spell projectile can travel
     */
    public static final int maxProjectileDistance = 50;

    /**
     * A spell is considered mastered at level 100
     */
    public static final int spellMasteryLevel = 100;

    /**
     * The message for an isAllowed failure
     */
    public static final String isAllowedFailureMessage = "A powerful protective magic prevents you from casting this spell here.";

    /**
     * The message for cooldown spell failures
     */
    public static final String cooldownMessage = "You are too tired to cast this spell right now.";

    /**
     * The currently traveled distance for the projectile
     */
    int projectileDistance = 0;

    /**
     * The player who cast this spell.
     */
    public Player player;

    /**
     * The type this spell is.
     */
    public O2SpellType spellType;

    /**
     * The location of the spell projectile
     */
    public Location location;

    /**
     * How long has this spell been alive
     */
    private int age = 0;

    /**
     * How long this spell projectile has been alive in game ticks.
     */
    private int projectileAge = 0;

    /**
     * Whether this spell should be terminated.
     */
    private boolean kill = false;

    /**
     * The callback to the MC plugin
     */
    final Ollivanders2 p;

    /**
     * Ollivanders common functions
     */
    Ollivanders2Common common;

    /**
     * The modifier for this spell based on usage. This is for spells that change behavior based on the caster's experience.
     */
    public double usesModifier = 1;

    /**
     * The vector of the projectile.
     */
    public Vector vector;

    /**
     * Represents which wand the user was holding. See Ollivanders2Common.wandCheck()
     */
    public double rightWand;

    /**
     * Whether the projectile has hit a target
     */
    private boolean hitTarget = false;

    /**
     * Does this spell do a spell projectile or not. Generally spells will send a projectile but any spell that targets
     * the caster, such as apparate, should not.
     */
    public boolean noProjectile = false;

    /**
     * The sound this projectile makes as it moves.
     */
    Effect moveEffect = Effect.STEP_SOUND;

    /**
     * The visual effect this projectile has when it moves.
     */
    Material moveEffectData = Material.GLOWSTONE;

    /**
     * The cooldown for this spell. Spells take mental and physical energy for the caster and cannot be cast in rapid
     * succession.
     */
    static final long DEFAULT_COOLDOWN = Ollivanders2Common.ticksPerSecond * 45;

    /**
     * The branch of magic this spell is
     */
    protected O2MagicBranch branch = O2MagicBranch.CHARMS;

    /**
     * (optional) Flavor text for this spell in spellbooks, etc.
     */
    protected List<String> flavorText;

    /**
     * The label for flavor text in the localization strings in config.yml
     */
    final static String flavorTextConfigLabel = "_flavorText";

    /**
     * The description text for this spell in spell books. Required or spell cannot be written in a book.
     */
    protected String text;

    /**
     * The label for text in the localization strings in config.yml
     */
    final static String textConfigLabel = "_Text";

    /**
     * A list of block types that this spell is allowed to target. Takes precedence over deny list.
     */
    List<Material> materialAllowList = new ArrayList<>();

    /**
     * A list of block types that cannot be affected by this spell
     */
    List<Material> materialBlockedList = new ArrayList<>();

    /**
     * A list of block types that this projectile will pass through
     */
    List<Material> projectilePassThrough = new ArrayList<>();

    /**
     * A list of the worldguard permissions needed for this spell
     */
    List<StateFlag> worldGuardFlags = new ArrayList<>();

    /**
     * Message to display to the user on a successful cast of this spell.
     */
    String successMessage = null;

    /**
     * Message to display to the user on a failed cast of this spell.
     */
    String failureMessage = "Nothing seems to happen.";

    /**
     * The default radius for spells when checking for conditions
     */
    public static final double defaultRadius = 1.5;

    /**
     * Default constructor should only be used for fake instances of the spell such as when initializing the book
     * text.
     *
     * @param plugin a callback to the plugin
     */
    public O2Spell(Ollivanders2 plugin) {
        p = plugin;
    }

    /**
     * Constructor
     *
     * @param plugin    a callback to the O2 plugin
     * @param player    the player casting the spell
     * @param rightWand wand check for the player
     */
    public O2Spell(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        location = player.getEyeLocation();
        this.player = player;
        p = plugin;
        common = new Ollivanders2Common(p);

        vector = location.getDirection().normalize();
        location.add(vector);
        this.rightWand = rightWand;

        // block types that cannot be affected by any spell
        materialBlockedList.addAll(Ollivanders2Common.getUnbreakableMaterials());

        // block types that this spell's projectiles pass through
        projectilePassThrough.add(Material.AIR);
        projectilePassThrough.add(Material.CAVE_AIR);
        projectilePassThrough.add(Material.WATER);
        projectilePassThrough.add(Material.FIRE);
        projectilePassThrough.add(Material.SOUL_FIRE);
    }

    /**
     * Initialize player-dependent spell properties that cannot be determined in the constructor.
     *
     * <p>Called once during spell setup to calculate spell type, usage modifier, and perform spell-specific
     * initialization. Must be called from the most-derived spell class constructor since usage depends on
     * the specific spell type which is only available after full object construction.</p>
     */
    void initSpell() {
        if (spellType == null) {
            common.printDebugMessage("O2Spell.initSpell() spell type is null, this probably means initSpell was called in an abstract class.", null, null, true);
            kill();
            return;
        }

        setUsesModifier();

        // do spell-specific initialization
        doInitSpell();
    }

    /**
     * Spell-specific initialization logic performed once during setup.
     *
     * <p>Called from {@link #initSpell()} after general initialization. Subclasses should override this
     * to perform custom setup such as loading spell-specific data, initializing effect parameters, or
     * setting up spell behavior. The default implementation does nothing.</p>
     */
    void doInitSpell() {
    }

    /**
     * Get the world guard flags for this spell.
     *
     * @return a copy of the world guard flags
     */
    public List<StateFlag> getWorldGuardFlags() {
        return new ArrayList<>(worldGuardFlags);
    }

    /**
     * Age the spell by 1, called each game tick.
     */
    private void age() {
        age = age + 1;
    }

    /**
     * The total age of this spell.
     *
     * @return the number of ticks this spell has been alive.
     */
    public int getAge() {
        return age;
    }

    /**
     * Main game tick update called every server tick while the spell is active.
     *
     * <p>Handles core spell lifecycle logic:</p>
     * <ul>
     * <li>Validates spell is allowed at current location; kills if not</li>
     * <li>Increments spell lifetime and kills if exceeding max lifetime</li>
     * <li>Moves projectile via {@link #move()} if projectile hasn't hit a target</li>
     * <li>Executes spell-specific effects via {@link #doCheckEffect()}</li>
     * </ul>
     *
     * <p>Subclasses typically override {@link #doCheckEffect()} instead of this method.
     * Only override this method if you need to customize the spell's entire tick behavior.</p>
     */
    public void checkEffect() {
        // check whether this spell can exist
        if (!isSpellAllowed()) {
            kill();
            return;
        }

        // move the spell projectile, this will also handle if we hit a target, and age the projectile
        if (!noProjectile) {
            move();
            projectileAge = projectileAge + 1;
        }

        // run the spell-specific checkEffect actions
        doCheckEffect();

        // age the spell 1 tick
        age();
    }

    /**
     * Advances the spell projectile one block in its direction of travel.
     *
     * <p>Called each game tick while the spell is active and hasn't hit a target. Performs the following:</p>
     * <ul>
     * <li>Checks if spell has been killed or already hit target; exits early if so</li>
     * <li>Checks if projectile has exceeded max travel distance; kills spell if so</li>
     * <li>Updates projectile location and fires {@link OllivandersSpellProjectileMoveEvent}</li>
     * <li>Validates spell is still allowed at new location per config and WorldGuard</li>
     * <li>Plays movement visual effect at new location</li>
     * <li>Checks if projectile hit a solid block; stops projectile and validates block target if so</li>
     * </ul>
     *
     * <p>The projectile travels in the direction specified by {@link #vector} and can travel up to
     * {@link #maxProjectileDistance} blocks before being automatically terminated.</p>
     */
    public void move() {
        // if this is somehow called when the spell is set to killed, or we've already hit a target, do nothing
        if (isKilled() || hasHitTarget())
            return;

        // if this spell should not do a projectile, stop the projectile and return from move
        if (noProjectile) {
            stopProjectile();
            return;
        }

        // if we have gone beyond the max distance, kill this spell
        if (isAtMaxDistance()) {
            common.printDebugMessage("O2Spell.move: projectile reached max distance " + projectileAge + " without hitting a target", null, null, false);
            kill();
            return;
        }

        Location prevLoc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());

        // move the projectile and fire move event
        location.add(vector);
        p.getServer().getPluginManager().callEvent(new OllivandersSpellProjectileMoveEvent(player, this, prevLoc, location));
        projectileDistance = projectileDistance + 1;

        // determine if this spell is allowed in this location per Ollivanders2 config and WorldGuard
        if (!isSpellAllowed()) {
            common.printDebugMessage("O2Spell.move: spell not allowed here", null, null, false);
            kill();
            return;
        }

        // play the project moving effect

        World world = location.getWorld();
        if (world == null) {
            common.printDebugMessage("O2Spell.move: world null", null, null, true);
            kill();
            return;
        }

        if (!Ollivanders2.testMode)
            world.playEffect(location, moveEffect, moveEffectData);

        // check the block at this location, if it is not a pass-through block, stop the projectile and check the target
        Material targetBlockType = location.getBlock().getType();
        if (!projectilePassThrough.contains(targetBlockType)) {
            common.printDebugMessage("O2Spell.move: spell hit " + targetBlockType, null, null, false);
            stopProjectile();
            checkTargetBlock();
        }
    }

    /**
     * Check if the projectile has exceeded its maximum travel distance.
     *
     * @return true if projectile distance exceeds max distance, false otherwise
     */
    public boolean isAtMaxDistance() {
        return projectileDistance > maxProjectileDistance;
    }

    /**
     * Check the current block for use with this spell.
     * <p>
     * If the block is not on the allow list, is on the blocked list, or the spell cannot be cast in this location, kills the spell
     */
    private void checkTargetBlock() {
        // if this is somehow called before we've hit a target, do nothing
        if (!hasHitTarget())
            return;

        Block target = location.getBlock();

        // check blockAllowList, if it is empty then all block types are allowed
        if ((!materialAllowList.isEmpty()) && !(materialAllowList.contains(target.getType()))) {
            common.printDebugMessage(target.getType() + " is not in the material allow list", null, null, false);
            kill();
        }
        // check deny list
        else if (materialBlockedList.contains(target.getType())) {
            common.printDebugMessage(target.getType() + " is in the material deny list", null, null, false);
            kill();
        }
    }

    /**
     * Check to see if this spell is allowed per Ollivanders2 config and WorldGuard.
     *
     * @return true if the spell can exist here, false otherwise
     */
    public boolean isSpellAllowed() {
        boolean isAllowed = true;

        // determine if this spell is allowed in this location per Ollivanders2 config
        if (!Ollivanders2API.getSpells().isSpellTypeAllowed(location, spellType)) {
            kill();
            isAllowed = false;
        }
        // determine if spell is allowed in this location per WorldGuard
        else if (!checkWorldGuard()) {
            kill();
            isAllowed = false;
        }

        if (!isAllowed) {
            failureMessage = isAllowedFailureMessage;
            sendFailureMessage();
        }

        return isAllowed;
    }

    /**
     * Checks world guard, if enabled, to determine if this spell can be cast here.
     *
     * @return true if the spell can be cast, false otherwise
     */
    private boolean checkWorldGuard() {
        if (!Ollivanders2.worldGuardEnabled)
            return true;

        // check every flag needed for this spell
        for (StateFlag flag : worldGuardFlags) {
            if (!Ollivanders2.worldGuardO2.checkWGFlag(player, location, flag)) {
                common.printDebugMessage(spellType.toString() + " cannot be cast because of WorldGuard flag " + flag, null, null, false);
                return false;
            }
        }

        return true;
    }

    /**
     * Gets entities within a radius of current spell projectile location, excluding the caster
     *
     * @param radius radius within which to get entities
     * @return a list of entities within the radius of the projectile
     */
    @NotNull
    public List<Entity> getNearbyEntities(double radius) {
        if (radius <= 0)
            radius = 1.0;

        Collection<Entity> entities = EntityCommon.getEntitiesInRadius(location, radius);

        List<Entity> close = new ArrayList<>();

        for (Entity e : entities) {
            if (e instanceof LivingEntity) {
                if (!e.equals(player))
                    close.add(e);
            }
            else
                close.add(e);
        }

        return close;
    }

    /**
     * Get all item entities within the radius of the projectile
     *
     * @param radius radius within which to get entities
     * @return a list of item entities within the radius of projectile
     */
    @NotNull
    public List<Item> getNearbyItems(double radius) {
        return EntityCommon.getItemsInRadius(location, radius);
    }

    /**
     * Get all living entities within the radius of projectile, including the caster if within the radius
     *
     * @param radius radius within which to get entities
     * @return a list of living entities within one block of projectile
     */
    @NotNull
    public List<LivingEntity> getNearbyLivingEntities(double radius) {
        List<Entity> entities = getNearbyEntities(radius);
        List<LivingEntity> living = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof LivingEntity)
                living.add((LivingEntity) e);
        }

        // handle also adding the current player when the projectile is close to the player since getCloseEntities()
        // excludes the player
        if (Ollivanders2Common.isInside(player.getLocation(), location, (int) radius) && !entities.contains(player) && projectileAge > 1)
            living.add(player);

        return living;
    }

    /**
     * Get all players within the radius of projectile, including the caster if within the radius.
     *
     * @param radius radius within which to get entities
     * @return a list of players within the specified radius of projectile
     */
    @NotNull
    public List<Player> getNearbyPlayers(double radius) {
        List<Entity> entities = getNearbyEntities(radius);
        List<Player> players = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof Player)
                players.add((Player) e);
        }

        // handle also adding the current player when the projectile is close to the player since getCloseEntities()
        // excludes the player
        if (Ollivanders2Common.isInside(player.getLocation(), location, (int) radius) && !entities.contains(player) && projectileAge > 1)
            players.add(player);

        return players;
    }

    /**
     * Get all damageable entities within the radius of projectile, including the caster if within the radius
     *
     * @param radius the radius to check
     * @return a list of damageable entities within the radius
     */
    @NotNull
    public List<Damageable> getNearbyDamageableEntities(double radius) {
        List<Entity> entities = getNearbyEntities(radius);
        List<Damageable> damageable = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof Damageable)
                damageable.add((Damageable) e);
        }

        // handle also adding the current player when the projectile is close to the player since getCloseEntities()
        // excludes the player
        if (Ollivanders2Common.isInside(player.getLocation(), location, (int) radius) && !entities.contains(player) && projectileAge > 1)
            damageable.add(player);

        return damageable;
    }

    /**
     * Calculate and set the {@link #usesModifier} based on spell experience, wand type, and player level.
     *
     * <p>The modifier accounts for:</p>
     * <ul>
     * <li>Spell usage count (or max level if enabled)</li>
     * <li>Wand correctness (halved if wrong wand, doubled if elder wand)</li>
     * <li>HIGHER_SKILL effect (doubles modifier)</li>
     * <li>Player year/level compared to spell level (if years enabled)</li>
     * </ul>
     */
    protected void setUsesModifier() {
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        if (o2p == null) {
            common.printLogMessage("Null o2player in O2Spell.setUsesModifier", null, null, true);
            return;
        }

        // if max skill is set, set usesModifier to max level
        if (Ollivanders2.maxSpellLevel)
            usesModifier = spellMasteryLevel * 2;
        else {
            // uses modifier is the number of times the spell has been cast
            usesModifier = o2p.getSpellCount(spellType);

            // if it is not a wandless spell, uses modifier is halved if the player is not using their destined wand,
            // doubled if they are using the elder wand
            if (!O2Spells.wandlessSpells.contains(spellType))
                usesModifier = usesModifier / rightWand;

            // if the caster is affected by HIGHER_SKILL, double their usesModifier
            if (Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL))
                usesModifier *= 2;
        }

        // if years is enabled, spell usage is affected by caster's level
        if (Ollivanders2.useYears) {
            MagicLevel maxLevelForPlayer = o2p.getYear().getHighestLevelForYear();

            if (maxLevelForPlayer.ordinal() > (spellType.getLevel().ordinal() + 1))
                // double skill level when 2 or more levels above
                usesModifier *= 2;
            else if (maxLevelForPlayer.ordinal() > spellType.getLevel().ordinal())
                // 50% skill increase when 1 level above
                usesModifier *= 1.5;
                /*
                    maxLevelForPlayer.ordinal() == spellType.getLevel().ordinal())
                    no change to usesModifier
                 */
            else if ((maxLevelForPlayer.ordinal() + 1) < spellType.getLevel().ordinal())
                // 25% skill when 2 or more levels below
                usesModifier *= 0.25;
            else if (maxLevelForPlayer.ordinal() < spellType.getLevel().ordinal())
                // half skill when 1 level below
                usesModifier *= 0.5;
        }

        common.printDebugMessage("O2Spell.setUsesModifier: usesModifier = " + usesModifier, null, null, false);
    }

    /**
     * Get the target block for the spell.
     *
     * @return the target block
     */
    @Nullable
    public Block getTargetBlock() {
        if (hitTarget)
            return location.getBlock();
        else
            return null;
    }

    /**
     * Spell-specific effects and behavior executed each game tick.
     *
     * <p>Called each tick from {@link #checkEffect()} after validation and movement logic.
     * Subclasses must override this method to implement the spell's unique effects such as
     * damage, block changes, particle effects, or other gameplay mechanics.</p>
     */
    abstract protected void doCheckEffect();

    /**
     * Terminates this spell, stopping projectile movement and performing cleanup.
     *
     * <p>Stops the projectile via {@link #stopProjectile()}, performs spell-specific cleanup via {@link #revert()},
     * and marks the spell as killed so it will no longer be processed.</p>
     */
    public void kill() {
        stopProjectile();

        revert();

        kill = true;
    }

    /**
     * Reverts any temporary changes made to the world by this spell.
     *
     * <p>Called when the spell is terminated via {@link #kill()}. Subclasses should override this method
     * to undo any temporary block changes, entity modifications, or other side effects. The default
     * implementation does nothing.</p>
     */
    protected void revert() {
    }

    /**
     * Whether this spell has been killed
     *
     * @return true if the spell has been terminated, false otherwise
     */
    public boolean isKilled() {
        return kill;
    }

    /**
     * Stops the spell projectile from moving further and marks it as having hit a target.
     *
     * <p>Sets {@link #hitTarget} to true only if the projectile has not already exceeded max distance.
     * This prevents marking as "hit target" when the spell is killed due to reaching max distance.</p>
     */
    void stopProjectile() {
        if (!isAtMaxDistance()) {
            hitTarget = true;
        }
    }

    /**
     * Whether this spell has hit a target
     *
     * @return true if the spell has hit a target, false otherwise
     */
    public boolean hasHitTarget() {
        return hitTarget;
    }

    /**
     * Get the spell's casting cooldown
     *
     * @return the cool-down time for this spell in ticks
     */
    public long getCoolDown() {
        return DEFAULT_COOLDOWN;
    }

    /**
     * Get the spell's book text
     *
     * @return the book text for this spell
     */
    @NotNull
    public String getText() {
        // first check to see if it has been set with config
        if (p.getConfig().isSet(spellType.toString() + textConfigLabel)) {
            String t = p.getConfig().getString(spellType.toString() + textConfigLabel);
            if (t != null && !(t.isEmpty()))
                return t;
        }

        if (text == null)
            return "";

        return text;
    }

    /**
     * Get the spell's book flavor text
     *
     * @return the flavor text for this spell
     */
    @Nullable
    public String getFlavorText() {
        // first check to see if it has been set with config
        if (p.getConfig().isSet(spellType.toString() + flavorTextConfigLabel)) {
            String f = p.getConfig().getString(spellType.toString() + flavorTextConfigLabel);
            if (f != null && !(f.isEmpty()))
                return f;
        }

        if (flavorText == null || flavorText.isEmpty())
            return null;
        else {
            int index = Math.abs(Ollivanders2Common.random.nextInt() % flavorText.size());
            return flavorText.get(index);
        }
    }

    /**
     * Get the spell's branch of magic
     *
     * @return the branch of magic for this spell
     */
    @NotNull
    public O2MagicBranch getMagicBranch() {
        return branch;
    }

    /**
     * Get the name of the spell
     *
     * @return the name of the spell
     */
    @NotNull
    public String getName() {
        return spellType.getSpellName();
    }

    /**
     * Get the number of ticks this spell has been alive for
     *
     * @return the lifeticks for this spell
     */
    public int getProjectileAge() {
        return projectileAge;
    }

    /**
     * Get the level of this spell. This is primarily for use with counter-spells
     *
     * @return the level of the spell
     */
    @NotNull
    public MagicLevel getLevel() {
        return spellType.getLevel();
    }

    /**
     * Check if a potentially harmful spell can target this entity based on WorldGuard permissions.
     *
     * <p>Validates spell targeting against WorldGuard flags for PvP, animal damage, item pickup, vehicle destruction,
     * item frame destruction, and painting destruction. Bypasses checks if WorldGuard is disabled.</p>
     *
     * @param entity the entity to check for targeting eligibility
     * @return true if the entity can be targeted, false if WorldGuard blocks the action
     */
    boolean entityHarmWGCheck(Entity entity) {
        if (!Ollivanders2.worldGuardEnabled) // short circuit this if WG is not enabled
            return true;

        // players
        if (entity instanceof Player && !Ollivanders2.worldGuardO2.checkWGFlag(player, location, Flags.PVP))
            return false;

        // friendly mobs
        if (entity instanceof Animals && !Ollivanders2.worldGuardO2.checkWGFlag(player, location, Flags.DAMAGE_ANIMALS))
            return false;

        // items
        if (entity instanceof Item && !Ollivanders2.worldGuardO2.checkWGFlag(player, location, Flags.ITEM_PICKUP))
            return false;

        // vehicles
        if (entity instanceof Vehicle && !Ollivanders2.worldGuardO2.checkWGFlag(player, location, Flags.DESTROY_VEHICLE))
            return false;

        // item frames
        if (entity instanceof ItemFrame && !Ollivanders2.worldGuardO2.checkWGFlag(player, location, Flags.ENTITY_ITEM_FRAME_DESTROY))
            return false;

        // paintings
        return entity instanceof Painting && !Ollivanders2.worldGuardO2.checkWGFlag(player, location, Flags.ENTITY_PAINTING_DESTROY);
    }

    /**
     * Send the player the success message, if it exists, for this spell
     */
    public void sendSuccessMessage() {
        if (successMessage != null && !(successMessage.isEmpty()))
            player.sendMessage(Ollivanders2.chatColor + successMessage);
    }

    /**
     * Get the success message string
     *
     * @return the success message string if set, or an empty string if not set
     */
    public String getSuccessMessage() {
        return successMessage;
    }

    /**
     * Send the player the failure message, if it exists, for this spell
     */
    public void sendFailureMessage() {
        if (failureMessage != null && !(failureMessage.isEmpty()))
            player.sendMessage(Ollivanders2.chatColor + failureMessage);
        else
            common.printDebugMessage("failure message unset or 0 length", null, null, false);
    }

    /**
     * Get the failure message string
     *
     * @return the failure message string if set, or an empty string if not set
     */
    public String getFailureMessage() {
        return failureMessage;
    }

    /**
     * Get the list of pass through materials for this spell projectile.
     *
     * @return the list of materials the projectile can pass through
     */
    public List<Material> getProjectilePassThroughMaterials() {
        List<Material> passThrough = new ArrayList<>();
        passThrough.addAll(projectilePassThrough);

        return passThrough;
    }

    /**
     * Get the list of blocked materials for this spell to target.
     *
     * @return the list of blocked materials
     */
    public List<Material> getBlockedMaterials() {
        List<Material> blocked = new ArrayList<>();
        blocked.addAll(materialBlockedList);

        return blocked;
    }

    /**
     * Get the list of allowed materials for this spell to target.
     *
     * @return the list of allowed materials
     */
    @NotNull
    public List<Material> getAllowedMaterials() {
        List<Material> allowed = new ArrayList<>();
        allowed.addAll(materialAllowList);

        return allowed;
    }
}