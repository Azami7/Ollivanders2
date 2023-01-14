package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.EntityCommon;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Pack is the incantation of a spell used to make items pack themselves into a trunk.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Pack_Charm
 */
public final class PACK extends O2Spell
{
    /**
     * The radius of things to pick up
     */
    private int radius;

    private final static int minRadius = 3;
    private final static int maxRadius = 20;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PACK(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.PACK;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>()
        {{
            add("Books, clothes, telescope and scales all soared into the air and flew pell-mell into the trunk.");
            add("The Packing Charm");
        }};

        text = "When cast at an ender chest or shulker box, it will suck any items nearby into it.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PACK(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PACK;
        branch = O2MagicBranch.CHARMS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.CHEST_ACCESS);

        // only allow targeting ender chests and shulker boxes - aka "magic" chests
        materialAllowList.add(Material.ENDER_CHEST);
        materialAllowList.add(Material.WHITE_SHULKER_BOX);
        materialAllowList.add(Material.BLACK_SHULKER_BOX);
        materialAllowList.add(Material.BLUE_SHULKER_BOX);
        materialAllowList.add(Material.SHULKER_BOX);
        materialAllowList.add(Material.BROWN_SHULKER_BOX);
        materialAllowList.add(Material.CYAN_SHULKER_BOX);
        materialAllowList.add(Material.GRAY_SHULKER_BOX);
        materialAllowList.add(Material.GREEN_SHULKER_BOX);
        materialAllowList.add(Material.LIGHT_BLUE_SHULKER_BOX);
        materialAllowList.add(Material.LIGHT_GRAY_SHULKER_BOX);
        materialAllowList.add(Material.LIME_SHULKER_BOX);
        materialAllowList.add(Material.MAGENTA_SHULKER_BOX);
        materialAllowList.add(Material.ORANGE_SHULKER_BOX);
        materialAllowList.add(Material.PINK_SHULKER_BOX);
        materialAllowList.add(Material.PURPLE_SHULKER_BOX);
        materialAllowList.add(Material.RED_SHULKER_BOX);
        materialAllowList.add(Material.YELLOW_SHULKER_BOX);

        materialBlockedList.removeAll(materialAllowList); // remove these unbreakables for this spell only

        initSpell();
    }

    /**
     * Set the radius based on the caster's skill
     */
    @Override
    void doInitSpell()
    {
        radius = (int) usesModifier / 10;

        if (radius > maxRadius)
            radius = maxRadius;
        else if (radius < minRadius)
            radius = minRadius;
    }

    /**
     * Find a nearby shulker box or ender chest and put any nearby items in to it
     */
    @Override
    protected void doCheckEffect()
    {
        if (!hasHitTarget())
            return;

        kill();
        common.printDebugMessage("Packing chest", null, null, false);

        // get nearby items
        List<Item> nearbyItems = EntityCommon.getItemsInRadius(location, radius);

        for (Item item : nearbyItems)
        {
            common.printDebugMessage("Adding " + item.getName() + " to chest", null, null, false);

            HashMap<Integer, ItemStack> overflow;

            if (getTargetBlock().getType() == Material.ENDER_CHEST)
                overflow = player.getEnderChest().addItem(item.getItemStack());
            else if (getTargetBlock().getState() instanceof ShulkerBox)
                overflow = ((ShulkerBox) getTargetBlock().getState()).getInventory().addItem(item.getItemStack());
            else
            {
                common.printDebugMessage("Target chest is not an ender chest or shulker box", null, null, true);
                return;
            }

            // figure out how much fit
            if (overflow.size() < 1)
                item.remove();
            else
            {
                // how many did it stash
                for (ItemStack itemStack : overflow.values())
                {
                    if (itemStack.getType() == item.getItemStack().getType())
                    {
                        int diff = item.getItemStack().getAmount() - itemStack.getAmount();
                        item.getItemStack().setAmount(diff);
                    }
                }
            }
        }
    }
}