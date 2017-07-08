package Spell;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

import java.util.ArrayList;

/**
 * A fancier version of Bothynus.
 *
 * @see BOTHYNUS
 * @see PyrotechniaSuper
 * @author Azami7
 */
public class BOTHYNUS_DUO extends PyrotechniaSuper
{
   public BOTHYNUS_DUO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.YELLOW);
      fireworkColors.add(Color.ORANGE);
      fireworkType = Type.STAR;
      hasTrails = true;

      if (usesModifier > 100)
      {
         maxFireworks = 10;
      }
      else
      {
         maxFireworks = usesModifier / 10;
      }
   }
}
