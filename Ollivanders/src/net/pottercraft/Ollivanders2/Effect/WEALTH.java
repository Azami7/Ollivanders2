package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Add money to a player's inventory every 10 seconds
 *
 * @author Azami7
 * @since 2.2.9
 */
public class WEALTH extends O2Effect
{
   int strength = 1;

   Player target;

   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid      the player this effect acts on
    */
   public WEALTH (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.WEALTH;
      informousText = legilimensText = "feels fortunate";

      target = p.getServer().getPlayer(targetID);

      divinationText.add("will be blessed by fortune");
      divinationText.add("will have unnatural luck");
      divinationText.add("shall be granted a wish");
      divinationText.add("will be gifted by a leprechaun");
   }

   /**
    * Age this effect each game tick.
    */
   @Override
   public void checkEffect ()
   {
      age(1);

      int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % 100) * strength;

      // only take action once per 10 seconds, which is every 120 ticks
      if ((duration % 120) == 0)
      {
         List<ItemStack> kit = new ArrayList<>();

         ItemStack money;

         if (rand > 90)
         {
            money = Ollivanders2API.common.getGalleon(1);
         }
         else if (rand > 60)
         {
            money = Ollivanders2API.common.getSickle(1);
         }
         else
         {
            money = Ollivanders2API.common.getKnut(1);
         }

         kit.add(money);

         Ollivanders2API.common.givePlayerKit(target, kit);
      }
   }

   /**
    * Set the strength of this effect
    *
    * @param s a positive integer where 1 is normal strength
    */
   public void setStrength (int s)
   {
      strength = s;
   }
}
