package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Spell which places a block of water against the targeted block.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class AGUAMENTI extends Charms
{
   boolean move;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public AGUAMENTI ()
   {
      super();

      flavorText.add("The Water-Making Spell conjures clean, drinkable water from the end of the wand.");
      flavorText.add("The Water-Making Spell");

      text = "Aguamenti will cause water to erupt against the surface you cast it on.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public AGUAMENTI (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      move = true;
   }

   public void checkEffect ()
   {
      if (move)
      {
         move();
         if (getBlock().getType() != Material.AIR)
         {
            Block block = location.subtract(super.vector).getBlock();
            block.setType(Material.WATER);
            changed.add(block);
            kill = false;
            move = false;
            lifeTicks = (int) (-(usesModifier * 1200));
         }
      }
      else
      {
         lifeTicks++;
      }
      if (lifeTicks >= 159)
      {
         revert();
         kill();
      }
   }

   @Override
   public void revert ()
   {
      for (Block block : changed)
      {
         Material mat = block.getType();
         if (mat == Material.WATER || mat == Material.STATIONARY_WATER)
         {
            block.setType(Material.AIR);
         }
      }
   }
}