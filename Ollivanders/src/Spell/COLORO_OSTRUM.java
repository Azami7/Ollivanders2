package Spell;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Created by Azami7 on 6/30/17.
 *
 * @author Azami7
 */
public class COLORO_OSTRUM extends ColoroSuper
{
   public COLORO_OSTRUM (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      //set color purple
      color = DyeColor.PURPLE;
   }
}
