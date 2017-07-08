package Spell;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

import java.util.ArrayList;

/**
 * Shoots yellow star fireworks in to the air.
 *
 * @see PyrotechniaSuper
 * @author Azami7
 */
public class BOTHYNUS extends PyrotechniaSuper
{
   public BOTHYNUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.YELLOW);
      fireworkType = Type.STAR;

      setMaxFireworks(10);
   }
}
