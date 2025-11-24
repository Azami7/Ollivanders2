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
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Giant;
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
    static final int maxProjectileDistance = 50;

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
     * How long this spell projectile has been alive in game ticks.
     */
    private int lifeTicks = 0;

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
    String failureMessage = null;

    /**
     * The default radius for spells when checking for conditions
     */
    static final double defaultRadius = 1.5;

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
        materialBlockedList.addAll(Ollivanders2Common.unbreakableMaterials);

        // block types that this spell's projectiles pass through
        projectilePassThrough.add(Material.AIR);
        projectilePassThrough.add(Material.CAVE_AIR);
        projectilePassThrough.add(Material.WATER);
        projectilePassThrough.add(Material.FIRE);
    }

    /**
     * Initialize the parts of the spell that are based on experience, the player, etc. and not on class
     * constants. This must be called by the child-most spell constructor since usage is based on the specific class
     * type and cannot be determined in the super-class constructors.
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
     * The spell-specific initialization based on usage, etc. Must be overridden by each spell class that
     * has any initializations.
     */
    void doInitSpell() {
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
        // check whether this spell can exist up until it hits a target
        if (!hitTarget && !isSpellAllowed()) {
            kill();
            return;
        }

        lifeTicks = lifeTicks + 1;

        // if this spell exceeds the max age, kill it
        if (lifeTicks > maxSpellLifetime)
            kill();

        // do nothing if spell is already marked as killed
        if (!kill) {
            // only move the projectile if a target has not been hit
            if (!hitTarget)
                move();

            // if the spell has not been killed, run the spell upkeep cycle
            if (!isKilled())
                doCheckEffect();
        }
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

        // if we have gone beyond the max distance, kill this spell
        if (projectileDistance > maxProjectileDistance) {
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

        world.playEffect(location, moveEffect, moveEffectData);

        // check the block at this location, if it is not a pass-through block, stop the projectile and check the target
        Material targetBlockType = location.getBlock().getType();
        if (!projectilePassThrough.contains(targetBlockType)) {
            stopProjectile();
            checkTargetBlock();
        }
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

        p.getLogger().info("Checking " + target.getType());

        // check blockAllowList, if it ia empty then all block types are allowed
        if ((!materialAllowList.isEmpty()) && !(materialAllowList.contains(target.getType()))) {
            common.printDebugMessage(target.getType() + " is not in the material allow list", null, null, false);
            kill();
            return;
        }

        // check deny list
        if (materialBlockedList.contains(target.getType())) {
            common.printDebugMessage(target.getType() + " is in the material deny list", null, null, false);
            kill();
        }
    }

    /**
     * Check to see if this spell is allowed per Ollivanders2 config and WorldGuard.
     *
     * @return true if the spell can exist here, false otherwise
     */
    boolean isSpellAllowed() {
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

        if (!isAllowed)
            p.spellFailedMessage(player);

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
     * Gets entities within a radius of current spell projectile location
     *
     * @param radius radius within which to get entities
     * @return a list of entities within the radius of the projectile
     */
    @NotNull
    List<Entity> getCloseEntities(double radius) {
        if (radius <= 0)
            radius = 1.0;

        Collection<Entity> entities = EntityCommon.getEntitiesInRadius(location, radius);
        List<Entity> close = new ArrayList<>();

        for (Entity e : entities) {
            if (e instanceof LivingEntity) {
                if (((LivingEntity) e).getEyeLocation().distance(location) < radius
                        || ((e instanceof EnderDragon
                        || e instanceof Giant) && ((LivingEntity) e).getEyeLocation().distance(location) < (radius + 5))) {
                    if (!e.equals(player))
                        close.add(e);
                    else {
                        if (lifeTicks > 1)
                            close.add(e);
                    }
                }
            }
            else
                close.add(e);

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
    public List<Item> getItems(double radius) {
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
        List<Entity> entities = getCloseEntities(radius);
        List<LivingEntity> living = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof LivingEntity)
                living.add((LivingEntity) e);
        }

        // handle also adding the current player when the projectile is at its first position
        if (lifeTicks <= radius && !entities.contains(player))
            living.add(player);

        return living;
    }

    /**
     * Get all players within the radius of projectile, including the caster if within the radius
     *
     * @param radius radius within which to get entities
     * @return a list of players within one block of projectile
     */
    @NotNull
    public List<Player> getNearbyPlayers(double radius) {
        List<Entity> entities = getCloseEntities(radius);
        List<Player> players = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof Player)
                players.add((Player) e);
        }

        // handle also adding the current player when the projectile is at its first position
        if (lifeTicks <= radius && !entities.contains(player))
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
        List<Entity> entities = getCloseEntities(radius);
        List<Damageable> damageable = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof Damageable)
                damageable.add((Damageable) e);
        }

        // handle also adding the current player when the projectile is at its first position
        if (lifeTicks <= radius && !entities.contains(player))
            damageable.add(player);

        return damageable;
    }

    /**
     * Sets the uses modifier that takes into account spell uses, wand type, and spell level if years are enabled. Returns 100 if the uses are 100 and the right wand is held.
     */
    protected void setUsesModifier() {
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        if (o2p == null) {
            common.printLogMessage("Null o2player in O2Spell.setUsesModifier", null, null, true);
            return;
        }

        // if max skill is set, set usesModifier to max level
        if (Ollivanders2.maxSpellLevel)
            usesModifier = 200;
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
     * The spell-specific actions taken for each check effect. This must be overridden by each spell or the spell
     * will do nothing.
     */
    abstract protected void doCheckEffect();

    /**
     * This kills the spell.
     */
    public void kill() {
        stopProjectile();

        revert();

        kill = true;
    }

    /**
     * Reverts any changes made to blocks if the effects are temporary. This must be overridden by each spell that has
     * a revert action.
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
     * Stops the spell projectile from moving further
     */
    void stopProjectile() {
        hitTarget = true;
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
    public int getLifeTicks() {
        return lifeTicks;
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
     * Determine if this entity can be targeted for a potentially harmful spell
     *
     * @param entity the entity to check
     * @return true if it can be targeted, false otherwise
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
        if (entity instanceof Painting && !Ollivanders2.worldGuardO2.checkWGFlag(player, location, Flags.ENTITY_PAINTING_DESTROY))
            return false;

        return true;
    }

    /**
     * Send the player the success message, if it exists, for this spell
     */
    public void sendSuccessMessage() {
        if (successMessage != null && !(successMessage.isEmpty()))
            player.sendMessage(Ollivanders2.chatColor + successMessage);
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
}