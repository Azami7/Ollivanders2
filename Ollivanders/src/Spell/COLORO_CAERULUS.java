package Spell;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Created by kristin on 6/29/17.
 *
 * @author Azami7
 */
public class COLORO_CAERULUS extends ColoroSuper
{
   public COLORO_CAERULUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      //set color blue
      color = DyeColor.BLUE;
   }
}
