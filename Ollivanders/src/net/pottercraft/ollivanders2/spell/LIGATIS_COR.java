package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.item.wand.O2WandCoreType;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Spell that binds a core material to a coreless wand to create a finished wand.
 *
 * <p>When cast near a coreless wand and a core material item, the spell combines them into a complete wand.
 * One coreless wand and one core material are consumed per cast.</p>
 *
 * @author Azami7
 * @see FRANGE_LIGNEA for the complementary log-to-coreless-wand spell
 */
public final class LIGATIS_COR extends O2Spell {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public LIGATIS_COR(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.LIGATIS_COR;
        branch = O2MagicBranch.CHARMS;

        text = "Ligatis Cor will bind a coreless wand to a core material. Make sure the two items are near each other when this spell is cast. You can only use this on one coreless wand and one core material at a time.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public LIGATIS_COR(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.LIGATIS_COR;
        branch = O2MagicBranch.CHARMS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.ITEM_PICKUP);
            worldGuardFlags.add(Flags.ITEM_DROP);
        }

        initSpell();
    }

    /**
     * Look for any coreless wands near the projectile and try to make a wand.
     */
    @Override
    protected void doCheckEffect() {
        // projectile has stopped, kill the spell
        if (hasHitTarget())
            kill();

        Item corelessWand = null;

        for (Item item : getNearbyItems(1.5)) {
            if (Ollivanders2API.getItems().getWands().isCorelessWand(item.getItemStack())) {
                corelessWand = item;
                break;
            }
        }

        if (corelessWand == null) {
            if (isKilled()) // we never found a coreless wand
                sendFailureMessage();
            return;
        }

        // we've found a coreless wand candidate to target, kill the spell
        kill();

        // attempt to create the wand and drop it in world
        createAndDropWand(corelessWand);
    }

    /**
     * Find a nearby core material, create a finished wand, decrement both item stacks, and drop the wand item in
     * to the world.
     *
     * @param corelessWand a coreless wand item entity
     */
    private void createAndDropWand(@NotNull Item corelessWand) {
        ItemStack wand;

        // is there a core item type nearby?
        Item coreItem = getNearbyWandCore(corelessWand.getLocation());
        if (coreItem == null) {
            sendFailureMessage(); // we only send here because this is the only player-caused failure, all others should not happen, they would be code bugs
            return;
        }

        O2ItemType coreItemType = Ollivanders2API.getItems().getItemTypeByItem(coreItem);
        if (coreItemType == null) {
            common.printDebugMessage("LIGATIS_COR.createAndDropWand: failed to find core O2Item", null, null, true);
            return;
        }

        O2WandCoreType coreType = O2WandCoreType.getWandCoreTypeByItemType(coreItemType);
        if (coreType == null) {
            common.printDebugMessage("LIGATIS_COR.createAndDropWand: failed to get O2WandCore by type", null, null, true);
            return;
        }

        wand = Ollivanders2API.getItems().getWands().makeWandFromCoreless(corelessWand.getItemStack(), coreType, 1);

        if (wand == null) {
            common.printDebugMessage("LIGATIS_COR.createAndDropWand: failed to make wand", null, null, true);
            return;
        }

        int amount = corelessWand.getItemStack().getAmount();

        // decrement the baseWand amount
        if (amount > 1)
            corelessWand.getItemStack().setAmount(amount - 1);
        else
            corelessWand.remove();

        // decrement the core material
        amount = coreItem.getItemStack().getAmount();
        if (amount > 1)
            coreItem.getItemStack().setAmount(amount - 1);
        else
            coreItem.remove();

        // drop the wand
        world.dropItemNaturally(location, wand);
    }

    /**
     * Get a wand core near the base wand.
     *
     * @param location the location of the base wand
     * @return the Item if a wand core was found nearby, null otherwise
     */
    @Nullable
    private Item getNearbyWandCore(Location location) {
        for (Item item : EntityCommon.getItemsInRadius(location, 2)) {
            if (O2WandCoreType.isWandCore(item)) {
                common.printDebugMessage("ligatis cor: found core item near wand", null, null, false);
                return item;
            }
        }

        return null;
    }
}