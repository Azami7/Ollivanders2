package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.EntityCommon;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Morsmordre - The Dark Mark Conjuration.
 *
 * <p>Conjures the Dark Mark (a skull with a serpent protruding from its mouth) in the sky above
 * the caster's location. The spell launches a green creeper-effect firework and applies the
 * BAD_OMEN potion effect to nearby players within a radius based on caster experience.</p>
 *
 * <p>Spell Mechanics:</p>
 * <ul>
 * <li>Creates a green skull firework with creeper burst effect</li>
 * <li>Applies BAD_OMEN effect to nearby players within 10-20 blocks</li>
 * <li>Effect duration ranges from 5-10 minutes depending on caster skill</li>
 * <li>Launches the Dark Mark as a visual flair effect at the spell's location</li>
 * </ul>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Morsmordre">Morsmordre - Harry Potter Wiki</a>
 */
public final class MORSMORDRE extends AddPotionEffectInRadius {
    /**
     * The power level for the firework
     */
    static final int fireworkPower = 1;

    /**
     * The color for the fireworks
     */
    static final List<Color> fireworkColors = new ArrayList<>() {{
        add(Color.GREEN);
    }};

    /**
     * The firework effect type
     */
    static final FireworkEffect.Type fireworkType = Type.CREEPER;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public MORSMORDRE(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.DARK_ARTS;
        spellType = O2SpellType.MORSMORDRE;

        flavorText = new ArrayList<>() {{
            add("\"Should the Dark Mark appear over any dwelling place or other  building, DO NOT ENTER, but contact the Auror office immediately.\" -Ministry of Magic");
            add("Then he realised it was a colossal skull, comprised of what looked like emerald stars, with a serpent protruding from its mouth like a tongue. As they watched, it rose higher and higher, blazing in a haze of greenish smoke, etched against the black sky like a new constellation.");
        }};

        text = "Conjures the Dark Mark in the sky.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public MORSMORDRE(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.DARK_ARTS;
        spellType = O2SpellType.MORSMORDRE;

        flair = true;
        minEffectRadius = 10;
        maxEffectRadius = 20;
        minDurationInSeconds = 300; // 5 mins
        maxDurationInSeconds = 600; // 10 mins
        targetSelf = false;

        potionEffectTypes.add(PotionEffectType.BAD_OMEN);

        initSpell();
    }

    /**
     * Display the Dark Mark firework as a visual flair effect.
     *
     * <p>Launches a green creeper-effect firework at the spell's location to create the
     * visual appearance of the Dark Mark appearing in the sky.</p>
     */
    void doFlair() {
        EntityCommon.shootFirework(location, false, false, false, fireworkPower, fireworkColors, null, fireworkType);
    }
}
