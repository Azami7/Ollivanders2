package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Encases target's head in a melon itemstack with amount 0
 *
 * @author lownes
 */
public class MELOFORS extends SpellProjectile implements Spell
{

   public MELOFORS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(1))
      {
         EntityEquipment ee = live.getEquipment();
         ItemStack helmet = ee.getHelmet();
         if (helmet != null)
         {
            if (helmet.getType() != Material.AIR)
            {
               live.getWorld().dropItem(live.getEyeLocation(), helmet);
            }
         }
         ee.setHelmet(new ItemStack(Material.MELON_BLOCK, 0));
         kill();
         return;
      }
   }

}