package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

public class MEMORY_POTION extends OEffect implements Effect
{
   public MEMORY_POTION (Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);
   }

   @Override
   public void checkEffect (Ollivanders2 p, Player owner)
   {
      age(1);
   }
}
