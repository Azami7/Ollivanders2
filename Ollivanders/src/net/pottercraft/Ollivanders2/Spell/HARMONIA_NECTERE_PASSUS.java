package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.*;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpells;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * Creates a pair of vanishing cabinets and teleports between them.
 *
 * @author lownes
 */
public final class HARMONIA_NECTERE_PASSUS extends Charms
{
   public HARMONIA_NECTERE_PASSUS ()
   {
      super();

      flavorText.add("He stares at the monolith before him, lifts his wand and begins to chant eerily. The surface of the cabinet glimmers, atremble in the ambient light. Almost alive. Then he stops. Looking back, his eyes haunted, he slips away. Light plays within the cabinet. Movement. Shadows flicker within, coalesce.");
      flavorText.add("\" ...we forced him head-first into that Vanishing Cabinet on the first floor.\"\n" +
            "\"But you'll get into terrible trouble!\"\n" +
            "\"Not until Montague reappears, and that could take weeks, I dunno where we sent him...\" -Fred Weasley and Hermione Granger");
      text = "Harmonia Nectere Passus will create a pair of vanishing cabinets if the cabinets on both ends are configured correctly.";
   }

   public HARMONIA_NECTERE_PASSUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      if (getBlock().getType() == Material.WALL_SIGN || getBlock().getType() == Material.SIGN_POST)
      {
         kill();
         Sign sign1 = (Sign) getBlock().getState();
         String[] lines1 = sign1.getLines();
         if (lines1.length == 4)
         {
            World toWorld = Bukkit.getWorld(lines1[0]);
            if (toWorld == null)
            {
               return;
            }
            int toX;
            int toY;
            int toZ;
            try
            {
               toX = Integer.parseInt(lines1[1]);
               toY = Integer.parseInt(lines1[2]);
               toZ = Integer.parseInt(lines1[3]);
            } catch (NumberFormatException e)
            {
               return;
            }
            Location fromLoc = new Location(getBlock().getWorld(), getBlock().getX() + 0.5, getBlock().getY() + 0.125, getBlock().getZ() + 0.5);
            Location toLoc = new Location(toWorld, toX + 0.5, toY + 0.125, toZ + 0.5);
            if (toLoc.getBlock().getType() != Material.WALL_SIGN && toLoc.getBlock().getType() != Material.SIGN_POST)
            {
               return;
            }
            Sign sign2 = (Sign) toLoc.getBlock().getState();
            String[] lines2 = sign2.getLines();
            if (lines2.length == 4)
            {
               World fromWorld = Bukkit.getWorld(lines2[0]);
               int fromX;
               int fromY;
               int fromZ;
               try
               {
                  fromX = Integer.parseInt(lines2[1]);
                  fromY = Integer.parseInt(lines2[2]);
                  fromZ = Integer.parseInt(lines2[3]);
               } catch (NumberFormatException e)
               {
                  return;
               }
               if (!fromWorld.equals(getBlock().getWorld()) || fromX != getBlock().getX() || fromY != getBlock().getY() || fromZ != getBlock().getZ())
               {
                  return;
               }
               for (StationarySpellObj stat : p.getStationary())
               {
                  if (stat instanceof net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS)
                  {
                     net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS harm = (net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS) stat;
                     if (harm.getBlock().equals(fromLoc.getBlock()) || harm.getBlock().equals(toLoc.getBlock()))
                     {
                        return;
                     }
                  }
               }
               net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS harmFrom = new net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS(player, fromLoc, StationarySpells.HARMONIA_NECTERE_PASSUS, 1, 10, toLoc);
               net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS harmTo = new net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS(player, toLoc, StationarySpells.HARMONIA_NECTERE_PASSUS, 1, 10, fromLoc);
               harmFrom.flair(20);
               harmTo.flair(20);
               p.addStationary(harmFrom);
               p.addStationary(harmTo);
            }
         }
      }
   }
}