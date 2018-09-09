package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Throws another player away from the caster. Twice as powerful as depulso.
 *
 * @author lownes
 */
public final class FLIPENDO extends Charms
{
   public FLIPENDO (O2SpellType type)
   {
      super(type);

      text = "Flipendo can be used to repel an opponent away from oneself.";
      flavorText.add("The Knockback Jinx");
      flavorText.add("The incantation for the knockback jinx is 'Flipendo'. This jinx is the most utilitarian of Grade 2 spell, in that it will allow the caster to 'knock back' an opponent or object and can also be used to push and activate certain magically charmed switches. Like many Grade 2 spells, Flipendo can be targeted.");
      flavorText.add("\"There was a loud bang and he felt himself flying backwards as if punched; as he slammed into the kitchen wall and slid to the floor, he glimpsed the tail of Lupin's cloak disappearing round the door.\"");
   }

   public FLIPENDO (Ollivanders2 plugin, Player player, O2SpellType type, Double rightWand)
   {
      super(plugin, player, type, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<LivingEntity> living = this.getLivingEntities(2);
      for (LivingEntity live : living)
      {
         if (live instanceof Player)
         {
            live.setVelocity(player.getLocation().getDirection().normalize().multiply(usesModifier / 10));
            kill();
            return;
         }
      }
   }
}