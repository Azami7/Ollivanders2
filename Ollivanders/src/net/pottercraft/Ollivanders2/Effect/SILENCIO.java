package net.pottercraft.Ollivanders2.Effect;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.OEffect;
import net.pottercraft.Ollivanders2.Ollivanders2;

public class SILENCIO extends OEffect implements Effect
{
   public SILENCIO (Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);
   }

   public void checkEffect (Ollivanders2 p, Player owner)
   {
      age(1);
   }
}