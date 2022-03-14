package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Shoots caster high into air.
 * <p>
 * Referece: https://harrypotter.fandom.com/wiki/Ascendio
 */
public final class ASCENDIO extends O2Spell
{
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ASCENDIO(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.ASCENDIO;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>()
        {{
            add("The Climbing Charm");
            add("Underwater he casts a spell which propels him towards the surface, he flies out and lands on the decking where the crowd are.");
        }};

        text = "Propels the caster into the air.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ASCENDIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        spellType = O2SpellType.ASCENDIO;
        branch = O2MagicBranch.CHARMS;

        initSpell();
    }

    /**
     * Propel the caster into the air
     */
    @Override
    public void checkEffect()
    {
        kill();

        if (!isSpellAllowed()) return;

        double up = usesModifier * 0.25;
        if (up > 2) up = 2;

        Vector vec = new Vector(0, up, 0);
        player.setVelocity(player.getVelocity().add(vec));
    }

    /**
     * Nothing to do since we overrode checkEffect() itself
     */
    @Override
    protected void doCheckEffect()
    {
    }
}