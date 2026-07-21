package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for spells that transfigure a living entity into a dropped item. The original entity is consumed, so
 * these transfigurations are permanent and never revert.
 *
 * @author Azami7
 */
public abstract class LivingEntityToItemTransfiguration extends EntityTransfiguration {
    /**
     * The default item material entities are turned into when not overridden by {@link #transfigurationMap}.
     */
    Material targetMaterial = null;

    /**
     * Per-source-entity-type item materials. An entity whose type is a key becomes the mapped material; any other
     * entity becomes {@link #targetMaterial}. Empty by default.
     */
    protected Map<EntityType, Material> transfigurationMap = new HashMap<>();

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public LivingEntityToItemTransfiguration(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.TRANSFIGURATION;
    }

    /**
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public LivingEntityToItemTransfiguration(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.TRANSFIGURATION;
        permanent = true;
        targetType = EntityType.ITEM;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.ITEM_DROP);
        }

        failureMessage = "Nothing seems to happen.";
    }

    /**
     * Transfigure the given living entity into a dropped item of the target material, consuming the entity.
     *
     * @param entity the entity to transfigure
     * @return the new item, or null if no target material could be resolved
     */
    @Override
    @Nullable
    protected Entity transfigureEntity(@NotNull Entity entity) {
        // get the material for this specific entity type if specified
        if (!transfigurationMap.isEmpty() && (transfigurationMap.containsKey(entity.getType()))) {
            targetMaterial = transfigurationMap.get(entity.getType());
        }

        // make sure material type is set either at the spell level (so for any entity type) or from the transfiguration map
        if (targetMaterial == null) {
            common.printDebugMessage("LivingEntityToItemTransfiguratio: targetMaterial is null", null, null, true);
            return null;
        }

        // spawn the new item entity
        Item item = entity.getWorld().dropItem(entity.getLocation(), new ItemStack(targetMaterial));

        // remove the original entity
        entity.remove();

        return item;
    }

    /**
     * Check whether an entity is an eligible target: in addition to the base entity checks, it must be a living entity
     * whose type is in {@link #transfigurationMap} when that map is populated.
     *
     * @param entity the entity to check
     * @return true if the entity can be transfigured, false otherwise
     */
    public boolean canTransfigure(@NotNull Entity entity) {
        if (!super.canTransfigure(entity))
            return false;

        if (!(entity instanceof LivingEntity)) {
            common.printDebugMessage(entity.getName() + " is not a living entity", null, null, false);
            return false;
        }

        if (!transfigurationMap.isEmpty() && !transfigurationMap.containsKey(entity.getType())) {
            common.printDebugMessage(spellType.toString() + ": cannot transform entity tyoe " + entity.getType(), null, null, false);
            return false;
        }

        return true;
    }

    /**
     * @return the default target item material, or null if none has been set
     */
    public Material getTargetMaterial() {
        return targetMaterial;
    }
}
