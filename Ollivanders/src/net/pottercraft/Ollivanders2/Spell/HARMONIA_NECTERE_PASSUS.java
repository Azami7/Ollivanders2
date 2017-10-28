package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.*;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpells;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * Creates a pair of vanishing cabinets and teleports between them.
 *
 * @author lownes
 */
public final class HARMONIA_NECTERE_PASSUS extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public HARMONIA_NECTERE_PASSUS ()
   {
      super();

      flavorText.add("He stares at the monolith before him, lifts his wand and begins to chant eerily. The surface of the cabinet glimmers, atremble in the ambient light. Almost alive. Then he stops. Looking back, his eyes haunted, he slips away. Light plays within the cabinet. Movement. Shadows flicker within, coalesce.");
      flavorText.add("\" ...we forced him head-first into that Vanishing Cabinet on the first floor.\"\n" +
            "\"But you'll get into terrible trouble!\"\n" +
            "\"Not until Montague reappears, and that could take weeks, I dunno where we sent him...\" -Fred Weasley and Hermione Granger");
      text = "Harmonia Nectere Passus will create a pair of vanishing cabinets if the cabinets on both ends are configured correctly.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public HARMONIA_NECTERE_PASSUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      Material blockType = getBlock().getType();
      p.getLogger().info("Block is " + blockType.toString());
      if (blockType == Material.WALL_SIGN || blockType == Material.SIGN_POST || blockType == Material.SIGN)
      {
         p.getLogger().info("detected sign object");
         kill();

         Block fromBlock = getBlock();
         // determine the location of the other vanishing cabinet
         Location toLoc = getSignLocation(fromBlock);
         if (toLoc == null)
         {
            if (p.debug)
               p.getLogger().info("Unable to get toLoc from sign.");

            return;
         }

         Block toBlock = toLoc.getBlock();
         Material toBlockType = toBlock.getType();
         if (toBlockType != Material.WALL_SIGN && toBlockType != Material.SIGN_POST && toBlockType != Material.SIGN)
         {
            if (p.debug)
               p.getLogger().info("Block at toLoc is not a sign block");

            return;
         }

         Location fromLoc = getSignLocation(toBlock);
         if (fromLoc == null)
         {
            if (p.debug)
               p.getLogger().info("Unable to get fromLoc from sign.");

            return;
         }

         for (StationarySpellObj statSpell : p.getStationary())
         {
            if (statSpell instanceof net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS)
            {
               net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS harmonia
                     = (net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS) statSpell;
               if (harmonia.getBlock().equals(fromLoc.getBlock()) || harmonia.getBlock().equals(toLoc.getBlock()))
               {
                  return;
               }
            }
         }
         net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS harmoniaFrom = new net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS(player, fromLoc, StationarySpells.HARMONIA_NECTERE_PASSUS, 1, 10, toLoc);
         net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS harmoniaTo = new net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS(player, toLoc, StationarySpells.HARMONIA_NECTERE_PASSUS, 1, 10, fromLoc);

         harmoniaFrom.flair(20);
         harmoniaTo.flair(20);

         p.addStationary(harmoniaFrom);
         p.addStationary(harmoniaTo);
      }
   }

   /**
    * Get the world from the sign block.
    *
    * @param block
    * @return the location or null if one could not be created
    */
   private Location getSignLocation (Block block)
   {
      Location location = null;

      Sign sign = (Sign)block.getState();
      String[] lines = sign.getLines();

      if (lines.length == 4)
      {
         World world = Bukkit.getWorld(lines[0]);
         if (world == null)
         {
            return null;
         }

         int x = 0;
         int y = 0;
         int z = 0;
         try
         {
            x = Integer.parseInt(lines[1]);
            y = Integer.parseInt(lines[2]);
            z = Integer.parseInt(lines[3]);
         }
         catch (NumberFormatException e)
         {
            if (p.debug)
            {
               p.getLogger().warning("Unable to parse coordinates from sign.");
            }
            return null;
         }

         location = new Location(world, (double)x, (double)y, (double)z);
      }

      return location;
   }
}