package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Mice to snuffboxes - https://harrypotter.fandom.com/wiki/Mice_to_Snuffboxes - for minecraft we are using rabbits and bundles
 *
 * @since 2.21
 * @author Azami7
 */
public class LEPUS_SACCULUM extends LivingEntityToItemTransfiguration {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public LEPUS_SACCULUM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.LEPUS_SACCULUM;
        branch = O2MagicBranch.TRANSFIGURATION;

        flavorText = new ArrayList<>() {{
            add("\"Professor McGonagall watched them turn a mouse into a snuffbox - points were given for how pretty the snuffbox was, but taken away if it had whiskers.\"");
        }};

        text = "The transfiguration spell Lepus Sacculum will transfigure a rabbit into a bundle.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public LEPUS_SACCULUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.LEPUS_SACCULUM;
        branch = O2MagicBranch.TRANSFIGURATION;

        transfigurationMap.put(EntityType.RABBIT, Material.WHITE_BUNDLE);
        radius = 1.5;
        successMessage = "The rabbit transforms.";

        initSpell();
    }

    /**
     * Determine success rate based on caster's skill
     */
    @Override
    void doInitSpell() {
        // chance is no less than 25%
        if (usesModifier <= 25)
            successRate = 25;
        else
            successRate = (int) (usesModifier);
    }
}
