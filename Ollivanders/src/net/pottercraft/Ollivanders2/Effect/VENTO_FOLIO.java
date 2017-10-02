package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.OEffect;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Grants player flight
 *
 * @author lownes
 */
public class VENTO_FOLIO extends OEffect implements Effect
{
   public VENTO_FOLIO (org.bukkit.entity.Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);
   }

   @Override
   public void checkEffect (Ollivanders2 p, org.bukkit.entity.Player owner)
   {
      age(1);
      if (duration > 1)
      {
         owner.setAllowFlight(true);
         owner.getWorld().playEffect(owner.getLocation(), org.bukkit.Effect.SMOKE, 4);
      }
      else
      {
         owner.setAllowFlight(false);
      }
   }
}
