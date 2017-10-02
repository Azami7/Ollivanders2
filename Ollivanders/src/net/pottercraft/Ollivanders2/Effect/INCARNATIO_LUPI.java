package net.pottercraft.Ollivanders2.Effect;

import org.bukkit.entity.Wolf;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Created by Azami7 on 6/27/17.
 *
 * Turn a target player in to a wolf.
 *
 * @see IncarnatioEffectSuper
 * @version Ollivanders2
 * @author lownes
 */
public class INCARNATIO_LUPI extends IncarnatioEffectSuper
{
   public INCARNATIO_LUPI(Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);

      animalShape = EntityType.WOLF;
      name = "Dog";
   }

   /**
    * Set traits for this wolf.
    */
   @Override
   public void setAnimalType()
   {
      Wolf myAnimal = (Wolf)animal;

      int rand = Math.abs(Ollivanders2.random.nextInt() % 10);
      if (rand == 0)
         myAnimal.isTamed();

      animal = myAnimal;
   }
}
