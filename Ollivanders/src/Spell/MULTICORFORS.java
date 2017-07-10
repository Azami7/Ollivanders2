package Spell;

import org.bukkit.Color;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * If an entity has leather armor on, then this changes it's color
 *
 * @author lownes
 */
public class MULTICORFORS extends SpellProjectile implements Spell
{
   public MULTICORFORS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(1))
      {
         EntityEquipment equipment = live.getEquipment();
         for (ItemStack armor : equipment.getArmorContents())
         {
            if (armor.getItemMeta() instanceof LeatherArmorMeta)
            {
               LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
               Color curColor = meta.getColor();
               int modifier = (int) (Math.random() * usesModifier * 40);
               modifier = modifier - modifier / 2;
               int blue = (curColor.getBlue() + modifier) % 256;
               int green = (curColor.getGreen() + modifier) % 256;
               int red = (curColor.getRed() + modifier) % 256;
               Color newColor = Color.fromRGB(red, green, blue);
               meta.setColor(newColor);
               armor.setItemMeta(meta);
               kill();
            }
         }
      }
   }
}