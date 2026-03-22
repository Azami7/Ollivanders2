package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Super class for all item entity transfigurations
 *
 * @author Azami7
 */
public abstract class ItemToItemTransfiguration extends EntityTransfiguration {
    /**
     * The list of changed items for non-permanent spells to revert
     */
    protected Map<Item, Material> changedItems = new HashMap<>();

    /**
     * If this is populated, any material type key will be changed to the value
     */
    protected Map<Material, Material> transfigurationMap = new HashMap<>();

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ItemToItemTransfiguration(Ollivanders2 plugin) {
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
    public ItemToItemTransfiguration(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        entityAllowedList.add(EntityType.ITEM);

        // world guard
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.ITEM_PICKUP);
            worldGuardFlags.add(Flags.ITEM_DROP);
        }
    }

    /**
     * Look for items and change them to the new type
     */
    @Override
    void transfigure() {
        if (isTransfigured)
            // we've already transfigured something
            return;

        for (Entity entity : getNearbyEntities(defaultRadius)) {
            common.printDebugMessage("checking " + entity.getName(), null, null, false);

            if (!(entity instanceof Item)) {
                common.printDebugMessage("!(entity instanceof Item)" + entity.getName(), null, null, false);
                continue;
            }

            Item item = (Item) entity;
            Material originalType = item.getItemStack().getType();
            originalEntity = entity;

            if (!transfigurationMap.containsKey(originalType)) {
                common.printDebugMessage("!transfigurationMap.containsKey(originalType)" + entity.getName(), null, null, false);
                continue;
            }

            Material newType = transfigurationMap.get(originalType);
            changeItem(item, newType);


            if (isTransfigured)
                sendSuccessMessage();
            else
                sendFailureMessage();
        }
    }

    /**
     * Change an item to a new type
     *
     * @param item    the item to change
     * @param newType the new item type to change to
     */
    void changeItem(Item item, Material newType) {
        if (canTransfigure(item)) {
            Material originalType = item.getItemStack().getType();

            if (!permanent)
                changedItems.put(item, originalType);

            ItemStack itemStack = item.getItemStack();
            itemStack.setType(newType);
            item.setItemStack(itemStack);

            isTransfigured = true;
        }
        else
            isTransfigured = false;
    }

    /**
     * Revert all items to their original types.
     */
    @Override
    void doRevert() {
        if (permanent)
            return;

        for (Item item : changedItems.keySet()) {
            ItemStack itemStack = item.getItemStack();
            itemStack.setType(changedItems.get(item));
            item.setItemStack(itemStack);
        }
    }

    /**
     * Transfigures entity into new EntityType.
     *
     * @param entity the entity to transfigure
     * @return the transfigured entity
     */
    @Nullable
    protected Entity transfigureEntity(@NotNull Entity entity) {
        return entity;
    }
}
