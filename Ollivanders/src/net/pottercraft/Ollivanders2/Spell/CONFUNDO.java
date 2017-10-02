package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Casts a powerful confusion potion effect on the player that scales with the caster's level in this spell.
 *
 * @version Ollivanders
 * @author lownes
 * @author Azami7
 */
public final class CONFUNDO extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public CONFUNDO ()
   {
      super();

      flavorText.add("The Confundus Charm");
      flavorText.add("\"Look who's talking. Confunded anyone lately?\" -Harry Potter");
      flavorText.add("The guard was confused. He stared down at the thin, golden Probe and then at his companion, who said in a slightly dazed voice, 'Yeah, you've just checked them, Marius.'");
      text = "Confundo causes the target to become confused.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public CONFUNDO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<LivingEntity> entities = getLivingEntities(1);
      if (entities.size() > 0)
      {
         LivingEntity entity = entities.get(0);
         int modifier = (int) usesModifier;
         PotionEffect confusion = new PotionEffect(PotionEffectType.CONFUSION, modifier * 20, modifier);
         entity.addPotionEffect(confusion);
         kill();
         return;
      }
   }
}