package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Creates a line of glowstone that goes away after a time.
 *
 * @author lownes
 * @author Azami7
 */
public final class LUMOS_DUO extends Charms
{
   private List<Block> line = new ArrayList();

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LUMOS_DUO ()
   {
      super();

      flavorText.add("A variation of the Wand-Lighting Charm.");
      text = "Creates a stream of flowstone to light your way.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
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