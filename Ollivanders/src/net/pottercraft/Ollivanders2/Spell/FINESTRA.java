package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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
   public FINESTRA ()
   {
      super();

      spellType = O2SpellType.FINESTRA;
      text = "Breaks glass in a radius.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FINESTRA(Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.FINESTRA;
      setUsesModifier();
   }

   public void checkEffect ()
   {
      move();
      Material type = getBlock().getType();
      System.out.println("Type 1: " + type.toString());
      if (type == Material.GLASS || type == Material.STAINED_GLASS ||
          type == Material.STAINED_GLASS_PANE || type == Material.THIN_GLASS)
      {
         for (Block block : Ollivanders2API.common.getBlocksInRadius(location, usesModifier))
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