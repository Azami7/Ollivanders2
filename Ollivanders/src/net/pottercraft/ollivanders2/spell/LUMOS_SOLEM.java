package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Effect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Burns sun-sensitive entities with a radius.
 *
 * @author Azami7
 * @version Ollivanders2
 */
public final class LUMOS_SOLEM extends O2Spell
{
   int duration;
   int targetCount = 1;
   double radius = 1.5;

   static int maxDuration = Ollivanders2Common.ticksPerSecond * 120; // 2 minutes
   static int maxTargetCount = 10;
   static int maxRadius = 10;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public LUMOS_SOLEM(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.LUMOS_SOLEM;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>() {{
         add("Light of the Sun");
      }};

      text = "Lumos Solem will cause a sun-like light to erupt in an area around the impact which will burn entities sensitive to sun.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public LUMOS_SOLEM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.LUMOS_SOLEM;
      branch = O2MagicBranch.CHARMS;

      initSpell();
   }

   @Override
   void doInitSpell()
   {
      // duration of fire damage
      duration = Ollivanders2Common.ticksPerSecond * (int)(usesModifier + 1);
      if (duration > maxDuration)
         duration = maxDuration;

      // max number of entities targeted
      targetCount = 1 + (int)(usesModifier / 10);
      if (targetCount > maxTargetCount)
         targetCount = maxTargetCount;

      // radius of the effect
      radius = (int)(usesModifier / 20) + 1;
      if (radius > maxRadius)
         radius = maxRadius;
   }

   @Override
   protected void doCheckEffect()
   {
      if (!hasHitTarget())
         return;

      kill();

      // create the bright flash and a sound
      Ollivanders2Common.flair(location, (int)(radius + 1), 10, Effect.MOBSPAWNER_FLAMES);

      List<LivingEntity> entities = getLivingEntities(1.5);

      for (LivingEntity entity : entities)
      {
         if (entity.getUniqueId() == player.getUniqueId())
            continue;

         if (Ollivanders2Common.undeadEntities.contains(entity.getType()))
         {
            entity.setFireTicks(duration);
            targetCount = targetCount - 1;
         }

         if (targetCount <= 0)
            return;
      }
   }
}