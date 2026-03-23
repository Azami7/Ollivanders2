package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for item-to-entity transfiguration spells.
 *
 * <p>Transforms dropped items into living entities. Unlike other entity transfigurations, item
 * transfigurations always consume the original item — it is not restored when the spell reverts.
 * Subclasses define which item types can be transfigured via {@link #transfigurationMap}.</p>
 *
 * <p>Registers as a Bukkit {@link Listener} to handle death events for the transfigured entity.</p>
 *
 * @author Azami7
 * @see EntityTransfiguration for base entity transfiguration mechanics
 */
public abstract class ItemToEntityTransfiguration extends EntityTransfiguration implements Listener {
    /**
     * If this is populated, any material type key will be changed to the entity type
     */
    protected Map<Material, EntityType> transfigurationMap = new HashMap<>();

    /**
     * Optional custom name for the transfigured entity
     */
    String entityCustomName = null;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ItemToEntityTransfiguration(Ollivanders2 plugin) {
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
    public ItemToEntityTransfiguration(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        entityAllowedList.add(EntityType.ITEM);

        // world guard flags
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.ITEM_PICKUP);
            worldGuardFlags.add(Flags.MOB_SPAWNING);
        }
    }

    /**
     * Transfigures item into EntityType. Assumes {@link #canTransfigure(Entity)} has already verified this is an Item.
     *
     * @param entity the item to transfigure
     * @return the transfigured entity if successful, null otherwise
     */
    @Override
    @Nullable
    protected Entity transfigureEntity(@NotNull Entity entity) {
        Item item = (Item) entity;

        if (!transfigurationMap.isEmpty() && transfigurationMap.containsKey(item.getItemStack().getType()))
            targetType = transfigurationMap.get(item.getItemStack().getType());

        // spawn the new entity
        Entity newEntity = item.getWorld().spawnEntity(item.getLocation(), targetType);
        if (entityCustomName != null && !entityCustomName.isEmpty())
            newEntity.setCustomName(entityCustomName);

        // remove the item
        item.remove();

        // register listeners once the transfiguration has been successful
        p.getServer().getPluginManager().registerEvents(this, p);

        return newEntity;
    }

    /**
     * Determine if this entity can be transfigured by this spell.
     *
     * <p>Entity can be transfigured if:</p>
     *
     * <ol>
     * <li>(super) Success check passes</li>
     * <li>(super) It is not in the blocked list</li>
     * <li>(super) It is in the allowed list, if the allowed list exists</li>
     * <li>(super) The entity is not already the target type</li>
     * <li>(super) The item is not enchanted, or the enchantment level is lower than this spell's level</li>
     * <li>(super) The entity is not already transfigured by another spell</li>
     * <li>The entity is an Item</li>
     * <li>The item type is in the transfiguration map, if it is populated</li>
     * </ol>
     *
     * @param entity the entity to check
     * @return true if it can be changed
     */
    public boolean canTransfigure(@NotNull Entity entity) {
        if (!super.canTransfigure(entity))
            return false;

        // make sure it is an item
        if (!(entity instanceof Item))
            return false;

        if (transfigurationMap.isEmpty())
            return true;
        else {
            Material itemType = ((Item) entity).getItemStack().getType();
            return transfigurationMap.containsKey(itemType);
        }
    }

    /**
     * Handle when the entity is killed.
     *
     * @param event the entity death event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath(EntityDeathEvent event) {
        if (transfiguredEntity == null)
            return;

        Entity entity = event.getEntity();
        if (entity.getUniqueId().equals(transfiguredEntity.getUniqueId()))
            // the entity was killed, kill this spell
            kill();
    }

    public Map<Material, EntityType> getTransfigurationMap() {
        return new HashMap<>() {{
            putAll(transfigurationMap);
        }};
    }
}
