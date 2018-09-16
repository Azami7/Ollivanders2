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

import java.util.ArrayList;

/**
 * Creates a pair of vanishing cabinets and teleports between them.
 *
 * @author lownes
 */
public final class HARMONIA_NECTERE_PASSUS extends Charms
{
   public O2SpellType spellType = O2SpellType.HARMONIA_NECTERE_PASSUS;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("He stares at the monolith before him, lifts his wand and begins to chant eerily. The surface of the cabinet glimmers, atremble in the ambient light. Almost alive. Then he stops. Looking back, his eyes haunted, he slips away. Light plays within the cabinet. Movement. Shadows flicker within, coalesce.");
      add("\" ...we forced him head-first into that Vanishing Cabinet on the first floor.\"\n" +
            "\"But you'll get into terrible trouble!\"\n" +
            "\"Not until Montague reappears, and that could take weeks, I dunno where we sent him...\" -Fred Weasley and Hermione Granger");
   }};

   protected String text = "Harmonia Nectere Passus will create a pair of vanishing cabinets if the cabinets on both ends are configured correctly.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public HARMONIA_NECTERE_PASSUS () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public HARMONIA_NECTERE_PASSUS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      Material blockType = getBlock().getType();
      if (Ollivanders2.debug)
         p.getLogger().info("Block is " + blockType.toString());

      if (blockType == Material.WALL_SIGN || blockType == Material.SIGN_POST || blockType == Material.SIGN)
      {
         if (Ollivanders2.debug)
            p.getLogger().info("detected sign object");
         kill();

         Block fromBlock = getBlock();
         // determine the location of the other vanishing cabinet
         Location toLoc = getSignLocation(fromBlock);
         if (toLoc == null)
         {
            if (Ollivanders2.debug)
               p.getLogger().info("Unable to get toLoc from sign.");

            return;
         }

         Block toBlock = toLoc.getBlock();
         Material toBlockType = toBlock.getType();
         if (toBlockType != Material.WALL_SIGN && toBlockType != Material.SIGN_POST && toBlockType != Material.SIGN)
         {
            if (Ollivanders2.debug)
               p.getLogger().info("Block at toLoc is not a sign block");

            return;
         }

         Location fromLoc = getSignLocation(toBlock);
         if (fromLoc == null)
         {
            if (Ollivanders2.debug)
               p.getLogger().info("Unable to get fromLoc from sign.");

            return;
         }

         for (StationarySpellObj statSpell : p.stationarySpells.getActiveStationarySpells())
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
         net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS harmoniaFrom = new net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS(p, player, fromLoc, StationarySpells.HARMONIA_NECTERE_PASSUS, 1, 10, toLoc);
         net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS harmoniaTo = new net.pottercraft.Ollivanders2.StationarySpell.HARMONIA_NECTERE_PASSUS(p, player, toLoc, StationarySpells.HARMONIA_NECTERE_PASSUS, 1, 10, fromLoc);

         harmoniaFrom.flair(20);
         harmoniaTo.flair(20);

         p.stationarySpells.addStationarySpell(harmoniaFrom);
         p.stationarySpells.addStationarySpell(harmoniaTo);
      }
   }

   /**
    * Get the world from the sign block.
    *
    * @param block the sign to check
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

         int x;
         int y;
         int z;

         try
         {
            x = Integer.parseInt(lines[1]);
            y = Integer.parseInt(lines[2]);
            z = Integer.parseInt(lines[3]);
         }
         catch (NumberFormatException e)
         {
            if (Ollivanders2.debug)
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