package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.StationarySpellObj;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.stationaryspell.HORCRUX;
import net.pottercraft.ollivanders2.Ollivanders2;

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
      setUsesModifier();
   }

   public void checkEffect ()
   {
      O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
      if (o2p == null)
      {
         kill();
         return;
      }

      move();

      Material targetBlockType = getBlock().getType();
      if (targetBlockType != Material.AIR && targetBlockType != Material.FIRE && targetBlockType != Material.WATER)
      {
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
            kill();
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
      }
   }
}