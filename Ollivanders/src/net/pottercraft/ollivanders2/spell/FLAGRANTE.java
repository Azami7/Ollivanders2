package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * FLAGRANTE - The Burning Curse spell.
 *
 * <p>Enchants an item to burn its bearer when picked up. When a player touches a FLAGRANTE-enchanted
 * item, the enchanted items system triggers the FLAGRANTE effect, dealing damage to the player.</p>
 *
 * <p>Spell Mechanics:</p>
 *
 * <ul>
 * <li>Classification: Dark Arts</li>
 * <li>Can enchant any non-wand, non-enchanted item type</li>
 * <li>No magnitude or strength modifiers (burns equally at all experience levels)</li>
 * <li>Effect: FLAGRANTE enchantment effect on pickup (see {@link net.pottercraft.ollivanders2.item.enchantment.FLAGRANTE})</li>
 * </ul>
 *
 * @see net.pottercraft.ollivanders2.item.enchantment.FLAGRANTE the enchantment that powers this spell
 * @see <a href="https://harrypotter.fandom.com/wiki/Flagrante_Curse">Harry Potter Wiki - Flagrante Curse</a>
 */
public final class FLAGRANTE extends ItemEnchant {
    /**
     * Constructor for generating spell information.
     *
     * <p>Initializes the spell with flavor text and description. Do not use to cast the spell.
     * Use the full constructor with player and wand parameters instead.</p>
     *
     * @param plugin the Ollivanders2 plugin
     */
    public FLAGRANTE(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.FLAGRANTE;
        branch = O2MagicBranch.DARK_ARTS;

        flavorText = new ArrayList<>() {{
            add("The Burning Curse");
            add("They have added Geminio and Flagrante curses! Everything you touch will burn and multiply, but the copies are worthless.");
        }};

        text = "Flagrante will cause an item to burn its bearer when picked up.";
    }

    /**
     * Constructor for casting the FLAGRANTE spell.
     *
     * <p>Initializes the spell with the player and wand information needed to cast and track the spell.
     * Magnitude is not used for FLAGRANTE (all FLAGRANTE enchantments behave identically regardless
     * of caster skill), but is still calculated following the standard ItemEnchant formula.</p>
     *
     * @param plugin    the Ollivanders2 plugin
     * @param player    the player casting this spell
     * @param rightWand the wand strength/correctness factor
     */
    public FLAGRANTE(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.FLAGRANTE;
        branch = O2MagicBranch.DARK_ARTS;
        enchantmentType = ItemEnchantmentType.FLAGRANTE;

        initSpell();
    }
}