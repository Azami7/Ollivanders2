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
 * Base class for all player-cast Ollivanders2 spells, providing projectile movement, collision, location and
 * WorldGuard validation, cooldowns, and lifecycle management.
 * <p>
 * Lifecycle: the spell is constructed with the caster and wand, then {@link #initSpell()} runs the player-based
 * setup. Each game tick {@link #checkEffect()} advances the projectile via {@link #move()} and runs the spell's
 * own {@link #doCheckEffect()} until the spell is killed. {@link #kill()} calls {@link #revert()} for cleanup.
 * </p>
 * <p>
 * Subclasses must implement {@link #doCheckEffect()} and typically override {@link #doInitSpell()} and
 * {@link #revert()}; they rarely override the lifecycle methods themselves.
 * </p>
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
    protected Player caster;

    /**
     * The O2Player for the caster.
     */
    protected O2Player casterO2P;

    /**
     * The type this spell is.
     */
    protected O2SpellType spellType;

    /**
     * The location of the spell projectile
     */
    protected Location location;

    /**
     * The world the spell is happening in
     */
    protected World world;

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
    protected final Ollivanders2 p;

    /**
     * Ollivanders common functions
     */
    protected Ollivanders2Common common;

    /**
     * The modifier for this spell based on usage. This is for spells that change behavior based on the caster's experience.
     */
    protected double usesModifier = 1;

    /**
     * The vector of the projectile.
     */
    protected Vector vector;

    /**
     * The caster's wand-correctness factor: 1 for the destined wand, 0.5 for a wrong wand, 2 for the elder wand.
     */
    protected double rightWand;

    /**
     * Whether the projectile has hit a target
     */
    private boolean hasHitBlock = false;

    /**
     * Does this spell do a spell projectile or not. Generally spells will send a projectile but any spell that targets
     * the caster, such as apparate, should not.
     */
    protected boolean noProjectile = false;

    /**
     * The sound this projectile makes as it moves.
     */
    Effect moveEffect = Effect.STEP_SOUND;

    /**
     * The visual effect this projectile has when it moves.
     */
    Material moveEffectData = Material.GLOWSTONE;

    /**
     * The default cooldown for a spell, in milliseconds. Spells take mental and physical energy for the caster and
     * cannot be cast in rapid succession.
     */
    static final long DEFAULT_COOLDOWN = 45 * 1000;

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
    protected final static String textConfigLabel = "_Text";

    /**
     * A list of block types that this spell is allowed to target. Takes precedence over deny list.
     */
    protected List<Material> materialAllowList = new ArrayList<>();

    /**
     * A list of block types that cannot be affected by this spell
     */
    protected List<Material> materialBlockedList = new ArrayList<>();

    /**
     * A list of block types that this projectile will pass through
     */
    protected List<Material> projectilePassThrough = new ArrayList<>();

    /**
     * A list of the worldguard permissions needed for this spell
     */
    protected List<StateFlag> worldGuardFlags = new ArrayList<>();

    /**
     * Message to display to the user on a successful cast of this spell.
     */
    protected String successMessage = null;

    /**
     * Message to display to the user on a failed cast of this spell.
     */
    protected String failureMessage = "Nothing seems to happen.";

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
     * Create a spell cast by a player. The projectile starts one block ahead of the caster's eye location along
     * their facing direction.
     *
     * @param plugin    a callback to the O2 plugin
     * @param player    the player casting the spell
     * @param rightWand the caster's wand-correctness factor from O2PlayerCommon.wandCheck; 1 for the destined wand,
     *                  0.5 for a wrong wand, 2 for the elder wand
     */
    public O2Spell(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        p = plugin;
        common = new Ollivanders2Common(p);

        caster = player;
        casterO2P = Ollivanders2API.getPlayers().getPlayer(caster.getUniqueId());
        if (casterO2P == null) {
            common.printLogMessage("O2Spell: O2Players.getPlayer(caster) returned null", null, null, false);
        }

        location = caster.getEyeLocation();
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

        world = location.getWorld();
        if (world == null) {
            common.printLogMessage("O2Spell: null world", null, null, true);
        }
    }

    /**
     * Set the usage modifier and run spell-specific setup that depends on the caster.
     * <p>
     * Must be called from the most-derived spell constructor, since it needs the concrete {@link #spellType}.
     * Kills the spell if {@link #spellType} has not been set.
     * </p>
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
     * Hook for spell-specific setup, run once from {@link #initSpell()}. The default implementation does nothing.
     */
    void doInitSpell() {
    }

    /**
     * Get the WorldGuard flags this spell requires.
     *
     * @return a copy of the flag list; empty if the spell needs none
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
     * Advance the spell one game tick. Kills the spell if it is no longer allowed at its current location.
     * <p>
     * Subclasses put their per-tick behavior in {@link #doCheckEffect()} rather than overriding this method.
     * </p>
     */
    public void checkEffect() {
        if (isKilled())
            return;

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
     * Advance the projectile one block along {@link #vector}, firing an {@link OllivandersSpellProjectileMoveEvent}
     * and stopping it when it hits a non pass-through block. Kills the spell if it exceeds
     * {@link #maxProjectileDistance} or is no longer allowed at the new location. No-op once the spell is killed or
     * the projectile has stopped.
     */
    public void move() {
        // if this is somehow called when the spell is set to killed, or we've already hit a target, do nothing
        if (isKilled() || hasHitBlock())
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
        p.getServer().getPluginManager().callEvent(new OllivandersSpellProjectileMoveEvent(caster, this, prevLoc, location));
        projectileDistance = projectileDistance + 1;

        // determine if this spell is allowed in this location per Ollivanders2 config and WorldGuard
        if (!isSpellAllowed()) {
            common.printDebugMessage("O2Spell.move: spell not allowed here", null, null, false);
            kill();
            return;
        }

        // play the project moving effect
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
        if (!hasHitBlock())
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
     * Check whether this spell is allowed at its current location per Ollivanders2 config and WorldGuard. When it is
     * not, this also kills the spell and sends the caster the failure message as a side effect.
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
            if (!Ollivanders2.worldGuardO2.checkWGFlag(caster, location, flag)) {
                common.printDebugMessage(spellType.toString() + " cannot be cast because of WorldGuard flag " + flag, null, null, false);
                return false;
            }
        }

        return true;
    }

    /**
     * Get the entities within a radius of the projectile, excluding the caster.
     *
     * @param radius the search radius in blocks; values &lt;= 0 are treated as 1.0
     * @return the entities in range, excluding the caster; empty if none
     */
    @NotNull
    public List<Entity> getNearbyEntities(double radius) {
        if (radius <= 0)
            radius = 1.0;

        Collection<Entity> entities = EntityCommon.getEntitiesInRadius(location, radius);

        List<Entity> close = new ArrayList<>();

        for (Entity e : entities) {
            if (e instanceof LivingEntity) {
                if (!e.equals(caster))
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
     * Get the living entities within a radius of the projectile, including the caster when in range.
     *
     * @param radius the search radius in blocks
     * @return the living entities in range, including the caster; empty if none
     */
    @NotNull
    public List<LivingEntity> getNearbyLivingEntities(double radius) {
        List<Entity> entities = getNearbyEntities(radius);
        List<LivingEntity> living = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof LivingEntity)
                living.add((LivingEntity) e);
        }

        // getNearbyEntities excludes the caster, so add them back in when the projectile is on top of them; the
        // projectileAge > 1 guard avoids catching the caster in the first ticks before the projectile has left them
        if (Ollivanders2Common.isInside(caster.getLocation(), location, (int) radius) && !entities.contains(caster) && projectileAge > 1)
            living.add(caster);

        return living;
    }

    /**
     * Get the players within a radius of the projectile, including the caster when in range.
     *
     * @param radius the search radius in blocks
     * @return the players in range, including the caster; empty if none
     */
    @NotNull
    public List<Player> getNearbyPlayers(double radius) {
        List<Entity> entities = getNearbyEntities(radius);
        List<Player> players = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof Player)
                players.add((Player) e);
        }

        // getNearbyEntities excludes the caster, so add them back in when the projectile is on top of them (see
        // getNearbyLivingEntities for the projectileAge > 1 rationale)
        if (Ollivanders2Common.isInside(caster.getLocation(), location, (int) radius) && !entities.contains(caster) && projectileAge > 1)
            players.add(caster);

        return players;
    }

    /**
     * Get the damageable entities within a radius of the projectile, including the caster when in range.
     *
     * @param radius the search radius in blocks
     * @return the damageable entities in range, including the caster; empty if none
     */
    @NotNull
    public List<Damageable> getNearbyDamageableEntities(double radius) {
        List<Entity> entities = getNearbyEntities(radius);
        List<Damageable> damageable = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof Damageable)
                damageable.add((Damageable) e);
        }

        // getNearbyEntities excludes the caster, so add them back in when the projectile is on top of them (see
        // getNearbyLivingEntities for the projectileAge > 1 rationale)
        if (Ollivanders2Common.isInside(caster.getLocation(), location, (int) radius) && !entities.contains(caster) && projectileAge > 1)
            damageable.add(caster);

        return damageable;
    }

    /**
     * Calculate and set {@link #usesModifier} from the caster's spell experience, wand correctness, the HIGHER_SKILL
     * effect, and — when years are enabled — the caster's level relative to the spell's level.
     */
    protected void setUsesModifier() {
        // if max skill is set, set usesModifier to max level
        if (Ollivanders2.maxSpellLevel)
            usesModifier = spellMasteryLevel * 2;
        else {
            // uses modifier is the number of times the spell has been cast
            usesModifier = casterO2P.getSpellCount(spellType);

            // if it is not a wandless spell, uses modifier is halved if the player is not using their destined wand,
            // doubled if they are using the elder wand
            if (!O2Spells.wandlessSpells.contains(spellType))
                usesModifier = usesModifier / rightWand;

            // if the caster is affected by HIGHER_SKILL, double their usesModifier
            if (Ollivanders2API.getPlayers().playerEffects.hasEffect(caster.getUniqueId(), O2EffectType.HIGHER_SKILL))
                usesModifier *= 2;
        }

        // if years is enabled, spell usage is affected by caster's level
        if (Ollivanders2.useYears) {
            MagicLevel maxLevelForPlayer = casterO2P.getYear().getHighestLevelForYear();

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
        if (hasHitBlock)
            return location.getBlock();
        else
            return null;
    }

    /**
     * Apply the spell's unique effects for one game tick. Called each tick from {@link #checkEffect()}; subclasses
     * implement their behavior here.
     */
    abstract protected void doCheckEffect();

    /**
     * Terminate this spell. Its projectile stops, any temporary changes are reverted, and the spell is no longer
     * processed on subsequent ticks.
     */
    public void kill() {
        stopProjectile();

        revert();

        kill = true;
    }

    /**
     * Hook to undo any temporary world changes this spell made, run from {@link #kill()}. Subclasses that mutate the
     * world override this; the default implementation does nothing.
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
     * Stop the projectile and mark it as having hit a block — but only if it has not already exceeded max distance,
     * so a spell killed for reaching max distance is not treated as having hit a target.
     */
    void stopProjectile() {
        if (!isAtMaxDistance()) {
            hasHitBlock = true;
        }
    }

    /**
     * Whether this spell has hit a non pass-though block and the projectile is stopped
     *
     * @return true if the spell has hit a non pass-though block and the projectile is stopped, false otherwise
     */
    public boolean hasHitBlock() {
        return hasHitBlock;
    }

    /**
     * Get the spell's casting cooldown.
     *
     * @return the cooldown time for this spell, in milliseconds
     */
    public long getCoolDown() {
        return spellType.getCooldown();
    }

    /**
     * Get the spell's book text, preferring a config.yml override over the built-in {@link #text}.
     *
     * @return the book text, or an empty string if none is set
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
     * Get flavor text for the spell's book entry, preferring a config.yml override, otherwise a random entry from
     * {@link #flavorText}.
     *
     * @return the flavor text, or null if none is set
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
        if (entity instanceof Player && !Ollivanders2.worldGuardO2.checkWGFlag(caster, location, Flags.PVP))
            return false;

        // friendly mobs
        if (entity instanceof Animals && !Ollivanders2.worldGuardO2.checkWGFlag(caster, location, Flags.DAMAGE_ANIMALS))
            return false;

        // items
        if (entity instanceof Item && !Ollivanders2.worldGuardO2.checkWGFlag(caster, location, Flags.ITEM_PICKUP))
            return false;

        // vehicles
        if (entity instanceof Vehicle && !Ollivanders2.worldGuardO2.checkWGFlag(caster, location, Flags.DESTROY_VEHICLE))
            return false;

        // item frames
        if (entity instanceof ItemFrame && !Ollivanders2.worldGuardO2.checkWGFlag(caster, location, Flags.ENTITY_ITEM_FRAME_DESTROY))
            return false;

        // paintings
        return entity instanceof Painting && !Ollivanders2.worldGuardO2.checkWGFlag(caster, location, Flags.ENTITY_PAINTING_DESTROY);
    }

    /**
     * Send the player the success message, if it exists, for this spell
     */
    public void sendSuccessMessage() {
        if (successMessage != null && !(successMessage.isEmpty()))
            caster.sendMessage(Ollivanders2.chatColor + successMessage);
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
            caster.sendMessage(Ollivanders2.chatColor + failureMessage);
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
     * Get the materials this spell's projectile passes through.
     *
     * @return a copy of the pass-through material list
     */
    public List<Material> getProjectilePassThroughMaterials() {
        return new ArrayList<>()  {{
            addAll(projectilePassThrough);
        }};
    }

    /**
     * Get the materials this spell cannot target.
     *
     * @return a copy of the blocked material list
     */
    public List<Material> getBlockedMaterials() {
        return new ArrayList<>() {{
            addAll(materialBlockedList);
        }};
    }

    /**
     * Get the materials this spell may target. An empty list means all materials are allowed; when non-empty it
     * takes precedence over the blocked list.
     *
     * @return a copy of the allowed material list
     */
    @NotNull
    public List<Material> getAllowedMaterials() {
        return new ArrayList<>() {{
            addAll(materialAllowList);
        }};
    }

    /**
     * @return true if this spell targets the caster and sends no projectile
     */
    public boolean isNoProjectile() {
        return noProjectile;
    }

    /**
     * @return this spell's type
     */
    public O2SpellType getSpellType() {
        return spellType;
    }

    /**
     * Get the projectile's current location. This is the live location that moves each tick, not a copy.
     *
     * @return the current projectile location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @return the uses modifier set by {@link #setUsesModifier()}
     */
    public double getUsesModifier() {
        return usesModifier;
    }
}