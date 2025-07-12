package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Snail to teapot for mincraft using tadpole as one of the smallest mobs
 *
 * @author Azami7
 * @see <a href = "https://harrypotter.fandom.com/wiki/Snail_to_Teapot">https://harrypotter.fandom.com/wiki/Snail_to_Teapot</a>
 * @since 2.21
 */
public class RANACULUS_AMPHORAM extends LivingEntityToItemTransfiguration {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public RANACULUS_AMPHORAM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.RANACULUS_AMPHORAM;
        branch = O2MagicBranch.TRANSFIGURATION;

        text = "The transfiguration spell Ranaculus Amphoram will transfigure a tadpole into a decorated pot.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public RANACULUS_AMPHORAM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.RANACULUS_AMPHORAM;
        branch = O2MagicBranch.TRANSFIGURATION;

        transfigurationMap.put(EntityType.TADPOLE, Material.DECORATED_POT);
        radius = 1.5;
        successMessage = "The tadpole transforms.";

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
