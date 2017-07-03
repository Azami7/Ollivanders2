package Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Creates a larger incendio that strafes and has 4x the radius and duration.
 *
 * @see IncarnatioSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public class INCENDIO_TRIA extends IncendioSuper
{
   public INCENDIO_TRIA (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      location.add(vector.multiply(2));
      strafe = true;
      radius = 2;
      blockRadius = 4;
      distance = 3;
      duration = 4;
   }
}