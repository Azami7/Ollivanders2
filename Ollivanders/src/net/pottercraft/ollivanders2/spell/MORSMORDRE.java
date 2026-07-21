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
 * Morsmordre: conjures the Dark Mark as a green creeper-burst firework and applies BAD_OMEN to nearby players for a
 * skill-scaled duration.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Morsmordre">Harry Potter Wiki - Morsmordre</a>
 */
public final class MORSMORDRE extends AddPotionEffectInRadius {
    static final int fireworkPower = 1;

    static final List<Color> fireworkColors = new ArrayList<>() {{
        add(Color.GREEN);
    }};

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
     * Launch the green creeper-burst firework that represents the Dark Mark at the spell's location.
     */
    void doFlair() {
        EntityCommon.shootFirework(location, false, false, false, fireworkPower, fireworkColors, null, fireworkType);
    }
}
