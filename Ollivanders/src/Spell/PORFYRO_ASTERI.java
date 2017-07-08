package Spell;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

import java.util.ArrayList;

/**
 * Shoots purple fireworks in to the air.
 *
 * @see PyrotechniaSuper
 * @author Azami7
 */
public class PORFYRO_ASTERI extends PyrotechniaSuper
{
   public PORFYRO_ASTERI (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.PURPLE);
      fireworkType = Type.STAR;

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
