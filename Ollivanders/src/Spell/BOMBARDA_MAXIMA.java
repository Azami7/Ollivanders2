package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

import org.bukkit.entity.Player;

/**
 * Creates an explosion at the target location twice as
 * powerful as bombarda. Doesn't break blocks.
 *
 * @see BombardaSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public class BOMBARDA_MAXIMA extends BombardaSuper
{

   public BOMBARDA_MAXIMA (Ollivanders2 p, Player player, Spells name, Double rightWand)
   {
      super(p, player, name, rightWand);
      strength = 1.6;
   }
}