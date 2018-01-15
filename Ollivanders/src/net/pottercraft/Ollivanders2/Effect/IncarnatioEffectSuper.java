package net.pottercraft.Ollivanders2.Effect;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.OEffect;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Created by Azami7 on 6/30/17.
 *
 * Turn a player in to a Creature.
 *
 * @author Azami7
 */
public class IncarnatioEffectSuper extends OEffect implements Effect
{
   String name;
   EntityType animalShape;
   Creature animal;
   private int myID = -1;

   public IncarnatioEffectSuper (Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);
   }

   @Override
   public void checkEffect(Ollivanders2 p, Player owner)
   {
      age(1);

      String pName = owner.getName();
      if (owner.getName().length() > 10)
      {
         pName = pName.substring(0,9);
      }
      String animalName = name + " " + pName;

      if (duration > 0)
      {
         if (myID == -1)
         {
            animal = (Creature) owner.getWorld().spawnEntity(owner.getLocation(), animalShape);
            setAnimalType();
            animal.setCustomName(animalName);
            animal.setCustomNameVisible(true);
            myID = animal.getEntityId();
         }
         else
         {
            for (Entity entity : owner.getWorld().getEntities())
            {
               if (entity.getEntityId() == myID && entity.getType() == animalShape)
               {
                  Creature creature = (Creature) entity;
                  if (creature.getCustomName().equals(animalName))
                  {
                     owner.teleport(entity);
                     if (creature.getTarget() == owner)
                     {
                        creature.setTarget(null);
                     }
                     if (duration > 0)
                     {
                        for (Player other : owner.getWorld().getPlayers())
                        {
                           other.hidePlayer(p, owner);
                        }
                     }
                     return;
                  }
               }
            }
            duration = 0;
         }
      }
      else
      {
         if (myID != -1)
         {
            for (Player other : owner.getWorld().getPlayers())
            {
               other.showPlayer(p, owner);
            }
            for (Entity entity : owner.getWorld().getEntities())
            {
               if (entity.getEntityId() == myID && entity.getType() == animalShape)
               {
                  if (entity.getCustomName().equals(animalName))
                  {
                     entity.remove();
                     myID = -1;
                     return;
                  }
               }
            }
         }
         myID = -1;
      }
   }

   public void setAnimalType()
   {
      //by default, do nothing
   }
}
