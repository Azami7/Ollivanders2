package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Reparifors is a healing spell that reverts minor magically-induced ailments, such as paralysis and poisoning.
 * http://harrypotter.wikia.com/wiki/Reparifors
 *
 * @author Azami7
 * @since 2.2.9
 */
public class REPARIFORS extends Charms
{
   public REPARIFORS ()
   {
      super();

      spellType = O2SpellType.REPARIFORS;

      text = "A healing spell for minor ailments such as paralysis or poisoning.";
   }

   public REPARIFORS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.REPARIFORS;
   }

   @Override
   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(1.5))
      {
         if (live.getUniqueId() == player.getUniqueId())
         {
            continue;
         }

         if (live instanceof Player)
         {
            Player player = (Player) live;

            // if they are affected by immobilize, remove the effect
            if (p.players.playerEffects.hasEffect(player.getUniqueId(), O2EffectType.IMMOBILIZE))
            {
               p.players.playerEffects.ageEffect(player.getUniqueId(), O2EffectType.IMMOBILIZE, (int) (usesModifier * 2400));
            }

            // do a minor heal
            player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, (int) (usesModifier * 600), 1), true);

            // reduce duration of poison by half
            if (player.hasPotionEffect(PotionEffectType.POISON))
            {
               int duration = player.getPotionEffect(PotionEffectType.POISON).getDuration();
               player.removePotionEffect(PotionEffectType.POISON);
               player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, (int) (duration / 2), 1), true);
            }

            kill();
            return;
         }
      }
   }
}
