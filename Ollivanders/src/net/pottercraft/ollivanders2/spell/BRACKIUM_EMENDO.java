package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Healing spell that can be used to mend broken bones.  Can also be used to remove bones and can be used defensively
 * against skeletons and withers.
 *
 * @author lownes
 * @author Azami7
 */
public final class BRACKIUM_EMENDO extends O2Spell
{
   private final int maxDuration = 300;
   private final int minDuration = 10;

   private final double maxDamage = 10;
   private final double minDamage = 0.25;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public BRACKIUM_EMENDO ()
   {
      super();

      spellType = O2SpellType.BRACKIUM_EMENDO;
      branch = O2MagicBranch.HEALING;

      flavorText = new ArrayList<String>() {{
         add("Bone-Healing Spell");
         add("\"Lie back, Harry. It's a simple charm I've used countless times --\" - Gilderoy Lockhard");
         add("As Harry got to his feet, he felt strangely lopsided. Taking a deep breath he looked down at his right side. What he saw nearly made him pass out again. Poking out of the end of his robes was what looked like a thick, fleshcoloured rubber glove. He tried to move his fingers. Nothing happened. Lockhart hadn't mended Harry's bones. He had removed them.");
      }};

      text = "A healing spell when used on a player. When used on a skeleton or wither, it damages them.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public BRACKIUM_EMENDO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.BRACKIUM_EMENDO;
      branch = O2MagicBranch.HEALING;

      initSpell();
   }

   /**
    * Find living entity near the projectile - if it is a skeleton, damage it, if it is a player,
    * heal them.
    */
   @Override
   protected void doCheckEffect ()
   {
      List<LivingEntity> entities = getLivingEntities(1.5);

      if (entities.size() > 0)
      {
         for (LivingEntity entity : entities)
         {
            if (entity.getUniqueId() == player.getUniqueId())
               continue;

            EntityType type = entity.getType();
            if (type == EntityType.SKELETON || type == EntityType.WITHER_SKULL || type == EntityType.WITHER)
            {
               double damage = usesModifier * 0.2;
               if (damage < minDamage)
               {
                  damage = minDamage;
               }
               else if (damage > maxDamage)
               {
                  damage = maxDamage;
               }

               entity.damage(damage, player);

               kill();
               break;
            }
            else if (type == EntityType.PLAYER)
            {
               int duration = (int) usesModifier;
               if (duration < minDuration)
               {
                  duration = minDuration;
               }
               else if (duration > maxDuration)
               {
                  duration = maxDuration;
               }

               int durationInTicks = duration * Ollivanders2Common.ticksPerSecond;

               PotionEffect effect = new PotionEffect(PotionEffectType.HEAL, durationInTicks, 1);
               entity.addPotionEffect(effect);

               if (Ollivanders2.debug)
               {
                  p.getLogger().info("Adding heal potion effect to " + entity.getName() + " for " + durationInTicks + " game ticks.");
               }

               kill();
               break;
            }
         }

         return;
      }

      // projectile has stopped, kill the spell
      if (hasHitTarget())
         kill();
   }
}