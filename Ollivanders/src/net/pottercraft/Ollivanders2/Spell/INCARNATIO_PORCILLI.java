package net.pottercraft.Ollivanders2.Spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.PigWatcher;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Created by Azami7 on 6/28/17.
 *
 * Turn target player in to a pig.
 *
 * @since 2.2.3
 * @author lownes
 * @author Azami7
 */
public final class INCARNATIO_PORCILLI extends PlayerDisguiseSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public INCARNATIO_PORCILLI ()
   {
      super();

      text = "Turns target player in to a pig.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public INCARNATIO_PORCILLI(Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      targetType = EntityType.PIG;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      PigWatcher watcher = (PigWatcher)disguise.getWatcher();
      watcher.setAdult();

      int rand = Math.abs(Ollivanders2.random.nextInt() % 100);
      if (rand == 0)
         watcher.setSaddled(true);
      else
         watcher.setSaddled(false);
   }
}