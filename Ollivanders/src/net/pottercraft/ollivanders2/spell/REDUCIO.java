package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Shrinks a giant to a normal zombie, makes certain entities babies and slimes smaller.
 *
 * @author Azami7
 * @see <a href = "https://harrypotter.fandom.com/wiki/Shrinking_Charm">https://harrypotter.fandom.com/wiki/Shrinking_Charm</a>
 */
public final class REDUCIO extends ChangeEntitySizeSuper {
    private static final int maxRadiusConfig = 20;
    private static final int maxTargetsConfig = 10;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public REDUCIO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.REDUCIO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Shrinking Charm");
            add("These straightforward but surprisingly dangerous charms cause certain things to swell or shrink. You will be learning both charms together, so that you can always undo an over-enthusiastic cast. There is thus no excuse for having accidentally shrunk your homework down to microscopic size or for allowing a giant toad to rampage through your school’s flower gardens.");
        }};

        text = "Makes adult entities babies and slimes smaller.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public REDUCIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.REDUCIO;
        branch = O2MagicBranch.CHARMS;

        growing = false;
        maxTargets = maxTargetsConfig;
        maxRadius = maxRadiusConfig;

        initSpell();
    }

    /**
     * Set the number of targets and the radius based on caster's experience.
     */
    @Override
    void doInitSpell() {
        targets = (int) (usesModifier / 10) + 1;
        if (targets > maxTargets)
            targets = maxTargets;

        radius = (int) (usesModifier / 10) + 1;
        if (radius > maxRadius)
            radius = maxRadius;
    }
}