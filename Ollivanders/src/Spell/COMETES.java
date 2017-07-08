package Spell;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

import java.util.ArrayList;

/**
 * Created by Azami7 on 6/29/17.
 *
 * Shoots mutliple orange burst fireworks in to the air.
 *
 * @see PyrotechniaSuper
 * @author Azami7
 */
public class COMETES extends PyrotechniaSuper
{
   public COMETES (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.ORANGE);
      fireworkType = Type.BURST;

      setMaxFireworks(10);
   }
}
