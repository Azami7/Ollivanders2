package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Creates a storm of a variable duration.
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Metelojinx">https://harrypotter.fandom.com/wiki/Metelojinx</a>
 */
public final class METELOJINX extends MetelojinxSuper {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public METELOJINX(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.METELOJINX;
        branch = O2MagicBranch.CHARMS;

        text = "Metelojinx will turn a sunny day into a storm.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public METELOJINX(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.METELOJINX;
        branch = O2MagicBranch.CHARMS;

        storm = true;

        initSpell();
    }
}