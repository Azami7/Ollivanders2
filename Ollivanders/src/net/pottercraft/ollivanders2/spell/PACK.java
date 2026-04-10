package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.EntityCommon;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
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
 * Packing Charm — when cast at a chest, ender chest, or shulker box, sucks any nearby items into it.
 * <p>
 * The radius of items collected scales with caster experience (see {@link #doInitSpell()}). Items that
 * fit are added to the target container's inventory and removed from the world; items that overflow
 * have their stack reduced to the leftover amount and remain dropped in the world. For ender chest
 * targets, items are routed into the caster's personal ender chest inventory rather than into the
 * targeted block.
 * </p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Pack_Charm">Pack Charm</a>
 */
public final class PACK extends O2Spell {
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
    public PACK(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.PACK;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("Books, clothes, telescope and scales all soared into the air and flew pell-mell into the trunk.");
            add("The Packing Charm");
        }};

        text = "When cast at a chest or shulker box, it will suck any items nearby into it.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PACK(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PACK;
        branch = O2MagicBranch.CHARMS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.CHEST_ACCESS);

        // only allow targeting chests and shulker boxes
        materialAllowList.add(Material.CHEST);
        materialAllowList.add(Material.TRAPPED_CHEST);
        materialAllowList.add(Material.ENDER_CHEST);
        materialAllowList.addAll(Tag.SHULKER_BOXES.getValues());

        // chests and shulker boxes are normally on the projectile blocked-target list — allow them for this spell
        materialBlockedList.removeAll(materialAllowList);

        initSpell();
    }

    /**
     * Set the radius based on the caster's skill, clamped to the {@link #minRadius}–{@link #maxRadius} range.
     */
    @Override
    void doInitSpell() {
        radius = (int) usesModifier / 10;

        if (radius > maxRadius)
            radius = maxRadius;
        else if (radius < minRadius)
            radius = minRadius;
    }

    /**
     * On projectile impact with a valid container, scoop nearby items into that container.
     * <p>
     * Iterates over all item entities within {@link #radius} of the projectile's impact point and adds
     * each to the target container's inventory. The destination depends on the target block type:
     * </p>
     * <ul>
     * <li>{@code ENDER_CHEST} — items go into the caster's personal ender chest inventory, not the block.</li>
     * <li>Any shulker box variant — items go into that shulker box's block inventory.</li>
     * <li>{@code CHEST} / {@code TRAPPED_CHEST} — items go into that chest's block inventory.</li>
     * </ul>
     * <p>
     * Items that fit completely are removed from the world. Items that don't fully fit have their stack
     * size reduced to the leftover amount and remain dropped in the world.
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitBlock())
            return;

        kill();
        common.printDebugMessage("Packing chest", null, null, false);

        // get nearby items
        List<Item> nearbyItems = EntityCommon.getItemsInRadius(location, radius);

        Block targetBlock = getTargetBlock();
        if (targetBlock == null) {
            common.printDebugMessage("PACK.doCheckEffect: target block is null", null, null, false);
            return;
        }

        for (Item item : nearbyItems) {
            common.printDebugMessage("Adding " + item.getItemStack().getType().name() + " to chest", null, null, false);

            HashMap<Integer, ItemStack> overflow;

            if (targetBlock.getType() == Material.ENDER_CHEST)
                overflow = caster.getEnderChest().addItem(item.getItemStack());
            else if (targetBlock.getState() instanceof ShulkerBox state)
                overflow = state.getInventory().addItem(item.getItemStack());
            else if (targetBlock.getState() instanceof Chest chestState) { // it's a normal chest
                // MockBukkit's Chest.getInventory() returns a fresh snapshot inventory on each
                // getState() call, so spell-side writes don't surface to test-side reads.
                // getBlockInventory() returns the persistent backing snapshot, which both sides
                // share. In real Paper, getInventory() is the live tile-entity inventory and is
                // the correct call.
                if (Ollivanders2.testMode)
                    overflow = chestState.getBlockInventory().addItem(item.getItemStack());
                else
                    overflow = chestState.getInventory().addItem(item.getItemStack());
            }
            else {
                common.printDebugMessage("PACK.doCheckEffect: block is not a chest or shulker box - material allowList flaw", null, null, true);
                return;
            }

            // figure out how much fit
            if (overflow.isEmpty())
                item.remove();
            else {
                item.setItemStack(overflow.values().iterator().next());
            }
        }
    }
}