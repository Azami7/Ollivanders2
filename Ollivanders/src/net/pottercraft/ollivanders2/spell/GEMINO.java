package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * GEMINO duplication spell implementation.
 * <p>
 * GEMINO is a dark arts spell that enchants an item with the Doubling Curse. When a player picks up
 * a GEMINO-enchanted item, it duplicates exponentially (2^magnitude copies), potentially filling
 * containers and overwhelming the player with copies.</p>
 * <p>
 * Spell mechanics:</p>
 * <ul>
 * <li>Strength: 0.25x modifier multiplier</li>
 * <li>Max magnitude: 10 (produces 2^10 = 1024 copies at maximum)</li>
 * <li>Classification: Dark Arts</li>
 * </ul>
 *
 * @see net.pottercraft.ollivanders2.item.enchantment.GEMINO the enchantment that powers this spell
 * @see <a href="https://harrypotter.fandom.com/wiki/Doubling_Charm">Doubling Charm - Harry Potter Wiki</a>
 * @author Azami7
 */
public final class GEMINO extends ItemEnchant {
    /**
     * Constructor for generating spell information (spell name, text, lore).
     * <p>
     * This constructor initializes the spell with flavor text and description but does not perform
     * any actual spell casting. Use the full constructor with player and wand parameters to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin instance
     */
    public GEMINO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.GEMINO;
        branch = O2MagicBranch.DARK_ARTS;

        flavorText = new ArrayList<>() {{
            add("Hermione screamed in pain, and Harry turned his wand on her in time to see a jewelled goblet tumbling from her grip. But as it fell, it split, became a shower of goblets, so that a second later, with a great clatter, the floor was covered in identical cups rolling in every direction, the original impossible to discern amongst them.");
            add("The Doubling Curse");
        }};

        text = "Gemino will cause an item to duplicate when picked up.";
    }

    /**
     * Constructor for casting the GEMINO duplication spell.
     * <p>
     * Initializes the spell with the player and wand information needed to cast and track the spell.
     * Spell magnitude is calculated based on player skill modifiers and wand strength.
     * </p>
     *
     * @param plugin   the Ollivanders2 plugin instance
     * @param player   the player casting this spell
     * @param rightWand the wand strength/power modifier (1.0 = normal, higher = more powerful)
     */
    public GEMINO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.GEMINO;
        branch = O2MagicBranch.DARK_ARTS;
        enchantmentType = ItemEnchantmentType.GEMINO;

        // magnitude = (int) ((usesModifier / 4) * strength)
        strength = 0.25;
        // spell experience of 200 would result in a natural magnitude of 12.5
        maxMagnitude = 10; // 2^10

        initSpell();
    }
}