package net.pottercraft.Ollivanders2.Effect;

import org.bukkit.entity.Pig;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Created by Azami7 on 6/27/17.
 *
 * Turn target player in to a pig.
 *
 * @author lownes
 * @author Azami7
 */
public class INCARNATIO_PORCILLI extends IncarnatioEffectSuper
{
   public INCARNATIO_PORCILLI(Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);

      animalShape = EntityType.PIG;
      name = "Pig";
   }

   /**
    * Set traits about this pig.
    */
   @Override
   public void setAnimalType()
   {
      int rand = Math.abs(Ollivanders2.random.nextInt() % 100);
      Pig myAnimal = (Pig)animal;

      if (rand == 0)
         myAnimal.setSaddle(true);

      animal = myAnimal;
   }
}
