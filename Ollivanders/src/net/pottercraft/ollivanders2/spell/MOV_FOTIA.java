package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Causes purple firecrackers to shoot out from the tip of one's wand.
 * <p>
 * Seen/Mentioned: On 31 October 1991, Albus Dumbledore used this spell to get the attention of panicking diners in the
 * Great Hall when a troll was loose in the castle.
 */
public final class MOV_FOTIA extends Pyrotechnia
{
    // todo make this a sparks spell rather than a firework spell, should also pacify in a radius
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public MOV_FOTIA(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.MOV_FOTIA;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>()
        {{
            add("It took several purple firecrackers exploding from the end of Professor Dumbledore's wand to bring silence.");
            add("Purple Firecrackers");
        }};

        text = "Causes purple firecrackers to shoot out from the tip of one's wand.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public MOV_FOTIA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        spellType = O2SpellType.MOV_FOTIA;
        branch = O2MagicBranch.CHARMS;

        fireworkColors = new ArrayList<>();
        fireworkColors.add(Color.PURPLE);
        fireworkType = Type.BALL;
        fireworkPower = 0;

        maxFireworks = 1;

        initSpell();
    }

    /**
     * Set the number of fireworks, will always be 1
     */
    @Override
    void doInitSpell()
    {
        setNumberOfFireworks();
    }
}
