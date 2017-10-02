package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.*;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpells;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * Registers a new floo network entry
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class ALIQUAM_FLOO extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ALIQUAM_FLOO ()
   {
      super();

      text = "Aliquam Floo will register a fireplace with the Floo Network. "
            + "Place a sign above a fire with the unique name of the fireplace and cast this spell at the fire. "
            + "Once your fireplace is registered, you can destroy the sign and even put out the fire, but you must not "
            + "place a solid block where the fire was, or you will have to re-register your fireplace. "
            + "People can use your fireplace via Floo powder, which is made by smelting ender pearl. "
            + "Toss the powder into a registered fireplace, walk into the fire, and say the name of your destination.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public ALIQUAM_FLOO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      if (super.getBlock().getType() == Material.FIRE)
      {
         Location statLocation = new Location(location.getWorld(), super.getBlock().getX() + 0.5, super.getBlock().getY() + 0.125, super.getBlock().getZ() + 0.5);
         if (super.getBlock().getRelative(BlockFace.UP).getType() == Material.WALL_SIGN)
         {
            Sign sign = (Sign) super.getBlock().getRelative(BlockFace.UP).getState();
            String flooName = sign.getLine(0).trim() + " " + sign.getLine(1).trim() + " " + sign.getLine(2).trim() + " " + sign.getLine(3).trim();
            flooName = flooName.trim();
            flooName = flooName.toLowerCase();
            for (StationarySpellObj stat : p.getStationary())
            {
               if (stat instanceof net.pottercraft.Ollivanders2.StationarySpell.ALIQUAM_FLOO)
               {
                  net.pottercraft.Ollivanders2.StationarySpell.ALIQUAM_FLOO ali = (net.pottercraft.Ollivanders2.StationarySpell.ALIQUAM_FLOO) stat;
                  if (ali.getFlooName().equals(flooName) || ali.getBlock().equals(statLocation.getBlock()))
                  {
                     return;
                  }
               }
            }
            net.pottercraft.Ollivanders2.StationarySpell.ALIQUAM_FLOO aliquam = new net.pottercraft.Ollivanders2.StationarySpell.ALIQUAM_FLOO(player, statLocation, StationarySpells.ALIQUAM_FLOO, 2, 10, flooName);
            aliquam.flair(20);
            p.addStationary(aliquam);
         }
         kill();
      }
   }
}
