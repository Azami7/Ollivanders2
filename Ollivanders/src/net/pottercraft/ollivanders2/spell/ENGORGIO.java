package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Engorgement Charm that makes entities grow larger.
 *
 * <p>When cast, the spell targets living entities within a skill-based radius (up to 20 blocks)
 * and grows them. Baby peaceful creatures become adults, while Slimes and Magma Cubes increase
 * in size. At higher skill levels (100+), the spell can also affect hostile mobs.</p>
 *
 * <p>Spell Mechanics:</p>
 * <ul>
 * <li>Target Limit: Up to 10 entities per cast (scales with skill)</li>
 * <li>Range: 20-block maximum detection radius (scales with skill)</li>
 * <li>Effects: Babies → Adults, Slimes grow 1-2 sizes</li>
 * <li>Restrictions: Hostile mobs require skill level &ge; 100</li>
 * </ul>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Engorgement_Charm">Engorgement Charm</a>
 */
public final class ENGORGIO extends ChangeEntitySize {
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
            add("These straightforward but surprisingly dangerous charms cause certain entities to swell or shrink.");
        }};

        text = "Makes baby peaceful and neutral entities adults and small slimes and magma cubes larger. At higher skill levels it will affect hostile entities and makes slimes and magma cubes even bigger.";
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
        maxEffectRadius = maxRadiusConfig;

        initSpell();
    }

    /**
     * Initializes the spell by calculating targets and effect radius based on caster skill.
     *
     * <p>Called during spell initialization to set up dynamic values that scale with the
     * caster's experience with the spell. Both target count and effect radius are clamped
     * to their configured limits (10 targets max, 20 blocks max).</p>
     */
    @Override
    void doInitSpell() {
        calculateNumberOfTargets();
        calculateEffectRadius();
    }
}