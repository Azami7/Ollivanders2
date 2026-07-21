package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.block.BlockCommon;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Herbivicus, the Plant-Growing Charm, advances the growth of crops within a radius of where the spell lands.
 * <p>
 * Cast as a projectile, Herbivicus affects every {@link Ageable} crop block within a radius of the block it strikes,
 * advancing each crop's age toward maturity. Both the radius and the amount of growth scale with the caster's
 * experience, up to {@link #maxRadius} blocks and {@link #maxGrowth} age stages respectively.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Herbivicus_Charm">Harry Potter Wiki - Herbivicus Charm</a>
 */
public final class HERBIVICUS extends O2Spell {
    /**
     * The maximum radius, in blocks, that the spell can affect regardless of caster skill.
     */
    private final static int maxRadius = 15;

    /**
     * The maximum number of age stages a crop can be advanced in a single cast.
     */
    private final static int maxGrowth = 7;

    /**
     * The radius of crops that can be targeted
     */
    private int radius;

    /**
     * The amount the crops will grow
     */
    private int growth;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public HERBIVICUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.HERBIVICUS;
        branch = O2MagicBranch.HERBOLOGY;

        flavorText = new ArrayList<>() {{
            add("The Plant-Growing Charm");
        }};

        text = "Herbivicus causes crops within a radius to grow.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public HERBIVICUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.HERBIVICUS;
        branch = O2MagicBranch.HERBOLOGY;

        // pass-through materials
        projectilePassThrough.add(Material.WATER);
        projectilePassThrough.add(Material.FIRE);
        projectilePassThrough.add(Material.SOUL_FIRE);

        // required worldGuard state flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        initSpell();
    }

    /**
     * Scale the affected {@link #radius} and {@link #growth} amount by the caster's skill.
     * <p>
     * The radius is one quarter of {@code usesModifier} and the growth one twenty-fifth, each limited to a minimum
     * of 1 and capped at {@link #maxRadius} and {@link #maxGrowth} respectively.
     * </p>
     */
    @Override
    void doInitSpell() {
        radius = (int) ((usesModifier / 4));

        if (radius < 1)
            radius = 1;
        else if (radius > maxRadius)
            radius = maxRadius;

        growth = (int) ((usesModifier / 25));
        if (growth < 1)
            growth = 1;
        else if (growth > maxGrowth)
            growth = maxGrowth;
    }

    /**
     * Advance the growth of every crop within {@link #radius} blocks of where the spell landed.
     * <p>
     * Runs only once the projectile has hit a block. Each {@link Ageable} crop in range has its age advanced by
     * {@link #growth} stages, up to its maximum age. Fire and soul fire are also {@link Ageable} but are not crops,
     * so they are skipped.
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitBlock())
            return;

        for (Block block : BlockCommon.getBlocksInRadius(location, radius)) {
            BlockData blockData = block.getBlockData();

            if (blockData instanceof Ageable) {
                // fire and soul fire are Ageable but are not crops, so don't "grow" them
                if (block.getType() == Material.FIRE || block.getType() == Material.SOUL_FIRE)
                    continue;

                Ageable cropData = (Ageable) blockData;

                if (cropData.getAge() != cropData.getMaximumAge()) {
                    int toGrow = cropData.getAge() + growth;
                    if (toGrow > cropData.getMaximumAge())
                        toGrow = cropData.getMaximumAge();

                    cropData.setAge(toGrow);
                    block.setBlockData(cropData);
                }
            }
        }

        kill();
    }

    /**
     * Get the radius, in blocks, this cast affects. Set by {@link #doInitSpell()} from the caster's skill.
     *
     * @return the affected radius
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Get the number of age stages this cast advances each crop. Set by {@link #doInitSpell()} from the caster's skill.
     *
     * @return the growth amount
     */
    public int getGrowth() {
        return growth;
    }
}