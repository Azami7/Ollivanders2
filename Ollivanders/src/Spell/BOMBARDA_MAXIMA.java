package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Creates an explosion at the target location twice as
 * powerful as bombarda. Doesn't break blocks.
 *
 * @author lownes
 */
public class BOMBARDA_MAXIMA extends SpellProjectile implements Spell
{

   public BOMBARDA_MAXIMA (Ollivanders2 p, Player player, Spells name, Double rightWand)
   {
      super(p, player, name, rightWand);
   }


   public void checkEffect ()
   {
      move();
      if (super.getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER)
      {
         Location backLoc = super.location.clone().subtract(vector);
         backLoc.getWorld().createExplosion(backLoc.getX(), backLoc.getY(), backLoc.getZ(), (float) (1.6 * usesModifier), false, false);
      }
   }
}