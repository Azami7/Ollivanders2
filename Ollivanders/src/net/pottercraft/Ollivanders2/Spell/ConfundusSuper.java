package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Confundus Charm super class which causes confusion in the target
 *
 * @author Azami7
 * @author lownes
 */
public abstract class ConfundusSuper extends Charms
{
   int modifier = 0;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ConfundusSuper ()
   {

   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public ConfundusSuper (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      List<LivingEntity> entities = getLivingEntities(1);
      if (entities.size() > 0)
      {
         LivingEntity entity = entities.get(0);
         modifier = modifier * (int) usesModifier;
         PotionEffect confusion = new PotionEffect(PotionEffectType.CONFUSION, modifier * 20, modifier);
         entity.addPotionEffect(confusion);
         kill();
         return;
      }
   }
}
