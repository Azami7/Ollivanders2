package net.pottercraft.Ollivanders2.Effect;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.OEffect;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Created by kristin on 6/27/17.
 *
 * This spell causes flowers to sprout from the victim.
 *
 * @version Ollivanders2
 * @author lownes
 */
public class HERBIFORS extends OEffect implements Effect
{
   private int maxFlowers;
   private int flower_count;

   public HERBIFORS(Player sender, Effects effect, int duration, int max_flowers)
   {
      super(sender, effect, duration);
      maxFlowers = max_flowers;
      flower_count = 0;
   }

   public void checkEffect(Ollivanders2 p, Player owner)
   {
      age(1);

      if (flower_count <= maxFlowers)
      {
         World world = owner.getWorld();
         int rnum = Math.abs(Ollivanders2.random.nextInt() % 9);
         ItemStack flower = new ItemStack(Material.RED_ROSE);
         flower.setDurability((short)rnum);
         world.dropItem(owner.getEyeLocation(), flower);
         flower_count++;
      }
   }
}
