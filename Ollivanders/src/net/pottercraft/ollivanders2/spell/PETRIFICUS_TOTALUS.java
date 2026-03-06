package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Petrificus Totalus - The Full Body-Bind Curse.
 *
 * <p>PETRIFICUS_TOTALUS is a curse that temporarily paralyzes the target player's entire body,
 * preventing all movement and rotation. The victim becomes completely rigid and unable to perform any
 * action, much like a soldier at attention. The spell can target any player regardless of size or status,
 * making it a universally applicable immobilization curse.</p>
 *
 * <p>Spell Mechanics:</p>
 * <ul>
 * <li>Targets any player without restrictions</li>
 * <li>Applies FULL_IMMOBILIZE effect (prevents all movement including rotation)</li>
 * <li>No additional effects are applied</li>
 * <li>Duration scales with spell usage (usesModifier), clamped to 30-300 seconds</li>
 * <li>Complete immobilization leaves the victim helpless but unharmed</li>
 * </ul>
 *
 * @author Azami7
 * @see <a href="http://harrypotter.wikia.com/wiki/Full_Body-Bind_Curse">Harry Potter Wiki - Full Body-Bind Curse</a></p>
 */
public class PETRIFICUS_TOTALUS extends ImmobilizePlayer {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PETRIFICUS_TOTALUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.PETRIFICUS_TOTALUS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Full Body-Bind Curse");
            add("\"Neville's arms snapped to his sides. His legs sprang together. His whole body rigid, he swayed where he stood and then fell flat on his face, stiff as a board. Neville's jaws were jammed together so he couldn't speak. Only his eyes were moving, looking at them in horror.\"");
            add("\"Harry's body became instantly rigid and immobile, and he felt himself fall back against the tower wall, propped like an unsteady statue.\"");
        }};

        text = "Temporarily paralyzes a person.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PETRIFICUS_TOTALUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PETRIFICUS_TOTALUS;
        branch = O2MagicBranch.CHARMS;

        fullImmobilize = true;

        initSpell();
    }

    /**
     * Determine if a player can be targeted by this curse.
     *
     * <p>Petrificus Totalus can target any player without restrictions. There are no protective effects
     * or conditions that would prevent this curse from affecting a player.</p>
     *
     * @param target the player to validate as a potential target
     * @return always returns true, as any player can be targeted
     */
    boolean canTarget(Player target) {
        return true; // we can target any player
    }
}
