package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Bubble head charm gives the player water breathing for a length of time depending on the player's spell level.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 * @see <a href = "https://harrypotter.fandom.com/wiki/Ebublio_Jinx">https://harrypotter.fandom.com/wiki/Ebublio_Jinx</a>
 */
public final class EBUBLIO extends AddO2Effect {
    // todo this spell is wrong, ebublio is not the bubble-head charm

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public EBUBLIO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.EBUBLIO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Bubble-Head Charm");
            add("Fleur Delacour, though she demonstrated excellent use of the Bubble-Head Charm, was attacked by grindylows as she approached her goal, and failed to retrieve her hostage.");
            add("Cedric Diggory, who also used the Bubble-Head Charm, was first to return with his hostage, though he returned one minute outside the time limit of an hour.");
        }};

        text = "Gives target player the ability to breathe underwater.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public EBUBLIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.EBUBLIO;
        branch = O2MagicBranch.CHARMS;

        effectsToAdd.add(O2EffectType.WATER_BREATHING);
        strengthModifier = 1;
        minDurationInSeconds = 30;
        durationModifier = 0;
        targetSelf = true;

        initSpell();
    }
}