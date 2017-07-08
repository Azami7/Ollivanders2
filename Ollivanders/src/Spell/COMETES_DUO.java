package Spell;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

import java.util.ArrayList;

/**
 * A fancier version of COMETES.
 *
 * @see COMETES
 * @see PyrotechniaSuper
 * @author Azami7
 */
public class COMETES_DUO extends PyrotechniaSuper
{
   public COMETES_DUO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.ORANGE);
      fireworkType = Type.BURST;

      hasTrails = true;
      hasFlicker = true;
      hasFade = true;

      fadeColors = new ArrayList<>();
      fadeColors.add(Color.YELLOW);
      fadeColors.add(Color.WHITE);

      setMaxFireworks(10);
   }
}
