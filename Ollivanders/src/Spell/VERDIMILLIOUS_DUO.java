package Spell;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

import java.util.ArrayList;

/**
 * Fancier version of VERDIMILLIOUS
 *
 * @see PyrotechniaSuper
 * @see VERDIMILLIOUS
 * @author Azami7
 */
public class VERDIMILLIOUS_DUO extends PyrotechniaSuper
{
   public VERDIMILLIOUS_DUO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.GREEN);
      fireworkColors.add(Color.LIME);

      fireworkType = Type.BALL_LARGE;
      hasTrails = true;

      if (usesModifier > 150)
      {
         maxFireworks = 15;
      }
      else
      {
         maxFireworks = usesModifier / 10;
      }
   }
}
