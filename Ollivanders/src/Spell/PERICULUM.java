package Spell;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

import java.util.ArrayList;

/**
 * Shoots red burst fireworks in to the air.
 *
 * @see PyrotechniaSuper
 * @author lownes
 * @author Azami7
 */
public class PERICULUM extends PyrotechniaSuper
{
   public PERICULUM (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.RED);
      fireworkType = Type.BURST;

      setMaxFireworks(10);
   }
}