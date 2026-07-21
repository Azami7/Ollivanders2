package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.effect.FLYING;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Give the caster the ability to fly.
 * <p>
 * Tom Riddle is the first wizard known to achieve unassisted flying and only one other wizard learned it from him,
 * Severus Snape. Unassisted flying is against magical law.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Unsupported_flight">Harry Potter Wiki - Unsupported flight</a>
 */
public final class VENTO_FOLIO extends O2Spell {
    /**
     * The percent chance this spell will succeed each casting.
     */
    int successRate = 0;

    /**
     * The duration, in seconds, that the player will fly if successful
     */
    int durationInSeconds = 0;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public VENTO_FOLIO(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.DARK_ARTS;
        spellType = O2SpellType.VENTO_FOLIO;

        flavorText = new ArrayList<>() {{
            add("\"And then Harry saw him. Voldemort was flying like smoke on the wind, without broomstick or thestral to hold him, his snake-like face gleaming out of the blackness, his white fingers raising his wand again —\"");
            add("\"Remus, he can -\"\n\"Fly, I saw him too, he came after Hagrid and me.\" -Kingsley Shacklebolt and Harry Potter");
        }};

        text = "Vento Folio gives the caster the ability to fly unassisted for an amount of time.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public VENTO_FOLIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.DARK_ARTS;
        spellType = O2SpellType.VENTO_FOLIO;

        noProjectile = true;

        initSpell();
    }

    /**
     * Set the per-cast success rate and flight duration from the caster's skill.
     */
    @Override
    void doInitSpell() {
        calculateSuccessRate();
        calculateDuration();
    }

    /**
     * Set {@link #successRate} from the caster's skill: the chance rises with {@code usesModifier} to 100% at
     * mastery, with a 5% floor for novices.
     */
    void calculateSuccessRate() {
        if (usesModifier >= 200)
            successRate = 100;
        else if (usesModifier >= 100)
            successRate = (int) usesModifier / 2;
        else if (usesModifier >= 50)
            successRate = 25;
        else if (usesModifier >= 25)
            successRate = 10;
        else
            successRate = 5;
    }

    /**
     * Set {@link #durationInSeconds}, the flight time granted on a successful cast, from the caster's skill: the
     * duration rises with {@code usesModifier}, with a 5-second floor for novices. Converted to game ticks in
     * {@link #doCheckEffect()} before the flight effect is applied.
     */
    void calculateDuration() {
        if (usesModifier >= 100)
            durationInSeconds = (int)(usesModifier / 2);
        else if (usesModifier >= 50)
            durationInSeconds = 30;
        else if (usesModifier >= 10)
            durationInSeconds = 10;
        else
            durationInSeconds = 5;
    }

    /**
     * Get the chance, as a percent, that a cast at the caster's current skill succeeds.
     * <p>
     * Set by {@link #calculateSuccessRate()} during {@link #doInitSpell()}, so the value is only
     * meaningful after the spell has been initialized for casting.
     * </p>
     *
     * @return the success rate as a percent in the range 5 to 100
     */
    public int getSuccessRate() {
        return successRate;
    }

    /**
     * Get the flight duration, in seconds, granted by a successful cast at the caster's current skill.
     * <p>
     * Set by {@link #calculateDuration()} during {@link #doInitSpell()}, so the value is only
     * meaningful after the spell has been initialized for casting.
     * </p>
     *
     * @return the flight duration in seconds
     */
    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    /**
     * Roll against the success rate and, on success, grant the caster unassisted flight.
     * <p>
     * This is a no-projectile spell, so this runs on the first tick. A random roll under
     * {@link #successRate} adds a {@link FLYING} effect lasting {@link #durationInSeconds} (converted
     * to game ticks). The spell is killed afterward so it affects the caster only once per cast.
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        int rand = Ollivanders2Common.random.nextInt(100);
        int duration = durationInSeconds * Ollivanders2Common.ticksPerSecond;

        if (rand < successRate) {
            FLYING effect = new FLYING(p, duration, false, caster.getUniqueId());
            Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

            common.printDebugMessage("VENTO_FOLIO: Adding effect ", null, null, false);
        }

        kill();
    }
}