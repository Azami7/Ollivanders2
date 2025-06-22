package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Enchants a broomstick to fly.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Broomstick">a href = "https://harrypotter.fandom.com/wiki/Broomstick</a>
 */
public final class VOLATUS extends ItemEnchant
{
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public VOLATUS(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.VOLATUS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>()
        {{
            add("\"As every school-age wizard knows, the fact that we fly on broomsticks is probably our worst-kept secret. No Muggle illustration of a witch is complete without a broom and however ludicrous these drawings are (for none of the broomsticks depicted by Muggles would stay up in the air for a moment), they remind us that we were careless for far too many centuries to be surprised that broomsticks and magic are inextricably linked in the Muggle mind.\" -Kennilworthy Whisp, Quidditch Through the Ages");
        }};

        text = "Volatus is used to enchant a broomstick for flight. "
                + "To make a magical broomstick, you must first craft a broomstick.  This recipe requires two sticks and a wheat. "
                + "Place the first stick in the upper-right corner, the next stick in the center, and the wheat in the lower-left. "
                + "Once you have a broomstick, place it in the ground in front of you and cast the spell Volatus at it.";
    }

    /**
     * Constructor
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public VOLATUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        spellType = O2SpellType.VOLATUS;
        branch = O2MagicBranch.CHARMS;

        enchantmentType = ItemEnchantmentType.VOLATUS;

        // set to only work for broomsticks
        itemTypeAllowlist.add(O2ItemType.BASIC_BROOM);

        // magnitude = (int) ((usesModifier / 4) * strength)
        strength = 0.25;
        // spell experience of 200 would result in a natural magnitude of 12.5
        maxMagnitude = 10; // 2^10

        initSpell();
    }
}
