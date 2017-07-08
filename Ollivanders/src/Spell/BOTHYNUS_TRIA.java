package Spell;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

import java.util.ArrayList;

/**
 * Fanciest version of Bothynus.
 *
 * @see BOTHYNUS
 * @see PyrotechniaSuper
 * @author Azami7
 */
public class BOTHYNUS_TRIA extends PyrotechniaSuper
{
   public BOTHYNUS_TRIA (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.YELLOW);
      fireworkColors.add(Color.ORANGE);

      fadeColors = new ArrayList<>();
      fadeColors.add(Color.SILVER);

      fireworkType = Type.STAR;
      hasTrails = true;
      hasFade = true;

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
