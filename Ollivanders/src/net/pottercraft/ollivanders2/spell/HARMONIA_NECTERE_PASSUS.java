package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.*;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public HARMONIA_NECTERE_PASSUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.HARMONIA_NECTERE_PASSUS;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
         worldGuardFlags.add(Flags.BUILD);
   }

   /**
    * Specific logic for targeting the vanishing cabinet sign
    */
   @Override
   protected void doCheckEffect()
   {
      if (!hasHitTarget())
         return;

      kill();

      Block fromBlock = getTargetBlock();
      if (fromBlock == null)
      {
         common.printDebugMessage("HARMONIA_NECTERE_PASSUS.doCheckEffect: from block is null", null, null, true);
         return;
      }

      Material blockType = fromBlock.getType();
      if (!Ollivanders2Common.signs.contains(blockType))
      {
         common.printDebugMessage("Block is not a sign", null, null, false);
         return;
      }

      // determine the location of the other vanishing cabinet
      Location toLoc = getSignLocation(fromBlock);
      if (toLoc == null)
      {
         common.printDebugMessage("Unable to get toLoc from sign.", null, null, false);
         return;
      }

      Block toBlock = toLoc.getBlock();
      Material toBlockType = toBlock.getType();
      if (!Ollivanders2Common.signs.contains(toBlockType))
      {
         common.printDebugMessage("Block at toLoc is not a sign block", null, null, false);
         return;
      }

      Location fromLoc = getSignLocation(toBlock);
      if (fromLoc == null)
      {
         common.printDebugMessage("Unable to get fromLoc from sign.", null, null, false);
         return;
      }

      if (common.locationEquals(toLoc, fromLoc))
      {
         common.printDebugMessage("Vanishing cabinet to and from locations are the same", null, null, false);
         player.sendMessage(Ollivanders2.chatColor + "To and from locations on vanishing cabinet signs are the same.");
         return;
      }

      for (O2StationarySpell statSpell : Ollivanders2API.getStationarySpells(p).getActiveStationarySpells())
      {
         if (statSpell instanceof net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS)
         {
            net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS harmonia = (net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS) statSpell;
            if (harmonia.getBlock().equals(fromLoc.getBlock()) || harmonia.getBlock().equals(toLoc.getBlock()))
            {
               player.sendMessage(Ollivanders2.chatColor + "There is already a vanishing cabinet here.");
               return;
            }
         }
      }

      net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS harmoniaFrom = new net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS(p, player.getUniqueId(), fromLoc, O2StationarySpellType.HARMONIA_NECTERE_PASSUS, 1, 10, toLoc);
      net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS harmoniaTo = new net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS(p, player.getUniqueId(), toLoc, O2StationarySpellType.HARMONIA_NECTERE_PASSUS, 1, 10, fromLoc);

      harmoniaFrom.flair(20);
      harmoniaTo.flair(20);

      Ollivanders2API.getStationarySpells(p).addStationarySpell(harmoniaFrom);
      Ollivanders2API.getStationarySpells(p).addStationarySpell(harmoniaTo);
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
            common.printDebugMessage("Unable to parse coordinates from sign.", null, null, false);
            return null;
         }

         location = new Location(world, x, y, z);
      }

      return location;
   }
}