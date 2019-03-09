package net.pottercraft.Ollivanders2.Spell;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
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

   public BombardaSuper ()
   {
      super();
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public BombardaSuper (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      setUsesModifier();

      projectilePassThrough.add(Material.FIRE);
      projectilePassThrough.add(Material.WATER);

      worldGuardFlags.add(DefaultFlag.CREEPER_EXPLOSION);
   }

   /**
    * Create an explosion in front of the target block
    */
   protected void doCheckEffect ()
   {
      if (hasHitTarget())
      {
         Location backLoc = location.clone().subtract(vector);
         backLoc.getWorld().createExplosion(backLoc.getX(), backLoc.getY(), backLoc.getZ(), (float) (strength * usesModifier), false, false);

         kill();
      }
   }
}
