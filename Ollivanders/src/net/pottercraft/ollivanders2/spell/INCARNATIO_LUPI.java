package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.WolfWatcher;
import net.pottercraft.ollivanders2.O2Color;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Created by Azami7 on 6/28/17.
 * <p>
 * Turn target player in to a wolf.
 *
 * @author lownes
 * @author Azami7
 * @since 2.2.3
 */
public final class INCARNATIO_LUPI extends PlayerDisguise
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public INCARNATIO_LUPI()
   {
      super();

      spellType = O2SpellType.INCARNATIO_LUPI;
      branch = O2MagicBranch.TRANSFIGURATION;

      text = "Turns target player in to a wolf or dog.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public INCARNATIO_LUPI(Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.INCARNATIO_LUPI;
      branch = O2MagicBranch.TRANSFIGURATION;

      initSpell();
      calculateSuccessRate();

      targetType = EntityType.WOLF;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      WolfWatcher watcher = (WolfWatcher) disguise.getWatcher();
      watcher.setAdult();

      int rand = Math.abs(Ollivanders2Common.random.nextInt() % 10);
      if (rand < 9)
      {
         watcher.isTamed();
         watcher.setCollarColor(O2Color.getRandomPrimaryDyeableColor().getDyeColor());
      }
   }
}
