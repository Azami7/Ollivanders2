package Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Creates a larger incendio that strafes and doubles the radius and duration.
 *
 * @see IncarnatioSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public class INCENDIO_DUO extends IncendioSuper
{
   public INCENDIO_DUO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      location.add(vector);
      strafe = true;
      blockRadius = 2;
      radius = 2;
      distance = 2;
      duration = 2;
   }
}