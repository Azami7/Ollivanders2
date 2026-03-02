package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * VOLATUS - The broomstick flight enchantment spell.
 *
 * <p>Enchants a broomstick with the ability to fly. When a player holds a VOLATUS-enchanted
 * broomstick, the enchanted items system enables flight mode, allowing the player to ride
 * the broomstick through the air.</p>
 *
 * <p>Spell Mechanics:</p>
 *
 * <ul>
 * <li>Target: BASIC_BROOM only (O2ItemType restriction)</li>
 * <li>Strength multiplier: 0.75x (reduces spell experience effectiveness)</li>
 * <li>Maximum magnitude: 10</li>
 * <li>Classification: Charms</li>
 * <li>Effect: Enables flight when held (see {@link net.pottercraft.ollivanders2.item.enchantment.VOLATUS})</li>
 * </ul>
 *
 * @see net.pottercraft.ollivanders2.item.enchantment.VOLATUS the enchantment that powers this spell
 * @see <a href="https://harrypotter.fandom.com/wiki/Broomstick">Harry Potter Wiki - Broomstick</a>
 */
public final class VOLATUS extends ItemEnchant {
    /**
     * Constructor for generating spell information.
     *
     * <p>Initializes the spell with flavor text and description. Do not use to cast the spell.
     * Use the full constructor with player and wand parameters instead.</p>
     *
     * @param plugin the Ollivanders2 plugin
     */
    public VOLATUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.VOLATUS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("\"As every school-age wizard knows, the fact that we fly on broomsticks is probably our worst-kept secret. No Muggle illustration of a witch is complete without a broom and however ludicrous these drawings are (for none of the broomsticks depicted by Muggles would stay up in the air for a moment), they remind us that we were careless for far too many centuries to be surprised that broomsticks and magic are inextricably linked in the Muggle mind.\" -Kennilworthy Whisp, Quidditch Through the Ages");
        }};

        text = "Volatus is used to enchant a broomstick for flight. "
                + "To make a magical broomstick, you must first craft a broomstick.  This recipe requires two sticks and a wheat. "
                + "Place the first stick in the upper-right corner, the next stick in the center, and the wheat in the lower-left. "
                + "Once you have a broomstick, place it in the ground in front of you and cast the spell Volatus at it.";
    }

    /**
     * Constructor for casting the VOLATUS flight enchantment spell.
     *
     * <p>Initializes the spell with the player and wand information needed to cast and track the spell.
     * VOLATUS can only enchant BASIC_BROOM items (no generic material fallback). Spell magnitude is
     * calculated using the formula: magnitude = (int)((usesModifier / 10) * 0.75), capped at maxMagnitude of 10.</p>
     *
     * @param plugin    the Ollivanders2 plugin
     * @param player    the player casting this spell
     * @param rightWand the wand strength/correctness factor
     */
    public VOLATUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.VOLATUS;
        branch = O2MagicBranch.CHARMS;

        enchantmentType = ItemEnchantmentType.VOLATUS;

        // set to only work for broomsticks
        o2ItemTypeAllowList.add(O2ItemType.BASIC_BROOM);

        // magnitude = (int) ((usesModifier / 10) * strength)
        strength = 0.75;
        // spell experience of 200 would result in a natural magnitude of 15
        maxMagnitude = 10;

        initSpell();
    }
}
