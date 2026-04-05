package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Switching Spell. Swaps the material types of two nearby item entities.
 *
 * <p>Finds the first targetable item near the spell projectile, then searches within
 * {@link #switchRadius} for a second item of a different material type. If both items
 * are found, their material types are swapped for up to {@link #calculateNumberOfItems()}
 * items in each stack. If either item cannot be found or both are the same type, the spell fails.</p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Switching_Spell">https://harrypotter.fandom.com/wiki/Switching_Spell</a>
 */
public class PERMURATE extends ItemToItemTransfiguration {
    /**
     * The radius to search for a second item to swap with the first.
     */
    double switchRadius = 4;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PERMURATE(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.PERMURATE;
        branch = O2MagicBranch.TRANSFIGURATION;

        flavorText = new ArrayList<>() {{
            add("The Switching Spell");
            add("\"Well, there are Switching Spells... but what's the point of Switching it? Unless you swapped its fangs for wine-gums or something that would make it less dangerous...\" -Hermione Granger");
        }};

        text = "Switches two items.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PERMURATE(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PERMURATE;
        branch = O2MagicBranch.TRANSFIGURATION;

        initSpell();

        if (successRate < 10)
            successRate = 10;
    }

    /**
     * Look for two items that can be swapped.
     *
     * <p>Finds the first targetable item, stops the projectile, then searches for a second
     * item within {@link #switchRadius} that is a different material type. If both are found,
     * swaps their material types via {@link #swapItemStacks(Item, Item)}.</p>
     */
    @Override
    protected void transfigure() {
        if (isTransfigured) // we've already transfigured something
            return;

        // get the first item
        Item itemOne = getTargetableItem(defaultRadius, null);
        if (itemOne == null) // keep searching on the next tick
            return;

        // stop the spell from continuing since we targeted an item
        stopProjectile();

        // now find a second item within the switch radius to swap with
        Item itemTwo = getTargetableItem(switchRadius, itemOne.getUniqueId());
        if (itemTwo == null) { // spell failed because we targeted item one
            return;
        }

        if (itemOne.getItemStack().getType() == itemTwo.getItemStack().getType()) {
            return; // spell failed because two items are the same type
        }

        swapItemStacks(itemOne, itemTwo);

        isTransfigured = true;
    }

    /**
     * Swap the material types of two item stacks.
     *
     * <p>Creates new item stacks with swapped material types, preserving item meta from the originals.
     * The number of items swapped is limited by {@link #calculateNumberOfItems()} and capped to the
     * smaller of the two stacks. Original items are removed if fully consumed, or reduced in amount
     * if partially consumed.</p>
     *
     * @param itemOne the first item to swap
     * @param itemTwo the second item to swap
     */
    void swapItemStacks(Item itemOne, Item itemTwo) {
        ItemStack itemOneStack = itemOne.getItemStack();
        int itemOneAmount = itemOneStack.getAmount();
        ItemStack itemTwoStack = itemTwo.getItemStack();
        int itemTwoAmount = itemTwoStack.getAmount();

        // determine how many items in the stack will change
        int transAmount = calculateNumberOfItems();
        if (transAmount > itemOneAmount)
            transAmount = itemOneAmount;
        if (transAmount > itemTwoAmount)
            transAmount = itemTwoAmount;

        // create new stacks with the material type swapped, item meta the same
        ItemStack transOneStack = new ItemStack(itemTwoStack.getType(), transAmount);
        transOneStack.setItemMeta(itemOneStack.getItemMeta());

        ItemStack transTwoStack = new ItemStack(itemOneStack.getType(), transAmount);
        transTwoStack.setItemMeta(itemTwoStack.getItemMeta());

        Location item1Loc = itemOne.getLocation();
        Location item2Loc = itemTwo.getLocation();

        // update the original stacks
        if (transAmount >= itemOneAmount)
            itemOne.remove();
        else {
            itemOneStack.setAmount(itemOneAmount - transAmount);
            itemOne.setItemStack(itemOneStack);
        }

        if (transAmount >= itemTwoAmount)
            itemTwo.remove();
        else {
            itemTwoStack.setAmount(itemTwoAmount - transAmount);
            itemTwo.setItemStack(itemTwoStack);
        }

        // drop the transfigured stacks
        world.dropItem(item1Loc, transOneStack);
        world.dropItem(item2Loc, transTwoStack);
    }
}
