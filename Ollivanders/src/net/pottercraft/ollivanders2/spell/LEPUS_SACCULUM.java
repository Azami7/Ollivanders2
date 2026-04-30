package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Permanently transfigures a rabbit into a white bundle.
 * <p>
 * Lepus Sacculum is the Minecraft adaptation of "Mice to Snuffboxes," a third-year Hogwarts
 * Transfiguration lesson. Vanilla Minecraft has no mice, so the spell targets rabbits, and
 * snuffboxes are represented as {@link Material#WHITE_BUNDLE white bundles}.
 * </p>
 * <p>
 * Unlike most other transfiguration spells, Lepus Sacculum has a {@link #minSuccessRate 25%
 * minimum success rate} regardless of caster skill, reflecting the spell's relative ease in canon.
 * The transfiguration is permanent: the original rabbit is consumed and replaced with the bundle.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Mice_to_Snuffboxes">Harry Potter Wiki - Mice to Snuffboxes</a>
 */
public class LEPUS_SACCULUM extends LivingEntityToItemTransfiguration {
    /**
     * Minimum per-tick success rate (as a percentage) for this spell, regardless of caster skill.
     * <p>
     * Lepus Sacculum is intentionally easy to cast: even a caster with zero experience has at
     * least a 25% chance per tick to successfully transfigure the targeted rabbit. Used in
     * {@link #doInitSpell()} as the floor when {@code usesModifier} is at or below this value.
     * </p>
     */
    public static int minSuccessRate = 25;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     * <p>
     * Populates {@link #flavorText} with the canon "Mice to Snuffboxes" excerpt and sets the
     * descriptive {@link #text}. Does not initialize the {@link #transfigurationMap} or any
     * cast-time state — the casting constructor handles that.
     * </p>
     *
     * @param plugin the Ollivanders2 plugin
     */
    public LEPUS_SACCULUM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.LEPUS_SACCULUM;

        flavorText = new ArrayList<>() {{
            add("\"Professor McGonagall watched them turn a mouse into a snuffbox - points were given for how pretty the snuffbox was, but taken away if it had whiskers.\"");
        }};

        text = "The transfiguration spell Lepus Sacculum will transfigure a rabbit into a bundle.";
    }

    /**
     * Constructor for casting Lepus Sacculum.
     * <p>
     * Configures the rabbit→white bundle entry in the {@link #transfigurationMap}, sets the
     * projectile {@link #radius} and success message, then invokes {@link #initSpell()} which
     * triggers {@link #doInitSpell()} to compute the success rate based on caster skill.
     * </p>
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public LEPUS_SACCULUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.LEPUS_SACCULUM;

        transfigurationMap.put(EntityType.RABBIT, Material.WHITE_BUNDLE);
        radius = 1.5;
        successMessage = "The rabbit transforms.";

        initSpell();
    }

    /**
     * Calculate the per-tick success rate from the caster's skill, with a {@link #minSuccessRate} floor.
     * <p>
     * If {@code usesModifier} is at or below {@link #minSuccessRate}, the success rate is clamped
     * to that floor; otherwise it equals {@code usesModifier} directly. Higher skill therefore
     * produces a proportionally higher per-tick chance to transfigure the rabbit while the spell
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
