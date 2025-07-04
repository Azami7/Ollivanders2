package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Makes certain entities grow into adults, slimes grow larger, and at usesModifier 10, zombies grow into giants
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Engorgement_Charm">https://harrypotter.fandom.com/wiki/Engorgement_Charm</a>
 */
public final class ENGORGIO extends ChangeEntitySizeSuper {
    // todo rework with swelling solution potion
    private static final int maxRadiusConfig = 20;
    private static final int maxTargetsConfig = 10;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ENGORGIO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.ENGORGIO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Engorgement Charm");
            add("These straightforward but surprisingly dangerous charms cause certain things to swell or shrink.");
        }};

        text = "Makes baby entities adults and small slimes larger.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ENGORGIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.ENGORGIO;
        branch = O2MagicBranch.CHARMS;

        growing = true;
        maxTargets = maxTargetsConfig;
        maxRadius = maxRadiusConfig;

        initSpell();
    }

    /**
     * Set number of targets and spell radius based on caster's skill
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