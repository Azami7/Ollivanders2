package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Breaks glass.
 *
 * @author autumnwoz
 */
public final class FINESTRA extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FINESTRA()
   {
      super();

      text = "Breaks glass in a radius.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public FINESTRA(Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      Material type = getBlock().getType();
      System.out.println("Type 1: " + type.toString());
      if (type == Material.GLASS || type == Material.STAINED_GLASS ||
          type == Material.STAINED_GLASS_PANE || type == Material.THIN_GLASS)
      {
         for (Block block : Ollivanders2Common.getBlocksInRadius(location, usesModifier))
         {
            Material type2 = block.getType();
            System.out.println("Type 2: " + type2.toString());
            if (type2 == Material.GLASS || type2 == Material.STAINED_GLASS ||
                type2 == Material.STAINED_GLASS_PANE || type2 == Material.THIN_GLASS)
            {
               System.out.println("Break block");
               block.breakNaturally();
            }
         }
         kill();
      }
   }
}