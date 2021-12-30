package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Transform items to entities. This spell always consumes the item (ie. it doesn't come back on revert)
 *
 * @author Azami7
 */
public class ItemToEntityTransfiguration extends EntityTransfiguration
{
    /**
     * If this is populated, any material type key will be changed to the value
     */
    protected Map<Material, EntityType> transfigurationMap = new HashMap<>();

    /**
     * Optional custom name for the transfigured entity
     */
    String entityCustomName = null;

    /**
     * The entity created by this spell
     */
    Entity transfiguredEntity = null;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ItemToEntityTransfiguration(Ollivanders2 plugin)
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
    public ItemToEntityTransfiguration(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        entityWhitelist.add(EntityType.DROPPED_ITEM);
    }

    /**
     * Transfigures item into EntityType.
     *
     * @param entity the item to transfigure
     */
    @Override
    @Nullable
    protected Entity transfigureEntity(@NotNull Entity entity)
    {
        if (!(entity instanceof Item))
        {
            common.printDebugMessage("Entity is not type Item in " + spellType.toString(), null, null, true);
            return null;
        }

        Item item = (Item)entity;
        EntityType targetType = transfigurationMap.get(item.getItemStack().getType());
        if (targetType == null)
        {
            common.printDebugMessage("EntityType is null in " + spellType.toString(), null, null, true);
            return null;
        }

        // spawn the new entity
        transfiguredEntity = item.getWorld().spawnEntity(item.getLocation(), targetType);
        if (entityCustomName != null && entityCustomName.length() > 0)
            transfiguredEntity.setCustomName(entityCustomName);

        // remove the item
        item.remove();

        // register listeners
        if (this instanceof Listener)
            p.getServer().getPluginManager().registerEvents((Listener)this, p);

        return transfiguredEntity;
    }

    /**
     * Determine if this entity can be transfigured by this spell.
     *
     * @param entity the entity to check
     * @return true if the entity can be transfigured, false otherwise.
     */
    protected boolean canTransfigure(@NotNull Entity entity)
    {
        // make sure it is an item
        if (!(entity instanceof Item))
            return false;

        // make sure it is a type we can transfigure
        Material itemType = ((Item)entity).getItemStack().getType();
        if (!transfigurationMap.containsKey(itemType))
            return false;

        // run higher level checks
        return super.canTransfigure(entity);
    }
}
