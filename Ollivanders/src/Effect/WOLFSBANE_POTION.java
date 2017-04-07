package Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.OEffect;

public class WOLFSBANE_POTION extends OEffect implements Effect
{

   /**
    *
    */
   private static final long serialVersionUID = -7153440938645339899L;

   public WOLFSBANE_POTION (Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);
   }

   @Override
   public void checkEffect (Ollivanders2 p, Player owner)
   {
      age(1);
   }

}
