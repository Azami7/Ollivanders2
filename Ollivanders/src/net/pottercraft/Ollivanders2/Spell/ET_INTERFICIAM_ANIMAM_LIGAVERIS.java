package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Player.O2Player;
import net.pottercraft.Ollivanders2.StationarySpell.HORCRUX;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Creates a horcrux stationary spell object where it collides with a block.
 * Also damages the player and increases their souls count.
 *
 * @author lownes
 * @author Azami7
 */
public final class ET_INTERFICIAM_ANIMAM_LIGAVERIS extends DarkArts
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ET_INTERFICIAM_ANIMAM_LIGAVERIS ()
   {
      super();

      spellType = O2SpellType.ET_INTERFICIAM_ANIMAM_LIGAVERIS;

      flavorText = new ArrayList<String>() {{
         add("Tamper with the deepest mysteries — the source of life, the essence of self — only if prepared for consequences of the most extreme and dangerous kind.");
      }};

      text = "The most horrifying and destructive act man can do is the creation of a Horcrux. Through splitting one's soul through the murder of another player, one is able to resurrect with all of their magical experience intact. "
            + "However, this action has a terrible cost, for as long as the soul is split, the player's maximum health is halved for each Horcrux they have made. "
            + "The only known way of destroying a Horcrux is with Fiendfyre.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public ET_INTERFICIAM_ANIMAM_LIGAVERIS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.ET_INTERFICIAM_ANIMAM_LIGAVERIS;


      initSpell();

      // world guard flags
      worldGuardFlags.add(DefaultFlag.MOB_SPAWNING); // needed because Fiendfyre requires it, otherwise horcruxes could get made in locations players couldn't kill them
   }

   @Override
   public void checkEffect ()
   {
      if (!checkSpellAllowed())
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
      if (futureHealth > player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getBaseValue() / 2.0)
      {
         futureHealth = player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getBaseValue() / 2.0;
      }

      int souls = o2p.getSouls();

      //If the player's soul is split enough and they can survive making another horcrux, then make a new one and damage them
      if (futureHealth - 1 > 0 && souls > 0)
      {
         HORCRUX horcrux = new HORCRUX(p, player.getUniqueId(), location, O2StationarySpellType.HORCRUX, 5, 10);
         horcrux.flair(10);
         Ollivanders2API.getStationarySpells().addStationarySpell(horcrux);
         player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(
               player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() / 2.0);
         o2p.subtractSoul();
         player.damage(1.0);
      }
      else
      {
         if (souls == 0)
         {
            player.sendMessage(Ollivanders2.chatColor + "Your soul is not yet so damaged to allow this.");
            return;
         }

         //If they player couldn't survive making another horcrux then they are sent back to a previous horcrux
         else if ((futureHealth - 1) <= 0)
         {
            List<StationarySpellObj> stationaries = Ollivanders2API.getStationarySpells().getActiveStationarySpells();
            for (StationarySpellObj stationary : stationaries)
            {
               if (stationary.getSpellType() == O2StationarySpellType.HORCRUX && stationary.getCasterID().equals(player.getUniqueId()))
               {
                  Location tp = stationary.location;
                  tp.setY(tp.getY() + 1);
                  player.teleport(tp);
                  player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());

                  Ollivanders2API.getStationarySpells().removeStationarySpell(stationary);
                  return;
               }
            }

            //If the player doesn't have any horcruxes left then they are killed.
            player.damage(1000.0);
         }
      }

      kill();
   }
}