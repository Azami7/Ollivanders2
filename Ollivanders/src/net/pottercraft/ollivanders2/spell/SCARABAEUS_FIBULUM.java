package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Permanently transfigures an endermite into a polished blackstone button.
 * <p>
 * Scarabaeus Fibulum is the Minecraft adaptation of "Beetle into Button," a Hogwarts Transfiguration
 * exercise. Vanilla Minecraft has no beetles, so the spell targets endermites, and buttons are
 * represented as {@link Material#POLISHED_BLACKSTONE_BUTTON polished blackstone buttons}.
 * </p>
 * <p>
 * Like {@link LEPUS_SACCULUM} and {@link RANACULUS_AMPHORAM}, Scarabaeus Fibulum has a
 * {@link #minSuccessRate 25% minimum success rate} regardless of caster skill, reflecting the
 * spell's relative ease in canon. The transfiguration is permanent: the original endermite is
 * consumed and replaced with the button.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Beetle_into_Button">Harry Potter Wiki - Beetle into Button</a>
 */
public class SCARABAEUS_FIBULUM extends LivingEntityToItemTransfiguration {
    /**
     * Minimum per-tick success rate (as a percentage) for this spell, regardless of caster skill.
     * <p>
     * Scarabaeus Fibulum is intentionally easy to cast: even a caster with zero experience has at
     * least a 25% chance per tick to successfully transfigure the targeted endermite. Used in
     * {@link #doInitSpell()} as the floor when {@code usesModifier} is at or below this value.
     * </p>
     */
    public static int minSuccessRate = 25;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     * <p>
     * Populates {@link #flavorText} with the canon "Beetle into Button" excerpt and sets the
     * descriptive {@link #text}. Does not initialize the {@link #transfigurationMap} or any
     * cast-time state — the casting constructor handles that.
     * </p>
     *
     * @param plugin the Ollivanders2 plugin
     */
    public SCARABAEUS_FIBULUM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.SCARABAEUS_FIBULUM;

        flavorText = new ArrayList<>() {{
            add("\"He was supposed to be turning a beetle into a button, but all he managed to do was give his beetle a lot of exercise as it scuttled over the desk top avoiding his wand.\"");
        }};

        text = "The transfiguration spell Scarabaeus Fibulum will transfigure an endermite into a button.";
    }

    /**
     * Constructor for casting Scarabaeus Fibulum.
     * <p>
     * Configures the endermite→polished blackstone button entry in the {@link #transfigurationMap},
     * sets the projectile {@link #radius} and success message, then invokes {@link #initSpell()}
     * which triggers {@link #doInitSpell()} to compute the success rate based on caster skill.
     * </p>
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public SCARABAEUS_FIBULUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.SCARABAEUS_FIBULUM;

        transfigurationMap.put(EntityType.ENDERMITE, Material.POLISHED_BLACKSTONE_BUTTON);
        radius = 1.5;
        successMessage = "The endermite transforms.";

        initSpell();
    }

    /**
     * Calculate the per-tick success rate from the caster's skill, with a {@link #minSuccessRate} floor.
     * <p>
     * If {@code usesModifier} is at or below {@link #minSuccessRate}, the success rate is clamped
     * to that floor; otherwise it equals {@code usesModifier} directly. Higher skill therefore
     * produces a proportionally higher per-tick chance to transfigure the endermite while the spell
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
