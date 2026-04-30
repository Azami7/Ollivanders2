package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Permanently transfigures a tadpole into a decorated pot.
 * <p>
 * Ranaculus Amphoram is the Minecraft adaptation of "Snail to Teapot," a Hogwarts Transfiguration
 * exercise. Vanilla Minecraft has no snails, so the spell targets tadpoles (one of the smallest
 * mobs available), and teapots are represented as {@link Material#DECORATED_POT decorated pots}.
 * </p>
 * <p>
 * Like {@link LEPUS_SACCULUM}, Ranaculus Amphoram has a {@link #minSuccessRate 25% minimum success
 * rate} regardless of caster skill, reflecting the spell's relative ease in canon. The
 * transfiguration is permanent: the original tadpole is consumed and replaced with the pot.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Snail_to_Teapot">Harry Potter Wiki - Snail to Teapot</a>
 */
public class RANACULUS_AMPHORAM extends LivingEntityToItemTransfiguration {
    /**
     * Minimum per-tick success rate (as a percentage) for this spell, regardless of caster skill.
     * <p>
     * Ranaculus Amphoram is intentionally easy to cast: even a caster with zero experience has at
     * least a 25% chance per tick to successfully transfigure the targeted tadpole. Used in
     * {@link #doInitSpell()} as the floor when {@code usesModifier} is at or below this value.
     * </p>
     */
    public static int minSuccessRate = 25;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     * <p>
     * Sets the descriptive {@link #text} for the spell info display. Does not initialize the
     * {@link #transfigurationMap} or any cast-time state — the casting constructor handles that.
     * </p>
     *
     * @param plugin the Ollivanders2 plugin
     */
    public RANACULUS_AMPHORAM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.RANACULUS_AMPHORAM;

        text = "The transfiguration spell Ranaculus Amphoram will transfigure a tadpole into a decorated pot.";
    }

    /**
     * Constructor for casting Ranaculus Amphoram.
     * <p>
     * Configures the tadpole→decorated pot entry in the {@link #transfigurationMap}, sets the
     * projectile {@link #radius} and success message, then invokes {@link #initSpell()} which
     * triggers {@link #doInitSpell()} to compute the success rate based on caster skill.
     * </p>
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public RANACULUS_AMPHORAM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.RANACULUS_AMPHORAM;

        transfigurationMap.put(EntityType.TADPOLE, Material.DECORATED_POT);
        radius = 1.5;
        successMessage = "The tadpole transforms.";

        initSpell();
    }

    /**
     * Calculate the per-tick success rate from the caster's skill, with a {@link #minSuccessRate} floor.
     * <p>
     * If {@code usesModifier} is at or below {@link #minSuccessRate}, the success rate is clamped
     * to that floor; otherwise it equals {@code usesModifier} directly. Higher skill therefore
     * produces a proportionally higher per-tick chance to transfigure the tadpole while the spell
     * projectile is active.
     * </p>
     */
    @Override
    void doInitSpell() {
        if (usesModifier <= minSuccessRate)
            successRate = minSuccessRate;
        else
            successRate = (int) (usesModifier);
    }
}
