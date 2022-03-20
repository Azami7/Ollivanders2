package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.effect.FLYING;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Give the caster the ability to fly.
 * <p>
 * Tom Riddle is the first wizard known to achieve unassisted flying and only one other wizard learned it from him,
 * Severus Snape. Unassisted flying is against magical law.
 * <p>
 * https://harrypotter.fandom.com/wiki/Unsupported_flight
 */
public final class VENTO_FOLIO extends O2Spell
{
    /**
     * The percent chance this spell will succeed each casting.
     */
    protected int successRate = 0;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public VENTO_FOLIO(Ollivanders2 plugin)
    {
        super(plugin);

        branch = O2MagicBranch.DARK_ARTS;
        spellType = O2SpellType.VENTO_FOLIO;

        flavorText = new ArrayList<>()
        {{
            add("\"And then Harry saw him. Voldemort was flying like smoke on the wind, without broomstick or thestral to hold him, his snake-like face gleaming out of the blackness, his white fingers raising his wand again —\"");
            add("\"Remus, he can -\"\n\"Fly, I saw him too, he came after Hagrid and me.\" -Kingsley Shacklebolt and Harry Potter");
        }};

        text = "Vento Folio gives the caster the ability to fly unassisted for an amount of time.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public VENTO_FOLIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.DARK_ARTS;
        spellType = O2SpellType.VENTO_FOLIO;
        initSpell();
    }

    /**
     * Set the success rate based on the caster's skill.
     */
    @Override
    void doInitSpell()
    {
        // set success rate based on their experience
        if (usesModifier >= 200)
            successRate = 100;
        else if (usesModifier >= 100)
            successRate = (int) usesModifier / 2;
        else if (usesModifier >= 50)
            successRate = 25;
        else if (usesModifier >= 25)
            successRate = 10;
        else
            successRate = 5;
    }

    /**
     * Override checkEffect since this spell should not projectile. Add flying effect to caster.
     */
    @Override
    public void checkEffect()
    {
        if (!isSpellAllowed())
        {
            kill();
            return;
        }

        int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);
        int duration;

        if (usesModifier >= 100)
            // > 30 seconds
            duration = (int) usesModifier + 300;
        else if (usesModifier >= 50)
            // 30 seconds
            duration = 300;
        else if (usesModifier >= 10)
            // 10 seconds
            duration = 200;
        else // < 10
            // 5 seconds
            duration = 100;

        if (rand < successRate)
        {
            FLYING effect = new FLYING(p, duration, player.getUniqueId());
            Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

            common.printDebugMessage("VENTO_FOLIO: Adding effect ", null, null, false);
        }

        kill();
    }

    @Override
    protected void doCheckEffect()
    {
    }
}