package net.pottercraft.Ollivanders2.Effect;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;

import net.pottercraft.Ollivanders2.Ollivanders2;

public class MUCUS_AD_NAUSEAM extends O2Effect
{
   /**
    * Constructor
    *
    * @param plugin the callback to the MC plugin
    * @param effect the effect cast
    * @param duration the duration of the effect
    */
   public MUCUS_AD_NAUSEAM (Ollivanders2 plugin, O2EffectType effect, int duration)
   {
      super(plugin, effect, duration);
   }

   /**
    * Spawn a slime entity on the player's head once per second.
    *
    * @param target the player affected by the effect
    */
   public void checkEffect (Player target)
   {
      age(1);
      if (duration % 20 == 0)
      {
         World world = target.getWorld();
         Slime slime = (Slime) world.spawnEntity(target.getEyeLocation(), EntityType.SLIME);
         slime.setSize(1);
      }
   }
}