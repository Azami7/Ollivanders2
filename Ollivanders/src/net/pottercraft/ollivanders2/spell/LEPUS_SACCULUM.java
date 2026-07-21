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
     * Minimum per-tick success rate (percent) regardless of caster skill, applied as the floor in
     * {@link #doInitSpell()}. This spell is intentionally easy to cast, so even a zero-experience caster clears it.
     */
    public static int minSuccessRate = 25;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
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
     * Set the per-tick success rate to the caster's skill, floored at {@link #minSuccessRate}.
     */
    @Override
    void doInitSpell() {
        if (usesModifier <= minSuccessRate)
            successRate = minSuccessRate;
        else
            successRate = (int) (usesModifier);
    }
}
