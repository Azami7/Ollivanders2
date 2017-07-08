package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Fanciest version of PORFYRO_ASTERI.
 *
 * @see PyrotechniaSuper
 * @see PORFYRO_ASTERI
 * @author Azami7
 */
public class PORFYRO_ASTERI_TRIA extends PyrotechniaSuper
{
   public PORFYRO_ASTERI_TRIA (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.PURPLE);
      fireworkColors.add(Color.FUCHSIA);

      hasTrails = true;

      hasFade = true;
      fadeColors = new ArrayList<>();
      fadeColors.add(Color.WHITE);

      fireworkType = FireworkEffect.Type.STAR;
      shuffleTypes = true;

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
