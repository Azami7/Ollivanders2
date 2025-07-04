package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Pulls an item towards the caster.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 * @see <a href = "https://harrypotter.fandom.com/wiki/Seize_and_pull_charm">https://harrypotter.fandom.com/wiki/Seize_and_pull_charm</a>
 */
public final class CARPE_RETRACTUM extends Knockback {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public CARPE_RETRACTUM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.CARPE_RETRACTUM;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("\"...which is why the Carpe Retractum spell is useful. It allows you to seize and pull objects within your direct line of sight towards you...\" -Professor Flitwick");
            add("Seize and Pull Charm");
        }};

        text = "Pulls an item towards you.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public CARPE_RETRACTUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.CARPE_RETRACTUM;
        branch = O2MagicBranch.CHARMS;

        minVelocity = 0.25;
        maxVelocity = 3;
        pull = true;

        initSpell();
    }
}