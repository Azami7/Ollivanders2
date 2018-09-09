package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

/**
 * Sets a player's helmet to a specific block type.
 *
 * In HP, these would be Transfiguration spells but for code purposes they behave like charm projectiles so we
 * extend Charms then override the spell type.
 *
 * @author Azami7
 */
public abstract class GaleatiSuper extends Charms
{
   Material materialType = Material.AIR;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public GaleatiSuper (O2SpellType type)
   {
      super(type);

      branch = O2MagicBranch.TRANSFIGURATION;
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param type
    * @param rightWand
    */
   public GaleatiSuper (Ollivanders2 plugin, Player player, O2SpellType type, Double rightWand)
   {
      super(plugin, player, type, rightWand);
   }

   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(2))
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
         ee.setHelmet(new ItemStack(materialType, 1));
         kill();
         return;
      }
   }
}
