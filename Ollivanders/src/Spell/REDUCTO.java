package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Creates an explosion of magnitude depending on the spell level which
 * destroys blocks and sets fires.
 *
 * @author lownes
 */
public class REDUCTO extends SpellProjectile implements Spell
{

   public REDUCTO (Ollivanders2 p, Player player, Spells name, Double rightWand)
   {
      super(p, player, name, rightWand);
   }


   public void checkEffect ()
   {
      move();
      if (super.getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER)
      {
         Location backLoc = super.location.clone().subtract(vector);
         backLoc.getWorld().createExplosion(backLoc, (float) (usesModifier * 0.4));
      }
   }
}