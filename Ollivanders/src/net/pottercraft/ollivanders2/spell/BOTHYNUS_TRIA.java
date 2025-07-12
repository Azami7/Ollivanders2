package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Fanciest version of Bothynus.
 */
public final class BOTHYNUS_TRIA extends Pyrotechnia {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public BOTHYNUS_TRIA(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.BOTHYNUS_TRIA;
        branch = O2MagicBranch.CHARMS;

        text = "Creates one or more yellow and orange star fireworks with trails and that fades to silver.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public BOTHYNUS_TRIA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.BOTHYNUS_TRIA;
        branch = O2MagicBranch.CHARMS;

        fireworkColors = new ArrayList<>();
        fireworkColors.add(Color.YELLOW);
        fireworkColors.add(Color.ORANGE);

        fadeColors = new ArrayList<>();
        fadeColors.add(Color.SILVER);

        fireworkType = Type.STAR;
        hasTrails = true;
        hasFade = true;

        maxFireworks = 15;

        initSpell();
    }

    /**
     * Set the number of fireworks that can be cast based on the user's experience.
     */
    @Override
    void doInitSpell() {
        setNumberOfFireworks();
    }
}
