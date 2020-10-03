package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.AgeableWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.PandaWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.PolarBearWatcher;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Turns target player in to a polar bear.
 *
 * @author Azami7
 * @since 2.2.6
 */
public class INCARNATIO_URSUS extends PlayerDisguise
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public INCARNATIO_URSUS()
   {
      super();

      spellType = O2SpellType.INCARNATIO_URSUS;
      branch = O2MagicBranch.TRANSFIGURATION;

      text = "Turns target player in to a polar bear.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public INCARNATIO_URSUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.INCARNATIO_URSUS;
      branch = O2MagicBranch.TRANSFIGURATION;

      initSpell();
      calculateSuccessRate();

      int rand = Math.abs(Ollivanders2Common.random.nextInt() % 20);

      if (rand < 1) // 5% chance
      {
         targetType = EntityType.PANDA;
      }
      else
      {
         targetType = EntityType.POLAR_BEAR;
      }
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      AgeableWatcher watcher = (AgeableWatcher) disguise.getWatcher();
      watcher.setAdult();

      if (watcher instanceof PolarBearWatcher)
      {
         ((PolarBearWatcher) watcher).setStanding(false);
      }
      else // Panda
      {
         ((PandaWatcher) watcher).setMainGene(Panda.Gene.NORMAL);
         ((PandaWatcher) watcher).setSitting(false);
      }
   }
}
