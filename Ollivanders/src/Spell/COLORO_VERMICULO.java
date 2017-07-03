package Spell;

import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Created by Azami7 on 6/30/17.
 *
 * Target sheep or colored block turns red.
 *
 * @author Azami7
 */
public class COLORO_VERMICULO extends ColoroSuper
{
   public COLORO_VERMICULO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      //set color red
      color = DyeColor.RED;
   }
}
