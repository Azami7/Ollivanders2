package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Registers a new floo network entry
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class ALIQUAM_FLOO extends O2Spell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public ALIQUAM_FLOO(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.ALIQUAM_FLOO;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("\"In use for centuries, the Floo Network, while somewhat uncomfortable, has many advantages. Firstly, unlike broomsticks, the Network can be used without fear of breaking the International Statute of Secrecy. Secondly, unlike Apparition, there is little to no danger of serious injury. Thirdly, it can be used to transport children, the elderly and the infirm.\"");
      }};

      text = "Aliquam Floo will register a fireplace with the Floo Network. "
              + "Place a sign above a fire with the unique name of the fireplace and cast this spell at the fire. "
              + "Once your fireplace is registered, you can destroy the sign and even put out the fire, but you must not "
              + "place a solid block where the fire was, or you will have to re-register your fireplace. "
              + "People can use your fireplace via Floo powder. "
              + "Toss the powder into a registered fireplace, walk into the fire, and say the name of your destination.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public ALIQUAM_FLOO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.ALIQUAM_FLOO;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      // required worldGuard state flags
      if (Ollivanders2.worldGuardEnabled)
      {
         worldGuardFlags.add(Flags.INTERACT);
         worldGuardFlags.add(Flags.BUILD);
      }

      // pass-through materials
      projectilePassThrough.remove(Material.WATER);
      projectilePassThrough.remove(Material.FIRE);
   }

   /**
    * Creates an aliquam floo stationary spell at this location if it is a fire with a sign over it.
    */
   @Override
   protected void doCheckEffect()
   {
      if (!hasHitTarget())
         return;

      Block target = getTargetBlock();

      if (target != null && target.getType() == Material.FIRE)
      {
         Location statLocation = new Location(location.getWorld(), target.getX() + 0.5, target.getY() + 0.125, target.getZ() + 0.5);

         // find the sign above the fire
         if (Ollivanders2Common.wallSigns.contains(target.getRelative(BlockFace.UP).getType()))
         {
            Sign sign = (Sign) target.getRelative(BlockFace.UP).getState();
            String flooName = sign.getLine(0).trim() + " " + sign.getLine(1).trim() + " " + sign.getLine(2).trim() + " " + sign.getLine(3).trim();
            flooName = flooName.trim();
            flooName = flooName.toLowerCase();
            common.printDebugMessage("Floo name on sign is " + flooName, null, null, false);

            // make sure there is not already an aliquam floo spell at this block
            for (O2StationarySpell stat : Ollivanders2API.getStationarySpells(p).getActiveStationarySpells())
            {
               if (stat instanceof net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO)
               {
                  net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO ali = (net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO) stat;
                  if (ali.getFlooName().equals(flooName) || ali.getBlock().equals(statLocation.getBlock()))
                  {
                     kill();

                     player.sendMessage(Ollivanders2.chatColor + "There is already a fireplace registered with the name " + flooName + ".");
                     return;
                  }
               }
            }

            net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO aliquam = new net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO(p, player.getUniqueId(), statLocation, O2StationarySpellType.ALIQUAM_FLOO, 2, 10, flooName);
            aliquam.flair(20);
            Ollivanders2API.getStationarySpells(p).addStationarySpell(aliquam);
         }
      }
      else
      {
         if (Ollivanders2.debug)
         {
            if (target != null)
            {
               common.printDebugMessage("target block was " + target.getType().toString(), null, null, false);
            }
            else
            {
               common.printDebugMessage("target block was null", null, null, false);
            }
         }
      }

      kill();
   }
}
