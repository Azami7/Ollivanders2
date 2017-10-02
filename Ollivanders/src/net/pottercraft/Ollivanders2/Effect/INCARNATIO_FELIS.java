package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;

/**
 * Created by Azami7 on 6/27/17.
 *
 * Turn target player in to an ocelot.
 *
 * @see IncarnatioEffectSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public class INCARNATIO_FELIS extends IncarnatioEffectSuper
{
   public INCARNATIO_FELIS(Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);

      animalShape = EntityType.OCELOT;
      name = "Cat";
   }

   /**
    * Set traits about this ocelot.
    */
   @Override
   public void setAnimalType()
   {
      Ocelot myAnimal = (Ocelot)animal;

      int rand = Math.abs(Ollivanders2.random.nextInt() % 3);
      if (rand == 0)
         myAnimal.setCatType(Ocelot.Type.BLACK_CAT);
      else if (rand == 1)
         myAnimal.setCatType(Ocelot.Type.RED_CAT);
      else
         myAnimal.setCatType(Ocelot.Type.SIAMESE_CAT);

      rand = Ollivanders2.random.nextInt() % 10;
      if (rand == 0)
         myAnimal.isTamed();

      animal = myAnimal;
   }
}
