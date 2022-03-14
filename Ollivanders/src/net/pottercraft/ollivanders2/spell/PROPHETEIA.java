package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Reveal an unfulfilled prophecy about a player.
 * <p>
 * https://harrypotter.fandom.com/wiki/Prophecy
 * {@link net.pottercraft.ollivanders2.divination.O2Prophecy}
 */
public class PROPHETEIA extends O2Spell
{
    private final static int messageRadius = 10;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PROPHETEIA(Ollivanders2 plugin)
    {
        super(plugin);

        branch = O2MagicBranch.DIVINATION;
        spellType = O2SpellType.PROPHETEIA;

        flavorText = new ArrayList<>()
        {{
            add("\"But when Sybill Trelawney spoke, it was not in her usual ethereal, mystic voice, but in the hard, hoarse tones Harry had heard her use once before.\"");
        }};

        text = "Propheteia allows one to reveal an unfulfilled prophecy that has been made about a target player. Chances of success depend on experience.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PROPHETEIA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.DIVINATION;
        spellType = O2SpellType.PROPHETEIA;
        initSpell();
    }

    /**
     * Find a nearby player and reveal an unfulfilled prophecy about them.
     */
    @Override
    protected void doCheckEffect()
    {
        if (hasHitTarget())
        kill();

        for (Player target : getNearbyPlayers(defaultRadius))
        {
            if (target.getUniqueId() == player.getUniqueId())
                continue;

            int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % 10);

            if (usesModifier > rand)
            {
                String prophecy = Ollivanders2API.getProphecies().getProphecy(target.getUniqueId());

                if (prophecy != null)
                {
                    Ollivanders2Common.sendMessageInRadius(prophecy, location, messageRadius);

                    kill();
                    return;
                }
            }

            player.sendMessage(Ollivanders2.chatColor + "You do not discover anything.");

            kill();
            return;
        }
    }
}
