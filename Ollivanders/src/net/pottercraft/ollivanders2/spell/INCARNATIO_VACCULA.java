package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.AgeableWatcher;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Azami7 on 6/28/17.
 * <p>
 * Turn a target player in to a cow.
 *
 * @author lownes
 * @author Azami7
 * @since 2.2.3
 */
public final class INCARNATIO_VACCULA extends PlayerDisguise
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public INCARNATIO_VACCULA(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.INCARNATIO_VACCULA;
      branch = O2MagicBranch.TRANSFIGURATION;

      text = "Turns target player in to a cow.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public INCARNATIO_VACCULA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.INCARNATIO_VACCULA;
      branch = O2MagicBranch.TRANSFIGURATION;

      int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);
      if (rand == 0) // 1% chance
         targetType = EntityType.MUSHROOM_COW;
      else
         targetType = EntityType.COW;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      AgeableWatcher watcher = (AgeableWatcher)disguise.getWatcher();
      watcher.setAdult();

      initSpell();
   }

   @Override
   void doInitSpell()
   {
      calculateSuccessRate();
   }
}
