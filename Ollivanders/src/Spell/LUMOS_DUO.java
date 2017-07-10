package Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Creates a line of glowstone that goes away after a time.
 *
 * @author lownes
 */
public class LUMOS_DUO extends SpellProjectile implements Spell
{
   List<Block> line = new ArrayList<Block>();

   public LUMOS_DUO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      lifeTicks = (int) (-(usesModifier * 20));
   }

   public void checkEffect ()
   {
      move();
      if (getBlock().getType() != Material.AIR)
      {
         kill = false;
      }
      else
      {
         location.getBlock().setType(Material.GLOWSTONE);
         p.getTempBlocks().add(getBlock());
         line.add(getBlock());
      }
      if (lifeTicks >= 159)
      {
         for (Block block : line)
         {
            if (block.getType() == Material.GLOWSTONE)
            {
               block.setType(Material.AIR);
            }
            p.getTempBlocks().remove(block);
         }
         kill = true;
      }
   }
}