package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Grows a stationary shield spell's radius. Only the player who created the shield spell can change its radius.
 * <p>
 * Extends {@link HORREAT_PROTEGAT} (the shrink charm), setting {@link #shrink} to false so the shared logic
 * grows the targeted shields instead of shrinking them.
 * </p>
 *
 * @author Azami7
 * @see HORREAT_PROTEGAT
 */
public final class CRESCERE_PROTEGAT extends HORREAT_PROTEGAT {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public CRESCERE_PROTEGAT(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.CRESCERE_PROTEGAT;
        branch = O2MagicBranch.CHARMS;

        text = "Grows a stationary shield spell's radius. Only the player who created the shield spell can change its radius.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public CRESCERE_PROTEGAT(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.CRESCERE_PROTEGAT;
        branch = O2MagicBranch.CHARMS;

        // make the spell increase the size, rather than decrease
        shrink = false;

        initSpell();
    }
}