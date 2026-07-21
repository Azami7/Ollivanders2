package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * FLAGRANTE - The Burning Curse: enchants an item so it burns whoever picks it up.
 *
 * <p>The burn is applied by the {@link net.pottercraft.ollivanders2.item.enchantment.FLAGRANTE} enchantment on
 * pickup and does not scale with caster skill.</p>
 *
 * @see net.pottercraft.ollivanders2.item.enchantment.FLAGRANTE the enchantment that powers this spell
 * @see <a href="https://harrypotter.fandom.com/wiki/Flagrante_Curse">Harry Potter Wiki - Flagrante Curse</a>
 */
public final class FLAGRANTE extends ItemEnchant {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
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
     * Constructor for casting FLAGRANTE. All FLAGRANTE enchantments behave identically regardless of caster skill.
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