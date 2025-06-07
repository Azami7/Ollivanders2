package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Green sparks charm
 *
 * @author Azami7
 * @since 2.21
 */
public final class VERDIMILLIOUS extends SparksBase {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public VERDIMILLIOUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.VERDIMILLIOUS;

        flavorText = new ArrayList<>() {{
            add("Green Sparks Charm");
        }};

        text = "Shoots green sparks out of the caster's wand.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public VERDIMILLIOUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.VERDIMILLIOUS;
        moveEffectData = Material.GREEN_STAINED_GLASS;
        damageModifier = 0;

        initSpell();
    }
}
