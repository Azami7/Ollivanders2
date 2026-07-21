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
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for spells that transfigure a dropped item into an entity, mapping eligible item types to the entity
 * they become.
 *
 * @author Azami7
 * @see EntityTransfiguration
 */
public abstract class ItemToEntityTransfiguration extends EntityTransfiguration implements Listener {
    /**
     * Per-source-material target entity types. An item whose material is a key is transfigured into the mapped entity
     * type; when the map is empty any eligible item is transfigured into {@link #targetType}.
     */
    protected Map<Material, EntityType> transfigurationMap = new HashMap<>();

    /**
     * Custom name applied to the spawned entity, or null to leave it unnamed.
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
     * Transfigure the given dropped item into the target entity, consuming the item. Names the new entity when
     * {@link #entityCustomName} is set. The spell self-terminates when the new entity dies. Assumes the entity is a
     * dropped item, as verified by {@link #canTransfigure(Entity)}.
     *
     * @param entity the item to transfigure
     * @return the new entity
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
     * Check whether an entity is an eligible target: in addition to the base entity checks, it must be a dropped item
     * whose material is in {@link #transfigurationMap} when that map is populated.
     *
     * @param entity the entity to check
     * @return true if the entity can be transfigured, false otherwise
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
            // the entity was killed, kill this spell
            kill();
    }

    /**
     * @return a copy of the per-material target entity-type overrides
     */
    public Map<Material, EntityType> getTransfigurationMap() {
        return new HashMap<>() {{
            putAll(transfigurationMap);
        }};
    }

    /**
     * Stop tracking the transfigured entity's death. The transfigured item is not restored.
     */
    @Override
    void doRevert() {
        HandlerList.unregisterAll(this);
    }
}
