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
 * Mov Fotia - The Purple Firecracker Attention Spell with Tranquility Zone.
 *
 * <p>Creates a stationary zone of tranquility centered on the caster that prevents hostile mob
 * targeting and projectile launches. A purple firecracker (low-power ball firework) is launched
 * from the caster's location as a visual signal. The zone's radius and duration scale with the
 * caster's experience level.</p>
 *
 * <p>Spell Mechanics:</p>
 *
 * <ul>
 * <li>Shoots a single purple firecracker from the caster's location</li>
 * <li>Creates a TRANQUILLUS stationary spell (zone of tranquility) centered on the caster</li>
 * <li>Radius range: 5-20 blocks (same as TRANQUILLUS)</li>
 * <li>Duration range: 30 seconds to 5 minutes (same as TRANQUILLUS)</li>
 * <li>Within the zone: entities cannot target other entities, and projectiles cannot be launched</li>
 * </ul>
 *
 * <p>Seen/Mentioned: On 31 October 1991, Albus Dumbledore used this spell to get the attention
 * of panicking diners in the Great Hall when a troll was loose in the castle.</p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Purple_Firecrackers">https://harrypotter.fandom.com/wiki/Purple_Firecrackers</a>
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

    @Override
    @Nullable
    protected O2StationarySpell createStationarySpell() {
        EntityCommon.shootFirework(location, false, false, false, fireworkPower, fireworkColors, null, fireworkType);

        return new TRANQUILLUS(p, caster.getUniqueId(), location, radius, duration);
    }
}
