package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for spells that transfigure blocks into entities.
 *
 * <p>When cast, this spell transforms a target block into a living entity. The spell can optionally
 * be configured to have a time limit (temporary transfiguration) or be permanent. Temporary
 * transfigurations are automatically reverted when the effect duration expires or when the spawned
 * entity dies. The block is tracked as temporarily changed so it can be properly restored.</p>
 *
 * <p>Subclasses configure behavior by populating {@link #transfigurationMap} and {@link #materialAllowList},
 * then calling {@link #initSpell()} in their constructor.</p>
 *
 * @author Azami7
 */
public abstract class BlockToEntityTransfiguration extends BlockTransfiguration implements Listener {
    /**
     * The entity that has been transfigured by this spell
     */
    Entity transfiguredEntity = null;

    /**
     * The entity type to transfigure the target in to, a sheep by default
     */
    EntityType entityType = EntityType.SHEEP;

    /**
     * If this is populated, a map of materials and what entity type to change them in to.
     *
     * <p>For use with spells that can do more than one type of change. Add each material that can
     * be changed to this map. Any missing material will fall back to the default entityType.</p>
     *
     * <p>If the spell can only target one or more specific material types, and they all change to
     * the same thing, add that to materialWhitelist and set entityType.</p>
     *
     * <p>If the spell can target any material, make materialWhitelist blank and set entityType.</p>
     */
    protected Map<Material, EntityType> transfigurationMap = new HashMap<>();

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public BlockToEntityTransfiguration(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.TRANSFIGURATION;
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public BlockToEntityTransfiguration(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
    }

    /**
     * Transfigure the target block into the desired entity type.
     *
     * <p>Validates the target block using {@link #canTransfigure(Block)}, converts it to air,
     * spawns the entity at the block's location, and registers event handlers to track the spawned
     * entity's lifetime. If the transfiguration is temporary, the block is added to the temporarily
     * changed block tracking system so it will be restored after the effect duration expires.
     * Subclasses can override {@link #customizeEntity()} to apply additional configuration to the
     * spawned entity.</p>
     */
    @Override
    protected void transfigure() {
        Block target = getTargetBlock();
        if (target == null)
            // we have not hit a target yet, continue projectile
            return;

        if (!canTransfigure(target)) {
            common.printDebugMessage("Transfiguration not allowed", null, null, false);
            sendFailureMessage();
            kill();
            return;
        }

        if (!permanent) {
            Ollivanders2API.getBlocks().addTemporarilyChangedBlock(target, this);
        }

        // get the type to change this material
        if (transfigurationMap.containsKey(target.getType()))
            entityType = transfigurationMap.get(target.getType());

        // change the block to air
        target.setType(Material.AIR);
        isTransfigured = true;

        // spawn the entity in this location
        if (entityType != null) {
            transfiguredEntity = target.getWorld().spawnEntity(target.getLocation(), entityType);
            customizeEntity();
        }
        else {
            kill();
            common.printDebugMessage("Entity type was null in " + spellType.toString(), null, null, true);
            return;
        }

        // register listeners
        p.getServer().getPluginManager().registerEvents(this, p);

        sendSuccessMessage();
    }

    /**
     * Is this entity transfigured by this spell
     *
     * @param entity the entity to check
     * @return true if transfigured, false otherwise
     */
    @Override
    public boolean isEntityTransfigured(@NotNull Entity entity) {
        if (permanent)
            return false;

        if (transfiguredEntity == null)
            return false;

        return transfiguredEntity.getUniqueId().equals(entity.getUniqueId());
    }

    /**
     * Is this block transfigured by this spell
     *
     * @param block the block to check
     * @return false - since the block is now an entity, it isn't considered a block anymore
     */
    @Override
    public boolean isBlockTransfigured(@NotNull Block block) {
        return false;
    }

    /**
     * Optionally customize the spawned entity before it becomes active.
     *
     * <p>Called immediately after the entity is spawned but before listeners are registered.
     * Subclasses can override this method to apply entity-specific configuration such as name,
     * equipment, attributes, or behavior modifications. The spawned entity is available via the
     * {@link #transfiguredEntity} field.</p>
     */
    void customizeEntity() {
    }

    /**
     * Handle when the transfigured entity is killed.
     *
     * <p>When the spawned entity dies, this method terminates the spell. For permanent
     * transfigurations, the spell ends but the entity is not reverted (as it is now dead).
     * For temporary transfigurations, the entity remains as-is until the effect duration expires
     * and {@link #doRevert()} handles the final cleanup.</p>
     *
     * @param event the entity death event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath(EntityDeathEvent event) {
        if (transfiguredEntity == null)
            return;

        Entity entity = event.getEntity();
        if (entity.getUniqueId().equals(transfiguredEntity.getUniqueId()))
            kill();
    }

    /**
     * Get a copy of the transfiguration map.
     *
     * @return a new map containing the material-to-entity-type mappings
     */
    public Map<Material, EntityType> getEntityTransfigurationMap() {
        return new HashMap<>() {{
            putAll(transfigurationMap);
        }};
    }

    /**
     * Unregister event handlers and despawn the created entity if the transfiguration is temporary.
     *
     * <p>Called when the spell's effect duration expires. This method unregisters the spell's event
     * handlers to clean up any registered listeners, then removes the spawned entity if the
     * transfiguration is not permanent.</p>
     */
    @Override
    void doRevert() {
        HandlerList.unregisterAll(this);

        if (permanent)
            return;

        if (transfiguredEntity != null)
            transfiguredEntity.remove();
    }
}
