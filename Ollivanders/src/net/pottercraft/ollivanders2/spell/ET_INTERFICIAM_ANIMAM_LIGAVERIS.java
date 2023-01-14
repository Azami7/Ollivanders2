package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.stationaryspell.HORCRUX;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Creates a horcrux stationary spell object where it collides with a block.
 * Also damages the player and increases their souls count.
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Horcrux-making_spell
 */
public final class ET_INTERFICIAM_ANIMAM_LIGAVERIS extends O2Spell
{
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ET_INTERFICIAM_ANIMAM_LIGAVERIS(Ollivanders2 plugin)
    {
        super(plugin);

        spellType = O2SpellType.ET_INTERFICIAM_ANIMAM_LIGAVERIS;
        branch = O2MagicBranch.DARK_ARTS;

        flavorText = new ArrayList<>()
        {{
            add("Tamper with the deepest mysteries — the source of life, the essence of self — only if prepared for consequences of the most extreme and dangerous kind.");
        }};

        text = "The most horrifying and destructive act man can do is the creation of a Horcrux. Through splitting one's soul through the murder of another player, one is able to resurrect with all of their magical experience intact. "
                + "However, this action has a terrible cost, for as long as the soul is split, the player's maximum health is halved for each Horcrux they have made. "
                + "The only known way of destroying a Horcrux is with Fiendfyre.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ET_INTERFICIAM_ANIMAM_LIGAVERIS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
    {
        super(plugin, player, rightWand);
        spellType = O2SpellType.ET_INTERFICIAM_ANIMAM_LIGAVERIS;
        branch = O2MagicBranch.DARK_ARTS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.MOB_SPAWNING); // needed because Fiendfyre requires it, otherwise horcruxes could get made in locations players couldn't kill them

        initSpell();
    }

    /**
     * This spell is not a projectile but makes the stationary spell in the player's current location.
     */
    @Override
    public void checkEffect()
    {
        if (!isSpellAllowed())
        {
            kill();
            return;
        }

        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        if (o2p == null)
        {
            kill();
            return;
        }

        double futureHealth = player.getHealth();
        AttributeInstance healthAttribute = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute == null)
        {
            common.printDebugMessage("ET_INTERFICIAM_ANIMAM_LIGAVERIS.checkEffect: player health attribute is null", null, null, false);
            kill();
            return;
        }

        if (futureHealth > healthAttribute.getBaseValue() / 2.0)
            futureHealth = healthAttribute.getBaseValue() / 2.0;

        int souls = o2p.getSouls();

        if (souls == 0)
        {
            player.sendMessage(Ollivanders2.chatColor + "Your soul is not yet so damaged to allow this.");
            kill();
            return;
        }

        //If the player's soul isn't s0 split that they can survive making another horcrux, then make a new one and damage them
        if (futureHealth - 1 > 0)
        {
            HORCRUX horcrux = new HORCRUX(p, player.getUniqueId(), location);
            horcrux.flair(10);
            Ollivanders2API.getStationarySpells().addStationarySpell(horcrux);
            healthAttribute.setBaseValue(healthAttribute.getBaseValue() / 2.0);
            o2p.subtractSoul();
            player.damage(1.0);
        }
        else
        {
            //If they player couldn't survive making another horcrux then they are sent back to a previous horcrux
            List<O2StationarySpell> stationaries = Ollivanders2API.getStationarySpells().getActiveStationarySpells();
            for (O2StationarySpell stationary : stationaries)
            {
                if (stationary.getSpellType() == O2StationarySpellType.HORCRUX && stationary.getCasterID().equals(player.getUniqueId()))
                {
                    Location tp = stationary.getLocation();
                    tp.setY(tp.getY() + 1);
                    player.teleport(tp);
                    player.setHealth(healthAttribute.getBaseValue());

                    Ollivanders2API.getStationarySpells().removeStationarySpell(stationary);
                    return;
                }
            }

            //If the player doesn't have any horcruxes left then they are killed.
            player.damage(1000.0);
        }

        kill();
    }

    @Override
    protected void doCheckEffect()
    {
    }
}