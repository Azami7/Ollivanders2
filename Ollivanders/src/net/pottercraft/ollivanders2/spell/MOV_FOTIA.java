package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import net.pottercraft.ollivanders2.stationaryspell.TRANQUILLUS;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Mov Fotia - The Purple Firecracker charm: launches a purple firecracker and creates a zone of tranquility centered
 * on the caster.
 *
 * <p>Inside the zone — a {@link TRANQUILLUS} stationary spell — entities cannot target one another and projectiles
 * cannot be launched. Its radius and duration scale with the caster's skill.</p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Purple_Firecrackers">Harry Potter Wiki - Purple Firecrackers</a>
 */
public final class MOV_FOTIA extends StationarySpell {
    /**
     * The power level for the firework
     */
    static final int fireworkPower = 1;

    /**
     * The color for the fireworks
     */
    static final List<Color> fireworkColors = new ArrayList<>() {{
        add(Color.PURPLE);
    }};

    /**
     * The firework effect type
     */
    static final FireworkEffect.Type fireworkType = Type.BALL;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public MOV_FOTIA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.MOV_FOTIA;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("It took several purple firecrackers exploding from the end of Professor Dumbledore's wand to bring silence.");
            add("Purple Firecrackers");
        }};

        text = "Shoots a purple firecracker and pacifies the area around the caster.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public MOV_FOTIA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.MOV_FOTIA;
        branch = O2MagicBranch.CHARMS;

        maxRadius = TRANQUILLUS.maxRadiusConfig;
        minRadius = TRANQUILLUS.minRadiusConfig;
        maxDuration = TRANQUILLUS.maxDurationConfig;
        minDuration = TRANQUILLUS.minDurationConfig;
        durationModifierInSeconds = 0;
        flair = false;
        noProjectile = true;

        initSpell();
    }

    /**
     * Launch the purple firecracker as a visual signal, then create the tranquility zone.
     *
     * @return a new TRANQUILLUS stationary spell with this cast's radius and duration
     */
    @Override
    @Nullable
    protected O2StationarySpell createStationarySpell() {
        EntityCommon.shootFirework(location, false, false, false, fireworkPower, fireworkColors, null, fireworkType);

        return new TRANQUILLUS(p, caster.getUniqueId(), location, radius, duration);
    }
}
