package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
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
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FLIPENDO ()
   {
      super();

      spellType = O2SpellType.FLIPENDO;

      flavorText = new ArrayList<String>() {{
         add("The Knockback Jinx");
         add("The incantation for the knockback jinx is 'Flipendo'. This jinx is the most utilitarian of Grade 2 spell, in that it will allow the caster to 'knock back' an opponent or object and can also be used to push and activate certain magically charmed switches. Like many Grade 2 spells, Flipendo can be targeted.");
         add("\"There was a loud bang and he felt himself flying backwards as if punched; as he slammed into the kitchen wall and slid to the floor, he glimpsed the tail of Lupin's cloak disappearing round the door.\"");
      }};

      text = "Flipendo can be used to repel an opponent away from oneself.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FLIPENDO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.FLIPENDO;
      setUsesModifier();
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