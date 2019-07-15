package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Burns sun-sensitive entities with a radius.
 *
 * @version Ollivanders2
 * @author Azami7
 */
public final class LUMOS_SOLEM extends Charms
{
   int duration;
   int targetCount;

   static int maxDuration = Ollivanders2Common.ticksPerSecond * 120; // 2 minutes
   static int maxTargetCount = 10;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LUMOS_SOLEM ()
   {
      super();

      spellType = O2SpellType.LUMOS_SOLEM;

      flavorText = new ArrayList<String>() {{
         add("Light of the Sun");
      }};

      text = "Lumos Solem will cause a sun-like light to erupt in an area around the impact which will burn entities sensitive to sun.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public LUMOS_SOLEM (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.LUMOS_SOLEM;
      setUsesModifier();

      // duration of fire damage, min is 1 second
      duration = Ollivanders2Common.ticksPerSecond * (int) usesModifier;
      if (duration > maxDuration)
      {
         duration = maxDuration;
      }

      // max number of entities targeted
      targetCount = 1 + ((int) usesModifier / 10);
      if (targetCount > maxTargetCount)
      {
         targetCount = maxTargetCount;
      }
   }

   @Override
   protected void doCheckEffect ()
   {
      List<LivingEntity> entities = getLivingEntities(1.5);

      for (LivingEntity entity : entities)
      {
         if (entity.getUniqueId() == player.getUniqueId())
         {
            continue;
         }

         if (Ollivanders2Common.undeadEntities.contains(entity.getType()))
         {
            entity.setFireTicks(duration);
            targetCount--;
         }

         if (targetCount <= 0)
         {
            kill();
            return;
         }
      }

      if (entities.size() > 0 || hasHitTarget())
      {
         kill();
      }
   }
}
