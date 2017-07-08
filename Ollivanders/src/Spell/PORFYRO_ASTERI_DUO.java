package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * A fancier version of PORFYRO_ASTERI.
 *
 * @see PORFYRO_ASTERI
 * @see PyrotechniaSuper
 * @author Azami7
 */
public class PORFYRO_ASTERI_DUO extends PyrotechniaSuper
{
   public PORFYRO_ASTERI_DUO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
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
