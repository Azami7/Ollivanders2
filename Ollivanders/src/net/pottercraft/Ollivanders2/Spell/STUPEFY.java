package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Blinds and slows the target entity for a duration depending on the spell's level.
 *
 * @author lownes
 * @author Azami7
 */
public final class STUPEFY extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public STUPEFY ()
   {
      super();

      flavorText.add("The Stunning Spell");
      flavorText.add("\"Stunning is one of the most useful spells in your arsenal. It's sort of a wizard's bread and butter, really.\" -Harry Potter");
      text = "Stupefy will stun an opponent for a duration.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public STUPEFY (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
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
         int modifier = (int) usesModifier;
         PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, modifier * 20, modifier);
         PotionEffect slowness = new PotionEffect(PotionEffectType.SLOW, modifier * 20, modifier);
         entity.addPotionEffect(blindness);
         entity.addPotionEffect(slowness);
         kill = true;
      }
   }
}