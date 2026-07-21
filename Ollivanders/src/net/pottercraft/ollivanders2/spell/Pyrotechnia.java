package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.common.EntityCommon;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for spells that launch fireworks at the caster's location. The number fired scales with the caster's
 * skill up to a per-spell maximum; subclasses configure the color, power, effect type, and flags.
 *
 * @see <a href="https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/Firework.html">Bukkit Firework API</a>
 */
public abstract class Pyrotechnia extends O2Spell {
    /**
     * The maximum number of fireworks allowed for this type
     */
    int maxFireworks = 1;

    /**
     * The number of fireworks to create
     */
    int numberOfFireworks = 1;

    /**
     * The number of fireworks the spell has created
     */
    private int fireworksCount = 0;

    /**
     * The power level for the fireworks
     */
    int fireworkPower = 2;

    /**
     * The color(s) for the fireworks
     */
    List<Color> fireworkColors = new ArrayList<>();

    /**
     * The fade color(s) for the fireworks
     */
    List<Color> fadeColors = new ArrayList<>();

    /**
     * The firework effect type
     */
    FireworkEffect.Type fireworkType = Type.BALL;

    /**
     * True if firework has trails
     */
    boolean trails = false;

    /**
     * True if this firework has fade
     */
    boolean fade = false;

    /**
     * True if this firework has flicker
     */
    boolean flicker = false;

    /**
     * True if this firework should shuffle types
     */
    boolean shuffleTypes = false;

    /**
     * Firework effect types picked from at random when {@link #shuffleTypes} is set.
     */
    List<Type> randomTypes = new ArrayList<>() {{
        add(Type.STAR);
        add(Type.BALL_LARGE);
        add(Type.BALL);
        add(Type.BURST);
    }};

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public Pyrotechnia(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public Pyrotechnia(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        noProjectile = true;

        fireworkColors.add(Color.WHITE);
    }

    /**
     * Launch the configured fireworks, one every 10 ticks up to the skill-scaled count, then end the spell.
     */
    @Override
    protected void doCheckEffect() {
        if (fireworksCount == 0) // first time through, calculate the number of fireworks to create
            setNumberOfFireworks();

        if (fireworksCount < numberOfFireworks) {
            if (getAge() % 10 == 0) { // only shoot a firework every 10 ticks

                if (shuffleTypes)
                    setRandomFireworkType();

                EntityCommon.shootFirework(location, trails, fade, flicker, fireworkPower, fireworkColors, fadeColors, fireworkType);

                fireworksCount = fireworksCount + 1;
            }
        }
        else
            kill();
    }

    /**
     * Set {@link #numberOfFireworks} to {@code usesModifier / 10}, limited to [1, {@link #maxFireworks}].
     */
    void setNumberOfFireworks() {
        numberOfFireworks = (int) usesModifier / 10;

        if (numberOfFireworks < 1)
            numberOfFireworks = 1;
        else if (numberOfFireworks > maxFireworks)
            numberOfFireworks = maxFireworks;
    }

    /**
     * Pick a random {@link #fireworkType} from {@link #randomTypes} when {@link #shuffleTypes} is set.
     */
    void setRandomFireworkType() {
        if (shuffleTypes) {
            int rand = Ollivanders2Common.random.nextInt(randomTypes.size());

            fireworkType = randomTypes.get(rand);
        }
    }

    /**
     * Get the explosion power level of the fireworks.
     *
     * @return the firework power (0-4)
     */
    public int getFireworkPower() {
        return fireworkPower;
    }

    /**
     * Get the explosion effect type of the fireworks.
     *
     * @return the firework effect type
     */
    public Type getFireworkType() {
        return fireworkType;
    }

    /**
     * Get the primary colors applied to the fireworks.
     *
     * @return a copy of the list of explosion colors
     */
    public List<Color> getFireworkColors() {
        return new ArrayList<>() {{
            addAll(fireworkColors);
        }};
    }

    /**
     * Check if the fireworks have a fade effect.
     *
     * @return true if fade colors are applied, false otherwise
     */
    public boolean hasFade() {
        return fade;
    }

    /**
     * Get the fade colors applied to the fireworks.
     *
     * @return a copy of the list of fade colors
     */
    public List<Color> getFadeColors() {
        return new ArrayList<>() {{
            addAll(fadeColors);
        }};
    }

    /**
     * Get the number of fireworks that have been spawned so far.
     *
     * @return the count of fireworks launched
     */
    public int getFireworksCount() {
        return fireworksCount;
    }

    /**
     * Get the total number of fireworks to spawn for this spell.
     *
     * @return the target number of fireworks
     */
    public int getNumberOfFireworks() {
        return numberOfFireworks;
    }

    /**
     * Get the maximum number of fireworks allowed for this spell type.
     *
     * @return the maximum firework count
     */
    public int getMaxFireworks() {
        return maxFireworks;
    }
}
