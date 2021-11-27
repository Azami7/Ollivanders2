package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
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

public abstract class ItemTransfiguration extends EntityTransfiguration
{
    /**
     * The list of changed items for non-permanent spells to revert
     */
    protected Map<Item, Material> changedItems = new HashMap<>();

    /**
     * If this is populated, any material type key will be changed to the value
     */
    protected Map<Material, Material> transfigurationMap = new HashMap<>();

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ItemTransfiguration(Ollivanders2 plugin)
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
    public ItemTransfiguration(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        entityWhitelist.add(EntityType.DROPPED_ITEM);
    }

    /**
     * Look for items and change them to the new type
     */
    @Override
    void transfigure()
    {
        if (isTransfigured)
            // we've already transfigured something
            return;

        for (Entity entity : getCloseEntities(1.5))
        {
            p.getLogger().info("checking " + entity.getName());

            if (!canTransfigure(entity))
            {
                p.getLogger().info("!canTransfigure(entity)" + entity.getName());
                continue;
            }

            if (!(entity instanceof Item))
            {
                p.getLogger().info("!(entity instanceof Item)" + entity.getName());
                continue;
            }

            Item item = (Item)entity;
            Material originalType = item.getItemStack().getType();

            if (!transfigurationMap.containsKey(originalType))
            {
                p.getLogger().info("!transfigurationMap.containsKey(originalType)" + entity.getName());
                continue;
            }

            Material newType = transfigurationMap.get(originalType);

            int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);
            if (rand < successRate)
            {
                if (!permanent)
                    changedItems.put(item, originalType);

                ItemStack itemStack = item.getItemStack();
                itemStack.setType(newType);
                item.setItemStack(itemStack);

                isTransfigured = true;

                player.sendMessage(Ollivanders2.chatColor + successMessage);
            }
            else
            {
                player.sendMessage(Ollivanders2.chatColor + failureMessage);
            }

            kill();
            return;
        }
    }

    /**
     * Revert all items to their original types.
     */
    @Override
    void doRevert()
    {
        if (permanent)
            return;

        for (Item item : changedItems.keySet())
        {
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
    protected Entity transfigureEntity(@NotNull Entity entity)
    {
        return null;
    }
}
