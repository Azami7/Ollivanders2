package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.divination.O2DivinationType;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Ovomancy is an ancient greek form of divination by interpreting the inside (yolk and whites) of an egg.
 * http://harrypotter.wikia.com/wiki/Ovomancy
 *
 * @author Azami7
 * @since 2.2.9
 */
public class OVOGNOSIS extends Divination
{
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public OVOGNOSIS (Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.OVOGNOSIS;
        divinationType = O2DivinationType.OVOMANCY;
        branch = O2MagicBranch.DIVINATION;

        flavorText = new ArrayList<>()
        {{
            add("Egg divination was a common practice for ancient Greeks and Romans.");
            add("A practice that dates back to Orpheus of Greeze allows the one to know the future though interpreting eggs.");
        }};

        text = "By studying the shape of the egg whites and yolks in water, the seer will have the future revealed to them.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public OVOGNOSIS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        spellType = O2SpellType.OVOGNOSIS;
        divinationType = O2DivinationType.OVOMANCY;
        branch = O2MagicBranch.DIVINATION;

        facingBlock = Material.CAULDRON;
        facingBlockString = "a cauldron";

        itemHeld = O2ItemType.EGG;
        itemHeldString = "an egg";
        consumeHeld = true;

        initSpell();
    }
}
