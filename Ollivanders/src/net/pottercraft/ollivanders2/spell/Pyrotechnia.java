package net.pottercraft.ollivanders2.spell;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.jetbrains.annotations.NotNull;

/**
 * Shoots one or more fireworks in to the air.
 * <p>
 * https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/Firework.html
 */
public abstract class Pyrotechnia extends O2Spell
{
    /**
     * The maximum number of fireworks allowed for this type
     */
    int maxFireworks;

    /**
     * The number of fireworks left to shoot off
     */
    private int fireworksRemaining;

    /**
     * Th power level for the fireworks
     */
    int fireworkPower = 2;

    /**
     * The color(s) for the fireworks
     */
    List<org.bukkit.Color> fireworkColors = null;

    /**
     * The fade color(s) for the fireworks
     */
    List<org.bukkit.Color> fadeColors = null;

    /**
     * The firework effect type
     */
    FireworkEffect.Type fireworkType = null;

    /**
     * True if firework has trails
     */
    boolean hasTrails = false;

    /**
     * True if this firework has fade
     */
    boolean hasFade = false;

    /**
     * True if this firework has flicker
     */
    boolean hasFlicker = false;

    /**
     * True if this firework should shuffle types
     */
    boolean shuffleTypes = false;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public Pyrotechnia(Ollivanders2 plugin)
    {
        super(plugin);
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public Pyrotechnia(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
        fireworksRemaining = 0;
        maxFireworks = 1;
    }

    /**
     * Shoot a firework in to the sky from caster's location.
     */
    @Override
    public void checkEffect()
    {
        if (!isSpellAllowed())
        {
            kill();
            return;
        }

        if (fireworksRemaining > 0)
        {
            World world = location.getWorld();
            if (world == null)
            {
                common.printDebugMessage("Pyrotechnia.checkEffect: world is null", null, null, true);
                kill();
                return;
            }

            Firework firework = (Firework) (world.spawnEntity(location, EntityType.FIREWORK));

            FireworkMeta meta = firework.getFireworkMeta();
            meta.setPower(fireworkPower);

            FireworkEffect.Builder builder = FireworkEffect.builder();
            if (fireworkColors != null)
                builder.withColor(fireworkColors);
            else
                builder.withColor(Color.WHITE);

            if (shuffleTypes)
            {
                int rand = Ollivanders2Common.random.nextInt() % 4;
                if (rand == 0)
                    fireworkType = Type.STAR;
                else if (rand == 1)
                    fireworkType = Type.BALL_LARGE;
                else if (rand == 2)
                    fireworkType = Type.BALL;
                else
                    fireworkType = Type.BURST;
            }

            if (fireworkType != null)
                builder.with(fireworkType);
            else
                builder.with(Type.BALL);

            builder.flicker(hasFlicker);
            builder.trail(hasTrails);

            if (hasFade)
            {
                if (fadeColors != null)
                    builder.withFade(fadeColors);
            }

            meta.addEffect(builder.build());
            firework.setFireworkMeta(meta);

            fireworksRemaining -= 1;
        }
        else
            kill();
    }

    /**
     * Set the number of fireworks that can be cast based on the user's experience.
     */
    void setNumberOfFireworks()
    {
        fireworksRemaining = (int) usesModifier / 10;

        if (fireworksRemaining < 1)
            fireworksRemaining = 1;
        else if (fireworksRemaining > maxFireworks)
            fireworksRemaining = maxFireworks;
    }

    @Override
    protected void doCheckEffect()
    {
    }
}
