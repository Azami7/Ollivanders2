package net.pottercraft.Ollivanders2;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.entity.Player;

public class OEffect implements Serializable
{

   /**
    * Effect object stored in OPlayer
    */
   private static final long serialVersionUID = -1137020032216987693L;
   public Effects name;
   public UUID casterUUID;
   public int duration;
   public boolean kill;

   public OEffect (Player sender, Effects effect, int duration)
   {
      casterUUID = sender.getUniqueId();
      this.duration = duration;
      name = effect;
      kill = false;
   }

   /**
    * Ages the OEffect
    */
   public void age (int i)
   {
      duration -= i;
      if (duration < 0)
      {
         kill();
      }
   }

   /**
    * This kills the effect.
    */
   public void kill ()
   {
      kill = true;
   }

   /**
    * Returns the caster's UUID
    *
    * @return UUID
    */
   public UUID getCasterUUID ()
   {
      return casterUUID;
   }
}