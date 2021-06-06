package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.ollivanders2.*;
import net.pottercraft.ollivanders2.stationaryspell.StationarySpellObj;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
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
public final class HARMONIA_NECTERE_PASSUS extends O2Spell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public HARMONIA_NECTERE_PASSUS()
   {
      super();

      spellType = O2SpellType.HARMONIA_NECTERE_PASSUS;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("He stares at the monolith before him, lifts his wand and begins to chant eerily. The surface of the cabinet glimmers, atremble in the ambient light. Almost alive. Then he stops. Looking back, his eyes haunted, he slips away. Light plays within the cabinet. Movement. Shadows flicker within, coalesce.");
         add("\" ...we forced him head-first into that Vanishing Cabinet on the first floor.\"\n" +
               "\"But you'll get into terrible trouble!\"\n" +
               "\"Not until Montague reappears, and that could take weeks, I dunno where we sent him...\" -Fred Weasley and Hermione Granger");
      }};

      text = "Harmonia Nectere Passus will create a pair of vanishing cabinets if the cabinets on both ends are configured correctly.";
   }

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

      spellType = O2SpellType.HARMONIA_NECTERE_PASSUS;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
         worldGuardFlags.add(DefaultFlag.BUILD);
   }

   @Override
   protected void doCheckEffect ()
   {
      if (!hasHitTarget())
         return;

      Block fromBlock = getTargetBlock();
      Material blockType = fromBlock.getType();

      if (blockType == Material.WALL_SIGN || blockType == Material.SIGN)
      {
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
         if (toBlockType != Material.WALL_SIGN && toBlockType != Material.SIGN)
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

         for (StationarySpellObj statSpell : Ollivanders2API.getStationarySpells().getActiveStationarySpells())
         {
            if (statSpell instanceof net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS)
            {
               net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS harmonia
                     = (net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS) statSpell;
               if (harmonia.getBlock().equals(fromLoc.getBlock()) || harmonia.getBlock().equals(toLoc.getBlock()))
               {
                  return;
               }
            }
         }
         net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS harmoniaFrom = new net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS(p, player.getUniqueId(), fromLoc, O2StationarySpellType.HARMONIA_NECTERE_PASSUS, 1, 10, toLoc);
         net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS harmoniaTo = new net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS(p, player.getUniqueId(), toLoc, O2StationarySpellType.HARMONIA_NECTERE_PASSUS, 1, 10, fromLoc);

         harmoniaFrom.flair(20);
         harmoniaTo.flair(20);

         Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaFrom);
         Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaTo);
      }

      kill();
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

         location = new Location(world, x, y, z);
      }

      return location;
   }
}