package Effect;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;

/**
 * Created by Azami7 on 6/27/17. Imported from iarepandemonium/Ollivanders.
 *
 * @author lownes
 * @author Azami7
 */
public class INCARNATIO_DEVITO extends IncarnatioEffectSuper
{
   //int chickenID = -1;

   public INCARNATIO_DEVITO (Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);

      animalShape = EntityType.CHICKEN;
      name = "Chicken";
   }
}
