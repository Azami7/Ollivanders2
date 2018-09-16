package net.pottercraft.Ollivanders2.Effect;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;

import net.pottercraft.Ollivanders2.Ollivanders2;

public class MUCUS extends O2Effect
{
   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid the ID of the player this effect acts on
    */
   public MUCUS (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.MUCUS;
   }

   /**
    * Spawn a slime entity on the player's head once per 15 seconds.
    */
   public void checkEffect ()
   {
      age(1);
      if (duration % 300 == 0)
      {
         Player target = p.getServer().getPlayer(targetID);

         World world = target.getWorld();
         Slime slime = (Slime) world.spawnEntity(target.getEyeLocation(), EntityType.SLIME);
         slime.setSize(1);
      }
   }
}