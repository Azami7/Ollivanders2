package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Beetle to button for minecraft we are using endermites to button
 *
 * @author Azami7
 * @see <a href = "https://harrypotter.fandom.com/wiki/Beetle_into_Button">https://harrypotter.fandom.com/wiki/Beetle_into_Button</a>
 * @since 2.21
 */
public class SCARABAEUS_FIBULUM extends LivingEntityToItemTransfiguration {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public SCARABAEUS_FIBULUM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.SCARABAEUS_FIBULUM;
        branch = O2MagicBranch.TRANSFIGURATION;

        flavorText = new ArrayList<>() {{
            add("\"He was supposed to be turning a beetle into a button, but all he managed to do was give his beetle a lot of exercise as it scuttled over the desk top avoiding his wand.\"");
        }};

        text = "The transfiguration spell Scarabaeus Fibulum will transfigure an endermite into a button.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public SCARABAEUS_FIBULUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.SCARABAEUS_FIBULUM;
        branch = O2MagicBranch.TRANSFIGURATION;

        transfigurationMap.put(EntityType.ENDERMITE, Material.POLISHED_BLACKSTONE_BUTTON);
        radius = 1.5;
        successMessage = "The endermite transforms.";

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
