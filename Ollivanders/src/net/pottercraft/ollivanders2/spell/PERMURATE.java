package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.EntityCommon;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Switching Spell
 *
 * @author Azami7
 * @see <a href = "https://harrypotter.fandom.com/wiki/Switching_Spell">https://harrypotter.fandom.com/wiki/Switching_Spell</a>
 * @since 2.21
 */
public class PERMURATE extends ItemToItemTransfiguration {
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

        // world guard
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.ITEM_DROP);

        permanent = true;

        initSpell();

        if (successRate < 10)
            successRate = 10;
    }

    /**
     * Look for items that can be swapped
     */
    @Override
    void transfigure() {
        if (isTransfigured)
            // we've already transfigured something
            return;

        Item itemOne = null;
        // find an item close to the spell projectile path
        for (Entity entity : getCloseEntities(1.5)) {
            if ((entity instanceof Item) && canTransfigure(entity)) {
                itemOne = (Item) entity;

                common.printDebugMessage("permurate: targeting " + itemOne.getName(), null, null, false);
                break;
            }
        }
        if (itemOne == null)
            return;

        // stop the spell from continuing since we targeted an item
        kill();

        // now find a second item near the first to swap with
        Item itemTwo = null;
        for (Entity entity : EntityCommon.getEntitiesInRadius(itemOne.getLocation(), 4)) {
            if (entity.getUniqueId().equals(itemOne.getUniqueId()))
                continue;

            if ((entity instanceof Item) && canTransfigure(entity)) {
                itemTwo = (Item) entity;

                common.printDebugMessage("permurate: second target " + itemOne.getName(), null, null, false);
                break;
            }
        }
        if (itemTwo == null)
            return;

        Material itemOneMaterial = itemOne.getItemStack().getType();
        Material itemTwoMaterial = itemTwo.getItemStack().getType();

        // save the original types so we can revert if something goes wrong
        changedItems.put(itemOne, itemOneMaterial);
        changedItems.put(itemTwo, itemTwoMaterial);

        // make item one the item two type
        changeItem(itemOne, itemTwoMaterial);

        if (isTransfigured)
            // make item two the item one type
            changeItem(itemTwo, itemOneMaterial);
        else {
            return;
        }

        if (!isTransfigured) {
            // change back itemOne
            permanent = false;
            revert();
        }
    }
}
