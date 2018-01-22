package net.pottercraft.Ollivanders2.Spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.PolarBearWatcher;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Turns target player in to a polar bear.
 *
 * @since 2.2.6
 * @author Azami7
 */
public class INCARNATIO_URSUS extends PlayerDisguiseSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public INCARNATIO_URSUS ()
   {
      super();

      text = "Turns target player in to a polar bear.";
   }

   public INCARNATIO_URSUS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      targetType = EntityType.POLAR_BEAR;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      PolarBearWatcher watcher = (PolarBearWatcher)disguise.getWatcher();
      watcher.setAdult();
      watcher.setStanding(true);
   }
}
