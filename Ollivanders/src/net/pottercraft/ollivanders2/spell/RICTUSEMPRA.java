package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * The Tickling Charm, also known as the Tickle Charm - Rictusempra - is a charm that caused the target to buckle with laughter.
 *
 * @author Azami7
 * @see <a href = "https://harrypotter.fandom.com/wiki/Tickling_Charm">https://harrypotter.fandom.com/wiki/Tickling_Charm</a>
 * @since 2.21
 */
public class RICTUSEMPRA extends AddO2Effect {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public RICTUSEMPRA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.RICTUSEMPRA;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Tickling Charm");
            add("\"Harry pointed his wand straight at Malfoy and shouted, 'Rictusempra!' A jet of silver light hit Malfoy in the stomach and he doubled up, wheezing....as Malfoy sank to his knees; Harry had hit him with a Tickling Charm, and he could barely move for laughing.\"");
        }};

        text = "The Tickling Charm will cause the target to buckle with laughter from uncontrollable tickling.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public RICTUSEMPRA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.RICTUSEMPRA;
        branch = O2MagicBranch.CHARMS;

        // effect
        effectsToAdd.add(O2EffectType.LAUGHING);
        effectsToAdd.add(O2EffectType.TICKLING);

        maxDurationInSeconds = 180;

        // pass-through materials
        projectilePassThrough.remove(Material.WATER);

        initSpell();
    }
}
