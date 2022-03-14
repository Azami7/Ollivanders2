package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Shoots green fireworks in to the air.
 */
public final class VERDIMILLIOUS extends Pyrotechnia
{
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public VERDIMILLIOUS(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.VERDIMILLIOUS;
        text = "Conjures large green ball fireworks in the air.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public VERDIMILLIOUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        spellType = O2SpellType.VERDIMILLIOUS;

        fireworkColors = new ArrayList<>();
        fireworkColors.add(Color.GREEN);
        fireworkType = Type.BALL_LARGE;

        initSpell();
    }

    @Override
    void doInitSpell()
    {
        setMaxFireworks(10);
    }
}
