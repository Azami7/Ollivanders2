package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.PigWatcher;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Azami7 on 6/28/17.
 * <p>
 * Turn target player in to a pig.
 *
 * @author lownes
 * @author Azami7
 * @since 2.2.3
 */
public final class INCARNATIO_PORCILLI extends PlayerDisguise
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public INCARNATIO_PORCILLI()
   {
      super();

      spellType = O2SpellType.INCARNATIO_PORCILLI;
      branch = O2MagicBranch.TRANSFIGURATION;

      text = "Turns target player in to a pig.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public INCARNATIO_PORCILLI(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.INCARNATIO_PORCILLI;
      branch = O2MagicBranch.TRANSFIGURATION;

      initSpell();
      calculateSuccessRate();

      targetType = EntityType.PIG;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      PigWatcher watcher = (PigWatcher) disguise.getWatcher();
      watcher.setAdult();

      int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);

      if (rand == 0)
         watcher.setSaddled(true);
      else
         watcher.setSaddled(false);
   }
}