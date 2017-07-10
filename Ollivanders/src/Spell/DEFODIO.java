package Spell;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import StationarySpell.COLLOPORTUS;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.StationarySpellObj;

/**
 * Mines a line of blocks of length depending on the player's level
 * in this spell.
 *
 * @author lownes
 */
public class DEFODIO extends SpellProjectile implements Spell
{
   int depth;

   public DEFODIO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      depth = (int) usesModifier;
   }

   public void checkEffect ()
   {
      Location newl = location.clone();
      Location forward = newl.add(vector);
      Block block = forward.getBlock();
      List<Block> tempBlocks = p.getTempBlocks();
      if (depth > 0)
      {
         for (StationarySpellObj stat : p.getStationary())
         {
            if (stat instanceof COLLOPORTUS)
            {
               if (stat.isInside(block.getLocation()))
               {
                  return;
               }
            }
         }
         if (block.getType() != Material.BEDROCK && !tempBlocks.contains(block))
         {
            if (block.breakNaturally())
            {
               depth--;
            }
         }
         else
         {
            kill();
         }
      }
      move();
   }
}