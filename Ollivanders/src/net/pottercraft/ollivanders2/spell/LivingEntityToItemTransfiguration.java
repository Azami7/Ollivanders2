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
 * Transforms entities in to items. This spell kills the entity (ie. it does not revert)
 */
public abstract class LivingEntityToItemTransfiguration extends EntityTransfiguration {
    //todo make player transfiguration an effect so it persists over log out/restarts
    /**
     * The target item material for this spell, only used if transfiguration map is not populated, then will turn any
     * entity type in to this material.
     */
    Material targetMaterial = null;

    /**
     * If this is populated, any entity type key will be changed to item type material
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
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public LivingEntityToItemTransfiguration(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        permanent = true;
        targetType = EntityType.ITEM;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.ITEM_DROP);
        }

        failureMessage = "Nothing seems to happen.";
    }

    /**
     * Transfigures item into EntityType.
     *
     * @param entity the item to transfigure
     * @return the transfigured entity if successful, null otherwise
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

        // spawn the new entity
        Entity newEntity = entity.getWorld().spawnEntity(entity.getLocation(), targetType);
        if (!(newEntity instanceof Item)) {
            newEntity.remove();
        }
        ((Item)newEntity).setItemStack(new ItemStack(targetMaterial));

        // remove the original entity
        entity.remove();

        return newEntity;
    }

    /**
     * Determine if this entity be transfigured by this spell.
     * <p>
     * Entity can transfigure if:<br>
     * 1. (super) success check passes<br>
     * 2. (super) It is not in the blocked list<br>
     * 3. (super) It is in the allowed list, if the allowed list exists<br>
     * 4. (super) The entity is not already the target type<br>
     * 5. (super) There are no WorldGuard permissions preventing the caster from altering this entity type<br>
     * 6. (super) It is not already under a non-permanent transfiguration<br>
     * 7. The entity is a LivingEntity<br>
     * 8. The entity type is in the transfiguration map, if it is populated<br>
     *
     * @param entity the entity to check
     * @return true if it can be changed
     */
    protected boolean canTransfigure(@NotNull Entity entity) {
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
}
