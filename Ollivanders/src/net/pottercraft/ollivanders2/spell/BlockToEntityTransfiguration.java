package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Transform a block in to an entity.
 *
 * @author Azami7
 */
public abstract class BlockToEntityTransfiguration extends BlockTransfiguration
{
    Entity transfiguredEntity = null;

    EntityType entityType = EntityType.SHEEP;

    /**
     * If this is populated, any material type key will be changed to the value
     */
    protected Map<Material, EntityType> transfigurationMap = new HashMap<>();

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public BlockToEntityTransfiguration(Ollivanders2 plugin)
    {
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
    public BlockToEntityTransfiguration(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
    }

    /**
     * Override the parent transfigure function because this spell targets blocks.
     */
    @Override
    protected void transfigure()
    {
        if (!hasHitTarget())
            return;

        Block target = getTargetBlock();
        if (target == null)
        {
            common.printDebugMessage("Target block is null in " + spellType.toString(), null, null, true);
            return;
        }

        if (!canTransfigure(target))
        {
            kill();
            return;
        }

        if (!permanent)
            changedBlocks.put(target, target.getType());

        // get the type to change this material
        if (transfigurationMap.containsKey(target.getType()))
            entityType = transfigurationMap.get(target.getType());

        // change the block to air
        target.setType(Material.AIR);
        isTransfigured = true;

        // spawn the entity in this location
        if (entityType != null)
            transfiguredEntity = target.getWorld().spawnEntity(location, entityType);
        else
        {
            kill();
            common.printDebugMessage("Entity type was null in " + spellType.toString(), null, null, true);
        }

        if (transfiguredEntity == null)
        {
            kill();
            common.printDebugMessage("Failed to create entity in " + spellType.toString(), null, null, true);
        }
        else
        {
            // register listeners
            if (this instanceof Listener)
                p.getServer().getPluginManager().registerEvents((Listener)this, p);
        }

        sendSuccessMessage();
    }

    /**
     * Despawn the created entity
     */
    @Override
    void doRevert()
    {
        if (permanent)
            return;

        if (transfiguredEntity != null)
            transfiguredEntity.remove();
    }

    /**
     * Is this entity transfigured by this spell
     *
     * @param entity the entity to check
     * @return true if transfigured, false otherwise
     */
    @Override
    public boolean isEntityTransfigured(@NotNull Entity entity)
    {
        if (permanent)
            return false;

        if (transfiguredEntity == null)
            return false;

        return transfiguredEntity.getUniqueId() == entity.getUniqueId();
    }

    /**
     * Is this block transfigured by this spell
     *
     * @param block the block to check
     * @return true if transfigured, false otherwise
     */
    @Override
    public boolean isBlockTransfigured(@NotNull Block block)
    {
        return false;
    }
}
