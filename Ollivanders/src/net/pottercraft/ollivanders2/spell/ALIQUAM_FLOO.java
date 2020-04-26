package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.stationaryspell.StationarySpellObj;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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

      spellType = O2SpellType.ALIQUAM_FLOO;

      flavorText = new ArrayList<String>() {{
         add("\"In use for centuries, the Floo Network, while somewhat uncomfortable, has many advantages. Firstly, unlike broomsticks, the Network can be used without fear of breaking the International Statute of Secrecy. Secondly, unlike Apparition, there is little to no danger of serious injury. Thirdly, it can be used to transport children, the elderly and the infirm.\"");
      }};

      text = "Aliquam Floo will register a fireplace with the Floo Network. "
            + "Place a sign above a fire with the unique name of the fireplace and cast this spell at the fire. "
            + "Once your fireplace is registered, you can destroy the sign and even put out the fire, but you must not "
            + "place a solid block where the fire was, or you will have to re-register your fireplace. "
            + "People can use your fireplace via Floo powder, which is made by smelting ender pearl. "
            + "Toss the powder into a registered fireplace, walk into the fire, and say the name of your destination.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public ALIQUAM_FLOO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.ALIQUAM_FLOO;
      setUsesModifier();
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
            for (StationarySpellObj stat : Ollivanders2API.getStationarySpells().getActiveStationarySpells())
            {
               if (stat instanceof net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO)
               {
                  net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO ali = (net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO) stat;
                  if (ali.getFlooName().equals(flooName) || ali.getBlock().equals(statLocation.getBlock()))
                  {
                     return;
                  }
               }
            }
            net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO aliquam = new net.pottercraft.ollivanders2.stationaryspell.ALIQUAM_FLOO(p, player.getUniqueId(), statLocation, O2StationarySpellType.ALIQUAM_FLOO, 2, 10, flooName);
            aliquam.flair(20);
            Ollivanders2API.getStationarySpells().addStationarySpell(aliquam);
         }
         kill();
      }
   }
}