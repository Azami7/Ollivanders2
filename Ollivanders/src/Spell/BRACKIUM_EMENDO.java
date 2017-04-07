package Spell;

import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Hurts skeletons, withers, and wither skeletons
 *
 * @author lownes
 */
public class BRACKIUM_EMENDO extends SpellProjectile implements Spell
{

   public BRACKIUM_EMENDO (Ollivanders2 plugin, Player player, Spells name,
                           Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<LivingEntity> entities = getLivingEntities(1);
      for (LivingEntity entity : entities)
      {
         EntityType type = entity.getType();
         if (type == EntityType.SKELETON || type == EntityType.WITHER_SKULL || type == EntityType.WITHER)
         {
            entity.damage(usesModifier * 2, player);
            kill();
            return;
         }
      }
   }

}