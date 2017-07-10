package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

import org.bukkit.entity.Player;

/**
 * Creates an explosion at the target which scales with
 * the player's level in the spell. Doesn't break blocks.
 *
 * @see BombardaSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public class BOMBARDA extends BombardaSuper
{
   public BOMBARDA (Ollivanders2 p, Player player, Spells name, Double rightWand)
   {
      super(p, player, name, rightWand);
      strength = 0.8;
   }
}