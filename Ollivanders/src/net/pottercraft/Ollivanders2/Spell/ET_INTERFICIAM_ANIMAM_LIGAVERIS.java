package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpells;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

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

      flavorText.add("Tamper with the deepest mysteries — the source of life, the essence of self — only if prepared for consequences of the most extreme and dangerous kind.");
      text = "The most horrifying and destructive act man can do is the creation of a Horcrux. Through splitting one's soul through the murder of another player, one is able to resurrect with all of their magical experience intact. "
            + "However, this action has a terrible cost, for as long as the soul is split, the player's maximum health is halved for each Horcrux they have made. "
            + "The only known way of destroying a Horcrux is with Fiendfyre.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public ET_INTERFICIAM_ANIMAM_LIGAVERIS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType()
            != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER)
      {
         double futureHealth = ((Damageable) player).getHealth();
         if (futureHealth > ((Attributable) player).getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getBaseValue() / 2.0)
         {
            futureHealth = ((Attributable) player).getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getBaseValue() / 2.0;
         }
         int souls = p.getO2Player(player).getSouls();
         //If the player's soul is split enough and they can survive
         //making another horcrux, then make a new one and damage them
         if (futureHealth - 1 > 0 && souls > 0)
         {
            HORCRUX horcrux = new HORCRUX(player, location, StationarySpells.HORCRUX, 5, 10);
            horcrux.flair(10);
            p.addStationary(horcrux);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(
                  player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() / 2.0);
            p.getO2Player(player).subtractSoul();
            player.damage(1.0);
            kill();
         }
         else
         {
            if (souls == 0)
            {
               player.sendMessage(ChatColor.getByChar(p.getConfig().getString("chatColor"))
                     + "Your soul is not yet so damaged to allow this.");
               return;
            }
            //If they player couldn't survive making another horcrux
            //then they are sent back to a previous horcrux
            else if ((futureHealth - 1) <= 0)
            {
               List<StationarySpellObj> stationarys = p.getStationary();
               for (StationarySpellObj stationary : stationarys)
               {
                  if (stationary.name == StationarySpells.HORCRUX && stationary.getPlayerUUID().equals(player.getUniqueId()))
                  {
                     Location tp = stationary.location.toLocation();
                     tp.setY(tp.getY() + 1);
                     player.teleport(tp);
                     player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
                     p.getO2Player(player).resetEffects();
                     p.remStationary(stationary);
                     return;
                  }
               }
               //If the player doesn't have any horcruxes left
               //then they are killed.
               player.damage(1000.0);
            }
         }
      }
   }
}