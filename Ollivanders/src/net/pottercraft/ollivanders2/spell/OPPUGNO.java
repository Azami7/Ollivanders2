package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.util.Vector;

import java.util.ArrayList;

/**
 * Causes a living entity to damage another living entity.
 *
 * @author Azami7
 * @version Ollivanders2
 */
public final class OPPUGNO extends O2Spell
{
   double damage;

   static double maxDamage = 6.0;
   static double minDamage = 0.5;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public OPPUGNO()
   {
      super();

      spellType = O2SpellType.OPPUGNO;
      branch = O2MagicBranch.DARK_ARTS;

      flavorText = new ArrayList<String>()
      {{
         add("Harry spun around to see Hermione pointing her wand at Ron, her expression wild: The little flock of birds was speeding like a hail of fat golden bullets toward Ron, who yelped and covered his face with his hands, but the birds attacked, pecking and clawing at every bit of flesh they could reach.");
         add("The Oppugno Jinx");
      }};

      text = "Oppugno will cause a creature or player to attack the targeted entity.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public OPPUGNO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.OPPUGNO;
      branch = O2MagicBranch.DARK_ARTS;

      initSpell();

      // world guard flags
      worldGuardFlags.add(Flags.PVP);
      worldGuardFlags.add(Flags.DAMAGE_ANIMALS);

      damage = usesModifier / 20;
      if (damage < minDamage)
      {
         damage = minDamage;
      }
      else if (damage > maxDamage)
      {
         damage = maxDamage;
      }
   }

   @Override
   protected void doCheckEffect ()
   {
      // get the target to be attacked
      LivingEntity target = null;

      for (LivingEntity livingEntity : getLivingEntities(1.5))
      {
         if (livingEntity.getUniqueId() == player.getUniqueId())
            continue;

         target = livingEntity;
         stopProjectile();
      }

      if (target != null)
      {
         // get an entity to attack the target
         LivingEntity attacker = null;

         for (LivingEntity livingEntity : getLivingEntities(10))
         {
            if (livingEntity.getUniqueId() == player.getUniqueId())
            {
               continue;
            }

            attacker = livingEntity;
         }

         if (attacker == null)
         {
            kill();
            return;
         }

         if (attacker instanceof Monster)
         {
            ((Monster) attacker).setTarget(target);
         }

         Vector targetPos = target.getLocation().toVector();
         Vector attackerPos = attacker.getLocation().toVector();
         Vector velocity = targetPos.subtract(attackerPos);
         attacker.setVelocity(velocity.normalize().multiply(2.0));

         target.damage(damage, attacker);
      }

      if (hasHitTarget())
      {
         kill();
      }
   }
}