package Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Turns all blocks in a radius into fallingBlock entities
 *
 * @author lownes
 */
public class DEPRIMO extends SpellProjectile implements Spell
{

   public DEPRIMO (Ollivanders2 plugin, Player player, Spells name,
                   Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @SuppressWarnings("deprecation")
   public void checkEffect ()
   {
      move();
      Block center = getBlock();
      double radius = usesModifier / 2;
      List<Block> tempBlocks = p.getTempBlocks();
      if (center.getType() != Material.AIR)
      {
         for (Block block : getBlocksInRadius(location, radius))
         {
            if (!tempBlocks.contains(block) &&
                  block.getType() != Material.WATER && block.getType() != Material.LAVA &&
                  block.getType() != Material.STATIONARY_WATER && block.getType() != Material.STATIONARY_LAVA &&
                  block.getType() != Material.AIR && block.getType() != Material.BEDROCK && block.getType().isSolid())
            {
               Material type = block.getType();
               byte data = block.getData();
               block.setType(Material.AIR);
               block.getWorld().spawnFallingBlock(block.getLocation(), type, data);
            }
         }
         kill();
      }
   }

}