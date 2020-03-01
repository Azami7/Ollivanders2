package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Sets a player's helmet to a specific block type.
 * <p>
 * In HP, these would be Transfiguration spells but for code purposes they behave like charm projectiles so we
 * extend Charms then override the spell type.
 *
 * @author Azami7
 */
public abstract class GaleatiSuper extends O2Spell
{
   Material materialType = Material.AIR;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public GaleatiSuper()
   {
      super();
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public GaleatiSuper (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
   }

   /**
    * Targets a player in radius of the projectile and changes their helmet.
    */
   @Override
   protected void doCheckEffect ()
   {
      List<LivingEntity> livingEntities = getLivingEntities(1.5);

      for (LivingEntity live : livingEntities)
      {
         if (live instanceof Player)
         {
            if (live.getUniqueId() == player.getUniqueId())
               continue;

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

      // projectile has stopped, kill the spell
      if (hasHitTarget())
         kill();
   }
}
