package net.pottercraft.Ollivanders2.Spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.AgeableWatcher;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Created by Azami7 on 6/28/17.
 *
 * Turn a target player in to a cow.
 *
 * @since 2.2.3
 * @author lownes
 * @author Azami7
 */
public final class INCARNATIO_VACCULA extends PlayerDisguiseSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public INCARNATIO_VACCULA ()
   {
      super();

      text = "Turns target player in to a cow.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public INCARNATIO_VACCULA(Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      int rand = Math.abs(Ollivanders2.random.nextInt() % 100);
      if (rand == 0)
         targetType = EntityType.MUSHROOM_COW;
      else
         targetType = EntityType.COW;
      disguiseType = DisguiseType.getType(targetType);
      disguise = new MobDisguise(disguiseType);

      AgeableWatcher watcher = (AgeableWatcher)disguise.getWatcher();
      watcher.setAdult();
   }
}
