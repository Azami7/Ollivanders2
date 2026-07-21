package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * GEMINO - The Doubling Curse: enchants an item so it multiplies when picked up.
 *
 * <p>On pickup the {@link net.pottercraft.ollivanders2.item.enchantment.GEMINO} enchantment duplicates the item
 * exponentially (2^magnitude copies, up to 2^8 at maximum skill), potentially flooding the bearer with copies.</p>
 *
 * @see net.pottercraft.ollivanders2.item.enchantment.GEMINO the enchantment that powers this spell
 * @see <a href="https://harrypotter.fandom.com/wiki/Doubling_Charm">Harry Potter Wiki - Doubling Charm</a>
 */
public final class GEMINO extends ItemEnchant {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    public GEMINO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.GEMINO;
        branch = O2MagicBranch.CURSE;

        flavorText = new ArrayList<>() {{
            add("Hermione screamed in pain, and Harry turned his wand on her in time to see a jewelled goblet tumbling from her grip. But as it fell, it split, became a shower of goblets, so that a second later, with a great clatter, the floor was covered in identical cups rolling in every direction, the original impossible to discern amongst them.");
            add("The Doubling Curse");
        }};

        text = "Gemino will cause an item to duplicate when picked up.";
    }

    /**
     * Constructor for casting GEMINO. The number of copies scales with caster skill up to a maximum magnitude of 8
     * (2^8 copies).
     *
     * @param plugin    the Ollivanders2 plugin instance
     * @param player    the player casting this spell
     * @param rightWand the wand strength/correctness factor
     */
    public GEMINO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.GEMINO;
        branch = O2MagicBranch.CURSE;
        enchantmentType = ItemEnchantmentType.GEMINO;

        // magnitude = (int) ((usesModifier / 10) * strength)
        strength = 0.75;
        // spell experience of 200 would result in a natural magnitude of 15
        maxMagnitude = 8; // 2^8

        initSpell();
    }
}