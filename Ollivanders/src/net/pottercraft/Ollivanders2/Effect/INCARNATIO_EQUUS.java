package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Horse;

import net.pottercraft.Ollivanders2.Effects;

/**
 * Created by Azami7 on 6/28/17.
 *
 * Turns target player in to a horse.
 *
 * @see IncarnatioEffectSuper
 * @author Azami7
 */
public class INCARNATIO_EQUUS extends IncarnatioEffectSuper
{
   public INCARNATIO_EQUUS(Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);

      animalShape = EntityType.HORSE;
      name = "Horse";
   }

   /**
    * Sets traits about this horse.
    */
   @Override
   public void setAnimalType()
   {
      Horse myAnimal = (Horse)animal;

      // small chance of a special style.
      int rand = Math.abs(Ollivanders2.random.nextInt() % 20);
      if (rand == 0)
         myAnimal.setStyle(Horse.Style.BLACK_DOTS);
      else if (rand == 1)
         myAnimal.setStyle(Horse.Style.WHITE);
      else if (rand == 2)
         myAnimal.setStyle(Horse.Style.WHITE_DOTS);
      else if (rand == 3)
         myAnimal.setStyle(Horse.Style.WHITEFIELD);
      else
         myAnimal.setStyle(Horse.Style.NONE);

      // randomize colors
      rand = Math.abs(Ollivanders2.random.nextInt() % 7);
      if (rand == 0)
         myAnimal.setColor(Horse.Color.BLACK);
      else if (rand == 1)
         myAnimal.setColor(Horse.Color.BROWN);
      else if (rand == 2)
         myAnimal.setColor(Horse.Color.CHESTNUT);
      else if (rand == 3)
         myAnimal.setColor(Horse.Color.CREAMY);
      else if (rand == 4)
         myAnimal.setColor(Horse.Color.DARK_BROWN);
      else if (rand == 5)
         myAnimal.setColor(Horse.Color.GRAY);
      else
         myAnimal.setColor(Horse.Color.WHITE);

      animal = myAnimal;
   }
}
