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
 * Base class for spells that transfigure a block into an entity.
 * <p>
 * The transfiguration may be permanent or temporary; a temporary one is reverted when its duration expires or when
 * the spawned entity dies.
 * </p>
 *
 * @author Azami7
 */
public abstract class BlockToEntityTransfiguration extends BlockTransfiguration implements Listener {
    /**
     * The entity spawned in place of the transfigured block, null until the spell transfigures a target.
     */
    Entity transfiguredEntity = null;

    /**
     * The entity type the target block is transfigured into when not overridden by {@link #transfigurationMap}.
     */
    EntityType entityType = EntityType.SHEEP;

    /**
     * Per-source-material entity-type overrides. A block whose material is a key spawns the mapped entity type; any
     * other block spawns {@link #entityType}. Empty by default; use {@link #materialAllowList} to restrict which
     * materials may be targeted.
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
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public BlockToEntityTransfiguration(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
    }

    /**
     * Transfigure the target block into the configured entity. The spell self-terminates when that entity dies, or
     * immediately if the block cannot be transfigured or no target entity type is set.
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
     * @param entity the entity to check
     * @return true if the given entity is the one this spell spawned; always false for permanent spells
     */
    @Override
    public boolean isTransfigured(@NotNull Entity entity) {
        if (permanent)
            return false;

        if (transfiguredEntity == null)
            return false;

        return transfiguredEntity.getUniqueId().equals(entity.getUniqueId());
    }

    /**
     * @param block the block to check
     * @return always false; the transfigured block has become an entity
     */
    @Override
    public boolean isTransfigured(@NotNull Block block) {
        return false;
    }

    /**
     * Hook for subclasses to customize the newly spawned entity. The default implementation does nothing.
     */
    void customizeEntity() {
    }

    /**
     * Kill the spell when the transfigured entity dies.
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
     * @return a copy of the per-material entity-type overrides
     */
    public Map<Material, EntityType> getEntityTransfigurationMap() {
        return new HashMap<>() {{
            putAll(transfigurationMap);
        }};
    }

    /**
     * Despawn the spawned entity when the transfiguration was temporary.
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
