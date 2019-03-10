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

      if (isGlass(getBlock()))
      {
         for (Block nearbyBlock : Ollivanders2API.common.getBlocksInRadius(location, usesModifier))
         {
            if (isGlass(nearbyBlock))
            {
               nearbyBlock.breakNaturally();
            }
         }
         kill();
      }
   }

   /**
    * Determine if a block is glass.
    *
    * @param block the block to check
    * @return true if the block is glass, false otherwise
    */
   private boolean isGlass (Block block)
   {
      Material blockType = block.getType();

      if (blockType == Material.GLASS || blockType == Material.GLASS_PANE
            || blockType == Material.BLACK_STAINED_GLASS || blockType == Material.BLACK_STAINED_GLASS_PANE
            || blockType == Material.GRAY_STAINED_GLASS || blockType == Material.GRAY_STAINED_GLASS_PANE
            || blockType == Material.BLUE_STAINED_GLASS || blockType == Material.BLUE_STAINED_GLASS_PANE
            || blockType == Material.BROWN_STAINED_GLASS || blockType == Material.BROWN_STAINED_GLASS_PANE
            || blockType == Material.CYAN_STAINED_GLASS || blockType == Material.CYAN_STAINED_GLASS_PANE
            || blockType == Material.GREEN_STAINED_GLASS || blockType == Material.GREEN_STAINED_GLASS_PANE
            || blockType == Material.LIGHT_BLUE_STAINED_GLASS || blockType == Material.LIGHT_BLUE_STAINED_GLASS_PANE
            || blockType == Material.RED_STAINED_GLASS || blockType == Material.RED_STAINED_GLASS_PANE
            || blockType == Material.YELLOW_STAINED_GLASS || blockType == Material.YELLOW_STAINED_GLASS_PANE
            || blockType == Material.LIME_STAINED_GLASS || blockType == Material.LIME_STAINED_GLASS_PANE
            || blockType == Material.ORANGE_STAINED_GLASS || blockType == Material.ORANGE_STAINED_GLASS_PANE
            || blockType == Material.LIGHT_GRAY_STAINED_GLASS || blockType == Material.LIGHT_GRAY_STAINED_GLASS_PANE
            || blockType == Material.MAGENTA_STAINED_GLASS || blockType == Material.MAGENTA_STAINED_GLASS_PANE
            || blockType == Material.PURPLE_STAINED_GLASS || blockType == Material.PURPLE_STAINED_GLASS_PANE
            || blockType == Material.WHITE_STAINED_GLASS || blockType == Material.WHITE_STAINED_GLASS_PANE
            || blockType == Material.PINK_STAINED_GLASS || blockType == Material.PINK_STAINED_GLASS_PANE)
      {
         return true;
      }
      else
      {
         return false;
      }
   }
}