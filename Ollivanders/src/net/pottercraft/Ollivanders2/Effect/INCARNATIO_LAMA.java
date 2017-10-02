package net.pottercraft.Ollivanders2.Effect;

import org.bukkit.entity.Llama;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Created by Azami7 on 6/28/17.
 *
 * @author Azami7
 */
public class INCARNATIO_LAMA extends IncarnatioEffectSuper
{
   public INCARNATIO_LAMA(Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);
      animalShape = EntityType.LLAMA;
      name = "Llama";
   }

   /**
    * Set traits for this llama.
    */
   @Override
   public void setAnimalType()
   {
      Llama myAnimal = (Llama)animal;

      int rand = Math.abs(Ollivanders2.random.nextInt() % 4);
      if (rand == 0)
         myAnimal.setColor(Llama.Color.BROWN);
      else if (rand == 1)
         myAnimal.setColor(Llama.Color.CREAMY);
      else if (rand == 2)
         myAnimal.setColor(Llama.Color.GRAY);
      else
         myAnimal.setColor(Llama.Color.WHITE);

      animal = myAnimal;
   }
}