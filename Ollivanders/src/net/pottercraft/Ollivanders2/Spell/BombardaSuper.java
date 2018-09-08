package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Creates an explosion at the target which scales with the player's level in the spell. Doesn't break blocks.
 *
 * @author Azami7
 */
public abstract class BombardaSuper extends Charms
{
   double strength;

   public BombardaSuper () { }

   public BombardaSuper (Ollivanders2 p, Player player, O2SpellType name, Double rightWand)
   {
      super(p, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      if (getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE
            && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER)
      {
         Location backLoc = super.location.clone().subtract(vector);
         backLoc.getWorld().createExplosion(backLoc.getX(), backLoc.getY(), backLoc.getZ(),
               (float) (strength * usesModifier), false, false);
      }
   }
}
